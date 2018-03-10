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

    private List<Event> eventList = DataSupport.findAll(Event.class);
    private List<Place> placeList = DataSupport.findAll(Place.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


                //String address = "https://bb609999.herokuapp.com/api?loc=22.316279,114.180408%7C22.312441," +
                //        "114.225046%7C22.310602,114.187868%7C22.308235,114.185765%7C22.320165,114.208168";
                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse (Call call, Response response) throws IOException {
                        final String responseData = response.body().string();
                        Log.d("00003", "onResponse: " + responseData);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //To a String array
                                String newResponseData = responseData.replace("[","");
                                newResponseData = newResponseData.replace("]","");
                                newResponseData = newResponseData.replace("\"","");

                                String[]splited = newResponseData.split(",");
                                String[]joined = new String[splited.length/2];

                                for(int i=0;i<splited.length/2;i++){
                                    joined[i] = splited[0+2*i]+","+splited[1+2*i];
                                }

                                Toast.makeText(ScheduleDisplayActivity.this, ""+ Arrays.toString(joined), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ScheduleDisplayActivity.this,ShortestPath_Map_Activity.class);
                                intent.putExtra("places",joined);
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
        startTime.set(Calendar.HOUR_OF_DAY, 12);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        endTime.set(Calendar.MONTH, newMonth - 1);
        WeekViewEvent event = new WeekViewEvent(100, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_01));
        events.add(event);



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

       /* startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 30);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 4);
        endTime.set(Calendar.MINUTE, 30);
        endTime.set(Calendar.MONTH, newMonth-1);
        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_02));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 4);
        startTime.set(Calendar.MINUTE, 20);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 5);
        endTime.set(Calendar.MINUTE, 0);
        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_03));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 5);
        startTime.set(Calendar.MINUTE, 30);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 2);
        endTime.set(Calendar.MONTH, newMonth-1);
        event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_02));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 5);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        startTime.add(Calendar.DATE, 1);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        endTime.set(Calendar.MONTH, newMonth - 1);
        event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_03));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 15);
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_04));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 1);
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_01));
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
        startTime.set(Calendar.HOUR_OF_DAY, 15);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_02));
        events.add(event);

        //AllDay event
        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 23);
        event = new WeekViewEvent(7, getEventTitle(startTime),null, startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_04));
        events.add(event);
        events.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 8);
        startTime.set(Calendar.HOUR_OF_DAY, 2);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 10);
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        event = new WeekViewEvent(8, getEventTitle(startTime),null, startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_03));
        events.add(event);

        // All day event until 00:00 next day
        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 10);
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 11);
        event.setColor(getResources().getColor(R.color.event_color_01));
        events.add(event);*/

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
            case 2:

                //restart Activity after Delete

                Intent intent = new Intent(ScheduleDisplayActivity.this,ScheduleDisplayActivity.class);
                finish();
                startActivity(intent);


            default:

        }

    }

}

