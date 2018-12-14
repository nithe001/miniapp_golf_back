<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
    <jsp:include page="../include/wechatCommonInclude.jsp"></jsp:include>
    <!--隐藏分享相关-->
    <script type="text/javascript" src="static/js/wxhideMenuItems.js" ></script>
    <meta charset="UTF-8">
    <title>我的咨询</title>
</head>
<body>
<div class="wrap">
    <div class="content">
        <div class="detail">
            <form action="user/addConsult" method="post">
                <div class="articles-msg">
                    <span class="source">问题内容：</span>
                    <input type="text" name="title" placeholder="请输入问题内容"/>
                    </br>
                    <input type="submit" value="提交"/>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>