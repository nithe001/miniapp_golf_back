<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <jsp:include page="include/commonInclude.jsp"></jsp:include>
    <title>403</title>
    <link rel="stylesheet" href="static/lib/jqweui-1.0.1/css/weui.min.css">
    <link rel="stylesheet" href="static/lib/jqweui-1.0.1/css/jquery-weui.min.css">
    <style>
        /**由于自定义的css中html的font-size被jquery-weui中的覆盖，导致防止rem不正确，所以重置*/
        html{ font-size: 62.5% }
    </style>
</head>
<body >
<div class="weui-msg">
    <div class="weui-msg__icon-area"><i class="weui-icon-warn weui-icon_msg"></i></div>
    <div class="weui-msg__text-area">
        <h2 class="weui-msg__title">403</h2>
        <p class="weui-msg__desc">抱歉，您没有访问权限。</p>
    </div>
</div>
</body>
</html>