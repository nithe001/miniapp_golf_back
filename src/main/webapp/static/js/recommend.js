$(document).ready(function(){
    if($("#userId").val()){
        $("#coverBox").hide();
        $("#box").hide();
    }
    $('#closeBox').click(function () {
        $("#coverBox").hide();
        $("#box").hide();
    });

    $("#sendMail").click(function () {
        $.showLoading();
        $.ajax({
            type: "POST",
            cache: false,
            dataType: "json",
            url: "news/sendDownloadLink",
            data: {lnMedliveId:$("#lnMedliveId").val()},
            success: function (json) {
                $.hideLoading();
                if(json.success == true){
                    $.toast("发送成功");
                }else{
                    $.toast(json.msg, "cancel");
                }
            }
        });
    });
});
