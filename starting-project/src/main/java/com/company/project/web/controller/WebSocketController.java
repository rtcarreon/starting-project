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

package com.company.project.web.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.misc.BASE64Decoder;

/**
 *  http://www.concretepage.com/spring-4/spring-4-websocket-sockjs-stomp-tomcat-example
 * @author Romer
 */
@Controller
public class WebSocketController {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private static Map<String, StringBuilder> map = new HashMap<>();

    @MessageMapping("/imageFileShareMapping")
    @SendTo("/topic/imageFileShareResponse")
    public String imageFileShare(String param) throws Exception {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(param.split(",")[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        File outputfile = new File("D:/GemShelf/" + UUID.randomUUID() + ".jpg");
        ImageIO.write(image, "jpg", outputfile);

        return param;
    }
    
    @MessageMapping("/fileShareMapping")
    //@SendTo("/topic/fileShareResponse")
    public void fileShare(String param) throws Exception {
        // TODO detect file type
        String id = param.substring(0, 36); // extract prepend UUID which is 36 chars length
        String base64Chunk = param.substring(36, param.length());

        if (base64Chunk.length() == 0) {
            this.simpMessagingTemplate.convertAndSend("/topic/fileShareResponse/" + id, "0");
            this.simpMessagingTemplate.convertAndSend("/topic/imageFileShareResponse", map.get(id).toString());
            BufferedImage image = null;
            byte[] imageByte;
            try {
                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(map.get(id).toString().split(",")[1]);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File outputfile = new File("D:/GemShelf/" + UUID.randomUUID() + ".jpg");
            ImageIO.write(image, "jpg", outputfile);
            map.remove(id);
        } else {
            StringBuilder sb = map.get(id);
            if (sb == null) {
                sb = new StringBuilder();
                sb.append(base64Chunk);
                map.put(id, sb);
            } else {
                sb.append(base64Chunk);
            }
            this.simpMessagingTemplate.convertAndSend("/topic/fileShareResponse/" + id, "1");
        }
        
    }
    
    
    @MessageMapping("/messageMapping")
    @SendTo("/topic/showResult")
    public String messageMapping(String param) throws Exception {
        return "@SendTo. Param : " + param;
    }
    
    /**
     * Use of SimpMessagingTemplate
     * 
     * @param param 
     */
    @MessageMapping("/messageMapping2" )
    public void messageMapping2(String param) {
        this.simpMessagingTemplate.convertAndSend("/topic/showResult", "SimpMessagingTemplate. Param : " + param);
    }
    
    /**
     * Use of SimpMessagingTemplate and path variable
     * 
     * @param pathVariable
     * @param param 
     */
    @MessageMapping("/messageMapping2/{pathVariable}" )
    public void messageMapping2DestinationVariable(@DestinationVariable String pathVariable, String param) {
        this.simpMessagingTemplate.convertAndSend("/topic/showResult/" + pathVariable, "SimpMessagingTemplate with path variable (" + pathVariable + ") and parameter (" + param + "). Target specific result \"" + "/topic/showResult/" + pathVariable + "\"");
    }
        
    /**
     * Target specific user
     * - on client side code : <code>stompClientToUser.subscribe('/user/topic/showResult', function(calResult){..</code>
     * 
     * @param param
     * @throws Exception 
     */
    @MessageMapping("/messageMappingSpecificUser" )    
    public void messageMapping2User(String param) throws Exception {
        this.simpMessagingTemplate.convertAndSendToUser("admin1", "/topic/showResult", "Message will appear on target user (admin1) with param (" + param + ").");
    }
    
    /**
     * Target specific user
     * - on client side code : <code>stompClientToUser.send("/appName/messageMappingSpecificUser/dest_var", {}, num1);..</code>
     * 
     * @param pathVariable
     * @param param
     * @throws Exception 
     */
    @MessageMapping("/messageMappingSpecificUser/{pathVariable}" )    
    public void messageMapping2UserDestinationVariable(@DestinationVariable String pathVariable, String param) throws Exception {
        this.simpMessagingTemplate.convertAndSendToUser("admin1", "/topic/showResult/" + pathVariable, "Message will appear on target user(admin1) with param (" + param + "). Target specific result \"" + "/topic/showResult/" + pathVariable + "\"");
    }

    @RequestMapping("/start")
    public String start() {
        return "start";
    }
}
