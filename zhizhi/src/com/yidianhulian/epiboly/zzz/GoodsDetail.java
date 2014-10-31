package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.AsyncImageLoader;
import com.yidianhulian.framework.AsyncImageLoader.ImageCallback;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CallApiListener;

public class GoodsDetail extends Fragment implements CallApiListener,
        OnPageChangeListener {
    private static final int GET_GOOD = 1;
    private static final int DO_COLLECT = 2;
    private static final int DO_HEAT_GOOD = 3;
    private static final int DO_GET_COUPON = 4;

    private TextView shop_name;
    private TextView stock_date;
    private TextView good_title;
    private TextView good_comment;
    private TextView rate_score;
    private WebView good_desc;

    private Button btnCollect;
    private Button btnComment;
    private Button btnHeatGood;
    private Button btnCoupon;

    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;

    /**
     * 图片url
     */
    private ArrayList<String> imgUrlArray = new ArrayList<String>();

    private AsyncImageLoader asyncImageLoader;

    private FrameLayout goods_imgs_layout;
    private ViewGroup groupView;
    private ImageView goodImageView;

    private View currentView;

    private String user_cookie = "";
    private int rate_score_value = 0;
    private int rate_colds_value = 0;
    private String html_content;
    private String imgUrl;

    Button btn_heat_score;
    Button btn_cold_score;

    private JSONObject good_data;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.goods_details, null);
        currentView = v;

        shop_name = (TextView) v.findViewById(R.id.shop_name);
        stock_date = (TextView) v.findViewById(R.id.stock_date);
        good_title = (TextView) v.findViewById(R.id.good_title);
        good_comment = (TextView) v.findViewById(R.id.good_comment);
        rate_score = (TextView) v.findViewById(R.id.rate_score);
        good_desc = (WebView) v.findViewById(R.id.good_desc);

        goods_imgs_layout = (FrameLayout) v
                .findViewById(R.id.goods_imgs_layout);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        goods_imgs_layout.getLayoutParams().height = dm.widthPixels;

        groupView = (ViewGroup) v.findViewById(R.id.img_view_group);
        viewPager = (ViewPager) v.findViewById(R.id.img_view_pager);
        goodImageView = (ImageView) v.findViewById(R.id.good_img_view);

        final GoodsDetailsActivity activity = (GoodsDetailsActivity) getActivity();
        asyncImageLoader = AsyncImageLoader.getInstance(activity);

        user_cookie = Util.getLoginUserItem(activity.getApplicationContext(),
                "cookie");

        reload();

        // 评论
        btnComment = (Button) v.findViewById(R.id.btn_comment);
        btnComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager manager = getFragmentManager();

                if (manager.findFragmentByTag("good_comment") == null) {
                    manager.beginTransaction()
                            .replace(R.id.good_detail_frame, new Comments(),
                                    "good_comment")
                            .setTransition(
                                    FragmentTransaction.TRANSIT_FRAGMENT_OPEN)// 设置动画
                            .addToBackStack("good_comment") // 将该fragment加入返回堆
                            .commit();
                }
            }
        });

        // 收藏
        btnCollect = (Button) v.findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!ZZZApplication.isLogin()) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), Signin.class);
                    getActivity().startActivity(intent);
                    return;
                }
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                CallApiTask.doCallApi(DO_COLLECT, GoodsDetail.this,
                        getActivity());
            }
        });

        // 加热商品,值否
        btnHeatGood = (Button) v.findViewById(R.id.btn_heat_good);
        btnHeatGood.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showWindow(arg0);
            }
        });

        // 优惠直达
        btnCoupon = (Button) v.findViewById(R.id.btn_coupon);
        btnCoupon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (good_data != null) {
                    Intent intent = new Intent(activity,
                            CouponWebViewActivity.class);
                    intent.putExtra("url",
                            Api.getJSONValue(good_data, "good_url").toString());
                    activity.startActivity(intent);
                }
            }
        });

        return v;
    }

    public void reload() {
        String good_id = ((GoodsDetailsActivity) getActivity()).getGoodId();
        if (good_id == null || good_id.isEmpty())
            return;
        Util.showLoading(getActivity(), Util.DATA_LOADING);
        CallApiTask.doCallApi(GET_GOOD, GoodsDetail.this, getActivity());
    }

    private void showWindow(View parent) {
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.heat_good, null);

        // final PopupWindow popupWindow = new PopupWindow(view, 240, 140);
        final PopupWindow popupWindow = new PopupWindow(view);

        // 控制popupwindow的宽度和高度自适应
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // popupWindow.showAtLocation(parent, Gravity.TOP |
        // Gravity.CENTER_HORIZONTAL, 0, 0);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        popupWindow.getContentView().measure(0, 0);
        int height = popupWindow.getContentView().getMeasuredHeight();
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0]
                - parent.getWidth() / 2, location[1] - height);

        btn_heat_score = (Button) view.findViewById(R.id.btn_heat_score);
        btn_cold_score = (Button) view.findViewById(R.id.btn_cold_score);
        btn_heat_score.setText("值\n" + rate_score_value);
        btn_cold_score.setText("不值\n" + rate_colds_value);

        btn_heat_score.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                if (!ZZZApplication.isLogin()) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), Signin.class);
//                    getActivity().startActivity(intent);
//                    return;
//                }
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                CallApiTask.doCallApi(DO_HEAT_GOOD, GoodsDetail.this,
                        getActivity(), "2");
                popupWindow.dismiss();
            }
        });

        btn_cold_score.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                if (!ZZZApplication.isLogin()) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), Signin.class);
//                    getActivity().startActivity(intent);
//                    return;
//                }
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                CallApiTask.doCallApi(DO_HEAT_GOOD, GoodsDetail.this,
                        getActivity(), "1");
                popupWindow.dismiss();
            }
        });
    }

    private void setGoodDetail(JSONObject result) {
        JSONObject jsonObj = (JSONObject) Api.getJSONValue(result, "good");
        good_data = jsonObj;

        String shop_name_desc = "";
        JSONArray jarr = (JSONArray) Api.getJSONValue(jsonObj, "shop");
        for (int j = 0; j < jarr.length(); j++) {
            try {
                shop_name_desc = shop_name_desc + " " + jarr.get(j);
            } catch (JSONException e) {

            }
        }
        shop_name.setText("商城：" + shop_name_desc);

        String stock_date_desc = Api.getJSONValue(jsonObj, "date").toString();
        stock_date_desc = stock_date_desc.substring(0, 10);
        stock_date.setText(stock_date_desc);

        String title = Api.getJSONValue(jsonObj, "title").toString();
        ArrayList positions = Util.filterTitleSpan(title);
        SpannableStringBuilder span_builder = Util.getSpanTextStyle(
                getActivity(), title, positions);
        good_title.setText(span_builder);

        good_comment.setText("评论："
                + Api.getJSONValue(jsonObj, "comment_count").toString());

        rate_score_value = (Integer) Api.getJSONValue(jsonObj, "rating_hots");
        rate_colds_value = (Integer) Api.getJSONValue(jsonObj, "rating_colds");
        rate_score.setText("值否：" + rate_score_value);

        good_desc.loadDataWithBaseURL("", Api.getJSONValue(jsonObj, "content")
                .toString(), "text/html", "UTF-8", "");
        html_content = Html.fromHtml(
                Api.getJSONValue(jsonObj, "content").toString()).toString();
        imgUrl = Api.getJSONValue(jsonObj, "thumbnail").toString();
        WebSettings webSettings = good_desc.getSettings();
        // webSettings.setUseWideViewPort(true);
        // webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        good_desc.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(getActivity(),
                        CouponWebViewActivity.class);
                intent.putExtra("url", url);
                getActivity().startActivity(intent);
                return true;
                // return super.shouldOverrideUrlLoading(view, url);
            }

        });

        imgUrlArray.clear();
        JSONArray pictures = (JSONArray) Api.getJSONValue(jsonObj, "pictures");
        for (int k = 0; k < pictures.length(); k++) {
            try {
                imgUrlArray.add(pictures.get(k).toString());
            } catch (JSONException e) {

            }
        }

        FragmentActivity activity = getActivity();
        Object is_coupon = Api.getJSONValue(result, "is_coupon");
        if (is_coupon != null && Boolean.parseBoolean(is_coupon.toString())) {
            goods_imgs_layout.setVisibility(View.GONE);
            final JSONObject couponObj = (JSONObject) Api.getJSONValue(result,
                    "coupon");
            LinearLayout coupon_layout = (LinearLayout) currentView
                    .findViewById(R.id.coupon_layout);
            coupon_layout.setVisibility(View.VISIBLE);

            ((TextView) currentView.findViewById(R.id.coupon_mall_name))
                    .setText(Api.getJSONValue(couponObj, "mall_name")
                            .toString());
            ((TextView) currentView.findViewById(R.id.coupon_discount_desc))
                    .setText(Api.getJSONValue(couponObj, "discount_desc")
                            .toString());
            ((TextView) currentView.findViewById(R.id.coupon_edate))
                    .setText(Api.getJSONValue(couponObj, "edate").toString()
                            + R.string.coupon_edate);
            ((TextView) currentView.findViewById(R.id.coupon_remained))
                    .setText(Api.getJSONValue(couponObj, "coupon_remained")
                            .toString());
            ((TextView) currentView.findViewById(R.id.coupon_use_desc))
                    .setText(Api.getJSONValue(couponObj, "description")
                            .toString() + R.string.coupon_use_desc);

            ImageButton btn_get_coupon = (ImageButton) currentView
                    .findViewById(R.id.btn_get_coupon);
            btn_get_coupon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                    CallApiTask.doCallApi(DO_GET_COUPON, GoodsDetail.this,
                            getActivity(),
                            Api.getJSONValue(couponObj, "batch_id").toString());
                }
            });
        } else {
            goods_imgs_layout.setVisibility(View.VISIBLE);
            int good_img_count = imgUrlArray.size();
            if (good_img_count > 1) {
                tips = new ImageView[good_img_count];
                for (int i = 0; i < tips.length; i++) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(new LayoutParams(10, 10));
                    tips[i] = imageView;
                    if (i == 0) {
                        tips[i].setBackgroundResource(R.drawable.img_indicator_focused);
                    } else {
                        tips[i].setBackgroundResource(R.drawable.img_indicator_unfocused);
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            new ViewGroup.LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));
                    layoutParams.leftMargin = 5;
                    layoutParams.rightMargin = 5;
                    groupView.addView(imageView, layoutParams);
                }

                // 将图片装载到数组中
                mImageViews = new ImageView[good_img_count];
                for (int i = 0; i < mImageViews.length; i++) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setTag(imgUrlArray.get(i));
                    mImageViews[i] = imageView;
                }
                goodImageView.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                groupView.setVisibility(View.VISIBLE);

                viewPager.setCurrentItem(0);
                viewPager.setAdapter(new MyAdapter());
                viewPager.setOnPageChangeListener(this);

            } else if (good_img_count == 1) {
                goodImageView.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                groupView.setVisibility(View.GONE);

                asyncImageLoader.loadDrawable(imgUrlArray.get(0).toString(),
                        new ImageCallback() {
                            @Override
                            public void imageLoaded(Drawable imageDrawable,
                                    String imageUrl) {
                                if (imageDrawable != null) {
                                    goodImageView
                                            .setBackgroundDrawable(imageDrawable);
                                } else {
                                    goodImageView
                                            .setBackgroundDrawable(getResources()
                                                    .getDrawable(
                                                            R.drawable.placeholder_error));
                                }
                            }
                        });
            } else {
                goodImageView.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                groupView.setVisibility(View.GONE);
                goodImageView.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.placeholder_error));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.good_detail_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ((item.getItemId() == R.id.menu_collect
                || item.getItemId() == R.id.menu_like || item.getItemId() == R.id.menu_unlike)
                && !ZZZApplication.isLogin()) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Signin.class);
            getActivity().startActivity(intent);
        }

        else if (item.getItemId() == android.R.id.home) {
            this.getActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.menu_share) {
            showOnekeyshare(null, true, html_content, imgUrl);
        } else if (item.getItemId() == R.id.menu_collect) {
            Util.showLoading(getActivity(), Util.DATA_PROCESSING);
            CallApiTask.doCallApi(DO_COLLECT, GoodsDetail.this, getActivity());
        } else if (item.getItemId() == R.id.menu_comment) {
            FragmentManager manager = getFragmentManager();

            if (manager.findFragmentByTag("good_comment") == null) {
                manager.beginTransaction()
                        .replace(R.id.good_detail_frame, new Comments(),
                                "good_comment")
                        .setTransition(
                                FragmentTransaction.TRANSIT_FRAGMENT_OPEN)// 设置动画
                        .addToBackStack("good_comment") // 将该fragment加入返回堆
                        .commit();
            }
        } else if (item.getItemId() == R.id.menu_like) {
            Util.showLoading(getActivity(), Util.DATA_PROCESSING);
            CallApiTask.doCallApi(DO_HEAT_GOOD, GoodsDetail.this, getActivity(), "2");
        } else if (item.getItemId() == R.id.menu_unlike) {
            Util.showLoading(getActivity(), Util.DATA_PROCESSING);
            CallApiTask.doCallApi(DO_HEAT_GOOD, GoodsDetail.this, getActivity(), "1");
        } else if (item.getItemId() == R.id.menu_coupon) {
            if (good_data != null) {
                Intent intent = new Intent(getActivity(),
                        CouponWebViewActivity.class);
                intent.putExtra("url", Api.getJSONValue(good_data, "good_url")
                        .toString());
                getActivity().startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0 % mImageViews.length);
    }

    /**
     * 设置选中的tip的背景
     * 
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.img_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.img_indicator_unfocused);
            }
        }
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            // ((ViewPager)container).removeView(mImageViews[position %
            // mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            int item = position % mImageViews.length;
            try {
                final ImageView imgView = mImageViews[item];
                imgView.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.placeholder));
                asyncImageLoader.loadDrawable(imgView.getTag().toString(),
                        new ImageCallback() {
                            @Override
                            public void imageLoaded(Drawable imageDrawable,
                                    String imageUrl) {
                                if (imageDrawable != null) {
                                    imgView.setBackgroundDrawable(imageDrawable);
                                } else {
                                    imgView.setBackgroundDrawable(getResources()
                                            .getDrawable(
                                                    R.drawable.placeholder_error));
                                }
                            }
                        });

                ((ViewPager) container).addView(imgView, item);
            } catch (Exception e) {

            }
            return mImageViews[item];
        }

    }

    public void showOnekeyshare(String platform, boolean silent,
            String content, String imgUrl) {
        OnekeyShare oks = new OnekeyShare();

        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, "");
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("来自值值值的分享");
        oks.setTitleUrl("http://www.zhizhizhi.com");

        oks.setSite("值值值");
        oks.setSiteUrl("http://www.zhizhizhi.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // 设置分享照片的url地址，如果没有可以不设置
        oks.setImageUrl(imgUrl);
        // latitude是维度数据，仅在新浪微博、腾讯微博和Foursquare使用
        oks.setLatitude(23.122619f);
        // longitude是经度数据，仅在新浪微博、腾讯微博和Foursquare使用
        oks.setLongitude(113.372338f);
        // 是否直接分享（true则直接分享）
        oks.setSilent(silent);
        // 指定分享平台，和slient一起使用可以直接分享到指定的平台
        if (platform != null) {
            oks.setPlatform(platform);
        }

        oks.show(getActivity());
    }

    @Override
    public Api getApi(int what, Object... params) {
        HashMap<String, String> search_params = new HashMap<String, String>();
        String good_id = ((GoodsDetailsActivity) getActivity()).getGoodId();

        String api = "";
        if (what == GET_GOOD) { // 获取详细
            search_params.put("cookie", user_cookie);
            api = Const.GET_GOOD;
            search_params.put("id", good_id);
        } else if (what == DO_COLLECT) { // 收藏
            search_params.put("cookie", user_cookie);
            api = Const.FAVOR_GOOD;
            search_params.put("gid", good_id);
        } else if (what == DO_HEAT_GOOD) { // 值否
            /**
             * author xialinchong
             * 登陆了就放cookie参数，否则就不传 cookie
             */
            if (ZZZApplication.isLogin()) {
                search_params.put("cookie", user_cookie);
            }
            api = Const.HEAT_GOOD;
            search_params.put("gid", good_id);
            search_params.put("score", params[0].toString());
        } else if (what == DO_GET_COUPON) { // 领劵
            search_params.put("cookie", user_cookie);
            api = Const.GET_COUPON;
            search_params.put("gid", good_id);
            search_params.put("couponId", params[0].toString());
        }

        return new Api("get", api, search_params);
    }

    @Override
    public boolean isCallApiSuccess(JSONObject result) {
        return false;
    }

    @Override
    public void apiNetworkException(Exception e) {

    }

    @Override
    public String getCacheKey(int what, Object... params) {
        return null;
    }

    @Override
    public void handleResult(int what, JSONObject result, boolean isDone,
            Object... params) {
        switch (what) {
        case GET_GOOD: // 获取详细

            if (result == null
                    || !"ok".equals(Api.getJSONValue(result, "status"))) {
                Util.hideLoading();
                return;
            }
            setGoodDetail(result);
            Util.hideLoading();

            break;
        case DO_COLLECT: // 收藏
        case DO_HEAT_GOOD: // 值否
        case DO_GET_COUPON: // 领劵
            // if (what == DO_COLLECT || what == DO_GET_COUPON) {
            Util.hideLoading();
            // }
            if (result == null
                    || !"ok".equals(Api.getJSONValue(result, "status"))) {
                if (Api.getStringValue(result, "msg") == null
                        || Api.getStringValue(result, "msg").equals("")) {
                    Toast.makeText(getActivity(),
                            R.string.operation_fail_notice, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getActivity(),
                            Api.getStringValue(result, "msg"),
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(),
                        R.string.operation_success_notice, Toast.LENGTH_SHORT)
                        .show();
            }
            break;
        }
    }

    @Override
    public JSONObject appendResult(int what, JSONObject from, JSONObject to) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject prependResult(int what, JSONObject from, JSONObject to) {
        // TODO Auto-generated method stub
        return null;
    }
}
