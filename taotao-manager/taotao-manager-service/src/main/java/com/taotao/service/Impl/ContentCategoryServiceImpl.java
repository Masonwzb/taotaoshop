package com.taotao.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EUTreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.service.ContentCategoryService;
/*
 * 内容分类管理
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	//查询所有节点
	@Override
	public List<EUTreeNode> getCategoryList(long parentId) {
		// 根据parentId查询节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EUTreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			//创建一个节点
			EUTreeNode node = new EUTreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			resultList.add(node);
		}
		return resultList;
	}

	//插入节点
	@Override
	public TaotaoResult insertContentCategory(long parentId, String name) {
		
		//创建一个pojo
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setName(name);
		contentCategory.setIsParent(false);
		//1:正常 2：删除
		contentCategory.setStatus(1);
		contentCategory.setParentId(parentId);
		contentCategory.setSortOrder(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//添加记录
		contentCategoryMapper.insert(contentCategory);
		//查看父节点的isParent列是否为true,如果不是true改成true
		TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentId);
		//判断是否为true
		if(!parentCat.getIsParent()){
			parentCat.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		//返回结果
		return TaotaoResult.ok(contentCategory);
	}

	//删除节点
	@Override
	public TaotaoResult deleteContentCategory(long id) {

		TbContentCategory contentcat = contentCategoryMapper.selectByPrimaryKey(id);
		//判断当前节点是否为父节点
		if(contentcat.getIsParent()){
			List<TbContentCategory> list = getContentCategoryByparentId(id);
			for (TbContentCategory tbContentCategory : list) {
				deleteContentCategory(tbContentCategory.getId());
			}
		}
		
		//判断该节点对应的父节点是否还有子节点，如果只剩当前要删除的这个节点将isparent改为false
		if(getContentCategoryByparentId(contentcat.getParentId()).size() == 1){
			TbContentCategory parentcc = contentCategoryMapper.selectByPrimaryKey(contentcat.getParentId());
			parentcc.setIsParent(false);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentcc);
		}
		
		// 删除id对应的记录
		System.out.println("删除节点为："+id);
		contentCategoryMapper.deleteByPrimaryKey(id);
		return TaotaoResult.ok();
	}
	
	//查询parentId为此值的节点
	private List<TbContentCategory> getContentCategoryByparentId(long parentid){
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andParentIdEqualTo(parentid);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		return list;
	}

	//更新节点
	@Override
	public TaotaoResult updateContentCategory(long id, String name) {
		TbContentCategory contentcat = contentCategoryMapper.selectByPrimaryKey(id);
		contentcat.setName(name);
		contentCategoryMapper.updateByPrimaryKey(contentcat);
		return TaotaoResult.ok();
	}

}
