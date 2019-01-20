package com.golf.golf.service;

import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.golf.bean.MatchUserGroupMappingBean;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.MatchDao;
import com.golf.golf.dao.TeamDao;
import com.golf.golf.db.*;
import com.golf.golf.enums.MatchGroupUserMappingTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 球队管理
 * Created by nmy on 2017/7/1.
 */
@Service
public class TeamService implements IBaseService {

    @Autowired
    private TeamDao teamDao;

    /**
     * 获取球队列表
     * @return
     */
    public POJOPageInfo getTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getTeamList(searchBean,pageInfo);
    }

    /**
     * 获取我加入的球队列表
     * @return
     */
    public POJOPageInfo getMyTeamList(SearchBean searchBean, POJOPageInfo pageInfo) {
        return teamDao.getMyTeamList(searchBean,pageInfo);
    }
}
