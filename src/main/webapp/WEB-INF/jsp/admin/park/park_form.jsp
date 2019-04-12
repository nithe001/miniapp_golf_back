<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .divBorder {
        border: 1px solid #00acd6;
    }

    .width100 {
        width: 100%;
    }

    .width20 {
        width: 20%;
    }

    .width10 {
        width: 10%;
    }
</style>
<input type="hidden" name="piId" id="piId" value="${park.piId }"/>
<div class="box-body">
    <div class="form-group">
        <label for="piName" class="col-sm-2 control-label">球场名</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="piName" name="piName" value="${park.piName}" placeholder="球场名"
            <c:if test='${park.piId != null}'>
                   readonly
            </c:if>
            >
        </div>
    </div>
    <div class="form-group">
        <label for="piLogo" class="col-sm-2 control-label">logo</label>
        <div class="col-sm-5">
            <img src="${park.piLogo}" id="piLogo"/>
            <input type="file" value="上传Logo"/>
        </div>
    </div>
    <div class="form-group">
        <label for="piLat" class="col-sm-2 control-label">球场经度</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="piLat" name="piLat" value="${park.piLat}"
                   placeholder="球场经度">
        </div>
    </div>
    <div class="form-group">
        <label for="piLng" class="col-sm-2 control-label">球场纬度</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="piLng" name="piLng" value="${park.piLng}"
                   placeholder="球场纬度">
        </div>
    </div>

    <div class="form-group">
        <label for="zone" class="col-sm-2 control-label">球场分区</label>
        <div class="col-sm-7" id="zone">
            <input type="button" class="btn btn-success" value="添加分区" id="addFenqu"/><br/><br/>
            <div id="fenquDiv">
                <c:if test="${parkZoneList != null && parkZoneList.size() > 0 }">
                    <table id="example2" class="table table-bordered table-hover">
                        <thead>
                        <tr>
                            <th>分区名称</th>
                            <th>球洞号</th>
                            <th>球洞标准杆</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${parkZoneList}" var="zone" varStatus="s">
                            <tr>
                                <td>${zone.ppZoneName }</td>
                                <td>${zone.ppHoleNum }</td>
                                <td>${zone.ppHoleStandardRod }</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div style="margin-left:55%;">总计：</div>
                </c:if>
            </div>
        </div>
    </div>
</div>
<%--

<div class="box-footer">
    <div class="col-xs-push-2 col-xs-2">
        <a class="btn btn-default" href=admin/park/list" role="button">取消</a>
    </div>
    <div class="col-xs-push-4 col-xs-2">
        <button type="submit" class="btn btn-info pull-right">保存</button>
    </div>
</div>


<!-- Modal -->
<div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示</h4>
            </div>
            <div class="modal-body">确定要删除吗？</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="sureDelBtn">确定</button>
            </div>
        </div>
    </div>
</div>--%>
