<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <script src="static/lib/jQuery-File-Upload-9.9.3/js/vendor/jquery.ui.widget.js"></script>
    <script src="static/lib/jQuery-File-Upload-9.9.3/js/jquery.iframe-transport.js"></script>
    <script src="static/lib/jQuery-File-Upload-9.9.3/js/jquery.fileupload.js"></script>
    <script type="text/javascript" src="static/js/register.js"></script>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
    <title>修改个人信息</title>
</head>
<style>
    .input_pwd {
        -webkit-flex: 1;
        height: 32px;
        border: 1px solid #dcdcdc;
        box-sizing: border-box;
        padding: 0 8px;
    }
    input[type="file"]{width: 60px;height: 60px;position: absolute;top: 0;left: 0;opacity: 0;}
    .head_port_new{width: 98px;height: 98px;border: 2px solid #0ba4e3;box-shadow: 0 0 4px #98c2e2;
                position: absolute;bottom: 0;left: 50%;margin-left: -49px;display: -webkit-flex;align-items: center;justify-content: center;}
</style>
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
                window.location.href ="user/userDetail";
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
<script type="text/javascript">
$(document).ready(function () {
    var level = $("#cuHospitalLevel_select").val();
    if(level && level!="一级" && level!="二级" && level!="三级"){
        $("#cuHospitalLevel_select").attr("style","width:55%");
        $("#cuHospitalLevel_other").attr("type","text");
        $("#cuHospitalLevel_other").attr("name","cuHospitalLevel");
        $("#cuHospitalLevel_select").removeAttr("name");
        $("#cuHospitalLevel_select").val("其他（手填）");
    }

    var dept = $("#cuDept_select").val();
    if(dept && dept!="心内科" && dept!="心外科" && dept!="介入科" && dept!="影像科" && dept!="急诊科" && dept!="麻醉科") {
        $("#cuDept_select").attr("style","width:55%");
        $("#cuDept_other").attr("type","text");
        $("#cuDept_other").attr("name","cuDept");
        $("#cuDept_select").removeAttr("name");
        $("#cuDept_select").val("其他（手填）");
    }

    $(".selectHospital").select({
        title: "选择医院等级",
        items: ["三级", "二级", "一级", "其他（手填）"],
        onClose: function() {
            var cuHospitalLevel_select = $("#cuHospitalLevel_select").val();
            if ("其他（手填）" == cuHospitalLevel_select) {
                $("#cuHospitalLevel_other").attr("type", "text");
                $("#cuHospitalLevel_select").attr("style", "width:55%");
                $("#cuHospitalLevel_other").val("");
            } else {
                $("#cuHospitalLevel_other").attr("type", "hidden");
                $("#cuHospitalLevel_other").val(cuHospitalLevel_select);
                $("#cuHospitalLevel_select").removeAttr("style", "width:55%");
            }
            console.log("close");
        }
    });
    $(".selectDept").select({
        title: "选择科室",
        items: ["心内科", "心外科", "介入科", "影像科", "急诊科", "麻醉科", "其他（手填）","1","2"],
        onClose: function() {
            var cuDept_select = $("#cuDept_select").val();
            if("其他（手填）" == cuDept_select){
                $("#cuDept_other").attr("type","text");
                $("#cuDept_select").attr("style","width:55%");
                $("#cuDept_other").val("");
            }else{
                $("#cuDept_other").attr("type","hidden");
                $("#cuDept_other").val(cuDept_select);
                $("#cuDept_select").removeAttr("style");
            }
            console.log("close");
        }
    });
    $("#cuProfessional").select({
        title: "选择职称",
        items: ["高级", "副高级", "中级", "初级"]
//        autoClose:true
    });
});
</script>
<body>
<div class="wrapper_flex">
    <div class="wrapper">
        <form action="user/updateUser" method="post" onsubmit="return beforeSubmit(1);">
            <div class="logo_img">
                <img src="static/wechatImages/regis_top.jpg">
                <div class="head_port_new">
                    <input name="file" id="fileupload" type="file" accept="image/*" style="z-index: 2;"/>
                    <img id="headImg" style="width:98px;height: 98px;"
                        <c:if test="${user.cuHeadimg == null || user.cuHeadimg == ''}">src="static/wechatImages/head.jpg"</c:if>
                        <c:if test="${user.cuHeadimg != null && user.cuHeadimg != ''}">src="${user.cuHeadimg}"</c:if>
                    />
                </div>
            </div>
            <div class="input_box regis_box">
                <div class="input_line">
                    <p style="margin-left:35%;">点击头像进行修改</p>
                </div>
                <div class="input_line">
                    <label>姓名</label>
                    <input type="hidden" name="cuHeadimg" id="cuHeadimg" value="${user.cuHeadimg}"/>
                    <input type="hidden" name="cuId" id="cuId" value="${user.cuId}"/>
                    <input type="text" placeholder="请输入您的姓名" name="cuUserName" id="cuUserName" value="${user.cuUserName}"/>
                </div>
                <div class="input_line">
                    <label>性别</label>
                    <div>
                        <input type="radio" name="cuSex" id="cuSex_1" <c:if test="${user.cuSex =='男'}">checked</c:if> value="男"/>
                        <label>男</label>
                        <input type="radio" name="cuSex" id="cuSex_2" <c:if test="${user.cuSex =='女'}">checked</c:if> value="女"/>
                        <label>女</label>
                    </div>
                </div>
                <div class="input_line">
                    <label>手机</label>
                    <input type="text" name="cuTelNo" id="cuTelNo" value="${user.cuTelNo}" readonly style="background-color:#dcdcdc;"/>
                </div>
                <div class="input_line">
                    <label>密码</label>
                    <input placeholder="请输入您的密码" name="cuPassword" type="password" id="cuPassword"/>
                </div>
                <div class="input_line">
                    <label>确认密码</label>
                    <input placeholder="请再次输入密码" type="password" id="cuPasswordConf"/>
                </div>
                <div class="input_line">
                    <label>邮箱</label>
                    <input placeholder="请输入您的邮箱" name="cuEmail" type="text" id="cuEmail" value="${user.cuEmail}"/>
                </div>
                <div class="input_line" id="select">
                    <label>医院等级</label>
                    <input type="text" placeholder="请选择您的医院等级" class="selectHospital" id="cuHospitalLevel_select" value="${user.cuHospitalLevel}"/>
                    <input type="hidden" placeholder="请输入医院等级" name="cuHospitalLevel" id="cuHospitalLevel_other" value="${user.cuHospitalLevel}" style="width:70%;"/>
                </div>
                <div class="input_line">
                    <label>医院名称</label>
                    <input type="text" placeholder="请输入您的医院" name="cuHospital" id="cuHospital" value="${user.cuHospital}"/>
                </div>
                <div class="input_line">
                    <label>科室</label>
                    <input type="text" placeholder="请选择您的科室" class="selectDept" id="cuDept_select" value="${user.cuDept}"/>
                    <input type="hidden" placeholder="请输入科室" name="cuDept" id="cuDept_other" value="${user.cuDept}" style="width:70%;"/>
                </div>
                <div class="input_line">
                    <label>职称</label>
                    <input type="text" placeholder="请选择您的职称" name="cuProfessional" id="cuProfessional" value="${user.cuProfessional}"/>
                </div>
            </div>
            <div class="input_btn_box">
                <div class="align_center">
                    <input type="submit" class="btn_frame" value="保存"/>
                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>