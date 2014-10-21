package com.yidianhulian.epiboly.zzz;

import com.yidianhulian.epiboly.ZZZApplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SampleListFragment extends ListFragment {
	 
	private TextView member_name = null;
	private SampleAdapter adapter = null;
	private SampleItem exitItem = null;
	
	private final static int GET_COUPONS = 2;
	private final static int GET_FAVORITES = 4;
	private final static int GET_SYSTEM_SETTING = 6;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.side_memu_list, null);
    	member_name = (TextView)view.findViewById(R.id.member_name);
    	if ( ! ZZZApplication.isLogin() ) {
    		setLoginClickListener();
    	}
    	return view;
    }
 
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         
        adapter = new SampleAdapter(getActivity());
        adapter.add(new SampleItem("优惠劵", R.drawable.coupon));
        adapter.add(new SampleItem("收藏夹", R.drawable.favorite));
        adapter.add(new SampleItem("系统设置", R.drawable.setting));
        adapter.add(new SampleItem("关于我们", R.drawable.about));
        if (ZZZApplication.isLogin()) {
        	member_name.setText(ZZZApplication.getInstance().getLoginUserItem("username"));
        	exitItem = new SampleItem("退出登录", R.drawable.logout);
        	adapter.add(exitItem);
        }
        
        setListAdapter(adapter);
    }
    
    
    
    @Override
    public void onResume() {  
        super.onResume();  
    }  
 
    private void setLoginClickListener() {
    	member_name.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), Signin.class);
				getActivity().startActivity(intent);
			}
		});
    }
    
    public class SampleAdapter extends ArrayAdapter<SampleItem> {
        public SampleAdapter(Context context) {
            super(context, 0);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.side_memu_row, null);
            }
            ImageView icon = (ImageView) convertView
                    .findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView
                    .findViewById(R.id.row_title);
            title.setText(getItem(position).tag);
            return convertView;
        }
    }
 
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fragment newContent = null;
//        switch (position) {
//	        case 0:
//	        	newContent = new CouponList(GET_COUPONS);
//	            break;
//	        case 1:
//	        	newContent = new CouponList(GET_FAVORITES);
//	            break;
//	        case 2:
//	        	newContent = new CouponList(GET_SYSTEM_SETTING);
//	            break;
//	        case 3:
//	        	newContent = new About();
//	            break;
//	        case 4:
//	        	ZZZApplication.getInstance().clearLoginUser();
//	        	member_name.setText(R.string.click_login);
//	        	setLoginClickListener();
//	        	adapter.remove(exitItem);
//	        	//MainActivity activity = (MainActivity)getActivity();
//	        	//activity.getSlidingMenu().showContent(false);
//	        	return;
//	            
//	    }
        if (newContent != null) {
            switchFragment(newContent);
        }
        super.onListItemClick(l, v, position, id);
    }
 
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null) {
            return;
        }
//        if (getActivity() instanceof MainActivity) {
//            MainActivity fca = (MainActivity) getActivity();
//            fca.switchContent(fragment);
//        }
    }
 
    private class SampleItem {
        public String tag;
        public int iconRes;
 
        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }
 
}
