package com.golf.golf.web.admin;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.golf.common.Const;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.QRCodeUtil;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchRule;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.admin.AdminMatchService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 赛事活动
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/admin/match")
public class AdminMatchController {
	private final static Logger logger = LoggerFactory.getLogger(AdminMatchController.class);

    @Autowired
    private AdminMatchService adminMatchService;
    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    protected WxMaService wxMaService;
	@Autowired
	private MatchService matchService;

	/**
	 * 赛事活动列表
	 * @return
	 */
	@RequestMapping("list")
	public String list(ModelMap mm, String keyword, String startDate, String endDate, Integer page,Integer rowsPerPage,
					   Integer type,Integer state){
		if (page == null || page.intValue() == 0) {
			page = 1;
		}
		if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
			rowsPerPage = Const.ROWSPERPAGE;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchInfo>(rowsPerPage , page);
		try {
			SearchBean searchBean = new SearchBean();
			if (StringUtils.isNotEmpty(keyword)) {
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			if (type != null) {
				searchBean.addParpField("type", type);
			}
			if (state != null) {
				searchBean.addParpField("state", state);
			}
			pageInfo = adminMatchService.matchList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取赛事活动列表时出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		mm.addAttribute("type",type);
		mm.addAttribute("keyword",keyword);
		mm.addAttribute("page",page);
		mm.addAttribute("rowsPerPage",rowsPerPage);
		mm.addAttribute("state",state);
		mm.addAttribute("startDate", startDate);
		mm.addAttribute("endDate", endDate);
		return "admin/match/list";
	}


    /**
     * 新增赛事活动init
     * @return
     */
    @RequestMapping("addMatchIdUI")
    public String addMatchIdUI(){
        return "admin/match/add";
    }

	/**
	 * 新增赛事活动
	 * @return
	 */
	@RequestMapping("addMatch")
	public String addMatch(MatchInfo calendar){
		try{
			String a = calendar.getMiContent().replace("\"", "\'");
			calendar.setMiContent(a);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("新增赛事活动时出错。"+ e );
			return "admin/error";
		}
		return "redirect:list";
	}

    /**
     * 编辑赛事活动init
     * @param mm
     * @param matchId 活动id
     * @return
     */
    @RequestMapping("editMatchUI")
    public String editMatchUI(ModelMap mm, Long matchId){
		try{
			MatchInfo matchInfo = adminMatchService.getMatchById(matchId);
			List<TeamInfo> joinTeamInfoList = adminMatchService.getJoinTeamList(matchInfo.getMiJoinTeamIds());
			List<TeamInfo> submitTeamInfoList = adminMatchService.getJoinTeamList(matchInfo.getMiReportScoreTeamId());
			Map<String,Object> score = matchService.getTotalScoreByMatchId(matchId);
			mm.addAttribute("matchInfo",matchInfo);
			mm.addAttribute("joinTeamInfoList",joinTeamInfoList);
			mm.addAttribute("submitTeamInfoList",submitTeamInfoList);
			mm.addAttribute("score",score);
			System.out.println(21);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("编辑赛事活动init出错。"+ e );
			return "admin/error";
		}
        return "admin/match/edit";
    }

    /**
     * 编辑赛事活动-保存
     * @param matchInfo
     * @return
     */
    @RequestMapping("matchEdit")
    public String matchEdit(MatchInfo matchInfo){
        try{
            adminMatchService.editMatch(matchInfo);
        }catch(Exception e){
            e.printStackTrace();
			logger.error("编辑赛事活动时出错。"+ e );
            return "admin/error";
        }
        return "redirect:calendarList";
    }

    /**
     * 生成二维码
     * https://developers.weixin.qq.com/miniprogram/dev/api/getWXACodeUnlimit.html
     * 必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index, 根路径前不要填加 /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
     * @return
     */
    @RequestMapping("createEwm")
    public String createEwm() {
        try {
            String oldFileName = UserUtil.getOpenId() + ".png";//文件名称
            String QRCodePath = WebUtil.getRealPath("/") + "upload/QRCode";
//            oldFileName = WebUtil.getRealPath(PropertyConst.prcodeWithPic + "/" + oldFileName);
            oldFileName = QRCodePath + "/" + oldFileName;

            File oldFile = new File(oldFileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            //生成二维码
            Long docId= 1L;
            //		https://mp.weixin.qq.com/wiki?id=mp1443433542&highline=%E4%BA%8C%E7%BB%B4%E7%A0%81
            WxMpQrCodeTicket qrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateLastTicket(docId + "");

//           String scene, String page, int width, boolean autoColor, WxMaCodeLineColor lineColor, boolean isHyaline
            File file = new File(QRCodePath,oldFileName);
//            file = wxMaService.getQrcodeService().createWxaCodeUnlimit();
            String qrcodeUrl = qrCodeTicket.getUrl();//二维码内容
            String imagePath = QRCodePath + "/";//二维码保存绝对物理路径
            String fileName = UserUtil.getOpenId() + ".png";//文件名称
//            String logoPaht = WebUtil.getRealPath(PropertyConst.userPhoto) + "/"; //用户微信头像保存路径
//            logoPaht += fileName;
//            QRCodeUtil.createQRImageWithLogo(qrcodeUrl, logoPaht, imagePath, fileName);//生成带头像的微信二维码
            QRCodeUtil.createQRImage(qrcodeUrl, imagePath, fileName);//生成不带头像的微信二维码
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/match/add";
    }












	/**
	 * 高球规则列表
	 * @return
	 */
	@RequestMapping("ruleList")
	public String ruleList(ModelMap mm, String keyword, Integer page,Integer rowsPerPage){
		if (page == null || page.intValue() == 0) {
			page = 1;
		}
		if (rowsPerPage == null || rowsPerPage.intValue() == 0) {
			rowsPerPage = Const.ROWSPERPAGE;
		}
		POJOPageInfo pageInfo = new POJOPageInfo<MatchRule>(rowsPerPage , page);
		try {
			SearchBean searchBean = new SearchBean();
			if (StringUtils.isNotEmpty(keyword)) {
				searchBean.addParpField("keyword", "%" + keyword.trim() + "%");
			}
			pageInfo = adminMatchService.matchRuleList(searchBean, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取高球规则列表时出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("pageInfo",pageInfo);
		mm.addAttribute("keyword",keyword);
		mm.addAttribute("page",page);
		mm.addAttribute("rowsPerPage",rowsPerPage);
		return "admin/matchRule/list";
	}


	/**
	 * 新增高球规则init
	 * @return
	 */
	@RequestMapping("addRuleUI")
	public String addRuleUI(){
		return "admin/matchRule/add";
	}

	/**
	 * 新增高球规则
	 * @return
	 */
	@RequestMapping("addRule")
	public String addRule(MatchRule rule){
		try{
			adminMatchService.saveRule(rule);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("新增高球规则时出错。"+ e );
			return "admin/error";
		}
		return "redirect:ruleList";
	}

	/**
	 * 编辑高球规则init
	 * @param mm
	 * @param ruleId id
	 * @return
	 */
	@RequestMapping("editRuleUI")
	public String editRuleUI(ModelMap mm, Long ruleId){
		try{
			MatchRule matchRule = adminMatchService.getRuleById(ruleId);
			mm.addAttribute("matchRule",matchRule);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("编辑高球规则init出错。"+ e );
			return "admin/error";
		}
		return "admin/matchRule/edit";
	}

	/**
	 * 编辑高球规则-保存
	 * @return
	 */
	@RequestMapping("updateRule")
	public String updateRule(MatchRule rule){
		try{
			adminMatchService.editRule(rule);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("编辑高球规则时出错。"+ e );
			return "admin/error";
		}
		return "redirect:ruleList";
	}
}
