package com.yidianhulian.epiboly.zzz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListRowCache {

	private View baseView;
	private TextView good_desc;
	private TextView vendor_name;
	private TextView time_interval;
	private TextView collect_qty;
	private TextView comment_qty;
	private TextView like_qty;
	private ImageView webView;
	
	public ListRowCache (View baseView) {
		this.baseView = baseView;
	}
	
	public TextView getGood_desc() {
		if (good_desc == null) {
			good_desc = (TextView) baseView.findViewById(R.id.good_desc);
		}
		return good_desc;
	}
	public TextView getVendor_name() {
		if (vendor_name == null) {
			vendor_name = (TextView) baseView.findViewById(R.id.vendor_name);
		}
		return vendor_name;
	}
	public TextView getTime_interval() {
		if (time_interval == null) {
			time_interval = (TextView) baseView.findViewById(R.id.time_interval);
		}
		return time_interval;
	}
	public TextView getCollect_qty() {
		if (collect_qty == null) {
			collect_qty = (TextView) baseView.findViewById(R.id.collect_qty);
		}
		return collect_qty;
	}
	public TextView getComment_qty() {
		if (comment_qty == null) {
			comment_qty = (TextView) baseView.findViewById(R.id.comment_qty);
		}
		return comment_qty;
	}
	public TextView getLike_qty() {
		if (like_qty == null) {
			like_qty = (TextView) baseView.findViewById(R.id.like_qty);
		}
		return like_qty;
	}
	public ImageView getWebView() {
		if (webView == null) {
			webView = (ImageView) baseView.findViewById(R.id.good_img);
		}
		return webView;
	}
	
}
