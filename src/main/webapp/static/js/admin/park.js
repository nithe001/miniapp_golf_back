var obj;
$(document).ready(function(){
    $("#addFenqu").click(function () {
        $("#fqName").val("");
        $("#fenquModal").modal("show");
    });

    $("#sureBtn").click(function () {
            var html = '<div class="box-header with-border divBorder" id="">' +
            '            <h3 class="box-title"> '+$("#fqName").val()+'：' +
            '               <input type="button" class="btn btn-danger" value="删除分区" onclick="delFenqu(this)"/>' +
            '               <input type="button" class="btn btn-danger" value="添加球洞" onclick="addQiudong(this)"/>' +
            '            </h3>' +
            '                    <table id="example2" class="table table-bordered table-hover">' +
            '                        <thead>' +
            '                        <tr>' +
            '                            <th width="8%">球洞序号</th>' +
            '                            <th width="10%">球洞标准杆</th>' +
            '                            <th>球洞球T距离</th>' +
            '                            <th>操作</th>' +
            '                        </tr>' +
            '                        </thead>' +
            '                        <tbody>' +
            '                        <tr>' +
            '                            <td><input type="text" onkeyup="value=value.replace(/^(0+)|[^\\d]+/g,\'\')"></td>' +
            '                            <td><input type="number" class="form-control"/></td>' +
            '                            <td>' +
            '                                黑：<input type="number" class="formcontrol"/>&nbsp;' +
            '                                金：<input type="number" class="formcontrol"/>&nbsp;' +
            '                                蓝：<input type="number" class="formcontrol"/>&nbsp;' +
            '                                白：<input type="number" class="formcontrol"/>&nbsp;' +
            '                                红：<input type="number" class="formcontrol"/>&nbsp;' +
            '                            </td>' +
            '                            <td><input type="button" class="btn btn-danger" value="删除球洞" onclick="delQiudong(this)"/></td>' +
            '                        </tr>' +
            '                        </tbody>' +
            '                    </table>' +
            '                </div>';
        $("#fenquDiv").append(html);
        $("#fenquModal").modal("hide");
    });

    //删除分区、球洞
    $("#sureDelBtn").click(function () {
        $("#delModal").modal("hide");
        $(obj).parent().parent().remove();
    });

});

//添加球洞
function addQiudong(object) {
    var html='<tr>' +
        '<td><input type="text" onkeyup="value=value.replace(/^(0+)|[^\\d]+/g,\'\')"></td>' +
        '<td><input type="number" class="form-control"/></td>' +
        '<td>' +
        '黑：<input type="number" class="formcontrol"/>&nbsp;' +
        '金：<input type="number" class="formcontrol"/>&nbsp;' +
        '蓝：<input type="number" class="formcontrol"/>&nbsp;' +
        '白：<input type="number" class="formcontrol"/>&nbsp;' +
        '红：<input type="number" class="formcontrol"/>&nbsp;' +
        '</td>' +
        '<td><input type="button" class="btn btn-danger" value="删除球洞" onclick="delQiudong(this)"/></td>' +
        '</tr>';
    $(object).parent().parent().find("table tbody").append(html);
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