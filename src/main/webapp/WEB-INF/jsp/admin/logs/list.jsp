<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>日志列表</title>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../include/header.jsp">
        <jsp:param value="nav_logs" name="navId"/>
    </jsp:include>
    <div class="content-wrapper">
        <section class="content-header">
            <h1>
                日志列表
            </h1>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <!-- search -->
                            <c:if test="${nameList != null && nameList.size() > 0 }">
                                <table id="example2" class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <th>序号</th>
                                        <th>路径</th>
                                        <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${nameList}" var="name" varStatus="s">
                                        <tr>
                                            <td>${s.index +1}</td>
                                            <td>${name}</td>
                                            <td>
                                                <a class="btn btn-success" href="/admin/logs/download?fileName=${name}">
                                                    下载
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                            <c:if test="${nameList == null || nameList.size() <= 0 }">
                                暂无数据！
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<jsp:include page="../include/adminlteJsInclude.jsp"/>
</body>