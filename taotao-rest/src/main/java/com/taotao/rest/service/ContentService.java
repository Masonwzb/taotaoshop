package com.taotao.rest.service;

import java.util.List;

import com.taotao.pojo.TbContent;

/*
 * 前端内容接口
 */
public interface ContentService {
	
	List<TbContent> getContentList(long contentCid);
}
