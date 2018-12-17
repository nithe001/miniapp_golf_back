package com.golf.common.export;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * 导出到excel的模板方法
 * 
 * @author peihong
 *
 */
public abstract class AbstractXlsTemplate {
	
	private List<?> xlsContentList;
	private HSSFWorkbook book;
	private String sheetName;
	private String[] titles = {};
	private int cellNo;
	private int startRowNum;
	private CellStyle cellStyle;

	public AbstractXlsTemplate(){
		this(new HSSFWorkbook());
	}
	
	/**
	 * 如果有多个sheet的场合。
	 * @param book
	 */
	public AbstractXlsTemplate(HSSFWorkbook book){
		this.book = book;
		startRowNum = 0;
		cellStyle = book.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	}
	
	/**
	 * 取得Excel
	 * 
	 * @param sheetName sheet名称 
	 * @param titles 标题名称
	 * @param xlsContentList 内容
	 * @return
	 * @throws Exception 
	 */
	public HSSFWorkbook createExcelBook(String sheetName, String[] titles, List<?> xlsContentList) throws Exception {
		HSSFSheet sheet = this.initSheet(sheetName, titles, xlsContentList);
		if(xlsContentList != null && xlsContentList.size() > 0){
			// 对应表内容
			sheet = createContent(sheet, xlsContentList);
		}
		return book;
	}
	
	/**
	 * 取得Excel
	 * 
	 * @param sheetName sheet名称 
	 * @param titles 标题名称
	 * @param xlsContentList 内容
	 * @return
	 * @throws Exception 
	 */
	public HSSFWorkbook createExcelBookWithStyle(String sheetName, String[] titles,
			List<?> xlsContentList, List<Map<String, String>> cellStyleList) throws Exception {
		HSSFSheet sheet = this.initSheet(sheetName, titles, xlsContentList);
		if(xlsContentList != null && xlsContentList.size() > 0){
			// 对应表内容
			sheet = createContentWithStyle(sheet, xlsContentList, cellStyleList);
		}
		return book;
	}
	
	/**
	 * 取得Excel
	 * 
	 * @param sheetName sheet名称 
	 * @param titles 标题名称
	 * @param xlsContentList 内容
	 * @return
	 * @throws Exception 
	 */
	public HSSFWorkbook createExcelBook(String sheetName, String[] titles,
			String[] titlesEn, List<?> xlsContentList) throws Exception {
		return this.createExcelBook(sheetName, titles, titlesEn, xlsContentList, null);
	}

	/**
	 * 取得Excel
	 * 
	 * @param sheetName sheet名称 
	 * @param titles 标题名称
	 * @param xlsContentList 内容
	 * @return
	 * @throws Exception 
	 */
	public HSSFWorkbook createExcelBook(String sheetName, String[] titles,
			String[] titlesEn, List<?> xlsContentList, Map<String, ConvertCallBack> map) throws Exception {
		HSSFSheet sheet = this.initSheet(sheetName, titles, xlsContentList);
		if(xlsContentList != null && xlsContentList.size() > 0){
			// 对应表内容(通过反射设定)
			sheet = createContent(sheet, titlesEn, xlsContentList, map);
		}
		return book;
	}

	/**
	 * 取得Excel
	 * 
	 * @param sheetName sheet名称 
	 * @param titles 标题名称
	 * @param xlsContentList 内容
	 * @return
	 * @throws Exception 
	 */
	public HSSFWorkbook createExcelBookWithStyle(String sheetName, String[] titles,
			String[] titlesEn, List<?> xlsContentList, List<Map<String, String>> cellStyleList) throws Exception {
		return this.createExcelBookWithConvertAndStyle(sheetName, titles, titlesEn, xlsContentList, null, cellStyleList);
	}
	
	/**
	 * 取得Excel
	 * 
	 * @param sheetName sheet名称 
	 * @param titles 标题名称
	 * @param xlsContentList 内容
	 * @return
	 * @throws Exception 
	 */
	public HSSFWorkbook createExcelBookWithConvertAndStyle(String sheetName, String[] titles,
			String[] titlesEn, List<?> xlsContentList, Map<String, ConvertCallBack> map,
			List<Map<String, String>> cellStyleList) throws Exception {
		HSSFSheet sheet = this.initSheet(sheetName, titles, xlsContentList);
		
		if(xlsContentList != null && xlsContentList.size() > 0){
			// 对应表内容(通过反射设定)
			sheet = createContentWithStyle(sheet, titlesEn, xlsContentList, map, cellStyleList);
		}
		
		return book;
	}
	
	private HSSFSheet initSheet(String sheetName, String[] titles, List<?> xlsContentList){
		this.sheetName = sheetName;
		this.titles = titles;
		this.xlsContentList = xlsContentList;
		
		// 生成sheet
		book.createSheet(this.sheetName);
		HSSFSheet sheet = book.getSheet(this.sheetName);
		// Excel表头行
		HSSFRow titleRow = sheet.createRow(startRowNum);
		// 生成表头
		titleRow = createTitleCell(titleRow, this.titles);
		
		return sheet;
	}
	
	/**
	 * 自动创建表格头
	 * @param title
	 * @param titleNames
	 */
	private HSSFRow createTitleCell(HSSFRow titleRow, String[] titleNames){
		for(int i = 0; i < titleNames.length; i++){
			
//			// poi 2.5 乱码解决专用
//			HSSFCell cell = titleRow.createCell(new Integer(i).shortValue());
//			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//			cell.setCellValue(titleNames[i]);
			// poi 3.5
			titleRow.createCell(i).setCellValue(titleNames[i]);
		}
		return titleRow;
	}
	
	
	/**
	 * 子类中实现填充Excel内容
	 * @param sheet
	 * @return
	 * @throws Exception 
	 */
	protected abstract HSSFSheet createContent(HSSFSheet sheet,
			List<?> xlsContentList) throws Exception;
	
	/**
	 * 子类中实现填充Excel内容
	 * @param sheet
	 * @return
	 * @throws Exception 
	 */
	protected abstract HSSFSheet createContentWithStyle(HSSFSheet sheet,
			List<?> xlsContentList, List<Map<String, String>> cellStyleList) throws Exception;
	
	/**
	 * 
	 * @param sheet Excel的sheet
	 * @param titlesEn 英文标题(bean 中的属性)
	 * @param xlsContentList bean的list
	 * @param map 转换函数
	 * @return
	 * @throws Exception
	 */
	protected abstract HSSFSheet createContent(HSSFSheet sheet, String[] titlesEn,
			List<?> xlsContentList, Map<String, ConvertCallBack> map) throws Exception;
	
	/**
	 * 
	 * @param sheet Excel的sheet
	 * @param titlesEn 英文标题(bean 中的属性)
	 * @param xlsContentList bean的list
	 * @param map 转换函数
	 * @return
	 * @throws Exception
	 */
	protected abstract HSSFSheet createContentWithStyle(HSSFSheet sheet, String[] titlesEn,
			List<?> xlsContentList, Map<String, ConvertCallBack> map, 
			List<Map<String, String>> cellStyleList) throws Exception;
	

	
	/**
	 * 
	 * @param sheet
	 * @param index
	 * @return
	 */
	protected HSSFRow createRow(HSSFSheet sheet){
		cellNo = 0;
		return sheet.createRow(startRowNum++);
	}
	/**
	 * 
	 * @param sheet
	 * @param index
	 * @return
	 */
	protected HSSFRow createRow(HSSFSheet sheet, int index){
		cellNo = 0;
		return sheet.createRow(index);
	}
	
	protected void createCell(HSSFRow row, String cellValue){
//		// poi 2.5 乱码解决专用
//		HSSFCell cell = row.createCell(cellNo);
//		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//		cell.setCellValue(changeNull2Empty(cellValue));
		// poi 3.5
		row.createCell(cellNo).setCellValue(changeNull2Empty(cellValue));
		cellNo++;
	}
	
	protected void createCell(HSSFRow row, Double cellValue){
		row.createCell(cellNo).setCellValue(cellValue);
		cellNo++;
	}
	
	protected void createCell(HSSFRow row, Integer cellValue){
		row.createCell(cellNo).setCellValue(cellValue);
		cellNo++;
	}
	
	protected void createCellWithStyle(HSSFRow row, String cellValue){
//		// poi 2.5 乱码解决专用
//		HSSFCell cell = row.createCell(cellNo);
//		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
//		cell.setCellValue(changeNull2Empty(cellValue));
		// poi 3.5
		HSSFCell cell = row.createCell(cellNo);
		cell.setCellValue(changeNull2Empty(cellValue));
		cell.setCellStyle(cellStyle);
		cellNo++;
	}

	/**
	 * 工具方法，null字符串转换成空串
	 * @param str
	 * @return
	 */
	private String changeNull2Empty(String str){
		return str == null ? "" : str;
	}
	
	public HSSFWorkbook getBook() {
		return book;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}
	
}
