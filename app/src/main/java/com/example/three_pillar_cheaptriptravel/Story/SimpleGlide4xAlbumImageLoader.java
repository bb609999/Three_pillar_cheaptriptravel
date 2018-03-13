package com.example.three_pillar_cheaptriptravel.Story;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.album.AlbumImageLoader;
import com.album.model.AlbumModel;
import com.album.model.FinderModel;
import com.album.ui.annotation.FrescoType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.three_pillar_cheaptriptravel.R;

/**
 * Created by waiwai on 13/3/2018.
 */

public class SimpleGlide4xAlbumImageLoader implements AlbumImageLoader {

    private RequestOptions requestOptions;

    public SimpleGlide4xAlbumImageLoader() {
        requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .centerCrop();
    }

    @Override
    public void displayAlbum(@NonNull ImageView view, int width, int height, @NonNull AlbumModel albumModel) {
        Glide
                .with(view.getContext())
                .load(albumModel.getPath())
                .apply(requestOptions.override(width, height))
                .into(view);
    }

    @Override
    public void displayAlbumThumbnails(@NonNull ImageView view, @NonNull FinderModel finderModel) {
        Glide
                .with(view.getContext())
                .load(finderModel.getThumbnailsPath()).apply(requestOptions)
                .into(view);
    }

    @Override
    public void displayPreview(@NonNull ImageView view, @NonNull AlbumModel albumModel) {
        Glide
                .with(view.getContext())
                .load(albumModel.getPath()).apply(requestOptions)
                .into(view);
    }

    @Override
    public ImageView frescoView(@NonNull Context context, @FrescoType int type) {
        return null;
    }
}
