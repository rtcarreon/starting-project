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

package com.company.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Romer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class UserMapperImplTest {
    
    @Configuration
    @ComponentScan(basePackages = "com.company.project")
    public static class Config {}
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Autowired
    private UserMapperImpl userMapperImpl;
    
    /**
     * Test of get method, of class UserMapperImpl.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        String username = "admin";
        String password = "admin";
        Map result = userMapperImpl.get(username);
        assertNotNull(result);
        assertEquals(username, result.get("username"));
        assertEquals(password, result.get("password"));
        assertEquals("1", "" + result.get("enabled"));
        System.out.print("Result: " + result);
    }

    //@Test
    public void testInsert() {
        System.out.println("insert");
        Map user = new HashMap();
        user.put("username", "adminx");
        user.put("password", "adminx");
        
        int result = userMapperImpl.insert(user);
         System.out.print("Result: " + result);
        assertTrue(result > 0);
    }
    
    //@Test
    public void testInsertx() {
        System.out.println("insertx");
        Map user = new HashMap();
        user.put("username", "adminx");
        user.put("password", "adminx");
        
        int result = userMapperImpl.insertx(user);
         System.out.print("Result: " + result);
        assertTrue(result > 0);
    }
}
