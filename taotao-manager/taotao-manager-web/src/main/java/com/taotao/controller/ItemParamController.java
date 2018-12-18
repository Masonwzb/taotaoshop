package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItemParam;
import com.taotao.service.ItemParamService;

/*
 * 商品规格参数controller
 */
@Controller
@RequestMapping("/item/param")
public class ItemParamController {

	@Autowired
	private ItemParamService itemParamService;
	
	@RequestMapping("/list")
	@ResponseBody
	public EUDataGridResult getItemParamList(@RequestParam(defaultValue="1") Integer page,
											 @RequestParam(defaultValue="30") Integer rows){
		EUDataGridResult result = itemParamService.getItemParamList(page, rows);
		System.out.println(result.getTotal());
		System.out.println(result.getRows());
		return result;
	}
	
	@RequestMapping("/query/itemcatid/{itemCatId}")
	@ResponseBody
	public TaotaoResult getItemParamByCid(@PathVariable long itemCatId){
		TaotaoResult result = itemParamService.getItemParamByCid(itemCatId);
		return result;
	}
	
	@RequestMapping("/save/{cid}")
	@ResponseBody
	public TaotaoResult insertItemParam(@PathVariable Long cid,String paramData){
		//创建pojo对象
		TbItemParam itemParam = new TbItemParam();
		itemParam.setItemCatId(cid);
		itemParam.setParamData(paramData);
		TaotaoResult result = itemParamService.insertItemParam(itemParam);
		return result;
	}
	
	
	/*
	 * 批量删除商品
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public TaotaoResult deleteItemParamC(long[] ids){
		//遍历商品
		int count = 0;
		for (long paramId : ids) {
			itemParamService.deleteItemParam(paramId);
			count++;
		}
		if(count == ids.length){
			System.out.println(ids.length);
			return TaotaoResult.ok();
		}
		return null;
	}
	
}
