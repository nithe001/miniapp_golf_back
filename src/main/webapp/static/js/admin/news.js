var ue;
$(document).ready(function(){

    var audio = document.getElementById("musicAudio");
    $('.music_box').click(function(){
        $('.music_icon').toggleClass("music_rotate off");
        if(audio.paused){audio.play();}
        else{audio.pause();}
    })

    $('#lnDate').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        autoclose: true,
        todayHighlight: true
    });

    $("input:radio[name='lnIsBanner']").change(function () {
        var item = $("input[name='lnIsBanner']:checked").val();
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
                    $("#ShowThumbnailPath").attr("src", array);
                    $("#ShowThumbnailPath").show();
                    $("#lnThumbnailPath").val(array);
                }else{
                    $("#ShowLnBannerPicPath").attr("src", array);
                    $("#ShowLnBannerPicPath").show();
                    $("#lnBannerPicPath").val(array);
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
		if($("#lnId").val()!= null && $("#lnId").val()!= ""){
			ue.setContent($("#lnContent").val());
		}
	});

    // $("input[name='lnIsBanner']").bind("change", function () {
    //     alert($("input[name='lnIsBanner']").val());
    // });

	initValidation();
});
//校验
function initValidation(){
	var validations = [
		{id: 'lnTitle', valid: "validate[required] text-input"},
        {id: 'lnDate', valid: "validate[required] datepicker text-input"},
        {id: 'lnFrom', valid: "validate[required] text-input"},
        {id: 'lnContent', valid: "validate[required] text-input"}
	];
	for(var i=0,len = validations.length; i<len;i++ ){
		$("#"+ validations[i].id).addClass(validations[i].valid);
	}
	$("#newsForm").validationEngine('attach', {promptPosition : "topLeft", scroll: true});
}

//保存
/*
function beforeSubmit(){
	 var flag = true;
	 //对编辑器的操作在编辑器ready之后再做
	 ue.ready(function() {
		 //获取html内容，返回: <p>hello</p>
		 var contentHtml = ue.getContent();
		 $("#lnContent").val(contentHtml);
	 });
	 return flag;
}*/
