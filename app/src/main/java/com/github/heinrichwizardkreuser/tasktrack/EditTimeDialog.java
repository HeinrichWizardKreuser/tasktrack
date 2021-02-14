package com.github.heinrichwizardkreuser.tasktrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class EditTimeDialog extends AppCompatDialogFragment {

    private BetterChronometer chronometer;
    private EditText editTextTime;
    private EditTimeListener listener;


    public EditTimeDialog(BetterChronometer chronometer) {
        this.chronometer = chronometer;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_time, null);

        builder.setView(view);
        builder.setTitle("Edit Time");


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String timeText = editTextTime.getText().toString();
                listener.applyTimeTexts(timeText, chronometer);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Snackbar.make(view,
                        "Negative",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        editTextTime = view.findViewById(R.id.edit_text_time);
        editTextTime.setText(chronometer.getText().toString());


        return builder.create();
    }

    //@Override
    //public void onAttach(@NonNull Activity activity) {
    //    super.onAttach(activity);
    //
    //    Log.d("onAttach(activity)", "----------------------------");
    //
    //    try {
    //        listener = (EditTimeListener) activity;
    //    } catch (ClassCastException e) {
    //        throw new ClassCastException(activity.toString() +
    //                "must implement EditTimeListener");
    //    }
    //}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EditTimeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement EditTimeListener");
        }
    }

    public interface EditTimeListener {
        void applyTimeTexts(String timeText, BetterChronometer chronometer);
    }
}
