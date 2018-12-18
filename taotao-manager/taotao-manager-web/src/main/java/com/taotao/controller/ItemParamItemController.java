package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.service.ItemPramItemService;

/*
 * 展示商品规格参数
 */
@Controller
public class ItemParamItemController {
	
	@Autowired
	private ItemPramItemService itemPramItemService;
	
	@RequestMapping("/showitem/{itemId}")
	public String showItemParam(@PathVariable Long itemId,Model model){
		String string = itemPramItemService.getItemParamByItemId(itemId);
		model.addAttribute("itemparam",string);	
		return "item";
	}
	
}
