package com.taotao.order.service;

import java.util.List;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

/*
 * 订单接口
 */
public interface OrderService {

	TaotaoResult createOrder(TbOrder order,List<TbOrderItem> itemList, TbOrderShipping orderShipping);
	TaotaoResult getOrderById(String orderId);
	TaotaoResult getOrderList(long userId,int pageIndex,int pageSize);
}
