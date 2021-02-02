package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class TrackerAdapter
        extends RecyclerView.Adapter<TrackerAdapter.ViewHolder>{

    private ArrayList<TrackerData> trackerDataList;

    // RecyclerView recyclerView;
    public TrackerAdapter(ArrayList<TrackerData> trackerDataList) {
        this.trackerDataList = trackerDataList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TrackerData myListData = trackerDataList.get(position);
        holder.editText.setText(trackerDataList.get(position).getDescription());
        holder.trackerData = myListData;
        //holder.imageView.setImageResource(trackerDataList.get(position).getImgId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.editText.clearFocus();

            }
        });
    }

    @Override
    public int getItemCount() {
        return trackerDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public ImageView imageView;
        public EditText editText;
        public TrackerData trackerData;
        //public LinearLayout linearLayout;
        public RelativeLayout relativeLayout;
        public FloatingActionButton playButton;
        public boolean paused = true;
        public PausableChronometer chronometer;
        public ViewHolder(View itemView) {
            super(itemView);
            //this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.editText = (EditText) itemView.findViewById(R.id.editText);
            relativeLayout = (RelativeLayout)itemView.findViewById(
                    R.id.relativeLayout);
            this.chronometer = (PausableChronometer)itemView.findViewById(R.id.chronometer);
            this.playButton = (FloatingActionButton)itemView.findViewById(R.id.fab_play);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Toast.makeText(itemView.getContext(),
                    //        "trackerData null: " + (trackerData == null), Toast.LENGTH_LONG).show();

                    if (paused) {
                        // start playing
                        chronometer.start();
                        paused = false;
                        playButton.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_pause_24));

                    } else {
                        // pause
                        chronometer.stop();
                        paused = true;
                        playButton.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_play_arrow_24));

                        // save the time
                        trackerData.setElapsedTime(chronometer.getCurrentTime());
                        //TODO: write to DB
                    }
                }
            });


            //add listener to remove keyboard and cursor on action done
            this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    //Toast.makeText(v.getContext(), "triggered " + actionId, Toast.LENGTH_LONG).show();
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        editText.clearFocus();
                    }
                    return false;
                }
            });
        }
    }
}
