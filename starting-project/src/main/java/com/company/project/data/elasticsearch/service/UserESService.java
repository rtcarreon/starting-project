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

package com.company.project.data.elasticsearch.service;

import com.company.project.data.elasticsearch.entities.User;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 *
 * @author Romer
 */
//@Service
@Service("userESService")
public class UserESService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    public static void main(String[] args) {
        UserESService service = new UserESService();
        service.crud();
    }
    
    public void crud() {
        //elasticsearchTemplate.deleteIndex("xxx");
        //elasticsearchTemplate.createIndex("xxx");
        elasticsearchTemplate.deleteIndex(User.class);
        elasticsearchTemplate.createIndex(User.class);
        elasticsearchTemplate.putMapping(User.class);
        elasticsearchTemplate.refresh(User.class, true);
        
        IndexQuery userIndex = new IndexQuery();
        User user = new User();
        user.setId("1");
        user.setName("user1");
        userIndex.setId("1");
        userIndex.setObject(user);
        //elasticsearchTemplate.index(userIndex);
        //elasticsearchTemplate.index(new IndexQueryBuilder().withObject(user).build());
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(user.getId())
            .withIndexName("xuseridx")
            .withObject(user)
            .build();
         elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery2));
        
//        userIndex = new IndexQuery();
//        user = new User();
//        user.setId("2");
//        user.setName("user2");
//        userIndex.setId("2");
//        userIndex.setObject(user);
//        elasticsearchTemplate.index(userIndex);
//        
//        userIndex = new IndexQuery();
//        user = new User();
//        user.setId("3");
//        user.setName("user3");
//        userIndex.setId("3");
//        userIndex.setObject(user);
//        elasticsearchTemplate.index(userIndex);

        elasticsearchTemplate.refresh(User.class, true);
        
        //QueryBuilder builder = nestedQuery("xuseridx", boolQuery().must(termQuery("xusertype.name", "user2")).must(termQuery("xusertype.name", "user3")));
        //QueryBuilder builder = nestedQuery("xuseridx", boolQuery().must(fieldQuery("name", "user2")).must(fieldQuery("name", "user3")));
        QueryBuilder builder = nestedQuery("xuseridx", boolQuery().must(termQuery("xusertype.name", "user2")).must(termQuery("xusertype.name", "user3")));

        
        //SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .withIndices("xuseridx")
            .build();
        List persons = elasticsearchTemplate.queryForList(searchQuery, User.class);
        System.out.println("People size:" + (persons != null ? persons.size() : null));
    }

    private QueryBuilder fieldQuery(String xusertypename, String user2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
