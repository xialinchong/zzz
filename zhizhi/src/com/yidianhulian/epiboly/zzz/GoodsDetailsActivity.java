package com.yidianhulian.epiboly.zzz;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class GoodsDetailsActivity extends FragmentActivity {
	private String good_id = null;
	private GoodsDetail fg ;
	@Override
	public void onStart() {
		super.onStart();
		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
		if (click != null) {
			String customContent = click.getCustomContent();
			if (customContent != null && customContent.length() != 0) {
				try {
					JSONObject jsObject = new JSONObject(customContent);
					good_id = jsObject.getString("goods_id");
					fg = (GoodsDetail) getSupportFragmentManager().
							findFragmentByTag("good_detail");
					fg.reload();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		XGPushManager.onActivityStoped(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_detail_frame);
		
		FragmentManager manager = getSupportFragmentManager();
		good_id = getIntent().getStringExtra("id");
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("优惠详情");
		
	    if (manager.findFragmentByTag("good_detail") == null) {  
	        // if(savedInstanceState == null)也可判断该fragment是否已经加载  
	    	
	        manager.beginTransaction()  
	            .replace(R.id.good_detail_frame, new GoodsDetail(), "good_detail")  
	            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)// 设置动画  
	            .addToBackStack("good_detail") // 将该fragment加入返回堆  	          
	            .commit();
	        
	    }  else if (manager.findFragmentByTag("good_comment") == null){
	    	manager.beginTransaction()  
            .replace(R.id.good_detail_frame, new Comments(), "good_comment")  
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)// 设置动画  
            .addToBackStack("good_comment") // 将该fragment加入返回堆  	          
            .commit();  
	    }
	    
	}

	public String getGoodId() {
		return good_id;
	}
	
	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
			finish();
			return;
		}
		
		super.onBackPressed();
	}
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
		return super.onOptionsItemSelected(item);
	}
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {		
//		getMenuInflater().inflate(R.menu.good_detail_main, menu);
//		
//		return super.onCreateOptionsMenu(menu);
//	}

}
