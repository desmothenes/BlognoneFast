package com.jolmagic.channimit.blognonefast;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.jolmagic.channimit.blognonefast.view.NewsHeadlineAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

public class NodeScrollingActivity extends AppCompatActivity {

    private Intent intent;
    private TextView descView;
    private String url;

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
                Intent commentIntent = new Intent(getBaseContext(), WebCommentActivity.class);
                commentIntent.putExtra("Link", url);
                startActivity(commentIntent);
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        descView = (TextView) findViewById(R.id.desc_in_node_view);
        descView.setMovementMethod(LinkMovementMethod.getInstance());
        descView.setText(Html.fromHtml(intent.getStringExtra("Description")));

        if (intent.getData() == null) {
            setTitle(intent.getStringExtra("Title"));
            url = intent.getStringExtra("Link");

            final Handler handler = new Handler();
            new Thread(new Runnable() {
                public void run() {
                    final Spanned Description = Html.fromHtml(
                            "<b><big>" + intent.getStringExtra("Title") + "</big></b>\n\n\n" +
                                    intent.getStringExtra("Description"), getImageHTML(), null);
                    handler.post(new Runnable() {
                        public void run() {
                            descView.setText(Description);
                        }
                    });
                }
            }).start();
        } else {
            url = intent.getData().toString();
            Ion.with(getBaseContext())
                    .load(url)
                    .asInputStream()
                    .setCallback(new FutureCallback<InputStream>() {
                        @Override
                        public void onCompleted(Exception ignore, InputStream result) {
                            try {
                                final Document doc = Jsoup.parse(result, "UTF8", url);
                                setTitle(doc.title());

                                final Handler handler = new Handler();
                                new Thread(new Runnable() {
                                    public void run() {
                                        final Spanned Description = Html.fromHtml(
                                                "<b><big>" + doc.title() + "</big></b>\n\n\n" +
                                                        doc.select(".node-content").first().html(), getImageHTML(), null);
                                        handler.post(new Runnable() {
                                            public void run() {
                                                descView.setText(Description);
                                            }
                                        });
                                    }
                                }).start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
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
