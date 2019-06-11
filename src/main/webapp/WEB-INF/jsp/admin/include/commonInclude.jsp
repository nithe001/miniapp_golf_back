<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String path = request.getContextPath();
//本地用
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//正式环境用
//String basePath = request.getHeader("X-Forwarded-Scheme")+"://"+request.getServerName()+path+"/";
%>
<base href="<%=basePath%>">
<meta name="viewport" content="width=device-width, initial-scale=1"><!-- bootstrap -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">



<script>var GLOBAL_BASE_PATH = "<%=basePath%>"</script>
<!-- jQuery 2.2.3 -->
<script src="static/lib/adminlte-2.3.7/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="static/lib/adminlte-2.3.7/bootstrap/js/bootstrap.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="static/lib/adminlte-2.3.7/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="static/lib/adminlte-2.3.7/dist/css/AdminLTE.min.css">
<link rel="stylesheet" href="static/lib/adminlte-2.3.7/dist/css/skins/skin-blue.min.css">

<!-- Font Awesome -->
<link rel="stylesheet" href="static/lib/font-awesome-4.7.0/css/font-awesome.min.css">
<!-- Ionicons -->
<link rel="stylesheet" href="static/lib/ionicons-2.0.1/css/ionicons.min.css">

<!-- form表单验证 -->
<link href="static/lib/jQuery-Validation-Engine-2.6.4/css/validationEngine.jquery.css" rel="stylesheet">
<script type="text/javascript" src="static/lib/jQuery-Validation-Engine-2.6.4/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="static/lib/jQuery-Validation-Engine-2.6.4/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="static/lib/jQuery-Validation-Engine-2.6.4/js/contrib/other-validations.js"></script>

<!-- 日期控件 -->
<link rel="stylesheet" href="static/lib/adminlte-2.3.7/plugins/datepicker/datepicker3.css">
<script src="static/lib/adminlte-2.3.7/plugins/datepicker/bootstrap-datepicker.js"></script>
<script src="static/lib/adminlte-2.3.7/plugins/datepicker/locales/bootstrap-datepicker.zh-CN.js"></script>

<!-- 日期控件 :带时分秒-->
<link href="static/lib/bootstrap-datetimepicker-2.3.4/css/bootstrap-datetimepicker.css" rel="stylesheet">
<script src="static/lib/bootstrap-datetimepicker-2.3.4/js/bootstrap-datetimepicker.js"></script>
<script src="static/lib/bootstrap-datetimepicker-2.3.4/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>

<!-- UEditor -->
<!-- 配置文件 -->
<script type="text/javascript" src="static/lib/ueditor/ueditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="static/lib/ueditor/ueditor.all.js"></script>

<script src="static/lib/jQuery-File-Upload-9.9.3/js/vendor/jquery.ui.widget.js"></script>
<script src="static/lib/jQuery-File-Upload-9.9.3/js/jquery.iframe-transport.js"></script>
<script src="static/lib/jQuery-File-Upload-9.9.3/js/jquery.fileupload.js"></script>


<!-- jQuery trunk8 -->
<script type="text/javascript" src="static/lib/trunk8.js" ></script>