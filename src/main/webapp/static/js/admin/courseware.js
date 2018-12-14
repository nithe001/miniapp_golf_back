var ue;
$(document).ready(function(){
    $('#wecPublishTimeStr').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    });
	// 实例化编辑器
	ue = UE.getEditor('container',{
		topOffset:50,//浮动时工具栏距离浏览器顶部的高度，用于某些具有固定头部的页面
        initialFrameHeight: 400
	});
	ue.ready(function() {
		if($("#wecId").val()!= null && $("#wecId").val()!= ""){
			ue.setContent($("#wecContent").val());
		}
	});

    var picType = "thumbnail";
    $("#uploadBtn").bind("click", function(){
        picType = "thumbnail";
        $('#myModal').modal('show');
    });
    $("#uploadBtn4Pdf").bind("click", function(){
        $('#myModal4Pdf').modal('show');
    });

    $("#uploadBannerBtn").bind("click", function(){
        picType = "banner";
        $('#myModal').modal('show');
    });
    $('#fileupload').fileupload({
        type: "POST",
        cache:false,
        async: false, //同步，，即此代码执行时，其他的不可执行。
        dataType: "json",
        url: 'upload/uploadPicPc',
        success: function(json) {
            if (json.success) {
                var array = json.data;
                $('#myModal').modal('hide');
                if(picType == "thumbnail"){
                    $("#picShow").attr("src", array);
                    $("#picShow").show();
                    $("#wecThumbnail").val(array);
                }
                /*else{
                    $("#ShowLnBannerPicPath").attr("src", array);
                    $("#ShowLnBannerPicPath").show();
                    $("#lnBannerPicPath").val(array);
                }*/
            }else{
                alert("上传图片过程中有错误发生，请稍后再试。");
            }
        }
    });

    /*$('#pdfFileupload').fileupload({
        type: "POST",
        cache:false,
        async: false, //同步，，即此代码执行时，其他的不可执行。
        dataType: "json",
        url: 'upload/uploadPdf',
        success: function(json) {
            if (json.success) {
                var array = json.data;
                $('#myModal4Pdf').modal('hide');
                $('#msg').html("上传成功");
            }else{
                alert("上传过程中有错误发生，请稍后再试。");
                $('#msg').html("上传过程中有错误发生，请稍后再试。");
            }
        }
    });*/

    initValidation();
});
//校验
function initValidation(){
    var validations = [
        {id: 'wecTitle', valid: "validate[required,maxSize[128]] text-input"},
        {id: 'wecExpertName', valid: "validate[required,maxSize[32]] text-input"},
        {id: 'wecDepartment', valid: "validate[required,maxSize[32]] text-input"},
        {id: 'wecPosition', valid: "validate[required,maxSize[32]] text-input"},
        {id: 'wecCompany', valid: "validate[required,maxSize[128]] text-input"},
        {id: 'wecPdfFile', valid: "validate[required]"},
        {id: 'wecContent', valid: "validate[required] text-input"},
        {id: 'wecPublishTimeStr', valid: "validate[required] datepicker text-input"}
    ];
    for(var i=0,len = validations.length; i<len;i++ ){
        $("#"+ validations[i].id).addClass(validations[i].valid);
    }
    $("#coursewareForm").validationEngine('attach', {promptPosition : "topLeft", scroll: true});
}
/*
//保存
function beforeSubmit(){
	 var flag = true;
	 //对编辑器的操作在编辑器ready之后再做
	 ue.ready(function() {
		 //获取html内容，返回: <p>hello</p>
		 var contentHtml = ue.getContent();
		 $("#wecContent").val(contentHtml);
	 });
	 return flag;
}*/
function getFileSize(){
    var fup = document.getElementById("wecPptFile");
    var size = fup.files[0].size / 1024 / 1024;
    if (size > 80) {
        alert("文件太大！");
        $("#wecPptFile").val("");
        $("#wecPpt").val("");
        return false;
    }
}