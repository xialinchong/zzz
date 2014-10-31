package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yidianhulian.epiboly.Util;
import com.yidianhulian.framework.Api;
import com.yidianhulian.framework.CallApiTask;
import com.yidianhulian.framework.CallApiTask.CacheType;
import com.yidianhulian.framework.CallApiTask.CallApiListener;
import com.yidianhulian.framework.CallApiTask.FetchType;

public class GoodsList extends Fragment implements CallApiListener {

    private PullToRefreshListView listView;
    private ListRowAdapter adapter;
    List<ListRow> listRowArr = new ArrayList<ListRow>();

    private String search_api = "";
    private Map<String, String> search_params = new HashMap<String, String>();
    private String slug = "";
    // private Map<String, Integer> search_pagenos = new HashMap<String,
    // Integer>();
    private Map<String, List<ListRow>> tabDatas = new HashMap<String, List<ListRow>>();

    /**
     * author xialinchong 该参数是识别到底是上拉刷新、下拉刷新还是默认列表 up/down/none
     */
    public static final int PULL_UP = 1;
    public static final int PULL_DOWN = 2;
    public static final int PULL_NONE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.goods_list, null);
        listView = (PullToRefreshListView) view
                .findViewById(R.id.goods_list_view);
        listView.setMode(Mode.BOTH);

        adapter = new ListRowAdapter(this.getActivity(), listRowArr, listView,
                R.layout.goods_list_row);
        // 实现列表的显示
        listView.setAdapter(adapter);
        // 添加刷新
        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // if (search_pagenos.containsKey(slug)) {
                // search_pagenos.remove(slug);
                // }
                if (tabDatas.containsKey(slug) && tabDatas.get(slug).size() > 0) {
                    tabDatas.get(slug).clear();
                }
                search_params.put("pageno", "1");
                setContentDatas(search_api, PULL_DOWN, search_params, slug,
                        getActivity());
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                // Integer pageno = 0;
                // if (search_pagenos.containsKey(slug)) {
                // pageno = search_pagenos.get(slug);
                // pageno = pageno + 1;
                // } else {
                // pageno = 2;
                // }
                // search_pagenos.put(slug, pageno);
                /**
                 * author xialinchong
                 */
                String pageNo = getPageNo();
                Integer pageno = 0;
                if (pageNo.equals("")) {
                    pageno = 2;
                } else {
                    pageno = Integer.valueOf(pageNo) + 1;
                }
                
                search_params.put("pageno", pageno.toString());

                setContentDatas(search_api, PULL_UP, search_params, slug,
                        getActivity());
            }

        });

        // 添加点击
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(getActivity(),
                        GoodsDetailsActivity.class);
                intent.putExtra("id", listRowArr.get((int) arg3).getId());
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    /**
     * 
     * @param search_api
     *            api接口地址
     * @param search_params
     *            查询的参数
     * @param slug
     *            以后特殊情况时扩展用,栏目的标签,可用于判断当前栏目
     * @param activity
     *            author xialingchong: 该方法调用时可能还没有获取到activity对象，所以通过参数传入
     */
    public void setContentDatas(String search_api, int pullType,
            Map<String, String> search_params, String slug, Activity activity) {
        this.search_api = search_api;
        this.search_params = search_params;
        this.slug = slug;
        if (this.search_params != null
                && !this.search_params.containsKey("per_page")) {
            this.search_params.put("per_page", "20");
        }
        if (!tabDatas.containsKey(slug) || tabDatas.get(slug).size() == 0
                || (search_params.containsKey("pageno"))) {
            switch (pullType) {
            case PULL_UP:
                CallApiTask.doCallApi(PULL_UP, GoodsList.this, activity,
                        CacheType.APPEND, FetchType.FETCH_API);
                break;
            case PULL_DOWN:
                CallApiTask.doCallApi(PULL_DOWN, GoodsList.this, activity,
                        CacheType.REPLACE, FetchType.FETCH_API);
                break;
            case PULL_NONE:
            default:
                CallApiTask.doCallApi(PULL_NONE, GoodsList.this, activity,
                        CacheType.REPLACE, FetchType.FETCH_CACHE_ELSE_API);
                break;
            }
        } else {
            exchangeDatas();
            adapter.notifyDataSetChanged();
            Util.hideLoading();
        }
    }

    private void exchangeDatas() {
        listRowArr.clear();
        if (tabDatas.containsKey(slug)) {
            List<ListRow> row = tabDatas.get(slug);
            for (int i = 0; i < row.size(); i++) {
                ListRow old = row.get(i);
                ListRow newData = new ListRow(old.getId(), old.getGood_desc(),
                        old.getVendor_name(), old.getTime_interval(),
                        old.getCollect_qty(), old.getComment_qty(),
                        old.getLike_qty(), old.getWebView());
                listRowArr.add(newData);
            }
        }
    }

    private void storeDatas(List<ListRow> listRowArr) {
        List<ListRow> newList = new ArrayList<ListRow>();
        for (int i = 0; i < listRowArr.size(); i++) {
            ListRow old = listRowArr.get(i);
            ListRow newRow = new ListRow(old.getId(), old.getGood_desc(),
                    old.getVendor_name(), old.getTime_interval(),
                    old.getCollect_qty(), old.getComment_qty(),
                    old.getLike_qty(), old.getWebView());

            newList.add(newRow);
        }
        tabDatas.put(slug, newList);
    }

    @Override
    public Api getApi(int what, Object... params) {
        return new Api("get", search_api, search_params);
    }

    @Override
    public boolean isCallApiSuccess(JSONObject result) {
        if (result == null || !"ok".equals(Api.getJSONValue(result, "status")))
            return false;
        return true;
    }

    @Override
    public void apiNetworkException(Exception e) {
        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getCacheKey(int what, Object... params) {
        System.out.println("ffd");
        return search_api + slug + "pageNo";
    }

    @Override
    public void handleResult(int what, JSONObject result, boolean isDone,
            Object... params) {
        if (result == null) {
            Util.hideLoading();
            listRowArr.clear();
            adapter.notifyDataSetChanged();
            listView.onRefreshComplete();
            return;
        }
        try {
            JSONArray goods = (JSONArray) Api.getJSONValue(result, "goods");
            if (goods == null) {
                Util.hideLoading();
                return;
            }

            exchangeDatas();

            for (int i = 0; i < goods.length(); i++) {
                JSONObject jsonObj = (JSONObject) goods.get(i);

                String good_desc = Api.getJSONValue(jsonObj, "title")
                        .toString();
                String vendor_name = "";
                JSONArray jarr = (JSONArray) Api.getJSONValue(jsonObj, "shop");
                for (int j = 0; j < jarr.length(); j++) {
                    try {
                        vendor_name = vendor_name + " " + jarr.get(j);
                    } catch (JSONException e) {

                    }
                }
                String id = Api.getJSONValue(jsonObj, "id").toString();
                String scdate = Api.getJSONValue(jsonObj, "date").toString();
                String time_interval = Util.getTimeIntervalStr(scdate);
                String collect_qty = "收藏："
                        + Api.getJSONValue(jsonObj, "favor_count").toString();
                String comment_qty = "评论："
                        + Api.getJSONValue(jsonObj, "comment_count").toString();
                String like_qty = "值否："
                        + Api.getJSONValue(jsonObj, "rating_hots").toString();
                String webView = Api.getJSONValue(jsonObj, "thumbnail")
                        .toString();
                ListRow listRow = new ListRow(id, good_desc, vendor_name,
                        time_interval, collect_qty, comment_qty, like_qty,
                        webView);
                listRowArr.add(listRow);
            }

            storeDatas(listRowArr);

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * author xialinchong
         * 处理缓存
         */
        switch (what) {
        case PULL_UP:
            String pageNo = getPageNo();
            if (pageNo.equals("")) {
                setPageNo("2");
//                Util.setConfig(getActivity(), search_api + slug + "pageNo", "2");
            } else {
                setPageNo("" + (Integer.valueOf(pageNo) + 1) );
            }
            break;
        case PULL_DOWN:
        case PULL_NONE:
        default:
            setPageNo("1");
            break;
        }

        listView.onRefreshComplete();

        Util.hideLoading();
    }

    @Override
    public JSONObject appendResult(int what, JSONObject from, JSONObject to) {
        if (what == PULL_UP && from != null) {
            try {
                JSONArray toGoods = (JSONArray) Api.getJSONValue(to, "goods");
                JSONArray fromGoods = (JSONArray) Api.getJSONValue(from,
                        "goods");
                int fromGoodsLength = fromGoods.length();
                for (int i = 0; i < fromGoodsLength; i++) {
                    toGoods.put(fromGoods.get(i));
                }

                to.put("goods", toGoods);
                return to;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public JSONObject prependResult(int what, JSONObject from, JSONObject to) {
        return null;
    }
    
    /**
     * author xialinchong
     * @return 返回缓存页码
     */
    private String getPageNo() {
        return Util.getConfig(getActivity(), search_api + slug + "pageNo");
    }
    
    /**
     * author xialinchong
     * 设置缓存页码
     */
    private void setPageNo( String pageNo ) {
        Util.setConfig(getActivity(), search_api + slug + "pageNo" , pageNo);
    }
}