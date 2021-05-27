<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" name="auId" id="id" value="${user.auId }"/>
<div class="box-body">
	<div class="form-group">
	  <label for="auUserName" class="col-sm-2 control-label">用户名</label>
	  <div class="col-sm-5">
		<input type="text" class="form-control" id="auUserName" name="auUserName" value="${user.auUserName }" placeholder="用户名"
			<c:if test='${user.auId != null}'>
				   readonly
			</c:if>
		>
	  </div>
	</div>
	<div class="form-group">
	  <label for="auPassword" class="col-sm-2 control-label">密码</label>
	  <div class="col-sm-5">
		<input type="password" class="form-control" id="auPassword" name="auPassword" value=""${user.auPassword }  placeholder="密码">
	  </div>
	</div>
	<div class="form-group">
	  <label for="surePass" class="col-sm-2 control-label">确认密码</label>
	  <div class="col-sm-5">
		<input type="password" class="form-control" id="surePass" placeholder="确认密码">
	  </div>
	</div>
	<div class="form-group">
	  <label for="auShowName" class="col-sm-2 control-label">姓名</label>
	  <div class="col-sm-5">
		<input type="text" class="form-control" id="auShowName" name="auShowName" value="${user.auShowName }" placeholder="姓名">
	  </div>
	</div>
	<div class="form-group">
		<label for="auShowName" class="col-sm-2 control-label">用户类型</label>
		<div class="col-sm-5">
			<select class="form-control" name="auRole">
                <option value="2" <c:if test='${user.auRole == 2}'>selected="selected"</c:if>>
                    临时管理员
                </option>
				<option value="1" <c:if test='${user.auRole == 1}'>selected="selected"</c:if>>
					普通管理员
				</option>
				<option value="0" <c:if test='${user.auRole == 0}'>selected="selected"</c:if>>
					超级管理员
				</option>
			</select>
		</div>
	</div>
</div>
<!-- /.box-body -->

<div class="box-footer">
	<div class="col-xs-push-2 col-xs-2">
		<a class="btn btn-default" href="admin/user/adminUserList" role="button">取消</a>
	</div>
	<div class="col-xs-push-4 col-xs-2">
		<button type="submit" class="btn btn-info pull-right">保存</button>
	</div>
</div>
		           
       				
       		
       		

