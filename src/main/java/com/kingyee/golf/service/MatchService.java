package com.kingyee.golf.service;

import com.kingyee.common.IBaseService;
import com.kingyee.common.model.POJOPageInfo;
import com.kingyee.common.model.SearchBean;
import com.kingyee.golf.dao.MatchDao;
import com.kingyee.golf.db.MatchInfo;
import com.kingyee.golf.db.TeamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学术活动
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchService implements IBaseService {
	
    @Autowired
    private MatchDao dao;

	public MatchInfo getMatchInfoById(Long wcId) {
		return dao.get(MatchInfo.class,wcId);
	}

	/**
	 * 获取赛事列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		return dao.getMatchList(searchBean,pageInfo);
	}


	/**
	 * 加点击量
	 * @return
	 */
	public MatchInfo addHit(Long matchId){
		MatchInfo db = dao.get(MatchInfo.class,matchId);
		if(db.getHit() == null || db.getHit() == 0){
			db.setHit(1);
		}else{
			db.setHit(db.getHit() + 1);
		}
		dao.update(db);
		return db;
	}

	/**
	 * 创建比赛
	 * @return
	 */
	public TeamInfo getTeamInfoById(String teamId) {
		return dao.get(TeamInfo.class,Long.parseLong(teamId));
	}
}
