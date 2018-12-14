<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
    <title>基本信息</title>
</head>
<script>
    $(function(){
        pushHistory();
        var bool=false;
        setTimeout(function(){
            bool=true;
        },500);
        window.addEventListener("popstate", function(e) {  //回调函数中实现需要的功能
            if(bool){
//                alert("我监听到了浏览器的返回按钮事件啦");//根据自己的需求实现自己的功能
                window.location.href ="user/userManage";
            }
            pushHistory();
        }, false);
    });
    function pushHistory() {
        var state = {
            title: "title",
            url: window.location.href
        };
        window.history.pushState(state, state.title, state.url);
    }
</script>
<body>
<div class="wrapper_flex">
    <div class="wrapper bg_grey">
        <div class="member_list">
            <div class="member_line basic_line">
                <div class="basic_left">姓名</div>
                <div class="basic_right">${user.cuUserName}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">性别</div>
                <div class="basic_right">${user.cuSex}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">我的邮箱</div>
                <div class="basic_right">${user.cuEmail}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">手机</div>
                <div class="basic_right">${user.cuTelNo}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">医院等级</div>
                <div class="basic_right">${user.cuHospitalLevel}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">医院名称</div>
                <div class="basic_right">${user.cuHospital}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">科室</div>
                <div class="basic_right">${user.cuDept}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">职称</div>
                <div class="basic_right">${user.cuProfessional}</div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">会员等级</div>
                <div class="basic_right">
                    <c:if test="${user.cuType==1}">理事会员</c:if>
                    <c:if test="${user.cuType==2}">委员会员</c:if>
                    <c:if test="${user.cuType==3}">普通用户</c:if>
                </div>
            </div>
            <div class="member_line basic_line">
                <div class="basic_left">所属俱乐部</div>
                <div class="basic_right">
                    <c:if test="${user.cuClub==null}">无</c:if>
                    <c:if test="${user.cuClub==1}">益心论道</c:if>
                    <c:if test="${user.cuClub==2}">心脏内外科医师沙龙</c:if>
                    <c:if test="${user.cuClub==3}">北京青年CTO俱乐部</c:if>
                    <c:if test="${user.cuClub==4}">女医师俱乐部</c:if>
                </div>
            </div>
        </div>
        <div class="send_box">
            <a href="user/editUserInfoUI" class="send_btn">修改个人信息</a>
        </div>
    </div>
</div>
</body>
</html>