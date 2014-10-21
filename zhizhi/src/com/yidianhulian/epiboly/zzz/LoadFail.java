package com.yidianhulian.epiboly.zzz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class LoadFail extends Activity {
	
	private ImageButton re_loading = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_fail);
		
		re_loading = (ImageButton) findViewById(R.id.re_loading);
		re_loading.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(LoadFail.this, SplashActivity.class);
				LoadFail.this.startActivity(intent);
				LoadFail.this.finish();
			}
		});
	}

}
