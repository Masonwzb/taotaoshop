package com.taotao.service;
/*
 * 商品参数规格接口
 */

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItemParam;

public interface ItemParamService {
	EUDataGridResult getItemParamList(int page,int rows);
	TaotaoResult getItemParamByCid(long cid);
	TaotaoResult insertItemParam(TbItemParam itemParam);	
	void deleteItemParam(long paramId);
}
