package com.golf.golf.web;

import com.golf.common.gson.JsonWrapper;
import com.golf.golf.common.GenericController;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页
 *
 * @author peihong
 * 2017年05月10日
 */
@Controller
@RequestMapping(value = "/")
public class IndexController extends GenericController {
	private final static Logger logger = LoggerFactory
			.getLogger(IndexController.class);

	/**
	 * 权限不足页面
	 * @return
	 */
	@RequestMapping("403")
	public String notFound(){
		return "403";
	}

}
