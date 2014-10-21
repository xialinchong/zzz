package com.yidianhulian.epiboly.zzz;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Register extends Activity implements CallApiListener,OnClickListener {
	
	private static final int GET_REGISTER_NONCE = 1;
	private static final int REGISTER = 2;
	private static final int GET_SIGNIN_NONCE = 3;
	private static final int SIGNIN = 4;
	
	private ZZZApplication app = null;

	private String nonce = null;
	private EditText username = null; // 登录名
	private EditText password = null; // 密码
	private EditText nickname = null; // 昵称
	private EditText email = null; // 邮箱

	private ImageButton registerButton;
	private Button go_login = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.psw);
		email = (EditText) findViewById(R.id.email);
		nickname = (EditText) findViewById(R.id.display_name);

		registerButton = (ImageButton) findViewById(R.id.regist);
		registerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭软键盘
				Util.closeKeyboard(getWindow().peekDecorView(), getApplication());
				
				if(username.getText().toString().isEmpty()){
					Util.sendToast(getApplicationContext(), "用户名不能为空！", Toast.LENGTH_SHORT);
				}else if(email.getText().toString().isEmpty()){
					Util.sendToast(getApplicationContext(), "邮箱不能为空！", Toast.LENGTH_SHORT);
				}else if(password.getText().toString().isEmpty()){
					Util.sendToast(getApplicationContext(), "密码不能为空！", Toast.LENGTH_SHORT);
				}else if(nickname.getText().toString().isEmpty()){
					Util.sendToast(getApplicationContext(), "昵称不能为空！", Toast.LENGTH_SHORT);
				}else{
					Util.showLoading(Register.this, Util.DATA_PROCESSING);
					load_api(GET_REGISTER_NONCE);
				}
			}
		});

		go_login = (Button) findViewById(R.id.go_login);
		go_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Register.this, Signin.class);
				Register.this.startActivity(intent);
				Register.this.finish();
			}
		});
		
		ShareSDK.initSDK(this);
		findViewById(R.id.sina).setOnClickListener(this);
		findViewById(R.id.qq).setOnClickListener(this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public void load_api(int what) {
		new CallApiTask(what, Register.this).execute();
	}

	@Override
	public JSONObject callApi(int what, Object... params) {
		Map<String, String> map = new HashMap<String, String>();
		
		switch (what) {
			case GET_REGISTER_NONCE:
				map.put("controller", "goods");
				map.put("method", "user_register");
				return Api.get(Const.GET_NONCE, map);
			case REGISTER:
				map.put("nonce", nonce);
				map.put("username", username.getText().toString());
				map.put("password", password.getText().toString());
				map.put("email", email.getText().toString());
				map.put("display_name", nickname.getText().toString());
				return Api.get(Const.USER_REGISTER, map);
			case GET_SIGNIN_NONCE:
				map.put("controller", "goods");
				map.put("method", "user_login");
				return Api.get(Const.GET_NONCE, map);
			case SIGNIN:
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
			case GET_REGISTER_NONCE:
				nonce = (String) Api.getJSONValue(result, "nonce");
				load_api(REGISTER);
				break;
			case REGISTER:
				Util.hideLoading();
				// 成功好自动登录
				Util.showLoading(Register.this, "自动登录...");
				load_api(GET_SIGNIN_NONCE);
				break;
			case GET_SIGNIN_NONCE:
				nonce = (String) Api.getJSONValue(result, "nonce");
				load_api(SIGNIN);
				break;
			case SIGNIN:
				Util.hideLoading();
				// 缓存登录信息
				ZZZApplication.getInstance().saveLoginUser(result.toString());				
				// 跳回主页面
				app = ZZZApplication.getInstance();
				if ( app != null&& !app.adapter.isEmpty() && app.addresses.size() > 1 ) {
	                Register.this.finish();  
	    		} else {
	    			
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

	@Override
	public void onClick(View v) {
		ThirdLogin thirdLogin = null;
		switch (v.getId()) {
			case R.id.sina:
				thirdLogin = new ThirdLogin(Register.this, "sina");
				thirdLogin.startThirdLogin();
				break;
			case R.id.qq:
				thirdLogin = new ThirdLogin(Register.this, "qq");
				thirdLogin.startThirdLogin();
				break;
			default:
				break;
		}
		Util.showLoading(Register.this, "第三方登录中...");
	}

}
