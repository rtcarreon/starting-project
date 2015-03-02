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

import java.util.Date;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 *
 * @author Romer
 */
@Repository
public class PersistentTokenDao {
    
    @Autowired
    private DataSource dataSource;
    
    public Map<String,Object> get(String series) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Map<String,Object> map = null;
        map = jdbcTemplate.queryForMap("SELECT * from persistent_logins where series = ?" , series);
        return map;
    }
    
    public void insert(String username, String series, String token, Date date) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "insert into persistent_logins (username, series, token, last_used) values (?, ?, ?, ?)",
            username, series, token, date
        );
    }
    
    public void update(String series, String token, Date date) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "update into persistent_logins set token = ?, last_used = ? where series = ? ", token, date, series
        );
    }
    
    public void delete(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "delete from persistent_logins where username = ?", username
        );
    }
    
    public void createTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("create table messages (messagekey varchar(20), message varchar(100))");
    }
    
    public void dropTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	jdbcTemplate.update("drop table messages");
    }
}
