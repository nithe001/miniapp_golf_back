<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
    $(document).ready(function(){
        $('#miMatchTime').datepicker({
            format: 'yyyy-mm-dd hh',
            minView: 1,//只显示到小时
            language: "zh-CN",
            autoclose: true,
            todayHighlight: true
        });
    });
</script>
<input type="hidden" name="miId" id="miId" value="${matchInfo.miId }"/>
<div class="box-body">
    <div class="form-group">
        <label for="miLogo" class="col-sm-2 control-label">比赛Logo</label>
        <div class="col-sm-5">
            <image src="${matchInfo.miLogo}" id="miLogo" style="width:30%;height:30%;border-radius: 50%;"/>
        </div>
    </div>
	<div class="form-group">
	  <label for="miTitle" class="col-sm-2 control-label">比赛名称</label>
	  <div class="col-sm-5">
		<input type="text" class="form-control" id="miTitle" name="miTitle" value="${matchInfo.miTitle }" placeholder="比赛名称"
            <c:if test="${matchInfo.miId != null}">
                readonly
            </c:if>
        />
	  </div>
	</div>
    <div class="form-group">
        <label for="" class="col-sm-2 control-label">参赛球队</label>
        <div class="col-sm-5">
            <c:forEach items="${joinTeamInfoList}" var="joinTeam" varStatus="s">
                <image src="${joinTeam.tiLogo}" style="width:20%;border-radius: 50%;"/> ${joinTeam.tiName}&nbsp;&nbsp;&nbsp;
            </c:forEach>
        </div>
    </div>
    <div class="form-group">
        <label for="miParkName" class="col-sm-2 control-label">比赛球场</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="miParkName" name="miParkName" readonly value="${matchInfo.miParkName}" placeholder="比赛球场">
        </div>
    </div>

    <div class="form-group">
        <label for="miMatchTime" class="col-sm-2 control-label">比赛时间</label>
        <div class="col-sm-5">
            <div class="input-group date">
                <div class="input-group-addon">
                    <i class="fa fa-calendar"></i>
                </div>
                <input type="text" class="form-control pull-right" id="miMatchTime" name="miMatchTime" value="${matchInfo.miMatchTime }" placeholder="比赛时间">
            </div>
        </div>
    </div>

    <div class="form-group">
        <label for="miMatchOpenType" class="col-sm-2 control-label">观战范围</label>
        <%--观战范围：（1、公开 球友均可见；2、队内公开：参赛者的队友可见；3、封闭：参赛队员可见）--%>
        <div class="col-sm-5">
            <input type="radio" name="miMatchOpenType" value="1" id="miMatchOpenType"
                   <c:if test="${matchInfo.miMatchOpenType == 1}">checked</c:if> />公开 球友均可见 &nbsp;&nbsp;&nbsp;
            <input type="radio" name="miMatchOpenType" value="2"
                   <c:if test="${matchInfo.miMatchOpenType == 2}">checked</c:if> />队内公开 &nbsp;&nbsp;&nbsp;
            <input type="radio" name="miMatchOpenType" value="3"
                   <c:if test="${matchInfo.miMatchOpenType == 3}">checked</c:if> />封闭 &nbsp;&nbsp;&nbsp;
        </div>
    </div>

    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">成绩上报</label>
        <div class="col-sm-5">
            <c:forEach items="${submitTeamInfoList}" var="joinTeam" varStatus="s">
                <image src="${joinTeam.tiLogo}" style="width:20%;border-radius: 50%;"/> ${joinTeam.tiName}&nbsp;&nbsp;&nbsp;
            </c:forEach>
        </div>
    </div>

    <div class="form-group">
        <label for="miMatchFormat1" class="col-sm-2 control-label">赛制</label>
        <div class="col-sm-5">
            <input type="radio" name="miMatchFormat1" value="0" id="miMatchFormat1"
                   <c:if test="${matchInfo.miMatchFormat1 == 0}">checked</c:if> />比杆 &nbsp;&nbsp;&nbsp;
            <input type="radio" name="miMatchFormat1" value="1"
                   <c:if test="${matchInfo.miMatchFormat1 == 1}">checked</c:if> />比洞 <br/>
            <input type="radio" name="miMatchFormat2" value="0"
                   <c:if test="${matchInfo.miMatchFormat2 == 0}">checked</c:if> />个人 &nbsp;&nbsp;&nbsp;
            <input type="radio" name="miMatchFormat2" value="1"
                   <c:if test="${matchInfo.miMatchFormat2 == 1}">checked</c:if> />双人 &nbsp;&nbsp;&nbsp;
        </div>
    </div>

    <div class="form-group">
        <label for="miContent" class="col-sm-2 control-label">比赛说明</label>
        <div class="col-sm-5">
            <textarea style="width: 100%;" class="form-control" id="miContent" name="miContent" placeholder="比赛说明">${matchInfo.miContent}</textarea>
        </div>
    </div>

    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">比分列表</label>
    </div>
    <c:if test="${score != null && score.size() > 0 }">
        <table id="example2" class="table table-bordered table-hover" style="float:left;width:14%;">
            <thead>
            <tr>
                <th>序号</th>
                <th>球友姓名</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${score.userList}" var="userInfo" varStatus="s">
                <tr>
                    <td>${s.index +1}</td>
                    <td>${userInfo.uiRealName}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${score != null && score.size() > 0 }">
        <table id="example2" class="table table-bordered table-hover" style="float:left;width:75%;">
            <thead>
            <tr>
                <c:forEach items="${score.list}" var="scoreList" varStatus="sl">
                    <c:if test="${sl.index == 0}">
                        <c:forEach items="${scoreList.userScoreTotalList}" var="ss" varStatus="sss">
                            <th>${ss.holeNum}</th>
                        </c:forEach>
                    </c:if>
                </c:forEach>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${score.list}" var="scoreList" varStatus="sl">
                <c:if test="${sl.index >= 2}">
                <tr>
                    <c:forEach items="${scoreList.userScoreTotalList}" var="ss" varStatus="sss">
                        <td>${ss.rodNum}</td>
                    </c:forEach>
                </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>
<div class="box-footer">
	<div class="col-xs-push-2 col-xs-2">
		<a class="btn btn-default" href="admin/consult/list" role="button">取消</a>
	</div>
	<div class="col-xs-push-4 col-xs-2">
		<button type="submit" class="btn btn-info pull-right">保存</button>
	</div>
</div>