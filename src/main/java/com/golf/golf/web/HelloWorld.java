package com.golf.golf.web;

import com.google.gson.JsonElement;
import com.golf.common.gson.JsonWrapper;
import com.golf.golf.common.GenericController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/hello")
public class HelloWorld extends GenericController {
	private final static Logger logger = LoggerFactory
			.getLogger(UserController.class);
	/**
	 * 登录Init
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "index")
	public JsonElement index() {
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		return JsonWrapper.newDataInstance(list);
	}

	/**
	 * 注册
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "reg")
	public JsonElement reg(String username,String password) {
		System.out.println(username);
		System.out.println(password);
		return JsonWrapper.newSuccessInstance();
	}

	/**
	 * 注册
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "login")
	public JsonElement login(String username,String password) {
		System.out.println(username);
		System.out.println(password);
		return JsonWrapper.newSuccessInstance();
	}


	/**
	 * 注册Init
	 *
	 * @return
	 */
	@RequestMapping("indexhello")
	public String register() {
		return "user/register";
	}
}
