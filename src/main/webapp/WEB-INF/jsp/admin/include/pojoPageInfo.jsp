 <%-- 分页信息POJOPageInfo --%>
<%@ page language="java" import="java.util.* " pageEncoding="UTF-8"%>
<%@ page import="com.kingyee.common.model.POJOPageInfo" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<script type="text/javascript">
<!--
	function goPage(toPage, strAction, objForm) {
		var page = document.getElementById("page");
		if(page) {
			page.value = toPage;
		}
		var form = objForm;
		if(!form) {
			form = document.forms[0];
		}else{
			form = eval("document."+objForm);
		}
		if(strAction != null && strAction != "null" && strAction != ''){
			form.action = strAction;
		}
		if(submitType && submitType == 'ajax'){
			formId = form.id;
			$('#' + contentId).load(strAction,$('#' + formId).serialize());
		}else{
			//form.method = "post";
			form.target = "_self";
			form.submit();
		}
	}
//-->
</script>
<%	
	POJOPageInfo pageInfo = (POJOPageInfo)request.getAttribute("pageInfo");
	
	/*执行操作的Action*/
	String act = request.getParameter("act");
	/*要提交的form名*/
	String formName = request.getParameter("formName");
	if(formName == null) {
		formName = "";
	}
	/*是否AJax方式提交*/
	String submitType = request.getParameter("submitType");
	if(null == submitType){
		submitType = "";
	}
	/*Ajax提交后显示Div的Id*/
	String contentId = request.getParameter("contentId");
	if(null == contentId){
		contentId = "content";
	}
	if(pageInfo != null) {
		int size = 3;
		long countRows = pageInfo.getCount();
		int rowsPerPage = pageInfo.getRowsPerPage();
		int totalPage = (Integer.valueOf(pageInfo.getCount() + "")) / rowsPerPage
			+ ((pageInfo.getCount()%rowsPerPage) > 0?1:0);
		int nowPage = pageInfo.getNowPage();
		if(nowPage <=0) {
			nowPage = 1;
		}
		int firstDisplayPage = nowPage-size;
		int lastDisplayPage = nowPage+size;
		if(firstDisplayPage <= 0) {
			firstDisplayPage = 1;
		}
		if(lastDisplayPage > totalPage) {
			lastDisplayPage = totalPage;
		}
%>
	<script language="javascript">
		nowPage = <%= nowPage %>;
		var objForm,formId;
		if("<%=formName%>" == "") {
			objForm = document.forms[0];/** 获取第一个表单*/
			formId = $(document.forms[0]).attr('id');
		}else {
			objForm = eval("document.<%=formName%>");
			formId = "<%=formName%>";
		}
		var submitType = "<%=submitType%>";
		var contentId = "<%=contentId%>";
		$(function(){
			/** 每页显示几条 start */
			var rowsPerPage = $("#rowsPerPage").val()
			$("#rows").val(rowsPerPage);
			//回车
			$("#rows").keydown(function(e){
				if(e.keyCode==13){ 
					$("#page").val(1);
					$("#rowsPerPage").val($("#rows").val());
					if(submitType && submitType == 'ajax'){
						$('#' + contentId).load("<%=act%>",$('#' + formId).serialize());
					}else{
						objForm.target = "_self";
						objForm.submit();
					}
				}
			});
			/** 每页显示几条 end*/
		}) 
		 
	</script>
	
	<div class="row">
		<div class="col-md-5">
			<nav>
				<ul class="pagination">
			  	<%
					if (totalPage > 1 && nowPage > 1) {
				%>
				    <li><a href='javascript:goPage(1,"<%=act%>","<%=formName%>")' aria-label="Previous"><span aria-hidden="true">&lt;&lt;</span></a></li>
				    <li><a href='javascript:goPage(<%=nowPage - 1%>,"<%=act%>","<%=formName%>")' aria-label="Previous"><span aria-hidden="true">&lt;</span></a></li>
				<% } else { %>
					<li class="disabled"><a href="javascript:void(0);" aria-label="Previous"><span aria-hidden="true">&lt;&lt;</span></a></li>
					<li class="disabled"><a href="javascript:void(0);" aria-label="Previous"><span aria-hidden="true">&lt;</span></a></li>			
				<%	}
					if (totalPage >= 1) {
						for(int i=firstDisplayPage; i<=lastDisplayPage; i++) {
							if (i == nowPage) {
								out.print("<li class='active'><a href='javascript:void(0);'>"+i+"</a></li>");
							} else {
								out.print("<li><a href='javascript:goPage(" + i + ",\""+act+"\",\""+formName+ "\")'>"+i+"</a></li>");
							}
					  	}
					}
					if (totalPage > 1 && nowPage < totalPage) {
		 		%>
				    <li><a href='javascript:goPage(<%=nowPage + 1%>,"<%=act%>","<%=formName%>")'  aria-label="Previous"><span aria-hidden="true">&gt;</span></a></li>
			    	<li><a href='javascript:goPage(<%=totalPage%>,"<%=act%>","<%=formName%>")'   aria-label="Previous"><span aria-hidden="true">&gt;&gt;</span></a></li>
				<% } else { %>
					<li class="disabled"><a href="javascript:void(0);" aria-label="Previous"><span aria-hidden="true">&gt;</span></a></li>			
					<li class="disabled"><a href="javascript:void(0);" aria-label="Previous"><span aria-hidden="true">&gt;&gt;</span></a></li>
				<%	} %>
				
			  </ul>
			  
			</nav>
			
		</div>
		<div class="col-md-3" style="margin-top: 20px;" >
			<div class="input-group">
				<span class="input-group-addon">每页显示</span>
			  		<input name="rows" id="rows"  class="form-control" />
			  	<span class="input-group-addon">条</span>
			</div>
		</div>
	</div>
<% } %>
