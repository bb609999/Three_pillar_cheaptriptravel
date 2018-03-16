package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 8/3/2018.
 */

public class Place extends DataSupport{

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
}
