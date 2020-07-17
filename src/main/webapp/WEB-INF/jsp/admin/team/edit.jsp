<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   	<title>球队详细信息</title>
	<jsp:include page="../include/commonInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
        <jsp:include page="../include/header.jsp">
            <jsp:param value="nav_team" name="navId"/>
        </jsp:include>
		
		<div class="content-wrapper">
			<section class="content-header">
				<h1>球队详细信息</h1>
		        <ol class="breadcrumb">
		           <li><a href="admin/team/list"><i class="fa fa-dashboard"></i>球队列表</a></li>
		           <li class="active">球队详细信息</li>
		        </ol>
		    </section>
		    <section class="content">
				<div class="row">
				  <div class="col-md-12">
		            <div class="box box-info">
			            <div class="box-header with-border">
			              <h3 class="box-title">球队详细信息</h3>
                            <a class="btn btn-success" href="admin/team/claimTeamUser?teamId=${teamInfo.teamInfo.tiId}">整队认领</a>
			            </div>
						<form class="form-horizontal" id="calendarForm" action="admin/team/teamEdit" method="post">
							<jsp:include page="form.jsp"></jsp:include>
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

