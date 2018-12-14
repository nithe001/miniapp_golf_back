<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><spring:message code ="weChat"/><spring:message code ="admin.wechatMenu"/><spring:message code ="admin.wechatMenu.group"/><spring:message code ="admin.tips.list"/>-<spring:message code='medtronic'/></title>
    <jsp:include page="../../include/commonInclude.jsp"></jsp:include>
    <script type="text/javascript" src="static/js/admin/commonutil.js"></script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <jsp:include page="../../include/header_wx.jsp">
        <jsp:param value="nav_wechat" name="navId"></jsp:param>
        <jsp:param value="nav_wechatMenu" name="subNavId"></jsp:param>
    </jsp:include>

    <div class="content-wrapper">
        <section class="content-header">
            <h1><spring:message code ="weChat"/><spring:message code ="admin.wechatMenu.group"/><spring:message code ="admin.tips.list"/></h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <div class="box-header">
                                <div style="float:left;margin-right:10px">
                                    <a class="btn btn-success" href="admin/wechat/menu/addOrEditInit?siteId=${siteId}"><spring:message code ="admin.operation.add"/><spring:message code ="admin.wechatMenu.group"/></a>
                                </div>
                                <form class="form-inline" name="searchForm" id="searchForm"
                                      style="margin-bottom: 15px;">
                                    <input type="hidden" id="page" name="page" value="${page }"/>
                                    <input type="hidden" id="siteId" name="siteId" value="${siteId }"/>
                                    <input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${rowsPerPage }"/>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="keyword" name="keyword"
                                               placeholder="<spring:message code ="admin.field.title"/>" value="${keyword }"/>

                                        <spring:message code ="admin.date"/>：
                                        <input type="text" class="form-control" id="startDate" name="startDate"
                                               placeholder="<spring:message code ="admin.startTime"/>" value="${startDate }"/>
                                        ~
                                        <input type="text" class="form-control" id="endDate" name="endDate"
                                               placeholder="<spring:message code ="admin.endTime"/>" value="${endDate }"/>

                                        <input type="radio" name="state" value="1"
                                               <c:if test="${state == 1 }">checked</c:if> /><spring:message code ="admin.menu.published"/>
                                        <input type="radio" name="state" value="0"
                                               <c:if test="${state == 0 }">checked</c:if> /><spring:message code ="admin.menu.unpublished"/>
                                    </div>
                                    <button type="button" class="btn btn-success" id="searchBtn"><spring:message code ="search"/></button>
                                </form>
                            </div>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <c:if test="${pageInfo.items != null && pageInfo.items.size() > 0 }">
                                <table id="example2" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th><spring:message code ="order.number"/></th>
                                        <th><spring:message code ="admin.field.title"/></th>
                                        <th><spring:message code ="status"/></th>
                                        <th><spring:message code ="admin.field.creationTime"/></th>
                                        <th><spring:message code ="admin.field.creator"/></th>
                                        <th><spring:message code ="admin.field.updateTime"/></th>
                                        <th><spring:message code ="admin.wechatMenu.updater"/></th>
                                        <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span><spring:message code ="operation"/></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${pageInfo.items}" var="u" varStatus="s">
                                        <tr>
                                            <td>${(pageInfo.rowsPerPage  * (pageInfo.nowPage -1)) + (s.index +1)  }</td>
                                            <td>${u.wcmgName }</td>
                                            <td>
                                                <c:if test="${u.wcmgIsValid == 1 }">已发布</c:if>
                                                <c:if test="${u.wcmgIsValid == 0 }">未发布</c:if>
                                            </td>
                                            <td>${u.wcmgCreateTimeStr }</td>
                                            <td>${u.wcmgCreateUserName }</td>
                                            <td>${u.wcmgUpdateTimeStr }</td>
                                            <td>${u.wcmgUpdateUserName }</td>
                                            <td>
                                                <c:if test="${u.wcmgIsValid == null || u.wcmgIsValid == 0 }">
                                                    <a class="btn btn-success" href="javascript:void(0);" onclick="deploy(${u.wcmgId})"><spring:message code ="admin.wechatMenu.release"/></a>|
                                                </c:if>
                                                <a class="btn btn-success" href="admin/wechat/menu/menuInfo?groupId=${u.wcmgId}&siteId=${siteId}"><spring:message code ="edit"/></a>
                                                <c:if test="${u.wcmgIsValid == null || u.wcmgIsValid == 0 }">
                                                    | <a class="btn btn-danger" href="javascript:del(${u.wcmgId});"><spring:message code ="delete"/></a>
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
                            <jsp:include page="../../include/pojoPageInfo.jsp">
                                <jsp:param value="admin/wechat/menu/list" name="act"/>
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
<div class="modal fade" id="deployModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><spring:message code ="admin.operation.confirm"/></h4>
            </div>
            <div class="modal-body"><spring:message code ="admin.modal.tipBeforeRelease"/></div>
            <input type="hidden" id="groupId" value=""/>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code ="close"/></button>
                <button type="button" class="btn btn-primary" id="sureBtn"><spring:message code ="admin.modal.sure"/></button>
            </div>
        </div>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><spring:message code ="admin.operation.confirm"/></h4>
            </div>
            <div class="modal-body"><spring:message code ="admin.modal.deleteTips"/></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code ="close"/></button>
                <button type="button" class="btn btn-primary" id="sureDelBtn"><spring:message code ="admin.modal.sure"/></button>
            </div>
        </div>
    </div>
</div>


<jsp:include page="../../include/adminlteJsInclude.jsp"/>
<script type="text/javascript">
    $(document).ready(function () {
        var siteId = localStorage.getItem("m");
        //检索
        $("#searchBtn").bind("click", function () {
            var form = document.forms[0];
            form.action = "admin/wechat/menu/menuGroupList";
            $("#page").val(1);
            form.submit();
        });

        //回车提交表单
        $("#keyword").keydown(function (event) {
            if (event.keyCode == 13) {
                $("#searchBtn").click();
            }
        });

        $('#startDate').datepicker({
            format: 'yyyy-mm-dd',
            language: 'cn',
            autoclose: true,
            todayHighlight: true
        });

        $('#endDate').datepicker({
            format: 'yyyy-mm-dd',
            language: 'cn',
            autoclose: true,
            todayHighlight: true
        });

        //发布确认btn
        $("#sureBtn").click(function(){
            window.location.href="admin/wechat/menu/reset?groupId="+$("#groupId").val()+"&siteId="+siteId;
        });
        //删除确认btn
        $("#sureDelBtn").click(function(){
            window.location.href="admin/wechat/menu/deleteMenuGroup?groupId="+$("#groupId").val()+"&siteId="+siteId;
        });
    });
    //发布弹框
    function deploy(groupId){
        $("#deployModal").modal("show");
        $("#groupId").val(groupId);
    }
    //删除弹框
    function del(groupId){
        $("#delModal").modal("show");
        $("#groupId").val(groupId);
    }
</script>
</body>
</html>