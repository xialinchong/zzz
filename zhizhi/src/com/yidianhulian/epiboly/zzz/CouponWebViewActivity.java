package com.yidianhulian.epiboly.zzz;

import com.yidianhulian.epiboly.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class CouponWebViewActivity extends Activity  {
	private WebView myWebView;
	Boolean start_flag = true;
	Boolean end_flag   = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.coupon_webview);
		
	    myWebView = (WebView) findViewById(R.id.coupon_webview);
		myWebView.loadUrl(getIntent().getStringExtra("url"));
			
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (start_flag) {
					start_flag = false;
					Util.showLoading(CouponWebViewActivity.this, Util.DATA_LOADING);
					super.onPageStarted(view, url, favicon);
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (end_flag) {
					end_flag = false;
					Util.hideLoading();
					super.onPageFinished(view, url);
				}
			}
			
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
