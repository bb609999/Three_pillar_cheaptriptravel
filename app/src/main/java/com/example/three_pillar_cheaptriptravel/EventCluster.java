package com.example.three_pillar_cheaptriptravel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.three_pillar_cheaptriptravel.drag.OnStartDragListener;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.EventLocationCluster;
import com.example.three_pillar_cheaptriptravel.object.EventManager;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class EventCluster extends AppCompatActivity implements OnStartDragListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private int schedule_id;
    private List<Event> eventList = new ArrayList<>();
    private ArrayList<ArrayList<Event>> clusterList = new ArrayList<ArrayList<Event>>();
    private int radius = 1250;

    private SeekBar radiusSeekBar;

    private Button change_radius_button;
    private Button apply_button;


    public static Intent newIntent(Context packageContext, int schedule_id) {
        Intent intent = new Intent(packageContext, EventCluster.class);
        intent.putExtra("schedule_id",schedule_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_cluster);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id",-1);
        eventList = Event.getEvents(schedule_id);


        clusterList = EventLocationCluster.clusterEvent(Event.getEvents(schedule_id),1250);


        radiusSeekBar = (SeekBar) findViewById(R.id.radiusSeekBar);
        radiusSeekBar.setMax(10000);
        radiusSeekBar.setProgress(1250);

        change_radius_button = (Button)findViewById(R.id.changeRadius);
        change_radius_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radius = radiusSeekBar.getProgress();

                mMap.clear();
                clusterList = EventLocationCluster.clusterEvent(Event.getEvents(schedule_id),radiusSeekBar.getProgress());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.28552, 114.15769), 12));
                addEventMarker();
                drawCircle(radius);

            }
        });

        apply_button = (Button)findViewById(R.id.apply_cluster);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radius = radiusSeekBar.getProgress();
                EventManager.arrangeEventByCluster(Schedule.getSchedule(schedule_id),eventList,radius);
                finish();

            }
        });







    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.28552, 114.15769), 12));
        addEventMarker();
        drawCircle(radius);



    }

    public void addEventMarker(){

        for(Event event:eventList){


            Place place = event.getPlace();
            IconGenerator iconGenerator = new IconGenerator(EventCluster.this);
            Bitmap bitmap =  iconGenerator.makeIcon(place.getPlaceName()+" ");
            bitmap.getScaledHeight(10);

            mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getPlaceName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));



        }

    }
    public void drawCircle(int radius){

        for(List<Event> cluterEventList:clusterList) {

                Place place = cluterEventList.get(0).getPlace();


                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(place.getLatLng())
                        .radius(radius)
                        .strokeColor(Color.RED));
            }



    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
