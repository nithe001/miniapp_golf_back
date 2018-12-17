package com.golf.common.sms;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新短信通道,福建电信版
 * 
 * @author 张宏亮
 * @version 2015年6月30日下午1:25:43
 */
public class SmsTplUtil {
	/** 项目域名,短信标识 */
	private static final String DOMAIN = "www.bcpta.cn";
	/** 发送 url */
	private static final String SMS_SEND_URL = "http://www.baidu.com";

	/** httpclient超时时间10秒 */
	private static int HTTPCLIENT_TIMEOUT = 10000;

	/**
	 * 发送短信
	 * 
	 * @param mobile
	 *            手机号,只能是一个
	 * @param msg
	 *            短信内容
     *     104291       【Value1】验证码【Value2】，您正在进行认证，验证码【Value3】分钟内有效。千万不要说出去哦！【Value4】
	 * @return
	 */
	public static JsonObject sendSms(String mobile, String templetid, List<String> values) {
		HttpClient client = null;
		HttpPost post = null;
		try {
			client = getHttpClient();
			post = new HttpPost(SMS_SEND_URL);
			post.setEntity(createSendEntity(mobile, templetid, values));
			HttpResponse response = client.execute(post);
			if (response.getEntity() != null) {
				return new JsonParser().parse(EntityUtils.toString(response.getEntity())).getAsJsonObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (post != null) {
					post.abort();
				}
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				// nothing to do
			}
		}
		JsonObject res = new JsonObject();
		res.addProperty("success", false);
		return res;
	}

	/**
	 * 发送
	 * 
	 * @param mobile
	 * @param msg
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static HttpEntity createSendEntity(String mobile, String templetid, List<String> values) throws UnsupportedEncodingException {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("domain", DOMAIN));
		parameters.add(new BasicNameValuePair("calleeNbr", mobile));
		parameters.add(new BasicNameValuePair("templetid", templetid));
		for (int i = 0; i < values.size(); i++) {
			parameters.add(new BasicNameValuePair("value" + (i + 1), values.get(i)));
		}
		HttpEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
		return entity;
	}

	private static HttpClient getHttpClient() throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// 设置超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTPCLIENT_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, HTTPCLIENT_TIMEOUT);
		// 为避免时间过长，不retry
		DefaultHttpRequestRetryHandler retryhandler = new DefaultHttpRequestRetryHandler(0, false);
		httpClient.setHttpRequestRetryHandler(retryhandler);

		HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

		return httpClient;
	}
}
