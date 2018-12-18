package com.taotao.service.Impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.service.ContentService;
/*
 * 内容实现类
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${REST_CONTENT_SYNC_URL}")
	private String REST_CONTENT_SYNC_URL;
	
	
	//查询所有内容，返回值DataGrid格式
	@Override
	public EUDataGridResult getContentList(int page, int rows, long categoryId) {
		//创建查询条件
		TbContentExample example = new TbContentExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andCategoryIdEqualTo(categoryId);
		
		//查询内容、并分页处理
		PageHelper.startPage(page, rows);
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//创建返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(list);
		//记录内容总数
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		
		return result ;
	}

	//增加内容
	@Override
	public TaotaoResult insertContent(TbContent content) {
		//补全一个tbContent
		content.setCreated(new Date());
		content.setUpdated(new Date());
		tbContentMapper.insert(content);
		
		//添加redis缓存同步逻辑
		try {
			HttpClientUtil.doGet(REST_BASE_URL + REST_CONTENT_SYNC_URL + content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaotaoResult.ok();
	}

	
	/*
	 * 根据Id获取内容
	 */
	@Override
	public TaotaoResult getContentById(long contentId) {
		//设置查询条件
		TbContent content = tbContentMapper.selectByPrimaryKey(contentId);
		return TaotaoResult.ok(content);
	}

	
	/*
	 * 更新内容
	 */
	@Override
	public TaotaoResult updateContent(TbContent content) {
		//补全更新内容
		content.setUpdated(new Date());
		
		//执行更新
		tbContentMapper.updateByPrimaryKeySelective(content);
		
		//添加redis缓存同步逻辑
				try {
					HttpClientUtil.doGet(REST_BASE_URL + REST_CONTENT_SYNC_URL + content.getCategoryId());
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		return TaotaoResult.ok();
	}

	
	
	/*
	 * 根据Id删除文本
	 */
	@Override
	public TaotaoResult deleteContent(long contentId) {
		//根据id删除文本
		tbContentMapper.deleteByPrimaryKey(contentId);
		
		//添加redis缓存同步逻辑
		try {
			//根据Id查询类别Id
			TbContent content = tbContentMapper.selectByPrimaryKey(contentId);
			HttpClientUtil.doGet(REST_BASE_URL + REST_CONTENT_SYNC_URL + content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaotaoResult.ok();
	}

}
