<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>菜单管理</title>
    <jsp:include page="../../include/commonInclude.jsp"></jsp:include>
    <link rel="stylesheet" href="static/lib/zTree_v3-3.5.23/css/zTreeStyle/zTreeStyle.css" type="text/css"/>
    <script type="text/javascript" src="static/lib/zTree_v3-3.5.23/js/jquery.ztree.core.js"></script>
    <script type="text/javascript" src="static/lib/zTree_v3-3.5.23/js/jquery.ztree.excheck.js"></script>
    <script type="text/javascript" src="static/js/admin/commonutil.js"></script>
    <script type="text/javascript" src="static/js/admin/wechat/wechatMenu.js?dt=20170104"></script>
    <script type="text/javascript" src="static/js/admin/wechat/buildHtml4Menu.js?dt=20180914"></script>

    <style type="text/css">
        .menu div, .main div {
            padding: 5px;
        }

        .info_table {
            width: 100%;
            border-collapse: collapse;
        }

        .info_table td {
            padding: 7px;
            border: 1px solid #ccc;
        }

        .info_table thead td {
            background-color: #efefef;
        }

        .info_input {
            font-size: 12px;
            padding: 3px;
            border: 1px solid #ccc;
            width: 170px;
        }

        .info_select {
            font-size: 12px;
            padding: 3px;
            border: 1px solid #ccc;
            width: 400px;
        }

        .rq {
            color: red;
            min-width: 60px;
        }

        .td_left {
            text-align: left;
        }

        .td_right {
            text-align: right;
        }

        .treeBlank {
            display: inline-block;
            width: 20px;
        }

        .treeBlank_spe, .exp, .col {
            display: inline-block;
            width: 15px;
            cursor: pointer;
            font-size: 15px;
            font-weight: bold;
        }

        .table-responsive tbody tr:hover {
            background-color: #ABCDEF;
            cursor: hand;
        }
    </style>
</head>
<%--onbeforeunload="return checkIsSave();"--%>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../../include/header.jsp">
        <jsp:param value="nav_wechat" name="navId"></jsp:param>
        <jsp:param value="nav_wechatMenu" name="subNavId"></jsp:param>
    </jsp:include>

    <input type="hidden" id="siteId" name="siteId" value="${siteId }"/>
    <input type="hidden" id="isAutoReply" name="isAutoReply" value="1"/>

    <div class="content-wrapper">
        <section class="content-header">
            <h1>菜单详情</h1>
            <ol class="breadcrumb">
                <li><a href="admin/wechat/menu/menuGroupList?siteId=${siteId}"><i class="fa fa-dashboard"></i>菜单列表</a></li>
                <li class="active">菜单详情</li>
            </ol>
        </section>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div class="box box-info">
                        <div class="box-header with-border">
                            <h3 class="box-title">菜单详情</h3>
                        </div>
                        <input type="hidden" name="cpId" id="id" value="${project.cpId }"/>
                        <div class="box-body" style="height:900px;">
                            <div class="box-body">
                                <div class="form-group" style="width:40%;">
                                    <label for="wcmgName" class="col-sm-3 control-label">菜单组名称</label>
                                    <div class="col-sm-7">
                                        <input type="text" class="form-control" id="wcmgName"
                                               value="${menuGroup.wcmgName }" name="wcmgName" placeholder="菜单组名称">
                                        <%--组ID--%>
                                        <input type="hidden" value="${menuGroup.wcmgId}" id="groupId">
                                    </div>
                                </div>
                                <div style="margin-left:40%;margin-top:-3%;"></br>提示：</br>
                                    1、最多只能有两级菜单。</br>
                                    2、没有二级菜单的一级菜单，必须指定菜单内容。</br>
                                    2、二级菜单必须指定菜单内容。</br>
                                </div>
                                <br/>
                                <br/>
                                <br/>
                                <br/>
                                <div class="form-group" style="width:40%;">
                                    <label for="wcmgName" class="col-sm-3 control-label">发布状态：</label>
                                    <div class="col-sm-3" id="youxiaozhuangtai">
                                        <c:if test="${menuGroup.wcmgIsValid == 1 }">已发布</c:if>
                                        <c:if test="${menuGroup.wcmgIsValid == 0 }">未发布</c:if>
                                    </div>
                                    <input type="hidden" class="form-control" id="wcmgIsValid"
                                           value="${menuGroup.wcmgIsValid}" name="wcmgIsValid">
                                </div>
                            </div>

                            <div class="form-group">
                                <!-- 学科树表格 -->
                                <div class="info_table"
                                     style="min-height: 450px;width:40%;margin-top:3%;margin-left:3%;">
                                    <div class="box-body">
                                        <!-- 按钮 -->
                                        <div class="col-md-1">
                                            <a class="btn btn-success" id="addParentBtn">新增菜单</a>
                                        </div>
                                    </div>
                                    <table class="info_table" id="subjectTable">
                                        <thead>
                                        <tr>
                                            <td>菜单标题</td>
                                            <td>操作</td>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        </tbody>
                                    </table>
                                </div>

                                <%--类型选择--%>
                                <div class="form-group"
                                     style="border:0px solid red; width:45%;margin-left:45%;margin-top:-50%;">
                                    <div class="col-sm-10" style="display: none;" id="childrenDiv">
                                        <div class="panel panel-warning">
                                            <div class="panel-collapse collapse" id="childrenDivShow">
                                                <div class="panel-body " style="background-color: #f4f6f9;" id="keywordDiv">
                                                    <input type="hidden" required="required" name="wcmgId"
                                                           value="${menuGroup.wcmgId}" style="width:300px;"
                                                           class="form-control" disabled="disabled"/>
                                                    <input type="hidden" required="required" name="wcmId" id="wcmId"
                                                           style="width:300px;" class="form-control"
                                                           disabled="disabled"/>
                                                    <input type="hidden" required="required" name="wcmParentId"
                                                           id="wcmParentId" style="width:300px;" class="form-control"
                                                           disabled="disabled"/>
                                                    <input type="hidden" required="required" name="wcmReplyBizId"
                                                           id="wcmReplyBizId" style="width:300px;" class="form-control"
                                                           disabled="disabled"/>

                                                    <div class="form-group">
                                                        <label for="parentName" class="control-label ">父菜单</label>
                                                        <div class="form-controls">
                                                            <input required="required" name="parentName" id="parentName"
                                                                   style="width:150px;" class="form-control"
                                                                   disabled="disabled"/>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="wcmName" class="control-label "><spring:message code ="admin.wechatMenu.parent"/><spring:message code ="admin.field.title"/></label>
                                                        <div class="form-controls">
                                                            <input required="required" name="wcmName" id="wcmName"
                                                                   class="form-control" style="width:150px;"
                                                                   placeholder="<spring:message code ="admin.wechatMenu"/><spring:message code ="admin.field.title"/>"/>
                                                            <span class="help-block"><spring:message code ="admin.wechatMenu.wordCountTips"/></span>
                                                        </div>
                                                    </div>
                                                    <div class="form-group">
                                                        <label class="control-label"><spring:message code ="admin.wechatMenu"/><spring:message code ="admin.field.content"/></label>
                                                        <div class="form-controls">
                                                            <input type="radio" id="sendMsg" name="wcmType" value="1"
                                                                   <c:if test="${wcmType == 1 }">checked</c:if> />发消息
                                                            <input type="radio" id="jumpLink" name="wcmType" value="2"
                                                                   <c:if test="${wcmType == 2 }">checked</c:if> />跳转网页
                                                        </div>
                                                    </div>
                                                    <div class="form-group" id="huifuneirong" style="display: none;">
                                                        <label class="control-label"><spring:message code ="admin.field.reply"/><spring:message code ="admin.field.content"/></label>
                                                        <div class="form-controls">
                                                            <div class="radio">
                                                                <label>
                                                                    <input type="radio" id="wcmReplyType1"
                                                                           name="wcmReplyType" value="0"
                                                                           <c:if test="${wcmReplyType == 0 }">checked</c:if> /><spring:message code ="admin.material.material"/>
                                                                </label>
                                                            </div>
                                                            <div class="radio">
                                                                <label>
                                                                    <input type="radio" id="wcmReplyType2"
                                                                           name="wcmReplyType" value="1"
                                                                           <c:if test="${wcmReplyType == 1 }">checked</c:if> /><spring:message code ="admin.field.picture"/>
                                                                </label>
                                                            </div>
                                                            <div class="radio">
                                                                <label>
                                                                    <input type="radio" id="wcmReplyType3"
                                                                           name="wcmReplyType" value="2"
                                                                           <c:if test="${wcmReplyType == 2 }">checked</c:if> /><spring:message code ="admin.wechatMenu.triggerKey"/>
                                                                </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="form-group" id="yemiandizhi" style="display: none;">
                                                        <label class="control-label"><spring:message code ="admin.field.url"/></label>
                                                        <div class="form-controls">
                                                            <span class="help-block"><spring:message code ="admin.wechatMenu.triggerUrlTips"/></span>
                                                            <input type="text" class="form-control" id="wcmLink"
                                                                   name="wcmLink" value="${menu.wcmLink}"/>
                                                            <span class="help-block"><spring:message code ="admin.wechatMenu.triggerUrlTips1"/></span>
                                                        </div>
                                                    </div>
                                                    <%--选择的素材--%>
                                                    <div class="form-group" id="replyInfoDiv" style="display: none;">
                                                        <label class="control-label" id="replyTypeMsg"></label>
                                                        <input type="hidden" id="materialIds" value="${materialIds}"/>
                                                        <div class="form-controls" id="materialList">

                                                        </div>
                                                        <br/>
                                                    </div>
                                                </div>

                                                <div class="box-footer">
                                                    <div class="col-xs-push-4 col-xs-3">
                                                        <button id="addtopicBtn_" type="button" class="btn btn-info pull-right"><spring:message code ="admin.modal.sure"/></button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="box-footer">
                                <%--<div class="col-xs-push-2 col-xs-2">
                                    <a class="btn btn-default" href="/admin/wechat/menu/menuGroupList" role="button"><spring:message code ="cancel"/></a>
                                </div>--%>
                                <div class="col-xs-push-5 col-xs-2">
                                    <button type="button" class="btn btn-info pull-right" id="onlySave"><spring:message code ="admin.material.saveLocal"/></button>
                                </div>
                                <div class="col-xs-push-5 col-xs-2">
                                    <button type="button" class="btn btn-info pull-right" id="pushMenu"><spring:message code ="admin.wechatMenu.saveAndRelease"/></button>
                                </div>
                            </div>
                        </div>
                    </div>


                    <!-- Modal -->
                    <div class="modal fade" id="pushModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
                         aria-hidden="true">
                        <div class="modal-dialog modal-sm">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                            aria-hidden="true">&times;</span></button>
                                    <h4 class="modal-title"><spring:message code ="admin.operation.confirm"/></h4>
                                </div>
                                <div class="modal-body" id="pushModalMsg"><spring:message code ="admin.modal.tipBeforeRelease"/></div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code ="close"/></button>
                                    <button type="button" class="btn btn-primary" id="surePushBtn"><spring:message code ="admin.modal.sure"/></button>
                                </div>
                            </div>
                        </div>
                    </div>


                    <!-- Modal -->
                    <div class="modal fade" id="keywordModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
                         aria-hidden="true">
                        <div class="modal-dialog modal-sm" style="width:35%;">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                            aria-hidden="true">&times;</span></button>
                                    <h4 class="modal-title"><spring:message code ="admin.wechatMenu.chooseKey"/></h4>
                                </div>
                                <div class="modal-body" id="guanjianzichooseDiv">
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code ="close"/></button>
                                    <button type="button" class="btn btn-primary" id="sureKyBtn"><spring:message code ="admin.modal.sure"/></button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>

<%--选择图文modal 只能选择一条--%>
<input type="hidden" id="isAddToMenu" value="1"/>
<jsp:include page="../../include/commonChooseModal.jsp"></jsp:include>

<jsp:include page="../../include/adminlteJsInclude.jsp"/>
</body>
</html>