package com.yidianhulian.epiboly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.tencent.android.tpush.service.s;
import com.yidianhulian.epiboly.zzz.R;
import com.yidianhulian.epiboly.zzz.Signin;
import com.yidianhulian.framework.Api;

import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util {

	private static HashMap<String, String> loginUser = null;
	/**
	 * 提示对话框
	 */
	public static ProgressDialog loading;
	/**
	 * 数据加载中
	 */
	public static final int DATA_LOADING = 1;
	/**
	 * 数据处理中
	 */
	public static final int DATA_PROCESSING = 2;

	public static boolean checkResult(Context context, JSONObject result) {
		if (result == null) {
			return false;
		}
		String status = (String) Api.getJSONValue(result, "status");
		if ("ok".equals(status)) {
			return true;
		} else if ("error".equals(status)) {
			sendToast(context, Api.getStringValue(result, "error").toString(), Toast.LENGTH_SHORT);
//			Toast.makeText(context, Api.getStringValue(result, "error"),
//					Toast.LENGTH_LONG).show();
			return false;
		} else {
			return false;
		}
		// if(result == null && !"ok".equals(Api.getJSONValue(result,
		// "status"))){
		// Toast.makeText(context, Api.getStringValue(result, "msg"),
		// Toast.LENGTH_LONG).show();
		// return false;
		// }
		// return true;
	}

	public static String getTimeIntervalStr(String scdate) {
		Date date = new Date();
		int now = (int) (date.getTime() / 1000);
		int after = 0;

		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(scdate));
			after = (int) (c.getTimeInMillis() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int diff = now - after;
		String desc;
		if (diff < 60) {
			desc = "刚才";
		} else if (diff < 3600) {
			desc = (int) (diff / 60) + "分钟前";
		} else if (diff < 86400) {
			desc = (int) (diff / 3600) + "小时前";
		} else {
			desc = (int) (diff / 86400) + "天前";
		}
		return desc;
	}

	public static void setConfig(Context context, String key, String value) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(
				Const.PREF_FILE_NAME, 0);
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);

		editor.commit();
	}

	public static String getConfig(Context context, String key) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(
				Const.PREF_FILE_NAME, 0);

		return mSharedPreferences.getString(key, "");
	}

	public static void clearConfig(Context context, String key) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(
				Const.PREF_FILE_NAME, 0);
		SharedPreferences.Editor editor = mSharedPreferences.edit();

		editor.remove(key).commit();
	}

	public static void saveLoginUser(Context context, String json_str) {
		setConfig(context, "login_user", json_str);
	}

	public static void clearLoginUser(Context context) {
		loginUser = null;
		clearConfig(context, "login_user");
	}

	public static String getLoginUserItem(Context context, String key) {
		if (loginUser == null)
			loginUser = getLoginUser(context);

		if (loginUser != null && loginUser.containsKey(key)) {
			return loginUser.get(key);
		}

		return "";
	}

	public static HashMap<String, String> getLoginUser(Context context) {
		String json_str = getConfig(context, "login_user");
		if (!json_str.isEmpty()) {
			try {
				loginUser = new HashMap<String, String>();
				JSONObject json = new JSONObject(json_str);
				loginUser.put("cookie", Api.getJSONValue(json, "cookie")
						.toString());
				JSONObject user = (JSONObject) Api.getJSONValue(json, "user");
				Iterator it = user.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = Api.getJSONValue(user, key).toString();
					loginUser.put(key, value);
				}
			} catch (Exception e) {
				Log.d("get-api-exception", json_str);
				e.printStackTrace();
			}
		}

		return loginUser;
	}

	/**
	 * 加载提示对话框
	 * 
	 * @param context
	 * @param title
	 */
	public static void showLoading(Activity context, String desc) {
		if (loading != null) {
			loading.dismiss();
		}
		if (context == null) return;
		
		loading = new ProgressDialog(context);
	    loading.setCancelable(false);	// 返回按钮无响应
//	    loading.setCanceledOnTouchOutside(false);	// 返回按钮有响应
		loading.setMessage(desc);
		loading.show();
	}

	/**
	 * 加载提示对话框
	 * 
	 * @param context
	 * @param type
	 */
	public static void showLoading(Activity context, int type) {
		String desc = "";
		switch (type) {
		case DATA_LOADING:
			desc = "数据加载中,请稍等 …";
			break;
		case DATA_PROCESSING:
			desc = "数据处理中,请稍等 …";
			break;
		default:
			desc = "数据加载中,请稍等 …";
		}
		showLoading(context, desc);
	}

	/**
	 * 隐藏提示对话框
	 */
	public static void hideLoading() {
		if (loading != null && loading.isShowing()) {
			loading.dismiss();
		}
	}

	/**
	 * 过滤商品的span标签，并记住每对标签的内容的起始位置，这个起始位置是去掉标签后的位置
	 * 
	 * @param title
	 */
	public static ArrayList filterTitleSpan(String title) {
		int pos1 = title.indexOf("<span>");
		int pos2 = title.indexOf("</span>");
		ArrayList positions = new ArrayList();
		if (pos1 != -1 && pos2 != -1) {
			positions.add(pos1);
			positions.add(pos2 - 6);
		}

		int len = 13;
		int pos_s = pos2;
		while (true) {
			int pos3 = title.indexOf("<span>", pos_s);
			if (pos3 == -1)
				break;
			int pos4 = title.indexOf("</span>", pos3);
			if (pos4 == -1)
				break;

			positions.add(pos3 - len);
			positions.add(pos4 - len - 6);

			len += 13;

			pos_s = pos4;
		}

		return positions;
	}

	public static SpannableStringBuilder getSpanTextStyle(Context context,
			String title, ArrayList positions) {
		if (positions == null || positions.size() == 0)
			return null;

		title = title.replaceAll("<span>", "");
		title = title.replaceAll("</span>", "");
		SpannableStringBuilder style = new SpannableStringBuilder(title);
		for (int i = 0; i < positions.size(); i = i + 2) {
			int start = Integer.parseInt(positions.get(i).toString());
			int end = Integer.parseInt(positions.get(i + 1).toString());
			style.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.span_color)), start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		return style;
	}
	
	/**
	 * 隐藏软键盘
	 * @param view getWindow().peekDecorView();
	 * @param application
	 */
	public static void closeKeyboard(View view,Application application){
        if (view != null) {
        	InputMethodManager inputmanger = (InputMethodManager) application.getSystemService(application.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}
	
	/**
	 * 自定义Toast
	 * @param context
	 * @param msg
	 * @param duration
	 */
	public static void sendToast(Context context, String msg, int duration ){
		Toast toast = Toast.makeText(context, msg, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
