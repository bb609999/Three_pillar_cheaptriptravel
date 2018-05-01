package com.example.three_pillar_cheaptriptravel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.example.three_pillar_cheaptriptravel.Story.DiaryListActivity;
import com.example.three_pillar_cheaptriptravel.dialog.DayToCalculateDialog;
import com.example.three_pillar_cheaptriptravel.dialog.EventTimeArrangeDialog;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.EventManager;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.example.three_pillar_cheaptriptravel.object.ScheduleSettingActivity;
import com.example.three_pillar_cheaptriptravel.object.TimeFormat;
import com.example.three_pillar_cheaptriptravel.search.PlaceSearchActivity;
import com.example.three_pillar_cheaptriptravel.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ScheduleDisplayActivity extends ScheduleDisplay
        implements  EventDialog.EventDialogListener, DayToCalculateDialog.Callbacks{


    public final static String TAG = "ScheduleDisplayActivity";

    private List<Place> placeList;
    private List<Event> eventList;
    private int schedule_id;
    private Schedule schedule;
    private Calendar startFrom;

    public static Intent newIntent(Context packageContext, int schedule_id) {
        Intent intent = new Intent(packageContext, ScheduleDisplayActivity.class);
        intent.putExtra("schedule_id", schedule_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        updateUI();
        setGetUpAndBackTime();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(schedule.getName());
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


            case R.id.action_mangage_time:
//                arrangeTimeAllDay();
//                updateUI();
                EventTimeArrangeDialog eventTimeArrangeDialog = EventTimeArrangeDialog.newInstance(schedule_id);
                eventTimeArrangeDialog.show(getSupportFragmentManager(), " EventTimeArrangeDialog");

                break;

            case R.id.action_cluster_event:

                Intent intent = EventCluster.newIntent(this,schedule_id);
                startActivity(intent);



//                EventManager.arrangeEventByCluster(schedule,eventList,1250);



                updateUI();

                break;
            case R.id.action_add_event:
                startActivity(PlaceSearchActivity.newIntent(this,schedule_id));

                break;

            case R.id.action_calculate_path:
                DayToCalculateDialog dayToCalculateDialog = DayToCalculateDialog.newInstance(schedule_id);
                dayToCalculateDialog.show(getSupportFragmentManager(), " DayToCalculateDialog");


//                calculatePath();
                break;
            case R.id.action_schedule_setting :
                Intent setting = ScheduleSettingActivity.newIntent(this,schedule_id);
                startActivity(setting);

                break;

            default:
        }
        return true;
    }

    //new
    public long event_id;
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        super.onEventClick(event, eventRect);



        //new
        event_id=event.getId();


        EventDialog eventDialog = new EventDialog().newInstance(event_id);
        eventDialog.show(getSupportFragmentManager(), " EventDialog");

    }

    @Override
    public void onItemClick(android.support.v4.app.DialogFragment dialog, int which) {
        switch (which) {

            case 0:

                break;
            case 1:
                final Long id = dialog.getArguments().getLong("id");

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");



                        String dateString = simpleDateFormat.format(calendar.getTime());
                        Event event = Event.getEvent(id.intValue());
                        event.setDate(dateString);
                        event.update(id.intValue());
                        updateUI();



                    }
                },2018,4-1,30);
                datePickerDialog.setTitle("Choose date");
                datePickerDialog.show();





                break;
            case 2:
                //restart Activity after Delete
                updateUI();
                break;
            case 3:
                final Long id2 = dialog.getArguments().getLong("id");


                //EndTime
                TimePickerDialog EndTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        //Get Event ID amd update that event
                        Event event = new Event();



                        event.setEndTime(i+(i1/60.0));
                        event.updateAll("id = ?", ""+id2);
                        updateUI();
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
                        event.updateAll("id = ?", ""+id2);

                    }
                },12,0,true);


                StartTimePicker.setTitle("Start Time");
                StartTimePicker.show();

                break;
            case 4:
                Intent intent = new Intent(ScheduleDisplayActivity.this, DiaryListActivity.class);
                intent.putExtra("schedule_id",schedule_id);
                intent.putExtra("event_id",(int)event_id);
                Log.d("eric_image: ", String.valueOf(schedule_id));
                finish();
                startActivity(intent);
                break;
            default:
        }
    }


    public String formatTime(int hour, int minute) {
        String hour_formatted = hour < 10 ? "0" + hour : "" + hour;
        String minute_formatted = minute < 10 ? "0" + minute : "" + minute;

        return hour_formatted + ":" + minute_formatted;
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
                    Calendar event_date = an_event.getDateInCalendar();

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

                    String OpeningHour = place.getOneOpeningHour(startTime.get(Calendar.DAY_OF_WEEK));

                    event.setColor(getResources().getColor(R.color.event_color_black));

                    String text = an_event.getPlaceName()+"\n"+
                            formatTime(start_hour,start_minute)+"-"+formatTime(end_hour,end_minute)+
                            "\n Opening Hour = "+OpeningHour;

                    //not null + Exceed Time Limit   0123-5678
                   if(an_event.exceedOpeningHour(OpeningHour)){
                       text = "Exceed Open Hour\n" + text;
                       event.setColor(getResources().getColor(R.color.event_color_02));
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        updateUI();
    }

    private void updateUI() {
        //Get Event under specific schedule id
        placeList = DataSupport.findAll(Place.class);
        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id", -1);
        eventList = Event.getEvents(schedule_id);
        schedule = Schedule.getSchedule(schedule_id);

        //Create View and go to 9:00
        goToScheduleDate();
        getWeekView().notifyDatasetChanged();
       
    }

    public void arrangeTimeAllDay(){
        //loop 10
        for(int i=0;i<10;i++) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            try {

                date = format.parse(schedule.getDate());
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, i);
                date = c.getTime();
            }catch (Exception e){
                e.printStackTrace();
            }

            String dateString = format.format(date);

            List<Event> eventListWithSameDay =  Event.getEventsByDate(schedule_id,dateString);
            Log.d(TAG, "onOptionsItemSelected: " + eventListWithSameDay.size());



            EventManager.arrangeEvent(eventListWithSameDay,schedule_id);

        }
    }

    private void setGetUpAndBackTime() {

        if((schedule.getGetUpTime()!=0.00)&&(schedule.getBackTime()!=0.00)){
            return;
        }

        TimePickerDialog endTimeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Schedule schedule = new Schedule();
                schedule.setBackTime(TimeFormat.getTimeFromHM(i,i1));
                schedule.update(schedule_id);

            }
        },22,0,true);

        endTimeDialog.setTitle("When would you want to get back to hotel?");
        endTimeDialog.show();


        TimePickerDialog getUpTimeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Schedule schedule = new Schedule();
                schedule.setGetUpTime(TimeFormat.getTimeFromHM(i,i1));
                schedule.update(schedule_id);

            }
        },9,0,true);

        getUpTimeDialog.setTitle("When would you start your daily trip (leaving hotel)?");
        getUpTimeDialog.show();






    }

    @Override
    public void calculatePath(final int schedule_id, final String dateString) {
                String address = "https://bb609999.herokuapp.com/api?loc=";


        Schedule schedule = Schedule.getSchedule(schedule_id);
        List<Event> eventList = Event.getEventsByDate(schedule_id,dateString);

        for(Event event:eventList) {
            Place place = event.getPlace();
            address += "" + place.getLat() + "," + place.getLng() + "|";
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
                            startActivity(ShortestPath_Map_Activity.newIntent
                                    (ScheduleDisplayActivity.this,schedule_id,dateString,responseData));
                        }

                    });
                }
            }
        });
    }
}

