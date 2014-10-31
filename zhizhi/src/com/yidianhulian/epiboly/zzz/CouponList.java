package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yidianhulian.epiboly.Const;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.epiboly.ZZZApplication;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CacheType;
import com.yidianhulian.framework.CallApiTask.CallApiListener;
import com.yidianhulian.framework.CallApiTask.FetchType;

public class CouponList extends Fragment implements CallApiListener {

    private PullToRefreshListView listView = null;
    private Button couponBtn = null;
    private Button favouriteBtn = null;
    private Button settingBtn = null;
    private TextView textView = null;
    private Button click_to_coupon = null;
    private RelativeLayout settingLayout = null;
    private Button close;
    private Button all;
    private Button only;
    private Button some;

    private Button check_voice;
    private Button check_shake;
    private Button check_silence;
    private String check_value;

    private String clickedType;
//    private String btnType;
    private int load_Type;
    private final static int GET_COUPONS = 2;
    private final static int REMOVE_COUPON = 3;
    private final static int GET_FAVORITES = 4;
    private final static int REMOVE_FAVORITE = 5;
    private final static int GET_SYSTEM_SETTING = 6;
    private final static int SAVE_SYSTEM_SETTING = 7;

    private String stimes = "";
    private String etimes = "";
    private Button start_time;

    private JSONObject good_data;
    private ListRowAdapter couponAdapter;
    private ListRowAdapter favouriteAdapter;
    private List<ListRow> listData = new ArrayList<ListRow>();
    private String couponId;
    private String favoriterId;
    TimePickerDialog timePickerDialog1;
    TimePickerDialog timePickerDialog2;
    Calendar calendar = Calendar.getInstance();

    public CouponList(int loadType) {
        this.load_Type = loadType;
    }

    public CouponList() {
        this.load_Type = GET_COUPONS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.couponslist, null);

        close = (Button) v.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_value = "0";
                clickedType = "push_option";
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });
        all = (Button) v.findViewById(R.id.all);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_value = "1";
                clickedType = "push_option";
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });
        only = (Button) v.findViewById(R.id.only);
        only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_value = "2";
                clickedType = "push_option";
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });
        some = (Button) v.findViewById(R.id.some);
        some.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_value = "3";
                clickedType = "push_option";
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });

        check_voice = (Button) v.findViewById(R.id.voice);
        check_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedType = "check_voice";
                if (check_voice.isSelected()) {
                    check_voice.setSelected(false);
                    check_value = "0";
                } else {
                    check_value = "1";
                    check_voice.setSelected(true);
                }
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });

        check_shake = (Button) v.findViewById(R.id.shake);
        check_shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedType = "check_shake";
                if (check_shake.isSelected()) {
                    check_shake.setSelected(false);
                    check_value = "0";
                } else {
                    check_value = "1";
                    check_shake.setSelected(true);
                }
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });

        check_silence = (Button) v.findViewById(R.id.silence);
        check_silence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedType = "check_silence";
                if (check_silence.isSelected()) {
                    check_silence.setSelected(false);
                    check_value = "0";
                } else {
                    check_value = "1";
                    check_silence.setSelected(true);
                }
                Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                load_data(SAVE_SYSTEM_SETTING);
            }
        });

        start_time = (Button) v.findViewById(R.id.start_time);
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedType = "start_time";
                // Calendar calendar = Calendar.getInstance();
                timePickerDialog1 = new AwerTimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute) {
                                stimes = hourOfDay + ":" + minute + ":00";
                                timePickerDialog1.dismiss();
                                // Calendar calendar = Calendar.getInstance();
                                timePickerDialog2 = new AwerTimePickerDialog(
                                        getActivity(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(
                                                    TimePicker view,
                                                    int hourOfDay, int minute) {
                                                etimes = hourOfDay + ":"
                                                        + minute + ":00";
                                                start_time.setText("从" + stimes
                                                        + "到" + etimes);
                                                timePickerDialog2.dismiss();
                                                Util.showLoading(getActivity(),
                                                        Util.DATA_PROCESSING);
                                                load_data(SAVE_SYSTEM_SETTING);
                                            }
                                        }, calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE), true);
                                timePickerDialog2.setTitle("结束时间");
                                timePickerDialog2.setButton(
                                        DialogInterface.BUTTON_POSITIVE, "设定",
                                        timePickerDialog2);

                                timePickerDialog2.setButton(
                                        DialogInterface.BUTTON_NEGATIVE, "取消",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                timePickerDialog2.dismiss();
                                            }

                                        });
                                timePickerDialog2.show();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar
                                .get(Calendar.MINUTE), true);
                timePickerDialog1.setTitle("开始时间");
                timePickerDialog1.setButton(DialogInterface.BUTTON_POSITIVE,
                        "设定", timePickerDialog1);

                timePickerDialog1.setButton(DialogInterface.BUTTON_NEGATIVE,
                        "取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                timePickerDialog1.dismiss();
                            }

                        });
                timePickerDialog1.show();
            }
        });

        listView = (PullToRefreshListView) v.findViewById(R.id.centerlist);

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (load_Type == GET_COUPONS) {
                    load_data(GET_COUPONS);
                } else if ( load_Type == GET_FAVORITES ) {
                    load_data(GET_FAVORITES);
                }
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

            }

        });
        couponBtn = (Button) v.findViewById(R.id.couponBtn);

        favouriteBtn = (Button) v.findViewById(R.id.favouriteBtn);
        settingBtn = (Button) v.findViewById(R.id.settingBtn);
        textView = (TextView) v.findViewById(R.id.coupon_qty);
        click_to_coupon = (Button) v.findViewById(R.id.click_to_coupon);

        settingLayout = (RelativeLayout) v.findViewById(R.id.setting_layout);

        couponAdapter = new MyListRowAdapter(getActivity(), listData, listView,
                R.layout.coupons_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView delete = (Button) view.findViewById(R.id.delete);
                delete.setTag(position);
                delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 调用移除coupon接口
                        couponId = listData.get((Integer) v.getTag()).getId();
                        Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                        
                        load_data(REMOVE_COUPON);
                    }
                });

                Button copy_code = (Button) view.findViewById(R.id.copy_code);
                copy_code.setTag(position);
                copy_code.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ClipboardManager cbm = (ClipboardManager) getActivity()
                                .getSystemService(
                                        getActivity().CLIPBOARD_SERVICE);
                        cbm.setText(listData.get((Integer) v.getTag())
                                .getCode_in());
                        Util.sendToast(getActivity(), "已成功复制到剪切板",
                                Toast.LENGTH_LONG);
                    }
                });
                return view;
            }
        };

        favouriteAdapter = new MyListRowAdapter(this.getActivity(), listData,
                listView, R.layout.favourite_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Button delete = (Button) view.findViewById(R.id.delete);
                delete.setTag(position);
                delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 调用移除favorite接口
                        favoriterId = listData.get((Integer) v.getTag())
                                .getId();
                        Util.showLoading(getActivity(), Util.DATA_PROCESSING);
                        load_data(REMOVE_FAVORITE);
                    }
                });

                Button reach = (Button) view.findViewById(R.id.reach);
                reach.setTag(position);
                reach.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        good_data = listData.get((Integer) v.getTag())
                                .getGoodData();
                        Intent intent = new Intent(getContext(),
                                CouponWebViewActivity.class);
                        intent.putExtra("url",
                                Api.getJSONValue(good_data, "good_url")
                                        .toString());
                        getContext().startActivity(intent);

                    }
                });
                return view;
            }

        };

        couponBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                load_Type = GET_COUPONS;
                textView.setVisibility(View.GONE);
                click_to_coupon.setVisibility(View.GONE);
                settingLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(couponAdapter);
                couponBtn.setSelected(true);
                couponBtn.setTextColor(getResources().getColor(R.color.red));
                favouriteBtn.setSelected(false);
                favouriteBtn
                        .setTextColor(getResources().getColor(R.color.gray));
                settingBtn.setSelected(false);
                settingBtn.setTextColor(getResources().getColor(R.color.gray));
                Util.showLoading(getActivity(), Util.DATA_LOADING);
                load_data(GET_COUPONS);
            }

        });

        favouriteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                load_Type = GET_FAVORITES;
                textView.setVisibility(View.GONE);
                click_to_coupon.setVisibility(View.GONE);
                settingLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(favouriteAdapter);
                couponBtn.setSelected(false);
                favouriteBtn.setSelected(true);
                settingBtn.setSelected(false);
                couponBtn.setTextColor(getResources().getColor(R.color.gray));
                favouriteBtn.setTextColor(getResources().getColor(R.color.red));
                settingBtn.setTextColor(getResources().getColor(R.color.gray));
                Util.showLoading(getActivity(), Util.DATA_LOADING);
                load_data(GET_FAVORITES);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                load_Type = GET_SYSTEM_SETTING;
                textView.setVisibility(View.GONE);
                click_to_coupon.setVisibility(View.GONE);
                settingLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                couponBtn.setSelected(false);
                favouriteBtn.setSelected(false);
                settingBtn.setSelected(true);
                couponBtn.setTextColor(getResources().getColor(R.color.gray));
                favouriteBtn
                        .setTextColor(getResources().getColor(R.color.gray));
                settingBtn.setTextColor(getResources().getColor(R.color.red));
                Util.showLoading(getActivity(), Util.DATA_LOADING);
                load_data(GET_SYSTEM_SETTING);
            }
        });
        switch (load_Type) {
        case GET_COUPONS:
//            load_Type = GET_COUPONS;
            textView.setVisibility(View.GONE);
            click_to_coupon.setVisibility(View.GONE);
            settingLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(couponAdapter);
            break;
        case GET_FAVORITES:
//            load_Type = GET_FAVORITES;
            textView.setVisibility(View.GONE);
            click_to_coupon.setVisibility(View.GONE);
            settingLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(favouriteAdapter);
            break;
        case GET_SYSTEM_SETTING:
//            load_Type = GET_SYSTEM_SETTING;
            textView.setVisibility(View.GONE);
            click_to_coupon.setVisibility(View.GONE);
            settingLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            break;
        default:
            break;
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(getActivity(),
                        GoodsDetailsActivity.class);
                if (load_Type == GET_COUPONS) {
                    // dy_edit
                    intent.putExtra("id", listData.get((int) id).getGood_id());
                } else if (load_Type == GET_FAVORITES) {
                    intent.putExtra("id", listData.get((int) id).getId());
                }

                getActivity().startActivity(intent);
            }
        });

        return v;
    }

    public void load_data(int what) {
//    	CallApiTask.doCallApi(what, CouponList.this, getActivity());
    	// 国兴财
        CallApiTask.doCallApi(what, CouponList.this, getActivity(), CacheType.REPLACE, FetchType.FETCH_API_ELSE_CACHE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // if(!ZZZApplication.isLogin()){
        // getActivity().finish();
        // }
        // 用户登进登出
//        if ("coupon".equals(btnType)) {
//            Util.showLoading(getActivity(), Util.DATA_LOADING);
//            load_data(GET_COUPONS);
//        } else if ("favorite".equals(btnType)) {
//            Util.showLoading(getActivity(), Util.DATA_LOADING);
//            load_data(GET_FAVORITES);
//        }
        /**
         * author xialinchong
         * 把btnType全部换成load_Type
         */
        Util.showLoading(getActivity(), Util.DATA_LOADING);
        load_data(load_Type);
    }

    class MyListRowAdapter extends ListRowAdapter {

        public MyListRowAdapter(Activity activity, List<ListRow> listRow,
                PullToRefreshListView listView, int rowlayout) {
            super(activity, listRow, listView, rowlayout);
        }

    }

    @Override
    public Api getApi(int what, Object... params) {
        HashMap<String, String> search_params = new HashMap<String, String>();
        search_params.put("cookie", Util.getLoginUserItem(getActivity()
                .getApplicationContext(), "cookie"));
        switch (what) {
        // 获取优惠券列表
        case GET_COUPONS:
            return new Api("get", Const.GET_USER_COUPONS, search_params);

            // 移除优惠券列表
        case REMOVE_COUPON:
            search_params.put("couponId", couponId);
            return new Api("get", Const.REMOVE_COUPON, search_params);

            // 获取收藏列表
        case GET_FAVORITES:
            return new Api("get", Const.GET_FAVORITE_GOODS, search_params);

            // 移除收藏列表
        case REMOVE_FAVORITE:
            search_params.put("gid", favoriterId);
            return new Api("get", Const.REMOVE_FAVORITE_GOODS, search_params);

            // 获取系统设置
        case GET_SYSTEM_SETTING:
            return new Api("get",Const.GET_USER_SETTING, search_params);

            // 保存系统设置
        case SAVE_SYSTEM_SETTING:
            if (clickedType.equals("push_option")) {
                search_params.put("push_option", check_value);
            } else if (clickedType.equals("check_voice")) {
                search_params.put("notice_voice", check_value);
            } else if (clickedType.equals("check_shake")) {
                search_params.put("notice_viberate", check_value);
            } else if (clickedType.equals("check_silence")) {
                search_params.put("receive_topic", check_value);
            } else {
                search_params.put("silence_time_start", stimes);
                search_params.put("silence_time_end", etimes);
            }
            return new Api("get", Const.SAVE_USER_SETTING, search_params);

        default:
            return null;
        }
    }

    @Override
    public boolean isCallApiSuccess(JSONObject result) {
    	if (result == null || !"ok".equals(Api.getJSONValue(result, "status"))) return false;
        return true;
    }

    @Override
    public void apiNetworkException(Exception e) {
        
    }

    @Override
    public String getCacheKey(int what, Object... params) {
    	// 添加KEY 
    	switch(what){
    		case GET_COUPONS:
    			return Const.GET_USER_COUPONS;
			case GET_FAVORITES:
				return Const.GET_FAVORITE_GOODS;
			default : 
				return null;
    	}
    }

    @Override
    public void handleResult(int what, JSONObject result, boolean isDone,
            Object... params) {
        if (isDone) {
            Util.hideLoading();
        }
        
        boolean check_result = Util.checkResult(getActivity(), result);
        try {
            switch (what) {
            // 获取优惠券列表
            case GET_COUPONS:
                listData.clear();
                if (check_result
                        && Api.getJSONValue(result, "list") != null
                        && !Api.getJSONValue(result, "list").toString()
                                .equals("null")) {
                    JSONArray list = (JSONArray) Api.getJSONValue(result,
                            "list");

                    textView.setVisibility(View.VISIBLE);
                    click_to_coupon.setVisibility(View.GONE);
                    textView.setText("您已领取" + list.length() + "张优惠券，请尽快使用");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObj = (JSONObject) list.get(i);
                        JSONObject coupon = (JSONObject) Api.getJSONValue(
                                jsonObj, "coupon");
                        JSONObject good = (JSONObject) Api.getJSONValue(
                                jsonObj, "good");

                        String id = Api.getJSONValue(coupon, "id").toString();
                        String desc = Api.getJSONValue(coupon, "description")
                                .toString();
                        String good_id = Api.getJSONValue(good, "id")
                                .toString();
                        String img = Api.getJSONValue(good, "thumbnail")
                                .toString();
                        String code_in = Api.getStringValue(coupon, "code_in");
                        ListRow listRow = new ListRow(id, good_id, desc, img,
                                code_in);
                        listData.add(listRow);
                    }
                } else {
                    click_to_coupon.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    click_to_coupon.setText("您还没有领取优惠券，点击前往领取!");
                    click_to_coupon.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            GoodsListNav nav = new GoodsListNav(ZZZApplication
                                    .getInstance().addresses, "0", "youhuiquan");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, nav).commit();

                            ((MainActivity) getActivity()).restoreActionBar();
                        }

                    });
                }
                couponAdapter.notifyDataSetChanged();
                couponBtn.setSelected(true);
                favouriteBtn.setSelected(false);
                settingBtn.setSelected(false);
                couponBtn.setTextColor(getResources().getColor(R.color.red));
                favouriteBtn
                        .setTextColor(getResources().getColor(R.color.gray));
                settingBtn.setTextColor(getResources().getColor(R.color.gray));
                listView.onRefreshComplete();
                break;
            // 移除优惠券列表
            case REMOVE_COUPON:
                if (check_result) {
                    Util.showLoading(getActivity(), Util.DATA_LOADING);
                    load_data(GET_COUPONS);
                }

                break;
            // 获取收藏列表
            case GET_FAVORITES:
                listData.clear();
                if (check_result
                        && Api.getJSONValue(result, "goods") != null
                        && !Api.getJSONValue(result, "goods").toString()
                                .equals("null")) {
                    JSONArray goods = (JSONArray) Api.getJSONValue(result,
                            "goods");

                    for (int i = 0; i < goods.length(); i++) {
                        JSONObject jsonObj = (JSONObject) goods.get(i);
                        String id = Api.getJSONValue(jsonObj, "id").toString();
                        String desc = Api.getJSONValue(jsonObj, "title")
                                .toString();
                        String img = Api.getJSONValue(jsonObj, "thumbnail")
                                .toString();
                        ListRow listRow = new ListRow(id, desc, img, jsonObj);
                        listData.add(listRow);
                    }
                } else {
                    textView.setText("");
                }
                couponBtn.setSelected(false);
                favouriteBtn.setSelected(true);
                settingBtn.setSelected(false);
                couponBtn.setTextColor(getResources().getColor(R.color.gray));
                favouriteBtn.setTextColor(getResources().getColor(R.color.red));
                settingBtn.setTextColor(getResources().getColor(R.color.gray));
                favouriteAdapter.notifyDataSetChanged();
                listView.onRefreshComplete();
                break;
            // 移除优惠券列表
            case REMOVE_FAVORITE:
                if (check_result) {
                    Util.showLoading(getActivity(), Util.DATA_LOADING);
                    load_data(GET_FAVORITES);
                }

                break;
            // 获取系统设置
            case GET_SYSTEM_SETTING:
            case SAVE_SYSTEM_SETTING:
                JSONObject jsonObj = (JSONObject) Api.getJSONValue(result,
                        "settings");
                if (check_result && jsonObj != null
                        && !jsonObj.toString().equals("null")) {
                    if (jsonObj.get("notice_voice").equals("1")) {
                        check_voice.setSelected(true);
                    } else {
                        check_voice.setSelected(false);
                    }
                    if (jsonObj.get("notice_viberate").equals("1")) {
                        check_shake.setSelected(true);
                    } else {
                        check_shake.setSelected(false);
                    }
                    if (jsonObj.get("receive_toptic").equals(null)) {
                        // null=1--------------------------
                        check_silence.setSelected(true);
                        start_time.setClickable(true);
                    } else {
                        check_silence.setSelected(false);
                        start_time.setClickable(false);
                    }

                    String start = jsonObj.get("silence_time_start").toString();
                    String end = jsonObj.get("silence_time_end").toString();
                    start_time.setText("从" + start + "到" + end);

                    if (jsonObj.get("push_option").equals("0")) {
                        close.setSelected(true);
                        all.setSelected(false);
                        only.setSelected(false);
                        some.setSelected(false);
                    } else if (jsonObj.get("push_option").equals("1")) {
                        all.setSelected(true);
                        close.setSelected(false);
                        only.setSelected(false);
                        some.setSelected(false);
                    } else if (jsonObj.get("push_option").equals("2")) {
                        only.setSelected(true);
                        close.setSelected(false);
                        all.setSelected(false);
                        some.setSelected(false);
                    } else {
                        some.setSelected(true);
                        close.setSelected(false);
                        all.setSelected(false);
                        only.setSelected(false);
                    }
                }
                couponBtn.setSelected(false);
                favouriteBtn.setSelected(false);
                settingBtn.setSelected(true);
                couponBtn.setTextColor(getResources().getColor(R.color.gray));
                favouriteBtn
                        .setTextColor(getResources().getColor(R.color.gray));
                settingBtn.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject appendResult(int what, JSONObject from, JSONObject to) {
        return null;
    }

    @Override
    public JSONObject prependResult(int what, JSONObject from, JSONObject to) {
        return null;
    }
}
