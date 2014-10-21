package com.yidianhulian.epiboly.zzz;

import java.util.HashMap;
import java.util.Map;


import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

public class Signin extends Activity implements CallApiListener,OnClickListener {

	private static final int GET_NONCE = 1;
	private static final int GET_LOGIN = 2;

	private String nonce = null;
	private EditText username = null;
	private EditText password = null;
	private ImageButton uSignin = null;

	private TextView go_regist = null;
	private TextView go_edit_pwd = null;
	private ZZZApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin);

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.psw);

		go_regist = (TextView) findViewById(R.id.go_regist);
		go_regist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Signin.this, Register.class);
				Signin.this.startActivity(intent);
				Signin.this.finish();
			}
		});
		
		go_edit_pwd = (TextView)findViewById(R.id.forgetpsw);
		go_edit_pwd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Signin.this, EditPsw.class);
				Signin.this.startActivity(intent);
				Signin.this.finish();
			}
		});

		uSignin = (ImageButton) findViewById(R.id.login);
		uSignin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭软键盘
				Util.closeKeyboard(getWindow().peekDecorView(), getApplication());
				
				if(username.getText().toString().isEmpty()){
					Util.sendToast(getApplicationContext(), "用户名不能为空！", Toast.LENGTH_SHORT);
				}else if(password.getText().toString().isEmpty()){
					Util.sendToast(getApplicationContext(), "密码不能为空！", Toast.LENGTH_SHORT);
				}else{
					Util.showLoading(Signin.this, "登录中...");
					load_api(GET_NONCE);
				}
			}
		});
		// 返回按钮
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		ShareSDK.initSDK(this);
		findViewById(R.id.sina).setOnClickListener(this);
		findViewById(R.id.qq).setOnClickListener(this);
	}

	public void load_api(int what) {
		new CallApiTask(what, Signin.this).execute();
	}

	@Override
	public JSONObject callApi(int what, Object... params) {
		Map<String, String> map = new HashMap<String, String>();
		switch (what) {
		case GET_NONCE:
			map.put("controller", "goods");
			map.put("method", "user_login");
			return Api.get(Const.GET_NONCE, map);
		case GET_LOGIN:
			map.put("nonce", nonce);
			map.put("username", username.getText().toString());
			map.put("password", password.getText().toString());
			return Api.get(Const.USER_LOGIN, map);
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
			load_api(GET_LOGIN);
			break;
		case GET_LOGIN:
			Util.hideLoading();
			// 缓存登录信息
			ZZZApplication.getInstance().saveLoginUser(result.toString());				
			// 跳回主页面
			app = ZZZApplication.getInstance();
			if ( app != null&& !app.adapter.isEmpty() && app.addresses.size() > 1 ) {
                Signin.this.finish();  
    		} else {
    			
    		}
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

	@Override
	public void onClick(View v) {
		ThirdLogin thirdLogin = null;
		switch (v.getId()) {
			case R.id.sina:
				thirdLogin = new ThirdLogin(Signin.this, "sina");
				thirdLogin.startThirdLogin();
				break;
			case R.id.qq:
				thirdLogin = new ThirdLogin(Signin.this, "qq");
				thirdLogin.startThirdLogin();
				break;
	
			default:
				break;
		}
		Util.showLoading(Signin.this, "第三方登录中...");
		
	}

}
