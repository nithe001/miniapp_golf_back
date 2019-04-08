<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../include/commonInclude.jsp"></jsp:include>
    <title>赛事活动列表</title>
    <style type="text/css">
        .head {
            width: 100%;
        }

        .head ul {
            list-style: none;
            margin: 0px;
        }

        .head li {
            float: left;
            width: 25%;
            text-align: center;
            line-height: 1.75rem;
            margin-bottom: 0.9375rem;
            margin-left: 2%;
        }

        .head ul li {
            color: #888888;
            font-weight: bold;
            font-size: 1.125rem;
            cursor: pointer;
        }

        .head input {
            background: url(static/img/0CCD6A9F-199D-443E-8409-46011FE9EE99/DF6178E1-2D79-465D-B2A7-454251087423@1x.png)no-repeat center center;
            background-color: #F3F3F3;
            width: 100%;
        }

        .head input {
            border-style: none;
            border-radius: 0.625rem;
            text-indent: 0.625rem;
            outline: none;
            height: 1.5rem;
        }

        .head input::-webkit-input-placeholder {
            font-size: 0.875rem;
            padding-left: 2.5rem;
            text-align: center;
        }

        .head input::-moz-placeholder {
            font-size: 0.875rem;
            padding-left: 2.5rem;
            text-align: center;
        }

        .head input:-ms-input-placeholder {
            font-size: 0.875rem;
            padding-left: 2.5rem;
            text-align: center;
        }

        .match {
            width: 100%;
            height: 50px;
            margin-top: 0.625rem;
            background-color: #FAFAFA;
        }

        .match div {
            float: left;
            width: 40%;
            height: 1.875rem;
            margin-top: 0.625rem;
            margin-left: 8%;
            font-size: 1.0625rem;
            color: #888888;
            text-align: center;
            line-height: 1.875rem;
        }

        .match_click {
            border: 0.0625rem solid #094527;
            border-radius: 0.3125rem;
        }
    </style>
    <script>
        function tab_page(tab_class){
            if(tab_class=="比赛"){
                $(".tab_1").css('display','');
                $(".tab_2").css('display','none');
                $(".tab_3").css('display','none');
            }else if(tab_class=="报名"){
                $(".tab_1").css('display','none');
                $(".tab_2").css('display','');
                $(".tab_3").css('display','none');
            }else if(tab_class=="创建"){
                $(".tab_1").css('display','none');
                $(".tab_2").css('display','none');
                $(".tab_3").css('display','');
            }
        }
        function all_match(){
            $("div[name='all_match']").addClass('match_click');
            $("div[name='my_match']").removeClass('match_click');
        }
        function my_match(){
            $("div[name='my_match']").addClass('match_click');
            $("div[name='all_match']").removeClass('match_click')
        }
        function create(){
            location.href="set_up.html"
        }

    </script>
</head>
<body>
<div class="head">
    <ul>
        <li onclick="tab_page('比赛')" style="margin-left: 0;">比赛</li>
        <li onclick="tab_page('报名')">报名</li>
        <li onclick="tab_page('创建')">创建</li>
    </ul>
    <input type="text" placeholder="搜索" />
</div>
</body>
</html>