package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.framework.AsyncImageLoader;
import com.yidianhulian.framework.AsyncImageLoader.ImageCallback;

public class ListRowAdapter extends ArrayAdapter<ListRow> {
	private AsyncImageLoader asyncImageLoader;
	private PullToRefreshListView listView;
	private int rowlayout;

	public ListRowAdapter(Activity activity, List<ListRow> listRow,
			PullToRefreshListView listView, int rowlayout) {
		super(activity, 0, listRow);
		this.listView = listView;
		this.rowlayout = rowlayout;
		asyncImageLoader = AsyncImageLoader.getInstance(activity);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Activity activity = (Activity) getContext();

		// Inflate the views from XML
		View rowView = convertView;
		ListRowCache viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(rowlayout, null);
			viewCache = new ListRowCache(rowView);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ListRowCache) rowView.getTag();
		}
		ListRow imageAndText = getItem(position);

		// Load the image and set it on the ImageView
		final ImageView imageView = viewCache.getWebView();
		imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.placeholder));
		
		asyncImageLoader.loadDrawable(imageAndText.getWebView(), new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {				
				Drawable d = imageDrawable != null ? imageDrawable : activity.getResources().getDrawable(R.drawable.placeholder_error);
				imageView.setImageDrawable(d);				
			}
		});
		
		String good_title = imageAndText.getGood_desc();
		if (good_title != null) {
			TextView good_desc = viewCache.getGood_desc();
			ArrayList positions = Util.filterTitleSpan(good_title);
			SpannableStringBuilder span_builder = Util.getSpanTextStyle(activity, good_title, positions);
			if (span_builder == null) {
				good_desc.setText(good_title);
			} else {
				good_desc.setText(span_builder);
			}
		}
			
		if (imageAndText.getVendor_name() != null) {
			TextView vendor_name = viewCache.getVendor_name();
			vendor_name.setText(imageAndText.getVendor_name());
		}
		
		if (imageAndText.getTime_interval() != null) {
			TextView time_interval = viewCache.getTime_interval();
			time_interval.setText(imageAndText.getTime_interval());
		}

		if (imageAndText.getCollect_qty() != null) {
			TextView collect_qty = viewCache.getCollect_qty();
			collect_qty.setText(imageAndText.getCollect_qty());
		}

		if (imageAndText.getComment_qty() != null) {
			TextView comment_qty = viewCache.getComment_qty();
			comment_qty.setText(imageAndText.getComment_qty());
		}
		
		if (imageAndText.getLike_qty() != null) {
			TextView like_qty = viewCache.getLike_qty();
			like_qty.setText(imageAndText.getLike_qty());
		}

		return rowView;

	}

}
