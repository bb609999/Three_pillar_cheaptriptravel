package com.example.three_pillar_cheaptriptravel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.Story.StoriesActivity;
import com.example.three_pillar_cheaptriptravel.dialog.CreateScheduleDialog;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.example.three_pillar_cheaptriptravel.object.ScheduleAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class ScheduleListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private SwipeRefreshLayout swipeRefresh;

    private List<Schedule> Schedules;

    private ScheduleAdapter adapter;

    private final static String TAG = "ScheduleListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        //CreateDataBase of Event if not exsit
        Connector.getDatabase();

        //Query Schedule, return all schedule user create
        Schedules = DataSupport.findAll(Schedule.class);

        //Toolbar
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //Navigation
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //Floating Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Data deleted",Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ScheduleListActivity.this, "Data Restored",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        //CardView Display of Schedule
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScheduleAdapter(Schedules);
        recyclerView.setAdapter(adapter);

        //swipe

        swipeRefresh =  (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrestSchedules();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.t1:
                Intent intent = new Intent(ScheduleListActivity.this, StoriesActivity.class);
                startActivity(intent);
                break;
            case R.id.create_schedele:
                CreateScheduleDialog createScheduleDialog = new CreateScheduleDialog();
                createScheduleDialog.show(getSupportFragmentManager(), " CreateScheduleDialog");
                break;
            case R.id.f1:
                break;
            case R.id.f2:
                break;
            case R.id.settings:
                List<Event> Events = DataSupport.findAll(Event.class);
                List<Place> Places = DataSupport.findAll(Place.class);

                for(Event one_event: Events ) {
                    Log.d(TAG, "onOptionsItemSelected: "+one_event.toString());
                }
                for(Schedule one_Schedule: Schedules ) {
                    Log.d("TAG", "Schedule " + one_Schedule.getId()+" "+one_Schedule.getName()+one_Schedule.getImageId());
                }
                for(Place one_Place: Places ) {
                    Log.d(TAG, "onOptionsItemSelected: "+one_Place.toString());
                }




                break;
            default:
        }
        return true;
    }

    //swipe refresh
    private void refrestSchedules(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        }).start();
    }



}

