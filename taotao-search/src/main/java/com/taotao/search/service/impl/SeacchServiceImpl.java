package com.taotao.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.search.dao.SearchDao;
import com.taotao.search.pojo.SearchResult;
import com.taotao.search.service.SearchService;
/*
 * 搜索Service
 */
@Service
public class SeacchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	
	@Override
	public SearchResult search(String queryString, int page, int rows) throws Exception {
		//创建查询对象
		SolrQuery query = new SolrQuery();
		//设置查询条件
		query.setQuery(queryString);
		//设置分页
		query.setStart((page-1) * rows);
		query.setRows(rows);
		//设置默认关键字
		query.set("df", "item_keywords");
		//设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		//执行查询
		SearchResult searchresult = searchDao.search(query);
		long recordCount = searchresult.getRecordCount();
		long pageCount = recordCount / rows;
		if(recordCount % rows > 0){
			pageCount ++;
		}
		searchresult.setPageCount(pageCount);
		searchresult.setCurPage(page);
		return searchresult;
	}

}
