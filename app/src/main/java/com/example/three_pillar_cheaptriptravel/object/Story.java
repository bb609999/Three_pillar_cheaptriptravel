package com.example.three_pillar_cheaptriptravel.object;

import org.litepal.crud.DataSupport;

/**
 * Created by waiwai on 11/3/2018.
 */

public class Story extends DataSupport {
    private String comment;

    private String img;

    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
