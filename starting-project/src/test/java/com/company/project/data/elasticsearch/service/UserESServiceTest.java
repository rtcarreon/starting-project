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

import com.company.project.data.elasticsearch.entities.Car;
import com.company.project.data.elasticsearch.entities.User;
import com.company.project.data.elasticsearch.entities.ChildEntity;
import com.company.project.data.elasticsearch.entities.ParentEntity;
import com.company.project.data.elasticsearch.entities.User;
import com.company.project.data.elasticsearch.repositories.UserRepository;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import org.apache.commons.lang.RandomStringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import static org.elasticsearch.index.query.FilterBuilders.boolFilter;
import static org.elasticsearch.index.query.FilterBuilders.existsFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.hasChildQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.topChildrenQuery;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import org.elasticsearch.search.aggregations.Aggregations;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.facet.request.TermFacetRequestBuilder;
import org.springframework.data.elasticsearch.core.facet.result.Term;
import org.springframework.data.elasticsearch.core.facet.result.TermResult;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.repository.util.ClassUtils;
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
public class UserESServiceTest {
    
    @Configuration
    @ComponentScan(basePackages = "com.company.project")
    public static class Config {}
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Autowired
    private UserESService userESService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    @Resource
    private UserRepository userRepository;
    
    /**
     * Test of crud method, of class UserESService.
     */
    //@Test
    public void testCrud() {
        System.out.println("crud");
        //userESService.crud();
        
        
        // TODO review the generated test code and remove the default call to fail.
        
        
        elasticsearchTemplate.deleteIndex("xuseridx");
        elasticsearchTemplate.deleteIndex(User.class);
        //elasticsearchTemplate.createIndex(User.class);
        //elasticsearchTemplate.createIndex(User.class, user);
        elasticsearchTemplate.createIndex("xuseridx");
       
        elasticsearchTemplate.putMapping(User.class);
        elasticsearchTemplate.refresh(User.class, true);
        
        boolean typeExists = elasticsearchTemplate.typeExists("xuseridx", "xusertype");
       /**
         * Single index example
         */
        String id = UUID.randomUUID().toString();
        User user = new User();
        user.setId(id);
        user.setName("user-" + id);
        IndexQuery userIndex = new IndexQuery();
        userIndex.setId(id);
        userIndex.setObject(user);
        userIndex.setIndexName("xuseridx");
        userIndex.setType("xusertype");
        elasticsearchTemplate.index(userIndex);
        
        /**
         * Bulk index example
         */
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(user.getId())
            .withIndexName("xuseridx")
            .withObject(user)
            .build();
        
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + RandomStringUtils.random(5));
        IndexQuery indexQuery3 = new IndexQueryBuilder().withId(user.getId())
            .withIndexName("xuseridx")
            .withObject(user)
            .build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(indexQuery2, indexQuery3));
        // alternative to index
        //userRepository.save(Arrays.asList(user));
        
        elasticsearchTemplate.refresh(User.class, true);
        
        
        
        QueryBuilder builder = nestedQuery("xuseridx", boolQuery().must(termQuery("xusertype.name", "user2")).must(termQuery("xusertype.name", "user3")));

        //https://github.com/BioMedCentralLtd/spring-data-elasticsearch-sample-application/blob/master/src/main/java/org/springframework/data/elasticsearch/entities/Article.java
        Page<User> pageUser = userRepository.findByName("user-" + id, new PageRequest(0,10));
        System.out.println("Page user total elements" + pageUser.getTotalElements());
        System.out.println("Page user total elements" + pageUser.getTotalPages());
        System.out.println("Page user first element" + pageUser.getContent().get(0).getName());
        
        Iterable<User> pageUserSorted = userRepository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "xusertype.name")));
        System.out.println("Page user sorted total elements" + pageUserSorted.iterator().hasNext());
        System.out.println("Page user sorted first element" + pageUserSorted.iterator().next().getName());
        
        //SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .withIndices("xuseridx")
            .build();
        List persons = elasticsearchTemplate.queryForList(searchQuery, User.class);
        System.out.println("People size:" + (persons != null ? persons.size() : null));
        
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFilter(boolFilter().must(existsFilter("name")))
                .withPageable(new PageRequest(0,2))
                .build();
        Page<User> users = userRepository.search(searchQuery);
        System.out.println("Page user query total elements" + users.getTotalElements());
        System.out.println("Page user query total pages" + users.getTotalPages());
        System.out.println("Page user query first element" + users.getContent().get(0).getName());
    }
    
    //@Test
    public void testQueryWithFilter() {
        System.out.println("testRepository");

        //elasticsearchTemplate.deleteIndex("xuseridx");
        elasticsearchTemplate.deleteIndex(User.class);
        elasticsearchTemplate.createIndex(User.class);
        //elasticsearchTemplate.createIndex(User.class, user);
        //elasticsearchTemplate.createIndex("xuseridx");
       
        elasticsearchTemplate.putMapping(User.class);
        elasticsearchTemplate.refresh(User.class, true);
      
        String id = UUID.randomUUID().toString();
        User user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(1l);
        Map<Integer, Collection<String>> userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter2"));
        userFilter.put(2, Arrays.asList("filter11", "filter12"));
        user.setFilter(userFilter);
        userRepository.save(user);
        
        List<User> users = new ArrayList<>();
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(2l);
        userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter3"));
        userFilter.put(2, Arrays.asList("filter11", "filter13"));
        user.setFilter(userFilter);
        users.add(user);
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(3l);
        userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter4"));
        userFilter.put(2, Arrays.asList("filter11", "filter14"));
        user.setFilter(userFilter);
        users.add(user);
        userRepository.save(users);

        elasticsearchTemplate.refresh(User.class, true);
        
        // list all users with "filter1" on map key "1"
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(nestedQuery("filter", termQuery("filter.1", "filter1")))
				.build();
        Page<User> pagedUsers = userRepository.search(searchQuery);
        assertEquals(3, pagedUsers.getTotalElements());
        System.out.println("Paged user 1 : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 2 : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 3 : " + pagedUsers.getContent().get(2).getName());
        
        // list all users with "filter3" on map key "1"
        searchQuery = new NativeSearchQueryBuilder()
				.withQuery(nestedQuery("filter", termQuery("filter.1", "filter3")))
				.build();
        pagedUsers = userRepository.search(searchQuery);
        assertEquals(1, pagedUsers.getTotalElements());
        System.out.println("Paged user 1 : " + pagedUsers.getContent().get(0).getName());
    }
    
    //@Test
    public void testRepositoryMethodKeywords() {
        System.out.println("testRepository");

        //elasticsearchTemplate.deleteIndex("xuseridx");
        elasticsearchTemplate.deleteIndex(User.class);
        elasticsearchTemplate.createIndex(User.class);
        //elasticsearchTemplate.createIndex(User.class, user);
        //elasticsearchTemplate.createIndex("xuseridx");
       
        elasticsearchTemplate.putMapping(User.class);
        elasticsearchTemplate.refresh(User.class, true);
      
        String id = UUID.randomUUID().toString();
        User user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(1l);
        Map<Integer, Collection<String>> userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter2"));
        userFilter.put(2, Arrays.asList("filter11", "filter12"));
        user.setFilter(userFilter);
        userRepository.save(user);
        
        List<User> users = new ArrayList<>();
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(2l);
        userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter3"));
        userFilter.put(2, Arrays.asList("filter11", "filter13"));
        user.setFilter(userFilter);
        users.add(user);
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(3l);
        userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter4"));
        userFilter.put(2, Arrays.asList("filter11", "filter14"));
        user.setFilter(userFilter);
        users.add(user);
        userRepository.save(users);

        elasticsearchTemplate.refresh(User.class, true);
        
        User pagedUser = userRepository.findOne(id);
        assertEquals("user-" + id, pagedUser.getName());
        
        Page<User> pagedUsers = userRepository.findByNameAndRole("user-" + id, 3l, new PageRequest(0,10));
        assertEquals(1, pagedUsers.getTotalElements());
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());

        pagedUsers = userRepository.findByNameStartingWith("u", new PageRequest(0,10));
        assertEquals(3, pagedUsers.getTotalElements());
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 2 name : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 2 role : " + pagedUsers.getContent().get(1).getRole());
        System.out.println("Paged user 3 name : " + pagedUsers.getContent().get(2).getName());
        System.out.println("Paged user 3 role : " + pagedUsers.getContent().get(2).getRole());
        
        pagedUsers = userRepository.findByNameStartingWith("x", new PageRequest(0,10));
        assertEquals(0, pagedUsers.getTotalElements());
        
        pagedUsers = userRepository.findByRoleBetween(1, 2, new PageRequest(0,10));
        assertEquals(2, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 1 || u.getRole() == 2);
        }
        //assertEquals("2", "" + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 2 name : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 2 role : " + pagedUsers.getContent().get(1).getRole());
        
        pagedUsers = userRepository.findByRoleLessThan(2, new PageRequest(0,10));
        assertEquals(2, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 1 || u.getRole() == 2);
        }
        //assertEquals("2", "" + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 2 name : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 2 role : " + pagedUsers.getContent().get(1).getRole());
        
        pagedUsers = userRepository.findByRoleGreaterThan(2, new PageRequest(0,10));
        assertEquals(2, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 3 || u.getRole() == 2);
        }
        //assertEquals("2", "" + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 2 name : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 2 role : " + pagedUsers.getContent().get(1).getRole());
        
        pagedUsers = userRepository.findByRoleBefore(2, new PageRequest(0,10));
        assertEquals(2, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 1 || u.getRole() == 2);
        }
        //assertEquals("2", "" + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 2 name : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 2 role : " + pagedUsers.getContent().get(1).getRole());
        
        pagedUsers = userRepository.findByRoleAfter(2, new PageRequest(0,10));
        assertEquals(2, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 3 || u.getRole() == 2);
        }
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
        System.out.println("Paged user 2 name : " + pagedUsers.getContent().get(1).getName());
        System.out.println("Paged user 2 role : " + pagedUsers.getContent().get(1).getRole());
        
        pagedUsers = userRepository.findByNameEndingWith(id, new PageRequest(0,10));
        assertEquals(1, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 3);
        }
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());

        pagedUsers = userRepository.findByNameContaining(id, new PageRequest(0,10));
        assertEquals(1, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 3);
        }
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());

        pagedUsers = userRepository.findByNameLike(id, new PageRequest(0,10));
        assertEquals(1, pagedUsers.getTotalElements());
        for (User u : pagedUsers.getContent()) {
            assertTrue(u.getRole() == 3);
        }
        System.out.println("Paged user 1 name : " + pagedUsers.getContent().get(0).getName());
        System.out.println("Paged user 1 role : " + pagedUsers.getContent().get(0).getRole());
    }
    
    //@Test
    public void testRepositoryQueries() {
        System.out.println("testRepository");

        //elasticsearchTemplate.deleteIndex("xuseridx");
        elasticsearchTemplate.deleteIndex(User.class);
        elasticsearchTemplate.createIndex(User.class);
        //elasticsearchTemplate.createIndex(User.class, user);
        //elasticsearchTemplate.createIndex("xuseridx");
       
        elasticsearchTemplate.putMapping(User.class);
        elasticsearchTemplate.refresh(User.class, true);
      
        String id = UUID.randomUUID().toString();
        User user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(1l);
        user.setPath("1.0");
        Map<Integer, Collection<String>> userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter2"));
        userFilter.put(2, Arrays.asList("filter11", "filter12"));
        user.setFilter(userFilter);
        userRepository.save(user);
        
        boolean exists = userRepository.exists(user.getId());
        assertTrue(exists);
        
        List<User> users = new ArrayList<>();
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(2l);
        user.setPath("1.1.0");
        userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter3"));
        userFilter.put(2, Arrays.asList("filter11", "filter13"));
        user.setFilter(userFilter);
        users.add(user);
        //userRepository.save(user);
        id = UUID.randomUUID().toString();
        user = new User();
        user.setId(id);
        user.setName("user-" + id);
        user.setRole(3l);
        user.setPath("1.1.1.0");
        userFilter = new HashMap<>();
        userFilter.put(1, Arrays.asList("filter1", "filter4"));
        userFilter.put(2, Arrays.asList("filter11", "filter14"));
        user.setFilter(userFilter);
        users.add(user);
        //userRepository.save(user);
        userRepository.save(users); //bulk save
        exists = userRepository.exists(user.getId());
        assertTrue(exists);

        elasticsearchTemplate.refresh(User.class, true);
        
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id);
        User userIndexed = elasticsearchTemplate.queryForObject(getQuery, User.class);
        assertEquals("user-" + id, "" + userIndexed.getName());
        assertEquals(id, "" + userIndexed.getId());
        assertEquals("3", "" + userIndexed.getRole());
        
        String facetName = "testName";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFacet(new TermFacetRequestBuilder(facetName)
                .fields("role").descCount().build()).build();
       
        // when
        FacetedPage<User> result = elasticsearchTemplate.queryForPage(searchQuery, User.class);
        assertEquals(3, result.getNumberOfElements());
        TermResult facet = (TermResult) result.getFacet(facetName);
        assertEquals(3, facet.getTerms().size());
        for (Term term : facet.getTerms()) {
            assertTrue("1".equals(term.getTerm()) || "2".equals(term.getTerm()) || "3".equals(term.getTerm()));
            System.out.println("Facet term : " + term.getTerm());
            //System.out.println("Facet count : " + term.getCount());
            //result
            //Facet term : 3
            //Facet term : 2
            //Facet term : 1
        }
        
        // query list all
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                //.withFacet(new TermFacetRequestBuilder(facetName).fields("role").descCount().build())
                .build();
        List<User> userList = elasticsearchTemplate.queryForList(searchQuery, User.class);
        assertEquals(3, userList.size());
        for (User u : userList) {
            System.out.println("User ID: " + u.getId());
            System.out.println("User Name: " + u.getName());
        }
        
        // query list all with pagination
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withPageable(new PageRequest(0,10))
                //.withIndices("xuseridx")
                //.withTypes("xusertype")
                //.withFacet(new TermFacetRequestBuilder(facetName).fields("role").descCount().build())
                .build();
        Page<User> pagedUsers = elasticsearchTemplate.queryForPage(searchQuery, User.class);
        assertEquals(3, pagedUsers.getTotalElements());
        assertEquals(1, pagedUsers.getTotalPages());
        for (User u : pagedUsers.getContent()) {
            System.out.println("User ID: " + u.getId());
            System.out.println("User Name: " + u.getName());
        }
        
        // query filter with key "1" contains value "filter3" and key "2" contains value "filter13"
        QueryBuilder builder = nestedQuery("filter",
                boolQuery()
                .must(termQuery("1", "filter3"))
                .must(termQuery("2", "filter13"))
            );
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();
        userList = elasticsearchTemplate.queryForList(searchQuery, User.class);
        assertEquals(1, userList.size());
        for (User u : userList) {
            System.out.println("User ID: " + u.getId());
            System.out.println("User Name: " + u.getName());
        }
        
        // query all with filter path prefix "1.1"
        builder = boolQuery()
                .must(prefixQuery("path", "1.1"));
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();
        userList = elasticsearchTemplate.queryForList(searchQuery, User.class);
        assertEquals(2, userList.size());
        for (User u : userList) {
            System.out.println("User ID: " + u.getId());
            System.out.println("User Name: " + u.getName());
        }
        
        builder = QueryBuilders.multiMatchQuery(
            "user",             // term to search
            "path", "name"      // field to search
        );
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();
        userList = elasticsearchTemplate.queryForList(searchQuery, User.class);
        assertEquals(3, userList.size());
        for (User u : userList) {
            System.out.println("User ID: " + u.getId());
            System.out.println("User Name: " + u.getName());
        }
        
        
        // ref: https://github.com/elasticsearch/elasticsearch/blob/master/docs/java-api/query-dsl-queries.asciidoc#boolean-query
        // Boolean Query
        builder = boolQuery()
            .must(termQuery("content", "test1"))            // field name, keyword
            .must(termQuery("content", "test4"))            // field name, keyword
            .mustNot(termQuery("content", "test2"))         // field name, keyword
            .should(termQuery("content", "test3"));         // field name, keyword
        // Boosting Query
        builder = QueryBuilders.boostingQuery()
            .positive(termQuery("name","kimchy"))           // query that will promote documents
            .negative(termQuery("name","dadoonet"))         // query that will demote documents
            .negativeBoost(0.2f);                           // negative boost
        //IDs Query
        builder = QueryBuilders.idsQuery().ids("1", "2");
        // Constant Score Query
        builder = QueryBuilders.constantScoreQuery(
            termFilter("name","kimchy")                     // you can use a filter
        ).boost(2.0f);                                      // filter score
        builder = QueryBuilders.constantScoreQuery(
            termQuery("name","kimchy")                      // you can use a query
        ).boost(2.0f); 
        // Prefix Query
        builder = QueryBuilders.prefixQuery(
            "brand",        // field
            "heine"         // term
        );
        // QueryString Query
        builder = QueryBuilders.queryString("+kimchy -elasticsearch");
        // Range Query
        builder = QueryBuilders.rangeQuery("price")     // field
            .from(5)                                    // from
            .to(10)                                     // to
            .includeLower(true)                         // include lower value means that from is gt when false or gte when true
            .includeUpper(false);                       // include upper value means that to is lt when false or lte when true
        
        
        builder = QueryBuilders.disMaxQuery()               // add your queries
            .add(termQuery("name","kimchy"))                // add your queries
            .add(termQuery("name","elasticsearch"))         // 
            .boost(1.2f)                                    // boost factor
            .tieBreaker(0.7f);                              // tie breaker
        // Fuzzy Like This (Field) Query (flt and flt_field)
         builder = QueryBuilders.fuzzyLikeThisQuery("name.first", "name.last")  // fields
            .likeText("text like this one")                                     // text
            .maxQueryTerms(12);                                                 // max num of Terms in generated queries
        //FuzzyQuery
        builder = QueryBuilders.fuzzyQuery(
            "name",                                     // field
            "kimzhy"                                    // term
        );
    }
    
    //@Test
    public void testNestedQueries() {
        elasticsearchTemplate.deleteIndex(ChildEntity.class);
        elasticsearchTemplate.deleteIndex(ParentEntity.class);
        elasticsearchTemplate.createIndex(ParentEntity.class);
        elasticsearchTemplate.createIndex(ChildEntity.class);
        elasticsearchTemplate.putMapping(ParentEntity.class);
        elasticsearchTemplate.putMapping(ChildEntity.class);
        
        // index parents
        ParentEntity parent1 = new ParentEntity("parent1", "First Parent");
        ParentEntity parent2 = new ParentEntity("parent2", "Second Parent");
        ParentEntity parent3 = new ParentEntity("parent3", "Third Parent");
        ParentEntity parent4 = new ParentEntity("parent4", "Fourth Parent");
        ParentEntity parent5 = new ParentEntity("parent5", "Fifth Parent");
        IndexQuery index = new IndexQuery();
        index.setId(parent1.getId());
        index.setObject(parent1);
        elasticsearchTemplate.index(index);
        index = new IndexQuery();
        index.setId(parent2.getId());
        index.setObject(parent2);
        elasticsearchTemplate.index(index);
        index = new IndexQuery();
        index.setId(parent3.getId());
        index.setObject(parent3);
        elasticsearchTemplate.index(index);
        index = new IndexQuery();
        index.setId(parent4.getId());
        index.setObject(parent4);
        elasticsearchTemplate.index(index);
        index = new IndexQuery();
        index.setId(parent5.getId());
        index.setObject(parent5);
        elasticsearchTemplate.index(index);
        
        ChildEntity child1 = new ChildEntity("child1", "parent1", "First");
        index = new IndexQuery();
        index.setId(child1.getId());
        index.setObject(child1);
        index.setParentId(child1.getParentId());
        elasticsearchTemplate.index(index);
        
        ChildEntity child2 = new ChildEntity("child2", "parent2", "Second");
        index = new IndexQuery();
        index.setId(child2.getId());
        index.setObject(child2);
        index.setParentId(child2.getParentId());
        elasticsearchTemplate.index(index);
        
        ChildEntity child3 = new ChildEntity("child3", "parent3", "Third");
        index = new IndexQuery();
        index.setId(child3.getId());
        index.setObject(child3);
        index.setParentId(child3.getParentId());
        elasticsearchTemplate.index(index);
        
        ChildEntity child4 = new ChildEntity("child4", "parent4", "Fourth");
        index = new IndexQuery();
        index.setId(child4.getId());
        index.setObject(child4);
        index.setParentId(child4.getParentId());
        elasticsearchTemplate.index(index);
        
        ChildEntity child5 = new ChildEntity("child4", "parent4", "Fifth");
        index = new IndexQuery();
        index.setId(child5.getId());
        index.setObject(child5);
        index.setParentId(child5.getParentId());
        elasticsearchTemplate.index(index);
        
        elasticsearchTemplate.refresh(ParentEntity.class, true);
        elasticsearchTemplate.refresh(ChildEntity.class, true);
        
        // find parents of the child
        QueryBuilder query = hasChildQuery(ParentEntity.CHILD_TYPE, QueryBuilders.termQuery("name", child1.getName().toLowerCase()));
        List<ParentEntity> parents = elasticsearchTemplate.queryForList(new NativeSearchQuery(query), ParentEntity.class);
        
        // we're expecting only the first parent as result
        assertEquals(1, parents.size());
        assertEquals(parent1.getId(), parents.get(0).getId());
        
        // find all parents that have the first child using topChildren Query
        query = topChildrenQuery(ParentEntity.CHILD_TYPE, QueryBuilders.termQuery("name", child2.getName().toLowerCase()));
        parents = elasticsearchTemplate.queryForList(new NativeSearchQuery(query), ParentEntity.class);

        // we're expecting only the first parent as result
        assertEquals(1, parents.size());
        assertEquals(parent2.getId(), parents.get(0).getId());

    }
    
    @Test
    public void testAggregation() {
        System.out.println("testAggregation");
        
        elasticsearchTemplate.deleteIndex(Car.class);
        elasticsearchTemplate.createIndex("car_idx");
        elasticsearchTemplate.putMapping(Car.class);
        elasticsearchTemplate.refresh(Car.class, true);
        
        boolean typeExists = elasticsearchTemplate.typeExists("car_idx", "car_type");
        
        assertTrue(typeExists);
       /**
         * Single index example
         */
        String id = "1";
        Car car = new Car();
        car.setId(id);
        car.setColor("red");
        car.setPrice(10000);
        car.setMake("honda");
        car.setSold("2014-10-28");
        IndexQuery userIndex = new IndexQuery();
        userIndex.setId(id);
        userIndex.setObject(car);
        userIndex.setIndexName("car_idx");
        userIndex.setType("car_type");
        elasticsearchTemplate.index(userIndex);
        
        /**
         * Bulk index example
         */
        id = "2";
        car = new Car();
        car.setId(id);
        car.setColor("red");
        car.setPrice(20000);
        car.setMake("honda");
        car.setSold("2014-11-05");
        IndexQuery indexQuery2 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        
        id = "3";
        car = new Car();
        car.setId(id);
        car.setColor("green");
        car.setPrice(30000);
        car.setMake("ford");
        car.setSold("2014-05-18");
        IndexQuery indexQuery3 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        
        id = "4";
        car = new Car();
        car.setId(id);
        car.setColor("blue");
        car.setPrice(15000);
        car.setMake("toyota");
        car.setSold("2014-07-02");
        IndexQuery indexQuery4 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        
        id = "5";
        car = new Car();
        car.setId(id);
        car.setColor("green");
        car.setPrice(12000);
        car.setMake("toyota");
        car.setSold("2014-08-19");
        IndexQuery indexQuery5 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        
        id = "6";
        car = new Car();
        car.setId(id);
        car.setColor("red");
        car.setPrice(20000);
        car.setMake("honda");
        car.setSold("2014-11-05");
        IndexQuery indexQuery6 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        
        id = "7";
        car = new Car();
        car.setId(id);
        car.setColor("red");
        car.setPrice(80000);
        car.setMake("bmw");
        car.setSold("2014-01-01");
        IndexQuery indexQuery7 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        
        id = "7";
        car = new Car();
        car.setId(id);
        car.setColor("blue");
        car.setPrice(25000);
        car.setMake("ford");
        car.setSold("2014-02-12");
        IndexQuery indexQuery8 = new IndexQueryBuilder().withId(car.getId())
            .withIndexName("car_idx")
            .withObject(car)
            .build();
        elasticsearchTemplate.bulkIndex(Arrays.asList(
                indexQuery2,
                indexQuery3,
                indexQuery4,
                indexQuery5,
                indexQuery6,
                indexQuery7,
                indexQuery8));
        // alternative to index
        //userRepository.save(Arrays.asList(car));
        
        elasticsearchTemplate.refresh(Car.class, true);
        
        
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.COUNT)
                .withIndices("car_idx").withTypes("car_type")
                .addAggregation(AggregationBuilders.terms("colors").field("color"))
                .build();
        Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });
        assertThat(aggregations, is(notNullValue()));
        assertThat(aggregations.asMap().get("colors"), is(notNullValue()));
        Gson gson = new Gson();
        for (Aggregation agg : aggregations.asList()) {
            System.out.println("Aggregation json string:" + gson.toJson(agg));
        }
        
    }
}
