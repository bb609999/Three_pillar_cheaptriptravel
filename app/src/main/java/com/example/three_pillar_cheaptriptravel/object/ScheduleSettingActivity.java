package com.example.three_pillar_cheaptriptravel.object;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.example.three_pillar_cheaptriptravel.R;

public class ScheduleSettingActivity extends AppCompatActivity {

    private int schedule_id;

    private EditText nameText;
    private TextView dateText;
    private TextView getUpTimeText;
    private TextView endTimeText;

    public static Intent newIntent(Context packageContext, int schedule_id) {
        Intent intent = new Intent(packageContext, ScheduleSettingActivity.class);
        intent.putExtra("schedule_id", schedule_id);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_setting);

        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id",-1);
        Schedule schedule = Schedule.getSchedule(schedule_id);

        nameText = findViewById(R.id.schedule_name_edit);
        nameText.setText(schedule.getName());

        dateText = findViewById(R.id.schedule_date_edit);
        dateText.setText(schedule.getDate());

        Double getUpTime = schedule.getGetUpTime();
        String getUpTimeString =
                TimeFormat.formatTime(TimeFormat.getHourOfTime(getUpTime),TimeFormat.getMinuteOfTime(getUpTime));

        getUpTimeText = findViewById(R.id.schedule_getUpTime_edit);
        getUpTimeText.setText(getUpTimeString);

        Double backTime = schedule.getBackTime();
        String backTimeString =
                TimeFormat.formatTime(TimeFormat.getHourOfTime(backTime),TimeFormat.getMinuteOfTime(backTime));
        endTimeText = findViewById(R.id.schedule_endTime_edit);
        endTimeText.setText(backTimeString);

    }
}
