package com.example.three_pillar_cheaptriptravel.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.ScheduleListActivity;
import com.example.three_pillar_cheaptriptravel.object.Schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 12/3/2018.
 */

public class CreateScheduleDialog extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_create_schedule, null))
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        EditText editText = (EditText)
                                CreateScheduleDialog.this.getDialog().findViewById(R.id.schedule_name_create);
                        String schedule_name = editText.getText().toString();

                        DatePicker datePicker = (DatePicker)
                                CreateScheduleDialog.this.getDialog().findViewById(R.id.schedule_date);

//                        String date = datePicker.getDayOfMonth()+"/"+(datePicker.getMonth()+1)+"/"+datePicker.getYear();
//
//                        Log.d("datepicker", "onClick: "+date);
//
//                        Log.d("schedule_name", "onClick: "+schedule_name);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        Date date = calendar.getTime();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                        String date1 = format1.format(date);


                        Schedule schedule = new Schedule();
                        schedule.setName(schedule_name);
                        schedule.setImageId(R.drawable.hk);
                        schedule.setDate(date1);
                        schedule.save();


                        CreateScheduleDialog.this.getActivity().finish();
                        Intent intent = new Intent(CreateScheduleDialog.this.getActivity(),ScheduleListActivity.class);
                        startActivity(intent);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateScheduleDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
