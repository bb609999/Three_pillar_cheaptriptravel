package com.example.three_pillar_cheaptriptravel;

import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.Story.DiaryShowScheduleActivity;
import com.example.three_pillar_cheaptriptravel.dialog.CreateScheduleDialog;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.PlaceAdapter;
import com.example.three_pillar_cheaptriptravel.util.ProcessJson;

import org.litepal.crud.DataSupport;

import java.util.List;

public class PlaceListActivity extends AppCompatActivity {

    private PlaceAdapter adapter;

    private List<Place> Places  ;

    private DrawerLayout mDrawerLayout;

    private SwipeRefreshLayout swipeRefresh;

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext,PlaceListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        Places = DataSupport.findAll(Place.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

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

                if(item.getItemId()==R.id.nav_friends){
                    Intent intent = PlaceListActivity.newIntent(PlaceListActivity.this);
                    startActivity(intent);
                }else if (item.getItemId()==R.id.nav_gallery){
                    //Toast.makeText(ScheduleListActivity.this, "check gallery", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlaceListActivity.this, DiaryShowScheduleActivity.class);
                    startActivity(intent);
                }
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
                                Toast.makeText(PlaceListActivity.this, "Data Restored",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        //swipe

        swipeRefresh =  (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });


        adapter = new PlaceAdapter(Places);
        recyclerView.setAdapter(adapter);

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
            case R.id.create_schedele:
                CreateScheduleDialog createScheduleDialog = new CreateScheduleDialog();
                createScheduleDialog.show(getSupportFragmentManager(), " CreateScheduleDialog");
                break;
            case R.id.f1:
                try {
                    ProcessJson.processPlaces(this);
                }catch (Exception e){
                    e.printStackTrace();
                }


                break;
            case R.id.f2:
                break;
            case R.id.settings:


                break;
            default:
        }
        return true;
    }



}
