<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>赛事活动列表</title>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../include/header.jsp">
        <jsp:param value="nav_match" name="navId"/>
    </jsp:include>
    <div class="content-wrapper">
        <section class="content-header">
            <h1>
                赛事活动列表
            </h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <!-- search -->
                            <div class="box-header">
                                <div style="float:left;margin-right:10px"><a class="btn btn-success" href="admin/activities/addMatchUI">新增</a></div>
                                <form class="form-inline" name="searchForm" id="searchForm" style="margin-bottom: 15px;">
                                    <input type="hidden" id="page" name="page" value="${page }"/>
                                    <input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${rowsPerPage }"/>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="keyword"
                                               name="keyword" placeholder="标题" value="${keyword }"/>
                                        类型：
                                        <select class="form-control" name="type">
                                            <option value="">全部</option>
                                            <option value="1" <c:if test='${type == 1}'>selected="selected"</c:if>>
                                                团队/多人赛
                                            </option>
                                            <option value="0" <c:if test='${type == 0}'>selected="selected"</c:if>>
                                                单练
                                            </option>
                                        </select>
                                        状态：
                                        <select class="form-control" name="state">
                                            <option value="">全部</option>
                                            <option value="1" <c:if test='${state == 1}'>selected="selected"</c:if>>
                                                报名中
                                            </option>
                                            <option value="1" <c:if test='${state == 1}'>selected="selected"</c:if>>
                                                进行中
                                            </option>
                                            <option value="0" <c:if test='${state == 0}'>selected="selected"</c:if>>
                                                已结束
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
                                        <th>标题</th>
                                        <th>类型</th>
                                        <th>所在球场</th>
                                        <th>前9洞</th>
                                        <th>后9洞</th>
                                        <th>观战范围</th>
                                        <th>参赛范围</th>
                                        <%--（2 0:个人 、1:双人 1 0:比杆 、1:比洞）--%>
                                        <th>赛制</th>
                                        <th>开球时间</th>
                                        <th>创建时间</th>
                                        <th>创建人</th>
                                        <th>更新时间</th>
                                        <th>更新人</th>
                                        <th>状态</th>
                                        <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${pageInfo.items}" var="matchInfo" varStatus="s">
                                        <tr>
                                            <td>${(pageInfo.rowsPerPage  * (pageInfo.nowPage -1)) + (s.index +1) }</td>
                                            <td>${matchInfo.miTitle}</td>
                                            <td><c:if test="${matchInfo.miType == 0}">单练</c:if>
                                                <c:if test="${matchInfo.miType == 1}">团队/多人赛</c:if></td>
                                            <td>${matchInfo.miCreateTime}</td>
                                            <td>${matchInfo.miCreateUserName}</td>
                                            <%--<td>${matchInfo.state}</td>--%>
                                            <td>
                                                <a class="btn btn-success" href="admin/match/editMatchUI?matchId=${matchInfo.miId}">
                                                    <span class="glyphicon glyphicon-pencil"></span>编辑
                                                </a>&nbsp;
                                                <%--<a class="btn btn-success" href="admin/match/delMatch?matchId=${calendar[0]}">--%>
                                                    <%--<span class="glyphicon glyphicon-pencil"></span>--%>
                                                    <%--<c:if test="${matchInfo.miIsDel == null || matchInfo.miIsDel == 0}">删除</c:if>--%>
                                                    <%--<c:if test="${matchInfo.miIsDel == 1 }">恢复</c:if>--%>
                                                <%--</a>--%>
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
                                <jsp:param value="admin/match/list" name="act"/>
                                <jsp:param value="searchForm" name="formName"/>
                            </jsp:include>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>
<jsp:include page="../include/adminlteJsInclude.jsp"/>
<script type="text/javascript">
    $(document).ready(function () {
        //检索
        $("#searchBtn").bind("click", function () {
            var form = document.forms[0];
            form.action = "admin/match/list";
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