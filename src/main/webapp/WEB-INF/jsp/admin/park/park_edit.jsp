<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>编辑管理员用户信息</title>
	<jsp:include page="../include/commonInclude.jsp"></jsp:include>
	<script src="static/js/admin/park.js"></script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
        <jsp:include page="../include/header.jsp">
            <jsp:param value="nav_park" name="navId"></jsp:param>
        </jsp:include>

		<div class="content-wrapper">
            <section class="content-header">
                <h1>编辑球场</h1>
                <ol class="breadcrumb">
                    <li><a href="admin/park/list"><i class="fa fa-dashboard"></i>球场列表</a></li>
                    <li class="active">编辑球场</li>
                </ol>
            </section>
		    <section class="content">
				<div class="row">
				  <div class="col-md-12">
		            <div class="box box-info">
			            <div class="box-header with-border">
			              <h3 class="box-title">球场信息</h3>
			            </div>
			            <!-- /.box-header -->
		            <!-- form start -->
                        <form class="form-horizontal" role="form" id="parkForm_add" name="parkForm_add"
                              action="/admin/park/parkEdit" method="post">
                            <jsp:include page="park_form.jsp"></jsp:include>
                        </form>
          			</div>
          		 </div>
          		</div>
          	</section>
          </div>
         </div>        				
	<script type="text/javascript" src="static/js/admin/user.js?dt=20170104"></script>
<jsp:include page="../include/adminlteJsInclude.jsp"/>
</body>
</html>

