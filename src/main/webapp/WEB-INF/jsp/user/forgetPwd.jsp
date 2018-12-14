<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
    <title>找回密码</title>
</head>
<script type="text/javascript">
    $(document).ready(function () {
        //验证手机
        $("#getPwdBtn").click(function () {
            var cuTelNo = $("#cuTelNo").val();
            var captcha = $("#captcha").val();
            if (cuTelNo == null || cuTelNo == "" || cuTelNo.trim() == "") {
                $.toast("请输入手机号", "cancel");
                return false;
            }else if (captcha == null || captcha == "" || captcha.trim() == "") {
                $.toast("请输入验证码", "cancel");
                return false;
            }else{
                $.ajax({
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    url: "user/auth",
                    data: {telNo: $("#cuTelNo").val(), code: $("#captcha").val(),type:"password"},
                    success: function (json) {
                        if (json.success == true) {
                            window.location.href=CONST_BASE_PATH+"user/forgetPwdResetUI?telNo="+$("#cuTelNo").val()+"&code="+$("#captcha").val();
                        }else{
                            $.toast(json.msg, "cancel");
                            return false;
                        }
                    }
                });
            }
        });
        //发送验证码
        $("#getCaptcha").click(function () {
            var cuTelNo = $("#cuTelNo").val();
            if (cuTelNo == null || cuTelNo == "" || cuTelNo.trim() == "") {
                $.toast("请输入手机号", "cancel");
                return false;
            }else{
                $.showLoading();
                $.ajax({
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    url: "user/code",
                    data: {telNo: $("#cuTelNo").val(),type:"1"},
                    success: function (json) {
                        $.hideLoading();
                        if (json.success == true) {
                            $.toast("验证码已发送");
                        } else {
                            $.toast(json.msg, "cancel");
                        }
                    }
                });
            }
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
                <input type="text" placeholder="请输入您的手机号" name="cuTelNo" id="cuTelNo" autofocus/>
            </div>
            <div class="input_line">
                <label>验证码</label>
                <div>
                    <div class="input_flex">
                        <input type="text" placeholder="请输入验证码" name="captcha" id="captcha" style="width:168px;"/>
                        <a href="javascript:void(0);" class="input_requ" id="getCaptcha">获取验证码</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="input_btn_box">
            <div class="align_center">
                <input type="button" class="btn_frame" value="立即找回" id="getPwdBtn" style="width: 65%;height: 31px;background-color: #0ba4e3;border-radius: 15px;color: #fff;"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>