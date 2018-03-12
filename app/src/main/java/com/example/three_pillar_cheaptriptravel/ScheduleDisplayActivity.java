package com.example.three_pillar_cheaptriptravel;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.search.PlaceSearchActivity;
import com.example.three_pillar_cheaptriptravel.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ScheduleDisplayActivity extends ScheduleDisplay implements  EventDialog.EventDialogListener{

    //private List<Event> eventList = DataSupport.findAll(Event.class);

    public final static String TAG = "ScheduleDisplayActivity";

    private List<Place> placeList = DataSupport.findAll(Place.class);
    private List<Event> eventList;
    private int schedule_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id",-1);
        Log.d(TAG, "onCreate: "+"schedule_id = "+schedule_id);

        eventList = DataSupport.where("Schedule_id=?",""+schedule_id).find(Event.class);
        Log.d(TAG, "onCreate: "+"eventList.size = "+eventList.size());




        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add_event:
                Intent search_intent = new Intent(ScheduleDisplayActivity.this, PlaceSearchActivity.class);
                search_intent.putExtra("schedule_id",schedule_id);
                finish();
                startActivity(search_intent);
                break;
            case R.id.action_calculate_path:
                String address = "https://bb609999.herokuapp.com/api?loc=";

                for(Place place:placeList){
                    for(Event event:eventList){
                        Log.d("00005", "onOptionsItemSelected: "+place.getPlaceName());
                        Log.d("00005", "onOptionsItemSelected: "+event.getPlaceName());
                        if(place.getPlaceName().equals(event.getPlaceName())){
                            address += ""+place.getLat()+","+place.getLng()+"|";
                        }
                    }
                }

                    address = address.substring(0,address.length()-1);

                Log.d("00005", "onOptionsItemSelected: "+address);
                Toast.makeText(this, address, Toast.LENGTH_SHORT).show();


                //String address = "https://bb609999.herokuapp.com/api?loc=22.316279,114.180408%7C22.312441," +
                //        "114.225046%7C22.310602,114.187868%7C22.308235,114.185765%7C22.320165,114.208168";
                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse (Call call, final Response response) throws IOException {
                        final String responseData = response.body().string();
                        Log.d("00003", "onResponse: " + responseData);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //0 is BestPath ; 1 is TotalDuration in seconds , 2 is PathDurationList
                                String[] splitToThree = responseData.split("\\|");
                                Log.d(TAG, "run: TotalDuration = "+splitToThree[0]);


                                String[]splited = splitToThree[0].split(",");
                                String[]joined = new String[splited.length/2];

                                for(int i=0;i<splited.length/2;i++){
                                    joined[i] = splited[0+2*i]+","+splited[1+2*i];
                                }

                                String[] DurationList = splitToThree[2].split(",");


                                Log.d(TAG, "run: TotalDuration = "+splitToThree[1]);

                                Toast.makeText(ScheduleDisplayActivity.this, ""+ Arrays.toString(joined), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ScheduleDisplayActivity.this,ShortestPath_Map_Activity.class);
                                intent.putExtra("places",joined);
                                intent.putExtra("TotalDuration",splitToThree[1]);
                                intent.putExtra("DurationList",DurationList);

                                startActivity(intent);
                            }
                        });
                    }
                });

                break;

            default:
        }
        return true;
    }





        @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar) startTime.clone();
        WeekViewEvent event;

        for(Event an_event:eventList) {

            int start_hour = (int)an_event.getStartTime();

            int start_minute = (int) Math.round((an_event.getStartTime()-start_hour)*60.0);

            int end_hour = (int)an_event.getEndTime();

            int end_minute = (int) Math.round((an_event.getEndTime()-end_hour)*60.0);


            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, start_hour);
            startTime.set(Calendar.MINUTE, start_minute);
            startTime.set(Calendar.MONTH, newMonth - 1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, end_hour);
            endTime.set(Calendar.MINUTE, end_minute);
            endTime.set(Calendar.MONTH, newMonth - 1);
            event = new WeekViewEvent(an_event.getId(), getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_02));
            event.setName(an_event.getPlaceName()+"\n"+start_hour+":"+start_minute+" to "+end_hour+":"+end_minute);
            events.add(event);
        }

        return events;
    }



    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        super.onEventClick(event, eventRect);

        EventDialog eventDialog = new EventDialog().newInstance(event.getId());



        eventDialog.show(getSupportFragmentManager(), " EventDialog");
    }

    @Override
    public void onItemClick(android.support.v4.app.DialogFragment dialog, int which) {
        switch (which){

            case 0:
                break;
            case 1:
                break;
            case 2:
                //restart Activity after Delete
                refreshDisplay();
                break;
            default:
        }
    }


    public void refreshDisplay(){
        Intent intent = new Intent(ScheduleDisplayActivity.this,ScheduleDisplayActivity.class);
        intent.putExtra("schedule_id",schedule_id);
        finish();
        startActivity(intent);
    }

}

