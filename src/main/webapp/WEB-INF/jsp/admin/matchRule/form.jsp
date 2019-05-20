<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" name="wcId" id="wcId" value="${rule.mrId }"/>
<div class="box-body">
	<div class="form-group">
	  <label for="mrTitle" class="col-sm-2 control-label">标题</label>
	  <div class="col-sm-5">
		<input type="text" class="form-control" id="mrTitle" name="mrTitle" value="${rule.mrTitle }" placeholder="标题"/>
	  </div>
	</div>

    <div class="form-group">
        <label for="url" class="col-sm-2 control-label">url</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="url" name="mrUrl" value="${rule.mrUrl }" placeholder="url"/>
        </div>
    </div>
    <div class="form-group">
      <label for="sort" class="col-sm-2 control-label">排序</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="sort" name="mrSort" value="${rule.mrSort }" placeholder="排序"/>
        </div>
    </div>
</div>
<div class="box-footer">
	<div class="col-xs-push-2 col-xs-2">
		<a class="btn btn-default" href="/admin/match/ruleList" role="button">取消</a>
	</div>
	<div class="col-xs-push-4 col-xs-2">
		<button type="submit" class="btn btn-info pull-right">保存</button>
	</div>
</div>