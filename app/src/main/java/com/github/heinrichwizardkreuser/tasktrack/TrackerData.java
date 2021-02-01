package com.github.heinrichwizardkreuser.tasktrack;

import java.io.Serializable;

public class TrackerData implements Serializable {


    public TrackerData(String description, int imgId) {
        this.description = description;
        this.imgId = imgId;
    }

    private int imgId;
    public int getImgId() { return imgId; }
    public void setImgId(int imgId) { this.imgId = imgId; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

}
