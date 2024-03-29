<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>导入成绩</title>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../include/header.jsp">
        <jsp:param value="nav_import" name="navId"></jsp:param>
    </jsp:include>

    <div class="content-wrapper">
        <section class="content-header">
            <h1>导入成绩</h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <div class="box-header">
                                <div class="form-group">
                                    <span style="color:red;">导入会员详细资料，可以导入一个资料文件或者把所选文件所在目录下的其他资料文件批量导入</span>
                                    <br/>
                                    <span style="color:red;">已经存在的会员详细信息将被覆盖</span>
                                    <br/>
                                    <span style="color:red;">请谨慎选择，一旦导入无法撤回</span>

                                </div>
                                <%--<div class="form-group">
                                    是否覆盖比赛信息：
                                    <input type="radio" name="isCoverMatch" value="1">是
                                    <input type="radio" name="isCoverMatch" value="0">否
                                </div>
                                <div class="form-group">
                                    是否覆盖球队信息：
                                    <input type="radio" name="isCoverTeam" value="1">是
                                    <input type="radio" name="isCoverTeam" value="0">否
                                </div>
                                <div class="form-group">
                                    是否覆盖比赛成绩信息：
                                    <input type="radio" name="isCoverScore" value="1">是
                                    <input type="radio" name="isCoverScore" value="0">否
                                </div>--%>
                                <button type="button" class="btn btn-success" id="importBtn">导入</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="myFileModal" tabindex="-1" role="dialog" aria-labelledby="myFileModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myFileModalLabel">提示</h4>
            </div>
            <div class="modal-body">
                <input type="file" id="multiFileUpload" name="files[]" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"  multiple/>
                <br/>
                <input type="image" src="static/images/loading.gif" style="width:70px;height:70px;" id="loadImg">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../include/adminlteJsInclude.jsp"/>

<script type="text/javascript">
    $(document).ready(function () {
        $("#loadImg").hide();
        $("#importBtn").bind("click", function () {
            /*if(!$("input[name=isCoverMatch]:checked").val()){
                alert("请选择是否覆盖比赛信息。");
                return false;
            }
            if(!$("input[name=isCoverTeam]:checked").val()){
                alert("请选择是否覆盖球队信息。");
                return false;
            }
            if(!$("input[name=isCoverScore]:checked").val()){
                alert("请选择是否覆盖比赛成绩信息。");
                return false;
            }*/
            $('#myFileModal').modal('show');
            $("#multiFileUpload").bind("click", function () {
                $("#loadImg").show();
            });

           $('#multiFileUpload').fileupload({
                type: "POST",
                cache: false,
                async: false,
                dataType: "json",
                singleFileUploads: false,
                url: 'admin/importMember/importMember',
                //formData:{choice:$("input[name=fileChoice]:checked").val()},
                //     isCoverTeam:$("input[name=isCoverTeam]:checked").val(),
                //     isCoverScore:$("input[name=isCoverScore]:checked").val()},
                success: function (json) {
                    if (json.success) {
                        $('#myFileModal').modal('hide');
                        $("#loadImg").hide();
                        alert("导入成功");
                    } else {
                        alert(json.msg);
                    }
                }
            });

        });
    });

</script>
</body>
</html>