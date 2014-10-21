package com.yidianhulian.epiboly.zzz;



import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

import android.R.string;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
 

public class About extends Fragment implements CallApiListener {
	private ImageButton upgrade_btn = null;
	private String version = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, null);
        upgrade_btn = (ImageButton) view.findViewById(R.id.upgrade_btn);
        upgrade_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.showLoading(getActivity(), "检查新版本。。。");
				// 获取本地版本号
				version = getLocalVersion(getActivity());
				// 检查新版本
				check_app_updates();
				
				
			}
		});
        return view;
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
 
    @Override
    public void onPause() {
        super.onPause();
    }
    
    public String getLocalVersion(Context context){
    	String localVersion = "";
    	try {
			localVersion = context.getPackageManager().getPackageInfo("com.yidianhulian.epiboly.zzz", 0).versionName;
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return localVersion.replace(".", "_");
    }
    
    public void check_app_updates(){
    	load_api(0);
    }
    
    public void load_api(int what) {
		new CallApiTask(what, About.this).execute();
	}

	@Override
	public JSONObject callApi(int what, Object... params) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("version", version);
		map.put("type", "2");
		return Api.get(Const.CHECK_DATA_UPDATES, map);
	}

	@Override
	public void handleResult(int what, JSONObject result, Object... params) {
		Util.hideLoading();
		if(! Util.checkResult(getActivity().getApplicationContext(), result)){
			return;
		}
		boolean is_update = (Boolean) Api.getJSONValue(result, "result");
		if(is_update){
			final String address = (String) Api.getJSONValue(result, "address");
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("软件升级").setMessage("发现新版本,建议立即更新使用.")
				.setPositiveButton("更新", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						String url = address;
//						String url = "http://gdown.baidu.com/data/wisegame/f98d235e39e29031/baiduxinwen.apk";
						UpdateManager updateManager = new UpdateManager(getActivity(), url);
						updateManager.showDownloadDialog();
//						dialog.dismiss();
//						Toast.makeText(getActivity(), "下载更新。（暂不支持）", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			alert.create().show();
		}else{
			Toast.makeText(getActivity(), "该版本为最新版本", Toast.LENGTH_SHORT).show();
		}
	}
    
    
}
