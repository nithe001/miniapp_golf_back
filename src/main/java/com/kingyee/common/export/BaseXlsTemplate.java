package com.kingyee.common.export;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;


public class BaseXlsTemplate extends AbstractXlsTemplate{
	/**
	 * 如果 xlsContentList里放的不是单纯的bean，请自己重写此方法。
	 */
	@Override
	protected HSSFSheet createContent(HSSFSheet sheet,
			List<?> xlsContentList) throws Exception {
		for(int i = 0; i < xlsContentList.size(); i++){
			HSSFRow row = createRow(sheet, i + 1);
			ArrayList<Object> obj = (ArrayList<Object>)xlsContentList.get(i);
			for(int j = 0; j < obj.size(); j++){
				createCell(row, obj.get(j).toString());
			}
		}
		return sheet;
	}
	

	/**
	 * 
	 * @param sheet Excel的sheet
	 * @param titlesEn 英文标题(bean 中的属性)
	 * @param xlsContentList bean的list
	 * @param map 转换函数
	 * @return
	 * @throws Exception
	 */
	@Override
	protected HSSFSheet createContent(HSSFSheet sheet, String[] titlesEn,
			List<?> xlsContentList, Map<String, ConvertCallBack> map)
			throws Exception {
		boolean isEmpty = false;
		if(map == null || map.isEmpty()){
			isEmpty = true;
		}
		for(int i = 0; i < xlsContentList.size(); i++){
			HSSFRow row = createRow(sheet, i + 1);
			Object obj = xlsContentList.get(i);
			for(int j = 0; j < titlesEn.length; j++){
				if(isEmpty || !map.containsKey(titlesEn[j])){
					createCell(row, BeanUtils.getProperty(obj, titlesEn[j]));
				}else{
					ConvertCallBack convert = map.get(titlesEn[j]);
					createCell(row, convert.getString(BeanUtils.getProperty(obj, titlesEn[j])));
				}
			}
		}
		return sheet;
	}
	
	/**
	 * 如果 xlsContentList里放的不是单纯的bean，请自己重写此方法。
	 */
	@Override
	protected HSSFSheet createContentWithStyle(HSSFSheet sheet,
			List<?> xlsContentList, List<Map<String, String>> cellStyleList) throws Exception {
		Map<String, String> cellStyleMap = null;
		// 有给单元格设置颜色的场合，
		if(cellStyleList != null && !cellStyleList.isEmpty()){
			for(int i = 0; i < xlsContentList.size(); i++){
				HSSFRow row = createRow(sheet, i + 1);
				cellStyleMap = cellStyleList.get(i);
				Object obj = xlsContentList.get(i);
				String fieldName = null;
				BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  //获取所有的属性
				for(PropertyDescriptor pd : propertyDescriptors) {
					fieldName = pd.getName();
					if(cellStyleMap.containsKey(fieldName)){
						createCellWithStyle(row, BeanUtils.getProperty(obj, fieldName));
					}else{
						createCell(row, BeanUtils.getProperty(obj, fieldName));
					}
				}
			}
		}else{
			for(int i = 0; i < xlsContentList.size(); i++){
				HSSFRow row = createRow(sheet, i + 1);
				Object obj = xlsContentList.get(i);
				String fieldName = null;
				BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  //获取所有的属性
				for(PropertyDescriptor pd : propertyDescriptors) {
					fieldName = pd.getName();
					createCell(row, BeanUtils.getProperty(obj, fieldName));
				}
			}
		}
		return sheet;
	}

	/**
	 * 
	 * @param sheet Excel的sheet
	 * @param titlesEn 英文标题(bean 中的属性)
	 * @param xlsContentList bean的list
	 * @param map 转换函数
	 * @return
	 * @throws Exception
	 */
	@Override
	protected HSSFSheet createContentWithStyle(HSSFSheet sheet, String[] titlesEn,
			List<?> xlsContentList, Map<String, ConvertCallBack> map, List<Map<String, String>> cellStyleList)
			throws Exception {
		boolean isEmpty = false;
		if(map == null || map.isEmpty()){
			isEmpty = true;
		}
		Map<String, String> cellStyleMap = null;
		String value = null;
		// 有给单元格设置颜色的场合，
		if(cellStyleList != null && !cellStyleList.isEmpty()){
			for(int i = 0; i < xlsContentList.size(); i++){
				HSSFRow row = createRow(sheet, i + 1);
				cellStyleMap = cellStyleList.get(i);
				Object obj = xlsContentList.get(i);
				for(int j = 0; j < titlesEn.length; j++){
					if(isEmpty || !map.containsKey(titlesEn[j])){
						value = BeanUtils.getProperty(obj, titlesEn[j]);
					}else{
						ConvertCallBack convert = map.get(titlesEn[j]);
						value = convert.getString(BeanUtils.getProperty(obj, titlesEn[j]));
					}
					if(cellStyleMap.containsKey(titlesEn[j])){
						createCellWithStyle(row, value);
					}else{
						createCell(row, value);
					}
				}
			}
		}else{
			for(int i = 0; i < xlsContentList.size(); i++){
				HSSFRow row = createRow(sheet, i + 1);
				Object obj = xlsContentList.get(i);
				for(int j = 0; j < titlesEn.length; j++){
					if(isEmpty || !map.containsKey(titlesEn[j])){
						value = BeanUtils.getProperty(obj, titlesEn[j]);
					}else{
						ConvertCallBack convert = map.get(titlesEn[j]);
						value = convert.getString(BeanUtils.getProperty(obj, titlesEn[j]));
					}
					createCell(row, value);
				}
			}
		}
		return sheet;
	}

	
}
