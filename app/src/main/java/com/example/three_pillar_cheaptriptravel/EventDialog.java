package com.example.three_pillar_cheaptriptravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.object.Event;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/9.
 */

public class EventDialog extends DialogFragment {

    public static EventDialog newInstance(long id) {
        EventDialog eventDialog = new EventDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("id", id);
        eventDialog.setArguments(args);

        return eventDialog;
    }


    public interface  EventDialogListener{
        public void onItemClick(DialogFragment dialog, int which);
    }


    EventDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (EventDialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement EvebtDialogListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Event")
                .setItems(new String[]{"Place Detail","Change Time","Delete Event"}, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        switch (which){

                            //CHANGE TIME
                            case 1:

                                //EndTime
                                TimePickerDialog timePickerDialog2 = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                                        //Get Event ID amd update that event
                                        Long id = getArguments().getLong("id");
                                        Event event = new Event();

                                        event.setEndTime(i+(i1/60.0));
                                        event.updateAll("id = ?", ""+id);

                                        Boolean finished = false;

                                        if(finished) {
                                            dialog.dismiss();
                                        }


                                    }
                                },13,0,true);

                                timePickerDialog2.show();


                                //Start Time
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        Long id = getArguments().getLong("id");
                                        Event event = new Event();


                                        event.setStartTime(i+(i1/60.0));
                                        event.updateAll("id = ?", ""+id);

                                    }
                                },12,0,true);

                                timePickerDialog.show();





                                break;


                            case 2:

                                Long id = getArguments().getLong("id");
                                DataSupport.deleteAll(Event.class,"id=?",""+id);
                                break;
                            default:

                        }

                        Long id = getArguments().getLong("id");
                        Toast.makeText(getActivity(), "ID of Event"+id, Toast.LENGTH_SHORT).show();




                        mListener.onItemClick(EventDialog.this,which);
                    }
                });
        return builder.create();
    }




}
