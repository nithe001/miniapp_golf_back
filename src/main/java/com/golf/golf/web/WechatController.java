package com.golf.golf.web;

import com.golf.golf.db.UserInfo;
import com.golf.golf.db.WechatUserInfo;
import com.google.gson.JsonElement;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.util.RandomUtil;
import com.golf.golf.common.GenericController;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.service.UserService;
import com.golf.golf.service.WechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Controller
public class WechatController extends GenericController {

    @Autowired
    protected WxMpConfigStorage configStorage;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WechatService wechatService;
    @Autowired
    protected UserService userService;

    /**
     * 微信公众号webservice主服务接口，提供与微信服务器的信息交互
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "wechat")
    public void wechatCore(HttpServletRequest request, HttpServletResponse response,String code) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        if (!this.wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }

        String echoStr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echoStr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            String echoStrOut = String.copyValueOf(echoStr.toCharArray());
            response.getWriter().println(echoStrOut);
            return;
        }


        WxMpXmlMessage inMessage = null;
        try{
            String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type"))
                    ? "raw"
                    : request.getParameter("encrypt_type");

            if ("raw".equals(encryptType)) {
                // 明文传输的消息
                inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
                WxMpXmlOutMessage outMessage = this.wechatService.route(inMessage);
                if(outMessage != null){
					System.out.println(outMessage.toXml());
					response.getWriter().write(outMessage.toXml());
					PrintWriter out=null;
					try {
						out = response.getWriter();
						out.print(outMessage.toXml());
					} catch (IOException e) {
						out.close();
						out=null;
						e.printStackTrace();
					}
					out.close();
					out=null;
                    return;
                }else{
                    return;
                }
            }else if ("aes".equals(encryptType)) {
                // 是aes加密的消息
                String msgSignature = request.getParameter("msg_signature");
                inMessage = WxMpXmlMessage.fromEncryptedXml(
                        request.getInputStream(), this.configStorage, timestamp, nonce,
                        msgSignature);
                this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
                WxMpXmlOutMessage outMessage = this.wechatService.route(inMessage);
                this.logger.info(response.toString());
                if(outMessage != null){
                    response.getWriter().write(outMessage.toEncryptedXml(this.configStorage));
                    return;
                }else{
                    return;
                }
            }

            response.getWriter().println("不可识别的加密类型");
        }catch (Exception e) {
            logger.error("响应微信请求失败。inMessage=" + inMessage, e);
        }

        return;
    }

	/**
	 * 微信公众号webservice主服务接口，提供与微信服务器的信息交互
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "wechatxcx")
	public void wechatxcx(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		String signature = request.getParameter("signature");
		String nonce = request.getParameter("nonce");
		String timestamp = request.getParameter("timestamp");

		if (!this.wxMpService.checkSignature(timestamp, nonce, signature)) {
			// 消息签名不正确，说明不是公众平台发过来的消息
			response.getWriter().println("非法请求");
			return;
		}

		String echoStr = request.getParameter("echostr");
		if (StringUtils.isNotBlank(echoStr)) {
			// 说明是一个仅仅用来验证的请求，回显echostr
			String echoStrOut = String.copyValueOf(echoStr.toCharArray());
			response.getWriter().println(echoStrOut);
			return;
		}


		WxMpXmlMessage inMessage = null;
		try{
			String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type"))
					? "raw"
					: request.getParameter("encrypt_type");

			if ("raw".equals(encryptType)) {
				// 明文传输的消息
				inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
				WxMpXmlOutMessage outMessage = this.wechatService.route(inMessage);
				if(outMessage != null){
					System.out.println(outMessage.toXml());
					response.getWriter().write(outMessage.toXml());
					PrintWriter out=null;
					try {
						out = response.getWriter();
						out.print(outMessage.toXml());
					} catch (IOException e) {
						out.close();
						out=null;
						e.printStackTrace();
					}
					out.close();
					out=null;
					return;
				}else{
					return;
				}
			}else if ("aes".equals(encryptType)) {
				// 是aes加密的消息
				String msgSignature = request.getParameter("msg_signature");
				inMessage = WxMpXmlMessage.fromEncryptedXml(
						request.getInputStream(), this.configStorage, timestamp, nonce,
						msgSignature);
				this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
				WxMpXmlOutMessage outMessage = this.wechatService.route(inMessage);
				this.logger.info(response.toString());
				if(outMessage != null){
					response.getWriter().write(outMessage.toEncryptedXml(this.configStorage));
					return;
				}else{
					return;
				}
			}

			response.getWriter().println("不可识别的加密类型");
		}catch (Exception e) {
			logger.error("响应微信请求失败。inMessage=" + inMessage, e);
		}

		return;
	}

//    /**
//     * 通过openid获得基本用户信息
//     * 详情请见: http://mp.weixin.qq.com/wiki/14/bb5031008f1494a59c6f71fa0f319c66.html
//     *
//     * @param response
//     * @param openid   openid
//     * @param lang     zh_CN, zh_TW, en
//     */
//    @RequestMapping(value = "getUserInfo")
//    public WxMpUser getUserInfo(HttpServletResponse response, @RequestParam(value = "openid") String openid, @RequestParam(value = "lang") String lang) {
//        ReturnModel returnModel = new ReturnModel();
//        WxMpUser wxMpUser = null;
//        try {
//            wxMpUser = this.wxMpService.getUserService().userInfo(openid, lang);
//            returnModel.setResult(true);
//            returnModel.setDatum(wxMpUser);
//            renderString(response, returnModel);
//        } catch (WxErrorException e) {
//            returnModel.setResult(false);
//            returnModel.setReason(e.getError().toString());
//            renderString(response, returnModel);
//            this.logger.error(e.getError().toString());
//        }
//        return wxMpUser;
//    }
//
//    /**
//     * 通过code获得基本用户信息
//     * 详情请见: http://mp.weixin.qq.com/wiki/14/bb5031008f1494a59c6f71fa0f319c66.html
//     *
//     * @param response
//     * @param code     code
//     * @param lang     zh_CN, zh_TW, en
//     */
//    @RequestMapping(value = "getOAuth2UserInfo")
//    public void getOAuth2UserInfo(HttpServletResponse response, @RequestParam(value = "code") String code, @RequestParam(value = "lang") String lang) {
//        ReturnModel returnModel = new ReturnModel();
//        WxMpOAuth2AccessToken accessToken;
//        WxMpUser wxMpUser;
//        try {
//            accessToken = this.wxMpService.oauth2getAccessToken(code);
//            wxMpUser = this.wxMpService.getUserService()
//                .userInfo(accessToken.getOpenId(), lang);
//            returnModel.setResult(true);
//            returnModel.setDatum(wxMpUser);
//            renderString(response, returnModel);
//        } catch (WxErrorException e) {
//            returnModel.setResult(false);
//            returnModel.setReason(e.getError().toString());
//            renderString(response, returnModel);
//            this.logger.error(e.getError().toString());
//        }
//    }

    /**
     * 用code换取oauth2的openid
     * 详情请见: http://mp.weixin.qq.com/wiki/1/8a5ce6257f1d3b2afb20f83e72b72ce9.html
     *
     * @param response
     * @param code     code
     */
    @RequestMapping(value = "getOpenid")
    public String getOpenid(HttpServletResponse response, @RequestParam(value = "code") String code, String url) throws Exception {
        WxMpOAuth2AccessToken accessToken;
        try {
            accessToken = this.wxMpService.oauth2getAccessToken(code);
            String openId = accessToken.getOpenId();
            WechatUserInfo wechatUser = userService.getWechatUserByOpenid(openId);
            if(wechatUser == null){
                WxMpUser wxMpUser = wechatService.getUserInfo(openId, "zh_CN");
                // 如果已经关注了微信号的人，数据没有在本地数据库中，则重新关注
//                wechatUser = userService.saveOrUpdateWechatUser(wxMpUser);
            }

            UserInfo user = null;
            if(wechatUser.getWuiId() != null){
                user = userService.getUserById(wechatUser.getWuiUId());
            }

            UserModel userModel = new UserModel();
            userModel.setWechatUser(wechatUser);
            userModel.setUser(user);
            UserUtil.login(userModel);

        } catch (WxErrorException e) {
            this.logger.error("取用用户openid接口，设置用户cookie失败", e);
        } catch (UnsupportedEncodingException e) {
            this.logger.error("取用用户openid接口，设置用户cookie失败", e);
        } catch (IOException e) {
            this.logger.error("取用用户openid接口，设置用户cookie失败", e);
        }
        return "redirect:" + url;
    }

    /**
     * 获取jssdk中的必要信息
     *
     * @param url     code
     */
    @RequestMapping(value = "getJsSdk")
    @ResponseBody
    public JsonElement getJsSdk(String url) {
        try {
            String jsapiTicket = this.wxMpService.getJsapiTicket(false);

            long timestamp = System.currentTimeMillis() / 1000;
            String noncestr = RandomUtil.generateMixString(16);
            String result = SHA1.genWithAmple(
                    "jsapi_ticket=" + jsapiTicket,
                    "noncestr=" + noncestr,
                    "timestamp=" + timestamp,
                    "url=" + url
            );

            Map<String, String> map = new HashMap<>();
            map.put("appId", configStorage.getAppId());
            map.put("timestamp", String.valueOf(timestamp));
            map.put("nonceStr", noncestr);
            map.put("signature", result);

            return JsonWrapper.newDataInstance(map);
        } catch (WxErrorException e) {
            logger.error("取得SDK有错误发生。", e);
            return JsonWrapper.newErrorInstance("取得SDK有错误发生。");
        }
    }

}
