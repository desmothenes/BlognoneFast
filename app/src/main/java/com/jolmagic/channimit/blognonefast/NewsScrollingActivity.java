package com.jolmagic.channimit.blognonefast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

        // TODO: Move this to where you establish a user session
        logUser();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }

}
