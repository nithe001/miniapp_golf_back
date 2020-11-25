package com.golf.golf.service.admin;

import com.golf.common.IBaseService;
import com.golf.common.excel.ExcelData;
import com.golf.golf.bean.MatchGroupUserScoreBean;
import com.golf.golf.bean.MatchTotalUserScoreBean;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.db.MatchScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		List<String> titles = Arrays.asList("用户姓名");
		List<String> nTitle = new ArrayList<>(titles);
		//表头中的半场球洞
		for(int i = 0;i<2;i++){
			for(int j = 1;j<10;j++){
				nTitle.add(j+"");
				if(j==9){
					nTitle.add(matchInfo.getMiZoneBeforeNine()+"场");
				}
			}
		}
		data.setTitles(nTitle);
		List<List<Object>> rows = new ArrayList<>();

		List<MatchGroupUserScoreBean> userList = (List<MatchGroupUserScoreBean>) scoreMap.get("list");
		if (userList != null && userList.size() > 0) {
			for (MatchGroupUserScoreBean user : userList) {
				//一个循环显示的是一行的数据
				if(user.getUserId() != 0){
					List<Object> row = new ArrayList<>();
					//用户名
					row.add(user.getUserName());
					//用户得分
					List<MatchTotalUserScoreBean> userScoreList = user.getUserScoreTotalList();
					for(MatchTotalUserScoreBean score:userScoreList){
						row.add(score.getRodNum().toString());
					}
					rows.add(row);
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
