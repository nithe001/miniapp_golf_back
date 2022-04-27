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
        <jsp:param value="nav_miniappUser" name="navId"></jsp:param>
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
                                        <th>头像</th>
                                        <th>昵称</th>
                                        <th>真实姓名</th>
                                        <th>性别</th>
                                        <th>用户类型</th>
                                        <th>关注时间</th>
                                        <th>状态</th>
                                        <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <c:forEach items="${pageInfo.items}" var="user" varStatus="s">
                                        <tr>
                                            <td>${(pageInfo.rowsPerPage  * (pageInfo.nowPage -1)) + (s.index +1)  }</td>
                                            <td><img src="${user.ui_headimg}" style="width:65px;height:65px;"></td>
                                            <td>${user.ui_nick_name}</td>
                                            <td>${user.ui_real_name}</td>
                                            <td>${user.ui_sex}</td>
                                            <td>
                                                <c:if test="${user.ui_openid == null }">
                                                    虚拟用户
                                                </c:if>
                                                <c:if test="${user.ui_openid != null }">
                                                    授权用户
                                                </c:if>

                                            </td>
                                            <td>${user.create_time}</td>
                                            <td><c:if test="${user.ui_is_valid == 0}">无效</c:if>
                                                <c:if test="${user.ui_is_valid == 1}">有效</c:if>
                                            </td>
                                            <td>
                                                <div class="form-inline" style="margin-bottom: 15px;">
                                                    <input type="hidden" value="${user.ui_id}"/>
                                                    <div class="form-group">
                                                        <input type="text" class="form-control" placeholder="认领人姓名" value=""/>
                                                    </div>
                                                    <button type="button" class="btn btn-success claimBtn">认领</button>

                                                    <a class="btn btn-success"
                                                       href="admin/miniappUser/miniappUserEditUI?userId=${user.ui_id}">
                                                        <span class="glyphicon glyphicon-pencil"></span>查看
                                                    </a>
                                                    <a class="btn btn-danger" href="javascript:void(0);" onclick="updateUserState(${user.ui_id})">
                                                        <c:if test="${user.ui_is_valid == null || user.ui_is_valid == 1}">注销</c:if>
                                                        <c:if test="${user.ui_is_valid == 0 }">恢复</c:if>
                                                    </a>
                                                </div>
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
                                <jsp:param value="admin/miniappUser/miniappUserList" name="act"/>
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
<div class="modal fade" id="updateStateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示</h4>
            </div>
            <input id="userId" value="" type="hidden"/>
            <div class="modal-body">确定要这么操作吗？</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="sureUpdateStateBtn">确定</button>
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
            form.action = "admin/miniappUser/miniappUserList";
            $("#page").val(1);
            form.submit();
        });

        //回车提交表单
        $("#keyword").keydown(function (event) {
            if (event.keyCode == 13) {
                $("#searchBtn").click();
            }
        });

        $("#sureUpdateStateBtn").click(function () {
            window.location.href="admin/miniappUser/updateminiappUserState?userId="+$("#userId").val();
        });
    });

    $(document).ready(function () {
        //认领
        $(".claimBtn").bind("click", function () {
            var userId = $(this).parent().find("input").eq(0).val();
            var ownerUserName = $(this).prev().children("input").val();
            window.location.href = "admin/miniappUser/claimUser?ownerUserName="+ownerUserName+"&userId="+userId;
        });
    });

    function updateUserState(userId) {
        $("#userId").val(userId);
        $("#updateStateModal").modal("show");
    }
</script>
</body>
</html>