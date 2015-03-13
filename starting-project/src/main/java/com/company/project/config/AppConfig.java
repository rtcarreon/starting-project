package com.company.project.config;

import com.company.project.service.TaskSchedulerService;
import com.company.project.service.dao.PersistentTokenDao;
import com.company.project.service.dao.UserDao;
import java.util.Locale;
import javax.servlet.MultipartConfigElement;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
//import org.springframework.boot.context.embedded.MultipartConfigFactory;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@EnableWebMvc       // equivalent to <mvc:annotation-driven />
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true) 
@ComponentScan(basePackages = { 
    "com.company.project", 
    "com.company.project.web", 
    "com.company.project.web.controller", 
    "com.company.project.web.controller.service",  
    "com.company.project.service",  
    "com.company.project.service.dao", 
    "com.company.project.config.core", 
    "com.company.project.config", 
    "com.company.project.data.elasticsearch.entities", 
    "com.company.project.data.elasticsearch.repositories", 
    "com.company.project.data.elasticsearch.service"
})
//@EnableAutoConfiguration
//@Import({ SecurityConfig.class })
//@Import({ AppWebSocketConfig.class })
@EnableAsync
@EnableScheduling
public class AppConfig extends WebMvcConfigurerAdapter {

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }
    
    @Bean(name = "localeResolver")
    public LocaleResolver sessionLocaleResolver(){
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("en"));
        
        //CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        //cookieLocaleResolver.setDefaultLocale(StringUtils.parseLocaleString("en"));

        return localeResolver;
    }
    
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("/WEB-INF/messages/messages");
        
        // if located in resources 
        //messageSource.setBasenames("classpath:messages/messages", "classpath:messages/validation");
        //String[] resources= {"classpath:labels","classpath:message"};
        //messageSource.setBasenames(resources);
        
        // if true, the key of the message will be displayed if the key is not
        // found, instead of throwing a NoSuchMessageException
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        // # -1 : never reload, 0 always reload
        messageSource.setCacheSeconds(0);
        
        return messageSource;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
    
//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setViewClass(JstlView.class);
//        viewResolver.setPrefix("/WEB-INF/pages/");
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        registry.addViewController("/register").setViewName("register");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        registry.addViewController("/home").setViewName("home");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        registry.addViewController("/processing").setViewName("processing");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        registry.addViewController("/403").setViewName("403");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        registry.addViewController("/protected").setViewName("protected");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
    
//    @Bean(name = "dataSource")
//    public DriverManagerDataSource dataSource() {
//        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/test");
//        driverManagerDataSource.setUsername("root");
//        driverManagerDataSource.setPassword("root");
//        return driverManagerDataSource;
//    }
	
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize("128KB");
//        factory.setMaxRequestSize("128KB");
//        return factory.createMultipartConfig();
//    }
    
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver createMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        //multipartResolver.setMaxUploadSize(20971520);   // 20MB
        multipartResolver.setMaxUploadSize(1048576000);   // 20MB
        multipartResolver.setMaxInMemorySize(1048576);  // 1MB
        return multipartResolver;
    }
    
    @Bean
    public TaskSchedulerService taskSchedulerService() {
        return new TaskSchedulerService();
    }
    
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
            pool.setCorePoolSize(5);
            pool.setMaxPoolSize(10);
            //pool.setQueueCapacity(10);
            pool.setWaitForTasksToCompleteOnShutdown(true);
            return pool;
    }
}