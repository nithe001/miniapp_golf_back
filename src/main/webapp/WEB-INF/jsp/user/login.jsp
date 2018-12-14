<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
	<title>登录</title>
</head>
<style>
    .input_pwd{-webkit-flex:1;height: 32px;border: 1px solid #dcdcdc;box-sizing: border-box;padding: 0 8px;}
</style>
<script type="text/javascript">
    $(document).ready(function () {
        $("#guest").click(function () {
            $("#coverBox").show();
            $("#box").show();
        });
        $('#closeBox').click(function () {
            $("#coverBox").hide();
            $("#box").hide();
//            window.location.href=CONST_BASE_PATH+"news/bannerUnscramble"
        });

        $("#loginBtn").click(function () {
            //校验
            var cuTelNo = $("#cuTelNo").val();
            if(cuTelNo == null || cuTelNo == "" || cuTelNo.trim() == ""){
                $.toast("请输入手机号", "cancel");
                return false;
            }
            var cuPassword = $("#cuPassword").val();
            if(cuPassword == null || cuPassword == "" || cuPassword.trim() == ""){
                $.toast("请输入密码", "cancel");
                return false;
            }

            $.ajax({
                type: "POST",
                cache: false,
                dataType: "json",
                url: "user/login",
                data: {telNo: $("#cuTelNo").val(), password: $("#cuPassword").val()},
                success: function (json) {
                    if (json.success == true) {
                        window.location.href=CONST_BASE_PATH+"user/userManage";
                    }else{
                        $.toast(json.msg, "cancel");
                    }
                }
            });
        });
    });
</script>
<body>
<div class="wrapper_flex">
    <div class="wrapper">
        <div class="logo_img">
            <img src="static/wechatImages/login_logo.jpg">
        </div>
        <div class="input_box">
            <div class="input_line">
                <label>手机</label>
                <input type="text" placeholder="请输入您的手机号" name="cuTelNo" id="cuTelNo"/>
            </div>
            <div class="input_line">
                <label>密码</label>
                <input type="password" placeholder="请输入您的密码" name="cuPassword" id="cuPassword"/>
            </div>
        </div>
        <div class="input_btn_box">
            <div class="align_center">
                <input type="button" class="btn_frame" value="登录" id="loginBtn" style="width: 65%;height: 31px;background-color: #0ba4e3;border-radius: 15px;color: #fff;"/>
            </div>
            <div class="align_center">
                <a href="user/forgetPwdUI" class="color_sha">忘记密码</a><span class="margi">？</span>|
                <a href="javascript:void(0);" class="color_deep" id="guest">我是游客</a>
            </div>
            <div class="align_center">
                没有账号？
                <a href="/user/registerInit" class="color_sha">立即注册</a>
                <span class="color_sha">>></span>
            </div>
        </div>
    </div>
</div>
<div class="cover1" id="coverBox" style="display:none;"></div>
<div class="login_pop" id="box" style="display:none;">
    <div class="pop_close" id="closeBox">
        <img src="static/wechatImages/pop_close.png">
    </div>
    <div class="pop_img">
        <img src="static/wechatImages/pop_light.png">
    </div>
    <div class="align_center color_blue">
        游客只可以看到部分信息，<br>观看内容有限制。
    </div>
    <div class="align_center">
        点击注册，成为会员，可进行咨询，<br>
        观看活动信息，观看专家PPT内容哟！
    </div>
</div>
</body>
</html>