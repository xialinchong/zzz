package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabWidget;

public class GoodsListNav extends Fragment {	
    private PagerAdapter mPagerAdapter;
    private TabWidget mTabWidget;
    private ArrayList<HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();
    private String catid = "0";
//    ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    private Button[] mBtnTabs;
    private int default_tab = 0; //默认打开的栏目
    private int current_tab = 0; //当前打开的栏目
    private GoodsList goodsListFragment = null;

    public GoodsListNav(){
    }
    
    public GoodsListNav(ArrayList<HashMap<String, String>> addr, String catid){
    	addresses = addr;
    	this.catid = catid;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        
        View v = inflater.inflate(R.layout.goods_list_nav, null);
        mTabWidget = (TabWidget) v.findViewById(R.id.tab_widget);
        mTabWidget.setStripEnabled(false);
        mBtnTabs = new Button[addresses.size()];
        
        Activity activity = getActivity();
        for (int i=0; i < addresses.size(); i++) {
        	HashMap<String, String> one_tab = addresses.get(i);
        	if (isDefaultCouponTab(one_tab)) default_tab = i;
        	
        	mBtnTabs[i] = new Button(activity);
            mBtnTabs[i].setText(one_tab.get("cat_name"));
            if ( i == 0 ) {
            	mBtnTabs[i].setSelected(true);
            	mBtnTabs[i].setTextColor( getResources().getColor(R.color.red) );
			} else {
				mBtnTabs[i].setSelected(false);
            	mBtnTabs[i].setTextColor( getResources().getColor(R.color.black) );
			}
            if ( i+1 == addresses.size() ) {
				mBtnTabs[i].setBackgroundResource(R.drawable.bar_menu_end_bgs);
			} else {
				mBtnTabs[i].setBackgroundResource(R.drawable.bar_menu_bgs);
			}
            
            //mBtnTabs[i].setTextColor(getResources().getColorStateList(R.color.button_bg_color_selector));
            mTabWidget.addView(mBtnTabs[i]);
            mBtnTabs[i].setOnClickListener(mTabClickListener);
        }

        getFragmentManager().beginTransaction().add(R.id.tabcontent, getGoodsList(default_tab)).commit();
        
        mTabWidget.setCurrentTab(default_tab);
        current_tab = default_tab;
        ((MainActivity)getActivity()).setCurrentTab(current_tab);
        
        return v;
    }
    
    public void setCurrentCategory(String catid) {
    	this.catid = catid;
    }  
    
    public void updateCouponGoodsList() {
    	if (current_tab == default_tab) {
    		getGoodsList(default_tab);  
    	} else {
    		mBtnTabs[default_tab].performClick();
    	}
    }
    
    private OnClickListener mTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
        	int redColor   = getResources().getColor(R.color.red);
        	int blackColor = getResources().getColor(R.color.black);
        	for (int i = 0; i < mBtnTabs.length; i++) {
				if (v == mBtnTabs[i]) {
					mBtnTabs[i].setTextColor(redColor);	
					mTabWidget.setCurrentTab(i);
					MainActivity activity = (MainActivity)getActivity();					
					current_tab = i;	
					activity.setCurrentTab(current_tab);
					activity.restoreActionBar();
					
					getGoodsList(i);
				} else {
					mBtnTabs[i].setTextColor(blackColor);
				}				
			}
        }
    };

    private Fragment getGoodsList(int position) {
    	HashMap<String, String> tab = addresses.get(position);
    	String search_api = "";
    	HashMap<String, String> search_params = new HashMap<String, String>();
    	String cat_id = "";
    	if (isDefaultCouponTab(tab)) {
    		search_api = Const.GET_RECENT_GOODS;
    		search_params.put("catid", catid);
    		cat_id = catid;
    	} else {
    		search_api = Const.GET_RECENT_GOODS_BY_CHANNEL;
    		search_params.put("chid", tab.get("site_cat_id"));
    	}
    	if (goodsListFragment == null) {
    		goodsListFragment = new GoodsList();
    	}

    	Util.showLoading(getActivity(), Util.DATA_LOADING);
    	goodsListFragment.setContentDatas(search_api, search_params, tab.get("slug") + cat_id);
    	
    	return goodsListFragment; 
    }
    
    private boolean isDefaultCouponTab(HashMap<String,String> tab) {
    	if (tab.get("slug").equals("p")) {
    		return true;
    	}
		return false;
    	
    }
}