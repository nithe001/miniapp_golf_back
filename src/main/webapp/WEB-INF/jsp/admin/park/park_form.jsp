<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .formcontrol{
        display: inline;
        width: 15%;
        height: 34px;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.42857143;
        color: #555;
        background-color: #fff;
        border: 1px solid #ccc;
        border-radius: 4px;
    }

    .divBorder{
        border: 1px solid #00acd6;
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
        <label for="piAddress" class="col-sm-2 control-label">球场地理位置</label>
        <div class="col-sm-5">
            <input type="text" class="form-control" id="piAddress" name="piAddress" value="${park.piAddress}"
                   placeholder="球场地理位置">
        </div>
    </div>

    "每个球场分多个区（A-Z）
    每个区有9个球洞（序号1-9），
    每个球洞有标准杆数（数字1-7）
    每个球洞有五个球T，
    每个球T对应一个距离"

    <div class="form-group">
        <label for="piAddress" class="col-sm-2 control-label">球场分区</label>
        <div class="col-sm-7">
            <input type="button" class="btn btn-success" value="添加分区" id="addFenqu"/><br/><br/>
            <div id="fenquDiv">

            </div>
        </div>
    </div>
</div>

<div class="box-footer">
    <div class="col-xs-push-2 col-xs-2">
        <a class="btn btn-default" href=admin/park/list" role="button">取消</a>
    </div>
    <div class="col-xs-push-4 col-xs-2">
        <button type="submit" class="btn btn-info pull-right">保存</button>
    </div>
</div>


<!-- Modal -->
<div class="modal fade" id="fenquModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">分区名称</h4>
            </div>
            <div class="modal-body">
                <input type="text" class="form-control" placeholder="请输入分区名称" id="fqName"/>
            </div>
            <input id="parkId" value="" type="hidden"/>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="sureBtn">确定</button>
            </div>
        </div>
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
</div>