package com.example.three_pillar_cheaptriptravel.Story;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.three_pillar_cheaptriptravel.R;
import com.example.three_pillar_cheaptriptravel.object.Story;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoriesProgressViewActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    private List<Bitmap> resources = new ArrayList<Bitmap>();

    private StoriesProgressView storiesProgressView;
    private int PROGRESS_COUNT =0;
    private ImageView image;
    private int counter = 0;
    //List<Bitmap> imagelist = new ArrayList<Bitmap>();
    //String[] images = imagelist.toArray(new String[0]);


    //播放圖片的時間
    long pressTime = 0L;
    long limit = 500L;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_progress_view);

        /*
        Intent intent = getIntent();
        Bitmap bitmap  = (Bitmap) intent.getParcelableExtra("BitmapImage");
        */
        List<Story> Stories = DataSupport.findAll(Story.class);

        for(Story story:Stories){
            resources.add(convertStringToIcon(story.getImg()));
        }

        PROGRESS_COUNT=Stories.size();
        if (PROGRESS_COUNT !=0) {
            storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
            storiesProgressView.setStoriesCount(PROGRESS_COUNT); // <- set stories
            storiesProgressView.setStoryDuration(1200L); // <- set a story duration
            storiesProgressView.setStoriesListener(this); // <- set listener
            storiesProgressView.startStories(); // <- start progress
            image = (ImageView) findViewById(R.id.image);
            Bitmap bm = resources.get(counter);
            image.setImageBitmap(bm);
        }else{
            Toast.makeText(this, "Please add photoes !!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StoriesProgressViewActivity.this, StoriesActivity.class);
            startActivity(intent);
        }


        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
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

    //右邊點就下一個
    @Override
    public void onNext() {
        Bitmap bm = resources.get(++counter);
        image.setImageBitmap(bm);
        //Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
    }

    //左邊點就上一個
    @Override
    public void onPrev() {
        // Call when finished revserse animation.
        //Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();
        if ((counter - 1) < 0) return;
        Bitmap bm = resources.get(--counter);
        image.setImageBitmap(bm);
    }

    @Override
    public void onComplete() {
        //Toast.makeText(this, "onComplete", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

}

