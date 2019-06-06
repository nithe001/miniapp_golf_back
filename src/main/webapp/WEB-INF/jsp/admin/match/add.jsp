<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   	<title>新增赛事活动</title>
	<jsp:include page="../include/commonInclude.jsp"></jsp:include>
    <script type="text/javascript" src="static/js/admin/activities.js?dt=20170104"></script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="../include/header.jsp">
            <jsp:param value="nav_match" name="navId"/>
		</jsp:include>

		<div class="content-wrapper">
		    <section class="content-header">
		      <h1>
				  新增
		      </h1>
		      <ol class="breadcrumb">
		        <li><a href="admin/match/list"><i class="fa fa-dashboard"></i>赛事活动列表</a></li>
		        <li class="active">新增</li>
		      </ol>
		    </section> 
		    
		    <section class="content">
				<div class="row">
				  <div class="col-md-12">
		            <div class="box box-info">
			            <div class="box-header with-border">
			              <h3 class="box-title">信息</h3>
			            </div>
						<form class="form-horizontal" id="calendarForm" action="/admin/match/addMatch" method="post">
							<jsp:include page="addForm.jsp"></jsp:include>
						</form>
          			</div>
          		 </div>
          		</div>
          	</section>
		</div>
	</div>
	<jsp:include page="../include/adminlteJsInclude.jsp"/>
</body>
</html>

