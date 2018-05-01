package com.example.three_pillar_cheaptriptravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class TravelActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView mTextView;

    public static Intent newIntent(Context packageContext, String origin,String destination) {
        Intent intent = new Intent(packageContext, TravelActivity.class);
        intent.putExtra("origin", origin);
        intent.putExtra("destination",destination);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        Intent intent =getIntent();
        String origin = intent.getStringExtra("origin");



        String destination = intent.getStringExtra("destination");


        mTextView = (TextView)findViewById(R.id.travel_text);
        mTextView.setText(origin+"       Go To      "+destination);

        mWebView = (WebView) findViewById(R.id.travel_web_view);
        mWebView.loadUrl("http://hketransport.gov.hk/index.php?golang=EN");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
            }
        });







    }
}
