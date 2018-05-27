package com.example.three_pillar_cheaptriptravel.Story;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Stories;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DiaryListActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener {
    private static final int PRC_PHOTO_PREVIEW = 1;

    private static final int RC_ADD_MOMENT = 1;

    private RecyclerView mMomentRv;
    private MomentAdapter mMomentAdapter;



    private BGANinePhotoLayout mCurrentClickNpl;

    //new
    private List<ArrayList<String>> images = new ArrayList<ArrayList<String>>();
    private List<String> text = new ArrayList<String>();
    private int schedule_id;
    private int event_id;
    private List<String> findtext = new ArrayList<String>();
    private List<ArrayList<String>> findimages = new ArrayList<ArrayList<String>>();


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_diary_list);
        mMomentRv = findViewById(R.id.rv_moment_list_moments);

    }


    @Override
    protected void setListener() {
        mMomentAdapter = new MomentAdapter(mMomentRv);
        Log.d("Adapter", mMomentAdapter.toString());
        mMomentAdapter.setOnRVItemClickListener(this);
        mMomentAdapter.setOnRVItemLongClickListener(this);

        mMomentRv.addOnScrollListener(new BGARVOnScrollListener(this));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle("Your Diary");

        mMomentRv.setLayoutManager(new LinearLayoutManager(this));
        mMomentRv.setAdapter(mMomentAdapter);
        Log.d("Adapter1", mMomentRv.toString());

        Intent intent = getIntent();
        schedule_id = intent.getIntExtra("schedule_id",-1);
        Log.d(TAG, "diarylist_schedule_id: "+schedule_id);

        event_id = intent.getIntExtra("event_id", -1);
        Log.d(TAG, "diarylist_event_id: "+event_id);

        FindDatainDB();
    }

    private void FindDatainDB() {
        List<Stories> stories = DataSupport.where("schedule_id=? and event_id=? ", ""+schedule_id, ""+event_id).find(Stories.class);
        List<Stories> moments = new ArrayList<>();
        for(Stories story:stories){
            findimages.add(story.getPhotos());
            findtext.add(story.getContent());
        }

        for (int i =0; i<stories.size();i++) {
            moments.add(new Stories(findtext.get(i), findimages.get(i)));
        }

        mMomentAdapter.setData(moments);

    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_moment_list_add) {

            //go to DiaryAddActivity and send schedule_id, event_id
            Intent intent = new Intent(this, DiaryAddActivity.class);
            intent.putExtra("schedule_id",schedule_id);
            intent.putExtra("event_id", event_id);
            startActivityForResult(intent, RC_ADD_MOMENT);

        }else if(v.getId() == R.id.tv_moment_list_del_all){
            //delete photo
            DataSupport.deleteAll(Stories.class,"event_id=? and schedule_id=?", ""+event_id, ""+schedule_id);
            images.clear();
            text.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_ADD_MOMENT) {
            mMomentAdapter.addFirstItem(DiaryAddActivity.getMoment(data));
            mMomentRv.smoothScrollToPosition(0);
        }
    }


    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void photoPreviewWrapper() {
        if (mCurrentClickNpl == null) {
            return;
        }

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File downloadDir = new File(Environment.getExternalStorageDirectory(), "3PillarDownload");
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(this)
                    .saveImgDir(downloadDir);
            if (mCurrentClickNpl.getItemCount() == 1) {
                photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl.getCurrentClickItem());
            } else if (mCurrentClickNpl.getItemCount() > 1) {
                photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl.getData())
                        .currentPosition(mCurrentClickNpl.getCurrentClickItemPosition());
            }
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "photo preview need your permission:\n\n1.access photo from device", PRC_PHOTO_PREVIEW, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PREVIEW) {
            Toast.makeText(this, "you refused to allow 'photo preivew' permission!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
        mCurrentClickNpl = ninePhotoLayout;
        photoPreviewWrapper();
    }

    //edit the diary
    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        Toast.makeText(this, "click item " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup viewGroup, View view, int position) {
        Toast.makeText(this, "long click item " + position, Toast.LENGTH_SHORT).show();
        return true;
    }


    private class MomentAdapter extends BGARecyclerViewAdapter<Stories> {

        public MomentAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_moment);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Stories stories) {

            images.clear();
            text.clear();
            List<Stories> Stories = DataSupport.where("schedule_id=? and event_id=?", ""+schedule_id, ""+event_id).find(Stories.class);
            for(Stories story:Stories){
                images.add(story.getPhotos());
                text.add(story.getContent());
            }
            Log.d("img", Arrays.toString(new List[]{images}));
            Log.d("imgtext", Arrays.toString(new List[]{text}));
            Log.d("position", String.valueOf(position));
            Log.d("diarylist_photo", Arrays.toString(new ArrayList[]{stories.photos}));



            if (TextUtils.isEmpty(stories.content)) {
                Log.d("data1", String.valueOf(View.GONE));
                helper.setVisibility(R.id.tv_item_moment_content, View.GONE);
            } else {
                helper.setVisibility(R.id.tv_item_moment_content, View.VISIBLE);
                helper.setText(R.id.tv_item_moment_content, stories.content);
                Log.d("data2", String.valueOf(View.VISIBLE));
                Log.d("data3", stories.content);

            }

            BGANinePhotoLayout ninePhotoLayout = helper.getView(R.id.npl_item_moment_photos);
            ninePhotoLayout.setDelegate(DiaryListActivity.this);

            ninePhotoLayout.setData(stories.photos);
        }

    }
    public static Bitmap convertStringToIcon(String st){
        // OutputStream out;
        Bitmap bitmap = null;
        try{
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }

}
