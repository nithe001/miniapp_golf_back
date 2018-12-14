package com.kingyee.golf.service.admin;

import com.kingyee.common.IBaseService;
import com.kingyee.common.model.POJOPageInfo;
import com.kingyee.common.model.SearchBean;
import com.kingyee.golf.dao.admin.AdminMatchDao;
import com.kingyee.golf.db.MatchInfo;
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
		MatchInfo db = dao.get(MatchInfo.class,matchInfo.getId());
		db.setTitle(matchInfo.getTitle());
		db.setMatchTime(matchInfo.getMatchTime());
		db.setDigest(matchInfo.getDigest());
		db.setContent(matchInfo.getContent());
		db.setAddress(matchInfo.getAddress());
		db.setIsOpen(matchInfo.getIsOpen());
		dao.update(db);
	}

	//删除赛事活动
	public void delMatch(Long id) {
		MatchInfo db = dao.get(MatchInfo.class,id);
		if(db.getIsDel() == null || db.getIsDel() == 0){
			db.setIsDel(1);
		}else if(db.getIsDel() == 1){
			db.setIsDel(0);
		}
		dao.update(db);
	}
}
