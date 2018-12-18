package com.taotao.portal.service;

import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.ItemInfo;

public interface ItemService {

	ItemInfo getItemById(long itemId);
	String getItemDescById(long itemId);
	String getItemParam(Long itemId);
}
