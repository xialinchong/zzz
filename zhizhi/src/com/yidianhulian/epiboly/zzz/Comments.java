package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;
 

public class Comments extends Fragment implements CallApiListener {
	private PullToRefreshListView listView;
	private String good_id = null;
	
	private static final int GET_COMMENTS = 1;
	private static final int SEND_COMMENT = 2;
	private static final int REFRESH_COMMENT = 3;
	private ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	private TextView ctrl_comment_total;
	private EditText ctrl_input_content;
	private Button btn_send;
	
	private Map<String, String> search_params = new HashMap<String, String>();
	private String slug = "";
	private Map<String, Integer> search_pagenos = new HashMap<String, Integer>();
	private String search_api = "";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.comments, null);
        
        listView = (PullToRefreshListView) v.findViewById(R.id.comment_list);
        ctrl_comment_total = (TextView) v.findViewById(R.id.comment_total);
        ctrl_input_content = (EditText) v.findViewById(R.id.input_content);
        btn_send = (Button) v.findViewById(R.id.btn_send);
        
        GoodsDetailsActivity activity = (GoodsDetailsActivity)getActivity();
        good_id = activity.getGoodId();
        
        Util.showLoading(activity, Util.DATA_LOADING);
        CallApiTask.doCallApi(GET_COMMENTS, Comments.this);

        //创建SimpleAdapter适配器将数据绑定到item显示控件上  
        adapter = new SimpleAdapter(this.getActivity(), datas, R.layout.comment_list_row,   
                new String[]{"commenter", "comment_content", "comment_time"}, new int[]{R.id.commenter, 
    		   R.id.comment_content,R.id.comment_date});  
        listView.setMode(Mode.PULL_FROM_END);
        //实现列表的显示  
        listView.setAdapter(adapter);  
        //刷新
        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				Integer pageno = 0;
				if (search_pagenos.containsKey(slug)) {
					pageno = search_pagenos.get(slug);
					pageno = pageno + 1;
				} else {
					pageno = 2;
				}
				search_pagenos.put(slug, pageno);
				search_params.put("pageno", pageno.toString());
				CallApiTask.doCallApi(REFRESH_COMMENT, Comments.this);
				
			}
        	
		});
        
        
        btn_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if ( ! ZZZApplication.isLogin() ) {
//					Toast.makeText(getActivity(), R.string.input_comment_validate_login, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(getActivity(), Signin.class);
					getActivity().startActivity(intent);
					return;
				}
				if (ctrl_input_content.getText().toString().trim().isEmpty()) {
					Toast.makeText(getActivity(), R.string.input_comment, Toast.LENGTH_SHORT).show();
					return;
				}
				
				Util.showLoading(getActivity(), Util.DATA_PROCESSING);
				CallApiTask.doCallApi(SEND_COMMENT, Comments.this);
			}
		});
        
        return v;
    }
 
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
 
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == android.R.id.home){
			this.getFragmentManager().popBackStack();;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
    public void onPause() {
        super.onPause();
    }

	@Override
	public JSONObject callApi(int what, Object... params) {
		HashMap<String,String> search_params = new HashMap<String,String>();
		search_params.put("gid", good_id);
		if (what == GET_COMMENTS) {
			return Api.get(Const.GET_COMMENTS, search_params);
		} else if (what == SEND_COMMENT) {
			search_params.put("cookie", Util.getLoginUserItem(getActivity().getApplicationContext() , "cookie"));
			search_params.put("content", ctrl_input_content.getText().toString());
			
			return Api.get(Const.SUBMIT_COMMENT, search_params);
		} else if(what == REFRESH_COMMENT){
			this.search_params.put("gid", good_id);
			return Api.get(Const.GET_COMMENTS, this.search_params);
		}
		return null;
	}

	@Override
	public void handleResult(int what, JSONObject result, Object... params) {
		Util.hideLoading();
		if (what == GET_COMMENTS) {
			if (result == null || ! "ok".equals(Api.getJSONValue(result, "status"))) 
				return;	
			try {
				datas.clear();
				String total = Api.getJSONValue(result, "total").toString();
				if (total.isEmpty()) total = "0";
				ctrl_comment_total.setText(total + "条评论");
				
				JSONArray comments = (JSONArray)Api.getJSONValue(result, "comments");
				
				for (int i = 0; i < comments.length(); i++) {
					JSONObject jsonObj = (JSONObject)comments.get(i);
					
					String author = Api.getJSONValue(jsonObj, "author").toString();
					String comment_content = Api.getJSONValue(jsonObj, "content").toString();
					String comment_time = Api.getJSONValue(jsonObj, "comment_time").toString();
		        	HashMap<String, String> row = new HashMap<String, String>();
		        	row.put("commenter", "  " + author);
		        	row.put("comment_time", comment_time);
		        	row.put("comment_content", comment_content);
		        	
					datas.add(row);
				}
				
				adapter.notifyDataSetChanged();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (what == SEND_COMMENT) {
			if (result == null || ! "ok".equals(Api.getJSONValue(result, "status"))) {
				Toast.makeText(getActivity(), R.string.operation_fail_notice, Toast.LENGTH_SHORT).show();
			} else {
				ctrl_input_content.setText("");
				Toast.makeText(getActivity(), R.string.operation_success_notice, Toast.LENGTH_SHORT).show();
				CallApiTask.doCallApi(GET_COMMENTS, Comments.this);
			}
			
		} else if (what == REFRESH_COMMENT){ // 刷新
			try {
				if (result == null) {
					listView.onRefreshComplete();
					return;
				}
				
				JSONArray comments = (JSONArray)Api.getJSONValue(result, "comments");
				if(comments.length() > 0){
					for (int i = 0; i < comments.length(); i++) {
						JSONObject jsonObj = (JSONObject)comments.get(i);
						
						String author = Api.getJSONValue(jsonObj, "author").toString();
						String comment_content = Api.getJSONValue(jsonObj, "content").toString();
						String comment_time = Api.getJSONValue(jsonObj, "comment_time").toString();
			        	HashMap<String, String> row = new HashMap<String, String>();
			        	row.put("commenter", "  " + author);
			        	row.put("comment_time", comment_time);
			        	row.put("comment_content", comment_content);
						datas.add(row);
					}
				}else{
					listView.onRefreshComplete();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			listView.onRefreshComplete();
		}
	}
	
}
