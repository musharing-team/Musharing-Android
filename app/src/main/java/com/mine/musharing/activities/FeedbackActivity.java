package com.mine.musharing.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mine.musharing.R;
import com.mine.musharing.utils.RequestUtil;
import com.mine.musharing.utils.Utility;

public class FeedbackActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Show Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_notice);
        toolbar.setTitle("意见反馈");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("意见反馈");
        }

        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(RequestUtil.FORUM_FEEDBACK_URL);

        setupTransition();
    }

    /**
     * 设置 Activity 的转场动画
     */
    private void setupTransition() {
        TransitionSet transitionSet1 = Utility.getRandomTransitionSet();
        TransitionSet transitionSet2 = Utility.getRandomTransitionSet();
        TransitionSet transitionSet3 = Utility.getRandomTransitionSet();

        getWindow().setEnterTransition(transitionSet1);
        getWindow().setExitTransition(transitionSet2);
        getWindow().setReenterTransition(transitionSet3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
