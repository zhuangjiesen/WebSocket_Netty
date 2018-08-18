package com.jason.core.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/27
 */
public class UserRealm  extends AuthorizingRealm {


    private final Logger log = LoggerFactory.getLogger(UserRealm.class);

    /**
     * 为当前登录的Subject授予角色和权限
     * @see  经测试:本例中该方法的调用时机为需授权资源被访问时
     * @see  经测试:并且每次访问需授权资源时都会执行该方法中的逻辑,这表明本例中默认并未启用AuthorizationCache
     * @see  个人感觉若使用了Spring3.1开始提供的ConcurrentMapCache支持,则可灵活决定是否启用AuthorizationCache
     * @see  比如说这里从数据库获取权限信息时,先去访问Spring3.1提供的缓存,而不使用Shior提供的AuthorizationCache
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        log.debug("-------角色验证---------------");



        return null;
    }

    /**
     * 验证当前登录的Subject
     * @see  经测试:本例中该方法的调用时机为LoginController.login()方法中执行Subject.login()时
     @author : 234778286@qq.com 庄杰森 2015年11月21日
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {
        log.debug("==登陆验证！==");
        AuthenticationInfo authenticationInfo = null;
        if(!(authcToken instanceof UsernamePasswordCaptchaToken)){
            throw new AuthenticationException("账号验证错误!");
        }
        UsernamePasswordCaptchaToken userToken = (UsernamePasswordCaptchaToken)authcToken;
        //获取基于用户名和密码的令牌
        //实际上这个authcToken是从LoginController里面currentUser.login(token)传过来的
        //两个token的引用都是一样的
        //此处无需比对,比对的逻辑Shiro会做,我们只需返回一个和令牌相关的正确的验证信息
        //说白了就是第一个参数填登录用户名,第二个参数填合法的登录密码(可以是从数据库中取到的,本例中为了演示就硬编码了)
        //这样一来,在随后的登录页面上就只有这里指定的用户和密码才能通过验证
        User user = new User();
        user.setName(userToken.getUsername());
        user.setPassword(new String(userToken.getPassword()));



        if(user != null){
            authenticationInfo=new SimpleAuthenticationInfo(user.getName(),user.getPassword(), getName());
        }else{
            throw new AuthenticationException("账号或者密码错误");
        }
        //没有返回登录用户名对应的SimpleAuthenticationInfo对象时,就会在LoginController中抛出UnknownAccountException异常
        return authenticationInfo;
    }

    @Override
    protected void checkPermission(Permission arg0, AuthorizationInfo arg1) {
        // TODO Auto-generated method stub
        log.debug("-------checkPermission------------");


        super.checkPermission(arg0, arg1);
    }

    @Override
    protected void checkRole(String arg0, AuthorizationInfo arg1) {
        // TODO Auto-generated method stub
        log.debug("-------checkRole------------");

        super.checkRole(arg0, arg1);
    }

    @Override
    protected void checkRoles(Collection<String> arg0, AuthorizationInfo arg1) {
        // TODO Auto-generated method stub

        log.debug("-------checkRoles------------");


        super.checkRoles(arg0, arg1);
    }

    @Override
    protected boolean[] isPermitted(List<Permission> arg0,
                                    AuthorizationInfo arg1) {
        // TODO Auto-generated method stub

        log.debug("-------isPermitted------------");


        return super.isPermitted(arg0, arg1);
    }



}
