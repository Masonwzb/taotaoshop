package com.taotao.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;

/*
 * 商品分类服务
 */
@Service
public class ItemCatServiceImpl implements ItemCatService{

	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${INDEX_ITEMCAT_REDIS_KEY}")
	private String INDEX_ITEMCAT_REDIS_KEY;
	
	@Override
	public CatResult getItemCatList() {
		
		//从缓存中取数据
		try {
			String result = jedisClient.get(INDEX_ITEMCAT_REDIS_KEY);
			if(!StringUtils.isEmpty(result)){
				//将字符串转换为对象
				CatResult catResult = JsonUtils.jsonToPojo(result, CatResult.class);
				return catResult;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		CatResult catResult = new CatResult();
		//查询分类列表
		catResult.setData(getCatList(0));
		
		//向缓存redis中存商品分类信息
		try {
			String json = JsonUtils.objectToJson(catResult);
			jedisClient.set(INDEX_ITEMCAT_REDIS_KEY, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return catResult;
	}
	
	/*
	 * 查询分类列表
	 * ￥￥￥递归￥￥￥实现将数据封装为json格式
	 */
	private List<?> getCatList(long parentId){
		//创建查询条件
		TbItemCatExample example = new TbItemCatExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
		//返回值list
		List resultList= new ArrayList();
		//向List中添加节点
		int count = 0;
		for(TbItemCat tbItemCat : list){
			//判断是否为叶子节点
			if(tbItemCat.getIsParent()){
				CatNode catNode = new CatNode();
				if(parentId == 0){
					catNode.setName("<a href='/products/"+tbItemCat.getId()+".html'>"+tbItemCat.getName()+"</a>");
				}else{
					catNode.setName(tbItemCat.getName());	
				}
				catNode.setUrl("/products/"+tbItemCat.getId()+".html");
				catNode.setItem(getCatList(tbItemCat.getId()));
		
				resultList.add(catNode);
				count++;
				//第一季类目只取14条
				if(parentId == 0 && count>=14){
					break;
				}
			}else{
				//叶子节点
				resultList.add("/products/"+tbItemCat.getId()+".html|"+tbItemCat.getName());
			}
			
		}
		return resultList;
		
	}

	
}
