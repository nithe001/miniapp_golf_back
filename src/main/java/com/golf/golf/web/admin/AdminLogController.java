package com.golf.golf.web.admin;

import com.golf.common.Const;
import com.golf.common.model.POJOPageInfo;
import com.golf.common.model.SearchBean;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.admin.AdminMatchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志
 * @author nmy
 * 2016年12月27日
 */
@Controller
@RequestMapping(value = "/admin/logs")
public class AdminLogController {
	private final static Logger logger = LoggerFactory.getLogger(AdminLogController.class);

	/**
	 * 日志列表
	 * @return
	 */
	@RequestMapping("list")
	public String list(ModelMap mm){
		List<String> nameList = new ArrayList<>();
		try {
			//获取根路径
			String path = WebUtil.getPath();
			File f = new File(path);
			String pPath = f.getParent();
			System.out.println(pPath);
			File parentFile = new File(pPath);
//			File parentFile = new File("D:\\workspace\\logs");
			File filelist[] = parentFile.listFiles();
			for(File f_ : filelist){
				String filename = f_.getName();
				if(filename.contains("miniapp_golf")){
					nameList.add(filename);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理-获取服务器日志列表时出错。"+ e );
			return "admin/error";
		}
		mm.addAttribute("nameList",nameList);
		return "admin/logs/list";
	}


	/**
	 * 日志列表
	 * @return
	 */
	@RequestMapping("download")
	public void download(HttpServletResponse response, String fileName){
		//获取根路径
		String path_ = WebUtil.getPath();
		File f = new File(path_);
		String pPath_ = f.getParent();
//		pPath_= "D:\\workspace\\logs";
		System.out.println(pPath_);
		File file = new File(pPath_,fileName);
		// 取得文件名。
		fileName = file.getName();
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/force-download");// 设置强制下载不打开
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso8859-1"));
			response.setHeader("Content-Length", String.valueOf(file.length()));

			byte[] b = new byte[1024];
			int len;
			while ((len = fis.read(b)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
			response.flushBuffer();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("后台管理-下载日志时出错。"+ e );
		}
	}

}
