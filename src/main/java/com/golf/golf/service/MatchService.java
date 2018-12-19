package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.TeamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学术活动
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchService implements IBaseService {
	
    @Autowired
    private MatchDao dao;

	public MatchInfo getMatchInfoById(Long wcId) {
		MatchInfo matchInfo = dao.get(MatchInfo.class,wcId);
		matchInfo.setHit(matchInfo.getHit()==null?1:matchInfo.getHit()+1);
		dao.update(matchInfo);
		return matchInfo;
	}

	/**
	 * 获取赛事列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		pageInfo = dao.getMatchList(searchBean,pageInfo);
		if(pageInfo.getCount() >0){
			for(MatchInfo matchInfo : (List<MatchInfo>)pageInfo.getItems()){
				matchInfo.setStateStr();
				matchInfo.setCreateTimeStr(matchInfo.getCreateTime());
				matchInfo.setMatchTimeStr(matchInfo.getMatchTime());
			}
		}
		return pageInfo;
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
	 * 获取本赛事的队伍
	 * @return
	 */
	public List<Object[]> getMatchGroupList(Long matchId) {
		return dao.getMatchGroupList(matchId);
	}

	/**
	 * 创建比赛
	 * @return
	 */
	public TeamInfo getTeamInfoById(String teamId) {
		return dao.get(TeamInfo.class,Long.parseLong(teamId));
	}


}
