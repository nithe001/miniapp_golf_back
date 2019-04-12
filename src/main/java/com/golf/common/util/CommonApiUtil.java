package com.golf.common.util;

import com.golf.golf.common.security.UserUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class CommonApiUtil{

	/**
	 * 公共方法 HttpGet
	 * @param
	 * @param url
	 * @return
	 * @throws Exception
	 * @author zhangwenna
	 * @version 2017年5月25日下午5:35:29
	 * @param
	 */
	public static String getUrl(String url) throws Exception {
		String result="";
		HttpClient client = null;
		HttpGet get = null;
		client = UserUtil.getHttpClient();
		try{
			get = new HttpGet(url);
			HttpResponse response1 = client.execute(get);
			int statusCode = response1.getStatusLine().getStatusCode();
			// 1.3判断响应码
			// 1.4获取响应内容
			result = EntityUtils.toString(response1.getEntity());
			if (statusCode == HttpStatus.SC_OK) {
				return result;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
