package com.example.three_pillar_cheaptriptravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.object.Event;
import com.example.three_pillar_cheaptriptravel.place.PlaceDetailActivity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/9.
 */

public class EventDialog extends DialogFragment {

    Activity activity;
    Context context;
    long event_id;

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
        this.activity = activity;
        try{
            mListener = (EventDialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement EvebtDialogListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        builder.setTitle("Event")
                .setItems(new String[]{"Place Detail","Change Time","Delete Event","Change Time"}, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        switch (which){

                            case 0:
                                Intent place_detail = new Intent(EventDialog.this.getActivity(), PlaceDetailActivity.class);
                                Long id = getArguments().getLong("id");
                                Event event = DataSupport.where("id = ?", ""+id).findFirst(Event.class);
                                int place_id = event.getPlace_id();
                                place_detail.putExtra("place_id",place_id);

                                startActivity(place_detail);

                                break;

                            //CHANGE TIME
                            case 1:
                                break;


                            case 2:

                                event_id = getArguments().getLong("id");
                                DataSupport.deleteAll(Event.class,"id=?",""+event_id);
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
