package com.github.heinrichwizardkreuser.tasktrack;

import java.io.Serializable;
import java.util.ArrayList;

public class TrackerStorage implements Serializable {

    public ArrayList<TrackerData> trackerDataList;
    public ArrayList<TrackerData> archive;

    public TrackerStorage() {}
}
