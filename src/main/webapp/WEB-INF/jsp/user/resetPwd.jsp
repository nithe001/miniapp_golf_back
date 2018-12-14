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
    <title>重置密码</title>
</head>
<style>
    .input_pwd {
        -webkit-flex: 1;
        height: 32px;
        border: 1px solid #dcdcdc;
        box-sizing: border-box;
        padding: 0 8px;
        display: block;-webkit-box-flex:1;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
        $("#resetPwdBtn").click(function () {
            var cuPassword = $("#cuPassword").val();
            if(cuPassword == null || cuPassword == "" || cuPassword.trim() == ""){
                $.toast("请输入密码", "cancel");
                return false;
            }
            var cuPasswordConf = $("#cuPasswordConf").val();
            if (cuPassword != cuPasswordConf) {
                $.toast("两次密码不一致", "cancel");
                return false;
            }
            $.ajax({
                type: "POST",
                cache: false,
                dataType: "json",
                url: "user/forgetPwdReset",
                data: {password:$("#cuPassword").val(),telNo: $("#telNo").val(),code:$("#code").val()},
                success: function (json) {
                    if (json.success == true) {
                        $.toast("重置成功,3秒后跳转登录页");
                        setTimeout(function() {
                            window.location.href=CONST_BASE_PATH+"user/loginInit";
                        }, 3000);
                    } else {
                        $.toast(json.msg, "cancel");
                    }
                }
            });
        });
    });
</script>
<body>
<input type="hidden" id="telNo" value="${telNo}"/>
<input type="hidden" id="code" value="${code}"/>
<div class="wrapper_flex">
    <div class="wrapper">
        <div class="logo_img">
            <img src="static/wechatImages/login_logo.jpg">
        </div>
        <div class="input_box">
            <div class="input_line">
                <label>密码</label>
                <input placeholder="请输入您的密码" name="cuPassword" type="password" id="cuPassword"/>
            </div>
            <div class="input_line">
                <label>确认密码</label>
                <input placeholder="请再次输入密码" type="password" id="cuPasswordConf"/>
            </div>
        </div>
        <div class="input_btn_box">
            <div class="align_center">
                <input type="button" class="btn_frame" value="保存" id="resetPwdBtn" style="width: 65%;height: 31px;background-color: #0ba4e3;border-radius: 15px;color: #fff;"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>