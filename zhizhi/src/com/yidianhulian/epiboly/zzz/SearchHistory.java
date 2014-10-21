package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchHistory extends Fragment {
	public static final String CONFIG_SEARCH_KEY = "search_history";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.search_history, null);
		ListView list = (ListView)v.findViewById(R.id.search_history_list);
		Button btn_clear_history = (Button)v.findViewById(R.id.btn_clear_history);
		
		String search_history = Util.getConfig(ZZZApplication.getInstance().getApplicationContext(), CONFIG_SEARCH_KEY);
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		if ( search_history != null && ! search_history.isEmpty() ) {
        	try {				
				JSONArray histories = new JSONArray(search_history);
				for (int i = histories.length() - 1; i >= 0; i--) {
					try {
						Map<String, String> item = new HashMap<String, String>();
						item.put("search_key", histories.get(i).toString());
						data.add(item);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	btn_clear_history.setVisibility(View.VISIBLE);
        } else {
        	btn_clear_history.setVisibility(View.GONE);
        }
        
        final SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_1,  
        		new String[] { "search_key" }, new int[] { android.R.id.text1 });
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String keyword = data.get(position).get("search_key");
				SearchActivity act = (SearchActivity)getActivity();
				act.searchItem.collapseActionView();
				act.getActionBar().setTitle(keyword);
				//act.getSearchView().setQuery(keyword, true);
				GoodsList goodsListFragment = (GoodsList)act.getSearchGoodsList(keyword);
				getFragmentManager().beginTransaction()
					.replace(R.id.search_frame, goodsListFragment)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("goodsList")
					.commit();
			}
		});
        
        btn_clear_history.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.clearConfig(ZZZApplication.getInstance().getApplicationContext(), CONFIG_SEARCH_KEY);
				data.clear();
				adapter.notifyDataSetChanged();
				v.setVisibility(View.GONE);
			}
		});
        
		return v;
	}
	
}
