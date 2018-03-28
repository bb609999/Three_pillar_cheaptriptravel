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
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.drag.EditItemTouchHelperCallback;
import com.example.three_pillar_cheaptriptravel.drag.ItemAdapter;
import com.example.three_pillar_cheaptriptravel.drag.OnStartDragListener;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.util.HttpUtil;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ShortestPath_Map_Activity extends AppCompatActivity implements OnStartDragListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback{

    //Google Map
    private TextView mTapTextView;
    private TextView mCameraTextView;
    private GoogleMap mMap;

    private  PolylineOptions mPolylines;

    private TextView TotalDurationText;

    private String json_response;

    private List<Event> eventList;
    private List<Place> placeList;

    private ItemTouchHelper mItemTouchHelper;

    private int schedule_id;

    private JSONArray route;
    private JSONArray duration;
    private LatLng[] latLngs;

    private List<Event> default_eventList;

    private  String TAG = "TEST";



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_path__map_);

        //Google Map
        mTapTextView = (TextView) findViewById(R.id.tap_text);
        mCameraTextView = (TextView) findViewById(R.id.camera_text);

        TotalDurationText = (TextView)findViewById(R.id.TotalDurationText);

        final Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id",-1);

        String json_response = intent.getStringExtra("json_response");
        try {
            JSONObject jsonObject = new JSONObject(json_response);
            route = jsonObject.getJSONArray("route");
            duration = jsonObject.getJSONArray("duration");

            latLngs = new LatLng[route.length()];
            for(int i=0;i<route.length();i++){
                Event event = DataSupport.where("id=?",""+(route.optInt(i)+1)).findFirst(Event.class);
                //default_eventList.add(event);
                Place place = DataSupport.where("id=?",""+event.getPlace_id()).findFirst(Place.class);
                latLngs[i] = place.getLatLng();

            }


        }catch (Exception e){
            Log.d(TAG, "onCreate: "+Log.getStackTraceString(e.getCause().getCause()));
        }


        eventList = DataSupport.order("startTime asc").where("Schedule_id=?",""+schedule_id).find(Event.class);
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
                    event.setStartTime(9 + 2 * i+(0.5*i));
                    event.setEndTime(11 + 2 * i+(0.5*i));
                    event.update(eventList.get(i).getId());
                }
               // goToScheduleDisplay();
                simulateRoute();

            }
        });


       Button apply_schedule = (Button)findViewById(R.id.apply_schedule);
        apply_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                List<Event> eventList = DataSupport.where("schedule_id=?",""+schedule_id).find(Event.class);
                    Log.d(TAG, "onClick: "+eventList.size());

                Double time = 0.0;
                for(int i =0;i<eventList.size();i++) {
                    Place place = DataSupport.where("lat=? AND lng=?",""+latLngs[i].latitude,
                            ""+latLngs[i].longitude).findFirst(Place.class);
                    Log.d("Place", "onClick: "+place.getPlaceName());

                    double travel_time = i>0?duration.optInt(i-1)/3600.0:0;
                    time += travel_time;

                    Event event = new Event();
                    event.setStartTime(9+2*i+time);
                    event.setEndTime(11+2*i+time);
                    event.updateAll("placeName=?",place.getPlaceName());
                }


                goToScheduleDisplay();


            }
        });

       int TotalDuration = 0;
       for(int i=0;i<duration.length();i++) {
           TotalDuration +=duration.optInt(i);
       }

       TotalDurationText.setText("Total Time Used : "+Double.valueOf(TotalDuration)/60 + " minutes");

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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 12));
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
                Bitmap bitmap2 =  iconGenerator.makeIcon(duration.optInt(i)/60 +" mins");
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

    public void goToScheduleDisplay(){
        Intent intent1 = new Intent(ShortestPath_Map_Activity.this,ScheduleDisplayActivity.class);
        intent1.putExtra("schedule_id",schedule_id);
        finish();
        startActivity(intent1);
    }

    public void simulateRoute(){
        String locations="";
        for(Event event:eventList){
            Place place = DataSupport.where("id=?",""+event.getPlace_id()).findFirst(Place.class);
            locations += place.getLat()+","+place.getLng()+"|";
        }
            locations = locations.substring(0,locations.length()-1);

        simpleroute(locations);
    }

    public void simpleroute(String locations){

        String address = "https://bb609999.herokuapp.com/api/place/simpleroute?loc="+locations;

        Log.d("address", "simpleroute: "+address);

        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback()

        {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d("TAG", "onResponse: responseData: " + responseData);

                final String duration = responseData.substring(1,responseData.length()-1);
                final String[] durationList = duration.split(",");

                //If result return
                if (responseData.equals("No Result")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShortestPath_Map_Activity.this, "No Result", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();
                            for(int i=0;i<eventList.size();i++){
                                Place place = DataSupport.where("id=?",""+eventList.get(i).getPlace_id()).findFirst(Place.class);
                                markerMake(place);

                                if(i<eventList.size()-1){
                                    Place nextplace = DataSupport.where("id=?",""+eventList.get(i+1).getPlace_id()).findFirst(Place.class);
                                    durationMake(place.getLat(),place.getLng(),nextplace.getLat(),nextplace.getLng(), durationList[i]);
                                }

                            }
                            polylineMake(eventList);
                            int TotalDuration =0;
                            for(int i=0;i<durationList.length;i++){
                                TotalDuration+=Integer.valueOf(durationList[i])/60*1.5;
                            }


                            TotalDurationText.setText("Total Time Used: "+TotalDuration+"mins");

                        }

                    });
                }
            }
        });
    }

    public void markerMake(Place place){
        IconGenerator iconGenerator = new IconGenerator(ShortestPath_Map_Activity.this);
        Bitmap bitmap =  iconGenerator.makeIcon(place.getPlaceName()+" ");

        mMap.addMarker(new MarkerOptions()
                .position(place.getLatLng())
               // .title(place.getPlaceName())
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

        );
    }

    public void polylineMake(List<Event> eventList){

        LatLng[] latLngs = new LatLng[eventList.size()];
        for(int i=0;i<eventList.size();i++){
            Place place = DataSupport.where("id=?",""+eventList.get(i).getPlace_id()).findFirst(Place.class);
            latLngs[i]= place.getLatLng();
        }

        mPolylines = new PolylineOptions().add(latLngs).width(2);

        mMap.addPolyline(mPolylines);

    }

    public void durationMake(double lat1,double lon1,double lat2,double lon2,String Duration){
        IconGenerator iconGenerator = new IconGenerator(ShortestPath_Map_Activity.this);
        Bitmap bitmap2 =  iconGenerator.makeIcon(Integer.valueOf(Duration)/60*1.5 +" mins");
        mMap.addMarker(new MarkerOptions()
                .position(midPoint(lat1,lon1,lat2,lon2))
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap2))
        );
    }



}

