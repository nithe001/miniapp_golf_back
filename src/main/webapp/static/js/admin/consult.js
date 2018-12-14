var ue;
$(document).ready(function(){

    $('#wacPublishTime').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    });

    $("input:radio[name='wacIsBanner']").change(function () {
        var item = $("input[name='wacIsBanner']:checked").val();
        if (item == 1) {
            $("#BannerDiv").show();
        } else {
            $("#BannerDiv").hide();
        }
    });

    var picType = "thumbnail";
	$("#uploadBtn").bind("click", function(){
        picType = "thumbnail";
		$('#myModal').modal('show');
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
                    $("#ShowWacBannerPicPath").attr("src", array);
                    $("#ShowWacBannerPicPath").show();
                    $("#wacBannerPicPath").val(array);
                }else{
                    $("#ShowWacBannerPicPath").attr("src", array);
                    $("#ShowWacBannerPicPath").show();
                    $("#wacBannerPicPath").val(array);
                }
			}else{
				alert("上传图片过程中有错误发生，请稍后再试。");
			}
		}
	});
	// 实例化编辑器
	ue = UE.getEditor('container',{
		topOffset:50,//浮动时工具栏距离浏览器顶部的高度，用于某些具有固定头部的页面
        initialFrameHeight: 400
	});
	ue.ready(function() {
		if($("#wacId").val()!= null && $("#waaId").val()!= ""){
			ue.setContent($("#wacAnswer").val());
		}
	});

	initValidation();
});
//校验
function initValidation(){
	var validations = [
        {id: 'wacPublishTime', valid: "validate[required] datepicker text-input"},
        {id: 'wacAnswer', valid: "validate[required] text-input"}
	];
	for(var i=0,len = validations.length; i<len;i++ ){
		$("#"+ validations[i].id).addClass(validations[i].valid);
	}
	$("#consultForm").validationEngine('attach', {promptPosition : "topLeft", scroll: true});
}

//保存
function beforeSubmit(){
	 var flag = true;
	 //对编辑器的操作在编辑器ready之后再做
	 ue.ready(function() {
		 //获取html内容，返回: <p>hello</p>
		 var contentHtml = ue.getContent();
		 $("#wacAnswer").val(contentHtml);
	 });
	 return flag;
}