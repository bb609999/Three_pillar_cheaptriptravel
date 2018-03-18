package com.example.three_pillar_cheaptriptravel.place;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Place;

import org.litepal.crud.DataSupport;

public class PlaceDetailActivity extends AppCompatActivity {

    private Place place;
    private TextView place_openinghour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent =  getIntent();
        int place_id = intent.getIntExtra("place_id",-1);
        place = DataSupport.where("id=?",""+place_id).findFirst(Place.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);

        ImageView placeImageView = (ImageView) findViewById(R.id.place_image_view);

        TextView place_address = (TextView) findViewById(R.id.place_address);

        place_openinghour = (TextView) findViewById(R.id.place_openinghour);
        setOpeninghour();

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(place.getPlaceName());
        Glide.with(this).load("https://blog.holimood.com/wp-content/uploads/2016/08/20160320043728_kWrnD.jpg").into(placeImageView);


        place_address.setText("Address : "+place.getAddress()+"\n");



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
            String[] openHourList = place.getOpeningHour().split(",");

            String place_openinghourText = "Opening Hours : \n" +
                    "Sunday : \t\t" + openHourList[0] + "\n" +
                    "Monday : \t\t" + openHourList[1] + "\n" +
                    "Tuseday : \t\t" + openHourList[2] + "\n" +
                    "Wednesday :" + openHourList[3] + "\n" +
                    "Thursday :    \t" + openHourList[4] + "\n" +
                    "Friday :       \t" + openHourList[5] + "\n" +
                    "Saturday :   \t" + openHourList[6] + "\n";

            place_openinghour.setText(place_openinghourText);
        }
        else{
            String place_openinghourText = "No Imformation of Opening Hour";
            place_openinghour.setText(place_openinghourText);
        }
    }
}
