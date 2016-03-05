package com.jolmagic.channimit.blognonefast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.jolmagic.channimit.blognonefast.view.NewsHeadlineAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import io.fabric.sdk.android.Fabric;

public class NewsScrollingActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    String TAG = " NewsScrollingActivity";
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_news_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Ion.with(getBaseContext())
                .load("https://www.blognone.com/atom.xml")
                .asInputStream()
                .setCallback(new FutureCallback<InputStream>() {
                    @Override
                    public void onCompleted(Exception ignore, InputStream result) {
                        try {
                            Feed feed = EarlParser.parseOrThrow(result, 0);

                            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.news_headline_in_main);
                            mRecyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mAdapter = new NewsHeadlineAdapter(feed.getItems());
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (XmlPullParserException | IOException | DataFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
