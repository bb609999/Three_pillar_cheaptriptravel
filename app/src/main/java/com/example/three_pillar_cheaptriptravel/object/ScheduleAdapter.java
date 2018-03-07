package com.example.three_pillar_cheaptriptravel.object;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.ScheduleDisplayActivity;

import java.util.List;

/**
 * Created by Administrator on 7/3/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>{

    private static final String TAG = "scheduleAdapter";

    private Context mContext;

    private List<Schedule> mScheduleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView scheduleImage;
        TextView scheduleName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            scheduleImage = (ImageView) view.findViewById(R.id.schedule_image);
            scheduleName = (TextView) view.findViewById(R.id.schedule_name);
        }
    }

    public ScheduleAdapter(List<Schedule> scheduleList) {
        mScheduleList = scheduleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Schedule schedule = mScheduleList.get(position);
                Intent intent = new Intent(mContext, ScheduleDisplayActivity.class);
                // intent.putExtra(scheduleActivity.schedule_NAME, schedule.getName());
                // intent.putExtra(scheduleActivity.schedule_IMAGE_ID, schedule.getImageId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Schedule schedule = mScheduleList.get(position);
        holder.scheduleName.setText(schedule.getName());
        Glide.with(mContext).load(schedule.getImageId()).into(holder.scheduleImage);
    }

    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }

}

