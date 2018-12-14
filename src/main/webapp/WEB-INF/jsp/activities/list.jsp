<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
    <title>赛事活动列表</title>
</head>
<body>
<jsp:include page="../include/header.jsp">
    <jsp:param name="navi" value="activities"></jsp:param>
</jsp:include>
<div class="content">
    <c:if test="${pageInfo.items == null || pageInfo.items.size() == 0 }">
        暂无活动
    </c:if>
    <c:if test="${pageInfo.items != null && pageInfo.items.size() > 0 }">
        <c:forEach items="${pageInfo.items}" var="matchInfo" varStatus="s">
            <div>
                <a href="activities/getMatchDetail?matchInfo=${matchInfo.wcId}">
                    缩略图： ${matchInfo.wcThumb}
                    标题：${matchInfo.wcTitle}
                    简介：${matchInfo.wcAbstract}
                    开赛日期：${matchInfo.wcEventTime}
                    围观人数：${matchInfo.wcHit}
                </a>
            </div>
        </c:forEach>
    </c:if>
</div>
</body>
</html>