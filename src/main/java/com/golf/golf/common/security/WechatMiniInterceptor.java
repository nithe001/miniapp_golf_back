package com.golf.golf.common.security;

import com.alibaba.fastjson.JSON;
import com.golf.common.util.AesUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

/**
 * 请求解密拦截器
 * @author peihong
 * @version 2014-4-14 下午2:03:58
 * 
 */
public class WechatMiniInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(WechatMiniInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuffer url = request.getRequestURL();
	    String aesData = request.getParameter("aesData");
        log.info("=== 拦截器 start 收到参数：aesData={} ===", aesData);
        if (StringUtils.isNotEmpty(aesData)) {
            log.info("=== 拦截器 开始解密 ===");
            //解密
            String decryptParameter = AesUtil.decrypt(aesData, AesUtil.key);
            log.info("=== 拦截器 解密结果：result={} ===", decryptParameter);
            Map<String, Object> map = JSON.parseObject(decryptParameter);
            if (map != null && map.size() > 0) {
                Set<String> set = map.keySet();
                for (String key : set) {
                    request.setAttribute(key, map.get(key) != null ? map.get(key) : "");
                }
            }else{
                log.info("=== 拦截器 end 参数aesData为空，返回错误信息给小程序 ===");
                PrintWriter writer = response.getWriter();
                JSONObject json = new JSONObject();
                StringReader reader = null;
                json.put("success","false");
                json.put("msg","传参为空");
                try {
                    reader = new StringReader(json.toString());
                    char[] buffer = new char[1024];
                    int charRead = 0;
                    while ((charRead = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, charRead);
                    }
                } finally {
                    if (reader != null)
                        reader.close();
                    if (writer != null) {
                        writer.flush();
                        writer.close();
                    }
                }
                return false;
            }
            log.info("=== 拦截器 end 解密结束 ===");
        }else{
            log.info("=== 拦截器 end 参数aesData为空，返回错误信息给小程序 ===");
            PrintWriter writer = response.getWriter();
            JSONObject json = new JSONObject();
            StringReader reader = null;
            json.put("success","false");
            json.put("msg","传参为空");
            try {
                reader = new StringReader(json.toString());
                char[] buffer = new char[1024];
                int charRead = 0;
                while ((charRead = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, charRead);
                }
            } finally {
                if (reader != null)
                    reader.close();
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
            return false;
        }
        return super.preHandle(request, response, handler);
	}

}
