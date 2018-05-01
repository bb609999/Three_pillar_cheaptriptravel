package com.example.three_pillar_cheaptriptravel.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.example.three_pillar_cheaptriptravel.util.HttpUtil;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.Call;
import okhttp3.Response;

public class HotelSearchActivity extends AppCompatActivity {
    private String TAG = "LocationSearch";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;

    private int schedule_id;
    private com.example.three_pillar_cheaptriptravel.object.Place place_selected
            = new com.example.three_pillar_cheaptriptravel.object.Place();

    private WebView webView;

    private com.example.three_pillar_cheaptriptravel.object.Place place_exist;

    public static Intent newIntent(Context packageContext, int schedule_id) {
        Intent intent = new Intent(packageContext, HotelSearchActivity.class);
        intent.putExtra("schedule_id", schedule_id);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

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


        //Add Event to Schedule
        Button btn_add_to_schedule = (Button) findViewById(R.id.add_hotel_to_schedule);
        btn_add_to_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(place_selected.getPlaceName()!=null) {


                    Schedule schedule = Schedule.getSchedule(schedule_id);
                    schedule.setHotel_id(place_selected.getId());
                    schedule.update(schedule_id);


                    finish();
                }
             }
        });





        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.hotel_autocomplete_fragment);

/*
* The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
* set a filter returning only results with a precise address.
*/
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

                place_selected.setPlaceName(placeName);
                place_selected.setLat(lat);
                place_selected.setLng(lng);
                place_selected.setAddress(""+place.getAddress());



                Log.d(TAG, "onPlaceSelected: "+round6(place.getLatLng().latitude));

                //if exist, dont add anymore
                place_exist =
                        DataSupport.where("placeName=? AND lat=? AND lng=?",placeName,""+lat,""+lng)
                                .findFirst(com.example.three_pillar_cheaptriptravel.object.Place.class);

                Log.d(TAG, "onPlaceSelected: "+(place_exist==null));

                if(place_exist==null) {
                    place_selected.save();

                    updateOpeningHour(place_selected.getId(), placeName, lat, lng);
                }


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

    public void updateOpeningHour(final int place_id, String keyword, double lat, double lng){
        String address = "https://bb609999.herokuapp.com/api/place/openinghour?keyword=" +
                keyword.replace(" ","")+"&lat="+lat+"&lng="+lng;

        Log.d(TAG, "setOpenningHour: "+address);

        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                try {
                    JSONObject JSON_Response = new JSONObject(responseData);
                    JSONArray OpeningHour = JSON_Response.getJSONArray("OpeningHour");

                    Log.d("TAG", "onResponse: responseData: " + responseData);

                    com.example.three_pillar_cheaptriptravel.object.Place place =
                            new com.example.three_pillar_cheaptriptravel.object.Place();

                    place.setOpeningHour(OpeningHour.toString());
                    place.updateAll("id=?", "" + place_id);


                }catch (JSONException e){

                }




            }
        });
    }


}

