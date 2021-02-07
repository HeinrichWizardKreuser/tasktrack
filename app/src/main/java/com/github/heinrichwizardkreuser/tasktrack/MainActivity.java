package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;
import android.media.MediaParser;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {

  public static Context appContext;
  private static TrackerStorage trackerStorage;
  private static TrackerAdapter adapter;

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
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    adapter = new TrackerAdapter(trackerStorage.trackerDataList);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);

    //FloatingActionButton fab_more_vert = findViewById(R.id.fab_more_vert);
    //fab_more_vert.setOnClickListener(new View.OnClickListener() {
    //    @Override
    //    public void onClick(View view) {
    //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
    //                .setAction("Action", null).show();
    //
    //    }
    //});

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
}