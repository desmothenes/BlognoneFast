package com.jolmagic.channimit.blognonefast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebCommentActivity extends AppCompatActivity {

    private Intent intent;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_comment);
        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        intent = getIntent();

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(intent.getStringExtra("Link")).get();
                    Element commentAreaElement = doc.getElementById("comment-area");
                    if (commentAreaElement != null) {
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
