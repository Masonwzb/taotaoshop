package com.taotao.rest.redis;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	
	@Test
	public void testJedisSingle(){
		//创建一个jedis的对象
		Jedis jedis = new Jedis("192.168.136.128",6379);
		//直接调用jedis对象的方法，方法名称和redis的命令一致
		jedis.set("key1", "jedis test");
		String string = jedis.get("key1");
		System.out.println(string);
		//关闭jedis
		jedis.close();
	}
	
	/*
	 * 使用连接池
	 */
	@Test
	public void testJedisPool(){
		//创建Jedis连接池
		JedisPool pool = new JedisPool("192.168.136.128",6379);
		//从连接池中获得Jedis对象
		Jedis jedis = pool.getResource();
		String string = jedis.get("key1");
		System.out.println(string);
		jedis.close();
		pool.close();
	}
	
	/*
	 * 连接集群
	 */
	@Test
	public void testJedisCluster() throws Exception{
		HashSet<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("192.168.0.19",6001));
		nodes.add(new HostAndPort("192.168.0.19",6002));
		nodes.add(new HostAndPort("192.168.0.19",6003));
		nodes.add(new HostAndPort("192.168.0.19",6004));
		nodes.add(new HostAndPort("192.168.0.19",6005));
		nodes.add(new HostAndPort("192.168.0.19",6006));
		JedisCluster cluster = new JedisCluster(nodes);
		cluster.set("key1","1000");
		String string = cluster.get("key1");
		System.out.println(string);
		
		cluster.close();
	}
	
	/*
	 * redis单机版
	 */
	@Test
	public void textSpringJedisSingle(){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JedisPool pool = (JedisPool) applicationContext.getBean("redisClient");
		Jedis jedis = pool.getResource();
		jedis.set("key1", "jedis test 2018/8/12 21.05 linux");
		String string = jedis.get("key1");
		System.out.println(string);
		pool.close();
		jedis.close();
	}
	
	/*
	 * redis集群版
	 */
	@Test
	public void testSpringJedisCluster() throws Exception{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JedisCluster jedisCluster = (JedisCluster) applicationContext.getBean("redisClient");
		String string = jedisCluster.get("key1");
		System.out.println(string);
		jedisCluster.close();
	}
	
}
