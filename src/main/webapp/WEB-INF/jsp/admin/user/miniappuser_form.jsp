<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" name="uiId" id="uiId" value="${userInfo.uiId }"/>
<div class="box-body">
    用户微信信息：
    <div class="form-group">
        <label for="cuUserName" class="col-sm-2 control-label">openid</label>
        <div class="col-sm-3">${userInfo.uiOpenId}</div>
    </div>
    <div class="form-group">
        <label for="cuUserName" class="col-sm-2 control-label">昵称</label>
        <div class="col-sm-3">${userInfo.uiNickName}</div>
    </div>

    <hr>
    用户个人信息：
    <div class="form-group">
        <label for="cuUserName" class="col-sm-2 control-label">用户名</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiRealName" name="uiRealName" value="${userInfo.uiRealName }"
                   placeholder="用户名"/>
        </div>
    </div>
    <div class="form-group">
        <label for="se" class="col-sm-2 control-label">性别</label>
        <div class="col-sm-3">
            <input type="radio" name="uiSex" value="男" <c:if test="${userInfo.uiSex =='男' }">checked</c:if> />男
            <input type="radio" name="uiSex" value="女" <c:if test="${userInfo.uiSex =='女' }">checked</c:if> />女
        </div>
    </div>
    <div class="form-group">
        <label for="uiTelNo" class="col-sm-2 control-label">手机</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiTelNo" name="uiTelNo" value="${userInfo.uiTelNo }"
                   placeholder="手机"
            />
        </div>
    </div>
    <div class="form-group">
        <label for="uiEmail" class="col-sm-2 control-label">邮箱</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiEmail" name="uiEmail" value="${userInfo.uiEmail }" placeholder="邮箱"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiGraduateSchool" class="col-sm-2 control-label">毕业学校</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiGraduateSchool" name="uiGraduateSchool"
                   value="${userInfo.uiGraduateSchool }" placeholder="毕业学校"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiGraduateDepartment" class="col-sm-2 control-label">毕业院系</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiGraduateDepartment" name="uiGraduateDepartment"
                   value="${userInfo.uiGraduateDepartment }" placeholder="毕业院系"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiGraduateTime" class="col-sm-2 control-label">毕业时间</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiGraduateTime" name="uiGraduateTime"
                   value="${userInfo.uiGraduateTime}" placeholder="毕业时间"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiMajor" class="col-sm-2 control-label">专业</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiMajor" name="uiMajor"
                   value="${userInfo.uiMajor}" placeholder="专业"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiStudentId" class="col-sm-2 control-label">学号</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiStudentId" name="uiStudentId"
                   value="${userInfo.uiStudentId}" placeholder="学号"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiWorkUnit" class="col-sm-2 control-label">工作单位</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiWorkUnit" name="uiWorkUnit"
                   value="${userInfo.uiWorkUnit}" placeholder="工作单位"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiPost" class="col-sm-2 control-label">职务</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiOccupation" name="uiOccupation"
                   value="${userInfo.uiOccupation}" placeholder="职务"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiAddress" class="col-sm-2 control-label">常住地</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiAddress" name="uiAddress"
                   value="${userInfo.uiAddress}" placeholder="常住地"/>
        </div>
    </div>
    <div class="form-group">
        <label for="uiHomeCourt" class="col-sm-2 control-label">主场</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="uiHomeCourse" name="uiHomeCourse"
                   value="${userInfo.uiHomeCourse}" placeholder="主场"/>
        </div>
    </div>

    <div class="form-group">
        <label for="uiType" class="col-sm-2 control-label">用户类型</label>
        <div class="col-sm-3">
            <select class="form-control" name="uiType" id="uiType">
                <option value="">请选择</option>
                <option value="0" <c:if test='${userInfo.uiType == 1 }'>selected="selected"</c:if>>
                    理事会
                </option>
                <option value="1" <c:if test='${userInfo.uiType == 2 }'>selected="selected"</c:if>>
                    委员会
                </option>
                <option value="2" <c:if test='${userInfo.uiType == 3 }'>selected="selected"</c:if>>
                    普通用户
                </option>
            </select>
        </div>
    </div>

    <div class="form-group">
        <label for="uiIsValid" class="col-sm-2 control-label">是否有效</label>
        <div class="col-sm-3">
            <input type="radio" name="uiIsValid" value="是" <c:if test="${userInfo.uiIsValid =='男' }">checked</c:if> />是
            <input type="radio" name="uiIsValid" value="否" <c:if test="${userInfo.uiIsValid =='女' }">checked</c:if> />否
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