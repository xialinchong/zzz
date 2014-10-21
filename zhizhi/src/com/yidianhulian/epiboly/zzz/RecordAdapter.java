package com.yidianhulian.epiboly.zzz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.AsyncImageLoader;
import com.yidianhulian.framework.AsyncImageLoader.ImageCallback;

public class RecordAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<JSONObject> listItem;
    private AsyncImageLoader asyncImageLoader;
    private ListView listView;
 
    public RecordAdapter(Context context,
    		ArrayList<JSONObject> listitem,
            ListView listView) {
        super();
        this.context = context;
        this.listItem = listitem;
        asyncImageLoader = AsyncImageLoader.getInstance((Activity) context);
        this.listView = listView;
    }
 
    public int getCount() {
        return listItem.size();
    }
 
    public Object getItem(int arg0) {
        return listItem.get(arg0);
    }
 
    public long getItemId(int arg0) {
        return arg0;
    }
 
    public View getView(int position , View convertView, ViewGroup parent) {
        // 取出数据http://www.oschina.net/question/178960_70945
    	JSONObject jsonObj = (JSONObject)listItem.get(position);
        String good_descStr = Api.getJSONValue(jsonObj, "title").toString();
        
        String ven_nameStr = "";
		JSONArray jarr =  (JSONArray)Api.getJSONValue(jsonObj, "shop");
		for (int j = 0; j < jarr.length(); j++) {
			try {
				ven_nameStr = ven_nameStr + " " + jarr.get(j);
			} catch (JSONException e) {
				
			}
		}
        
		//"5分钟之前date"
		String scdate = Api.getJSONValue(jsonObj, "date").toString();
        String time_intervalStr = getTimeIntervalStr(scdate);
        String collect_qtyStr = "收藏：" +Api.getJSONValue(jsonObj, "favor_count").toString();
        String comment_qtyStr = "评论：" + Api.getJSONValue(jsonObj, "comment_count").toString();
        String like_qtyStr    = "值否：" + Api.getJSONValue(jsonObj, "rating_hots").toString();
        
        String url = Api.getJSONValue(jsonObj, "thumbnail").toString();
        
        View tem;
        if (convertView == null) {
            LinearLayout temRl = (LinearLayout) View.inflate(context,
                    R.layout.goods_list_row, null);
            
            tem = temRl;
        } else {
            tem = convertView;
        }
        ImageView webView = (ImageView) tem.findViewById(R.id.good_img);
        webView.setTag( url );
        asyncImageLoader.loadDrawable(url, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                    imageViewByTag.setImageDrawable(imageDrawable);
                } 
//                else {
//                	imageViewByTag.setImageResource(R.drawable.placeholder_error);
//                }
			}
		});
        
        
        TextView good_desc = (TextView) tem.findViewById(R.id.good_desc);
        good_desc.setText(good_descStr);
        
        TextView vendor_name = (TextView) tem.findViewById(R.id.vendor_name);
        vendor_name.setText(ven_nameStr);
        
        TextView time_interval = (TextView) tem.findViewById(R.id.time_interval);
        time_interval.setText(time_intervalStr);
        
        TextView collect_qty = (TextView) tem.findViewById(R.id.collect_qty);
        collect_qty.setText( collect_qtyStr );
        
        TextView comment_qty = (TextView) tem.findViewById(R.id.comment_qty);
        comment_qty.setText( comment_qtyStr );
        
        TextView like_qty = (TextView) tem.findViewById(R.id.like_qty);
        like_qty.setText(like_qtyStr);
        
        return tem;
 
    }
 
    public String getTimeIntervalStr (String scdate) {
    	Date date = new Date();
    	int now = (int) (date.getTime()/1000);
    	int after = 0;
    	
    	Calendar c = Calendar.getInstance();
    	try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(scdate));
			after = (int) (c.getTimeInMillis()/1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	int diff = now - after;
    	String desc;
    	if (diff < 60) { 
    		desc = "刚才"; 
    	} else if (diff < 3600) { 
    		desc = (int) (diff / 60) + "分钟前"; 
    	} else if (diff < 86400) { 
    		desc = (int) (diff / 3600) + "小时前"; 
    	} else { 
    		desc = (int) (diff / 86400) + "天前"; 
    	} 
    	return desc; 
    }
    
}
