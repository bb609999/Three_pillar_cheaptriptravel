package com.example.three_pillar_cheaptriptravel.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.three_pillar_cheaptriptravel.object.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Administrator on 1/5/2018.
 */

public class ProcessJson {

    public static void processPlaces (Context context) throws Exception{



        AssetManager manager = context.getAssets();
        InputStream file = manager.open("places.json");
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        String placeJson = new String(formArray);

        Log.d("TEST", "processPlaces: "+placeJson);

        JSONObject jsonObject = new JSONObject(placeJson);

        JSONArray placesArray = jsonObject.getJSONArray("places");

        for(int i=0;i<placesArray.length();i++) {
            Place place = new Place();
            JSONObject placeInJson = placesArray.getJSONObject(i);

            place.setPlaceName(placeInJson.optString("name"));
            place.setAddress(placeInJson.optString("address"));
            place.setImgLink(placeInJson.getString("imgLink"));
            place.setLat(placeInJson.optDouble("lat"));
            place.setLng(placeInJson.optDouble("lng"));
            place.setOpeningHour(placeInJson.opt("openingHour").toString());
            place.setDescription(placeInJson.optString("description"));
            place.save();

        }






    }
}
