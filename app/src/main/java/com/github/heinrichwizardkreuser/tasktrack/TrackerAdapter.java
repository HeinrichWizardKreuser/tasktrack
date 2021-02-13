package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import java.util.Collections;

public class TrackerAdapter
    extends RecyclerView.Adapter<TrackerAdapter.ViewHolder>
    implements ItemMoveCallback.ItemTouchHelperContract {

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
    holder.chronometer.setCurrentTime(myListData.getElapsedTime());
    holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        holder.editText.clearFocus();
      }
    });
  }


  /*DRAG AND DROP*/

  @Override
  public void onRowMoved(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(trackerDataList, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(trackerDataList, i, i - 1);
      }
    }
    notifyItemMoved(fromPosition, toPosition);

    Log.d("onRowMoved", fromPosition + " moved to " + toPosition);

    // update position in collection
    MainActivity.saveTrackerData();
  }


  @Override
  public void onRowSelected(ViewHolder myViewHolder) {
    myViewHolder.rowView.setBackgroundColor(Color.GRAY);
  }

  @Override
  public void onRowClear(ViewHolder myViewHolder) {
    myViewHolder.rowView.setBackgroundColor(Color.WHITE);

  }



  @Override
  public int getItemCount() {
    return trackerDataList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    View rowView;
    //public ImageView imageView;
    public EditText editText;
    public TrackerData trackerData;
    //public LinearLayout linearLayout;
    public RelativeLayout relativeLayout;
    public FloatingActionButton playButton;
    public FloatingActionButton options;
    public boolean paused = true;
    public PausableChronometer chronometer;
    public ViewHolder(View itemView) {
      super(itemView);
      this.rowView = itemView;
      //this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
      this.editText = (EditText) itemView.findViewById(R.id.editText);
      this.relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
      this.chronometer = (PausableChronometer)itemView.findViewById(R.id.chronometer);
      this.playButton = (FloatingActionButton)itemView.findViewById(R.id.fab_play);
      this.options = (FloatingActionButton)itemView.findViewById(R.id.fab_more_vert);
      this.options.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
          PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.END);
          MenuInflater inflater = popup.getMenuInflater();
          inflater.inflate(R.menu.tracker_menu, popup.getMenu());
          popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
              int id = item.getItemId();
              switch (id) {
                case R.id.action_reset: {
                  Snackbar.make(v, "Timer '" + trackerData.getDescription() + "' reset to 0",
                          Snackbar.LENGTH_LONG)
                          .setAction("Action", null).show();
                  trackerData.setElapsedTime(0l);
                  chronometer.setCurrentTime(0l);
                  MainActivity.saveTrackerData();
                  break;
                }
                case R.id.action_archive: {
                  Snackbar.make(v, "Archived timer '" + trackerData.getDescription() + "'",
                          Snackbar.LENGTH_LONG)
                          .setAction("Action", null).show();
                  break;
                }
                case R.id.action_delete: {
                  boolean deleted = MainActivity.delete(trackerData);
                  if (deleted) {
                    Snackbar.make(v, "Deleted timer '" + trackerData.getDescription() + "'",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                  } else {
                    Snackbar.make(v,
                            "Could not delete timer '" + trackerData.getDescription() + "'",
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                  }
                  break;
                }
              }
              return false;
            }
          });
          popup.show();
        }
      });

      playButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (paused) {
            // start playing
            chronometer.start();
            paused = false;
            playButton.setImageDrawable(ContextCompat.getDrawable(
              view.getContext(), R.drawable.ic_baseline_pause_24));
          } else {
            // pause
            chronometer.stop();
            paused = true;
            playButton.setImageDrawable(ContextCompat.getDrawable(
              view.getContext(), R.drawable.ic_baseline_play_arrow_24));
            // save the time
            trackerData.setElapsedTime(chronometer.getCurrentTime());
            // write to DB
            MainActivity.saveTrackerData();
          }
        }
      });


      //add listener to remove keyboard and cursor on action done
      this.editText.setOnEditorActionListener(
        new TextView.OnEditorActionListener() {
        
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (actionId == EditorInfo.IME_ACTION_DONE) {
            // hide virtual keyboard
            InputMethodManager imm = (InputMethodManager) v.getContext()
              .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.clearFocus();
          }

          // write to DB
          Toast.makeText(v.getContext(), editText.getText(),
             Toast.LENGTH_LONG).show();
          trackerData.setDescription(editText.getText().toString());

          MainActivity.saveTrackerData();
          return false;
        }
      });
    }
  }
}
