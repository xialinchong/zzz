package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GoodsList extends Fragment implements CallApiListener {	
	
    private PullToRefreshListView listView;
    private ListRowAdapter adapter;
    List<ListRow> listRowArr = new ArrayList<ListRow>();
    
    private String search_api = "";
    private Map<String, String> search_params = new HashMap<String, String>();
    private String slug = "";
    private Map<String, Integer> search_pagenos = new HashMap<String, Integer>();
    private Map<String, List<ListRow>> tabDatas = new HashMap<String, List<ListRow>>();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
    	
        View view = inflater.inflate(R.layout.goods_list, null);  
        listView = (PullToRefreshListView) view.findViewById(R.id.goods_list_view);
        listView.setMode(Mode.BOTH);

        adapter = new ListRowAdapter(this.getActivity(), 
        		listRowArr, listView, R.layout.goods_list_row);
        //实现列表的显示  
        listView.setAdapter(adapter);  
        //添加刷新       
        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (search_pagenos.containsKey(slug)) {
					search_pagenos.remove(slug);
				}				
				if (tabDatas.containsKey(slug) && tabDatas.get(slug).size() > 0) {					
					tabDatas.get(slug).clear();					
				}
				search_params.put("pageno", "0");
				setContentDatas(search_api, search_params, slug);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				Integer pageno = 0;
				if (search_pagenos.containsKey(slug)) {
					pageno = search_pagenos.get(slug);
					pageno = pageno + 1;
				} else {
					pageno = 2;
				}
				search_pagenos.put(slug, pageno);
				search_params.put("pageno", pageno.toString());
				
				setContentDatas(search_api, search_params, slug);
			}
        	
		});      
        
        //添加点击  
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub dy_edit
				Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
				intent.putExtra("id", listRowArr.get((int)arg3).getId());
	        	getActivity().startActivity(intent);
			}
		});
        
        
        return view;
    }
    
    @Override  
    public void onResume() { 
    	
        super.onResume(); 
    }  
    
    /**
     * 
     * @param search_api api接口地址
     * @param search_params 查询的参数
     * @param slug 以后特殊情况时扩展用,栏目的标签,可用于判断当前栏目
     */
    public void setContentDatas (String search_api, Map<String, String> search_params, String slug) {
    	this.search_api 	= search_api;
    	this.search_params 	= search_params;
    	this.slug 			= slug;
    	if (this.search_params != null && ! this.search_params.containsKey("per_page")) {
    		this.search_params.put("per_page", "20");
    	}
    	if ( ! tabDatas.containsKey(slug) || tabDatas.get(slug).size() == 0 || (search_params.containsKey("pageno"))) {   			
        	CallApiTask.doCallApi(0, GoodsList.this);
    	} else { 		    	
    		exchangeDatas();
    		adapter.notifyDataSetChanged();
    		Util.hideLoading();
    	}
    }

	@Override
	public JSONObject callApi(int what, Object...params) {
		return Api.get(search_api, search_params);
	}

	@Override
	public void handleResult(int what, JSONObject result, Object...params) {
		if (result == null) {
			Util.hideLoading();
			listView.onRefreshComplete();
			return;
		}
		try {
			JSONArray goods = (JSONArray)Api.getJSONValue(result, "goods");
			if (goods == null) {
				Util.hideLoading();
				return;
			}
			
			exchangeDatas();
			
			for (int i = 0; i < goods.length(); i++) {
				JSONObject jsonObj = (JSONObject)goods.get(i);
				
				String good_desc = Api.getJSONValue(jsonObj, "title").toString();				
				String vendor_name = "";
				JSONArray jarr =  (JSONArray)Api.getJSONValue(jsonObj, "shop");
				for (int j = 0; j < jarr.length(); j++) {
					try {
						vendor_name = vendor_name + " " + jarr.get(j);
					} catch (JSONException e) {
						
					}
				}
				String id = Api.getJSONValue(jsonObj, "id").toString();
				String scdate = Api.getJSONValue(jsonObj, "date").toString();
				String time_interval = Util.getTimeIntervalStr(scdate);
				String collect_qty = "收藏：" +Api.getJSONValue(jsonObj, "favor_count").toString();
				String comment_qty = "评论：" + Api.getJSONValue(jsonObj, "comment_count").toString();
				String like_qty = "值否：" + Api.getJSONValue(jsonObj, "rating_hots").toString();
				String webView = Api.getJSONValue(jsonObj, "thumbnail").toString();
	        	ListRow listRow = new ListRow(id, good_desc, vendor_name, 
	        			time_interval, collect_qty, comment_qty, like_qty, webView);
	        	listRowArr.add(listRow);
			}
			
			storeDatas(listRowArr);
			
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		listView.onRefreshComplete();
		
		Util.hideLoading();
	}
	
	private void exchangeDatas() {
		listRowArr.clear();
		if (tabDatas.containsKey(slug)) {
			List<ListRow> row = tabDatas.get(slug);
			for (int i = 0; i < row.size(); i++) {
				ListRow old = row.get(i);
				ListRow newData = new ListRow(old.getId(), old.getGood_desc(), old.getVendor_name(), old.getTime_interval(),
						old.getCollect_qty(), old.getComment_qty(), old.getLike_qty(), old.getWebView());
				listRowArr.add(newData);
			}
		}
	}
	
	private void storeDatas(List<ListRow> listRowArr) {
		List<ListRow> newList = new ArrayList<ListRow>();
		for (int i = 0; i < listRowArr.size(); i++) {
			ListRow old = listRowArr.get(i);
			ListRow newRow = new ListRow(old.getId(), old.getGood_desc(), old.getVendor_name(), old.getTime_interval(),
					old.getCollect_qty(), old.getComment_qty(), old.getLike_qty(), old.getWebView());
			
			newList.add(newRow);
		}
		tabDatas.put(slug, newList);
	}
}