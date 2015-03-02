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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/**
 *
 * @author Romer
 */
//@WebListener
@Component
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {
 
    private final Log logger = LogFactory.getLog(StompConnectEvent.class);
 
    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        
        String  username = sha.getNativeHeader("username").get(0);          // from jsp : stompClient.connect({username: "${pageContext.request.userPrincipal.name}"}, function(frame) {
        logger.debug("Connect event [sessionId: " + sha.getSessionId() +"; username: "+ username + " ]");
        System.out.println("Connect event [sessionId: " + sha.getSessionId() +"; username: "+ username + " ]");
    }
}
