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
                <input type="file" id="multiFileUpload" name="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"/>
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
        $("#importBtn").bind("click", function () {
            $('#myFileModal').modal('show');
            $('#multiFileUpload').fileupload({
                type: "POST",
                cache: false,
                async: false,
                dataType: "json",
                url: 'admin/import/importScore',
                success: function (json) {
                    if (json.success) {
                        $('#myFileModal').modal('hide');
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