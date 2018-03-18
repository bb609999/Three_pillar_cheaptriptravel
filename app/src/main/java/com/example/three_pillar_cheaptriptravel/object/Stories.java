package com.example.three_pillar_cheaptriptravel.object;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by waiwai on 18/3/2018.
 */

public class Stories extends DataSupport implements Parcelable {
    public String content;
    public ArrayList<String> photos;
    public String images;
    private int id;
    private int schedule_id;
    private int event_id;

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeStringList(this.photos);
    }

    public String getContent() {
        return content;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Stories() {
    }

    public Stories(String content, ArrayList<String> photos) {
        this.content = content;
        this.photos = photos;
    }

    protected Stories(Parcel in) {
        this.content = in.readString();
        this.photos = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Stories> CREATOR = new Parcelable.Creator<Stories>() {
        @Override
        public Stories createFromParcel(Parcel source) {
            return new Stories(source);
        }

        @Override
        public Stories[] newArray(int size) {
            return new Stories[size];
        }
    };
}

