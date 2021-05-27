<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<input type="hidden" name="tiId" id="tiId" value="${teamInfo.teamInfo.tiId }"/>
<div class="box-body">
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">球队名称</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="wcTitle" name="tiName" value="${teamInfo.teamInfo.tiName}"
                   placeholder="球队名称"
                    <c:if test="${teamInfo.teamInfo.tiId != null}">
                        readonly
                    </c:if>
            />
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">球队简称</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="tiAbbrev" name="tiAbbrev"
                   value="${teamInfo.teamInfo.tiAbbrev}" placeholder="球队简称"/>
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">所属地区</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="tiAddress" name="tiAddress"
                   value="${teamInfo.teamInfo.tiAddress}" placeholder="所属地区"/>
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">球队签名</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="tiSignature" name="tiSignature"
                   value="${teamInfo.teamInfo.tiSignature}" placeholder="球队签名"/>
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">球队简介</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="tiDigest" name="tiDigest" value="${teamInfo.teamInfo.tiDigest}"
                   placeholder="球队简介"/>
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">是否加入审核</label>
        <div class="col-sm-5">
            <input type="radio" name="tiJoinOpenType" value="1"
                   <c:if test="${teamInfo.teamInfo.tiJoinOpenType == 1 }">checked</c:if> />是
            <input type="radio" name="tiJoinOpenType" value="0"
                   <c:if test="${teamInfo.teamInfo.tiJoinOpenType == 0 }">checked</c:if> />否
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">比赛信息是否公开</label>
        <div class="col-sm-5">
            <input type="radio" name="tiInfoOpenType" value="1"
                   <c:if test="${teamInfo.teamInfo.tiInfoOpenType == 1 }">checked</c:if> />是（比赛成绩等向所有球友开放）
            <input type="radio" name="tiInfoOpenType" value="0"
                   <c:if test="${teamInfo.teamInfo.tiInfoOpenType == 0 }">checked</c:if> />否（比赛成绩等向队友开放）
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">队员详细资料</label>
        <div class="col-sm-5">
            <input type="radio" name="tiUserInfoType" value="1"
                   <c:if test="${teamInfo.teamInfo.tiUserInfoType == 1 }">checked</c:if> />需要
            <input type="radio" name="tiUserInfoType" value="0"
                   <c:if test="${teamInfo.teamInfo.tiUserInfoType == 0 }">checked</c:if> />不需要
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">比赛成绩审核</label>
        <div class="col-sm-5">
            <input type="radio" name="tiMatchResultAuditType" value="1"
                   <c:if test="${teamInfo.teamInfo.tiMatchResultAuditType == 1 }">checked</c:if> />需要
            <input type="radio" name="tiMatchResultAuditType" value="0"
                   <c:if test="${teamInfo.teamInfo.tiMatchResultAuditType == 0 }">checked</c:if> />不需要
        </div>
    </div>
    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">是否有效</label>
        <div class="col-sm-5">
            <input type="radio" name="tiIsValid" value="1"
                   <c:if test="${teamInfo.teamInfo.tiIsValid == 1 }">checked</c:if> />是
            <input type="radio" name="tiIsValid" value="0"
                   <c:if test="${teamInfo.teamInfo.tiIsValid == 0 }">checked</c:if> />否
        </div>
    </div>

    <div class="form-group">
        <label for="wcTitle" class="col-sm-2 control-label">队员列表</label>
    </div>

<div class="box-footer">
    <div class="col-xs-push-2 col-xs-2">
        <a class="btn btn-default" href="admin/consult/list" role="button">取消</a>
    </div>
    <div class="col-xs-push-4 col-xs-2">
        <button type="submit" class="btn btn-info pull-right">保存</button>
    </div>
</div>
<br/>
<br/>

    <c:if test="${teamInfo.userList != null && teamInfo.userList.size() > 0 }">
        <div style="width: 60%;margin-left:10%;">
            <table id="example2" class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th width="8%">序号</th>
                    <th width="13%">队员头像</th>
                    <th width="20%">队员真实姓名/昵称</th>
                    <th width="13%">用户类型</th>
                    <th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${teamInfo.userList}" var="userInfo" varStatus="s">
                    <tr>
                        <td>${s.index +1}</td>
                        <td><img src="${userInfo.uiHeadimg}" style="width:55px;height:55px;"></td>
                        <td>
                            <c:if test="${userInfo.uiRealName ne '' && userInfo.uiRealName ne null}">
                                ${userInfo.uiRealName}
                            </c:if>
                            <c:if  test="${userInfo.uiRealName eq '' || userInfo.uiRealName eq null}">
                                ${userInfo.uiNickName}
                            </c:if>
                        <td>
                            <c:if test="${userInfo.tumUserType == 0}">
                                <div style="color:red;">队长</div>
                            </c:if>
                            <c:if test="${userInfo.tumUserType == 1}">
                                普通队员
                            </c:if>
                            <c:if test="${userInfo.tumUserType == 2}">
                                申请入队
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${userInfo.tumUserType != 0}">
                                <a class="btn btn-success" href="javascript:setCap(${userInfo.uiId})">设为队长</a>
                                <a class="btn btn-danger" href="javascript:delFromTeam(${userInfo.uiId})">移出队伍</a>
                            </c:if>
                            <c:if test="${userInfo.tumUserType == 0}">
                                <a class="btn btn-danger" href="javascript:delCap(${userInfo.uiId})">取消队长</a>
                            </c:if>
                            <c:if test="${userInfo.tumUserType == 2}">
                                <a class="btn btn-success" href="javascript:agreeApplyTeam(${userInfo.uiId})">同意入队</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
    <c:if test="${teamInfo.userList == null || teamInfo.userList.size() <= 0 }">
        暂无队员数据！
    </c:if>

</div>





<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myFileModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示</h4>
            </div>
            <div class="modal-body" id="tips">
            </div>
            <input type="hidden" id="userId"/>
            <input type="hidden" id="type"/>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="sureBtn">确定</button>
            </div>
        </div>
    </div>
</div>



<script>
    $(document).ready(function(){
        $("#sureBtn").click(function () {
            window.location.href = "admin/team/updateTeamUser?teamId="+$("#tiId").val()+"&userId="+$("#userId").val()+"&type="+$("#type").val();
        });
    });
    //设为队长
    function setCap(userId) {
        $("#userId").val(userId);
        $("#type").val(0);
        $("#tips").html("确定要将该球友设为队长吗？");
        $("#myModal").modal("show");
    }
    //取消设为队长
    function delCap(userId) {
        $("#userId").val(userId);
        $("#type").val(1);
        $("#tips").html("确定要取消该球友的队长吗？");
        $("#myModal").modal("show");
    }
    //同意入队
    function agreeApplyTeam(userId) {
        $("#userId").val(userId);
        $("#type").val(1);
        $("#tips").html("确定要将该球友加入队伍吗？");
        $("#myModal").modal("show");
    }
    //移出队伍
    function delFromTeam(userId) {
        $("#userId").val(userId);
        $("#type").val(2);
        $("#tips").html("确定要将该球友移出队伍吗？此操作不可恢复！");
        $("#myModal").modal("show");
    }
</script>