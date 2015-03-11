package com.company.project.config;

import com.company.project.persistence.UserMapper;
import com.company.project.service.IUserService;
import com.company.project.service.UserService;
import com.company.project.service.UserMapperImpl;
import com.company.project.service.dao.PersistentTokenDao;
import com.company.project.service.dao.UserDao;
import com.company.project.web.controller.service.CustomAuthenticationProvider;
import com.company.project.web.controller.service.CustomAuthenticationSuccessHandler;
import com.company.project.web.controller.service.CustomPersistentTokenBasedRememberMeServices;
import com.company.project.web.controller.service.CustomRememberMeUserDetailsService;
import com.company.project.web.controller.service.CustomTokenRepository;
import com.company.project.web.controller.service.CustomUserDetailsService;
import javax.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//import javax.sql.DataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true) 
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)           // add to method @Secured("IS_AUTHENTICATED_ANONYMOUSLY"), @Secured("ROLE_ADMIN"), @PreAuthorize("isAnonymous()"), @PreAuthorize("hasAuthority('ROLE_USER')") ref: https://github.com/spring-projects/spring-security/blob/master/docs/manual/src/asciidoc/index.adoc
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //@Resource(name="customAuthenticationProvider")
    //private CustomAuthenticationProvider customAuthenticationProvider;

//    @Resource(name="customAuthenticationSuccessHandler")
//    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
//    @Autowired
//    DataSource dataSource;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/resources/**", "/css/**", "/img/**", "/js/**", "/fonts/**", "/less/**", "/pages/**");
        
    }

//    @Autowired
//    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
//            auth.jdbcAuthentication().dataSource(dataSource)
//                    .usersByUsernameQuery("select username,password, enabled from users where username=?")
//                    .authoritiesByUsernameQuery("select username, role as authority from user_roles where username=?");
//                    //.passwordEncoder(encoder());      //TODO investigate not working
//    }
    
    @Bean 
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
         return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authMgrBldr) throws Exception {
        authMgrBldr.userDetailsService(userDetailService()).passwordEncoder(encoder());
        //authMgrBldr.authenticationProvider(customAuthenticationProvider);
        /*authMgrBldr
            .inMemoryAuthentication()
                .withUser("sadmin")
                .password("sadmin")
                .roles("SYS_ADMIN");
        authMgrBldr
            .inMemoryAuthentication()
                .withUser("admin")
                .password("admin")
                .roles("ADMIN");
        authMgrBldr
            .inMemoryAuthentication().
                withUser("dba")
                .password("dba")
                .roles("DBA", "ADMIN");
        authMgrBldr
            .inMemoryAuthentication().
                withUser("user")
                .password("user")
                .roles("USER");*/
    }
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .requestCache().requestCache(requestCache()).and()
            //.sessionManagement()
            //        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            //        .and()    
            .authorizeRequests()
            //.anyRequest().authenticated()         // every request requires the user to be authenticated
                //.and()
            
            .antMatchers("/resources/**", "/css/**", "/img/**", "/js/**", "/fonts/**", "/less/**", "/pages/**").permitAll()
            .antMatchers(
                    "/login",
                    "/processLogin",
                    "/resources/**",
                    "/register").permitAll()
            .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")                           
            .antMatchers("/dba/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DBA')")
            .antMatchers("/confidential/**").access("hasRole('SYS_ADMIN')")
            .antMatchers("/protected**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
            .antMatchers("/welcome**").authenticated()
            .antMatchers("/singleSave**").authenticated()
            .antMatchers("/doUpload").permitAll()
            .anyRequest().authenticated()
                .and()
            .formLogin()
                    .loginPage("/login").permitAll()
                    //.defaultSuccessUrl("/hello")
                    .failureUrl("/login?error")
                    //.loginProcessingUrl("/login?processLogin")   // when added login not working. TODO investigate
                    .defaultSuccessUrl("/")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(customAuthenticationSuccessHandler())
                    .permitAll()
                .and()
            .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                .and()
            .rememberMe() 
                    .key("your_key")
                    .rememberMeServices(rememberMeServices())
                    //.tokenRepository(rememberMeTokenRepository())
                    //.tokenValiditySeconds(1209600)
                .and()
            
            //.exceptionHandling()
            //        .authenticationEntryPoint(authenticationEntryPoint)
            //        .accessDeniedHandler(accessDeniedHandler)
            //        .and();   
                
            .exceptionHandling()
                .accessDeniedPage("/403")
                //.accessDeniedPage("/login")
            //.and()
            //.csrf() 
            .and()
            .httpBasic();                   //HTTP Basic Authentication is supported
        
    }
    
    @Bean
    public RequestCache requestCache() {
        return new HttpSessionRequestCache();
    }
    
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public RememberMeServices rememberMeServices() {
        UserDetailsService rememberMeUserDetailsService = customRememberMeUserDetailsService();
        //InMemoryTokenRepositoryImpl rememberMeTokenRepository = new InMemoryTokenRepositoryImpl();
        PersistentTokenRepository rememberMeTokenRepository = rememberMeTokenRepository();
        PersistentTokenBasedRememberMeServices services = new CustomPersistentTokenBasedRememberMeServices("your_key", rememberMeUserDetailsService, rememberMeTokenRepository);
        services.setCookieName("remember_me_cookie");
        services.setParameter("remember_me_checkbox");
        services.setTokenValiditySeconds(2678400); // 1month
        //services.setAlwaysRemember(true);
        //services.setUseSecureCookie(true);  // If you only want to use remember-me over HTTPS
        return services;
    }
    
    @Bean
    public CustomUserDetailsService userDetailService() {
        return new CustomUserDetailsService();
    }
    
    @Bean
    public CustomRememberMeUserDetailsService customRememberMeUserDetailsService() {
        return new CustomRememberMeUserDetailsService();
    }
    
    @Bean
    public PersistentTokenRepository rememberMeTokenRepository() {
        return new CustomTokenRepository();
    }
    
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
    
    //@Bean(name="persistentTokenDao")
    @Bean
    public PersistentTokenDao persistentTokenDao() {
        return new PersistentTokenDao();
    }
	
    //@Bean(name="userDao")
    @Bean
    public UserDao userDao() {
        return new UserDao();
    }
    
    //@Bean(name="userService")
    @Bean
    public IUserService userService() {
        return new UserService();
    }
    
    //@Bean(name="userMapperImpl")
    @Bean             // do not use bean name same as mapper file name
    public UserMapper userMapperImpl() {
        return new UserMapperImpl();
    }
    

}