<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<jsp:include page="include/commonInclude.jsp"></jsp:include>
	<title>出错了~</title>	
	<style type="text/css">
		.hei_auto{ height:auto;}
		.wrong_page{ width:1000px; height:494px; margin:184px auto 0; text-align:center; color:#666; font-size:16px; line-height:26px;}
		.wrong_page h2{ margin-top:33px; font-size:36px; line-height:106px;}
		.color_099{ color:#099;}		
	</style>	
</head>
<body class="hei_auto">
	<div class="wrong_page">
		<h2><span class="orange">抱歉</span>，出现错误了，请稍后再试。</h2>
		<p><span id="time">3</span>秒后自动跳转到<a class="color_099" href="admin/index">首页</a></p>
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
	countdown(3,'admin/index');
</script>
</html>