package com.example.three_pillar_cheaptriptravel.Story;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Schedule;
import com.example.three_pillar_cheaptriptravel.object.Stories;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DiaryAddActivity extends BGAPPToolbarActivity implements EasyPermissions.PermissionCallbacks, BGASortableNinePhotoLayout.Delegate {
    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";


    private CheckBox mSingleChoiceCb;

    private CheckBox mTakePhotoCb;

    private CheckBox mEditableCb;

    private CheckBox mPlusCb;

    private CheckBox mSortableCb;

    private BGASortableNinePhotoLayout mPhotosSnpl;

    private EditText mContentEt;

    //get schedule name
    private List<Schedule> name;
    //count the diary num of the events
    private int id;

    public static Stories getMoment(Intent intent) {
        return intent.getParcelableExtra(EXTRA_MOMENT);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_diary_add);
        mSingleChoiceCb = findViewById(R.id.cb_moment_add_single_choice);
        mTakePhotoCb = findViewById(R.id.cb_moment_add_take_photo);

        mEditableCb = findViewById(R.id.cb_moment_add_editable);
        mPlusCb = findViewById(R.id.cb_moment_add_plus);
        mSortableCb = findViewById(R.id.cb_moment_add_sortable);

        mContentEt = findViewById(R.id.et_moment_add_content);
        mPhotosSnpl = findViewById(R.id.snpl_moment_add_photos);
    }

    @Override
    protected void setListener() {
        mSingleChoiceCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mPhotosSnpl.setData(null);
                    mPhotosSnpl.setMaxItemCount(1);
                } else {
                    mPhotosSnpl.setMaxItemCount(9);
                }
            }
        });
        mEditableCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPhotosSnpl.setEditable(checked);
            }
        });
        mPlusCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPhotosSnpl.setPlusEnable(checked);
            }
        });
        mSortableCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mPhotosSnpl.setSortable(checked);
            }
        });

        mPhotosSnpl.setDelegate(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Add to friend circle");

        mEditableCb.setChecked(mPhotosSnpl.isEditable());
        mPlusCb.setChecked(mPhotosSnpl.isPlusEnable());
        mSortableCb.setChecked(mPhotosSnpl.isSortable());
    }

    public void onClick(View v) {
        if (v.getId() == R.id.tv_moment_add_choice_photo) {
            choicePhotoWrapper();
        } else if (v.getId() == R.id.tv_moment_add_publish) {
            String content = mContentEt.getText().toString().trim();
            if (content.length() == 0 && mPhotosSnpl.getItemCount() == 0) {
                Toast.makeText(this, "you must fill in your currently thinking & choose the photo！", Toast.LENGTH_SHORT).show();
                return;
            }else if (content.length() != 0 && mPhotosSnpl.getItemCount() == 0){
                Toast.makeText(this, "must choose a photo！", Toast.LENGTH_SHORT).show();
                return;
            }

            //get note
            Log.d("comment", mContentEt.getText().toString().trim());
            //get image path
            Log.d("imageoutput", mPhotosSnpl.getData().toString());

            Stories stories = new Stories();
            Intent intent = getIntent();
            if (content.length()!=0 && mPhotosSnpl.getItemCount() != 0) {
                stories.setContent(content);
                stories.setPhotos(new ArrayList<String>(mPhotosSnpl.getData()));
                stories.getId();
            }else if (content.length() == 0 && mPhotosSnpl.getItemCount() != 0){
                stories.setContent("");
                stories.setPhotos(new ArrayList<String>(mPhotosSnpl.getData()));
                stories.getId();
            }

            int schedule_id = intent.getIntExtra("schedule_id",-1);
            Log.d(TAG, "DiaryAdd_schedule_id: "+schedule_id);
            stories.setSchedule_id(schedule_id);

            int event_id = intent.getIntExtra("event_id", -1);
            Log.d(TAG, "DiaryAdd_event_id: "+event_id);
            stories.setEvent_id(event_id);


            Intent intent1 = new Intent();
            intent1.putExtra(EXTRA_MOMENT, stories);
            setResult(RESULT_OK, intent1);
            finish();
            stories.save();

        }
    }
/*  private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //Log.d("photo1", Arrays.toString(new Bitmap[]{bitmap}));
            //picture.setImageBitmap(bitmap);
            Stories story = new Stories();
            String photoString = convertIconToString(bitmap);
            story.setImages(photoString);
            story.save();

        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
        }
    }*/



    public static String convertIconToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }
    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models)
                .selectedPhotos(models)
                .maxChooseCount(mPhotosSnpl.getMaxItemCount())
                .currentPosition(position)
                .isFromTakePhoto(false)
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
        Toast.makeText(this, "change the order", Toast.LENGTH_SHORT).show();
    }

    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "3PillarTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(mTakePhotoCb.isChecked() ? takePhotoDir : null)
                    .maxChooseCount(mPhotosSnpl.getMaxItemCount() - mPhotosSnpl.getItemCount())
                    .selectedPhotos(null)
                    .pauseOnScroll(false)
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);


        } else {
            EasyPermissions.requestPermissions(this, "photo preview need your permission:\n\n1.access photo from device\n\n2.take photo", PRC_PHOTO_PICKER, perms);
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
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "you refused to allow 'select photo' permission!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            if (mSingleChoiceCb.isChecked()) {
                mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedPhotos(data));
            } else {
                mPhotosSnpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
            }
            Log.d("imagelink", BGAPhotoPickerActivity.getSelectedPhotos(data).toString());

        } else if (requestCode == RC_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }
}