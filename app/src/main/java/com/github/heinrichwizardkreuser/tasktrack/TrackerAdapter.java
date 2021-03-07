package com.github.heinrichwizardkreuser.tasktrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.text.InputType;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import static androidx.core.content.ContextCompat.getSystemService;

public class TrackerAdapter
    extends RecyclerView.Adapter<TrackerAdapter.ViewHolder>
    implements ItemMoveCallback.ItemTouchHelperContract {

  private FragmentManager fragmentManager;
  private ArrayList<TrackerData> trackerDataList;

  public TrackerAdapter(ArrayList<TrackerData> trackerDataList, FragmentManager fragmentManager) {
    this.trackerDataList = trackerDataList;
    this.fragmentManager = fragmentManager;
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
    holder.labelTextView.setText(trackerDataList.get(position).getDescription());
    holder.trackerData = myListData;
    holder.fragmentManager = this.fragmentManager;
    holder.chronometer.setCurrentTime(myListData.getElapsedTime());
  }

  /***************************************DRAG AND DROP**************************************/

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



  public void removeItem(int position) {
    trackerDataList.remove(position);
    notifyItemRemoved(position);
  }

  public void restoreItem(TrackerData item, int position) {
    trackerDataList.add(position, item);
    notifyItemInserted(position);
  }

  public ArrayList<TrackerData> getData() {
    return trackerDataList;
  }



  @Override
  public int getItemCount() {
    return trackerDataList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    View rowView;
    public TextView labelTextView;
    public TrackerData trackerData;
    public FragmentManager fragmentManager;
    public RelativeLayout relativeLayout;
    public FloatingActionButton playButton;
    public FloatingActionButton options;
    public boolean paused = true;
    public BetterChronometer chronometer;
    public ViewHolder(View itemView) {
      super(itemView);
      this.rowView = itemView;
      this.labelTextView = (TextView) itemView.findViewById(R.id.labelTextView);
      this.relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
      this.chronometer = (BetterChronometer)itemView.findViewById(R.id.chronometer);
      this.playButton = (FloatingActionButton)itemView.findViewById(R.id.fab_play);
      this.options = (FloatingActionButton)itemView.findViewById(R.id.fab_more_vert);
      ViewHolder viewHolder = this;
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
                  //int pos = trackerDataList.indexOf(trackerData);
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

                  notifyDataSetChanged();
                  //notifyItemRemoved(pos);
                  break;
                }
                case R.id.action_edit: {
                  EditTimeDialog dialog = new EditTimeDialog(viewHolder);
                  dialog.show(fragmentManager, "");
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
    }
  }
}
