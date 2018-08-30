package com.shiro.shirodemo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.shiro.mgt.SecurityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroJdbcApplicationTest {
	private static final transient Logger log = LoggerFactory.getLogger(ShiroJdbcApplicationTest.class);

	@Test
	public void contextLoads() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-mysql.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("ROOT", "111111");
		token.setRememberMe(true);// 是否记住用户
		try {
			currentUser.login(token);
			// 当我们获登录用户之后
			log.info("用户 [" + currentUser.getPrincipal() + "] 登陆成功");
			// 查看用户是否有角色
			if (currentUser.hasRole("admin")) {
				log.info("您有admin角色");
			} else {
				log.info("您没有admin角色");
			}
			if (currentUser.hasRole("test")) {
				log.info("您有test角色");
			} else {
				log.info("您没有test角色");
			}
			// 查看用户是否有某个权限
			if (currentUser.isPermitted("create")) {
				log.info("您有create权限");
			} else {
				log.info("您没有create权限");
			}
			if (currentUser.isPermitted("update")) {
				log.info("您有update权限");
			} else {
				log.info("您没有update权限");
			}
			// 登出
			currentUser.logout();
		} catch (UnknownAccountException uae) {
			log.info(token.getPrincipal() + "账户不存在");
		} catch (IncorrectCredentialsException ice) {
			log.info(token.getPrincipal() + "密码不正确");
		} catch (LockedAccountException lae) {
			log.info(token.getPrincipal() + "用户被锁定了 ");
		} catch (AuthenticationException ae) {
			// 无法判断是什么错了
			log.info(ae.getMessage());
		}
	}
}
