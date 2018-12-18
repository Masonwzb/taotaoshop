package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.service.ContentService;

/*
 * 内容控制器
 */
@Controller
@RequestMapping("/content")
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/query/list")
	@ResponseBody
	public EUDataGridResult getContentLists(Integer page,Integer rows,Long categoryId){
		EUDataGridResult result = contentService.getContentList(page, rows, categoryId);
		return result;
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public TaotaoResult createContent(TbContent content){
		TaotaoResult result = contentService.insertContent(content);
		return result;
	}
	
	
	/*
	 * 根据Id查询文本
	 */
	@RequestMapping("/queryById/{contentId}")
	@ResponseBody
	public TaotaoResult getContentByIdC(@PathVariable long contentId){
		TaotaoResult result = contentService.getContentById(contentId);
		return result;
	}
	
	
	/*
	 * 更新内容
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public TaotaoResult editContent(TbContent content){
		TaotaoResult result = contentService.updateContent(content);
		return result;
	}
	
	/*
	 * 批量删除内容
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public TaotaoResult deleteContentC(long[] ids){
		//遍历商品
		int count = 0;
		for (long contentId : ids) {
			contentService.deleteContent(contentId);
			count++;
		}
		if(count == ids.length){
			System.out.println(ids.length);
			return TaotaoResult.ok();
		}
		return null;
	}
	
}
