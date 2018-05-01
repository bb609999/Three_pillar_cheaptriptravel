package com.example.three_pillar_cheaptriptravel.object;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 8/3/2018.
 */

public class Place extends DataSupport {

    private int id;

    private String placeName;

    private String address;

    private String imgLink;

    private double lat;

    private double lng;

    private String openingHour;

    private String description;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", address='" + address + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", openingHour='" + openingHour + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    public static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public String getOneOpeningHour(int DayOfWeek) {
        String OpeningHour = "";

        try {

            if (openingHour != null) {
                JSONArray openingHourArray = new JSONArray(openingHour);
                OpeningHour = openingHourArray.getString(DayOfWeek - 1);
            } else {
                OpeningHour = "No Information";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return OpeningHour;

    }

    public static Place getPlace(int place_id){
        return DataSupport.find(Place.class,place_id);
    }





}
