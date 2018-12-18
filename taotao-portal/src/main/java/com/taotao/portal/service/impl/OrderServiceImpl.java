package com.taotao.portal.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.OrderService;
@Service
public class OrderServiceImpl implements OrderService {

	@Value("${ORDER_BASE_URL}")
	private String ORDER_BASE_URL;
	@Value("${ORDER_CREATE_URL}")
	private String ORDER_CREATE_URL;
	@Value("${ORDER_GET_LIST}")
	private String ORDER_GET_LIST;
	
	@Override
	public String createOrder(Order order) {
		//调用Taotao-Order的服务提交订单
		String json = HttpClientUtil.doPostJson(ORDER_BASE_URL + ORDER_CREATE_URL, JsonUtils.objectToJson(order));
		//把json转换成taotaoResult
		TaotaoResult taotaoResult = TaotaoResult.format(json);
		if(taotaoResult.getStatus() == 200){
			Object orderId = (Object) taotaoResult.getData();
			return orderId.toString();
		}
		return "";
	}

	@Override
	public EUDataGridResult getOrderList(HttpServletRequest request,int pageIndex,int pageSize) {
		// 从request域中获取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		//调用Taotao-Order的服务获取订单信息
		String json = HttpClientUtil.doGet(ORDER_BASE_URL + ORDER_GET_LIST + user.getId() +
											"?pageIndex=" + pageIndex + "&pageSize=" + pageSize);
		if(!StringUtils.isEmpty(json)){
			//把json转换成taotaoResult
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, EUDataGridResult.class);
			if(taotaoResult.getStatus() == 200){
				EUDataGridResult result = (EUDataGridResult) taotaoResult.getData();
				return result;
			}
		}
		return null;
	}

}
