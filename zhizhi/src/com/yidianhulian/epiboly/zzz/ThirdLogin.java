package com.yidianhulian.epiboly.zzz;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

public class ThirdLogin implements CallApiListener,PlatformActionListener,Callback{
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR= 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	
	private static final int GET_NONCE_SNS = 8;
	private static final int USER_LOGIN_SNS = 9;
	
	private Activity mContext = null;
	private String from = "";
	
	private String lUserID = "";
	private String lParams = "";
	private String lFrom = "";
	
	private String nonce = null;
	
	private ZZZApplication app = null;
	
	public ThirdLogin(Activity context,String from){
		this.mContext = context;
		this.from = from;
	}
	
	public void startThirdLogin(){
		ShareSDK.initSDK(mContext);
		if(from.toLowerCase().equals("qq")){
			authorize(new QQ(mContext));
		}else if(from.toLowerCase().equals("sina")){
			authorize(new SinaWeibo(mContext));
		}
	}
	
	private void authorize(Platform plat){
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	private void login(HashMap<String, Object> userInfo) {
		String plat = (String) userInfo.get("platName");
		String userId = (String) userInfo.get("userid");
		JSONObject object = new JSONObject();
		try {
			if(plat.toUpperCase().equals("QQ")){
				this.lFrom = "qq";
				String string = (String) userInfo.get("nickname");
				object.put("nickname", string);
				object.put("figureurl_2", userInfo.get("figureurl_qq_1"));
			}else if(plat.toUpperCase().equals("SINAWEIBO")){
				this.lFrom = "sina";
				object.put("screen_name", userInfo.get("screen_name"));
				object.put("domain", userInfo.get("domain"));
				object.put("url", userInfo.get("profile_image_url"));
			}
			this.lUserID = userId;
			this.lParams = object.toString();
			load_api(GET_NONCE_SNS);
			Message msg = new Message();
			msg.what = MSG_LOGIN;
			msg.obj = plat;
			UIHandler.sendMessage(msg, this);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}

	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			res.put("platName", platform.getName());
			res.put("userid", platform.getDb().getUserId());
			msg.obj = res;
			UIHandler.sendMessage(msg, this);
		}
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
			platform.removeAccount();
		}
		t.printStackTrace();
	}

	@Override
	public boolean handleMessage(Message msg) {
		Util.hideLoading();
		switch(msg.what) {
			case MSG_USERID_FOUND: {
				Util.hideLoading();
				Toast.makeText(mContext, R.string.userid_found, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_LOGIN: {
				String text = mContext.getResources().getString(R.string.logining, msg.obj);
				Util.showLoading(mContext, text);
//				Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_AUTH_CANCEL: {
				Util.hideLoading();
				Toast.makeText(mContext, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_AUTH_ERROR: {
				Util.hideLoading();
				Toast.makeText(mContext, R.string.auth_error, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_AUTH_COMPLETE: {
				Util.showLoading(mContext, R.string.auth_complete);
//				Toast.makeText(mContext, R.string.auth_complete, Toast.LENGTH_SHORT).show();
				HashMap<String, Object> res = (HashMap<String, Object>) msg.obj;
				login(res);
			}
			break;
		}
	return false;
	}
	
	public void load_api(int what) {
		new CallApiTask(what, ThirdLogin.this).execute();
	}

	@Override
	public JSONObject callApi(int what, Object... params) {
		Map<String, String> map = new HashMap<String, String>();
		switch (what) {
		case GET_NONCE_SNS:
			map.put("controller", "goods");
			map.put("method", "user_login_sns");
			return Api.get(Const.GET_NONCE, map);
		case USER_LOGIN_SNS:
			map.put("nonce", this.nonce);
			map.put("from", this.lFrom);
			map.put("openid",this.lUserID);
			map.put("params", this.lParams);
			return Api.get(Const.USER_LOGIN_SNS, map);
		default:
			return null;
		}
	}

	@Override
	public void handleResult(int what, JSONObject result, Object... params) {
		if( ! Util.checkResult(mContext, result)){
			Util.sendToast(mContext, "登录失败", Toast.LENGTH_SHORT);
			Util.hideLoading();
			return;
		}
		switch (what) {
			case GET_NONCE_SNS:
				nonce = (String) Api.getJSONValue(result, "nonce");
				load_api(USER_LOGIN_SNS);
				break;
			case USER_LOGIN_SNS:
				Util.hideLoading();
				// 缓存登录信息
				ZZZApplication.getInstance().saveLoginUser(result.toString());
				app = ZZZApplication.getInstance();
				if ( app != null&& !app.adapter.isEmpty() && app.addresses.size() > 1 ) {
	                mContext.finish();
	    		} else {
	    			
	    		}
				break;
			default:
				break;
		}
		
	}

}
