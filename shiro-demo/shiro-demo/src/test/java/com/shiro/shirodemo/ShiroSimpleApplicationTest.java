package com.shiro.shirodemo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroSimpleApplicationTest {

	IniRealm iniRealm=new IniRealm();
	@Before
	public void loadbefore() {
		iniRealm.addAccount("admin", "123456", "user");
	}
	@Test
	public void contextLoads() {
		DefaultSecurityManager securityManager=new DefaultSecurityManager();
		securityManager.setRealm(iniRealm);
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject(); // 获取Subject单例对象
		UsernamePasswordToken token = new UsernamePasswordToken("admin", "123456");
		subject.login(token);
		System.out.println("subject.isAuthenticated()="+subject.isAuthenticated());
		System.out.println("subject.hasRole()="+subject.hasRole("user"));
	}

}
