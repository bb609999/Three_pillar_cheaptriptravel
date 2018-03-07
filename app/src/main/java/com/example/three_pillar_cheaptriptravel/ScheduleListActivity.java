package com.example.three_pillar_cheaptriptravel;

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

import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.example.three_pillar_cheaptriptravel.object.ScheduleAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScheduleListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private SwipeRefreshLayout swipeRefresh;

    private Schedule[] schedules = {new Schedule("Ap360", R.drawable.ap360), new Schedule("Bitch", R.drawable.bitch),
            new Schedule("Blee", R.drawable.blee), new Schedule("Bubub", R.drawable.bubub),
            new Schedule("Clearbay", R.drawable.clearbay), new Schedule("Disney", R.drawable.disney),
            new Schedule("Ffprak", R.drawable.ffprak), new Schedule("GoledenFlower", R.drawable.goledenflower),
            new Schedule("Horse", R.drawable.horse), new Schedule("Queenpark", R.drawable.queenpark)};

    private List<Schedule> scheduleList = new ArrayList<>();

    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
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

        //CardView
        initSchedules();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScheduleAdapter(scheduleList);
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
            case R.id.f1:
                Connector.getDatabase();
                break;
            case R.id.f2:
                Event event = new Event("OUHK","school",3.5,3.5,"visit school");

                event.save();

                break;
            case R.id.settings:
                List<Event> Events = DataSupport.findAll(Event.class);

                for(Event one_event: Events ) {
                    Log.d("00002", " " + one_event.getPlaceName()+one_event.getDescription() );
                }



                break;
            default:
        }
        return true;
    }

    //CardView
    private void initSchedules(){
        scheduleList.clear();
        for(int i =0 ;i<50;i++) {
            Random random = new Random();
            int index = random.nextInt(schedules.length);
            scheduleList.add(schedules[index]);
        }
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
                        initSchedules();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }



}

