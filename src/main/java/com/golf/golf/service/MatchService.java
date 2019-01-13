package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScoreUserMapping;
import com.golf.golf.db.TeamInfo;
import com.golf.golf.db.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学术活动
 * Created by nmy on 2017/7/1.
 */
@Service
public class MatchService implements IBaseService {
	
    @Autowired
    private MatchDao matchDao;

	public MatchInfo getMatchInfoById(Long wcId) {
		MatchInfo matchInfo = matchDao.get(MatchInfo.class,wcId);
		matchInfo.setMiHit(matchInfo.getMiHit()==null?1:matchInfo.getMiHit()+1);
		matchDao.update(matchInfo);
		return matchInfo;
	}

	/**
	 * 获取全部比赛列表
	 * @return
	 */
	public POJOPageInfo getMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getMatchList(searchBean,pageInfo);
	}

	/**
	 * 获取我参加的比赛列表
	 * @return
	 */
	public POJOPageInfo getMyMatchList(SearchBean searchBean, POJOPageInfo pageInfo) {
		return matchDao.getMyMatchList(searchBean,pageInfo);
	}


	/**
	 * 加点击量
	 * @return
	 */
	public MatchInfo addHit(Long matchId){
		MatchInfo db = matchDao.get(MatchInfo.class,matchId);
		if(db.getMiHit() == null || db.getMiHit() == 0){
			db.setMiHit(1);
		}else{
			db.setMiHit(db.getMiHit() + 1);
		}
		matchDao.update(db);
		return db;
	}

	/**
	 * 获取本比赛的围观用户列表和比赛分组
	 * @return
	 */
	public Map<String, Object> getMatchGroupList(Long matchId) {
        Map<String, Object> result = new HashMap<String, Object>();
	    //围观
	    List<Object[]> watchList = matchDao.getWatchUserListByMatchId(matchId);
        List<UserInfo> newWatchList = buildNewWatchList(watchList);

	    //分组
        List<Object[]> groupList = matchDao.getMatchGroupList(matchId);
        List<UserInfo> newGroupList = buildNewGroupList(groupList);

        result.put("watchList", newWatchList);
        result.put("groupList", newGroupList);
		return result;
	}

    private List<UserInfo> buildNewWatchList(List<Object[]> list) {
        List<UserInfo> watchList = new ArrayList<UserInfo>();
	    if(list != null && list.size()>0){
            for(Object[] obj:list){
                watchList.add((UserInfo)(obj[1]));
            }
        }
        return watchList;
    }

    private List<UserInfo> buildNewGroupList(List<Object[]> list) {
        List<UserInfo> watchList = new ArrayList<UserInfo>();
        if(list != null && list.size()>0){
            for(Object[] obj:list){
                watchList.add((UserInfo)(obj[1]));
            }
        }
        return watchList;
    }

    /**
	 * 创建比赛
	 * @return
	 */
	public TeamInfo getTeamInfoById(String teamId) {
		return matchDao.get(TeamInfo.class,Long.parseLong(teamId));
	}

    /**
     * 保存一条用户记分对应关系
     * @return
     */
    public void saveUserScoreMapping(Long matchId, Long groupId, Long scorerId) {
        MatchScoreUserMapping mapping = new MatchScoreUserMapping();
        mapping.setMsumMatchId(matchId);
        mapping.setMsumGroupId(groupId);
        mapping.setMsumScorerId(scorerId);
        mapping.setMsumCreateTime(System.currentTimeMillis());
        matchDao.save(mapping);
    }

    /**
     * 点击组内用户头像，判断是否能给该用户记分 跳转记分卡页面
     * @return
     */
    public boolean getScoreType(SearchBean searchBean) {
        //判断是否赛长
        Long isCaptain = matchDao.getIsCaptain(searchBean);
        if(isCaptain > 0){
            return true;
        }
        //判断是否可记分
        Long count = matchDao.getScoreTypeCount(searchBean);
        if(count > 0){
            return true;
        }
        return false;
    }
}
