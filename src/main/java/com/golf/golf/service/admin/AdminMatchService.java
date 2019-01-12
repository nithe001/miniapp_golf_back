package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.common.security.AdminUserUtil;
import com.golf.golf.dao.admin.AdminMatchDao;
import com.golf.golf.db.MatchInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 赛事活动
 * @author nmy
 * 2017年05月08日
 */
@Service
public class AdminMatchService implements IBaseService {
	
    @Autowired
    private AdminMatchDao dao;

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

}
