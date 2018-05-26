package com.example.three_pillar_cheaptriptravel.place;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.dialog.AddPlaceToSchedule;
import com.example.three_pillar_cheaptriptravel.object.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

public class PlaceDetailActivity extends AppCompatActivity {

    private Place place;
    private TextView place_openinghour;
    private FloatingActionButton mFloatingActionButton;
    private TextView place_description;

    public static Intent newIntent(Context packageContext, int place_id) {
        Intent intent = new Intent(packageContext, PlaceDetailActivity.class);
        intent.putExtra("place_id", place_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent =  getIntent();
        final int place_id = intent.getIntExtra("place_id",-1);
        place = DataSupport.where("id=?",""+place_id).findFirst(Place.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);

        ImageView placeImageView = (ImageView) findViewById(R.id.place_image_view);
        TextView placeAddress = (TextView) findViewById(R.id.place_address);


        place_openinghour = (TextView) findViewById(R.id.place_openinghour);
        setOpeninghour();

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(place.getPlaceName());

        if(place.getImgLink()!=null) {
            Glide.with(this).load(place.getImgLink()).into(placeImageView);
        }else{
            Glide.with(this).load("https://blog.holimood.com/wp-content/uploads/2016/08/20160320043728_kWrnD.jpg").into(placeImageView);
        }

        placeAddress.setText(place.getAddress());

        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.add_place_to_schedule);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPlaceToSchedule addPlaceToSchedule = AddPlaceToSchedule.newInstance(place_id);
                addPlaceToSchedule.show(getSupportFragmentManager(), "AddPlaceToSchedule");

            }
        });

        place_description = (TextView)findViewById(R.id.place_description);
        place_description.setText(place.getDescription());



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setOpeninghour(){
        if(place.getOpeningHour()!=null) {

            try {
                JSONArray openHourList = new JSONArray(place.getOpeningHour());
                Log.d("Json", "setOpeninghour: "+openHourList.toString());
                String place_openinghourText = "\nOpening Hours : \n" +
                            "Sunday : \t\t" + openHourList.optString(0) + "\n" +
                            "Monday : \t\t" + openHourList.optString(1) + "\n" +
                            "Tuseday : \t\t" + openHourList.optString(2) + "\n" +
                            "Wednesday :" + openHourList.optString(3) + "\n" +
                            "Thursday :    \t" + openHourList.optString(4) + "\n" +
                            "Friday :       \t" + openHourList.optString(5)+ "\n" +
                            "Saturday :   \t" + openHourList.optString(6) + "\n";

                place_openinghour.setText(place_openinghourText);
            }catch (JSONException e){}
        }
        else{
            String place_openinghourText = "No Imformation of Opening Hour";
            place_openinghour.setText(place_openinghourText);
        }
    }
}
