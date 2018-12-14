var ue;
$(document).ready(function(){
    var curTime = new Date();
    curTime.setHours(curTime.getHours()+1);
    // $('#wcEventTime').datepicker({
    //     format: 'yyyy-mm-dd',
    //     language: "zh-CN",
    //     autoclose: true,
    //     todayHighlight: true
    // }).on("click",function(ev){//限制只能选择当前日期之后的时间
    //     $("#wcEventTime").datepicker("setStartDate", curTime);
    // });
    $("#wcEventTime").datetimepicker({
        format: 'yyyy-mm-dd hh:mm:ss',
        language: 'zh-cn',
        autoclose: true,
        todayHighlight: true
    });

    $("#wcApplyStartTime").datetimepicker({
        format: 'yyyy-mm-dd',
        language: 'cn',
        autoclose: true,
        todayHighlight: true
    });
    $("#wcApplyEndTime").datetimepicker({
        format: 'yyyy-mm-dd',
        language: 'cn',
        autoclose: true,
        todayHighlight: true
    });


	// 实例化编辑器
	ue = UE.getEditor('container',{
		topOffset:50,//浮动时工具栏距离浏览器顶部的高度，用于某些具有固定头部的页面
        initialFrameHeight: 400
	});
	ue.ready(function() {
		if($("#wcId").val()!= null && $("#wcId").val()!= ""){
			ue.setContent($("#wcContent").val());
		}
	});
    initValidation();
    
    $("#wcActivitiesId").change(function () {
        var activitieName = $(this).children('option:selected').text();
        if(activitieName && activitieName.trim() == "学会会讯"){
            $("#isOpenDiv").hide();
            $("input[name='wcIsOpen']").val(1);
        }else{
            $("#isOpenDiv").show();
        }
    })

    var selectValue = $("#wcActivitiesId").children('option:selected').text();
    if(selectValue && selectValue.trim() == "学会会讯"){
        $("#isOpenDiv").hide();
    }
});
//校验
function initValidation(){
    var validations = [
        {id: 'wcTitle', valid: "validate[required,maxSize[128]] text-input"},
        {id: 'wcEventTime', valid: "validate[required] datepicker text-input"},
        {id: 'wcAddress', valid: "validate[required,maxSize[128]] text-input"},
        {id: 'wcAbstract', valid: "validate[required,maxSize[500]] text-input"},
        {id: 'wcContent', valid: "validate[required] text-input"},
        {id: 'wcExpertName', valid: "validate[required] text-input"},
        {id: 'wcType', valid: "validate[required] text-input"}

    ];
    for(var i=0,len = validations.length; i<len;i++ ){
        $("#"+ validations[i].id).addClass(validations[i].valid);
    }
    $("#calendarForm").validationEngine('attach', {promptPosition : "topLeft", scroll: true});
}