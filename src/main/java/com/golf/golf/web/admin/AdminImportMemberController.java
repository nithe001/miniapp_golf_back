package com.golf.golf.web.admin;

import com.golf.common.gson.JsonWrapper;
import com.golf.golf.db.MatchInfo;
import com.golf.golf.service.admin.AdminImportService;
import com.google.gson.JsonElement;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 导入成绩
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/admin/importMember")
public class AdminImportMemberController {
	private final static Logger logger = LoggerFactory.getLogger(AdminImportMemberController.class);

	@Autowired
	private AdminImportService adminImportService;

	/**
	 * 导入init
	 * @return
	 */
	@RequestMapping("init")
	public String importInit(){
		return "admin/importMember/init";
	}

	/**
	 * 导入比赛详情
	 * 是否覆盖：1：是 0：否
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = {"importMember"})

	public JsonElement importScore(@RequestParam("files[]") MultipartFile[] files,MultipartHttpServletRequest request) throws IOException {
       try {
        String suffix="";
        //校验上传文件上否为空
        if(null==files || files.length<=0){
            JsonWrapper.newErrorInstance("后台管理——导入会员详细资料为空。");
        }
         for (MultipartFile myfile : files) {
            if (!myfile.isEmpty()) {
                //强转为File
                String path = request.getSession().getServletContext().getRealPath("/upload/");
                suffix = myfile.getOriginalFilename().substring(myfile.getOriginalFilename().lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + suffix;
                File localFile = new File(path, fileName);
                myfile.transferTo(localFile);

                XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(localFile));
                adminImportService.importMember( xwb );
            }
          }
         return JsonWrapper.newSuccessInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("后台管理——导入成绩时出错。"+ e );
			return JsonWrapper.newErrorInstance("后台管理——导入成绩时出错。");
		}
	}
}
