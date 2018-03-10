package com.example.three_pillar_cheaptriptravel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ShortestPath_Map_Activity extends AppCompatActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    //Google Map
    private TextView mTapTextView;
    private TextView mCameraTextView;
    private GoogleMap mMap;

    private LatLng[] latLngs;

    private List<Event> eventList = DataSupport.findAll(Event.class);
    private List<Place> placeList = DataSupport.findAll(Place.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_path__map_);

        //Google Map
        mTapTextView = (TextView) findViewById(R.id.tap_text);
        mCameraTextView = (TextView) findViewById(R.id.camera_text);

        Intent intent = getIntent();
        String[] places = intent.getStringArrayExtra("places");

        latLngs = new LatLng[places.length];

        for(int i=0;i<places.length;i++) {

            double Lat = Double.valueOf(places[i].split(",")[0]);
            double Lng = Double.valueOf(places[i].split(",")[1]);

            latLngs[i] = new LatLng(Lat,Lng);
        }

//        Log.d("0004", "onCreate: "+ latLngs[0]);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Google Map
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        mMap.addPolyline(new PolylineOptions()
               .add(latLngs)
                .width(5)
        );

        if(latLngs.length!=0) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 13));
        }

        for(int i=0;i<latLngs.length;i++) {


             Place place =  DataSupport.where("lat=?",""+latLngs[i].latitude).findFirst(Place.class);

            IconGenerator iconGenerator = new IconGenerator(ShortestPath_Map_Activity.this);
            Bitmap bitmap =  iconGenerator.makeIcon(place.getPlaceName());

            mMap.addMarker(new MarkerOptions()
                    .position(latLngs[i])
                    .title(place.getPlaceName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))



            );
        }

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




}
