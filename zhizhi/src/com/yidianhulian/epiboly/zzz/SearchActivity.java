package com.yidianhulian.epiboly.zzz;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class SearchActivity extends Activity  {	
	private Fragment mContent;
	public MenuItem searchItem;
	public SearchView searchView;
	
	@Override
	public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
          
        mContent = new SearchHistory(); 
        getFragmentManager().beginTransaction()
        	.replace(R.id.search_frame, mContent)
        	.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        	.addToBackStack("history")
        	.commit();

        ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("搜索");

    }
	
	public Fragment getSearchGoodsList(String search_key) {		   	
    	HashMap<String, String> search_params = new HashMap<String, String>();
    	String search_api = Const.GET_SEARCH_RESULTS;    	
    	search_params.put("s", search_key);   	
    	Util.showLoading(this, Util.DATA_LOADING);
    	
    	GoodsList goodsListFragment = new GoodsList();  	
    	goodsListFragment.setContentDatas(search_api, search_params, "");
    	
    	return goodsListFragment; 
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			if (getFragmentManager().getBackStackEntryCount() <= 1) {
				finish();
				return true;
			}
			this.getFragmentManager().popBackStack();
			searchItem.expandActionView();
			return true;
		}
		if(item.getItemId() == R.id.menu_search){
			if (getFragmentManager().getBackStackEntryCount() > 1) {
				this.getFragmentManager().popBackStack();
				searchItem.expandActionView();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.search_menu, menu);
		if (searchView == null) {
			searchItem = menu.findItem(R.id.menu_search);  
			searchView = (SearchView) searchItem.getActionView(); 
			searchItem.expandActionView();
//			searchView.setFocusable(false);
//	        Util.closeKeyboard(searchView, ZZZApplication.getInstance());
	        
		}
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				query = query.trim();
				if (query.isEmpty()) {
					Toast.makeText(SearchActivity.this, R.string.input_search_key_notice, Toast.LENGTH_SHORT).show();
					return true;
				}
				
				doSearch(query);
				getActionBar().setTitle(query);
				searchItem.collapseActionView();
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		return true;
	}

	public SearchView getSearchView() {
		return searchView;
	}
    
    private void doSearch(String search_key) {
    	if (search_key == null || search_key.isEmpty()) return;
    	
		String search_history = Util.getConfig(ZZZApplication.getInstance().getApplicationContext(), SearchHistory.CONFIG_SEARCH_KEY);
		JSONArray histories = new JSONArray();
		if ( search_history != null && ! search_history.isEmpty() ) {
			try {
				JSONArray tmp_histories = new JSONArray(search_history);
				for (int i = 0; i < tmp_histories.length(); i++) {
					if ( ! tmp_histories.optString(i).equalsIgnoreCase(search_key)) {
						histories.put(tmp_histories.optString(i));
					}
				}
				histories.put(search_key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				histories.put(0, search_key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Util.setConfig(ZZZApplication.getInstance().getApplicationContext(), SearchHistory.CONFIG_SEARCH_KEY, histories.toString());
		
		if (mContent == null || mContent instanceof SearchHistory) {
			mContent = getSearchGoodsList(search_key);
			getFragmentManager().beginTransaction()
				.replace(R.id.search_frame, mContent)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack("goodsList")
				.commit();
		} else if (mContent instanceof GoodsList){
			getSearchGoodsList(search_key);
		}
		if (searchView != null) {
			searchView.clearFocus();
	        Util.closeKeyboard(searchView, ZZZApplication.getInstance());
		}
    }
    
    
}
