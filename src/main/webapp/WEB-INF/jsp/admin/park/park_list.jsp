<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   	<title>球场列表</title>
	<jsp:include page="../include/commonInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="../include/header.jsp">
			<jsp:param value="nav_park" name="navId"></jsp:param>
		</jsp:include>
		
		<div class="content-wrapper">
			<section class="content-header">
		      <h1>球场列表</h1>
		    </section>
		    <section class="content">
		      <div class="row">
		        <div class="col-xs-12">
		          <div class="box">
           			<div class="box-body">
		            <div class="box-header">
		              <div style="float:left;margin-right:10px"><a class="btn btn-success" href="admin/park/parkAddUI">新增</a></div>
		              <form class="form-inline" name="searchForm" id="searchForm" style="margin-bottom: 15px;">
							<input type="hidden" id="page" name="page" value="${page }"/>
							<input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${rowsPerPage }"/>
							<div class="form-group">
								<input type="text" class="form-control" id="keyword" name="keyword" placeholder="球场名称" value="${keyword }"/>
							</div>
							是否可用：
							<input type="radio" name="state" value="1" <c:if test="${state == 1 }">checked</c:if> />是
							<input type="radio" name="state" value="0" <c:if test="${state == 0 }">checked</c:if> />否
							<button type="button" class="btn btn-success" id="searchBtn">搜索</button>
                            <button type="button" class="btn btn-success" id="importBtn">导入</button>
					  </form>
		            </div>
		            </div>
		            <!-- /.box-header -->
		            <div class="box-body">
		            <c:if test="${pageInfo.items != null && pageInfo.items.size() > 0 }">
		              <table id="example2" class="table table-bordered table-hover">
		                <thead>
		                <tr>
		                  	<th>序号</th>
                            <th>城市</th>
                            <th>球场名称</th>
                            <th>地址</th>
							<th>状态</th>
							<th><span class="glyphicon glyphicon-cog" aria-hidden="true"></span>操作</th>
		                </tr>
		                </thead>
		                <tbody>
		                <c:forEach items="${pageInfo.items}" var="p" varStatus="s">
     					<tr>
     						<td>${(pageInfo.rowsPerPage  * (pageInfo.nowPage -1)) + (s.index +1)  }</td>
							<td>${p.piCity }</td>
                            <td>${p.piName }</td>
                            <td>${p.piAddress }</td>
							<td>
								<c:if test="${p.piIsValid == 1 }">是</c:if>
								<c:if test="${p.piIsValid == 0 }">否</c:if>
							</td>
							<td>
								<c:if test="${p.piIsValid == 1 }">
									<a class="btn btn-success" href="/admin/park/parkEditUI?parkId=${p.piId}">
										查看
									</a>
									<a class="btn btn-danger" href="javascript:void(0);" onclick="resetPark(${p.piId})">注销</a>
								</c:if>
								<c:if test="${p.piIsValid == 0 }">
									<a class="btn btn-success" href="javascript:void(0);" onclick="resetPark(${p.piId})">恢复</a>
								</c:if>
							</td>
						</tr>
						</c:forEach>
		               	</tbody>		                		                
              		  </table>
              		  </c:if>
					  <c:if test="${pageInfo.items == null || pageInfo.items.size() <= 0 }">
							暂无数据！
					  </c:if>
					  <jsp:include page="../include/pojoPageInfo.jsp" >
							<jsp:param value="admin/park/list" name="act"/>
							<jsp:param value="searchForm" name="formName"/>
					  </jsp:include> 
            	   </div>
          	    </div>
          	 </div>
      	  </div>
	    </section>
	</div>
</div>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
	 aria-hidden="true">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
						aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">提示</h4>
			</div>
			<div class="modal-body">确定要这么操作吗</div>
			<input id="parkId" value="" type="hidden"/>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-primary" id="sureBtn">确定</button>
			</div>
		</div>
	</div>
</div>

    <!-- Modal -->
    <div class="modal fade" id="myFileModal" tabindex="-1" role="dialog" aria-labelledby="myFileModalLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" id="myFileModalLabel">提示</h4>
                </div>
                <div class="modal-body">
                    <input type="file" id="multiFileUpload" name="file" cssClass="form-control"/>
                    <br/>
                    <div class="col-md-12"></div><br/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>

<jsp:include page="../include/adminlteJsInclude.jsp"/>
<script type="text/javascript">
	$(document).ready(function(){
		//检索
		$("#searchBtn").bind("click", function(){
			var form = document.forms[0];
			form.action = "admin/park/list";
			$("#page").val(1);
			form.submit();
		});

		//回车提交表单
		$("#keyword").keydown(function(event){
			if(	event.keyCode == 13){
				$("#searchBtn").click();
			}
		});

		$("#sureBtn").click(function () {
		    window.location.href="admin/park/parkReset?parkId="+$("#parkId").val();
        });

        $("#importBtn").bind("click", function () {
            $('#myFileModal').modal('show');
            $('#multiFileUpload').fileupload({
                type: "POST",
                cache: false,
                async: false,
                dataType: "json",
                url: 'admin/park/importPark',
                success: function (json) {
                    if (json.success) {
                        $('#myFileModal').modal('hide');
                        // window.location.href="admin/sysuser/list";
                        //showMsg(ky.lang["upload.file.tip"]);
                        $("#loadImg").hide();
                        alert("导入成功");
                    } else {
                        alert(json.msg);
                    }
                }
            });
        });
	});
	function resetPark(parkId) {
		$("#myModal").modal("show");
        $("#parkId").val(parkId);
	}
</script>
</body>
</html>