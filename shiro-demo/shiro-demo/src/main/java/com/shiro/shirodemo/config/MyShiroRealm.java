package com.shiro.shirodemo.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.shiro.shirodemo.domain.Permission;
import com.shiro.shirodemo.domain.Role;
import com.shiro.shirodemo.domain.User;
import com.shiro.shirodemo.service.ILoginService;

/**
 * 在认证、授权内部实现机制中都有提到，最终处理都将交给Real进行处理。因为在Shiro中，最终是通过Realm来获取应用程序中的用户、角色及权限信息的。通常情况下，在Realm中会直接从我们的数据源中获取Shiro需要的验证信息。可以说，Realm是专用于安全框架的DAO.
 * Shiro的认证过程最终会交由Realm执行，这时会调用Realm的getAuthenticationInfo(token)方法。
 * 该方法主要执行以下操作:
 *
 * 检查提交的进行认证的令牌信息
 * 根据令牌信息从数据源(通常为数据库)中获取用户信息
 * 对用户信息进行匹配验证。
 * 验证通过将返回一个封装了用户信息的AuthenticationInfo实例。
 * 验证失败则抛出AuthenticationException异常信息。而在我们的应用程序中要做的就是自定义一个Realm类，继承AuthorizingRealm抽象类，重载doGetAuthenticationInfo()，重写获取用户信息的方法。
 */
public class MyShiroRealm extends AuthorizingRealm {

	// 用于用户查询
	@Autowired
	private ILoginService loginService;
	private static final transient Logger log = LoggerFactory.getLogger(MyShiroRealm.class);

	// 角色权限和对应权限添加
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		// 获取登录用户名
		String name = (String) principalCollection.getPrimaryPrincipal();
		// 查询用户名称
		User user = loginService.findByName(name);
		log.info("user=" + JSONObject.toJSONString(user));
		// 添加角色和权限
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		for (Role role : user.getRoles()) {
			// 添加角色
			simpleAuthorizationInfo.addRole(role.getRoleName());
			for (Permission permission : role.getPermissions()) {
				// 添加权限
				simpleAuthorizationInfo.addStringPermission(permission.getPermission());
			}
		}
		return simpleAuthorizationInfo;
	}

	// 用户认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		// 加这一步的目的是在Post请求的时候会先进认证，然后在到请求
		if (authenticationToken.getPrincipal() == null) {
			return null;
		}
		// 获取用户信息
		String name = authenticationToken.getPrincipal().toString();
		User user = loginService.findByName(name);
		if (user == null) {
			// 这里返回后会报出对应异常
			return null;
		} else {
			//byte[] salt = Encodes.decodeHex(user.getSalt());
			// 这里验证authenticationToken和simpleAuthenticationInfo的信息
			SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name,
					user.getPassword().toString(), getName());
			//设置用户session
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute("user", user);
			return simpleAuthenticationInfo;
		}
	}
}
