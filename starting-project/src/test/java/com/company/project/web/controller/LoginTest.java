/*
 * Copyright 2014 Romer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.company.project.web.controller;

import org.hamcrest.core.Is;
import org.junit.Assert;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Romer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class LoginTest {
    @Configuration
    @ComponentScan(basePackages = "com.company.project")
    public static class Config {}

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext wac;
    
    @Autowired
    protected MockHttpSession session;
    
    private MockMvc mockMvc;
    
    @Before
    public final void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void testAdminLoginSuccess() throws Exception {
        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/login").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "admin")
                    .param("password", "admin")
                    .param("remember_me_checkbox", "0")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andReturn()
                .getRequest()
                .getSession();
        
        assertNotNull(session);
        
        testAuthenticatedAsAdminNavigateToPages();
        
        //assertNotNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
        //assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        
        //mockMvc.perform(SecurityMockMvcRequestBuilders.logout());
        //mockMvc.perform(SecurityMockMvcRequestBuilders.logout("/logout"));
    }
    
    @Test
    public void testUserLoginSuccess() throws Exception {    
        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/login").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "user")
                    .param("password", "user")
                    .param("remember_me_checkbox", "0")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"))
                .andReturn()
                .getRequest()
                .getSession();
        
        assertNotNull(session);
        
        testAuthenticatedAsUserNavigateToPages();
    }
    
    @Test
    public void testLoginFail() throws Exception {
        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/login").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "admins")
                    .param("password", "admin")
                    .param("remember_me_checkbox", "0")
                )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"))
            .andReturn()
            .getRequest()
            .getSession();
        
        assertNotNull(session);
        
        testNotAuthenticatedNavigateToPages();
    }
    
    @Test
    public void testLoginFailInvalidCsrf() throws Exception {
        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/login").with(csrf().useInvalidToken())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "admin")
                    .param("password", "admin")
                    .param("remember_me_checkbox", "0")
                )
            .andExpect(status().is4xxClientError())     // 403
            .andReturn()
            .getRequest()
            .getSession();
        
        assertNotNull(session);
        
        testNotAuthenticatedNavigateToPages();
    }
    
    @Test
    public void testAdminLogout() throws Exception {
        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/login").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "admin")
                    .param("password", "admin")
                    .param("remember_me_checkbox", "0")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andReturn()
                .getRequest()
                .getSession();
        
        assertNotNull(session);
        testAuthenticatedAsAdminNavigateToPages();

        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/logout").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andReturn()
                .getRequest()
                .getSession();
        
        assertNotNull(session);
        
        testNotAuthenticatedNavigateToPages();
    }
    
    @Test
    public void testUserLogout() throws Exception {
        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/login").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username", "user")
                    .param("password", "user")
                    .param("remember_me_checkbox", "0")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"))
                .andReturn()
                .getRequest()
                .getSession();
        
        assertNotNull(session);
        testAuthenticatedAsUserNavigateToPages();

        session = (MockHttpSession)
            mockMvc
                .perform(
                    post("/logout").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andReturn()
                .getRequest()
                .getSession();
        
        assertNotNull(session);
        
        testNotAuthenticatedNavigateToPages();
    }
    
    private void testNotAuthenticatedNavigateToPages() throws Exception {
        // test navigate to welcome page, user should be authenticated
        mockMvc
            .perform(
                get("/welcome").session(session)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
        
        // test navigate to protected page, user should be authenticated
        mockMvc
            .perform(
                get("/protected").session(session)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
        
        // test navigate to admin only page
        mockMvc
            .perform(
                get("/admin").session(session)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
        
        // test navigate to signup page, no authentication required
        mockMvc
            .perform(
                get("/signup").session(session)
            )
            .andExpect(status().isOk());
        
        // test navigate to login page, no authentication required
        mockMvc
            .perform(
                get("/login").session(session)
            )
            .andExpect(status().isOk());
    }
    
    private void testAuthenticatedAsAdminNavigateToPages() throws Exception {
        // test navigate to protected page, ROLE_USER or ROLE_ADMIN
        mockMvc
            .perform(
                get("/protected").session(session)
            )
            .andExpect(status().isOk());
        
        // test navigate to welcome page, user should be authenticated
        mockMvc
            .perform(
                get("/welcome").session(session)
            )
            .andExpect(status().isOk())
            .andExpect(view().name("hello"))
            .andExpect(model().attributeExists("title"))
            .andExpect(model().attributeExists("message"))
            .andExpect(model().attribute("title", Is.is("Spring Security Hello World")))
            .andExpect(model().attribute("message", Is.is("This is welcome page!")));
        
        // test navigate to welcome page, user should be authenticated
        mockMvc
            .perform(
                get("/").session(session)
            )
            .andExpect(status().isOk())
            .andExpect(view().name("hello"))
            .andExpect(model().attributeExists("title"))
            .andExpect(model().attributeExists("message"))
            .andExpect(model().attribute("title", Is.is("Spring Security Hello World")))
            .andExpect(model().attribute("message", Is.is("This is welcome page!")));

        // test navigate to admin page, user should be authenticated
        mockMvc
            .perform(
                get("/admin").session(session)
            )
            .andExpect(status().isOk())
            .andExpect(view().name("admin"))
            .andExpect(model().attributeExists("title"))
            .andExpect(model().attributeExists("message"))
            .andExpect(model().attribute("title", Is.is("Spring Security Hello World")))
            .andExpect(model().attribute("message", Is.is("This is protected page - Admin Page!")));
    }
    
    private void testAuthenticatedAsUserNavigateToPages() throws Exception {
        // test navigate to protected page, ROLE_USER or ROLE_ADMIN
        mockMvc
            .perform(
                get("/protected").session(session)
            )
            .andExpect(status().isOk());
        
        // test navigate to welcome page, user should be authenticated
        mockMvc
            .perform(
                get("/welcome").session(session)
            )
            .andExpect(status().isOk());
        
        // test navigate to welcome page, user should be authenticated
        mockMvc
            .perform(
                get("/admin").session(session)
            )
            .andExpect(status().is4xxClientError());    // 403
    }
    
    
    //@Test
    public void testCreateAuthentication() throws Exception {
        // http://spring.io/blog/2014/05/23/preview-spring-security-test-web-security
        // code to run as a specific user for every request to run a test with any of the approaches described in Method Based Security Testing
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
//                .defaultRequest(get("/").with(userAdmin()))
//                .addFilters(springSecurityFilterChain)
//                .build();
        
        
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain, "/login*")
                .build();
        
        // run as a user (which does not need to exist) 
        session = (MockHttpSession) mockMvc
                .perform(post("/login").with(user("admin").password("admin").roles("USER","ADMIN")).with(csrf()))
            .andExpect(status().isOk())
            //.andExpect(redirectedUrl("/admin"))
            .andReturn()
            .getRequest()
            .getSession();

            assertNotNull(session);

            assertNotNull(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());

            Authentication auth = ((SecurityContextImpl)session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)).getAuthentication();

            assertNotNull(auth);
            
            assertEquals("admin", ((UserDetails)auth.getPrincipal()).getUsername());
            assertEquals("ROLE_ADMIN", ((UserDetails)auth.getPrincipal()).getAuthorities().toArray()[0].toString());
            assertEquals("ROLE_USER", ((UserDetails)auth.getPrincipal()).getAuthorities().toArray()[1].toString());
    }

    //@Test
    @WithMockUser(roles="ADMIN")
    public void requestProtectedUrlWithAdmin() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain, "/welcome*", "/admin*")
                .build();
        mockMvc
            .perform(get("/welcome"))
            .andExpect(status().isOk());
      
        mockMvc
            .perform(get("/admin"))
            .andExpect(status().isOk());
    }
    
    //@Test
    @WithMockUser(roles="USER")
    public void requestProtectedUrlWithUser() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain, "/welcome*", "/admin*")
                .build();
        mockMvc
            .perform(get("/welcome"))
            .andExpect(status().isOk());
      
        mockMvc
            .perform(get("/admin"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void itShouldAllowAccessToSecuredPageForPermittedUser() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);

        mockMvc.perform(get("/admin").session(session))
                .andExpect(status().isOk());
    }
    
    public static RequestPostProcessor userAdmin() {
        return user("admin").roles("USER","ADMIN");
    }
    
    public static RequestPostProcessor userUser() {
        return user("user").roles("USER");
    }
}
