<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<jsp:include page="include/commonInclude.jsp"></jsp:include>
	<title>出错了</title>
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
            <h2 class="weui-msg__title">出错了</h2>
            <p class="weui-msg__desc"><h2>抱歉，出现错误了，请稍后再试。</h2></p>
            <%--<c:choose>--%>
                <%--<c:when test="${errMsg == null || errMsg == ''}">--%>
                    <%--<p class="weui-msg__desc"><h2><span class="orange">抱歉</span>，出现错误了，请稍后再试。</h2></p>--%>
                <%--</c:when>--%>
                <%--<c:otherwise>--%>
                    <%--<p class="weui-msg__desc"><h2><span class="orange">抱歉</span>，出现错误了。错误内容为：${errMsg}</h2></p>--%>
                <%--</c:otherwise>--%>
            <%--</c:choose>--%>
        </div>
    </div>
</body>
<script>
	function countdown(secs, surl){
		var time = document.getElementById("time");
		time.innerText = secs;//<span>中显示的内容值
		if(--secs>0){
	       setTimeout("countdown("+secs+",'"+surl+"')",1000);//设定超时时间
	    }else{
	       location.href=surl;//跳转页面
	    } 
	}
//	countdown(3,'index');
</script>
</html>