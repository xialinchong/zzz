package com.yidianhulian.epiboly.zzz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.R.string;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

public class EditPsw extends Activity implements CallApiListener {

	private static final int GET_NONCE = 1;
	private static final int GET_PWD = 2;

	private String nonce = null;
	private EditText edit_key = null;
	private ImageButton get_pwd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_psw);

		edit_key = (EditText) findViewById(R.id.edit_key);
		get_pwd = (ImageButton) findViewById(R.id.get_new_psw);

		get_pwd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// 关闭软键盘
				Util.closeKeyboard(getWindow().peekDecorView(), getApplication());
				
				String getKey = edit_key.getText().toString();
				if (getKey.isEmpty()) {
					Util.sendToast(getApplicationContext(), "用户名或电子邮箱不能为空！", Toast.LENGTH_SHORT);
				} else {
					Util.showLoading(EditPsw.this, "邮件发送中...");
					load_api(GET_NONCE);
				}

			}
		});
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	public void load_api(int what) {
		new CallApiTask(what, EditPsw.this).execute();
	}

	@Override
	public JSONObject callApi(int what, Object... params) {
		Map<String, String> map = new HashMap<String, String>();
		switch (what) {
		case GET_NONCE:
			map.put("controller", "goods");
			map.put("method", "user_lost_password");
			return Api.get(Const.GET_NONCE, map);
		case GET_PWD:
			map.put("nonce", nonce);
			map.put("username", edit_key.getText().toString());
			return Api.get(Const.USER_LOST_PASSWORD, map);
		default:
			return null;
		}

	}

	@Override
	public void handleResult(int what, JSONObject result, Object... params) {
		if( ! Util.checkResult(this, result)){
			Util.hideLoading();
			return;
		}
		switch (what) {
		case GET_NONCE:
			nonce = (String) Api.getJSONValue(result, "nonce");
			load_api(GET_PWD);
			break;
		case GET_PWD:
			Util.hideLoading();
			if((Boolean) Api.getJSONValue(result, "sent")){
				Util.sendToast(getApplicationContext(), "密码已发送至邮箱，请注意查收。", Toast.LENGTH_SHORT);
			}
			break;

		default:
			break;
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
