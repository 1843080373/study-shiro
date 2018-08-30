package com.shiro.shirodemo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

@Configuration	
public class ShiroConfiguration {
	// thymeleaf模板引擎和shiro整合时使用
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	/**
	 * LifecycleBeanPostProcessor，这是个DestructionAwareBeanPostProcessor的子类，
	 * 负责org.apache.shiro.util.Initializable类型bean的生命周期的，初始化和销毁。
	 * 主要是AuthorizingRealm类的子类，以及EhCacheManager类。
	 */
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * HashedCredentialsMatcher，这个类是为了对密码进行编码的， 防止密码在数据库里明码保存，当然在登陆认证的时候，
	 * 这个类也负责对form里输入的密码进行编码。
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
		credentialsMatcher.setHashAlgorithmName("MD5");
		credentialsMatcher.setHashIterations(2);
		credentialsMatcher.setStoredCredentialsHexEncoded(true);
		return credentialsMatcher;
	}

	// 授权缓存管理器
	@Bean
	public EhCacheManager getEhCacheManager() {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
		return em;
	}

	// 将自己的验证方式加入容器
	@Bean
	public MyShiroRealm myShiroRealm() {
		MyShiroRealm myShiroRealm = new MyShiroRealm();
		return myShiroRealm;
	}

	// 权限管理，配置主要是Realm的管理认证
	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setCacheManager(getEhCacheManager()); // 缓存管理器
		// 注入记住我管理器;
		securityManager.setRememberMeManager(rememberMeManager());
		// 自定义session管理 可使用redis
		securityManager.setSessionManager(sessionManager());
		securityManager.setRealm(myShiroRealm());
		return securityManager;
	}

	// 配置自定义的密码比较器
	@Bean(name = "credentialsMatcher")
	public CredentialsMatcher credentialsMatcher() {
		return new CredentialsMatcher();
	}
	
	/**
     * shiro的密码比较器
     * @return
     */
//  @Bean
//  public HashedCredentialsMatcher hashedCredentialsMatcher() {
//      HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
//      hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
//      hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
//      return hashedCredentialsMatcher;
//  }

	
	/** //必须
     * cookie对象;会话Cookie模板 ,默认为: JSESSIONID 问题: 与SERVLET容器名冲突,重新定义为sid或rememberMe，自定义
     * @return
     */
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        cookie.setHttpOnly(true);
        cookie.setMaxAge(86400);
        return cookie;
    }

    /** //必须
     * cookie管理对象;记住我功能,rememberMe管理器
     * @return
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位) //3AvVhmFLUs0KTA3Kprsdag==
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

	// Filter工厂，设置对应的过滤条件和跳转条件
	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		Map<String, String> filterChainDefinitionMap = new HashMap<String, String>();
		// 登出
		filterChainDefinitionMap.put("/logout", "logout");
		// 对所有用户认证
		filterChainDefinitionMap.put("/**", "authc");
		// 登录
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 首页
		shiroFilterFactoryBean.setSuccessUrl("/index");
		// 错误页面，认证不通过跳转
		shiroFilterFactoryBean.setUnauthorizedUrl("/error");
		// 静态资源允许访问//登录页允许访问,一个URL可以配置多个Filter,使用逗号分隔,当设置多个过滤器时，全部验证通过，才视为通过,部分过滤器可指定参数，如perms，roles
//      filterChainDefinitionMap.put("/login.html*", "anon"); //表示可以匿名访问，*表示参数如?error等
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/unauthorized", "anon");
		filterChainDefinitionMap.put("/css/**", "anon");
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/fonts/**", "anon");
		filterChainDefinitionMap.put("/img/**", "anon");
		filterChainDefinitionMap.put("/druid/**", "anon");
		filterChainDefinitionMap.put("/user/regist", "anon");
		filterChainDefinitionMap.put("/gifCode", "anon");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * DefaultAdvisorAutoProxyCreator，Spring的一个bean，由Advisor决定对哪些类的方法进行AOP代理。
	 */
	@Bean
	@ConditionalOnMissingBean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
		defaultAAP.setProxyTargetClass(true);
		return defaultAAP;
	}

	/**
	 * AuthorizationAttributeSourceAdvisor，shiro里实现的Advisor类，
	 * 内部使用AopAllianceAnnotationsAuthorizingMethodInterceptor来拦截用以下注解的方法。
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor aASA = new AuthorizationAttributeSourceAdvisor();
		aASA.setSecurityManager(securityManager());
		return aASA;
	}
	//SessionManager和SessionDAO可以不配置，会话DAO
    @Bean
    public SessionDAO sessionDAO() {
        MemorySessionDAO sessionDAO = new MemorySessionDAO();
        return sessionDAO;
    }

////会话管理器，设定会话超时及保存
   @Bean
   public SessionManager sessionManager() {
       DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
       Collection<SessionListener> listeners = new ArrayList<SessionListener>();
       listeners.add(new ShiroSessionListener());
       sessionManager.setSessionListeners(listeners);
       sessionManager.setGlobalSessionTimeout(1800000); //全局会话超时时间（单位毫秒），默认30分钟
       sessionManager.setSessionDAO(sessionDAO());
       sessionManager.setDeleteInvalidSessions(true);
       sessionManager.setSessionValidationSchedulerEnabled(true);
       //定时清理失效会话, 清理用户直接关闭浏览器造成的孤立会话
       sessionManager.setSessionValidationInterval(1800000);
//     sessionManager.setSessionValidationScheduler(executorServiceSessionValidationScheduler());
       sessionManager.setSessionIdCookieEnabled(true);
       sessionManager.setSessionIdCookie(rememberMeCookie());
       return sessionManager;
   }
   //会话验证调度器，每30分钟执行一次验证
   @Bean(name="sessionValidationScheduler")
   public ExecutorServiceSessionValidationScheduler executorServiceSessionValidationScheduler() {
       ExecutorServiceSessionValidationScheduler sessionValidationScheduler=new ExecutorServiceSessionValidationScheduler();
       sessionValidationScheduler.setInterval(1800000);
       sessionValidationScheduler.setSessionManager((ValidatingSessionManager)sessionManager());
       return sessionValidationScheduler;
   }
}
