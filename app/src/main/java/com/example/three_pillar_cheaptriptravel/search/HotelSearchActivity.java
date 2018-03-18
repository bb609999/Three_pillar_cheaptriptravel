package com.example.three_pillar_cheaptriptravel.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.ScheduleDisplayActivity;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class HotelSearchActivity extends AppCompatActivity {
    private String TAG = "LocationSearch";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;

    private com.example.three_pillar_cheaptriptravel.object.Place place_selected
            = new com.example.three_pillar_cheaptriptravel.object.Place();

    private WebView webView;

    private com.example.three_pillar_cheaptriptravel.object.Place place_exist;

    private int schedule_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id",-1);
        Log.d(TAG, "onClick: "+schedule_id);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_search);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.google.com.hk/search?q=hong+kong&source=lnms&tbm=isch&" +
                "sa=X&ved=0ahUKEwjH7YCElufZAhXFHJQKHScGCIoQ_AUICigB&biw=1536&bih=734");
        webView.scrollBy(0,500);


        Button add_hotel_to_schedule = (Button)findViewById(R.id.add_hotel_to_schedule);
        add_hotel_to_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(place_selected!=null){
                    Event event = new Event();
                    event.setSchedule_id(schedule_id);
                    event.setPlaceName(place_selected.getPlaceName());
                    event.setType("hotel");
                    event.setStartTime(0);
                    event.setEndTime(9.5);

                    Schedule schedule = DataSupport.where("id=?",""+schedule_id).findFirst(Schedule.class);

                    event.setDate(schedule.getDate());

                    if(place_exist!=null){
                        event.setPlace_id(place_exist.getId());
                    }else {
                        event.setPlace_id(place_selected.getId());
                    }
                    event.save();

                    Intent add_intent = new Intent(HotelSearchActivity.this, ScheduleDisplayActivity.class);
                    //add_intent.putExtra("PlaceName",place_Name);
                    add_intent.putExtra("schedule_id",schedule_id);
                    Log.d(TAG, "onClick: PUT"+schedule_id);
                    finish();
                    startActivity(add_intent);


                }
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.hotel_autocomplete_fragment);


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .build();

        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(22.179338, 113.820305),
                new LatLng(22.547751, 114.413402)
        ));

        autocompleteFragment.setFilter(typeFilter);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place1: " + place.getName());//get place details here
                Log.i(TAG, "onPlaceSelected: "+place.toString());
                Log.i(TAG, "onPlaceSelected: "+place.getLatLng());


                String placeName = place.getName().toString();
                double lat = round6(place.getLatLng().latitude);
                double lng = round6(place.getLatLng().longitude);

                place_exist =
                        DataSupport.where("placeName=? AND lat=? AND lng=?",placeName,""+lat,""+lng)
                                .findFirst(com.example.three_pillar_cheaptriptravel.object.Place.class);

                Log.d(TAG, "onPlaceSelected: "+(place_exist==null));


                    place_selected.setPlaceName(placeName);
                    place_selected.setLat(lat);
                    place_selected.setLng(lng);
                    place_selected.setAddress(""+place.getAddress());

                if(place_exist==null) {
                    place_selected.save();

                }

                Log.d(TAG, "onPlaceSelected: "+round6(place.getLatLng().latitude));

                webView.loadUrl("https://www.google.com.hk/search?q="+place.getName()+"&source=lnms" +
                        "&tbm=isch&sa=X&ved=0ahUKEwjH7YCElufZAhXFHJQKHScGCIoQ_AUICigB&biw=1536&bih=734");

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }


    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place2:" + place.toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public double  round6(Double val) {
        return new BigDecimal(val.toString()).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }


}

