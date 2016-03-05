package com.jolmagic.channimit.blognonefast;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class NodeScrollingActivity extends AppCompatActivity {

    private Intent intent;
    private TextView descView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        setTitle(intent.getStringExtra("Title"));
        descView = (TextView) findViewById(R.id.desc_in_node_view);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                final Spanned Description = Html.fromHtml(intent.getStringExtra("Description"), getImageHTML(), null);
                handler.post(new Runnable(){
                    public void run() {
                        descView.setText(Description);
                    }
                });
            }
        }).start();
    }

    public Html.ImageGetter getImageHTML() {
        return new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                Drawable drawable = null;
                try {
                    float multipleSize = 1.0f;
                    int density= getResources().getDisplayMetrics().densityDpi;

                    switch(density)
                    {
                        case DisplayMetrics.DENSITY_LOW:
                            multipleSize = 0.75f;
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                            multipleSize = 1f;
                            break;
                        case DisplayMetrics.DENSITY_HIGH:
                            multipleSize = 1.5f;
                            break;
                        case DisplayMetrics.DENSITY_XHIGH:
                            multipleSize = 2f;
                            break;
                        case DisplayMetrics.DENSITY_XXHIGH:
                            multipleSize = 3f;
                            break;
                        case DisplayMetrics.DENSITY_XXXHIGH:
                            multipleSize = 4f;
                            break;
                    }

                    InputStream result = Ion.with(getBaseContext()).load(source).asInputStream().get();
                    drawable = Drawable.createFromStream(result, "src name");
                    drawable.setBounds(
                            0,
                            0,
                            (int)(drawable.getIntrinsicWidth() * multipleSize),
                            (int)(drawable.getIntrinsicHeight() * multipleSize)
                    );
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                return drawable;
            }
        };
    }
}
