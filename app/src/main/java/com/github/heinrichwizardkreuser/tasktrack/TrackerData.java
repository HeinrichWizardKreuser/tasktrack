package com.github.heinrichwizardkreuser.tasktrack;

import java.io.Serializable;

public class TrackerData implements Serializable {


    public TrackerData(String description) {
        this.description = description;
    }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    private long elapsedTime;
    public long getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }
}
