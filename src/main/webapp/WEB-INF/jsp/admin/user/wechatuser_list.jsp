<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>小程序用户列表</title>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
    <script type="text/javascript" src="static/js/admin/user.js?dt=20170104"></script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../include/header.jsp">
        <jsp:param value="nav_wechatUser" name="navId"></jsp:param>
        <jsp:param value="user" name="navParentId"/>
    </jsp:include>

    <div class="content-wrapper">
        <section class="content-header">
            <h1>小程序用户列表</h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <div class="box-header">
                                <form class="form-inline" name="searchForm" id="searchForm"
                                      style="margin-bottom: 15px;">
                                    <input type="hidden" id="page" name="page" value="${page }"/>
                                    <input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${rowsPerPage }"/>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="keyword" name="keyword"
                                               placeholder="昵称、真实姓名" value="${keyword }"/>
                                    </div>
                                    <button type="button" class="btn btn-success" id="searchBtn">搜索</button>
                                </form>
                            </div>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <c:if test="${pageInfo.items != null && pageInfo.items.size() > 0 }">
                                <table id="example2" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th>序号</th>
                                        <th>昵称</th>
                                        <th>openid</th>
                                        <th>真实姓名</th>
                                        <th>性别</th>
                                        <th>省份</th>
                                        <th>城市</th>
                                        <th>关注时间</th>
                                        <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${pageInfo.items}" var="user" varStatus="s">
                                        <tr>
                                            <td>${(pageInfo.rowsPerPage  * (pageInfo.nowPage -1)) + (s.index +1)  }</td>
                                            <%--select wu.wui_nick_name,wu.wui_openid,u.ui_real_name,wu.wui_sex,wu.wui_province,wu.wui_city,wu.create_time--%>
                                            <td>${user[1]}</td>
                                            <td>${user[2]}</td>
                                            <td>${user[3]}</td>
                                            <td>${user[4]}</td>
                                            <td>${user[5]}</td>
                                            <td>${user[6]}</td>
                                            <td>${user[7]}</td>
                                            <td>
                                                <c:if test="${user[0] != null }">
                                                    <a class="btn btn-success"
                                                       href="admin/user/wechatUserEditUI?id=${user[0]}">
                                                        <span class="glyphicon glyphicon-pencil"></span>查看
                                                    </a>&nbsp;|
                                                    <%--<a class="btn btn-success"
                                                       href="admin/user/setAdmin?userId=${user[0]}">
                                                        <span class="glyphicon glyphicon-pencil"></span>设为赛事管理员
                                                    </a>--%>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                            <c:if test="${pageInfo.items == null || pageInfo.items.size() <= 0 }">
                                暂无数据！
                            </c:if>
                            <jsp:include page="../include/pojoPageInfo.jsp">
                                <jsp:param value="admin/user/wechatUserList" name="act"/>
                                <jsp:param value="searchForm" name="formName"/>
                            </jsp:include>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel"></h4>
            </div>
            <div class="modal-body" id="fileDiv"><input type="file" id="fileupload" name="file"
                                                        cssClass="form-control"/></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" id="closeBtn">关闭</button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../include/adminlteJsInclude.jsp"/>
<script type="text/javascript">
    $(document).ready(function () {
        //检索
        $("#searchBtn").bind("click", function () {
            var form = document.forms[0];
            form.action = "admin/user/wechatUserList";
            $("#page").val(1);
            form.submit();
        });

        //回车提交表单
        $("#keyword").keydown(function (event) {
            if (event.keyCode == 13) {
                $("#searchBtn").click();
            }
        });
    });
</script>
</body>
</html>