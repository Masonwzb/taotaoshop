package com.taotao.portal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.portal.pojo.CarItem;

public interface CarService {

	TaotaoResult addCarItem(long itemId,int num,HttpServletRequest request,HttpServletResponse response);
	List<CarItem> getCarItemList(HttpServletRequest request,HttpServletResponse response);
	List<CarItem> getCarItemById(long[] itemIds,HttpServletRequest request, HttpServletResponse response);
	TaotaoResult deleteCarItem(long itemId,HttpServletRequest request, HttpServletResponse response);
} 
