<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
    <!--点击返回按钮关闭窗口-->
    <script type="text/javascript" src="static/js/closeWxWindow.js" ></script>
    <title>会员管理</title>
</head>
<body>
<input type="hidden" id="edit" value="${edit}"/>
<div class="wrapper_flex">
    <div class="wrapper bg_grey">
        <div class="member_list">
            <div class="member_line member_top">
                <div class="member_head">
                    <img
                         <c:if test="${user.cuHeadimg == null || user.cuHeadimg == ''}">src="static/wechatImages/head.jpg"</c:if>
                         <c:if test="${user.cuHeadimg != null}">src="${user.cuHeadimg}"</c:if>
                    />
                </div>
                <div class="member_men">
                    <div>
                        <p>${user.cuUserName}</p>
                        <p>
                            <c:if test="${user.cuType==1}">理事会员</c:if>
                            <c:if test="${user.cuType==2}">委员会员</c:if>
                            <c:if test="${user.cuType==3}">普通用户</c:if>
                        </p>
                    </div>
                    <div>
                        <p>${user.cuProfessional}</p>
                        <p>
                            <c:if test="${user.cuClub==1}">益心论道</c:if>
                            <c:if test="${user.cuClub==2}">心内外沙龙</c:if>
                            <c:if test="${user.cuClub==3}">青年CTO俱乐部</c:if>
                            <c:if test="${user.cuClub==4}">女医师俱乐部</c:if>
                            <c:if test="${user.cuClub==5}">大繁至简</c:if>
                        </p>
                    </div>
                </div>
            </div>
            <a href="user/userDetail" class="member_line">
                <div class="member_icon">
                    <img src="static/wechatImages/member_icon1.png">
                </div>
                <div class="member_center">
                    基本信息
                </div>
                <div class="member_next">
                    <img src="static/wechatImages/next.png">
                </div>
            </a>
            <a href="consult/bannerUnscramble?type=1" class="member_line">
                <div class="member_icon">
                    <img src="static/wechatImages/member_icon2.png">
                </div>
                <div class="member_center">
                    我的咨询
                </div>
                <div class="member_next">
                    <img src="static/wechatImages/next.png">
                </div>
            </a>
            <a href="activities/calendarList?clubType=2" class="member_line">
                <div class="member_icon">
                    <img src="static/wechatImages/member_icon3.png">
                </div>
                <div class="member_center">
                    我的精彩活动
                </div>
                <div class="member_next">
                    <img src="static/wechatImages/next.png">
                </div>
            </a>
        </div>
    </div>
</div>
</body>
</html>