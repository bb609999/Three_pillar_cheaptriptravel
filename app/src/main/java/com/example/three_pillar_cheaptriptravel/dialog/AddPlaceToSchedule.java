package com.example.three_pillar_cheaptriptravel.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Place;
import com.example.three_pillar_cheaptriptravel.object.Schedule;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/5/2018.
 */

public class AddPlaceToSchedule extends DialogFragment {

    private List<String> data = new ArrayList<>();
    private Spinner spinner;
    private int place_id;

    private List<Schedule> scheduleList = new ArrayList<>();

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }


    public static AddPlaceToSchedule newInstance(int place_id) {
        AddPlaceToSchedule f = new  AddPlaceToSchedule();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("place_id", place_id);
        f.setArguments(args);

        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        place_id = getArguments().getInt("place_id");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();



        scheduleList = DataSupport.findAll(Schedule.class);
        for(Schedule schedule:scheduleList) {
            data.add(schedule.getName());
        }

        View view = inflater.inflate(R.layout.dialog_add_place,null);
        spinner = (Spinner)view.findViewById(R.id.spinner_schedules);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Schedule schedule = scheduleList.get(spinner.getSelectedItemPosition());
                        Log.d("Spinner", "onClick: "+spinner.getSelectedItemPosition());
                        Place place = Place.getPlace(place_id);

                        Event event = new Event();
                        event.setPlaceName(place.getPlaceName());
                        event.setStartTime(schedule.getGetUpTime());
                        event.setEndTime(schedule.getGetUpTime()+2);
                        event.setDate(schedule.getDate());
                        event.setSchedule_id(schedule.getId());
                        event.setPlace_id(place_id);
                        event.save();


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPlaceToSchedule.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
    

