<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<input type="hidden" name="cuId" id="cuId" value="${cmUser.cuId }"/>
<div class="box-body">
    <div class="form-group">
        <label for="cuUserName" class="col-sm-2 control-label">用户名</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="cuUserName" name="cuUserName" value="${cmUser.cuUserName }"
                   placeholder="用户名"
                <c:if test='${cmUser.cuId != null}'>
                       readonly
                </c:if>
            />
        </div>
    </div>
    <div class="form-group">
        <label for="cuHospital" class="col-sm-2 control-label">性别</label>
        <div class="col-sm-3">
            <input type="radio" name="cuSex" value="男" <c:if test="${cmUser.cuSex =='男' }">checked</c:if> />男
            <input type="radio" name="cuSex" value="女" <c:if test="${cmUser.cuSex =='女' }">checked</c:if> />女
        </div>
    </div>
    <div class="form-group">
        <label for="cuTelNo" class="col-sm-2 control-label">手机</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="cuTelNo" name="cuTelNo" value="${cmUser.cuTelNo }"
                   placeholder="手机"
                    <c:if test='${cmUser.cuTelNo != null}'>
                           readonly
                    </c:if>
            />
        </div>
    </div>
    <div class="form-group">
        <label for="cuEmail" class="col-sm-2 control-label">邮箱</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="cuEmail" name="cuEmail" value="${cmUser.cuEmail }" placeholder="邮箱"/>
        </div>
    </div>
    <div class="form-group">
        <label for="cuHospitalLevel" class="col-sm-2 control-label">医院等级</label>
        <div class="col-sm-3">
            <input type="hidden" name="cuHospitalLevel" id="cuHospitalLevel" value="${cmUser.cuHospitalLevel}"/>
            <select class="form-control" id="cuHospitalLevelSelect">
                <option value="">请选择</option>
                <option value="三级" <c:if test='${cmUser.cuHospitalLevel == "三级" }'>selected="selected"</c:if>>
                    三级
                </option>
                <option value="二级" <c:if test='${cmUser.cuHospitalLevel == "二级" }'>selected="selected"</c:if>>
                    二级
                </option>
                <option value="一级" <c:if test='${cmUser.cuHospitalLevel == "一级" }'>selected="selected"</c:if>>
                    一级
                </option>
                <option value="其他（手填）" <c:if test='${cmUser.cuHospitalLevel != "一级" && cmUser.cuHospitalLevel != "二级" && cmUser.cuHospitalLevel != "三级"}'>selected="selected"</c:if>>
                    其他（手填）
                </option>
            </select><br/>
            <input type="text" style="display: none;" class="form-control" value="${cmUser.cuHospitalLevel}" placeholder="医院等级" id="otherLevel" onblur="changeLevelValue(this.value)"/>
        </div>
    </div>
    <div class="form-group">
        <label for="cuType" class="col-sm-2 control-label">用户类型</label>
        <div class="col-sm-3">
            <select class="form-control" name="cuType" id="cuType">
                <option value="">请选择</option>
                <option value="1" <c:if test='${cmUser.cuType == 1 }'>selected="selected"</c:if>>
                    理事会
                </option>
                <option value="2" <c:if test='${cmUser.cuType == 2 }'>selected="selected"</c:if>>
                    委员会
                </option>
                <option value="3" <c:if test='${cmUser.cuType == 3 }'>selected="selected"</c:if>>
                    普通用户
                </option>
            </select>
        </div>
    </div>
</div>

<div class="box-footer">
    <div class="col-xs-push-2 col-xs-2">
        <a class="btn btn-default" href=admin/user/userList" role="button">取消</a>
    </div>
    <div class="col-xs-push-4 col-xs-2">
        <button type="submit" class="btn btn-info pull-right">保存</button>
    </div>
</div>