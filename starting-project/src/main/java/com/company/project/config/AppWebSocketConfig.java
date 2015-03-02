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

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 *
 * @author Romer
 */
@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("/topic");
            config.setApplicationDestinationPrefixes("/appName");
            //config.setPathMatcher(new AntPathMatcher("."));     //using "." (dot) instead of "/" (slash) as the separator in @MessageMapping
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/messageMapping", "/messageMapping2", "/messageMappingSpecificUser", "/imageFileShareMapping", "/fileShareMapping").withSockJS()  
                //.setStreamBytesLimit(512 * 1024)
                //.setHttpMessageCacheSize(1000)
                //.setDisconnectDelay(30 * 1000);
                    ;
            
            //SockJsServiceRegistration registration = registry.addEndpoint("/hello").withSockJS().setClientLibraryUrl("http://localhost:8090/resources/sockjs-0.3.4.js");
            //registration.setWebSocketEnabled(true);
            //registration.setSessionCookieNeeded(false);

    }
    
    
//    @Bean
//    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
////        container.setMaxTextMessageBufferSize(32768);
////        container.setMaxBinaryMessageBufferSize(32768);
//        container.setMaxTextMessageBufferSize(655360);
//        container.setMaxBinaryMessageBufferSize(655360);
//        return container;
//    }
    
    
    
//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
//        messageConverters.add(new ByteArrayMessageConverter());
//        return false;
//    }
//    
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.setInterceptors(presenceChannelInterceptor());
//    }
//    
//    @Override
//    public void configureClientOutboundChannel(ChannelRegistration registration) {
//        registration.taskExecutor().corePoolSize(8);
//        registration.setInterceptors(presenceChannelInterceptor());
//    }
//    
//    @Bean
//    public PresenceChannelInterceptor presenceChannelInterceptor() {
//        return new PresenceChannelInterceptor();
//    }

}
