package com.company.project.config.core;

import javax.servlet.ServletContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

//@Order(value = 1)
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    
//    @Override
//    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
//            insertFilters(servletContext,
//               new HiddenHttpMethodFilter(),
//               new MultipartFilter());
//    }
    
//    @Override
//    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
//        insertFilters(servletContext, new MultipartFilter());
//    }
}