package com.taotao.service.Impl;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
/*
 * 商品管理Service
 */
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper ItemDescMapper;
	
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	
	//根据商品ID查询商品信息
	@Override
	public TbItem getItemById(long itemId) {
		
		//TbItem item = itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		//添加查询条件
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list =  itemMapper.selectByExample(example);
		if(list != null && list.size()>0){
			TbItem tbItem = list.get(0);
			return tbItem;
		}
		
		return null;
	}
	/*
	 * 根据商品ID查询商品描述信息(non-Javadoc)
	 * @see com.taotao.service.ItemService#getItemDesc(long)
	 */
	@Override
	public TaotaoResult getItemDesc(long itemId) {
		//查询条件
		TbItemDesc itemDesc = ItemDescMapper.selectByPrimaryKey(itemId);
		return TaotaoResult.ok(itemDesc);
	}
	/*
	 * 根据商品Id查询商品规格参数
	 */
	@Override
	public TaotaoResult getItemParam(long itemId) {
		//设置查询条件
		TbItemParamItemExample example = new TbItemParamItemExample();
		com.taotao.pojo.TbItemParamItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
		if(list != null && list.size() > 0){
			TbItemParamItem tbItemParamItem = list.get(0);
			return TaotaoResult.ok(tbItemParamItem);
		}
		return TaotaoResult.build(400, "无此商品规格");
	
	}

	
	
	//查询所有商品，返回值DataGrid格式
	@Override
	public EUDataGridResult getItemList(int page, int rows) {
		//查询商品列表
		TbItemExample example = new TbItemExample();
		//分页处理
		PageHelper.startPage(page,rows);
		List<TbItem> list = itemMapper.selectByExample(example);
		//创建一个返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(list);
		//取记录总条数
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	
	
	/*
	 * 添加商品
	 */
	@Override
	public TaotaoResult createItem(TbItem item,String desc,String itemParam) throws Exception{
		//item补全
		//生成商品ID
		Long itemId = IDUtils.genItemId();
		item.setId(itemId);
		// '商品状态，1-正常，2-下架，3-删除',
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//插入到数据库
		itemMapper.insert(item);
		//添加商品描述信息
		TaotaoResult result = insertItemDesc(itemId, desc);
		if(result.getStatus() != 200){
			throw new Exception();
		}
		//添加商品规格参数
		result = insertItemParamItem(itemId, itemParam);
		if(result.getStatus() != 200){
			throw new Exception();
		}
		return TaotaoResult.ok();
	}
	/*
	 * 添加商品描述
	 */
	private TaotaoResult insertItemDesc(Long itemId,String desc){
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		ItemDescMapper.insert(itemDesc);
		
		return TaotaoResult.ok();
	}
	/*
	 * 添加商品规格参数
	 */
	private TaotaoResult insertItemParamItem(Long itemId,String itemParam){
		//创建一个pojo
		TbItemParamItem itemParamItem = new TbItemParamItem();
		itemParamItem.setItemId(itemId);
		itemParamItem.setParamData(itemParam);
		itemParamItem.setCreated(new Date());
		itemParamItem.setUpdated(new Date());
		//向表中插入数据
		itemParamItemMapper.insert(itemParamItem);

		return TaotaoResult.ok();
	}
	
	
	
	/*
	 * 编辑商品并更新
	 */
	@Override
	public TaotaoResult updateItem(TbItem item, String desc, String itemParams) {
		
		//补全更新内容
		item.setUpdated(new Date());
		//执行商品更新
		itemMapper.updateByPrimaryKeySelective(item);
		
		//更新商品描述信息
		updateItemDesc(item.getId(), desc);
		
		//更新商品规格参数
		updateItemParam(item.getId(), itemParams);
		
		return TaotaoResult.ok();
	}
	/*
	 *编辑商品描述并更新
	 */
	private TaotaoResult updateItemDesc(Long itemId,String desc){
		//根据商品Id更新商品描述信息
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		
		//执行更新
		ItemDescMapper.updateByPrimaryKeySelective(itemDesc);

		
		return TaotaoResult.ok();
	}
	/*
	 * 编辑商品规格并更新
	 */
	private TaotaoResult updateItemParam(Long itemId,String itemParams){
		//设置更新条件
		TbItemParamItemExample example = new TbItemParamItemExample();
		com.taotao.pojo.TbItemParamItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andItemIdEqualTo(itemId);
		
		//根据商品Id更新商品规格信息
		TbItemParamItem itemParamItem = new TbItemParamItem();
		itemParamItem.setParamData(itemParams);
		itemParamItem.setUpdated(new Date());
		
		//执行更新
		itemParamItemMapper.updateByExampleSelective(itemParamItem, example);
	
		return TaotaoResult.ok();
	}
	
	
	
	/*
	 * 删除商品
	 */
	@Override
	public TaotaoResult deleteItem(long itemId) {
		//删除商品基本信息
		itemMapper.deleteByPrimaryKey(itemId);
		
		//删除商品描述信息
		ItemDescMapper.deleteByPrimaryKey(itemId);
		
		//删除商品规格参数信息
		//设置条件
		TbItemParamItemExample example = new TbItemParamItemExample();
		com.taotao.pojo.TbItemParamItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andItemIdEqualTo(itemId);
		//执行删除
		itemParamItemMapper.deleteByExample(example);
		
		return TaotaoResult.ok();
	}


}
