package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.excel.ExcelData;
import com.golf.golf.bean.HoleMatchScoreResultBean;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScore;
import com.golf.golf.service.MatchService;
import com.golf.golf.service.MatchHoleService;
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
import java.io.UnsupportedEncodingException;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;



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
    private MatchHoleService matchHoleService;
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
	 * 导出成绩
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
        nTitle.add("");
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
        row.add("分组");
        row.add("用户姓名");
        row.add("性别");
        row.add("球队");
        Integer totalNum=0;
        Double totalNetNum=0D;
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
        row.add("总杆");
        row.add("净杆");
        row.add("鸟数");
        row.add("鹰数");
        rows.add(row);

		//添加比赛成绩
        /*
        if (matchInfo.getMiType() ==5 ) { //汇总比赛
            String userName;
            String teamName;

            List<Map<String, Object>> userList =  (List<Map<String, Object>> )scoreMap.get("userList");
            if (userList != null && userList.size() > 0) {
                for (Map<String, Object> user : userList) {
                    //一个循环显示的是一行的数据
                    userName = matchService.getName(user,"uiRealName");
                    teamName = matchService.getName(user,"teamAbbrev");

                    row = new ArrayList<>();
                    row.add("");
                    row.add(userName);
                    row.add("");
                    row.add(teamName);
                    for(int i=0;i<20;i++) {
                        row.add(0);
                    }
                    //总杆
                    totalNum = matchService.getIntegerValue(user,"sumRod");
                    row.add(totalNum);
                    //净杆
                    totalNetNum = Double.parseDouble(user.get("sumRodNet").toString());
                    row.add(totalNetNum);

                    rows.add(row);

                }
            }

        } else {
        */

            List<MatchGroupUserScoreBean> userList = (List<MatchGroupUserScoreBean>) scoreMap.get("list");
            if (userList != null && userList.size() > 0) {

                for (MatchGroupUserScoreBean user : userList) {
                    //一个循环显示的是一行的数据
                    if (user.getUserId() != 0) {
                        row = new ArrayList<>();
                        //用户名
                        row.add(Integer.valueOf(user.getGroupName()));
                        row.add(user.getUserName());
                        row.add(user.getUserSex());
                        row.add(user.getTeamAbbrev());
                        //用户得分
                        List<MatchTotalUserScoreBean> userScoreList = user.getUserScoreTotalList();
                        Integer birdNum = 0;
                        Integer eagleNum = 0;
                        for (MatchTotalUserScoreBean score : userScoreList) {
                           row.add(score.getRodNum().toString());

                           Integer rodCha =0;
                           if (score.getHoleStandardRod() != null) {
                               rodCha = score.getRodNum()-score.getHoleStandardRod();
                           }
                            if (rodCha == -1 ) {
                                birdNum++;
                            } else if(rodCha == -2){
                                eagleNum++;
                            }

                        }
                        totalNum = user.getTotalRodScore();
                        row.add(totalNum);
                        totalNetNum = user.getTotalNetRodScore();
                        row.add(totalNetNum);
                        row.add(birdNum);
                        row.add(eagleNum);
                        rows.add(row);
                    }
                }
            }
      //  }
		data.setRows(rows);
		return data;
	}

    /**
     * 导出比洞赛结果
     * @param
     * @return
     */
    public ExcelData exportHoleResault(Map<String,Object> scoreMap,MatchInfo matchInfo) throws InvocationTargetException, IllegalAccessException , UnsupportedEncodingException{
        ExcelData data = new ExcelData();
        data.setName("比洞赛成绩");
        //表头
        List<String> titles = Arrays.asList("");
        List<String> nTitle = new ArrayList<>(titles);

        //表头中的球洞号
        nTitle.add("分组");
        for(int i = 0;i<2;i++){
            for(int j = 1;j<10;j++){
                nTitle.add(j+"");
            }
        }
        data.setTitles(nTitle);

        List<List<Object>> rows = new ArrayList<>();


        //添加比赛成绩
        Long matchId = matchInfo.getMiId();
        List<Map<String,Object>> groupList = matchDao.getMatchGroupList(matchId,matchInfo.getMiIsEnd());
        for (Map<String,Object> group : groupList) {
            Long groupId = matchService.getLongValue(group,"groupId");

            Map<String, Object> holeScoreCard = matchHoleService.updateOrGetHoleScoreCardByGroupId(matchId, groupId) ;
            List<HoleMatchScoreResultBean> chengjiList = new ArrayList<>();
            chengjiList =   (List<HoleMatchScoreResultBean>) holeScoreCard.get("chengjiList");
            List<Object> row = new ArrayList<>();
            row.add(matchService.getIntegerValue(group,"groupName"));
            String res;
            Double resault=0d;
            Integer isStartHole =0;
            Integer put=0;
            for (int j=0; j<2;j++) {
                for (int i = 0; i < chengjiList.size(); i++) {
                    res = chengjiList.get(i).getUpDnAs();
                    isStartHole = chengjiList.get(i).getIsStartHole();
                    if ( isStartHole!= null && isStartHole ==1 ) { put ++;}
                    if(put ==1 ) {

                        if ("UP".equals(res)) {
                            resault = 1d;
                        } else if ("DN".equals(res)) {
                            resault = 0d;
                        } else if ("A/S".equals(res)) {
                            resault = 0.5;
                        }
                        row.add(resault);
                    } if (put ==2){
                        break;
                    }
                }
            }
            rows.add(row);
        }

        data.setRows(rows);
        return data;
    }

    /**
     * 导出比洞赛按时间轴的结果
     * @param
     * @return
     */
    public ExcelData exportHoleResaultTime(Map<String,Object> scoreMap,MatchInfo matchInfo) throws InvocationTargetException, IllegalAccessException ,ParseException, UnsupportedEncodingException{
        ExcelData data = new ExcelData();
        data.setName("比洞赛成绩");
        //表头
        List<String> titles = Arrays.asList("");
        List<String> nTitle = new ArrayList<>(titles);

        //表头中的球洞号
        nTitle.add("分组");
        for(int i = 0;i<2;i++){
            for(int j = 1;j<10;j++){
                nTitle.add(j+"");
            }
        }
        data.setTitles(nTitle);

        List<List<Object>> rows = new ArrayList<>();


        //添加比赛成绩
        Long matchId = matchInfo.getMiId();
        List<Map<String,Object>> groupList = matchDao.getMatchGroupList(matchId,matchInfo.getMiIsEnd());
        String beginTime= "2021-09-16 12:00:00";
        String endTime= "2021-09-16 19:00:00";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date sd1=df.parse(beginTime);
        Date sd2=df.parse(endTime);
        Long createTime=sd1.getTime();


        while( createTime <=sd2.getTime() ) {
            createTime += (long) 10 * 60*1000 ;

            List<Object> row = new ArrayList<>();

            for (Map<String, Object> group : groupList) {
                Long groupId = matchService.getLongValue(group, "groupId");

                Map<String, Object> holeScoreCard = matchHoleService.updateOrGetHoleScoreCardByGroupIdTime(matchId, groupId, createTime);
                Double result=0d;
                HoleMatchScoreResultBean chengjiResult ;
                chengjiResult =  (HoleMatchScoreResultBean) holeScoreCard.get("chengjiResult");
                String res=chengjiResult.getUpDnAs();

                if ("UP".equals(res)) {
                    result = 1d;
                } else if ("DN".equals(res)) {
                    result = 0d;
                } else if ("A/S".equals(res)) {
                    result = 0.5;
                } else if (res == null){
                    result = -1d;
                }
                row.add(result);

            }
            rows.add(row);
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
