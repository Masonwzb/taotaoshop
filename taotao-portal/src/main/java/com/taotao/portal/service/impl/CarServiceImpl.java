package com.taotao.portal.service.impl;

import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.CarItem;
import com.taotao.portal.service.CarService;
/*
 * 购物车
 */
@Service
public class CarServiceImpl implements CarService {

	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITEM_INFO_URL}")
	private String ITEM_INFO_URL;
	
	/*
	 * 添加购物车商品(non-Javadoc)
	 * @see com.taotao.portal.service.CarService#addCarItem(long, int)
	 */
	@Override
	public TaotaoResult addCarItem(long itemId, int num,
			HttpServletRequest request,HttpServletResponse response) {
		
		
		//取购物车商品列表
		CarItem carItem = null;	
		List<CarItem> carItemList = getCarItemListPrivate(request);
		//判断购物车是否存在此商品
		for (CarItem cItem : carItemList) {
			//如果存在此商品
			if(cItem.getId() == itemId){
				//增加商品数量
				cItem.setNum(cItem.getNum() + num);
				carItem = cItem;
				break;
			}
		}
		
		if(carItem == null){
			carItem = new CarItem();
			// 根据商品Id查询商品基本信息
			String json = HttpClientUtil.doGet(REST_BASE_URL + ITEM_INFO_URL + itemId);
			//把json转换成java对象
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItem.class);
			if(taotaoResult.getStatus() == 200){
				TbItem item = (TbItem) taotaoResult.getData();
				carItem.setId(item.getId());
				carItem.setTitle(item.getTitle());
				carItem.setImage(item.getImage() == null ? "":item.getImage().split(",")[0]);
				carItem.setNum(num);
				carItem.setPrice(item.getPrice());
			}
			//添加到购物车列表
			carItemList.add(carItem);
		}
		//把购物车列表写入cookie
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(carItemList), true);
		
		return TaotaoResult.ok();
	}
	
	/*
	 * 私有方法
	 * 从Cookie中取商品列表
	 */
	private List<CarItem> getCarItemListPrivate(HttpServletRequest request){
		//从cookie中取商品列表
		String carJson = CookieUtils.getCookieValue(request, "TT_CART", true);
		if(carJson == null){
			return new ArrayList<>();
		}
		try {
			//把json转换成商品列表
			List<CarItem> list = JsonUtils.jsonToList(carJson, CarItem.class);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	
	/*
	 * 取cookie中的商品列表(non-Javadoc)
	 * @see com.taotao.portal.service.CarService#getCarItemList(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public List<CarItem> getCarItemList(HttpServletRequest request, HttpServletResponse response) {
		List<CarItem> carItemList = getCarItemListPrivate(request);
		return carItemList;
	}

	/*
	 * 删除购物车商品(non-Javadoc)
	 * @see com.taotao.portal.service.CarService#deleteCarItem(long)
	 */
	@Override
	public TaotaoResult deleteCarItem(long itemId,HttpServletRequest request, HttpServletResponse response) {
		//从cookie中取购物车商品列表
		List<CarItem> carItemList = getCarItemListPrivate(request);
		//从列表中找到此商品
		for (CarItem carItem : carItemList) {
			if(carItem.getId() == itemId){
				carItemList.remove(carItem);
				break;
			}
		}
		//从购物车列表重新写入
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(carItemList), true);
		return TaotaoResult.ok();
	}

	
	/*
	 * 根据商品Id查询商品基本信息
	 */
	@Override
	public List<CarItem> getCarItemById(long[] itemIds,HttpServletRequest request, HttpServletResponse response) {

		//选取勾选的购物车商品
		List<CarItem> carItemListById = new ArrayList<CarItem>();
		
		//从cookie中取购物车商品列表
		List<CarItem> carItemList = getCarItemListPrivate(request);
		//根据找到此商品
		for (long id : itemIds) {
			for (CarItem carItem : carItemList) {
				if(carItem.getId() == id){
					carItemListById.add(carItem);
				}
			}
		}
		
		return carItemListById;
	}

}
