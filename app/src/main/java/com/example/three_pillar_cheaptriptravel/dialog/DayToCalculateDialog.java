package com.example.three_pillar_cheaptriptravel.dialog;

import android.app.Dialog;
import android.content.Context;
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
import com.example.three_pillar_cheaptriptravel.object.DateFormat;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.object.Schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayToCalculateDialog extends DialogFragment {

        private int schedule_id;
        private List<String> data = new ArrayList<>();
        private Spinner spinner;
        private Context mContext;
        private Callbacks mCallbacks;

    public interface Callbacks {
        void calculatePath(int schedule_id,String dateString);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface NoticeDialogListener {
            public void onDialogPositiveClick(DialogFragment dialog);
            public void onDialogNegativeClick(DialogFragment dialog);
        }

    public static DayToCalculateDialog newInstance(int schedule_id) {
        DayToCalculateDialog f = new DayToCalculateDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("schedule_id", schedule_id);
        f.setArguments(args);

        return f;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        schedule_id = getArguments().getInt("schedule_id");
        final Schedule schedule = Schedule.getSchedule(schedule_id);
        Log.d("TEST", "onCreateDialog: "+schedule_id);
        for(int i=0;i<schedule.getMaxDays();i++){

            Date start_date = Event.StringToDate(schedule.getDate());
            String now_date = DateFormat.DateToString(DateFormat.changeDate(start_date,i));

            data.add(now_date+"(Day"+(i+1)+")");
        }


//        Log.d("TEST", "onCreateDialog: "+schedule.getMaxDays());


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_event_time_arrange,null);

        spinner = (Spinner)view.findViewById(R.id.spinner_days);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
//        mContext = getActivity();



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Calculate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        int days = spinner.getSelectedItemPosition();
                        Date start_date = Event.StringToDate(schedule.getDate());
                        String now_date = DateFormat.DateToString(DateFormat.changeDate(start_date,days));


//                        calculatePath(schedule_id,now_date);
                        mCallbacks.calculatePath(schedule_id,now_date);




                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DayToCalculateDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
