package com.example.three_pillar_cheaptriptravel.Story;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Schedule;

import org.litepal.crud.DataSupport;
import com.example.three_pillar_cheaptriptravel.object.Stories;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
public class DiaryShowScheduleActivity extends AppCompatActivity {

    private List<String> schedule_name = new ArrayList<String>();
    private List<Integer> schedule_id = new ArrayList<Integer>();
    private List<Integer> event_id = new ArrayList<Integer>();
    private List<Integer> schedule_unique_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_show_schedule);
        ListView listView = (ListView) findViewById(R.id.listview_allgallery);

        final List<Schedule> schedule = DataSupport.findAll(Schedule.class);
        for (Schedule schedule1:schedule){
            schedule_name.add(schedule1.getName());
        }
        List<Stories> scheduleid = DataSupport.findAll(Stories.class);
        for (Stories stories1:scheduleid){
            schedule_id.add(stories1.getSchedule_id());
        }
       // Log.d("Show schedule name", "schedule_name:"+schedule_name);
        // Log.d("Show schedule id", "schedule_id:"+schedule_id);

        //final ArrayList<Integer> unique = new ArrayList<>();
        HashSet<Integer> hs = new HashSet<Integer>(schedule_id);
        schedule_unique_id=new ArrayList<Integer>(hs);
        Log.d("Show schedule id", "unique_id:"+schedule_unique_id);

        ListAdapter adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1
                ,schedule_name);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ListView listView = (ListView) parent;
                //Toast.makeText(DiaryShowScheduleActivity.this, "Schedule"+id, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(DiaryShowScheduleActivity.this, DiaryGalleryActivity.class);
                i.putExtra("schedule_nameFromDSS", schedule_name.get(position));
                Log.d("DSS", "schedule_name "+schedule_name.get(position));
                i.putExtra("schedule_idFromDSS", schedule_unique_id.get(position));
                Log.d("DSS", "schedule_id "+schedule_unique_id.get(position));


                startActivity(i);
            }
        });
    }
}
