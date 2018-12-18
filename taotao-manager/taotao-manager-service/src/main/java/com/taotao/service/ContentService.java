package com.taotao.service;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

/*
 * 内容接口
 */
public interface ContentService {

	EUDataGridResult getContentList(int page,int rows,long categoryId);
	TaotaoResult insertContent(TbContent content);
	TaotaoResult getContentById(long contentId);
	TaotaoResult updateContent(TbContent content);
	TaotaoResult deleteContent(long contentId);
	
}
