package com.golf.golf.web;

import com.golf.common.gson.JsonWrapper;
import com.golf.golf.common.security.UserModel;
import com.golf.golf.db.MatchRule;
import com.golf.golf.db.UserInfo;
import com.golf.golf.service.UserService;
import com.google.gson.JsonElement;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户
 * @author nmy
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
	private final static Logger logger = LoggerFactory
			.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	protected WxMpService wxMpService;


	/**
	 * 保存/更新 微信用户信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "saveOrUpdateWechatUserInfo")
	public JsonElement saveOrUpdateWechatUserInfo(@RequestAttribute("openid") String openid,
												  @RequestAttribute("userDataStr") String userDataStr) {
		try {
			if(StringUtils.isNotEmpty(openid) && StringUtils.isNotEmpty(userDataStr)){
				userService.saveOrUpdateWechatUserInfo(openid, userDataStr);
				return JsonWrapper.newSuccessInstance();
			}
			return JsonWrapper.newErrorInstance("保存/更新 微信用户信息时出错。openid="+openid+" 用户信息="+userDataStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存/更新 微信用户信息时出错。openid="+openid+" 用户信息="+userDataStr ,e);
			return JsonWrapper.newErrorInstance("保存/更新 微信用户信息时出错"+e);
		}
	}


	/**
	 * 更新用户经纬度信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "updateUserLocation")
	public JsonElement updateUserLocation(@RequestAttribute("latitude") String latitude,
										  @RequestAttribute("longitude") String longitude,
										  @RequestAttribute("openid") String openid) {
		try {
			if(StringUtils.isNotEmpty(latitude) && StringUtils.isNotEmpty(longitude) && StringUtils.isNotEmpty(openid)){
				userService.updateUserLocation(latitude, longitude, openid);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新用户经纬度信息时出错。" ,e);
			return JsonWrapper.newErrorInstance("更新用户经纬度信息时出错");
		}
	}

	/**
	 * 获取我的信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserInfoByOpenId")
	public JsonElement getUserInfoByOpenId(@RequestAttribute("openid") String openid) {
		try {
			UserModel userModel = userService.getUserModelByOpenid(openid);
			return JsonWrapper.newDataInstance(userModel);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取用户信息失败。", e);
			return JsonWrapper.newErrorInstance("获取用户信息失败");
		}
	}

	/**
	 * 获取我的信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserInfo")
	public JsonElement getUserInfo(@RequestAttribute("openid") String openid) {
		try {
			Map<String,Object> result = userService.getMyDetail(openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取用户信息失败。", e);
			return JsonWrapper.newErrorInstance("获取用户信息失败");
		}
	}

	/**
	 * 获取其他用户的详细资料
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getOthersUserInfo")
	public JsonElement getOthersUserInfo(@RequestAttribute("userId") Long userId) {
		try {
			Map<String,Object> result = userService.getOtherDetail(userId);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取用户信息失败。", e);
			return JsonWrapper.newErrorInstance("获取用户信息失败");
		}
	}

	/**
	 * 修改个人信息
	 * 同时更新其他表中有用到真实姓名的地方
	 * 同时用真实姓名匹配导入的成绩
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateUserInfo")
	public JsonElement updateUserInfo(@RequestAttribute("userInfo") String userInfo, @RequestAttribute("openid") String openid) {
		try {
			if(StringUtils.isNotEmpty(userInfo) && StringUtils.isNotEmpty(openid)){
				net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(userInfo);
				UserInfo userInfoBean = (UserInfo) net.sf.json.JSONObject.toBean(jsonObject, UserInfo.class);
				String result = userService.updateUser(userInfoBean, openid);
				//增加了一个返回值
				return JsonWrapper.newDataInstance(result);
			}
			return JsonWrapper.newSuccessInstance();
			} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新用户信息时失败。", e);
			return JsonWrapper.newErrorInstance("更新用户信息时失败");
		}
	}

	/**
	 * 更新签名
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateUserSignature")
	public JsonElement updateUserSignature(@RequestAttribute("signature") String signature, @RequestAttribute("openid") String openid) {
		try {
			if(StringUtils.isNotEmpty(signature) && StringUtils.isNotEmpty(openid)){
				userService.updateUserSignature(signature, openid);
			}
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新用户签名时失败。", e);
			return JsonWrapper.newErrorInstance("更新用户签名时失败");
		}
	}


	/**
	 * 基本信息
	 * 详细资料 只有是队友且该球队要求 详细资料时才可见
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getUserInfoById")
	public JsonElement getUserInfoById(@RequestAttribute("teamId") String teamId,
									   @RequestAttribute("matchId") String matchId,
									   @RequestAttribute("userId") Long userId,
									   @RequestAttribute("openid") String openid) {
		try {
			Map<String, Object> result = userService.getUserDetaliInfoById(teamId,matchId,userId,openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-根据用户id获取用户信息失败。" ,e);
			return JsonWrapper.newErrorInstance("根据用户id获取用户信息失败。");
		}
	}


	/**
	 * 历史成绩-包括单练成绩
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMyHistoryScoreByUserId")
	public JsonElement getMyHistoryScoreByUserId(@RequestAttribute("openid") String openid) {
		try {
			Map<String,Object> result = userService.getMyHistoryScoreByUserId(openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-根据用户id获取历史成绩息失败。" ,e);
			return JsonWrapper.newErrorInstance("根据用户id获取历史成绩息失败。");
		}
	}

	/**
	 * 年度成绩分析 包括单练
	 * 计算一年内平均每18洞分项的数量:把一个人所有场的各类数据求和以后，要除以场数
	 * “暴洞”是指+3及以上的洞数总和
	 * 开球情况对应记分卡 球道滚轮的箭头
	 * 标ON是计算出来的，如果某洞：杆数-推杆数=该洞标准杆数-2，则该洞为 标ON
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMyHistoryScoreByYear")
	public JsonElement getMyHistoryScoreByYear(@RequestAttribute("date") String date, @RequestAttribute("openid") String openid) {
		try {
			Map<String,Object> result = userService.getMyHistoryScoreByYear(date, openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-根据用户id获取历史成绩信息失败。" ,e);
			return JsonWrapper.newErrorInstance("根据用户id获取历史成绩信息失败。");
		}
	}

	/**
	 * 高球规则
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getMatchRuleList")
	public JsonElement getMatchRuleList() {
		try {
			List<MatchRule> ruleList = userService.getMatchRuleList();
			return JsonWrapper.newDataInstance(ruleList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-根据用户id获取高球规则失败。" ,e);
			return JsonWrapper.newErrorInstance("根据用户id获取高球规则失败。");
		}
	}

	/**
	 * 查询是否有待认领的成绩
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getHasScoreByClaim")
	public JsonElement getHasScoreByClaim(@RequestAttribute("openid") String openid) {
		try {
			List<Map<String, Object>> result = userService.getHasScoreByClaim(openid);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-查询是否有待认领的成绩时失败。openid="+openid ,e);
			return JsonWrapper.newErrorInstance("前台-查询是否有待认领的成绩时失败。openid="+openid);
		}
	}

	/**
	 * 我——认领成绩
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateClaimUserScore")
	public JsonElement updateClaimUserScore(@RequestAttribute("openid") String openid, @RequestAttribute("chooseIds") String chooseIds) {
		try {
			userService.updateClaimUserScore(openid,chooseIds);
			return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-用户认领成绩失败。openid="+openid ,e);
			return JsonWrapper.newErrorInstance("前台-用户认领成绩失败。openid="+openid);
		}
	}

	/**
	 * 比赛中点头像——认领成绩
	 * @return
	 */
	@ResponseBody
	@RequestMapping("claimUserScore")
	public JsonElement claimUserScore(@RequestAttribute("openid") String openid,
									  @RequestAttribute("importUserId") String importUserId,
									  @RequestAttribute("importUserRealName") String importUserRealName,
									  @RequestAttribute("matchId") String matchId) {
		try {
			String result = userService.claimUserScore(openid,importUserId,importUserRealName,matchId);
			return JsonWrapper.newDataInstance(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-用户认领成绩失败。openid="+openid ,e);
			return JsonWrapper.newErrorInstance("前台-用户认领成绩失败。openid="+openid);
		}
	}

	/**
	 * 个人中心-修改性别
	 * @return
	 */
	@ResponseBody
	@RequestMapping("changeSex")
	public JsonElement changeSex(@RequestAttribute("openid") String openid,
									  @RequestAttribute("sex") String sex) {
		try {
			if(StringUtils.isNotEmpty(openid) && !Objects.equals("guest", openid) && StringUtils.isNotEmpty(sex)){
				userService.updateUserSex(openid, sex);
				return JsonWrapper.newSuccessInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("前台-个人中心-修改性别失败。openid="+openid ,e);
			return JsonWrapper.newErrorInstance("前台-个人中心-修改性别失败。openid="+openid);
		}
		return JsonWrapper.newErrorInstance("出错了。");
	}
}
