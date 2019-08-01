package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.admin.AdminMatchDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchRule;
import com.golf.golf.db.MatchUserQrcode;
import com.golf.golf.db.TeamInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 赛事活动
 * @author nmy
 * 2017年05月08日
 */
@Service
public class AdminMatchService implements IBaseService {
	
    @Autowired
    private AdminMatchDao dao;
	@Autowired
	private MatchDao matchDao;


	/**
	 * 赛事活动列表
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo<MatchInfo> matchList(SearchBean searchBean, POJOPageInfo pageInfo){
		return  dao.matchList(searchBean,pageInfo);
	}

	/**
	 * 获取赛事活动
	 * @param id
	 * @return
	 */
	public MatchInfo getMatchById(Long id) {
		return dao.get(MatchInfo.class,id);
	}

	/**
	 * 保存赛事活动
	 * @param matchInfo
	 * @return
	 */
	public void editMatch(MatchInfo matchInfo) {
		MatchInfo db = dao.get(MatchInfo.class,matchInfo.getMiId());
		db.setMiMatchTime(matchInfo.getMiMatchTime());
		db.setMiDigest(matchInfo.getMiDigest());
		db.setMiContent(matchInfo.getMiContent());
		db.setMiJoinOpenType(matchInfo.getMiJoinOpenType());
		db.setMiMatchOpenType(matchInfo.getMiMatchOpenType());
		db.setMiMatchFormat1(matchInfo.getMiMatchFormat1());
		db.setMiMatchFormat2(matchInfo.getMiMatchFormat2());
		db.setMiUpdateTime(System.currentTimeMillis());
		db.setMiUpdateUserId(AdminUserUtil.getUserId());
		db.setMiUpdateUserName(AdminUserUtil.getShowName());
		dao.update(db);
	}

	/**
	 * 高球规则列表
	 * @return
	 */
	public POJOPageInfo matchRuleList(SearchBean searchBean, POJOPageInfo pageInfo) {
		return  dao.matchRuleList(searchBean,pageInfo);
	}

	/**
	 * 新增高球规则
	 * @return
	 */
	public void saveRule(MatchRule rule) {
		dao.save(rule);
	}

	/**
	 * 编辑高球规则init
	 * @param ruleId id
	 * @return
	 */
	public MatchRule getRuleById(Long ruleId) {
		return dao.get(MatchRule.class,ruleId);
	}

	/**
	 * 编辑高球规则-保存
	 * @return
	 */
	public void editRule(MatchRule rule) {
		dao.update(rule);
	}

	/**
	 * 获取参赛队
	 * @return
	 */
	public List<TeamInfo> getJoinTeamList(String teamIds) {
		if(StringUtils.isNotEmpty(teamIds)){
			List<Long> teamIdList = new ArrayList<>();
			String ids[] = teamIds.split(",");
			for(String id : ids){
				if(StringUtils.isNotEmpty(id)){
					teamIdList.add(Long.parseLong(id));
				}
			}
			return matchDao.getTeamListByIds(teamIdList);
		}
		return null;
	}

	/**
	 * 修改状态
	 * @param matchId 比赛id
	 * @return
	 */
	public void updateMatchState(Long matchId) {
		MatchInfo matchInfo = dao.get(MatchInfo.class, matchId);
		if(matchInfo.getMiIsValid() == 1){
			matchInfo.setMiIsValid(0);
		}else{
			matchInfo.setMiIsValid(1);
		}
        dao.update(matchInfo);
	}

	/**
	 * 获取参赛队员列表
	 * @return
	 */
	public List<Object[]> getMatchUserGroupMappingList(Long matchId) {
		return matchDao.getMatchUserGroupMappingList(matchId);
	}

    /**
     * 删除比赛
     * @param matchId 比赛id
     * @return
     */
    public void delMatch(Long matchId) {
		//删除比赛球队确认配置
		dao.delMatchScoreConfig(matchId);
		//删除比赛分组
		dao.delMatchGroup(matchId);
		//删除比赛输赢情况
		dao.delMatchWinOrLose(matchId);
		//删除比赛信息
        dao.del(MatchInfo.class, matchId);
		//删除比赛观战信息
		dao.delMatchWatchInfo(matchId);
		//删除比赛对应的用户比分
		dao.delMatchScore(matchId);
		//删除比赛对应的邀请记分信息
		dao.delMatchUserApplyScoreInfo(matchId);
        //删除比赛对应的用户mapping
        dao.delMatchUserMapping(matchId);
		//删除比赛对应的用户积分信息
		dao.delMatchUserPointInfo(matchId);

		//获取比赛对应的生成二维码信息
		List<MatchUserQrcode> matchUserQrcodeList = dao.getMatchUserQRCodeList(matchId);
		//删掉对应的二维码文件
		if(matchUserQrcodeList != null && matchUserQrcodeList.size() >0){
			for(MatchUserQrcode qrcode:matchUserQrcodeList){
				String path = WebUtil.getPath();
				String qrcodePath = qrcode.getMuqQrcodePath();
				File file = new File(path, qrcodePath);
				if(file.exists()){
					file.delete();
				}
			}
		}
		//删除比赛对应的生成二维码信息
		dao.delMatchQRCodeInfo(matchId);

		//删除比赛对应的扫二维码信息
		dao.delScanMatchQRCodeInfo(matchId);
    }
	/**
	 * 删除高球规则
	 * @param ruleId 规则id
	 * @return
	 */
	public void delMatchRule(Long ruleId) {
		dao.del(MatchRule.class,ruleId);
	}
}
