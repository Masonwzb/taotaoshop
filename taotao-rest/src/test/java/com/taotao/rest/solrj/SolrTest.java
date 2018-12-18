package com.taotao.rest.solrj;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrTest {

	//solr url
	private final static String BASE_URL = "http://192.168.0.17:8080/solr/";
	 
    /**
     * 获取SolrClient
     * solrj 6.5及以后版本获取方式
     * @return
     */
	public static HttpSolrClient getSolrClient(){
		return new HttpSolrClient.Builder(BASE_URL)
				.withConnectionTimeout(10000)
				.withSocketTimeout(60000)
				.build();
	}
  
	/*
	 * 添加修改商品
	 */
	@Test
	public void addDocument() throws Exception{
		
		//创建一个连接
		HttpSolrClient solrClient = getSolrClient();
		
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "test001");
		document.addField("item_title", "测试商品1");
		document.addField("item_price", 54321);
		//把文档对象写入索引库
		solrClient.add(document);
		//提交
		solrClient.commit();
	}
	
	/*
	 * 删除商品
	 */
	@Test
	public void deleteDocument() throws Exception{
		//创建一个连接
		HttpSolrClient solrClient = getSolrClient();
		//solrClient.deleteById("test001");
		solrClient.deleteByQuery("*:*");
		solrClient.commit();
	}
	
	/*
	 * 查询商品
	 */
	@Test
	public void queryDocument() throws Exception{
		//创建一个连接
		HttpSolrClient solrClient = getSolrClient();
		//创建一个查询对象
		SolrQuery query = new SolrQuery();
		//设置查询条件
		query.setQuery("*:*");
		query.setStart(20);
		query.setRows(50);
		//执行查询
		QueryResponse response = solrClient.query(query);
		//取查询结果
		SolrDocumentList results = response.getResults();
		System.out.println("共有条数："+results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
		}
	}
	
}
