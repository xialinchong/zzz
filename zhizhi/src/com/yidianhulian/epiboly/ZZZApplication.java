package com.yidianhulian.epiboly;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

import android.app.Application;
import android.util.Log;


public class ZZZApplication extends Application implements CallApiListener {
	public ArrayList<HashMap<String, String>> adapter = new ArrayList<HashMap<String, String>>();
	public ArrayList<HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();
	
	private static ZZZApplication mInstance = null;
	private HashMap<String, String> loginUser = null;
	private String deviceToken = null ;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		
		XGPushManager.registerPush(getApplicationContext()
        		, new XGIOperateCallback() {
			
			@Override
			public void onSuccess(Object arg0, int arg1) {
				Log.d("TPush", "注册成功，设备token为：" + arg0);
				deviceToken = (String) arg0;
				HashMap<String, String> userHashMap = getLoginUser();
				if (userHashMap != null) {
					new CallApiTask(0, ZZZApplication.this).execute();
				}
			}
			
			@Override
			public void onFail(Object arg0, int arg1, String arg2) {
				Log.d("TPush", "注册失败，错误码：" + arg1 + ",错误信息：" + arg2);
			}
		});
		
		mInstance = this;
	}
	
	public static ZZZApplication getInstance() {
		return mInstance;
	}
	
	/**
	 * 获取登陆用户信息
	 * @return
	 */
	public HashMap<String, String> getLoginUser() {
		if(loginUser != null) return loginUser;
		
		loginUser = Util.getLoginUser(this);
		
		return loginUser;
	}
	
	/**
	 * 
	 * 获取指定key的用户信息
	 * @return
	 */
	public String getLoginUserItem(String key) {
		if (loginUser == null) loginUser = getLoginUser();
		
		if (loginUser != null && loginUser.containsKey(key)) {
			return loginUser.get(key);
		}
		
		return "";
	}
	
	/**
	 * 
	 * 保存登陆信息
	 */
	public void saveLoginUser(String json_str) {
		Util.saveLoginUser(this, json_str);
		loginUser = Util.getLoginUser(this);
	}
	
	/**
	 * 
	 * 保存登陆信息
	 */
	public void clearLoginUser() {
		Util.clearLoginUser(this);
		loginUser = null;
	}
	
	/**
	 * 
	 * 验证用户是否登陆
	 */
	public static boolean isLogin() {
		if (mInstance.getLoginUser() == null) {
			return false;
		}
		return true;
	}

	@Override
	public JSONObject callApi(int what, Object... params) {
		Map<String, String> mitem = new HashMap<String, String>();
		mitem.put("cookie", 
				Util.getLoginUserItem(ZZZApplication.this.getApplicationContext(), "cookie"));
		mitem.put("type", "android");
		mitem.put("op", "add");
//		mitem.put("deviceToken", deviceToken);
		mitem.put("token", deviceToken);
		return Api.get(Const.UPLOAD_DEVICE_TOKEN, mitem);
	}

	@Override
	public void handleResult(int what, JSONObject result, Object... params) {
		System.out.println("11111");
	}
}
