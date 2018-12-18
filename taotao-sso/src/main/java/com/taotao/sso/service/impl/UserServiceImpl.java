package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.UserService;
/*
 * 用户管理Service
 */
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private TbUserMapper userMapper;
	
	//redis缓存
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;
	
	
	@Override
	public TaotaoResult checkData(String param, Integer type) {
		//创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria createCriteria = example.createCriteria();
		
		//对数据进行校验，参数1、2、3分别代表username、phone、email
		//用户名校验
		if(1 == type){
			createCriteria.andUsernameEqualTo(param);
		//电话校验
		}else if(2 == type){
			createCriteria.andPhoneEqualTo(param);
		}else{
			createCriteria.andEmailEqualTo(param);
		}
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size() == 0){
			return TaotaoResult.ok(true);
		}else{
			return TaotaoResult.ok(false);
		}
	}

	
	/*
	 * 注册用户(non-Javadoc)
	 * @see com.taotao.sso.service.UserService#createUser(com.taotao.pojo.TbUser)
	 */
	@Override
	public TaotaoResult createUser(TbUser user) {
		// 注册补全
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//用户密码进行md5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	
	/*
	 * 用户登录
	 */
	@Override
	public TaotaoResult userLogin(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		// 创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		//如果没有此用户名
		if(null == list || list.size() == 0){
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		//比对密码
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		//生成唯一标识符token
		String token = UUID.randomUUID().toString();
		//保存用户在redis缓存中之前，把用户对象中的密码清空
		user.setPassword(null);
		//把用户信息写入redis缓存
		jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(user));
		//设置session的过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
	
		//添加写cookie的逻辑,cookie的有效期关闭浏览器就失效
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);
		
		//返回token
		return TaotaoResult.ok(token);
	}

	
	
	/*
	 * 根据token查询用户信息(non-Javadoc)
	 * @see com.taotao.sso.service.UserService#getUserByToken(java.lang.String)
	 */
	@Override
	public TaotaoResult getUserByToken(String token) {
		//根据token从redis查询用户信息
		String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
		//判断是否为空
		if(StringUtils.isEmpty(json)){
			return TaotaoResult.build(400, "session已经过期，请重新登录");
		}
		//更新过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		
		//返回用户信息
		return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}


	/*
	 * 退出用户
	 */
	@Override
	public TaotaoResult logoutUser(String token) {
		//删除redis中对应的用户记录
		jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token);
		return TaotaoResult.ok();
	}


	
}
