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

package com.company.project.config;

import java.io.IOException;
import javax.sql.DataSource;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;


/**
 *
 * @author Romer
 */
@Configuration
//@EnableAspectJAutoProxy(proxyTargetClass = true) 
@MapperScan("com.company.project.persistence")
@EnableTransactionManagement
@PropertySource("classpath:/datasource.properties")
@EnableCaching
@EnableElasticsearchRepositories(basePackages = "com/company/project/data/elasticsearch/repositories")
public class DataConfig implements TransactionManagementConfigurer {
    
    private @Value("${jdbc.driver}") String driverClassName;
    private @Value("${jdbc.url}") String url;
    private @Value("${jdbc.username}") String username;
    private @Value("${jdbc.password}") String password;
    
    @Autowired
    Environment env;
    
    //@Bean
    @Bean(name = "dataSource")
    public DataSource dataSource() {        
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/test");
//        driverManagerDataSource.setUsername("root");
//        driverManagerDataSource.setPassword("root");
        
        // fails during unit tests
//        driverManagerDataSource.setDriverClassName(driverClassName);
//        driverManagerDataSource.setUrl(url);
//        driverManagerDataSource.setUsername(username);
//        driverManagerDataSource.setPassword(password);
        
        driverManagerDataSource.setDriverClassName(env.getProperty("jdbc.driver"));
        driverManagerDataSource.setUrl(env.getProperty("jdbc.url"));
        driverManagerDataSource.setUsername(env.getProperty("jdbc.username"));
        driverManagerDataSource.setPassword(env.getProperty("jdbc.password"));
        
        // create a table and populate some data
        //JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //System.out.println("Creating tables");
        //jdbcTemplate.execute("drop table users if exists");
        //jdbcTemplate.execute("create table users(id serial, firstName varchar(255), lastName varchar(255), email varchar(255))");
        //jdbcTemplate.update("INSERT INTO users(firstName, lastName, email) values (?,?,?)", "Mike", "Lanyon", "lanyonm@gmail.com");

        return driverManagerDataSource;
    }
    
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
    
    @Bean(name = "txManager")
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }
    
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        // package containing the domain objects(DTO) that will be available as types in the MyBatis xml files 
        sessionFactory.setTypeAliasesPackage("com.company.project.model");
        return sessionFactory;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(env.getProperty("redis.host"));
        jedisConnectionFactory.setPort(Integer.valueOf(env.getProperty("redis.port")));
        jedisConnectionFactory.setPassword(env.getProperty("redis.password"));
        jedisConnectionFactory.setUsePool(true);
        return jedisConnectionFactory;
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Blog> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConfig.redisConnectionFactory());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new JacksonJsonRedisSerializer<>(Blog.class));
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new JacksonJsonRedisSerializer<>(Blog.class));
//        return template;

        //RedisTemplate<String, String> template = new StringRedisTemplate(redisConnectionFactory());
        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory());
        // explicitly enable transaction support - 4.9.1. @Transactional Support http://docs.spring.io/spring-data/redis/docs/current/reference/html/
        template.setEnableTransactionSupport(true);
        return template;
    }
    
    //http://blog.joshuawhite.com/java/caching-with-spring-data-redis/
    //http://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/cache.html
// sample code on the service side
//    @Service('helloService')
//    public class HelloServiceImpl implements HelloService {
//        /**
//         * Using SpEL for conditional caching - only cache method executions when
//         * the name is equal to 'Joshua'
//         */
//        @Cacheable(value='messageCache', condition=''Joshua'.equals(#name)')
//        public String getMessage(String name) {
//            System.out.println('Executing HelloServiceImpl' +
//                            '.getHelloMessage(\'' + name + '\')');
//            return 'Hello ' + name + '!';
//        }
//    }
    @Bean
    CacheManager cacheManager() {
        return new RedisCacheManager(redisTemplate());
    }
    
    @Bean
    //@Bean(name = "elasticsearchTemplate")
    public ElasticsearchTemplate elasticsearchTemplate() {
    //public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchTemplate(client());
    }

    @Bean
    public Client client(){
        TransportClient client= new TransportClient();
        TransportAddress address = new InetSocketTransportAddress("localhost", 9300); 
        client.addTransportAddress(address);
        return client;
    }
    
    
    /**
     * Using below code will create a elastic search embedded to spring
     */
//    @Bean
//    //public ElasticsearchOperations elasticsearchTemplate() throws IOException {
//    public ElasticsearchTemplate elasticsearchTemplate() throws IOException {
//     //Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch.yml").build();
//        Settings settings = ImmutableSettings.settingsBuilder()
//                .put("cluster.name", "romer_es")
//                .put("node.name", "traxex")
////                .put("bootstrap.mlockall", true)
////                .put("gateway.expected_nodes", 1)
////                .put("discovery.zen.ping.unicast.hosts", "[\"localhost:9200\"]")
////                .put("script.disable_dynamic", false)
////                //.put("index.store.type", "memory")
//                .put("index.store.type", "mmapfs")
////                .put("index.number_of_shards", 1)
////                .put("index.number_of_replicas", 0)
//                
//                
//                .put("client.transport.sniff", false)
//                //.put("path.home", "/path/to/elastic/home")
//                .put("index.number_of_replicas", 0)
//                .put("index.number_of_shards", 1)
//                .put("action.write_consistency", "one")
//                
//                
//                .build();
//
////        return new ElasticsearchTemplate(nodeBuilder()
////                .loadConfigSettings(false)
////                .local(true)
////                .settings(settings).node().client());
//        NodeBuilder nb = new NodeBuilder().settings(settings).local(true).data(true);
//        Node node = nb.node();
//        Client client = node.client();
//        return new ElasticsearchTemplate(client);
//    }
    
//    @Bean
//    public ElasticsearchTemplate elasticsearchTemplate() {
//        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
//    }
    
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchTemplate(client());
//    }
//
//    @Bean
//    public Client client() {
//        TransportClient client = new TransportClient();
//        TransportAddress address = new InetSocketTransportAddress(hostname, port);
//        client.addTransportAddress(address);
//        return client;
//    }
    
}
