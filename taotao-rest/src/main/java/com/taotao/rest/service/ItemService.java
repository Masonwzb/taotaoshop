package com.taotao.rest.service;

import com.taotao.common.pojo.TaotaoResult;

/*
 * 商品基本信息
 */
public interface ItemService {

	TaotaoResult getItemBaseInfo(long itemId);
	TaotaoResult getItemDesc(long itemId);
	TaotaoResult getItemParam(long itemId);
}
