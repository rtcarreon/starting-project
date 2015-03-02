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

package com.company.project.data.elasticsearch.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.company.project.data.elasticsearch.entities.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
/**
 *
 * @author Romer
 */
public interface UserRepository extends ElasticsearchRepository<User, String> {   
    //Page<User> findByNameAndPrice(String name, Pageable pageable);
    
    //http://docs.spring.io/spring-data/elasticsearch/docs/1.0.0.M1/reference/html/elasticsearch.repositories.html
    // {"bool" : {"should" : [ {"field" : {"name" : "?"}}, {"field" : {"price" : "?"}} ]}}
    Page<User> findByNameOrRole(String name, long role, Pageable pageable);
    // {"bool" : {"must" : [ {"field" : {"name" : "?"}}, {"field" : {"role" : "?"}} ]}}
    Page<User> findByNameAndRole(String name, long role, Pageable pageable);
    // {"bool" : {"must" : {"field" : {"name" : "?"}}}}
    Page<User> findByName(String name, Pageable pageable);
    // {"bool" : {"must" : {"field" : {"name" : {"query" : "?*","analyze_wildcard" : true}}}}}
    Page<User> findByNameStartingWith(String startsWith, Pageable pageable);
    // {"bool" : {"must" : {"range" : {"role" : {"from" : ?,"to" : ?,"include_lower" : true,"include_upper" : true}}}}}
    Page<User> findByRoleBetween(long min, long max, Pageable pageable);
    // {"bool" : {"must" : {"range" : {"role" : {"from" : null,"to" : ?,"include_lower" : true,"include_upper" : true}}}}}
    Page<User> findByRoleLessThan(long role, Pageable pageable);
    // {"bool" : {"must" : {"range" : {"price" : {"from" : ?,"to" : null,"include_lower" : true,"include_upper" : true}}}}}
    Page<User> findByRoleGreaterThan(long role, Pageable pageable);
    // {"bool" : {"must" : {"range" : {"price" : {"from" : null,"to" : ?,"include_lower" : true,"include_upper" : true}}}}}
    Page<User> findByRoleBefore(long role, Pageable pageable);
    // {"bool" : {"must" : {"range" : {"price" : {"from" : ?,"to" : null,"include_lower" : true,"include_upper" : true}}}}}
    Page<User> findByRoleAfter(long role, Pageable pageable);
    // {"bool" : {"must" : {"field" : {"name" : {"query" : "?*","analyze_wildcard" : true}}}}}
    Page<User> findByNameLike(String like, Pageable pageable);
    // {"bool" : {"must" : {"field" : {"name" : {"query" : "*?*","analyze_wildcard" : true}}}}}
    Page<User> findByNameContaining(String contains, Pageable pageable);
    // {"bool" : {"must" : {"field" : {"name" : {"query" : "*?","analyze_wildcard" : true}}}}}
    Page<User> findByNameEndingWith(String ending, Pageable pageable);
    
    
    
    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"message\" : \"?0\"}}}}")
    //Page<User> findByMessage(String message, Pageable pageable);
    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"message\" : \"?0\"}}}}")
    //List<User> findByMessage(String message);
}
