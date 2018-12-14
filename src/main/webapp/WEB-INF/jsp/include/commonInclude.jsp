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
<meta charset="UTF-8">
<meta name="renderer" content="webkit">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta name="keywords" content="北京心血管疾病防治研究会,心血管疾病,心血管,BCA">
<meta name="description" content="北京心血管疾病防治研究会，由北京行政区划内从事心血管学科研究和实践的医疗及相关领域的研究机构、医疗机构、社会组织、企事业单位和专家学者自愿联合发起成立的营利性社会团体。英文名称为Beijing Cardiovascular Disease Prevention&Treatment Association，缩写为BCA">

<link rel="stylesheet"  href="static/lib/swiper/swiper.min.css">
<link rel="stylesheet" href="static/css/pc.css?_dt=20170630">
<script src="static/lib/jquery/jquery-1.12.4.min.js"></script>
<script src="static/lib/swiper/swiper.min.js"></script>
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
<style>
    body{
        min-width: 1002px;
    }
</style>