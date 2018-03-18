package com.example.three_pillar_cheaptriptravel.search;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.ScheduleDisplayActivity;
import com.example.three_pillar_cheaptriptravel.object.Event;
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

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.Call;
import okhttp3.Response;

public class PlaceSearchActivity extends AppCompatActivity{


    private String TAG = "LocationSearch";
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;

    private Schedule schedule;
    private com.example.three_pillar_cheaptriptravel.object.Place place_selected
            = new com.example.three_pillar_cheaptriptravel.object.Place();

    private WebView webView;

    private com.example.three_pillar_cheaptriptravel.object.Place place_exist;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.google.com.hk/search?q=hong+kong&source=lnms&tbm=isch&" +
                "sa=X&ved=0ahUKEwjH7YCElufZAhXFHJQKHScGCIoQ_AUICigB&biw=1536&bih=734");
        webView.scrollBy(0,500);



        //Choose Time of Event
        final EditText startTime = (EditText)findViewById(R.id.start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlaceSearchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        //format 8:8 to 08:08
                        String hour = i<10 ? "0"+String.valueOf(i): String.valueOf(i);
                        String minute = i1<10 ? "0"+String.valueOf(i1): String.valueOf(i1);

                        startTime.setText(hour+":"+minute);
                    }
                },12,0,true);

                timePickerDialog.show();
            }
        });

        final EditText endTime = (EditText)findViewById(R.id.end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlaceSearchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        //format 8:8 to 08:08
                        String hour = i<10 ? "0"+String.valueOf(i): String.valueOf(i);
                        String minute = i1<10 ? "0"+String.valueOf(i1): String.valueOf(i1);

                        endTime.setText(hour+":"+minute);
                    }
                },13,0,true);

                timePickerDialog.show();
            }
        });

        //Add Event to Schedule
        Button btn_add_to_schedule = (Button) findViewById(R.id.add_to_schedule);
        btn_add_to_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(place_selected.getPlaceName()!=null){

                    //get time from edit text field
                    String startTime_raw = startTime.getText().toString();
                    String[] startTime_separated = startTime_raw.split(":");
                    int startTime_hour = Integer.valueOf(startTime_separated[0]);
                    int startTime_minute = Integer.valueOf(startTime_separated[1]);

                    double startTime_formatted = startTime_hour+(startTime_minute/60.0);



                    String endTime_raw = endTime.getText().toString();
                    String[] endTime_separated = endTime_raw.split(":");
                    int endTime_hour = Integer.valueOf(endTime_separated[0]);
                    int endTime_minute = Integer.valueOf(endTime_separated[1]);

                    double endTime_formatted = endTime_hour+(endTime_minute/60.0);

                    //ensure time is correct
                    if(startTime_formatted>=endTime_formatted){
                        Toast.makeText(PlaceSearchActivity.this, "Your start time is larger than end time", Toast.LENGTH_SHORT).show();
                    }else {

                        Event event = new Event(place_selected.getPlaceName(), "not-defined yet", startTime_formatted, endTime_formatted, "not-defined yet");

                        Intent intent = getIntent();
                        int schedule_id = intent.getIntExtra("schedule_id",-1);
                        Log.d(TAG, "onClick: "+schedule_id);

                        event.setSchedule_id(schedule_id);

                        schedule = DataSupport.where("id=?",""+schedule_id).findFirst(Schedule.class);

                        event.setDate(schedule.getDate());

                        if(place_exist!=null){
                            event.setPlace_id(place_exist.getId());
                        }else {
                            event.setPlace_id(place_selected.getId());
                        }
                        event.save();

                        Intent add_intent = new Intent(PlaceSearchActivity.this, ScheduleDisplayActivity.class);
                        add_intent.putExtra("schedule_id",schedule_id);


                        finish();
                        startActivity(add_intent);
                    }
                }
            }
        });





        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

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
                //["1000-1900","1000-1800","1000-1800","1000-1800",null,"1000-1800","1000-1900"]
                Log.d("TAG", "onResponse: responseData: " + responseData);


                com.example.three_pillar_cheaptriptravel.object.Place place = new com.example.three_pillar_cheaptriptravel.object.Place();

                if (responseData.length()>10) {
                    String formatOpeningHour = responseData;
                    formatOpeningHour = formatOpeningHour.substring(1, responseData.length() - 1);
                    place.setOpeningHour(formatOpeningHour);
                    Log.d(TAG, "onResponse: "+(responseData != "No Result"));


                place.updateAll("id=?", "" + place_id);
                    }




            }
        });
    }


}

