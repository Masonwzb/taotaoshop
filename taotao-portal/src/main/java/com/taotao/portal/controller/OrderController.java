package com.taotao.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Conversion;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.pojo.CarItem;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.CarService;
import com.taotao.portal.service.OrderService;

/*
 * 订单页面
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private CarService carService;
	@Autowired 
	private OrderService orderService;
	
	@RequestMapping("/order-cart")
	public String showOrderCart(HttpServletRequest request,HttpServletResponse response,Model model){
		
		String cookieValue = CookieUtils.getCookieValue(request, "TT_IDS");
		System.out.println("sssssssssssssssssssssssssssss+ :" + cookieValue);
		
		//将获取的Ids字符串拆分
		String[] string = cookieValue.split("-");
		long[] itemIds = new long[string.length];
		for(int i = 0; i<string.length; i++){
			itemIds[i] = Long.valueOf(string[i]);
		}
		
		//取购物车商品列表
		List<CarItem> carItemList = carService.getCarItemById(itemIds, request, response);
		model.addAttribute("cartList",carItemList);
		return "order-cart";
	}
	
	@RequestMapping("/create")
	public String createOrder(Order order,Model model,HttpServletRequest request){
		try {
			//从Request中取用户信息
			TbUser user = (TbUser) request.getAttribute("user");
			//在order对象中补全用户信息
			order.setUserId(user.getId());
			order.setBuyerNick(user.getUsername());
			
			//调用服务
			String orderId = orderService.createOrder(order);
			model.addAttribute("orderId",orderId);
			model.addAttribute("payment",order.getPayment());
			model.addAttribute("date",new DateTime().plusDays(3).toString("yyyy-MM-dd"));
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message","创建订单出错！");
			return "error/exception";
		}
	}
	
	
	/*
	 * 显示订单
	 */
	@RequestMapping("/list")
	public String showOrder(HttpServletRequest request,Model model,
							@RequestParam(defaultValue="1") Integer pageIndex,
							@RequestParam(defaultValue="3") Integer pageSize){
		EUDataGridResult result = orderService.getOrderList(request,pageIndex,pageSize);
		//获取订单信息
		List<Order> orderList = (List<Order>) result.getRows();
		model.addAttribute("orderList",orderList);
		//获取订单总数量
		long total = result.getTotal();
		model.addAttribute("recordCount",total);
		model.addAttribute("pageIndex",pageIndex);
		model.addAttribute("pageSize",pageSize);
		return "my-orders";
	}
	
}
