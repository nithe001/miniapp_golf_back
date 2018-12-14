$(document).ready(function(){
    $('#fileupload').fileupload({
        type: "POST",
        cache:false,
        async: false, //同步，，即此代码执行时，其他的不可执行。
        dataType: "json",
        url: 'upload/uploadHeadImg',
        add: function (e, data) {
            $.showLoading();
            setTimeout(function() {
                $("#coverBox").hide();
                data.submit();
            }, 2000);
        },
        success: function(json) {
            if (json.success) {
                var array = json.data;
                $("#headImg").attr("src", array);
                $("#headImg").show();
                $("#cuHeadimg").val(array);
                $.hideLoading();
                // $.toast("上传成功");
            }else{
                $.toast("上传图片过程中有错误发生，请稍后再试。", "cancel");
            }
        }
    });
    //发送验证码
    $("#getCaptcha").click(function () {
        if($("#cuTelNo").val()){
            $.showLoading();
            $.ajax({
                type: "POST",
                cache: false,
                dataType: "json",
                url: "user/code",
                data: {telNo: $("#cuTelNo").val(),type:"0"},
                success: function (json) {
                    $.hideLoading();
                    if (json.success == true) {
                        $.toast("验证码已发送");
                    }else{
                        $.toast(json.msg, "cancel");
                    }
                }
            });
        }else{
            $.toast("请输入手机号", "cancel");
        }
    });
});
//校验
function beforeSubmit(type) {
    var cuUserName = $("#cuUserName").val();
    if(cuUserName == null || cuUserName == "" || cuUserName.trim() == ""){
        $.toast("请输入姓名", "cancel");
        return false;
    }

    if(type == 0){
        var cuTelNo = $("#cuTelNo").val();
        if(cuTelNo == null || cuTelNo == "" || cuTelNo.trim() == ""){
            $.toast("请输入手机号", "cancel");
            return false;
        }
        var captcha = $("#captcha").val();
        if(captcha == null || captcha == "" || captcha.trim() == ""){
            $.toast("请输入验证码", "cancel");
            return false;
        }
        if(auth()){
            return false;
        }
    }

    var cuPassword = $("#cuPassword").val();
    if(type == 0){
        if(cuPassword == null || cuPassword == "" || cuPassword.trim() == "" || cuPassword == undefined){
            $.toast("请输入密码", "cancel");
            return false;
        }
        if(cuPassword.length <6){
            $.toast("密码至少6位", "cancel");
            return false;
        }
    }

    var cuPasswordConf = $("#cuPasswordConf").val();
    if(type == 1){
        if(cuPassword != cuPasswordConf){
            $.toast("两次密码不一致", "cancel");
            return false;
        }
    }

    var cuEmail = $("#cuEmail").val();
    if(cuEmail == null || cuEmail == "" || cuEmail.trim() == ""){
        $.toast("请输入邮箱", "cancel");
        return false;
    }

    var cuHospitalSelect = $("#cuHospitalLevel_select").val();
    var cuHospitalOther = $("#cuHospitalLevel_other").val();
    if((cuHospitalSelect && cuHospitalSelect =="其他（手填）") && !cuHospitalOther){
        $.toast("请输入医院等级", "cancel");
        return false;
    }
    if(!cuHospitalSelect){
        $.toast("请选择医院等级", "cancel");
        return false;
    }

    var cuHospital = $("#cuHospital").val();
    if(!cuHospital || cuHospital.trim() == ""){
        $.toast("请输入医院名称", "cancel");
        return false;
    }

    var cuDeptSelect = $("#cuDept_select").val();
    var cuDeptOther = $("#cuDept_other").val();
    if((cuDeptSelect && cuDeptSelect =="其他（手填）") && !cuDeptOther){
        $.toast("请输入科室", "cancel");
        return false;
    }
    if(!cuDeptSelect){
        $.toast("请选择科室", "cancel");
        return false;
    }
    var cuProfessional = $("#cuProfessional").val();
    if(!cuProfessional || cuProfessional.trim() == ""){
        $.toast("请输入职称", "cancel");
        return false;
    }
}

function auth() {
    $.ajax({
        type: "POST",
        cache: false,
        dataType: "json",
        url: "user/auth",
        data: {telNo: $("#cuTelNo").val(),code: $("#captcha").val(),type:"auth"},
        success: function (json) {
            if (json.success != true) {
                $.toast(json.msg, "cancel");
                $("#authType").val(0);
                return false;
            }
        }
    });
}
