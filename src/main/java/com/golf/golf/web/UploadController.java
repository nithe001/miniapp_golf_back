package com.golf.golf.web;

import com.google.gson.JsonElement;
import com.golf.common.gson.JsonWrapper;
import com.golf.common.spring.mvc.WebUtil;
import com.golf.common.util.PropertyConst;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 上传图片
 * 
 * @author nmy
 * 2016年10月31日
 */
@Controller
@RequestMapping(value = "/upload")
public class UploadController {
	private final static Logger logger = LoggerFactory.getLogger(UploadController.class);
    /**
     * 上传缩略图
     * @return
     */
    @ResponseBody
    @RequestMapping(value = { "uploadPicPc" }, method = RequestMethod.POST)
    public JsonElement uploadPic(MultipartFile file){
        String folderPath = PropertyConst.PIC_PATH;
		String path = WebUtil.getRealPath(folderPath);
        String fileName = file.getOriginalFilename();
        //重命名
        String ext = this.getSuffix(fileName);
        fileName = new Date().getTime() + "." + ext;
        File targetFile = new File(path, fileName);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        //保存
        try {
            file.transferTo(targetFile);
            /*String ext = this.getSuffix(fileName);
            destFileName = new Date().getTime() + "." + ext;
            ImageUtil.resizeFixedWidth(targetFile.getAbsolutePath(), path + File.separator + destFileName, 200, 1F);
            targetFile.delete();*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("pc端上传图片时出错。",e );
            return JsonWrapper.newErrorInstance("pc端上传图片时出错。");
        }
        return JsonWrapper.newDataInstance(folderPath + File.separator +  fileName);
    }

	/**
	 * 上传头像缩略图(调整手机上传图片横向的问题)
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "uploadHeadImg" }, method = RequestMethod.POST)
	public JsonElement uploadPicPc(MultipartFile file, HttpServletRequest request) {
		String path = WebUtil.getRealPath(PropertyConst.HEADIMG_PATH);
		String fileName = file.getOriginalFilename();
		System.out.println(path);// 要保存的路径
		// 重命名
		String ext = this.getSuffix(fileName);
		fileName = new Date().getTime() + "." + ext;
		File targetFile = new File(path, fileName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		// 保存
		BufferedImage bi = null;
		try {
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传图片时出错。" ,e);
			return JsonWrapper.newErrorInstance("上传图片时出错。");
		}
		return JsonWrapper.newDataInstance(PropertyConst.HEADIMG_PATH + File.separator+fileName);
	}

	// 获取文件后缀
	private String getSuffix(String filename) {
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0 && pos < filename.length() - 1) {
			suffix = filename.substring(pos + 1);
		}
		return suffix;
	}


	/**
	 * 使用插件上传缩略图
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "uploadImg" }, method = RequestMethod.POST)
	public JsonElement uploadAction(MultipartFile file,
			HttpServletRequest request) {
		String destFileName = "";
		int tag = 0;
		String errMsg = null;
		String path = WebUtil.getRealPath(PropertyConst.PIC_PATH);
		String fileName = file.getOriginalFilename();
		System.out.println(path);// 要保存的路径
		File targetFile = new File(path, fileName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		// 保存
		try {
			file.transferTo(targetFile);
			String ext = this.getSuffix(fileName);
			// 图片压缩？？？？
			if ("jpg".equals(ext) || "JPG".equals(ext) || "png".equals(ext)) {
				tag = 1;
				destFileName = new Date().getTime() + "." + ext;
//				ImageUtil.resize(targetFile.getAbsolutePath(), path
//						+ File.separator + destFileName, 200);
                Thumbnails.of(targetFile.getAbsolutePath()).width(200).toFile(path
                        + File.separator + destFileName);

				targetFile.delete();
			} else {
				destFileName = fileName;
			}
			System.out.println(PropertyConst.PIC_PATH + destFileName);
		} catch (Exception e) {
			e.printStackTrace();
			if (tag == 1) {
				errMsg = "上传图片时出错。";
			} else {
				errMsg = "上传视频时出错。";
			}
			logger.error(errMsg + e);
			return JsonWrapper.newErrorInstance(errMsg);
		}
		return JsonWrapper.newDataInstance(PropertyConst.PIC_PATH
				+ destFileName);
	}

	/**
	 * 上传PDF
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "uploadPdf" }, method = RequestMethod.POST)
	public JsonElement uploadPdf(MultipartFile file){
		String folderPath =PropertyConst.PPT_PATH;
		String path = WebUtil.getRealPath(folderPath);
		String fileName = file.getOriginalFilename();
		//重命名
		String ext = this.getSuffix(fileName);
		fileName = new Date().getTime() + "." + ext;
		File targetFile = new File(path, fileName);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		//保存
		try {
			//上传到
			file.transferTo(targetFile);
			//拷贝到pdf转图片的目录下
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传PPT时出错。",e );
			return JsonWrapper.newErrorInstance("上传PPT时出错。");
		}
		return JsonWrapper.newDataInstance(folderPath + fileName);
	}



    /**
     * 创建球队——上传logo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = { "uploadTeamLogo" }, method = RequestMethod.POST)
    public String uploadTeamLogo(MultipartFile file) throws IOException {
        logger.error("进入上传球队logo请求。");
        try {
            String logoPath = null;
            System.out.println("执行upload");
            logger.info("执行图片上传");
            if(!file.isEmpty()) {
                logger.info("成功获取照片");
                String fileName = file.getOriginalFilename();
                String type = null;
                type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
                logger.info("图片初始名称为：" + fileName + " 类型为：" + type);
                if (type != null) {
                    if ("GIF".equals(type.toUpperCase())||"PNG".equals(type.toUpperCase())||"JPG".equals(type.toUpperCase())) {
                        // 项目在容器中实际发布运行的根路径
                        String realPath = WebUtil.getRealPath(PropertyConst.TEAM_LOGO_PATH);
                        // 自定义的文件名称
                        String trueFileName = String.valueOf(System.currentTimeMillis()) + "."+type;

                        File targetFile = new File(realPath, trueFileName);
                        if(!targetFile.exists()){
                            targetFile.mkdirs();
                        }
                        file.transferTo(targetFile);
                        logoPath = PropertyConst.TEAM_LOGO_PATH+trueFileName;
                        logger.info("文件成功上传到指定目录下");
                    }else {
                        logger.info("文件类型不正确");
                        return "error";
                    }
                }else {
                    logger.info("文件不存在");
                    return "error";
                }
            }else {
                logger.info("文件不存在");
                return "error";
            }
//            return JsonWrapper.newDataInstance(logoPath);
            return logoPath;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传球队logo时出错。" ,e);
            return "error";
        }
    }
}