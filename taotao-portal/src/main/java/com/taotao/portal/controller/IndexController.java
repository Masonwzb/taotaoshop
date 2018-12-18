package com.taotao.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.portal.service.ContentService;

@Controller
public class IndexController {

	@Autowired
	private ContentService contentService;
	
	/*
	 * 显示首页
	 */
	@RequestMapping("/index")
	public String showIndex(Model model){
		String adjson = contentService.getContentList();
		model.addAttribute("ad1", adjson);
		return "index";
	}
	
	
	@RequestMapping(value="/httpclient/post",method=RequestMethod.POST,produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String testPost(String username,String password){
		String result = "username:"+username+",password:"+password;
		System.out.println(result);
		return result;
	}
	
}
