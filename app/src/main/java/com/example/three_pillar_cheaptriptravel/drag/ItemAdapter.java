package com.example.three_pillar_cheaptriptravel.drag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Event;
import com.google.maps.android.ui.IconGenerator;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 18/3/2018.
 */



public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Event> mEventList;
    OnItemClickListener mItemClickListener;
    private static final int TYPE_ITEM = 0;
    private final LayoutInflater mInflater;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;

    public ItemAdapter(Context context, List<Event> list, OnStartDragListener dragListner) {
        this.mEventList = list;
        this.mInflater = LayoutInflater.from(context);
        mDragStartListener = dragListner;
        mContext = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View v = mInflater.inflate(R.layout.event_item, viewGroup, false);
            return new VHItem(v );
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {

        if (viewHolder instanceof VHItem) {

            final VHItem holder= (VHItem)viewHolder;
            ((VHItem) viewHolder).title.setText(mEventList.get(i).getPlaceName());
          //  Picasso.with(mContext)
           //         .load("https://secure.parksandresorts.wdpromedia.com/resize/mwImage/1/630/354/75/" +
            //                "wdpromedia.disney.go.com/media/wdpro-assets/parks-and-tickets/entertainment/magic-kingdom/" +
             //               "move-it-shake-it-dance-play-it-street-party/move-it-shake-it-dance-play-it-00.jpg?22102014161654")
             //      .placeholder(R.drawable.ap360)
             //       .into(((VHItem) viewHolder).imageView);

            IconGenerator iconGenerator = new IconGenerator(mContext);
            Bitmap bitmap =  iconGenerator.makeIcon(""+i);
            ((VHItem) viewHolder).imageView.setImageBitmap(bitmap);

            ((VHItem) viewHolder).image_menu.setImageResource(R.drawable.ic_import_export_black_24dp);

            ((VHItem) viewHolder).image_menu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener ,ItemTouchHelperViewHolder{
        public TextView title;
        private ImageView imageView;
        private ImageView image_menu;

        public VHItem(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.event_name);
           image_menu = (ImageView) itemView.findViewById(R.id.image_menu);
            imageView = (ImageView) itemView.findViewById(R.id.event_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mEventList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < mEventList.size() && toPosition < mEventList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mEventList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mEventList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    public void updateList(List<Event> list) {
        mEventList = list;
        notifyDataSetChanged();
    }
}
