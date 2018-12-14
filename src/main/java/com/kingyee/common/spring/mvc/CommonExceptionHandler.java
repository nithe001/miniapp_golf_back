package com.kingyee.common.spring.mvc;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.kingyee.common.gson.JsonWrapper;
import com.kingyee.common.util.CommonUtil;
import com.kingyee.golf.common.security.AdminUserUtil;
import com.kingyee.golf.common.security.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ph on 2017/5/23.
 */
public class CommonExceptionHandler implements HandlerExceptionResolver {

    private final static Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception ex) {
        try {
            String url = CommonUtil.getFullUrl(request, false);
            String errMsg = "";
            //这里能抓住 所有的异常信息
            if(request.getServletPath().startsWith("/admin/")){
                // 后台
                errMsg = "后台-用户["+AdminUserUtil.getUserId()+"]，在请求["+url+"]时候发生了异常";
            }else{
                // 前台
                if(UserUtil.hasLogin()){
                    errMsg = "前台-用户["+UserUtil.getUserId()+"]，在请求["+url+"]时候发生了异常";
                }else{
                    errMsg = "前台-未登陆用户，在请求["+url+"]时候发生了异常";
                }
            }
            //写入日志
            logger.error(errMsg, ex);

            // 前台错误处理
            String requestType = request.getHeader("X-Requested-With");
            if(requestType != null){
                JsonElement jsonObj = JsonWrapper.newErrorInstance("运行时有错误。");
                // ajax请求
                response.setContentType("text/plain;");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "inline");
                PrintWriter writer = response.getWriter();
                try {
                    JsonWriter jsonWriter = new JsonWriter(writer);
                    jsonWriter.setLenient(true);
                    Streams.write(jsonObj, jsonWriter);
                } catch (Exception e1) {
                    logger.error("", e1);
                } finally {
                    try {
                        writer.close();
                    } catch (Exception e2) {
                        logger.error("", e2);
                    }
                }
                return new ModelAndView();
            }else{
                //转发
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("errMsg", ex);
                return new ModelAndView("error", model);
            }
        }catch (Exception e){
            logger.error("记录出错日志失败。", e);
        }
        return new ModelAndView("error");
    }

}
