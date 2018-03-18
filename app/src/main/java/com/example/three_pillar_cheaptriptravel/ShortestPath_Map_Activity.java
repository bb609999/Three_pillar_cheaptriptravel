package com.example.three_pillar_cheaptriptravel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.three_pillar_cheaptriptravel.drag.EditItemTouchHelperCallback;
import com.example.three_pillar_cheaptriptravel.drag.ItemAdapter;
import com.example.three_pillar_cheaptriptravel.drag.OnStartDragListener;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ShortestPath_Map_Activity extends AppCompatActivity implements OnStartDragListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback{

    //Google Map
    private TextView mTapTextView;
    private TextView mCameraTextView;
    private GoogleMap mMap;

    private  PolylineOptions mPolylines;

    private TextView TotalDurationText;

    private LatLng[] latLngs;

    private String[] DurationList;

    private List<Event> eventList;
    private List<Place> placeList;

    private ItemTouchHelper mItemTouchHelper;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_path__map_);

        //Google Map
        mTapTextView = (TextView) findViewById(R.id.tap_text);
        mCameraTextView = (TextView) findViewById(R.id.camera_text);

        TotalDurationText = (TextView)findViewById(R.id.TotalDurationText);

        final Intent intent = getIntent();
        String[] places = intent.getStringArrayExtra("places");
        String TotalDuration  = intent.getStringExtra("TotalDuration");
        DurationList = intent.getStringArrayExtra("DurationList");
        final int schedule_id = intent.getIntExtra("schedule_id",-1);


        eventList = DataSupport.where("Schedule_id=?",""+schedule_id).find(Event.class);
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.event_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ItemAdapter mAdapter = new ItemAdapter(this, eventList, this);
        ItemTouchHelper.Callback callback =
                new EditItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);

        Button change_order = (Button)findViewById(R.id.change_order);
        change_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                for(int i=0;i<eventList.size();i++) {
                    Event event = new Event();
                    event.setStartTime(9 + 2 * i);
                    event.setEndTime(11 + 2 * i);
                    event.update(eventList.get(i).getId());
                }

            }
        });


        Button apply_schedule = (Button)findViewById(R.id.apply_schedule);
        apply_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Event> eventList = DataSupport.where("schedule_id=?",""+schedule_id).find(Event.class);

                Double time = 0.0;
                for(int i =0;i<eventList.size();i++) {
                    Place place = DataSupport.where("lat=? AND lng=?",""+latLngs[i].latitude,
                            ""+latLngs[i].longitude).findFirst(Place.class);
                    Log.d("Place", "onClick: "+place.getPlaceName());

                    double travel_time = i>0?Double.valueOf(DurationList[i-1])/3600:0;
                    time += travel_time;

                    Event event = new Event();
                    event.setStartTime(9+2*i+time);
                    event.setEndTime(11+2*i+time);
                    event.updateAll("placeName=?",place.getPlaceName());
                }

                Intent intent1 = new Intent(ShortestPath_Map_Activity.this,ScheduleDisplayActivity.class);
                intent1.putExtra("schedule_id",schedule_id);
                finish();
                startActivity(intent1);

            }
        });



        for(String i:DurationList){
            Log.d("DurationList", "onCreate: "+ i);

        }


        latLngs = new LatLng[places.length];
        for(int i=0;i<places.length;i++) {
            double Lat = Double.valueOf(places[i].split(",")[0]);
            double Lng = Double.valueOf(places[i].split(",")[1]);
            latLngs[i] = new LatLng(Lat,Lng);
        }

        TotalDurationText.setText("Total Time Used : "+Double.valueOf(TotalDuration)/60 + " minutes");



//        Log.d("0004", "onCreate: "+ latLngs[0]);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    //Google Map
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        mPolylines = new PolylineOptions().add(latLngs).width(2);

        mMap.addPolyline(mPolylines);

        if(latLngs.length!=0) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 13));
        }

        for(int i=0;i<latLngs.length;i++) {
             Place place =  DataSupport.where("lat=?",""+latLngs[i].latitude).findFirst(Place.class);

            IconGenerator iconGenerator = new IconGenerator(ShortestPath_Map_Activity.this);
            Bitmap bitmap =  iconGenerator.makeIcon(place.getPlaceName()+" ");

            mMap.addMarker(new MarkerOptions()
                    .position(latLngs[i])
                    .title(place.getPlaceName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

            );


            if(i<latLngs.length-1){
                Bitmap bitmap2 =  iconGenerator.makeIcon(Integer.valueOf(DurationList[i])/60 +" mins");
                mMap.addMarker(new MarkerOptions()
                        .position(midPoint(latLngs[i].latitude,latLngs[i].longitude,latLngs[i+1].latitude,latLngs[i+1].longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap2))
                );
            }

        }

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener(){
            @Override
            public void onPolylineClick(Polyline polyline) {

            }
        });

    }



    @Override
    public void onMapClick(LatLng point) {
        mTapTextView.setText("tapped, point=" + point);


    }

    @Override
    public void onMapLongClick(LatLng point) {
        mTapTextView.setText("long pressed, point=" + point);
    }

    @Override
    public void onCameraIdle() {
        mCameraTextView.setText(mMap.getCameraPosition().toString());
    }

    public LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(Math.toDegrees(lat3),Math.toDegrees(lon3));
    }


}

