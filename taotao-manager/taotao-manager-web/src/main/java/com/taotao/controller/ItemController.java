package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
/*
 * 商品管理Controller
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	/*
	 * 根据商品Id查询商品信息
	 */
	@RequestMapping("/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId){
		TbItem itemById = itemService.getItemById(itemId);
		return itemById;
	}
	/*
	 *根据商品ID查询商品描述信息
	 */
	@RequestMapping(value = "/desc/{itemId}")
	@ResponseBody
	public TaotaoResult getItemDescC(@PathVariable Long itemId){
		TaotaoResult result = itemService.getItemDesc(itemId);
		return result;
	}
	/*
	 * 根据商品Id查询商品规格参数
	 */
	@RequestMapping(value = "/param/{itemId}")
	@ResponseBody
	public TaotaoResult getItemParmC(@PathVariable Long itemId){
		TaotaoResult result = itemService.getItemParam(itemId);
		return result;
	}
	
	
	/*
	 * 查询所有商品
	 */
	@RequestMapping("/list")
	@ResponseBody
	public EUDataGridResult getItemList(Integer page,Integer rows){
		EUDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	
	/*
	 * 添加商品信息
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult createItem(TbItem item,String desc,String itemParams) throws Exception {
		TaotaoResult result = itemService.createItem(item,desc,itemParams);
		return result;
	}
	
	
	/*
	 * 更新商品信息
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult updateItemC(TbItem item,String desc,String itemParams){
		TaotaoResult result = itemService.updateItem(item, desc, itemParams);
		return result;
	}
	
	
	/*
	 * 批量删除商品
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public TaotaoResult deleteItem(long[] ids){
		//遍历数组
		int count = 0;
		for (long itemId : ids) {
			System.out.println(itemId);
			itemService.deleteItem(itemId);
			count++;
		}
		if(count == ids.length){
			return TaotaoResult.ok();
		}
		return null;
	
	}
	
	
	
}
