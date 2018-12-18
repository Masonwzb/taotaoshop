package com.taotao.portal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.impl.UserServiceImpl;
/*
 * 拦截器，强制用户登录
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private UserServiceImpl userService;
	
	/*
	 * 在Handler执行之前处理
	 * 返回值决定handler是否执行，true:执行，false:不执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	    //判断用户是否登录
	    //从cookie中取token
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		//根据token换取用户信息，调用sso系统的借口
		TbUser user = userService.getUserByToken(token);
		System.out.println(user);
		//取不到用户信息
		if(null == user){
			//跳转到登录页面，把用户请求的url作为参数传递登录页面
			response.sendRedirect(userService.SSO_BASE_URL + userService.SSO_PRGE_LOGIN 
							+ "?redirectUrl=" + request.getRequestURL());
			//返回false
			return false;
		}
		
		//把用户信息放入Request
		request.setAttribute("user", user);
		
		//取到用户信息，放行
		return true;
	}
	
	
	/*
	 * handler执行之后，返回ModelAndView之前 
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	
	/*
	 *  返回ModelAndView之后
	 *  响应用户之后
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	
	}
	
}
