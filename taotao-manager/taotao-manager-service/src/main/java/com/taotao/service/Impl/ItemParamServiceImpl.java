package com.taotao.service.Impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.chainsaw.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.ModelMapper.TbItemParamModelMapper;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.model.TbItemParamModel;
import com.taotao.pojo.ItemParam;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamExample;
import com.taotao.pojo.TbItemParamExample.Criteria;
import com.taotao.service.ItemParamService;
/*
 * 商品规格参数Service
 */
@Service
public class ItemParamServiceImpl implements ItemParamService {

	@Autowired
	private TbItemParamMapper tbItemParamMapper;
	@Autowired
	private TbItemParamModelMapper itemParamModelMapper;
	
	//查询所有商品规格参数
	@Override
	public EUDataGridResult getItemParamList(int page, int rows) {
				
				//分页处理
				PageHelper.startPage(page,rows);
				List<ItemParam> list = itemParamModelMapper.selectItemParamList();
				//创建一个返回值对象
				EUDataGridResult result = new EUDataGridResult();
				result.setRows(list);
				//取记录总条数
				PageInfo<ItemParam> pageInfo = new PageInfo<>(list);
				result.setTotal(pageInfo.getTotal());
				System.out.println(result);
				return result;
	}

	
	//根据商品类别ID查询商品规格参数信息
	@Override
	public TaotaoResult getItemParamByCid(long cid) {
		
		TbItemParamExample example = new TbItemParamExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemCatIdEqualTo(cid);
		List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(example);
		//判断是否查询到结果
		if(list != null && list.size()>0){
			return TaotaoResult.ok(list.get(0));
		}
		
		return TaotaoResult.ok();
	}

	
	//添加商品规格参数
	@Override
	public TaotaoResult insertItemParam(TbItemParam itemParam) {
		//补全pojo
		itemParam.setCreated(new Date());
		itemParam.setUpdated(new Date());
		//插入到规格参数模板表
		tbItemParamMapper.insert(itemParam);
		return TaotaoResult.ok();
	}

	
	/*
	 * 根据规格参数Id删除规格参数
	 */
	@Override
	public void deleteItemParam(long paramId) {
		tbItemParamMapper.deleteByPrimaryKey(paramId);
	}


}
