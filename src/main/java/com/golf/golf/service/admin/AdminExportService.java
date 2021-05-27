package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.excel.ExcelData;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScore;
import com.golf.golf.service.MatchService;
import com.golf.golf.dao.MatchDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 导出功能
 * @author nmy
 * 2020年8月6日
 */
@Service
public class AdminExportService implements IBaseService {
    @Autowired
    private MatchService matchService;
    @Autowired
    private MatchDao matchDao;


	private final static Logger logger = LoggerFactory.getLogger(AdminExportService.class);

	/**
	 * 获取该比赛参赛用户的比分
	 * @param
	 * @return
	 */
	public List<MatchScore> getScoreList(Long matchId){
		return null;
	}


	/**
	 * 导出
	 * @param
	 * @return
	 */
	public ExcelData exportExcel(Map<String,Object> scoreMap,MatchInfo matchInfo) throws InvocationTargetException, IllegalAccessException {
		ExcelData data = new ExcelData();
		data.setName("比赛成绩");
		//表头
		List<String> titles = Arrays.asList("");
		List<String> nTitle = new ArrayList<>(titles);
        nTitle.add("");
		//表头中的球洞号
		for(int i = 0;i<2;i++){
			for(int j = 1;j<10;j++){
				nTitle.add(j+"");
				if(i==0 && j==9){
					nTitle.add(matchInfo.getMiZoneBeforeNine()+"场");
				} if(i==1 && j==9) {
                    nTitle.add(matchInfo.getMiZoneAfterNine()+"场");
                }
			}
		}
		data.setTitles(nTitle);

        List<List<Object>> rows = new ArrayList<>();
        //各球洞标准杆
        List<Object> row = new ArrayList<>();
        row.add("用户姓名");
        row.add("球队");
        Integer totalNum=0;
        List<Map<String, Object>> beforeParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchInfo.getMiId(),0);
        for (Map<String, Object> hole: beforeParkHoleList){
            Integer rodNum = matchService.getIntegerValue(hole,"holeStandardRod");
            row.add(rodNum.toString()) ;
            totalNum +=rodNum;
        }
        row.add(totalNum.toString());
        totalNum=0;
        List<Map<String, Object>> afterParkHoleList = matchDao.getBeforeAfterParkPartitionList(matchInfo.getMiId(),1);
        for (Map<String, Object> hole: afterParkHoleList){
            Integer rodNum = matchService.getIntegerValue(hole,"holeStandardRod");
            row.add(rodNum.toString()) ;
            totalNum +=rodNum;
        }
        row.add(totalNum.toString());
        row.add("合计");
        rows.add(row);

		//添加比赛成绩
        if (matchInfo.getMiType() ==2) { //汇总比赛
            String userName;
            String teamName;
            Integer rodNum;
            List<Map<String, Object>> userList =  (List<Map<String, Object>> )scoreMap.get("userList");
            if (userList != null && userList.size() > 0) {
                for (Map<String, Object> user : userList) {
                    //一个循环显示的是一行的数据
                    userName = matchService.getName(user,"uiRealName");
                    teamName = matchService.getName(user,"teamAbbrev");
                    rodNum = matchService.getIntegerValue(user,"sumRodNum");
                    row = new ArrayList<>();

                    row.add(userName);
                    row.add(teamName);
                    row.add( rodNum);

                    rows.add(row);

                }
            }

        } else {
            List<MatchGroupUserScoreBean> userList = (List<MatchGroupUserScoreBean>) scoreMap.get("list");
            if (userList != null && userList.size() > 0) {

                for (MatchGroupUserScoreBean user : userList) {
                    //一个循环显示的是一行的数据
                    if (user.getUserId() != 0) {
                        row = new ArrayList<>();
                        //用户名
                        row.add(user.getUserName());
                        row.add(user.getTeamAbbrev());
                        //用户得分
                        List<MatchTotalUserScoreBean> userScoreList = user.getUserScoreTotalList();
                        totalNum=0;
                        for (MatchTotalUserScoreBean score : userScoreList) {
                            totalNum += score.getRodNum();
                            row.add(score.getRodNum().toString());
                        }
                        row.add(totalNum/2);
                        rows.add(row);
                    }
                }
            }
        }
		data.setRows(rows);
		return data;
	}

    /**
     * 球友球队信息导出
     * @param
     * @return
     */
    public ExcelData exportUserTeamExcel(List<Map<String, Object>> userTeamList) {
        ExcelData data = new ExcelData();
        data.setName("球队球友信息");
        List<String> titles = Arrays.asList("球友姓名", "球队名称");
        data.setTitles(titles);
        List<List<Object>> rows = new ArrayList<>();
        if (userTeamList != null && userTeamList.size() > 0) {
            for (Map<String, Object> userTeam : userTeamList) {
                List<Object> row = new ArrayList<>();
                row.add(userTeam.get("userName"));
                row.add(userTeam.get("teamName"));
                rows.add(row);
            }
        }
        data.setRows(rows);
        return data;
    }
}
