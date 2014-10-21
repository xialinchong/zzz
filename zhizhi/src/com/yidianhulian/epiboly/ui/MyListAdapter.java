package com.yidianhulian.epiboly.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yidianhulian.epiboly.zzz.R;
import com.yidianhulian.framework.AsyncImageLoader;
import com.yidianhulian.framework.AsyncImageLoader.ImageCallback;
import com.yidianhulian.framework.ImageUtils;

/**
 * 该listAdapter主要封装有图片异步加载的处理，其它与BaseAdapter无异
 * 
 * @author leeboo
 *
 */
public class MyListAdapter extends BaseAdapter {
	protected Activity context = null;
	protected List<Map<String, String>> list = null;
	protected List<JSONObject> rawJson = new ArrayList<JSONObject>();
	protected AsyncImageLoader asyncImgloader;
	protected int itemLayoutId;
	protected String[] from;
	protected int[] to;

	public MyListAdapter(Activity context, List<Map<String, String>> list, List<JSONObject> rawJson, int layoutId,  String[] from, int[] to) {
		this.context = context;
		this.list = list;
		this.rawJson = rawJson;
		asyncImgloader =  AsyncImageLoader.getInstance((Activity)context);
		itemLayoutId = layoutId;
		this.from = from;
		this.to = to;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		
		//  初始化view
		if(view == null){
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(itemLayoutId, null);
			
			for (int i = 0; i < from.length; i++) {
				View v = view.findViewById(to[i]);
				if(v!=null) holder.putView(to[i], v);
			}
			
			view.setTag(holder);
		} else {
            holder = (ViewHolder) view.getTag();
        }
		
		if (list == null || list.size() == 0) {//没有数据
			return view;
		}

		// 改变view的数据
		for (int i = 0; i < from.length; i++) {
			View v = holder.getView(to[i]);
			if(v==null)continue;
			
			if(v instanceof ImageView){
				loadHead((ImageView)v, list.get(position).get(from[i]));
			}else if(v instanceof TextView){
				((TextView)v).setText(list.get(position).get(from[i]));
			}
		}
		return view;
	}


	protected void loadHead(final ImageView view, final String url) {
		if (url != null && !"".equals(url)) {
			asyncImgloader.loadDrawable(url,
					new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl) {
					if (imageDrawable == null){
						setDefaultHead(view);
						return;
					}
					
					Bitmap bitmap = ImageUtils.getRoundedCornerBitmap(ImageUtils.drawableToBitmap(imageDrawable), 5);
					view.setImageBitmap(bitmap);
				}
			});
		}else{
			setDefaultHead(view);
		}
	}

	protected void setDefaultHead(final ImageView head) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_good_img);
		Bitmap bitmap = ImageUtils.getRoundedCornerBitmap(bm, 5);
		head.setImageBitmap(bitmap);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 每一行的控件缓存
	 * @author leeboo
	 *
	 */
	static class ViewHolder {
		Map<Integer, View> views = new HashMap<Integer, View>();

		public void putView(int resId, View view){
			views.put(resId, view);
		}
		public View getView(int resId){
			return views.get(resId);
		}
    }
	
}
