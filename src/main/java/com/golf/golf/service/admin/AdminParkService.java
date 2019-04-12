package com.golf.golf.service.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.golf.common.IBaseService;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.util.CommonApiUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.common.security.UserUtil;
import com.golf.golf.dao.admin.AdminParkDao;
import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.ParkPartition;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 球场管理
 *
 * @author nmy
 * 2016年10月31日
 */
@Service
public class AdminParkService implements IBaseService {

	@Autowired
	private AdminParkDao adminParkDao;

	/**
	 * 球场列表
	 *
	 * @param searchBean
	 * @param pageInfo
	 * @return
	 */
	public POJOPageInfo getParkList(SearchBean searchBean, POJOPageInfo pageInfo) {
		return adminParkDao.getParkList(searchBean, pageInfo);
	}


	/**
	 * 保存球场
	 *
	 * @param parkInfo
	 * @return
	 */
	public Long saveParkInfo(ParkInfo parkInfo) {
		return adminParkDao.save(parkInfo);
	}

	/**
	 * 保存球场球洞信息
	 *
	 * @return
	 */
	public Long saveParkPartition(ParkPartition parkPartition) {
		return adminParkDao.save(parkPartition);
	}

	/**
	 * 根据id获取球场信息
	 * @param id
	 * @return
	 */
	public  ParkInfo getParkInfoById(Long id) {
		return adminParkDao.get(ParkInfo.class, id);
	}

	/**
	 * 根据id获取球场的球区信息
	 * @param id
	 * @return
	 */
	public  List<ParkPartition> getParkZoneById(Long id) {
		return adminParkDao.getParkZoneById(id);
	}

	/**
	 * 更新球场信息
	 *
	 * @param parkInfo
	 */
	public void edit(ParkInfo parkInfo) {
		ParkInfo db = adminParkDao.get(ParkInfo.class, parkInfo.getPiId());
		db.setPiLogo(parkInfo.getPiLogo());
		db.setPiCity(parkInfo.getPiCity());
		db.setPiIsValid(parkInfo.getPiIsValid());
		adminParkDao.update(db);
	}

	/**
	 * 恢复、注销球场
	 *
	 * @param parkId
	 */
	public void update(Long parkId) {
		ParkInfo db = adminParkDao.get(ParkInfo.class, parkId);
		if (db.getPiIsValid() == 0) {
			db.setPiIsValid(1);
		} else {
			db.setPiIsValid(0);
		}
		adminParkDao.update(db);
	}

	/**
	 * 查看球场名是否已经存在
	 *
	 * @param name
	 * @return
	 */
	public boolean checkName(String city, String name) {
		return adminParkDao.checkName(city, name);
	}

	/**
	 * 根据城市和球场名称获取球场信息
	 *
	 * @return
	 */
	public ParkInfo getByCityAndName(String city, String parkName) {
		return adminParkDao.getByCityAndName(city, parkName);
	}

	/**
	 * 获取球场经纬度
	 *
	 * @return
	 */
	public void updateParkInfoWithJwd() throws Exception {
		POJOPageInfo pageInfo = new POJOPageInfo<ParkInfo>(0, 1);
		SearchBean searchBean = new SearchBean();
		pageInfo = adminParkDao.getParkList(searchBean, pageInfo);
		List<ParkInfo> list = pageInfo.getItems();
		if (pageInfo.getCount() > 0 && list != null && list.size() > 0) {
			for (ParkInfo parkInfo : list) {
				String city = parkInfo.getPiCity();
				String name = parkInfo.getPiName();
				String ak = "F303b406b7af4e9a0073e886e986a8dd";
				String lat = "";
				String lng = "";
				try {
					name = java.net.URLEncoder.encode(name,"UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				String url = String.format("http://api.map.baidu.com/place/v2/search?ak=%s&region=%s&query=%s&output=json",ak,city, name);
				String result = CommonApiUtil.getUrl(url);
				if (StringUtils.isNotEmpty(result)) {
					JsonElement element = new JsonParser().parse(result);
					JsonObject jsonObj = element.getAsJsonObject();
					if (jsonObj != null && jsonObj.get("message").toString().equals("ok")) {
						JsonArray resultsJsonArray = jsonObj.get("results").getAsJsonArray();
//						JsonObject jsonObj1 = resultsJsonArray.get("location");
					}
				}

				parkInfo.setPiLat(lat);
				parkInfo.setPiLng(lng);

			}
		}
	}

	public void updateParkInfo(ParkInfo parkInfo) {
		adminParkDao.update(parkInfo);
	}
}
