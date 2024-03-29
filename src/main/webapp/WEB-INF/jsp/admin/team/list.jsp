<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>球队列表</title>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../include/header.jsp">
        <jsp:param value="nav_team" name="navId"/>
    </jsp:include>
    <div class="content-wrapper">
        <section class="content-header">
            <h1>
                球队列表
            </h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <!-- search -->
                            <div class="box-header">
                                <%--<div style="float:left;margin-right:10px"><a class="btn btn-success" href="admin/activities/addMatchUI">新增</a></div>--%>
                                <form class="form-inline" name="searchForm" id="searchForm" style="margin-bottom: 15px;">
                                    <input type="hidden" id="page" name="page" value="${page }"/>
                                    <input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${rowsPerPage }"/>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="keyword"
                                               name="keyword" placeholder="标题" value="${keyword }"/>
                                        是否有效：
                                        <select class="form-control" name="isValid">
                                            <option value="">全部</option>
                                            <option value="1" <c:if test='${isValid == 1}'>selected="selected"</c:if>>
                                                有效
                                            </option>
                                            <option value="0" <c:if test='${isValid == 0}'>selected="selected"</c:if>>
                                                无效
                                            </option>
                                        </select>
                                        日期：<input type="text" class="form-control" id="startDate" name="startDate"
                                               placeholder="开始日期" value="${startDate }"/>
                                        ~
                                        <input type="text" class="form-control" id="endDate" name="endDate"
                                               placeholder="截止日期" value="${endDate }"/>

                                    </div>
                                    <button type="submit" class="btn btn-success" id="searchBtn">搜索</button>
                                </form>
                            </div>
                            <c:if test="${pageInfo.items != null && pageInfo.items.size() > 0 }">
                                <table id="example2" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th>序号</th>
                                        <th>球队名称</th>
                                        <th>队长</th>
                                        <th>成员数</th>
                                        <th>创建时间</th>
                                        <th>创建人</th>
                                        <th>更新时间</th>
                                        <th>更新人</th>
                                        <th>状态</th>
                                        <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${pageInfo.items}" var="teamInfo" varStatus="s">
                                        <tr>
                                            <td>${(pageInfo.rowsPerPage  * (pageInfo.nowPage -1)) + (s.index +1) }</td>
                                            <td>${teamInfo.tiName}</td>
                                            <td>${teamInfo.captain}</td>
                                            <td>${teamInfo.userCount}</td>
                                            <td>${teamInfo.createTime}</td>
                                            <td>${teamInfo.createUser}</td>
                                            <td>${teamInfo.updateTime}</td>
                                            <td>${teamInfo.updateUser}</td>
                                            <td><c:if test="${teamInfo.valid == 0}">无效</c:if>
                                                <c:if test="${teamInfo.valid == 1}">有效</c:if></td>
                                            <td>
                                                <a class="btn btn-success" href="admin/team/editTeamUI?teamId=${teamInfo.tiId}">
                                                    查看
                                                </a>&nbsp;
                                                <a class="btn btn-danger" onclick="updateTeamState(${teamInfo.tiId})" href="javascript:void(0)">
                                                    <c:if test="${teamInfo.valid == null || teamInfo.valid == 1}">注销</c:if>
                                                    <c:if test="${teamInfo.valid == 0 }">恢复</c:if>
                                                </a>&nbsp;
                                                <c:if test="${teamInfo.valid == 0}">
                                                    <a class="btn btn-danger" onclick="delTeam(${teamInfo.tiId})" href="javascript:void(0)">
                                                        删除
                                                    </a>
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
                                <jsp:param value="admin/team/list" name="act"/>
                                <jsp:param value="searchForm" name="formName"/>
                            </jsp:include>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>


<input type="hidden" id="teamId" value=""/>

<!-- Modal -->
<div class="modal fade" id="updateTeamStateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示</h4>
            </div>
            <div class="modal-body">确定要这么操作吗？</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="sureUpdateStateBtn">确定</button>
            </div>
        </div>
    </div>
</div>



<!-- Modal -->
<div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示</h4>
            </div>
            <div class="modal-body">确定要这么操作吗？此操作会删除所有与本球队相关的数据！</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="sureDelBtn">确定</button>
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
            form.action = "admin/team/list";
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
            window.location.href="admin/team/updateTeamState?teamId="+$("#teamId").val();
        });
        $("#sureDelBtn").click(function () {
            window.location.href="admin/team/delTeam?teamId="+$("#teamId").val();
        });
    });

    function updateTeamState(teamId) {
        $("#teamId").val(teamId);
        $("#updateTeamStateModal").modal("show");
    }
    function delTeam(teamId) {
        $("#teamId").val(teamId);
        $("#delModal").modal("show");
    }
</script>
</body>