package com.example.three_pillar_cheaptriptravel.util;

import android.net.Uri;
import android.util.Log;

import com.example.three_pillar_cheaptriptravel.object.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/5/26.
 */

public class Download {

    private static final String TAG="Download";
    private static final String API_KEY="ac87b7405f6d40894692a078e00200b3";


    public static  byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public static JSONObject getJSON(String urlSpec) throws IOException,JSONException {
        return new JSONObject(getUrlString(urlSpec));
    }

    public  static void uploadPlace(Place place) {
        try {
            String url = Uri.parse("https://bb609999.herokuapp.com/insert/place")
                    .buildUpon()
                    .appendQueryParameter("name", place.getPlaceName())
                    .appendQueryParameter("address", place.getAddress())
                    .appendQueryParameter("imgLink", place.getImgLink())
                    .appendQueryParameter("lat", String.valueOf(place.getLat()))
                    .appendQueryParameter("lnt", String.valueOf(place.getLng()))
                    .appendQueryParameter("openingHour", place.getOpeningHour())
                    .appendQueryParameter("description",place.getDescription())
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
    }


    public static void fetchItems() {
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
    }

}
