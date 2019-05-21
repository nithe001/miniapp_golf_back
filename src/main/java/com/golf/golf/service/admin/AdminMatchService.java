package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.admin.AdminMatchDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchRule;
import com.golf.golf.db.TeamInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		db.setMiTitle(matchInfo.getMiTitle());
		db.setMiMatchTime(matchInfo.getMiMatchTime());
		db.setMiDigest(matchInfo.getMiDigest());
		db.setMiContent(matchInfo.getMiContent());
		db.setMiParkId(matchInfo.getMiParkId());
		db.setMiParkName(matchInfo.getMiParkName());
		db.setMiJoinOpenType(matchInfo.getMiJoinOpenType());
		db.setMiMatchOpenType(matchInfo.getMiMatchOpenType());
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

}
