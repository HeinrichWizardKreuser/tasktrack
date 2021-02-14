package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;
import android.media.MediaParser;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements EditTimeDialog.EditTimeListener {

  public static Context appContext;
  private static TrackerStorage trackerStorage;
  public static TrackerAdapter adapter;
  private static RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    appContext = getApplicationContext();

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //create a new tracker
        // iterate over all trackers and check if name is taken
        int mx = 1;
        for (TrackerData trackerData : trackerStorage.trackerDataList) {
          String name = trackerData.getDescription();
          if (name.startsWith("Tracker ")) {
            int v = 0;
            try {
              v = Integer.parseInt(name.substring("Tracker ".length()));
              if (v > mx) {
                mx = v;
              }
            } catch (Exception e) {
              continue;
            }
          }
        }
        String newName = String.format("Tracker %d", mx + 1);
        //generate a new tracker with this name
        TrackerData newTracker = new TrackerData(newName);
        // add to list
        trackerStorage.trackerDataList.add(newTracker);
        // add to recycler view
        adapter.notifyItemInserted(adapter.getItemCount());
        //save to db
        saveTrackerData();
      }
    });

    // populate database with predetermined data
    //populate();

    // load up the tracker data from db
    loadTrackerData();

    // add all of the TrackerData to recyclerview
    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    adapter = new TrackerAdapter(trackerStorage.trackerDataList, getSupportFragmentManager());
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // add callback for drag and drop
    ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
    touchHelper.attachToRecyclerView(recyclerView);

    recyclerView.setAdapter(adapter);
  }

  private void populate() {
    // delete the file
    ArrayList<TrackerData> trackerDataList = new ArrayList<TrackerData>();
    trackerDataList.add(new TrackerData("Tracker 1"));
    trackerDataList.add(new TrackerData("Tracker 2"));
    trackerDataList.add(new TrackerData("Tracker 3"));

    trackerStorage = new TrackerStorage();
    trackerStorage.archive = new ArrayList<>();
    trackerStorage.trackerDataList = trackerDataList;
    saveTrackerData();
  }

  private static final String trackerDataListFilePath = "trackerDataListFilePath";

  public static void saveTrackerData() {
    try {
      FileOutputStream fileOut = appContext.openFileOutput(
        trackerDataListFilePath,
        Context.MODE_PRIVATE);
      ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
      objOut.writeObject(trackerStorage);
      objOut.close();
      fileOut.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void loadTrackerData() {
    try {
      FileInputStream fileIn = appContext.openFileInput(trackerDataListFilePath);
      ObjectInputStream objIn = new ObjectInputStream(fileIn);
      trackerStorage = (TrackerStorage) objIn.readObject();
      objIn.close();
      fileIn.close();
    } catch (ClassNotFoundException e) {
      System.out.println("TrackerData class not found");
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      System.out.println(trackerDataListFilePath + " not found");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean delete(TrackerData trackerData) {
    // get index of object
    int index = trackerStorage.trackerDataList.indexOf(trackerData);
    if (index == -1) {
      return false;
    }
    // remove from storage
    trackerStorage.trackerDataList.remove(index);
    // remove from recycler view
    adapter.notifyItemRemoved(index);
    // save changes to db
    saveTrackerData();
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /******************************UPDATING TIME*********************************************/

  @Override
  public void applyTimeTexts(String timeText, BetterChronometer chronometer) {
    //TODO: check for format "HH:MM:SS.sss"
    char[] arr = timeText.toCharArray();
    // "HH:MM:SS.sss"
    //  01 34 67 901 check digits
    //    2  5       check colons
    if (arr.length < 8) {
      timeEditError("Time too short");
      return;
    }
    if (12 < arr.length) {
      timeEditError("Time too long");
      return;
    }
    //check all digits except millis
    int[] digits = new int[]{0, 1, 3, 4, 6, 7};
    for (int i : digits) {
      if (!Character.isDigit(arr[i])) {
        timeEditError("Time can only contain digits (0... 9)");
        return;
      }
    }

    // check millis
    for (int i = 9; i < arr.length; i++) {
      if (!Character.isDigit(arr[i])) {
        timeEditError("Time can only contain digits (0... 9)");
        return;
      }
    }
    // check colons
    if (arr[2] != ':' || arr[5] != ':') {
      timeEditError("Missing colons (:)");
      return;
    }
    // parse time
    int hours = Integer.parseInt(timeText.substring(0, 2));
    int minutes = Integer.parseInt(timeText.substring(3, 5));
    int seconds = Integer.parseInt(timeText.substring(6, 8));
    int milliseconds = Integer.parseInt(timeText.substring(9));
    // check minute limits
    if (60 <= minutes) {
      timeEditError("Minutes may not exceed 60");
      return;
    }
    // check seconds limits
    if (60 <= seconds) {
      timeEditError("Seconds may not exceed 60");
      return;
    }
    //  now convert time to new time elapsed
    long total = milliseconds +
            seconds * 1000 +
            minutes * 60 * 1000 +
            hours * 60 * 60 * 1000;

    //err("updating to " + hours + "h" + minutes + "m" + seconds + "s" + milliseconds + "ss" +
    //        ": " + chronometer.getTimeElapsed() + "" +
    //        ": " + total);


    chronometer.setCurrentTime(total);

    Snackbar.make(recyclerView,
            "Updated Successfully",
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
  }

  private void timeEditError(String s) {
    Snackbar.make(recyclerView,
            "Error: " + s,
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
  }
}