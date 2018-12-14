<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName();
    int port = request.getServerPort();
    if(port == 80){
        basePath = basePath + path + "/";
    }else{
        basePath = basePath + ":" + request.getServerPort() + path + "/";
    }

%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">

<script src="static/lib/jquery/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="static/js/wechatStyle.js" ></script>

<!-- jquery-weui-v1.0.1 start-->
<link rel="stylesheet" href="static/lib/jquery-weui-v1.0.1/lib/weui.min.css">
<link rel="stylesheet" href="static/lib/jquery-weui-v1.0.1/css/jquery-weui.css">
<link rel="stylesheet" href="static/lib/jquery-weui-v1.0.1/css/jquery-weui-custom.css?v=1">
<script src="static/lib/jquery-weui-v1.0.1/js/jquery-weui.js"></script>
<script src="static/lib/jquery-weui-v1.0.1/js/jquery-weui.min.js"></script>

<script src="static/lib/swiper/swiper.min.js"></script>
<link rel="stylesheet"  href="static/lib/swiper/swiper.min.css">

<link rel="stylesheet" type="text/css" href="static/css/style.css?_dt=20170631">


<script src="static/lib/jquery-weui-v1.0.1/lib/fastclick.js"></script>

<script>var CONST_BASE_PATH = "<%=basePath%>"</script>


<!-- 微信 js-sdk start -->
<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js" ></script>
<script type="text/javascript">
    (function($) {
        $.ajax({
            url : "getJsSdk",
            type : "POST",
            dataType: "json",
            data : {"url":location.href},
            success: function(o) {
                var jsApiList = [
                    //所有要调用的 API 都要加到这个列表中
                    "onMenuShareTimeline",//分享到朋友圈接口
                    "onMenuShareAppMessage",//分享给朋友接口
                    "onMenuShareQQ",//分享到qq接口
                    //"onMenuShareWeibo",//分享到腾讯微博
                    "onMenuShareQZone",//分享到QQ空间
                    "scanQRCode",//微信扫一扫
                    "hideMenuItems"//隐藏功能按钮接口
                ];
                wx.config({
                    debug: false,
                    appId: o.data.appId,
                    timestamp: o.data.timestamp,
                    nonceStr: o.data.nonceStr,
                    signature: o.data.signature,
                    jsApiList: jsApiList
                });

                wx.error(function(res){
//                    console.log("调用获取js-sdk的失败,"+res);
                });

            },
            error: function(xhr,e,t) {
                //alert(e);
            }
        });
    })(jQuery);

    $(function() {
        FastClick.attach(document.body);
    });
</script>
<!-- 微信 js-sdk end -->


<script>

    $(document).ready(function () {
        // 获取js-sdk需要的信息
//        var url = location.href;
//        $.ajax({
//            type: "GET",
//            cache: false,
//            dataType: "json",
//            url: "getJsSdk",
//            data: {url:url},
//            success: function (json) {
//                if(json.success){
//                    wx.config({
//                        debug: false,
//                        appId: json.data.appId,
//                        timestamp: json.data.timestamp,
//                        nonceStr: json.data.nonceStr,
//                        signature: json.data.signature,
//                        jsApiList: ['closeWindow']
//                    });
//                }else{
//                    console.log("调用获取js-sdk的失败");
//                }
//            }
//        });
    })
</script>