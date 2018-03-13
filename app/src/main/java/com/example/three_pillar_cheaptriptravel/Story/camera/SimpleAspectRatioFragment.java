package com.example.three_pillar_cheaptriptravel.Story.camera;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.cameraview.AspectRatio;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by waiwai on 13/3/2018.
 */

public class SimpleAspectRatioFragment extends DialogFragment {

    private static final String ARG_ASPECT_RATIOS = "aspect_ratios";
    private static final String ARG_CURRENT_ASPECT_RATIO = "current_aspect_ratio";

    private Listener mListener;

    public static SimpleAspectRatioFragment newInstance(Set<AspectRatio> ratios,
                                                        com.google.android.cameraview.AspectRatio currentRatio) {
        final SimpleAspectRatioFragment fragment = new SimpleAspectRatioFragment();
        final Bundle args = new Bundle();
        args.putParcelableArray(ARG_ASPECT_RATIOS, ratios.toArray(new com.google.android.cameraview.AspectRatio[ratios.size()]));
        args.putParcelable(ARG_CURRENT_ASPECT_RATIO, currentRatio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (Listener) context;
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final com.google.android.cameraview.AspectRatio[] ratios = (com.google.android.cameraview.AspectRatio[]) args.getParcelableArray(ARG_ASPECT_RATIOS);
        if (ratios == null) {
            throw new RuntimeException("No ratios");
        }
        Arrays.sort(ratios);
        final com.google.android.cameraview.AspectRatio current = args.getParcelable(ARG_CURRENT_ASPECT_RATIO);
        final AspectRatioAdapter adapter = new AspectRatioAdapter(ratios, current);
        return new AlertDialog.Builder(getActivity())
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        mListener.onAspectRatioSelected(ratios[position]);
                    }
                })
                .create();
    }

    private static class AspectRatioAdapter extends BaseAdapter {

        private final com.google.android.cameraview.AspectRatio[] mRatios;
        private final com.google.android.cameraview.AspectRatio mCurrentRatio;

        AspectRatioAdapter(com.google.android.cameraview.AspectRatio[] ratios, com.google.android.cameraview.AspectRatio current) {
            mRatios = ratios;
            mCurrentRatio = current;
        }

        @Override
        public int getCount() {
            return mRatios.length;
        }

        @Override
        public com.google.android.cameraview.AspectRatio getItem(int position) {
            return mRatios[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            AspectRatioAdapter.ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new AspectRatioAdapter.ViewHolder();
                holder.text = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(holder);
            } else {
                holder = (AspectRatioAdapter.ViewHolder) view.getTag();
            }
            com.google.android.cameraview.AspectRatio ratio = getItem(position);
            StringBuilder sb = new StringBuilder(ratio.toString());
            if (ratio.equals(mCurrentRatio)) {
                sb.append(" *");
            }
            holder.text.setText(sb);
            return view;
        }

        private static class ViewHolder {
            TextView text;
        }

    }

    interface Listener {
        void onAspectRatioSelected(@NonNull com.google.android.cameraview.AspectRatio ratio);
    }

}
