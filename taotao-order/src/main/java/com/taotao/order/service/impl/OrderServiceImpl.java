package com.taotao.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.dao.JedisClient;
import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderExample;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderItemExample;
import com.taotao.pojo.TbOrderItemExample.Criteria;
import com.taotao.pojo.TbOrderShipping;
/*
 * 订单管理Service
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ORDER_GEN_KEY}")
	private String ORDER_GEN_KEY;
	@Value("${ORDER_INIT_ID}")
	private String ORDER_INIT_ID;
	@Value("${ORDER_DETAIL_GEN_KEY}")
	private String ORDER_DETAIL_GEN_KEY;
	
	
	/*
	 * 创建订单
	 */
	@Override
	public TaotaoResult createOrder(TbOrder order, List<TbOrderItem> itemList, TbOrderShipping orderShipping) {
		//向订单表中插入记录
		//获得订单号
		String string = jedisClient.get(ORDER_GEN_KEY);
		if(StringUtils.isEmpty(string)){
			jedisClient.set(ORDER_GEN_KEY, ORDER_INIT_ID);
		}
		long orderId = jedisClient.incr(ORDER_GEN_KEY);
		//补全pojo的属性
		order.setOrderId(orderId + "");
		//'状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭',
		order.setStatus(1);
		order.setCreateTime(new Date());
		order.setUpdateTime(new Date());
		//0:未评价，1：已评价
		order.setBuyerRate(0);
		//向订单表插入数据
		orderMapper.insert(order);
		
		//插入订单明细
		for (TbOrderItem tbOrderItem : itemList) {
			//补全订单明细
			//取订单明细Id
			long orderDetailId = jedisClient.incr(ORDER_DETAIL_GEN_KEY);
			tbOrderItem.setId(orderDetailId+"");
			tbOrderItem.setOrderId(orderId + "");
			//向订单明细插入记录
			orderItemMapper.insert(tbOrderItem);
		}
		
		//插入物流表
		//补全物流表的属性
		orderShipping.setOrderId(orderId+"");
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		
		return TaotaoResult.ok(orderId);
	}


	/*
	 * 根据订单Id查询订单
	 */
	@Override
	public TaotaoResult getOrderById(String orderId) {
		//获取订单基本信息
		TbOrder tbOrder = orderMapper.selectByPrimaryKey(orderId);
		//封装订单基本信息
		Order order = new Order();
		order.setOrderId(orderId);
		order.setPayment(tbOrder.getPayment());
		order.setPaymentType(tbOrder.getPaymentType());
		order.setPostFee(tbOrder.getPostFee());
		order.setStatus(tbOrder.getStatus());
		order.setCreateTime(tbOrder.getCreateTime());
		order.setUpdateTime(tbOrder.getUpdateTime());
		order.setPaymentTime(tbOrder.getPaymentTime());
		order.setConsignTime(tbOrder.getConsignTime());
		order.setEndTime(tbOrder.getEndTime());
		order.setCloseTime(tbOrder.getCloseTime());
		order.setShippingName(tbOrder.getShippingName());
		order.setUserId(tbOrder.getUserId());
		order.setBuyerMessage(tbOrder.getBuyerMessage());
		order.setBuyerNick(tbOrder.getBuyerNick());
		order.setBuyerRate(tbOrder.getBuyerRate());
		
		
		//获取订单商品信息
		//设置查询条件
		TbOrderItemExample example = new TbOrderItemExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andOrderIdEqualTo(orderId);
		//执行查询
		List<TbOrderItem> list = orderItemMapper.selectByExample(example);
		//封装订单商品信息
		order.setOrderItems(list);
		
		//获取物流表信息
		TbOrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(orderId);
		//封装物流表信息
		order.setOrderShipping(orderShipping);
		
		return TaotaoResult.ok(order);
	}


	/*
	 * 根据用户ID分页查询订单
	 */
	@Override
	public TaotaoResult getOrderList(long userId,int pageIndex,int pageSize) {
		//设置查询条件
		TbOrderExample example = new TbOrderExample();
		com.taotao.pojo.TbOrderExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andUserIdEqualTo(userId);
		
		//分页处理
		PageHelper.startPage(pageIndex, pageSize);
		//执行查询
		List<TbOrder> list = orderMapper.selectByExample(example);
		//创建封装数据ListOrder
		List<Order> listOrder = new ArrayList<Order>(); 
		if(list !=null && list.size() > 0){
			for (TbOrder tbOrder : list) {
				//封装订单基本信息
				Order order = new Order();
				order.setOrderId(tbOrder.getOrderId());
				order.setPayment(tbOrder.getPayment());
				order.setPaymentType(tbOrder.getPaymentType());
				order.setPostFee(tbOrder.getPostFee());
				order.setStatus(tbOrder.getStatus());
				order.setCreateTime(tbOrder.getCreateTime());
				order.setUpdateTime(tbOrder.getUpdateTime());
				order.setPaymentTime(tbOrder.getPaymentTime());
				order.setConsignTime(tbOrder.getConsignTime());
				order.setEndTime(tbOrder.getEndTime());
				order.setCloseTime(tbOrder.getCloseTime());
				order.setShippingName(tbOrder.getShippingName());
				order.setUserId(tbOrder.getUserId());
				order.setBuyerMessage(tbOrder.getBuyerMessage());
				order.setBuyerNick(tbOrder.getBuyerNick());
				order.setBuyerRate(tbOrder.getBuyerRate());
				
				//根据订单ID查询订单商品信息
				//设置查询条件
				TbOrderItemExample example1 = new TbOrderItemExample();
				Criteria createCriteria2 = example1.createCriteria();
				createCriteria2.andOrderIdEqualTo(tbOrder.getOrderId());
				//执行查询
				List<TbOrderItem> list1 = orderItemMapper.selectByExample(example1);
				order.setOrderItems(list1);
				
				//根据订单ID查询订单物流信息
				TbOrderShipping orderShipping1 = orderShippingMapper.selectByPrimaryKey(tbOrder.getOrderId());
				order.setOrderShipping(orderShipping1);
				//添加listOrder
				listOrder.add(order);
			}
		}
		
		//创建一个返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(listOrder);
		//获取记录总数
		PageInfo<TbOrder> pageInfo = new PageInfo<TbOrder>(list);
		result.setTotal(pageInfo.getTotal());
		System.out.println("￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥￥总条数为：" + pageInfo.getTotal());
    
		return TaotaoResult.ok(result);
	}

}
