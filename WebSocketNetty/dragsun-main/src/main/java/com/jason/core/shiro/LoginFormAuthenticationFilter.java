package com.jason.core.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class LoginFormAuthenticationFilter extends FormAuthenticationFilter {
	private static final Logger log = LoggerFactory.getLogger(LoginFormAuthenticationFilter.class);
	
	
	/**  验证码参数名 */
	public static final String CAPTCHA_PARAM_NAME="captcha";
	/** 用户名*/
	public static final String USERTYPE_PARAM_NAME="userType";
	
	protected String getCaptcha(ServletRequest request) {
		return WebUtils.getCleanParam(request, CAPTCHA_PARAM_NAME);
	}
	
	protected String getUserType(ServletRequest request) {
		return WebUtils.getCleanParam(request, USERTYPE_PARAM_NAME);
	}
	/* 创建登陆认证token传值传递给UserRealm的shiro认证
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#createToken(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected AuthenticationToken createToken(ServletRequest request,
                                              ServletResponse response) {
		String username = getUsername(request);
		String password = getPassword(request);
		String captcha = getCaptcha(request);//获取验证码
		String userType=getUserType(request);
		
		boolean rememberMe = isRememberMe(request);
		String host = getHost(request);
		
		return new UsernamePasswordCaptchaToken(username, password.toCharArray(), rememberMe, host, userType, captcha);
	}

	/* 执行登陆
	 * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#executeLogin(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response)
			throws Exception {
		log.debug("---------查询登陆验证------------");
		return super.executeLogin(request, response);
	}
	
	
	

	/* 设置失败属性（登陆失败）
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#setFailureAttribute(javax.servlet.ServletRequest, org.apache.shiro.authc.AuthenticationException)
	 */
	@Override
	protected void setFailureAttribute(ServletRequest request,
			AuthenticationException ae) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		log.debug("---登陆失败！------"+ae.getMessage());
		request.setAttribute("authenticationExceptionMsg", ae.getMessage());
		super.setFailureAttribute(request, ae);
	}

	/* 链接被否定
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		log.debug("------登陆拦截！---------");
		
		
		return super.onAccessDenied(request, response);
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token,
                                     Subject subject, ServletRequest request, ServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		log.debug("-----成功登陆!!!---------");
		UsernamePasswordCaptchaToken mToken=(UsernamePasswordCaptchaToken)token;
		subject.getSession().setAttribute("username", mToken.getUsername());
		return super.onLoginSuccess(token, subject, request, response);
	}

	
	
	
}
