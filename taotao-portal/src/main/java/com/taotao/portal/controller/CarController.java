package com.taotao.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.cookie.CookieSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.portal.pojo.CarItem;
import com.taotao.portal.service.CarService;

/*
 * 购物车Controller
 */
@Controller
@RequestMapping("/cart")
public class CarController {

	@Autowired
	private CarService carService;
	
	/*
	 * 加入购物车
	 */
	@RequestMapping("/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId,
					@RequestParam(defaultValue="1") Integer num,
					HttpServletRequest request,HttpServletResponse response){
		carService.addCarItem(itemId, num, request, response);
		return "redirect:/cart/success.html";
	}
	//加入购物车跳转页面
	@RequestMapping("/success")
	public String showSuccess(){
		return "cartSuccess";
	}
	
	/*
	 * 购物车结算，进入购物车页面
	 */
	@RequestMapping("/cart")
	public String showCart(HttpServletRequest request,HttpServletResponse response,Model model){
		List<CarItem> carItemList = carService.getCarItemList(request, response);
		model.addAttribute("cartList",carItemList);
		return "cart";
	}
	
	
	/*
	 * 删除购物车商品
	 */
	@RequestMapping("/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response){
		carService.deleteCarItem(itemId, request, response);
		return "redirect:/cart/cart.html";
	}
	
	
	/*
	 * 获取勾选的商品ID
	 */
	@RequestMapping("/checkedItemId")
	@ResponseBody
	public TaotaoResult getCheckedItemId(String[] ids,HttpServletRequest request,HttpServletResponse response){
		
		for (String string : ids) {
			System.out.println("============================这是勾选的商品ID:"+string);
		}
	    String join = org.apache.commons.lang3.StringUtils.join(ids, "-");
		
		System.out.println("----------------------------域:" + join );
		
		CookieUtils.setCookie(request, response, "TT_IDS", join);
		
		String[] array = join.split("-");
		  System.out.println("结果是5，而不是预想中的8:" + array.length);
	        for (int i = 0; i < array.length; i++) {
	            System.out.println("结果是5，而不是预想中的8:" + array[i]);
	        }
		
		return TaotaoResult.ok();
	}
	
}
