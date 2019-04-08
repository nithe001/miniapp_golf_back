package com.golf.common.util;

import com.alibaba.fastjson.JSONObject;

/**
 * json转换工具
 * @author nmy
 */
public class JsonUtil {
    /**
     * 将json结果集转化为对象
     *
     * @param jsonStr json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonStr, Class<T> beanType) {
        try {
            JSONObject jsonData = JSONObject.parseObject(jsonStr);
            T t = (T) JSONObject.toJavaObject(jsonData,beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String value = "{name:\"zzzzz\",sex:\"nv\"}";
        /*model module= (model) jsonToPojo(value,model.class);
        System.out.println(module);*/
    }
}