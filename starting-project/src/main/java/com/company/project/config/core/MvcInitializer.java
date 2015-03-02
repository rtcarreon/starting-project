package com.company.project.config.core;

import com.company.project.config.AppConfig;
import com.company.project.config.AppWebSocketConfig;
import com.company.project.config.DataConfig;
import com.company.project.config.SecurityConfig;
import com.company.project.config.ViewResolver;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

//@Order(value = 2)
public class MvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("dispatchOptionsRequest", "true");
        registration.setAsyncSupported(true);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
//        FilterRegistration.Dynamic multipartFilter = servletContext.addFilter("multipartFilter", new MultipartFilter());
//        multipartFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
        
//        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
//        rootContext.register(SecurityConfig.class, DataConfig.class, AppConfig.class, ViewResolver.class);
//        
//        servletContext.addListener(new ContextLoaderListener(rootContext));
//        
//        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
//        dispatcherContext.register(SecurityConfig.class, DataConfig.class, AppConfig.class, ViewResolver.class);
//               
//        DispatcherServlet dispatcherServlet = new DispatcherServlet(dispatcherContext);
//        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
//        dispatcher.setLoadOnStartup(1);
//        dispatcher.addMapping("/*");
//
//        dispatcher.setMultipartConfig(new MultipartConfigElement("/tmp", 1024*1024*5, 1024*1024*5*5, 1024*1024));
        
        super.onStartup(servletContext);
        
//        ServletRegistration.Dynamic dispatcher = (ServletRegistration.Dynamic) servletContext.getServlet(DEFAULT_SERVLET_NAME);
//        dispatcher.setLoadOnStartup(1);
//        dispatcher.addMapping("/*");
//        dispatcher.setMultipartConfig(new MultipartConfigElement("/tmp", 1024*1024*5, 1024*1024*5*5, 1024*1024));
        
//        FilterRegistration.Dynamic multipartFilter = servletContext.addFilter("multipartFilter", new MultipartFilter());
//        multipartFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
    }
    
    // explanation : http://stackoverflow.com/questions/21526132/could-not-autowire-field-private-org-springframework-security-core-userdetails
    @Override
    protected Class<?>[] getRootConfigClasses() {
        //return new Class[] { SecurityConfig.class};
        return new Class[] { SecurityConfig.class, DataConfig.class, AppConfig.class, ViewResolver.class, AppWebSocketConfig.class };
        //return new Class[] { SecurityConfig.class, DataConfig.class, ViewResolver.class };
        //return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        //return new Class[] { DataConfig.class, AppConfig.class, ViewResolver.class };
        return null;
        //return new Class<?>[] { AppConfig.class };
        //return new Class<?>[] { AppConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/", "/ws/*" };
    }
    
    @Override
    protected Filter[] getServletFilters() {

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");

        return new Filter[] { characterEncodingFilter};
    }
}