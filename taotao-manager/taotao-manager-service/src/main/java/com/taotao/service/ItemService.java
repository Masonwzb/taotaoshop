package com.taotao.service;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
/*
 * 商品接口
 */
public interface ItemService {

	TbItem getItemById(long itemId);
	TaotaoResult getItemDesc(long itemId);
	TaotaoResult getItemParam(long itemId);
	EUDataGridResult getItemList(int page,int rows);
	TaotaoResult createItem(TbItem item,String desc,String itemParam) throws Exception;
	TaotaoResult updateItem(TbItem item,String desc,String itemParams);
	TaotaoResult deleteItem(long itemId);
}
