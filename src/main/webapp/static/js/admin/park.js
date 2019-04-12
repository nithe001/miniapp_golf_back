var obj;
var index = 1;
$(document).ready(function(){
    initValidation();
    $("#addFenqu").click(function () {
            var html = '<div class="box-header with-border divBorder">' +
            '            <h3 class="box-title"><input type="type" class="width20 fqName" name="parkPartitionList['+index+'].ppName" placeholder="输入分区名称"/>：' +
            '               <input type="button" class="btn btn-danger" value="删除分区" onclick="delFenqu(this)"/>' +
            '               <input type="button" class="btn btn-danger" value="添加球洞" onclick="addQiudong(this)"/>' +
            '            </h3>' +
            '                    <table id="example2" class="table table-bordered table-hover">' +
            '                        <thead>' +
            '                        <tr>' +
            '                            <th width="10%">球洞序号</th>' +
            '                            <th width="10%">球洞标准杆</th>' +
            '                            <th>球洞球T距离</th>' +
            '                            <th>操作</th>' +
            '                        </tr>' +
            '                        </thead>' +
            '                        <tbody>' +
            '                        <tr>' +
            '                            <td><input type="text" class="width100 qdxh" name="parkPartitionList['+index+'].ppHoleNum" placeholder="输入序号"/></td>' +
            '                            <td><input type="text" class="width100 bzg" name="parkPartitionList['+index+'].ppHoleStandardRod" placeholder="输入标准杆"/></td>' +
            '                            <td>' +
            '                                黑：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTBlackDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
            '                                金：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTGoldDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
            '                                蓝：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTBlueDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
            '                                白：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTWhiteDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
            '                                红：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTRedDistance" placeholder="输入距离"/>' +
            '                            </td>' +
            '                            <td><input type="button" class="btn btn-danger" value="删除球洞" onclick="delQiudong(this)"/></td>' +
            '                        </tr>' +
            '                        </tbody>' +
            '                    </table>' +
            '                </div>';
        $("#fenquDiv").append(html);
        $("#fenquModal").modal("hide");
        initValidation();
        index++;
    });

    //删除分区、球洞
    $("#sureDelBtn").click(function () {
        $("#delModal").modal("hide");
        $(obj).parent().parent().remove();
    });

});

function initValidation() {
    var validations = [
        // {class: 'fqName', valid: "validate[required, custom[onlyLetterSp]] text-input"},
        // {class: 'qdxh', valid: "validate[required, min[1], max[9], custom[onlyNumberSp]] text-input"},
        // {class: 'bzg', valid: "validate[required, min[1], max[7], custom[onlyNumberSp]] text-input"},
        // {class: 'juli', valid: "validate[required, min[1], max[100], custom[onlyNumberSp]] text-input"}
    ];
    for(var i=0,len = validations.length; i<len;i++ ){
        $("."+ validations[i].class).addClass(validations[i].valid);
    }
    $("#parkForm_add").validationEngine('attach', {promptPosition : "topRight", scroll: true});
}

//添加球洞
function addQiudong(object) {
    var html='<tr>' +
        '<td><input type="text" class="width100 qdxh" name="parkPartitionList['+index+'].ppHoleNum" placeholder="输入序号"/></td>' +
        '<td><input type="text" class="width100 bzg" name="parkPartitionList['+index+'].ppHoleStandardRod" placeholder="输入标准杆"/></td>' +
        '<td>' +
        '黑：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTBlackDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
        '金：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTGoldDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
        '蓝：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTBlueDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
        '白：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTWhiteDistance" placeholder="输入距离"/>&nbsp;&nbsp;' +
        '红：<input type="text" class="width10 juli" name="parkPartitionList['+index+'].ppHoleTRedDistance" placeholder="输入距离"/>' +
        '</td>' +
        '<td><input type="button" class="btn btn-danger" value="删除球洞" onclick="delQiudong(this)"/></td>' +
        '</tr>';
    $(object).parent().parent().find("table tbody").append(html);
    initValidation();
    index++;
}

//删除分区确认
function delFenqu(object) {
    obj = object;
    $("#type").val(1);
    $("#delModal").modal("show");
}

//删除球洞确认
function delQiudong(object) {
    obj = object;
    $("#delModal").modal("show");
}