<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" name="wcId" id="wcId" value="${matchInfo.wcId }"/>
<div class="box-body">
	<div class="form-group">
	  <label for="wcTitle" class="col-sm-2 control-label">活动名称</label>
	  <div class="col-sm-5">
		<input type="text" class="form-control" id="wcTitle" name="wcTitle" value="${matchInfo.wcTitle }" placeholder="活动名称"/>
	  </div>
	</div>
    <div class="form-group">
        <label for="wcEventTime" class="col-sm-2 control-label">发布日期</label>
        <div class="col-sm-2">
            <div class="input-group date">
                <div class="input-group-addon">
                    <i class="fa fa-calendar"></i>
                </div>
                <input type="text" readonly class="form-control pull-right" id="wcEventTime" name="wcEventTimeStr" value="${matchInfo.wcEventTimeStr}" placeholder="比赛日期">
            </div>
        </div>
    </div>
    <div class="form-group">
        <label for="wcEventTime" class="col-sm-2 control-label">报名时间</label>
        <div class="col-sm-2">
            <div class="input-group date">
                <div class="input-group-addon">
                    <i class="fa fa-calendar"></i>
                </div>
                <input type="text" readonly class="form-control pull-right" id="wcApplyStartTime" name="wcApplyStartTime" value="${matchInfo.wcApplyStartTime}" placeholder="报名开始时间">
            </div>
        </div>
        <div class="col-sm-2">
            <div class="input-group date">
                <div class="input-group-addon">
                    <i class="fa fa-calendar"></i>
                </div>
                <input type="text" readonly class="form-control pull-right" id="wcApplyEndTime" name="wcApplyEndTime" value="${matchInfo.wcApplyEndTime}" placeholder="报名截止时间">
            </div>
        </div>
    </div>

    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">球场名称</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="wcAddress" name="wcAddress" value="${matchInfo.wcAddress }" placeholder="球场名称"/>
        </div>
    </div>
    <%--<div class="form-group">
        <label for="wcAbstract" class="col-sm-2 control-label">活动介绍</label>
        <div class="col-sm-5">
            <input type="textarea" class="form-control" id="wcAbstract" name="wcAbstract" value="${matchInfo.wcAbstract}" placeholder="活动介绍"/>
        </div>
    </div>--%>
    <div class="form-group">
      <label for="wcContent" class="col-sm-2 control-label">活动内容</label>
      <div class="col-sm-7">
          <input type="hidden" id="wcContent" value="${matchInfo.wcContent}"/>
          <!-- 加载编辑器的容器 -->
          <script id="container" name="wcContent" type="text/plain" ></script>
      </div>
    </div>
</div>
<div class="box-footer">
	<div class="col-xs-push-2 col-xs-2">
		<a class="btn btn-default" href="admin/consult/list" role="button">取消</a>
	</div>
	<div class="col-xs-push-4 col-xs-2">
		<button type="submit" class="btn btn-info pull-right">仅保存</button>
	</div>
    <div class="col-xs-push-4 col-xs-2">
        <button type="type" class="btn btn-info pull-right">保存并发布</button>
    </div>
</div>