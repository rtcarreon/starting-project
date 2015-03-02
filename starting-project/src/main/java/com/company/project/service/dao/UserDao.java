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

package com.company.project.service.dao;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Spring JDBC Template implementation
 * @author Romer
 */
@Repository
public class UserDao {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    @Qualifier("txManager")
    private PlatformTransactionManager txManager;
    
    public Map<String,Object> get(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Map<String,Object> map = null;
        map = jdbcTemplate.queryForMap("SELECT * from users where username = ?",
            username);
        return map;
    }
    
    public int insert(String username, String password) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int result = jdbcTemplate.update(
            "insert into users (username, password) values (?, ?)",
            username, password
        );
        
        return result;
    }

    public int insertx(String username, String password) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = txManager.getTransaction(def);
        int result = 0;
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            result = jdbcTemplate.update(
                "insert into users (username, password) values (?, ?)",
                username, password
            );
            txManager.commit(status);                   // will not insert if this line is removed
        } catch(Exception e) {
            e.printStackTrace();
            txManager.rollback(status);
        }
        
        return result;
    }
    
    public int update(String username, String password, String enabled) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int result = jdbcTemplate.update(
            "update into users set password = ?, enabled = ? where username = ? ", 
            password, Integer.parseInt(enabled), username
        );
        
        return result;
    }
    
    public void delete(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "delete from users where username = ?", username
        );
    }
    
    public void createTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("create table users (username VARCHAR(45) NOT NULL, password VARCHAR(45) NOT NULL, enabled TINYINT NOT NULL DEFAULT 1, PRIMARY KEY (username))");
        jdbcTemplate.update("create table user_roles (user_role_id int(11) NOT NULL AUTO_INCREMENT, username varchar(45) NOT NULL, role varchar(45) NOT NULL, PRIMARY KEY (user_role_id), UNIQUE KEY uni_username_role (role,username), KEY fk_username_idx (username), CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username))");
        jdbcTemplate.update("create table persistent_logins (username varchar(64) not null, series varchar(64) not null, token varchar(64) not null, last_used timestamp not null, PRIMARY KEY (series))");
        
        jdbcTemplate.update(
            "insert into users (username, password) values (?, ?)",
            "admin", "admin"
        );
        jdbcTemplate.update(
            "insert into users (username, password) values (?, ?)",
            "user", "user"
        );
        jdbcTemplate.update(
            "insert into user_roles (username, role) values (?, ?)",
            "admin", "ROLE_ADMIN"
        );
        jdbcTemplate.update(
            "insert into user_roles (username, role) values (?, ?)",
            "admin", "ROLE_USER"
        );
        jdbcTemplate.update(
            "insert into user_roles (username, role) values (?, ?)",
            "user", "ROLE_USER"
        );
    }
    
    public void dropTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("drop table user_roles");
	jdbcTemplate.update("drop table users");
        jdbcTemplate.update("drop table persistent_logins");
    }
}
