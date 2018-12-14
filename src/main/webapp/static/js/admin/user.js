$(document).ready(function(){
	initValidation();

    var hospitalLevel=$("#cuHospitalLevel").val();
    if(hospitalLevel && hospitalLevel!="一级" && hospitalLevel!="二级" && hospitalLevel!="三级") {
        $("#otherLevel").show();
    }
    var dept=$("#cuDept").val();
    // A．心内科   B．心外科  C．介入科 D．影像科   E.急诊科  F. 麻醉科  G.其他（手填）
    if(dept && dept!="心内科" && dept!="心外科" && dept!="介入科" && dept!="影像科" && dept!="急诊科" && dept!="麻醉科") {
        $("#otherDept").show();
    }

    $("#cuHospitalLevelSelect").change(function () {
        var level=$(this).children('option:selected').val();
        if(level && "其他（手填）" != level){
            $("#otherLevel").hide();
            $("#cuHospitalLevel").val(level);
        }else{
            $("#otherLevel").show();
        }
    });
    $("#cuDeptSelect").change(function () {
        var dept=$(this).children('option:selected').val();
        if(dept && "其他（手填）" != dept){
            $("#otherDept").hide();
            $("#cuDept").val(dept);
        }else{
            $("#otherDept").show();
        }
    });
    $("#cuType").change(function () {
        var type=$(this).children('option:selected').val();
        if(type && type == "1"){
            $("#clubDiv").hide();
        }else{
            $("#clubDiv").show();
        }
    });

    $("#cuClub").change(function () {
        var club=$(this).children('option:selected').val();
        if(club){
            jQuery("#cuClub").validationEngine("hide",true);
        }
    });


    $("#uploadExcelBtn").bind("click", function(){
        picType = "thumbnail";
        $("#myModalLabel").html("上传excel");
        $('#myModal').modal('show');
    });

    $("#closeBtn").bind("click", function(){
        window.location.href=GLOBAL_BASE_PATH+"admin/user/wechatUserList";
    });

    $('#fileupload').fileupload({
        type: "POST",
        cache:false,
        async: false, //同步，，即此代码执行时，其他的不可执行。
        dataType: "json",
        url: 'admin/user/importUser',
        add: function (e, data) {
            $("#fileDiv").hide();
            $("#myModalLabel").html("正在导入请稍后");
            setTimeout(function() {
                $("#coverBox").hide();
                data.submit();
            }, 2000);
        },
        success: function(json) {
            if (json.success) {
                var array = json.data;
                $("#fileDiv").hide();
                $("#myModalLabel").html("导入成功");
                $('#myModal').modal('show');
            }else{
                $("#fileDiv").show();
                $("#myModalLabel").html("导入失败");
                $('#myModal').modal('show');
            }
        }
    });
});
//校验
function initValidation(){
	var validations = [
		{id: 'auUserName', valid: "validate[required, ajax[ajaxNameCall]] text-input"},
		// {id: 'auPassword', valid: "validate[required, minSize[1], maxSize[16]] text-input"},
		// {id: 'surePass', valid: "validate[required, equals[auPassword]] text-input"},
		// {id: 'auAge', valid: "validate[custom[onlyNumberSp]] text-input"},//年龄
		// {id: 'auTel', valid: "validate[custom[mobilePhone]] text-input"},//电话
		// {id: 'auEmail', valid: "validate[custom[email]]"},//邮箱
		// {id: 'auPostCode', valid: "validate[custom[postCode]]"},//邮编
        {id: 'cuHospitalLevelSelect', valid: "validate[required]"},//医院等级
        {id: 'cuHospital', valid: "validate[required]"},//医院名称
        {id: 'cuDeptSelect', valid: "validate[required]"},//科室
        {id: 'cuProfessional', valid: "validate[required]"},//职称
        {id: 'cuType', valid: "validate[required]"},//用户类型
        {id: 'otherLevel', valid: "validate[required]"},//医院等级
        {id: 'otherDept', valid: "validate[required]"}//科室
	];
	for(var i=0,len = validations.length; i<len;i++ ){
		$("#"+ validations[i].id).addClass(validations[i].valid);
	}
	$("#userInfoForm_add").validationEngine('attach', {promptPosition : "topLeft", scroll: true});
}

function changeLevelValue(level) {
    $("#cuHospitalLevel").val(level);
    jQuery("#otherLevel").validationEngine("hide",true);
}
function changeDeptValue(dept) {
    $("#cuDept").val(dept);
    jQuery("#otherDept").validationEngine("hide",true);
}

function beforeSubmit() {
    var hospitalLevel=$("#cuHospitalLevelSelect").children('option:selected').val();
    var levelInput=$("#otherLevel").val();
    if(hospitalLevel && hospitalLevel == "其他（手填）" && !levelInput){
        jQuery("#otherLevel").validationEngine("showPrompt", "请输入医院等级！", 'red', 'topRight', true);
        return false;
    }

    var dept=$("#cuDeptSelect").children('option:selected').val();
    var otherDept=$("#otherDept").val();
    if(dept && dept == "其他（手填）" && !otherDept){
        jQuery("#otherDept").validationEngine("showPrompt", "请输入科室！", 'red', 'topRight', true);
        return false;
    }

    var userType=$("#cuType").children('option:selected').val();
    var club=$("#cuClub").children('option:selected').val();
    if(userType && userType == 2 && !club){
        jQuery("#cuClub").validationEngine("showPrompt", "请选择所属俱乐部！", 'red', 'topRight', true);
        return false;
    }
}
