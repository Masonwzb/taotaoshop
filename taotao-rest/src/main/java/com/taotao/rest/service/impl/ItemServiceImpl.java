package com.taotao.rest.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.pojo.TbItemParamItemExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ItemService;
/*
 * 商品基本信息
 */
@Service
public class ItemServiceImpl implements ItemService {

	//商品dao注入
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	
	//redis缓存客户端注入
	@Autowired 
	private JedisClient jedisClient;
	
	@Value("${REDIS_ITEM_KEY}")
	private String REDIS_ITEM_KEY;
	@Value("${REDIS_ITEM_EXPIRE}")
	private Integer REDIS_ITEM_EXPIRE;
	
	
	//取商品基本信息
	@Override
	public TaotaoResult getItemBaseInfo(long itemId) {
		
		try {
			//添加redis缓存逻辑
			//从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY +":"+ itemId +":base");
			//判断是否为空
			if(!StringUtils.isEmpty(json)){
				//把json转换为java对象
				TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
				return TaotaoResult.ok(item);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//根据商品Id查询商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		
		try {
			//把商品信息写入缓存
			jedisClient.set(REDIS_ITEM_KEY +":"+ itemId +":base", JsonUtils.objectToJson(item));
			//设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY +":"+ itemId +":base", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//使用TaotaoResult包装
		return TaotaoResult.ok(item);
	}


	//取商品描述信息
	@Override
	public TaotaoResult getItemDesc(long itemId) {
		
		try {
			//添加redis缓存逻辑
			//从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY +":"+ itemId +":desc");
			//判断是否为空
			if(!StringUtils.isEmpty(json)){
				//把json转换为java对象
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return TaotaoResult.ok(itemDesc);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//根据商品Id查询商品描述
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		
		try {
			//把商品信息写入缓存
			jedisClient.set(REDIS_ITEM_KEY +":"+ itemId +":desc", JsonUtils.objectToJson(itemDesc));
			//设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY +":"+ itemId +":desc", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaotaoResult.ok(itemDesc);
		
		
	}

	//取商品规格参数信息
	@Override
	public TaotaoResult getItemParam(long itemId) {
		
		try {
			//添加redis缓存逻辑
			//从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY +":"+ itemId +":param");
			//判断是否为空
			if(!StringUtils.isEmpty(json)){
				//把json转换为java对象
				TbItemParamItem itemParamItem = JsonUtils.jsonToPojo(json, TbItemParamItem.class);
				return TaotaoResult.ok(itemParamItem);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 根据商品Id查询商品规格参数
		//设置查询条件
		TbItemParamItemExample example = new TbItemParamItemExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andItemIdEqualTo(itemId);
		//执行查询条件
		List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
		if(list != null && list.size() > 0){
			TbItemParamItem itemParamItem = list.get(0);
			
			try {
				//把商品信息写入缓存
				jedisClient.set(REDIS_ITEM_KEY +":"+ itemId +":param", JsonUtils.objectToJson(itemParamItem));
				//设置key的有效期
				jedisClient.expire(REDIS_ITEM_KEY +":"+ itemId +":param", REDIS_ITEM_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return TaotaoResult.ok(itemParamItem);
		}
		return TaotaoResult.build(400, "无此商品规格信息");
	}

}
