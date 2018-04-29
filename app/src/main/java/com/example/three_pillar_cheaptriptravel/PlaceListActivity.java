package com.example.three_pillar_cheaptriptravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.PlaceAdapter;

import org.litepal.crud.DataSupport;

import java.util.List;

public class PlaceListActivity extends AppCompatActivity {

    private PlaceAdapter adapter;

    private List<Place> Places  ;

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


        adapter = new PlaceAdapter(Places);
        recyclerView.setAdapter(adapter);

    }
}
