package com.yidianhulian.epiboly.zzz;

import org.json.JSONObject;

public class ListRow {

    private String id;
    private String good_id;
    private String good_desc;
    private String vendor_name;
    private String time_interval;
    private String collect_qty;
    private String comment_qty;
    private String like_qty;
    private String webView;
    private JSONObject goodData;
    private String code_in;

    public ListRow(String id, String good_desc, String webView,
            JSONObject goodData) {
        this.id = id;
        this.good_desc = good_desc;
        this.webView = webView;
        this.goodData = goodData;

    }

    public ListRow(String id, String good_id, String good_desc, String webView,
            String code_in) {
        this.id = id;
        this.good_id = good_id;
        this.good_desc = good_desc;
        this.webView = webView;
        this.code_in = code_in;
    }

    public ListRow(String id, String good_desc, String vendor_name,
            String time_interval, String collect_qty, String comment_qty,
            String like_qty, String webView) {
        this.id = id;
        this.good_desc = good_desc;
        this.vendor_name = vendor_name;
        this.time_interval = time_interval;
        this.collect_qty = collect_qty;
        this.comment_qty = comment_qty;
        this.like_qty = like_qty;
        this.webView = webView;
    }

    public String getId() {
        return id;
    }

    public String getGood_id() {
        return good_id;
    }

    public void setGood_id(String good_id) {
        this.good_id = good_id;
    }

    public String getGood_desc() {
        return good_desc;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public String getTime_interval() {
        return time_interval;
    }

    public String getCollect_qty() {
        return collect_qty;
    }

    public String getComment_qty() {
        return comment_qty;
    }

    public String getLike_qty() {
        return like_qty;
    }

    public String getWebView() {
        return webView;
    }

    public JSONObject getGoodData() {
        return goodData;
    }

    public void setGoodData(JSONObject goodData) {
        this.goodData = goodData;
    }

    public String getCode_in() {
        return code_in;
    }

    public void setCode_in(String code_in) {
        this.code_in = code_in;
    }

}
