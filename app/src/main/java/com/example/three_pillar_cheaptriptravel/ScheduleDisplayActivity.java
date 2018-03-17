package com.example.three_pillar_cheaptriptravel;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.example.three_pillar_cheaptriptravel.search.HotelSearchActivity;
import com.example.three_pillar_cheaptriptravel.search.PlaceSearchActivity;
import com.example.three_pillar_cheaptriptravel.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ScheduleDisplayActivity extends ScheduleDisplay implements  EventDialog.EventDialogListener {


    public final static String TAG = "ScheduleDisplayActivity";

    private List<Place> placeList;
    private List<Event> eventList;
    private int schedule_id;

    Schedule schedule;
    Calendar startFrom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Event under specific schedule id
        placeList = DataSupport.findAll(Place.class);
        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id", -1);
        eventList = DataSupport.where("Schedule_id=?", "" + schedule_id).find(Event.class);
        schedule = DataSupport.where("id=?", "" + schedule_id).findFirst(Schedule.class);

        //Create View and go to 9:00
        goToScheduleDate();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add_hotel:
                Intent hotel_search_intent = new Intent(ScheduleDisplayActivity.this, HotelSearchActivity.class);
                hotel_search_intent.putExtra("schedule_id", schedule_id);
                finish();
                startActivity(hotel_search_intent);
                break;
            case R.id.action_add_event:
                Intent search_intent = new Intent(ScheduleDisplayActivity.this, PlaceSearchActivity.class);
                search_intent.putExtra("schedule_id", schedule_id);
                finish();
                startActivity(search_intent);
                break;

            case R.id.action_calculate_path:
                calculatePath();
                break;
            default:
        }
        return true;
    }


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        super.onEventClick(event, eventRect);

        EventDialog eventDialog = new EventDialog().newInstance(event.getId());
        eventDialog.show(getSupportFragmentManager(), " EventDialog");

    }

    @Override
    public void onItemClick(android.support.v4.app.DialogFragment dialog, int which) {
        switch (which) {

            case 0:

                break;
            case 1:
                final Long id = dialog.getArguments().getLong("id");
                Log.d(TAG, "onItemClick: id"+id);

                //EndTime
                TimePickerDialog EndTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        //Get Event ID amd update that event
                        Event event = new Event();

                        event.setEndTime(i+(i1/60.0));
                        event.updateAll("id = ?", ""+id);
                        refreshDisplay();
                    }
                },13,0,true);

                EndTimePicker.setTitle("End Time");
                EndTimePicker.show();


                //Start Time
                TimePickerDialog StartTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Event event = new Event();

                        event.setStartTime(i+(i1/60.0));
                        event.updateAll("id = ?", ""+id);

                    }
                },12,0,true);


                StartTimePicker.setTitle("Start Time");
                StartTimePicker.show();



                break;
            case 2:
                //restart Activity after Delete
                refreshDisplay();
                break;
            case 3:
                break;

            default:
        }
    }


    public void refreshDisplay() {
        Intent intent = new Intent(ScheduleDisplayActivity.this, ScheduleDisplayActivity.class);
        intent.putExtra("schedule_id", schedule_id);
        finish();
        startActivity(intent);
    }

    public String formatTime(int hour, int minute) {
        String hour_formatted = hour < 10 ? "0" + hour : "" + hour;
        String minute_formatted = minute < 10 ? "0" + minute : "" + minute;

        return hour_formatted + ":" + minute_formatted;
    }

    public void calculatePath() {
        String address = "https://bb609999.herokuapp.com/api?loc=";

        for (
                Place place : placeList)

        {
            for (Event event : eventList) {
                if (place.getPlaceName().equals(event.getPlaceName())) {
                    address += "" + place.getLat() + "," + place.getLng() + "|";
                }
            }
        }

        address = address.substring(0, address.length() - 1);


        Log.d("TAG", "onOptionsItemSelected: address" + address);

        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback()

        {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d("TAG", "onResponse: responseData: " + responseData);

                //If result return
                if (responseData.equals("No Result")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScheduleDisplayActivity.this, "No Result", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            //0 is BestPath ; 1 is TotalDuration in seconds , 2 is PathDurationList
                            String[] splitToThree = responseData.split("\\|");
                            Log.d(TAG, "run: TotalDuration = " + splitToThree[0]);


                            String[] splited = splitToThree[0].split(",");
                            String[] joined = new String[splited.length / 2];

                            for (int i = 0; i < splited.length / 2; i++) {
                                joined[i] = splited[0 + 2 * i] + "," + splited[1 + 2 * i];
                            }

                            String[] DurationList = splitToThree[2].split(",");


                            Log.d(TAG, "run: TotalDuration = " + splitToThree[1]);

                            Intent intent = new Intent(ScheduleDisplayActivity.this, ShortestPath_Map_Activity.class);
                            intent.putExtra("places", joined);
                            intent.putExtra("TotalDuration", splitToThree[1]);
                            intent.putExtra("DurationList", DurationList);
                            intent.putExtra("schedule_id", schedule_id);

                            finish();
                            startActivity(intent);
                        }

                    });
                }
            }
        });
    }


    public void goToScheduleDate(){

        //GO TO Schedule's day 去創建活動的第一日
        startFrom = Calendar.getInstance();
        String[] date = schedule.getDate().split("/");
        startFrom.set(Calendar.DAY_OF_MONTH,Integer.valueOf(date[0]));
        startFrom.set(Calendar.MONTH,Integer.valueOf(date[1])-1);
        startFrom.set(Calendar.YEAR,Integer.valueOf(date[2]));
        getWeekView().goToDate(startFrom);


        WeekViewLoader loader = new WeekViewLoader() {
            int i =1;
            @Override
            public double toWeekViewPeriodIndex(Calendar instance) {
                return 0;
            }


            @Override
            public List<? extends WeekViewEvent> onLoad(int periodIndex) {
                if(i==3){
                    i=1;

                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

                for(Event an_event:eventList) {
                    //Event date and time
                    Calendar event_date = Calendar.getInstance();
                    String[] date = schedule.getDate().split("/");
                    event_date.set(Calendar.DAY_OF_MONTH,Integer.valueOf(date[0]));
                    event_date.set(Calendar.MONTH,Integer.valueOf(date[1])-1);
                    event_date.set(Calendar.YEAR,Integer.valueOf(date[2]));

                    Calendar startTime = (Calendar)event_date.clone();
                    Calendar endTime = (Calendar)event_date.clone();

                    int start_hour = (int)an_event.getStartTime();
                    int start_minute = (int) Math.round((an_event.getStartTime()-start_hour)*60.0);
                    int end_hour = (int)an_event.getEndTime();
                    int end_minute = (int) Math.round((an_event.getEndTime()-end_hour)*60.0);

                    startTime.set(Calendar.HOUR_OF_DAY, start_hour);
                    startTime.set(Calendar.MINUTE, start_minute);
                    endTime.set(Calendar.HOUR_OF_DAY, end_hour);
                    endTime.set(Calendar.MINUTE, end_minute);

                    //Set WeekViewEvent with event_id /
                    WeekViewEvent event;
                    event = new WeekViewEvent(an_event.getId(), getEventTitle(startTime), startTime, endTime);

                    //Opening hour of event place
                    Place place = DataSupport.where("id=?",""+an_event.getPlace_id()).findFirst(Place.class);
                    String[] OpeningHourList = (place.getOpeningHour()!=null)
                            ?place.getOpeningHour().split(","):new String[7];

                    //Opening hour = "1000-1800"
                    int DAY_OF_WEEK = startTime.get(Calendar.DAY_OF_WEEK);
                    String OpeningHour = OpeningHourList[DAY_OF_WEEK-1];

                    event.setColor(getResources().getColor(R.color.event_color_black));

                    String text = an_event.getPlaceName()+"\n"+
                            formatTime(start_hour,start_minute)+"-"+formatTime(end_hour,end_minute)+
                            "\n Opening Hour = "+OpeningHour;

                    //not null + Exceed Time Limit
                    if(OpeningHour!=null) {
                        if (start_hour < Integer.valueOf(OpeningHour.substring(1, 3)) || end_hour > Integer.valueOf(OpeningHour.substring(6, 8))) {
                            text = "Exceed Open Hour\n" + text;
                            event.setColor(getResources().getColor(R.color.event_color_02));
                        }
                        if (start_hour == Integer.valueOf(OpeningHour.substring(1, 3)) || end_hour == Integer.valueOf(OpeningHour.substring(6, 8))) {
                            if (start_minute < Integer.valueOf(OpeningHour.substring(3, 5)) || end_minute > Integer.valueOf(OpeningHour.substring(8, 10))) {
                                text = "Exceed Open Hour\n" + text;
                                event.setColor(getResources().getColor(R.color.event_color_02));

                            }
                        }
                    }
                    event.setName(text);
                    events.add(event);

                }


                return events;}
                else {
                    i++;
                    List events = new ArrayList();
                    return events;
                }
            }
        };
        getWeekView().setWeekViewLoader(loader);

    }

}

