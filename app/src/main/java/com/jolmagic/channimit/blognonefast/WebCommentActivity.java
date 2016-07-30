package com.jolmagic.channimit.blognonefast;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebCommentActivity extends AppCompatActivity {

    private static final int OPEN_IN_CHROME = 1;
    private Intent intent;
    private WebView webView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, OPEN_IN_CHROME, Menu.NONE, "OPEN_IN_CHROME");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPEN_IN_CHROME:
                String urlString = intent.getStringExtra("Link");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_comment);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        intent = getIntent();

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(intent.getStringExtra("Link")).get();
                    Element commentAreaElement = doc.getElementById("comment-area");
                    if (commentAreaElement != null && commentAreaElement.children().size() > 0) {
                        final String commentArea = doc.head().html() + doc.getElementById("comment-area").html();
                        handler.post(new Runnable() {
                            public void run() {
                                webView.loadDataWithBaseURL(intent.getStringExtra("Link"), commentArea, "text/html", "UTF-8", "");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            public void run() {
                                webView.loadDataWithBaseURL("", "No comment.", "text/html", "UTF-8", "");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
