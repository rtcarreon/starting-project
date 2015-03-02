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

import com.company.project.persistence.UserMapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Spring-4-Mybatis implementation
 * @author Romer
 */
//@Service("userService")
@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    @Qualifier("txManager")
    private PlatformTransactionManager txManager;
    
    public UserService() {}
        
    public Map get(String username) {
        return userMapper.get(username);
    }
    
    /**
     * Using SpEL for conditional caching - only cache method executions when
     * the name is equal to "Joshua"
     */
    //@Cacheable(value="messageCache", condition="'Joshua'.equals(#name)")
    //@Cacheable(value="book", condition="#name.length < 32", unless="#result.hardback")
    //http://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/cache.html
    @Cacheable("books")
    public List getAll() {
        return userMapper.getAll();
    }
    public boolean insert(Map user) {
        return userMapper.insert(user) == 1;
    }
    
    public boolean insertx(Map user) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = txManager.getTransaction(def);
        
        int result = 0;
        try {
            result = userMapper.insert(user);
            txManager.commit(status);                   // will not insert if this line is removed
        } catch(Exception e) {
            e.printStackTrace();
            //session.rollback();
            txManager.rollback(status);
        } finally {
        }
        
        return result == 1;
    }

    public boolean update(Map user) {
        return userMapper.update(user) == 1;
    }
}
