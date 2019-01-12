<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<jsp:include page="include/commonInclude.jsp"></jsp:include>
	<title>高尔夫小程序管理后台</title>
</head>
<body class="hold-transition login-page">
<div class="login-box">
  <div class="login-logo">
    <a href=""><b>高尔夫小程序管理后台</b></a>
  </div>
  <div class="login-box-body">
    <form action="admin/login" method="post">
      <div class="form-group has-feedback">
        <input name="auUserName" type="txt" class="form-control" placeholder="用户名" required autofocus>
        <span class="glyphicon glyphicon-user form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
        <input name="auPassword" type="password" class="form-control" placeholder="密码" required>
        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
      </div>
      <div class="row">
        <div class="col-xs-4">
          <button type="submit" class="btn btn-primary btn-block btn-flat" style="margin-left:140%;">登录</button>
        </div>
      </div>
    </form>
	<div id="errMsg">
		<lable style="color:red;">${msg }</lable>   
	</div>
  </div>
</div>
</body>
</html>