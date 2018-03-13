package com.example.three_pillar_cheaptriptravel.Story;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.album.Album;
import com.album.AlbumConfig;
import com.album.AlbumConstant;
import com.album.AlbumListener;
import com.album.model.AlbumModel;
import com.album.ui.annotation.PermissionsType;
import com.album.ui.widget.OnEmptyClickListener;
import com.album.util.FileUtils;
import com.album.util.SingleMediaScanner;
import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Story;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.yalantis.ucrop.UCrop;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoriesActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;

    //public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;

    List<String> images = new ArrayList<String>();

    //new
    private ArrayList<AlbumModel> list;
    private UCrop.Options dayOptions;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        picture = (ImageView) findViewById(R.id.photo_pictrue);// show the photo
        Button storyTime = (Button) findViewById(R.id.storyTime);

        //FloatingActionButton carema = (FloatingActionButton)findViewById(R.id.action_camera);
        //FloatingActionButton getphoto = (FloatingActionButton)findViewById(R.id.action_add_photo);
        FloatingActionButton delphoto = (FloatingActionButton)findViewById(R.id.action_delete);
        //new
        findViewById(R.id.add_photo).setOnClickListener(this);
        findViewById(R.id.change_photo).setOnClickListener(this);
        findViewById(R.id.btn_video).setOnClickListener(this);

        dayOptions = new UCrop.Options();
        dayOptions.setToolbarTitle("DayTheme");
        dayOptions.setToolbarColor(ContextCompat.getColor(this, R.color.colorAlbumToolbarBackgroundDay));
        dayOptions.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAlbumStatusBarColorDay));
        dayOptions.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAlbumToolbarBackgroundDay));
        list = new ArrayList<>();


        delphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.deleteAll(Story.class); //delete photo
            }
        });



        storyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoriesActivity.this, StoriesProgressViewActivity.class);
                startActivity(intent);
            }
        });


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;

            //new
            case AlbumConstant.ITEM_CAMERA:
                new SingleMediaScanner(this, FileUtils.getScannerFile(imagePath.getPath()),
                        new SingleMediaScanner.SingleScannerListener() {
                            @Override
                            public void onScanStart() {

                            }

                            @Override
                            public void onScanCompleted(int type) {

                            }
                        }, AlbumConstant.TYPE_RESULT_CAMERA);
                UCrop.of(Uri.fromFile(new File(imagePath.getPath())), imagePath = Uri.fromFile(FileUtils.getCameraFile(this, null, false)))
                        .withOptions(new UCrop.Options())
                        .start(this);

                break;
            case UCrop.REQUEST_CROP:
                new SingleMediaScanner(this, FileUtils.getScannerFile(imagePath.getPath()),
                        new SingleMediaScanner.SingleScannerListener() {
                            @Override
                            public void onScanStart() {

                            }

                            @Override
                            public void onScanCompleted(int type) {

                            }
                        }, AlbumConstant.TYPE_RESULT_CROP);

                Toast.makeText(getApplicationContext(), imagePath.getPath(), Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

 /*   @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //如果是 document 类型的 Uri，则通过 document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//显示选中的图片
        Log.d("photo0", imagePath);

    }

    private void handeleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过 Uri 和 selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
*/
    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //Log.d("photo1", Arrays.toString(new Bitmap[]{bitmap}));
            picture.setImageBitmap(bitmap);
            Story story = new Story();
            String photoString = convertIconToString(bitmap);
            story.setImg(photoString);
            story.save();
            /*
            Intent intent = new Intent(StoriesActivity.this, StoriesProgressViewActivity.class);
            intent.putExtra("BitmapImage", bitmap);
            startActivity(intent);
            */
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
        }
    }

    public static String convertIconToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }
//new
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo:
                Album
                        .getInstance()
//                        .setAlbumImageLoader(new SimpleFrescoAlbumImageLoader())
                        .setAlbumImageLoader(new SimpleGlide4xAlbumImageLoader())
                        .setAlbumListener(new MainAlbumListener(this, list))
                        .setAlbumModels(list)
                        .setOptions(dayOptions)
                        .setEmptyClickListener(new OnEmptyClickListener() {
                            @Override
                            public boolean click(View view) {
                                return true;
                            }
                        })
                        .setAlbumCameraListener(null)
                        .setAlbumClass(null)
                        .setConfig(new AlbumConfig()
                                .setCameraCrop(false)
                                .setPermissionsDeniedFinish(false)
                                .setPreviewFinishRefresh(true)
                                .setAlbumBottomFinderTextBackground(R.drawable.selector_btn)
//                                .setAlbumBottomPreviewTextBackground(R.drawable.selector_btn)
                                .setAlbumBottomSelectTextBackground(R.drawable.selector_btn)
                                .setAlbumContentItemCheckBoxDrawable(R.drawable.simple_selector_album_item_check)
//                                .setFrescoImageLoader(true)  // 通知 Album 图片加载框架使用的是 Fresco
                                .setPreviewBackRefresh(true))
                        .start(this);

                break;
            case R.id.btn_video:
                Album
                        .getInstance()
                        .setAlbumImageLoader(new SimpleGlide4xAlbumImageLoader())
                        .setAlbumListener(new MainAlbumListener(this, list))
                        .setAlbumCameraListener(null)
                        .setEmptyClickListener(new OnEmptyClickListener() {
                            @Override
                            public boolean click(View view) {
                                return true;
                            }
                        })
                        .setConfig(
                                new AlbumConfig()
                                        .setVideo(true)
                                        .setAlbumToolbarText(R.string.album_video_title)
                                        .setAlbumContentViewCameraTips(R.string.video_tips)
                                        .setPreviewBackRefresh(true))
                        .start(this);
                break;

            case R.id.change_photo:
                Album
                        .getInstance()
                        .setAlbumListener(new MainAlbumListener(this, null))
                        .setAlbumImageLoader(new SimpleGlide4xAlbumImageLoader())
                        .setOptions(dayOptions)
                        .setAlbumCameraListener(null)
                        .setAlbumClass(null)
                        .setEmptyClickListener(new OnEmptyClickListener() {
                            @Override
                            public boolean click(View view) {
                                return true;
                            }
                        })
                        .setConfig(new AlbumConfig(AlbumConstant.TYPE_NIGHT)
                                .setRadio(true)
                                .setHideCamera(true)
                                .setCrop(true).setCameraPath(Environment.getExternalStorageDirectory().getPath() + "/" + "DCIM/trip")
                                .setuCropPath(Environment.getExternalStorageDirectory().getPath() + "/" + "DCIM" + "/" + "tripCrop")
                                .setPermissionsDeniedFinish(true)
                                .setPreviewFinishRefresh(true)
                                .setPreviewBackRefresh(true)
                                .setCameraCrop(true))
                        .start(this);
                break;



        }
    }

    private class MainAlbumListener implements AlbumListener {

        private Context context;
        private List<AlbumModel> list = null;

        void toast(String s) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }

        MainAlbumListener(Context context, ArrayList<AlbumModel> list) {
            this.context = context.getApplicationContext();
            this.list = list;
        }

        @Override
        public void onAlbumActivityFinish() {
            toast("album activity finish");
        }

        @Override
        public void onAlbumPermissionsDenied(@PermissionsType int type) {
            toast("permissions error");
        }

        @Override
        public void onAlbumFragmentNull() {
            toast("album fragment null");
        }

        @Override
        public void onAlbumPreviewFileNull() {
            toast("preview image has been deleted");
        }

        @Override
        public void onAlbumFinderNull() {
            toast("folder directory is empty");
        }

        @Override
        public void onAlbumBottomPreviewNull() {
            toast("preview no image");
        }

        @Override
        public void onAlbumBottomSelectNull() {
            toast("select no image");
        }

        @Override
        public void onAlbumFragmentFileNull() {
            toast("album image has been deleted");
        }

        @Override
        public void onAlbumPreviewSelectNull() {
            toast("PreviewActivity,  preview no image");
        }

        @Override
        public void onAlbumCheckBoxFileNull() {
            toast("check box  image has been deleted");
        }

        @Override
        public void onAlbumFragmentCropCanceled() {
            toast("cancel crop");
        }

        @Override
        public void onAlbumFragmentCameraCanceled() {
            toast("cancel camera");
        }

        @Override
        public void onAlbumFragmentUCropError(@Nullable Throwable data) {
            toast("crop error:" + data);
        }

        @Override
        public void onAlbumResources(@NonNull List<AlbumModel> list) {
            toast("select count :" + list.size());
            //Log.d("imagepath", list);
            Log.d("image", Arrays.toString(new List[]{list}));
            //displayImage(String.valueOf(list.get(i)));

            Log.d("imageb", list.get(0).getPath());


            for (int i =0;i<list.size();i++){
                displayImage(list.get(i).getPath());
            }

            if (this.list != null) {
                this.list.clear();
                this.list.addAll(list);
            }
        }

        @Override
        public void onAlbumUCropResources(@Nullable File scannerFile) {
            toast("crop file:" + scannerFile);
            Log.d("pppppppp", Arrays.toString(new File[]{scannerFile}));
            displayImage(scannerFile.toString());
        }

        @Override
        public void onAlbumMaxCount() {
            toast("select max count");
        }

        @Override
        public void onAlbumActivityBackPressed() {
            toast("AlbumActivity Back");
        }

        @Override
        public void onAlbumOpenCameraError() {
            toast("camera error");
        }

        @Override
        public void onAlbumEmpty() {
            toast("no image");
        }

        @Override
        public void onAlbumNoMore() {
            toast("album no more");
        }

        @Override
        public void onAlbumResultCameraError() {
            toast("result error");
        }

        @Override
        public void onVideoPlayError() {
            toast("play video error : checked video app");
        }
    }

}
