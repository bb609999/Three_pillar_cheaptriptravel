package com.example.three_pillar_cheaptriptravel.object;

/**
 * Created by Administrator on 26/4/2018.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.three_pillar_cheaptriptravel.R;

import java.util.List;


/**
 * Created by Administrator on 7/3/2018.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>{

    private static final String TAG = "placeAdapter";

    private Context mContext;

    private List<Place> mPlaceList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView placeImage;
        TextView placeName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            placeImage = (ImageView) view.findViewById(R.id.place_image);
            placeName = (TextView) view.findViewById(R.id.place_name);
        }
    }

    public PlaceAdapter(List<Place> placeList) {
        mPlaceList = placeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.place_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Place place = mPlaceList.get(position);
                // intent.putExtra(placeActivity.place_NAME, place.getName());
                // intent.putExtra(placeActivity.place_IMAGE_ID, place.getImageId());
                //Intent intent = PlaceListActivity.newIntent(mContext,place.getId());
                //.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = mPlaceList.get(position);
        holder.placeName.setText(place.getPlaceName());
        //Glide.with(mContext).load(place.getImageId()).into(holder.placeImage);
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }

}


