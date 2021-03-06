package com.taotao.portal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.portal.pojo.Order;

public interface OrderService {
	
	String createOrder(Order order);
	EUDataGridResult getOrderList(HttpServletRequest request,int pageIndex,int pageSize);
}
