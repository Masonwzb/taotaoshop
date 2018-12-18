package com.taotao.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;

/*
 * 订单
 */
@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	
	/*
	 * 创建订单
	 */
	@RequestMapping(value="/create",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult createOrder(@RequestBody Order order){
		TaotaoResult result = null;
		try {
			result = orderService.createOrder(order, order.getOrderItems(), order.getOrderShipping());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	
	/*
	 * 根据订单Id查询订单
	 */
	@RequestMapping(value = "/info/{orderId}")
	@ResponseBody
	public TaotaoResult getOrderC(@PathVariable String orderId){
		TaotaoResult result = orderService.getOrderById(orderId);
		return result;
	}
	
	
	/*
	 * 根据用户ID分页查询订单
	 */
	@RequestMapping(value = "/list/{userId}")
	@ResponseBody
	public TaotaoResult getOrderListC(@PathVariable Long userId,
									  @RequestParam(defaultValue="1") Integer pageIndex,
									  @RequestParam(defaultValue="3") Integer pageSize){
		TaotaoResult result = orderService.getOrderList(userId,pageIndex,pageSize);
		return result;
	}
	
	
}
