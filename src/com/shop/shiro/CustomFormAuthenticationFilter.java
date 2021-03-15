package com.shop.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	/**
	 * 添加验证码组件
	 * @param request 获取浏览器的验证码
	 * @param response 输出验证码
	 * @return
	 * @throws Exception
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		//用户输入的验证码
		String inputcode = req.getParameter("code");
		System.out.println("inputcode = " + inputcode);
		//后台生成的验证码
		String getcode = (String) session.getAttribute("getcode");
		System.out.println("getcode = " + getcode);
		System.out.println("进入验证码验证");
		if (inputcode != null && getcode != null && !inputcode.equals(getcode)) {
			//验证码错误
			request.setAttribute("shiroLoginFailure", "randomCodeError");
			//不允许登录
			return true;

		}

		return super.onAccessDenied(request, response);
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		WebUtils.getAndClearSavedRequest(request);
		WebUtils.redirectToSavedRequest(request, response, "/main");
		return false;
	}
	
}
