package com.wampinfotech.marvelCharacters;

import java.util.Date;

public class Characters {

    private String mName;
    private String mTitle;
    private int mId;
    private String mDesc;
    private Date mUpdate;
    private String mImage;

    public Characters(String name, int id, String desc, Date update, String image) {
        this.mName = name;
        this.mId = id;
        this.mDesc = desc;
        this.mUpdate = update;
        this.mImage = image;
    }

    Characters(String name, String secondary, String desc) {
        this.mName = name;
        this.mTitle = secondary;
        this.mDesc = desc;
    }

    Characters(String name, String secondary, String desc, String image) {
        this.mName = name;
        this.mTitle = secondary;
        this.mDesc = desc;
        this.mImage = image;
    }

    public String getName() {
        return mName;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getId() {
        return mId;
    }

    public String getDesc() {
        return mDesc;
    }

    public Date getUpdate() {
        return mUpdate;
    }

    public String getImage() {
        return mImage;
    }
}
