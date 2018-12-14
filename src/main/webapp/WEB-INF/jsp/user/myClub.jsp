<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
    <title>我的日历</title>
</head>
<div class="wrapper_flex">
    <div class="wrapper bg">
        <div class="cal_list">
            <a href="activities/activitiesDetail?type=1" class="cal_line">
                <img src="static/wechatImages/cal_icon.png">
                益心论道
            </a>
            <a href="activities/activitiesDetail?type=2" class="cal_line">
                <img src="static/wechatImages/cal_icon.png">
                心内外沙龙
            </a>
            <a href="activities/activitiesDetail?type=3" class="cal_line">
                <img src="static/wechatImages/cal_icon.png">
                青年CTO俱乐部
            </a>
            <a href="activities/activitiesDetail?type=4" class="cal_line">
                <img src="static/wechatImages/cal_icon.png">
                北京中青年女介入医师俱乐部
            </a>
        </div>
    </div>
</div>
</body>
</html>