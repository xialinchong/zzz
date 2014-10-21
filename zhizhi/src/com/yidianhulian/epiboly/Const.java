package com.yidianhulian.epiboly;


/**
 * 系统中用到的一些常量定义，统一放到这里
 * 
 * @author leeboo
 *
 */
public class Const {
	/**
	 * 配置的保存文件名
	 */
	public static String PREF_FILE_NAME = "sys_pref";
	
	
	/**
	 * 主机域名地址
	 */
	public final static String HOST = "http://www.zhizhizhi.com/";
	
	/**
	 * 取栏目分类信息列表
	 */
	public final static String GET_TAB_LIST = HOST + "api/goods/get_tab_list";
	/**
	 * 取分类信息列表
	 */
	public final static String GET_GOODS_CATEGORY_LIST = HOST + "api/goods/get_goods_category_list";
	/**
	 * 取出“优惠”栏目 下的最新信息
	 */
	public final static String GET_RECENT_GOODS = HOST + "api/goods/get_recent_goods";
	
	/**
	 * 取栏目下的最新优惠文章
	 */
	public final static String GET_RECENT_GOODS_BY_CHANNEL = HOST + "api/goods/get_recent_goods_by_channel";
	/**
	 * 取指定优惠的详细信息
	 */
	public final static String GET_GOOD = HOST + "api/goods/get_good";
	/**
	 * 取文章评论
	 */
	public final static String GET_COMMENTS = HOST + "api/goods/get_comments";	
	/**
	 * 提交评论
	 */
	public final static String SUBMIT_COMMENT = HOST + "api/goods/submit_comment";
	/**
	 * 收藏商品
	 */
	public final static String FAVOR_GOOD = HOST + "api/goods/favor_goods";
	/**
	 * 加热商品
	 */
	public final static String HEAT_GOOD = HOST + "api/goods/heat_good";
	/**
	 * 领取优惠券
	 */
	public final static String GET_COUPON = HOST + "api/goods/draw_coupon";
	/**
	 * 查询
	 */
	public final static String GET_SEARCH_RESULTS = HOST + "api/goods/get_search_results";
	
	/**
	 * 读取用户优惠券列表
	 */
	public final static String GET_USER_COUPONS = HOST + "api/goods/get_user_coupons";
	
	/**
	 * 用户移除优惠券
	 */
	public final static String REMOVE_COUPON = HOST + "api/goods/remove_coupon";
	
	/**
	 * 读取用户收藏列表	
	 */
	public final static String GET_FAVORITE_GOODS = HOST + "api/goods/get_favorite_goods";
	
	/**
	 * 用户移除收藏
	 */
	public final static String REMOVE_FAVORITE_GOODS = HOST + "api/goods/remove_favorite_goods";
	
	/**
	 * 获取用户系统设置
	 */
	public final static String GET_USER_SETTING = HOST + "api/goods/get_user_setting";
	
	/**
	 * 保存用户系统设置
	 */
	public final static String SAVE_USER_SETTING = HOST + "api/goods/save_user_setting";
	
	/**
	 * 用户注册
	 */
	public final static String USER_REGISTER = HOST + "api/goods/user_register";
	
	/**
	 * 用户登录
	 */
	public final static String USER_LOGIN = HOST + "api/goods/user_login";
	
	/**
	 * 忘记密码
	 */
	public final static String USER_LOST_PASSWORD = HOST + "api/goods/user_lost_password";
	
	/**
	 * 获取nonce
	 */
	public final static String GET_NONCE = HOST + "api/goods/get_nonce";
	
	/**
	 * 检查新版本
	 */
	public final static String CHECK_DATA_UPDATES = HOST + "api/goods/check_app_updates";
	
	/**
	 * 第三方登录
	 */
	public final static String USER_LOGIN_SNS = HOST + "api/goods/user_login_sns";
	
	/**
	 * 上传设备token
	 */
	public final static String UPLOAD_DEVICE_TOKEN = HOST + "api/goods/upload_device_token";
}
