package com.yidianhulian.epiboly.zzz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

public class SplashActivity extends Activity implements CallApiListener {
//	private final int SPLASH_DISPLAY_LENGHT = 1;  
	ZZZApplication app;
//	ArrayList<HashMap<String, String>> adapter = new ArrayList<HashMap<String, String>>();
//	private ArrayList<HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();
//	private ArrayList<HashMap<String, Object>> content = new ArrayList<HashMap<String, Object>>();
	private static final int LOAD_CATEGORIES = 1;//加载分类
	private static final int LOAD_TABCATS = 2;//加载栏目
//	private static final int LOAD_TABCATS_CONTENT = 3;//默认最新优惠文章
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        app = (ZZZApplication) SplashActivity.this.getApplication();
        setContentView(R.layout.splash);  
        
        new CallApiTask(LOAD_CATEGORIES, SplashActivity.this).execute();   
    }

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public JSONObject callApi(int what, Object...params) {
		Map<String, String> mitem = new HashMap<String, String>();
		switch (what) {
		case LOAD_TABCATS:
			
			return Api.get(Const.GET_TAB_LIST, mitem);
		case LOAD_CATEGORIES:
			
			return Api.get(Const.GET_GOODS_CATEGORY_LIST, mitem);
//		case LOAD_TABCATS_CONTENT:
//			mitem.put("catid", "0");
//			mitem.put("per_page", "20");
//			return Api.get("http://www.zhizhizhi.com/api/goods/get_recent_goods", mitem);
		default:
			return null;
		}
	}

	@Override
	public void handleResult(int what, JSONObject result, Object...params) {
		if (result==null || !"ok".equals(Api.getJSONValue(result, "status"))) {
				//
			Intent intent = new Intent(SplashActivity.this, LoadFail.class);
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
		}
		else {
			switch (what) {
			case LOAD_TABCATS:
				try {
					JSONArray tabcats = result.getJSONArray("tabcats");
					app.addresses.clear();
					for (int i = 0; i < tabcats.length(); i++) {
						JSONObject jsonObject = (JSONObject)tabcats.get(i);
						HashMap<String, String> one = new HashMap<String, String>();
						one.put("site_cat_id", jsonObject.getString("site_cat_id"));
						one.put("slug", jsonObject.getString("slug"));
						one.put("cat_name", jsonObject.getString("cat_name"));
						app.addresses.add(one);
					}
					if ( !app.adapter.isEmpty() && app.addresses.size() > 1 ) {
	            		Intent mainIntent = new Intent(SplashActivity.this,  
	                            MainActivity.class);
	                    mainIntent.putExtra("adapter", (Serializable)app.adapter);
	                    mainIntent.putExtra("addresses", (Serializable)app.addresses);
	                    SplashActivity.this.startActivity(mainIntent);  
	                    SplashActivity.this.finish();  
	        		} else {
	        			
	        		}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
				break;
			case LOAD_CATEGORIES:
				try {
					JSONArray categories = (JSONArray)Api.getJSONValue(result, "categories");
					app.adapter.clear();
					HashMap<String, String> all = new HashMap<String, String>();
					all.put("name", "所有优惠");
					all.put("id", "0");
					app.adapter.add( all  );
					for (int i = 0; i < categories.length(); i++) {
						JSONObject jsonObject = (JSONObject)categories.get(i);
						HashMap<String, String> one = new HashMap<String, String>();
						one.put("name", jsonObject.getString("title"));
						one.put("id", jsonObject.getString("id"));
						app.adapter.add( one );
					}
					new CallApiTask(LOAD_TABCATS, SplashActivity.this).execute();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
//			case LOAD_TABCATS_CONTENT:
//				try {
//					JSONArray goods = (JSONArray)Api.getJSONValue(result, "goods");
//					for (int i = 0; i < goods.length(); i++) {
//						HashMap<String, Object> map = new HashMap<String, Object>();
//						JSONObject jsonObj = (JSONObject)goods.get(i);
//						map.put("good_desc", jsonObj.getString("title"));
//						String shop = "";
//						JSONArray jarr =  jsonObj.getJSONArray("shop");
//						for (int j = 0; j < jarr.length(); j++) {
//							shop = shop + " " + jarr.get(j);
//						}
//						map.put("vendor_name", shop);
//						map.put("good_img", jsonObj.getString("thumbnail"));
//						map.put("time_interval", "5分钟之前");
//						map.put("collect_qty", "收藏：" + jsonObj.getString("favor_count"));
//						map.put("comment_qty", "评论：" + jsonObj.getString("comment_count"));
//						content.add(map);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//            	if ( !adapter.isEmpty() && addresses.size() > 1 && 
//            			!content.isEmpty() ) {
//            		Intent mainIntent = new Intent(SplashActivity.this,  
//                            MainActivity.class);
//                    mainIntent.putExtra("adapter", (Serializable)adapter);
//                    mainIntent.putExtra("addresses", (Serializable)addresses);
//                    mainIntent.putExtra("content", (Serializable)content);
//                    SplashActivity.this.startActivity(mainIntent);  
//                    SplashActivity.this.finish();  
//        		} else {
//        			
//        		}
//				break;
			default:
				break;
			}
		}
	}
}
