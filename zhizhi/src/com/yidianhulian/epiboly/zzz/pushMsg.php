<?php
require_once ('XingeApp.php');
define ( "XG_ACCESS_ID", 2100050752 );
define ( "XG_SECRET_KEY", "c5f16b0f49433cdff29118abf718211a" );

// 下发所有设备
function DemoPushAllDevices($title, $content, $good_id) {
    $push = new XingeApp ( XG_ACCESS_ID, XG_SECRET_KEY );
    $mess = new Message ();
    $mess->setExpireTime ( 86400 );
    $mess->setTitle ( $title );
    $mess->setContent ( $content );
    // YPE_MESSAGE
    $mess->setType ( Message::TYPE_NOTIFICATION );
    
    $style = new Style ( 0 );
    // 义：样式编号0，响铃，震动，不可从通知栏清除，不影响先前通知
    $style = new Style ( 0, 1, 1, 1, 0 );
    $action = new ClickAction ();
    $action->setActionType ( ClickAction::TYPE_ACTIVITY );
    $action->setActivity("com.yidianhulian.epiboly.zzz.GoodsDetailsActivity");
    $custom = array ( 'id' => $good_id );
    $mess->setStyle ( $style );
    $mess->setAction ( $action );
    $mess->setCustom ( $custom );
    $acceptTime1 = new TimeInterval ( 0, 0, 23, 59 );
    $mess->addAcceptTime ( $acceptTime1 );
    // 默认，1表示推送生产环境，2表示推送开发环境,ios必须，对android不起用
    $ret = $push->PushAllDevices ( 0, $mess, 1 );
    switch ($ret ['ret_code']) {
        case 0 :
            return "接口调用成功";
        case - 1 :
            return "参数错误";
        case - 2 :
            return "请求时间戳不在有效期内";
        case - 3 :
            return "检查access_id和secret_key（注意不是access_key）";
        case 2 :
            return "参数错误";
        case 7 :
            return "别名/账号绑定的终端数满了（10个）";
        case 14 :
            return "收到非法token";
        case 15 :
            return "信鸽逻辑服务器繁忙";
        case 19 :
            return "操作时序错误";
        case 20 :
            return "鉴权错误";
        case 40 :
            return "推送的token没有在信鸽中注册";
        case 48 :
            return "推送的账号没有在信鸽中注册";
        case 63 :
            return "标签系统忙";
        case 71 :
            return "APNS服务器繁忙";
        case 73 :
            return "消息字符数超限";
        case 76 :
            return "请求过于频繁，请稍后再试";
        case 100 :
            return "APNS证书错误，重新提交正确的证书";
        default :
            return "内部错误";
    }
}

// 单个设备下发通知消息
function PushSingleDeviceNotification($deviceToken, $title, $content) {
    $push = new XingeApp ( XG_ACCESS_ID, XG_SECRET_KEY );
    $mess = new Message ();
    $mess->setType ( Message::TYPE_NOTIFICATION );
    $mess->setTitle ( $title );
    $mess->setContent ( $content );
    $style = new Style ( 0 );
    // 义：样式编号0，响铃，震动，不可从通知栏清除，不影响先前通知
    $style = new Style ( 0, 1, 1, 1, 0 );
    $action = new ClickAction ();
    $action->setActionType ( ClickAction::TYPE_ACTIVITY );
    // action->setUrl("http://xg.qq.com");
    // 开url需要用户确认
    // action->setComfirmOnUrl(1);
    $custom = array (
            'key1' => 'value1',
            'key2' => 'value2' 
    );
    $mess->setStyle ( $style );
    $mess->setAction ( $action );
    $mess->setCustom ( $custom );
    $acceptTime1 = new TimeInterval ( 0, 0, 23, 59 );
    $mess->addAcceptTime ( $acceptTime1 );
    // 默认，1表示推送生产环境，2表示推送开发环境,ios必须，对android不起用
    $ret = $push->PushSingleDevice ( $deviceToken, $mess, 1 );
    switch ($ret ['ret_code']) {
        case 0 :
            return "接口调用成功";
        case - 1 :
            return "参数错误";
        case - 2 :
            return "请求时间戳不在有效期内";
        case - 3 :
            return "检查access_id和secret_key（注意不是access_key）";
        case 2 :
            return "参数错误";
        case 7 :
            return "别名/账号绑定的终端数满了（10个）";
        case 14 :
            return "收到非法token";
        case 15 :
            return "信鸽逻辑服务器繁忙";
        case 19 :
            return "操作时序错误";
        case 20 :
            return "鉴权错误";
        case 40 :
            return "推送的token没有在信鸽中注册";
        case 48 :
            return "推送的账号没有在信鸽中注册";
        case 63 :
            return "标签系统忙";
        case 71 :
            return "APNS服务器繁忙";
        case 73 :
            return "消息字符数超限";
        case 76 :
            return "请求过于频繁，请稍后再试";
        case 100 :
            return "APNS证书错误，重新提交正确的证书";
        default :
            return "内部错误";
    }
}
?>