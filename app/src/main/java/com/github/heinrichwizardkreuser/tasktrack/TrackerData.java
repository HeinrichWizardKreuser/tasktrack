package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TrackerData implements Serializable {

  public TrackerData(String description) {
    this.description = description;
    this.id = "" + System.currentTimeMillis();
  }

  private String description;
  public String getDescription() { return description; }
  public void setDescription(String d) { this.description = d; }

  private long elapsedTime;
  public long getElapsedTime() { return elapsedTime; }
  public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

  private String id;
  public String id() { return id; }

}
