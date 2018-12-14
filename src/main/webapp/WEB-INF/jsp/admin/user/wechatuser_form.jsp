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
        <label for="cuHospital" class="col-sm-2 control-label">医院名称</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="cuHospital" name="cuHospital" value="${cmUser.cuHospital }"
                   placeholder="医院名称">
        </div>
    </div>
    <div class="form-group">
        <label for="cuDept" class="col-sm-2 control-label">科室</label>
        <div class="col-sm-3">
            <input type="hidden" name="cuDept" id="cuDept" value="${cmUser.cuDept}"/>
            <select class="form-control" id="cuDeptSelect">
                <option value="">请选择</option>
                <%--A．心内科   B．心外科  C．介入科 D．影像科   E.急诊科  F. 麻醉科  G.其他（手填）--%>
                <option value="心内科" <c:if test='${cmUser.cuDept == "心内科" }'>selected="selected"</c:if>>
                    心内科
                </option>
                <option value="心外科" <c:if test='${cmUser.cuDept == "心外科" }'>selected="selected"</c:if>>
                    心外科
                </option>
                <option value="介入科" <c:if test='${cmUser.cuDept == "介入科" }'>selected="selected"</c:if>>
                    介入科
                </option>
                <option value="影像科" <c:if test='${cmUser.cuDept == "影像科" }'>selected="selected"</c:if>>
                    影像科
                </option>
                <option value="急诊科" <c:if test='${cmUser.cuDept == "急诊科" }'>selected="selected"</c:if>>
                    急诊科
                </option>
                <option value="麻醉科" <c:if test='${cmUser.cuDept == "麻醉科" }'>selected="selected"</c:if>>
                    麻醉科
                </option>
                <option value="其他（手填）"
                        <c:if test='${cmUser.cuDept != "心内科" && cmUser.cuDept != "心外科" && cmUser.cuDept != "介入科" && cmUser.cuDept != "影像科" && cmUser.cuDept != "急诊科" && cmUser.cuDept != "麻醉科"}'>
                            selected="selected"
                        </c:if>
                >
                    其他（手填）
                </option>
            </select><br/>
            <input type="text" style="display: none;" class="form-control" value="${cmUser.cuDept}" placeholder="科室" id="otherDept" onblur="changeDeptValue(this.value)"/>
        </div>
    </div>
    <div class="form-group">
        <label for="cuProfessional" class="col-sm-2 control-label">职称</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" id="cuProfessional" name="cuProfessional" value="${cmUser.cuProfessional }"
                   placeholder="医院">
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
    <div class="form-group" id="clubDiv"
            <c:if test="${cmUser.cuType == '1'}">
                style="display: none;"
            </c:if>
    >
        <label for="cuClub" class="col-sm-2 control-label">所属俱乐部</label>
        <div class="col-sm-3">
            <select class="form-control" name="cuClub" id="cuClub">
                <option value="">请选择</option>
                <option value="1" <c:if test='${cmUser.cuClub == 1 }'>selected="selected"</c:if>>
                    益心论道
                </option>
                <option value="2" <c:if test='${cmUser.cuClub == 2 }'>selected="selected"</c:if>>
                    心脏内外科医师沙龙
                </option>
                <option value="3" <c:if test='${cmUser.cuClub == 3 }'>selected="selected"</c:if>>
                    北京青年CTO俱乐部
                </option>
                <option value="4" <c:if test='${cmUser.cuClub == 4 }'>selected="selected"</c:if>>
                    女医师俱乐部
                </option>
            </select>
        </div>
    </div>
</div>
<%--
<div class="box-header with-border">
    <h3 class="box-title">微信信息：</h3>
</div>

<div class="box-body">
    <div class="form-group">
        <label for="cuUserName" class="col-sm-2 control-label">openId</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" value="${wechatUser.cwuOpenid }" readonly/>
        </div>
    </div>
    <div class="form-group">
        <label for="cuHospital" class="col-sm-2 control-label">微信昵称</label>
        <div class="col-sm-3">
            <input type="text" class="form-control" value="${wechatUser.cwuNickname }" readonly/>
        </div>
    </div>
</div>--%>

<div class="box-footer">
    <div class="col-xs-push-2 col-xs-2">
        <a class="btn btn-default" href=admin/user/userList" role="button">取消</a>
    </div>
    <div class="col-xs-push-4 col-xs-2">
        <button type="submit" class="btn btn-info pull-right">保存</button>
    </div>
</div>