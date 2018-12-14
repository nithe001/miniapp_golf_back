<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String navId = request.getParameter("navi");
%>

<div class="top_line"></div>
<div class="logo_box">
    <img src="static/images/logo1.jpg">
</div>
<div class="nav_box">
    <ul>
        <li id="navi_index"><a href="/" class="index_icon">首页</a></li>
        <li id="navi_introduce"><a href="introduce">协会介绍</a></li>
        <li id="navi_notice"><a href="notice">入会须知</a></li>
        <li id="navi_activities"><a href="activitiespc">学术活动</a></li>
        <li id="navi_contact"><a href="contact">联系我们</a></li>
    </ul>
</div>


<script type="text/javascript">
    var navId = "<%=navId%>";
    $("#navi_" + navId).addClass("active");
</script>