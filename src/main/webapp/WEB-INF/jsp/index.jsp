<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="include/commonInclude.jsp"></jsp:include>
    <title>北京心血管疾病防治研究会</title>
</head>
<body>
<jsp:include page="include/header.jsp">
    <jsp:param name="navi" value="index"></jsp:param>
</jsp:include>
<div class="banner">
    <!-- Swiper -->
    <div class="swiper-container">
        <div class="swiper-wrapper">
            <div class="swiper-slide">
                <a href="#">
                    <img src="static/test_images/banner.jpg">
                </a>
            </div>
            <div class="swiper-slide">
                <a href="meetingIntrouduce">
                    <img src="static/test_images/banner2.jpg">
                </a>
            </div>
        </div>
        <!-- Add Pagination -->
        <div class="swiper-pagination"></div>
        <!-- Add Arrows -->
        <div class="swiper-button-next"></div>
        <div class="swiper-button-prev"></div>
    </div>
</div>
<div class="content clearfix">
    <div class="content_module left">
        <div class="module_title">
            <span class="title_line">协会介绍</span>
            <em>Association Introduction</em>
        </div>
        <div class="module_txt">
            <p class="module_img"><img src="static/test_images/1.jpg"></p>
            <p class="module_p">北京心血管疾病防治研究会经北京市民政局批准于2017年3月15日在北京成立，是由北京行政区划内从事心血管学科研究和实践的医疗及相关领域的研究机构、医疗机构、社会组织、企事业单位和专家学者自愿联合发起成立的营利性社会团体。英文名称为Beijing Cardiovascular Disease Prevention&Treatment Association，缩写为BCA。
                <a href="introduce">[ 查看详情 ]</a>
            </p>
            <p></p>

        </div>
    </div>
    <div class="content_module right">
        <div class="module_title">
            <span class="title_line">入会须知</span>
            <em>Association Introduction</em>
        </div>
        <div class="module_txt">
            <p class="module_img"><img src="static/test_images/2.jpg"></p>
            <p class="module_p">首先，基本要求为主治医师以上职称；临床10年以上经验。其次，入会需满足如下条件：拥护本研究会的章程；有加入本研究会的意愿；在本研究会的业务 (行业、学科)领域内具有一定的影响。最后，按入会程序成为会员。提交入会申请书；经会长批准同意；经理事会讨论通过；由理事会或理事会授权的机构发给会员证。
                <a href="notice">[ 查看详情 ]</a>
            </p>
            <p></p>

        </div>
    </div>
</div>
<jsp:include page="include/footer.jsp"></jsp:include>

<!-- Initialize Swiper -->
<script>
    var swiper = new Swiper('.swiper-container', {
        pagination: '.swiper-pagination',
        paginationClickable: true,
        nextButton: '.swiper-button-next',
        prevButton: '.swiper-button-prev',
        loop : true,
        autoplay: 5000
    });
</script>
</body>
</html>