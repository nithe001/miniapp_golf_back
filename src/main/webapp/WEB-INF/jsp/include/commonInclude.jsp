<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
//    String basePath = request.getScheme()+"://"+request.getServerName();
    String basePath = request.getHeader("X-Forwarded-Scheme")+"://"+request.getServerName();
    basePath = basePath + path + "/";
    /*int port = request.getServerPort();
    if(port == 80){
        basePath = basePath + path + "/";
    }else{
        basePath = basePath + ":" + request.getServerPort() + path + "/";
    }*/
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta name="renderer" content="webkit">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">

<script src="static/lib/jquery/jquery-1.12.4.min.js"></script>
<script>var CONST_BASE_PATH = "<%=basePath%>"</script>

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