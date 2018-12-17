<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="com.golf.golf.common.security.AdminUserUtil" %>
<%@ page import="com.golf.golf.common.security.AdminUserModel" %>
<%
	String navId = request.getParameter("navId");
    String navParentId = request.getParameter("navParentId");
	AdminUserModel user = AdminUserUtil.getLoginUser();
%>
<!-- Main Header -->
<header class="main-header"> <!-- Logo --> 
	<a href="javascript:void(0);" class="logo">
		<!-- mini logo for sidebar mini 50x50 pixels -->
      	<span class="logo-mini" style="margin-top:75%;"><i class="fa fa-cog"></i></span>
		<span class="logo-lg"><b>心信速递</b></span>
	</a> 
	<nav class="navbar navbar-static-top" role="navigation"> 
		<a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"> 
			<span class="sr-only">Toggle navigation</span>
		</a> 
		
	      <div class="navbar-custom-menu" style="border:0px solid red;">
	        <ul class="nav navbar-nav">
	        	<li class="dropdown user user-menu">
					<a href="user/userEditUI?id=<%= user.getId() %>" style="color:CFCFCF;">
					<span class="glyphicon glyphicon-user"></span>
					<%= user.getShowName() %>，你好
					</a>
	          	</li>
	          <li class="dropdown user user-menu">
	            <a href="admin/logout" >
	              <span class="hidden-xs">退出</span>
	            </a>
	          </li>
	        </ul>
	      </div>
	</nav> 
</header>
<aside class="main-sidebar">
<section class="sidebar">
<ul class="sidebar-menu">
    <li id="user" class="treeview"><a href="#"><i class="fa fa-user"></i> 用户管理
        <span></span> <span class="pull-right-container">
				<i class="fa fa-angle-left pull-right"></i>
		</span> </a>
        <ul class="treeview-menu">
            <li id="nav_adminUser"><a href="admin/user/adminUserList"><i class="fa fa-user"></i> <span>后台用户</span></a></li>
            <li id="nav_wechatUser"><a href="admin/user/wechatUserList"><i class="fa fa-user"></i> <span>前台用户</span></a></li>
        </ul>
    </li>
    <li id="nav_match"><a href="admin/match/list"><i class="fa fa-bookmark"></i> <span>赛事活动管理</span></a></li>
</ul>
</section>
</aside>

<script>
	var navId = "<%=navId%>";
    var navParentId = "<%=navParentId%>";
    //选中状态
    if (navParentId!=null){
        $("#"+navParentId).addClass("active");
    }
    $("#"+navId).addClass("active");
</script>
