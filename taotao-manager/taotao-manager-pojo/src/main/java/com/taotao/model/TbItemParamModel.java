package com.taotao.model;

import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemParam;

/*
 * 后台商品规格展示model实体
 */
public class TbItemParamModel extends TbItemParam{

	private TbItemCat itemcat;

	public TbItemParamModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TbItemParamModel(TbItemCat itemcat) {
		super();
		this.itemcat = itemcat;
	}

	public TbItemCat getItemcat() {
		return itemcat;
	}

	public void setItemcat(TbItemCat itemcat) {
		this.itemcat = itemcat;
	}
	
}
