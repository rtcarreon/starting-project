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
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 *
 * @author Romer
 */
@Component
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {
    private final Log logger = LogFactory.getLog(StompDisconnectEvent.class);
 
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        CloseStatus status = event.getCloseStatus();
        
        if (status.equals(CloseStatus.BAD_DATA)) {
            System.out.println("CloseStatus.BAD_DATA");
        }
        if (status.equals(CloseStatus.GOING_AWAY)) {
            System.out.println("CloseStatus.GOING_AWAY");
        }
        if (status.equals(CloseStatus.NORMAL)) {
            System.out.println("CloseStatus.NORMAL");
        }
        if (status.equals(CloseStatus.NOT_ACCEPTABLE)) {
            System.out.println("CloseStatus.NOT_ACCEPTABLE");
        }
        if (status.equals(CloseStatus.NO_CLOSE_FRAME)) {
            System.out.println("CloseStatus.NO_CLOSE_FRAME");
        }
        if (status.equals(CloseStatus.NO_STATUS_CODE)) {
            System.out.println("CloseStatus.NO_STATUS_CODE");
        }
        if (status.equals(CloseStatus.POLICY_VIOLATION)) {
            System.out.println("CloseStatus.POLICY_VIOLATION");
        }
        if (status.equals(CloseStatus.PROTOCOL_ERROR)) {
            System.out.println("CloseStatus.PROTOCOL_ERROR");
        }
        if (status.equals(CloseStatus.REQUIRED_EXTENSION)) {
            System.out.println("CloseStatus.REQUIRED_EXTENSION");
        }
        if (status.equals(CloseStatus.SERVER_ERROR)) {
            System.out.println("CloseStatus.SERVER_ERROR");
        }
        if (status.equals(CloseStatus.SERVICE_RESTARTED)) {
            System.out.println("CloseStatus.SERVICE_RESTARTED");
        }
        if (status.equals(CloseStatus.SESSION_NOT_RELIABLE)) {
            System.out.println("CloseStatus.SESSION_NOT_RELIABLE");
        }
        if (status.equals(CloseStatus.TLS_HANDSHAKE_FAILURE)) {
            System.out.println("CloseStatus.TLS_HANDSHAKE_FAILURE");
        }
        if (status.equals(CloseStatus.TOO_BIG_TO_PROCESS)) {
            System.out.println("CloseStatus.TOO_BIG_TO_PROCESS");
        }
        
        System.out.println("CloseStatus: " + status);
        
        logger.debug("Disconnect event [sessionId: " + sha.getSessionId() + " ]");
        System.out.println("Disconnect event [sessionId: " + event.getSessionId() + " ]");
    }
}
