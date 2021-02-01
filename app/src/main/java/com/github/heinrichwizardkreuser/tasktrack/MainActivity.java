package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });


        // add data objects
        //ArrayList<TrackerData> trackerDataList = generateTrackerData();
        //saveTrackerData(trackerDataList);
        ArrayList<TrackerData> trackerDataList = loadTrackerData();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        TrackerAdapter adapter = new TrackerAdapter(trackerDataList);
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

    //fab_more_vert

    private static final String trackerDataListFilePath = "trackerDataListFilePath";

    private void saveTrackerData(ArrayList<TrackerData> trackerDataList) {
        try {
            FileOutputStream fileOut = getApplicationContext().openFileOutput(
                    trackerDataListFilePath,
                    Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(trackerDataList);
            objOut.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TrackerData> loadTrackerData() {
        try {
            FileInputStream fileIn = getApplicationContext().openFileInput(trackerDataListFilePath);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            ArrayList<TrackerData> trackerDataList = (ArrayList<TrackerData>) objIn.readObject();
            objIn.close();
            fileIn.close();
            return trackerDataList;
        } catch (ClassNotFoundException e) {
            System.out.println("TrackerData class not found");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println(trackerDataListFilePath + " not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private ArrayList<TrackerData> generateTrackerData() {
        ArrayList<TrackerData> trackerDataList = new ArrayList<TrackerData>();
        trackerDataList.add(new TrackerData("Email", android.R.drawable.ic_dialog_email));
        trackerDataList.add(new TrackerData("Info", android.R.drawable.ic_dialog_info));
        return trackerDataList;
        //TrackerData[] myListData = new TrackerData[] {
        //        new TrackerData("Email", android.R.drawable.ic_dialog_email),
        //        new TrackerData("Info", android.R.drawable.ic_dialog_info),
        //        new TrackerData("Delete", android.R.drawable.ic_delete),
        //        new TrackerData("Dialer", android.R.drawable.ic_dialog_dialer),
        //        new TrackerData("Alert", android.R.drawable.ic_dialog_alert),
        //        new TrackerData("Map", android.R.drawable.ic_dialog_map),
        //        new TrackerData("Email", android.R.drawable.ic_dialog_email),
        //        new TrackerData("Info", android.R.drawable.ic_dialog_info),
        //        new TrackerData("Delete", android.R.drawable.ic_delete),
        //        new TrackerData("Dialer", android.R.drawable.ic_dialog_dialer),
        //        new TrackerData("Alert", android.R.drawable.ic_dialog_alert),
        //        new TrackerData("Map", android.R.drawable.ic_dialog_map),
        //};
    }

    /*called on a specific timer when the three dots are called*/
    public void threeDotsOnClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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