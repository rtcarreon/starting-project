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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FileUploadController {
    @RequestMapping(value="/singleUpload")
    public String singleUpload(){
    	return "hello";
    }
    @RequestMapping(value="/singleSave", method=RequestMethod.POST )
    public @ResponseBody String singleSave(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc ){
    	System.out.println("File Description:"+desc);
    	String fileName = null;
    	if (!file.isEmpty()) {
            try {
                fileName = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                BufferedOutputStream buffStream = 
                        new BufferedOutputStream(new FileOutputStream(new File("C:/cp/" + fileName)));
                buffStream.write(bytes);
                buffStream.close();
                return "You have successfully uploaded " + fileName;
            } catch (Exception e) {
                return "You failed to upload " + fileName + ": " + e.getMessage();
            }
        } else {
            return "Unable to upload. File is empty.";
        }
    }
    @RequestMapping(value="/multipleUpload")
    public String multiUpload(){
    	return "hello";
    }
    @RequestMapping(value="/multipleSave", method=RequestMethod.POST )
    public @ResponseBody String multipleSave(@RequestParam("file") MultipartFile[] files){
    	String fileName = null;
    	String msg = "";
    	if (files != null && files.length >0) {
    		for(int i =0 ;i< files.length; i++){
	            try {
	                fileName = files[i].getOriginalFilename();
	                byte[] bytes = files[i].getBytes();
	                BufferedOutputStream buffStream = 
	                        new BufferedOutputStream(new FileOutputStream(new File("C:/cp/" + fileName)));
	                buffStream.write(bytes);
	                buffStream.close();
	                msg += "You have successfully uploaded " + fileName +"<br/>";
	            } catch (Exception e) {
	                return "You failed to upload " + fileName + ": " + e.getMessage() +"<br/>";
	            }
    		}
    		return msg;
        } else {
            return "Unable to upload. File is empty.";
        }
    }
    
    @RequestMapping(value = "/doUpload", method = RequestMethod.POST)
    public String handleFileUpload(HttpServletRequest request,
            @RequestParam CommonsMultipartFile[] fileUpload) throws Exception {
          
        if (fileUpload != null && fileUpload.length > 0) {
            for (CommonsMultipartFile aFile : fileUpload){
                String fileName = null;
                if (!aFile.isEmpty()) {
                    try {
                        fileName = aFile.getOriginalFilename();
                        byte[] bytes = aFile.getBytes();
                        BufferedOutputStream buffStream = 
                                new BufferedOutputStream(new FileOutputStream(new File("D:/CX1/" + fileName)));
                        buffStream.write(bytes);
                        buffStream.close();
                        System.out.println("You have successfully uploaded " + fileName);
                    } catch (Exception e) {
                        System.out.println("You failed to upload " + fileName + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("Unable to upload. File is empty.");
                }
                  
                System.out.println("Saving file: " + aFile.getOriginalFilename());
                 
                
                aFile.getOriginalFilename();
                aFile.getBytes();
                             
            }
        }
  
        return "admin";
    }  
    
    @RequestMapping(value="/asyncUpload", method=RequestMethod.POST/*, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE*/)
    @ResponseBody
    public ResponseEntity<String> asyncFileUpload(HttpServletRequest request,
            @RequestParam CommonsMultipartFile[] fileUpload) throws Exception {
    
          
        if (fileUpload != null && fileUpload.length > 0) {
            for (CommonsMultipartFile aFile : fileUpload){
                String fileName = null;
                if (!aFile.isEmpty()) {
                    try {
                        fileName = aFile.getOriginalFilename();
                        byte[] bytes = aFile.getBytes();
                        BufferedOutputStream buffStream = 
                                new BufferedOutputStream(new FileOutputStream(new File("D:/CX1/" + fileName)));
                        buffStream.write(bytes);
                        buffStream.close();
                        System.out.println("You have successfully uploaded " + fileName);
                    } catch (Exception e) {
                        System.out.println("You failed to upload " + fileName + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("Unable to upload. File is empty.");
                }
                  
                System.out.println("Saving file: " + aFile.getOriginalFilename());
                 
                
                aFile.getOriginalFilename();
                aFile.getBytes();
                             
            }
        }
  
        return new ResponseEntity<>("success", HttpStatus.OK);
    } 

    @RequestMapping(value="/asyncUpload2", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> asyncFileUpload2(HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest mpr = (MultipartHttpServletRequest)request;
        if(request instanceof MultipartHttpServletRequest) {
            CommonsMultipartFile f = (CommonsMultipartFile) mpr.getFile("fileUpload");
            List<MultipartFile> fs = (List<MultipartFile>) mpr.getFiles("fileUpload");
            System.out.println("f: " + f);
            
            for (String key : mpr.getParameterMap().keySet()) {
                System.out.println("Param Key: " + key + "\n Value: " + mpr.getParameterMap().get(key));
            }
            
            for (String key : mpr.getMultiFileMap().keySet()) {
                System.out.println("xParam Key: " + key + "\n xValue: " + mpr.getMultiFileMap().get(key));
            }
            
            
        }
        
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        Map<String, Object> parameterMap = new HashMap<>();
        Map<String, String> formfields = (Map<String, String>) parameterMap.get("formfields");
        
        //if(request.getContentType().contains("multipart/form-data")){ 
        
        if (ServletFileUpload.isMultipartContent(request)) {
            Map<String, Object> map = new HashMap<>();
            List<FileItem> filefields = new ArrayList<>();
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {       //contentType: "image/jpeg", isFormField: false, fileName: "Chrysanthemum.jpg"
                if (item.isFormField()) {
                    formfields.put(item.getFieldName(), item.getString());
                } else {
                    filefields.add(item);
                }
            }
            
            parameterMap.put("filefields", filefields);
        }
          

  
        return new ResponseEntity<>("success", HttpStatus.OK);
    } 
} 
