<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--@page session="true"--%>
<html>
    <head>
        <script src="resources/sockjs-0.3.4.js"></script>
        <script src="resources/stomp.js"></script>
        <script type="text/javascript">
            var UUID = function(a,b){for(b=a='';a++<36;b+=a*51&52?(a^15?8^Math.random()*(a^20?16:4):4).toString(16):'-');return b};
            var STOMP_CLIENT = null;
            var STOMP_FILE_CLIENT = null;
            var CURRENT_FILE_CHUNKS = [];
            var CURRENT_FILES = [];
            var FILE_READER = null;

            /**
             * This function is limited to small files
             * @returns
             */
            function prepareShareImageFile() {
                //alert(UUID());
                var files = document.getElementById("fileImageShare");
                var message = "";
                if ('files' in files) {
                    if (files.files.length === 0) {
                        message = "Please browse for one or more files.";
                    } else {
                        for (var i = 0; i < files.files.length; i++) {
                            if (window.File && window.FileReader && window.FileList && window.Blob) {
                                var fileReader = new FileReader();
                                fileReader.loadend = function() {
                                    alert('fileReader.loadend');
                                };
                                fileReader.onload = function(e) { 
                                    var rawData  = e.target.result;
                                    /*alert( "Got the file.n" 
                                          +"name: " + e.target.name + "n"
                                          //+"type: " + files.files[i].type + "n"
                                          +"size: " + e.target.size + " bytesn"
                                          + "starts with: " + rawData.substr(1, rawData.indexOf("n"))
                                    );*/

                                    STOMP_CLIENT.send("/appName/imageFileShareMapping", {}, rawData);
                                    // use this with fileReader.readAsDataURL(files.files[i]);
                                    //document.getElementById("calculationDiv").innerHTML += '<img src="' + rawData + '"/>';
                                };
                                fileReader.readAsDataURL(files.files[i]);
                                
                                
                                //fileReader.readAsArrayBuffer(files.files[i]);
                                // java code using the above code: see http://www.acnenomor.com/87033p1/file-upload-using-java-websocket-api-and-javascript
                                // http://www.acnenomor.com/84313p1/websocket-file-upload-speed-issue-java-websocket-api-and-javascript


                                //fileReader.readAsText(files.files[i]);
                                //fileReader.readAsBinaryString(file);
                            } else {
                                alert('The File APIs are not fully supported by your browser.');
                            }

                            message += "<br /><b>" + (i+1) + ". file</b><br />";
                            var file = files.files[i];
                            if ('name' in file) {
                                message += "name: " + file.name + "<br />";
                            } else {
                                message += "name: " + file.fileName + "<br />";
                            }
                            if ('size' in file) {
                                message += "size: " + file.size + " bytes <br />";
                            } else {
                                message += "size: " + file.fileSize + " bytes <br />";
                            }
                            if ('mediaType' in file) {
                                message += "type: " + file.mediaType + "<br />";
                            }
                        }
                    }
                } 
                else {
                    if (files.value === "") {
                        message += "Please browse for one or more files.";
                        message += "<br />Use the Control or Shift key for multiple selection.";
                    } else {
                        message += "Your browser doesn't support the files property!";
                        message += "<br />The path of the selected file: " + files.value;
                    }
                }
            }
            
            /**
             * Split base64 image into chunks
             * @returns array of base64 image chunks
             */
            function base64Chunks(str, len) {
                var _size = Math.ceil(str.length/len),
                    _ret  = new Array(_size),
                    _offset
                ;

                for (var _i=0; _i<_size; _i++) {
                    _offset = _i * len;
                    _ret[_i] = str.substring(_offset, _offset + len);
                }

                return _ret;
            }
            
            /**
             * Transfer single or multiple files using web socket
             * - populate CURRENT_FILES constant with file/s currently selected
             * - verify current user support on FileReader
             * @returns
             */
            function prepareShareFile() {
                console.log("prepareShareFile()");
                var files = document.getElementById("fileImageShare");
                var message = "";
                if ('files' in files) {
                    if (files.files.length === 0) {
                        message = "Please browse for one or more files.";
                        console.log("Message: " + message);
                    } else {
                        if (window.File && window.FileReader && window.FileList && window.Blob) {
                            for (var i = 0; i < files.files.length; i++) {
                                CURRENT_FILES.push(files.files[i]);
                            }
                             //CURRENT_FILES = files.files;
                             console.log("Calling connectWSFile()");
                             connectWSFile();
                        } else {
                            console.log('The File APIs are not fully supported by your browser.');
                            alert('The File APIs are not fully supported by your browser.');
                        }
                    }
                } 
                else {
                    if (files.value === "") {
                        message += "Please browse for one or more files.";
                        message += "<br />Use the Control or Shift key for multiple selection.";
                    } else {
                        message += "Your browser doesn't support the files property!";
                        message += "<br />The path of the selected file: " + files.value;
                    }
                }
            }
            
            /**
             * Show/Hide html element
             * @param connected     true = connected, false = disconnected
             * @returns
             */
            function setConnected(connected) {
                document.getElementById('connect').disabled = connected;
                document.getElementById('disconnect').disabled = !connected;
                document.getElementById('calculationDiv').style.visibility = connected ? 'visible' : 'hidden';
                document.getElementById('calResponse').innerHTML = '';
            }
            
            /**
             * Create connection to Stomp Client and perform file transfer
             * - transfer is done 1 at a time
             * - on every file, a connection to Stomp Client is created and destroyed when file transfer is complete
             * @returns
             */
            function connectWSFile() {
                console.log('connectWSFile()');
                var fileId = UUID();
                console.log('fileId: ' + fileId);
                var file = CURRENT_FILES.shift();//alert(file.slice(0, 5 + 1));return;
                if (typeof file === 'undefined') {
                    return;
                }
                
                FILE_READER = new FileReader();
                // If we use onloadend, we need to check the readyState.
                FILE_READER.loadend = function(evt) {
                    alert('FILE_READER.loadend');
                    if (evt.target.readyState == FileReader.DONE) { // DONE == 2
                        alert('FILE_READER.loadend : ' + evt.target.result);
                    }
                };
                
                FILE_READER.onloadstart = function(evt) {
                    alert('FILE_READER.onloadstart');
                };
                
                FILE_READER.onprogress = function(evt) {
                    // evt is an ProgressEvent.
                    if (evt.lengthComputable) {
                      var percentLoaded = Math.round((evt.loaded / evt.total) * 100);
                      // Increase the progress bar length.
                      if (percentLoaded < 100) {
                        //progress.style.width = percentLoaded + '%';
                        //progress.textContent = percentLoaded + '%';
                        console.log('Progress: ' + percentLoaded + '%');
                      }
                    }
                };
                
                FILE_READER.onerror  = function(evt) {
                    switch(evt.target.error.code) {
                        case evt.target.error.NOT_FOUND_ERR:
                          alert('File Not Found!');
                          break;
                        case evt.target.error.NOT_READABLE_ERR:
                          alert('File is not readable');
                          break;
                        case evt.target.error.ABORT_ERR:
                          alert('evt.target.error.ABORT_ERR');
                          break; // noop
                        default:
                          alert('An error occurred reading this file.');
                    };
                };
                
                FILE_READER.onload = function(e) { 
                    console.log('FILE_READER.onload');
                    var rawData  = e.target.result;
                    console.log('rawData: ' + rawData);
                    var arr = base64Chunks(rawData, 65400);//12991
                    for (var i = 0; i < arr.length; i++) {
                        CURRENT_FILE_CHUNKS.push(arr[i]);
                    } 
                    console.log('CURRENT_FILE_CHUNKS ' + CURRENT_FILE_CHUNKS);
                    var options = {protocols_whitelist: ["websocket", "xhr-streaming", "xdr-streaming", "xhr-polling", "xdr-polling", "iframe-htmlfile", "iframe-eventsource", "iframe-xhr-polling"], debug: true};
                    var socket = new SockJS("<c:url value='/ws/fileShareMapping'/>", undefined, options);
                    socket.addEventListener('open', function () {
                        console.log('socket addEventListener open ');
                    });

                    // callback function to be called when stomp client is connected to server (see Note 2)
                    var connectCallback = function(frame) {
                        console.log('Connected: ' + frame);

                        STOMP_FILE_CLIENT.subscribe('/topic/fileShareResponse/' + fileId, function(calResult){
                            if (calResult.body === "1") {
                                console.log('calResult.body === 1 sending another chunk');
                                var chunk = CURRENT_FILE_CHUNKS.shift();
                                console.log('chunk to send: ' + chunk);
                                sendFileChunks(STOMP_FILE_CLIENT, fileId, (typeof chunk === 'undefined') ? "" : chunk);
                            }

                            if (calResult.body === "0") {
                                console.log('calResult.body === 0 disconnecting');
                                STOMP_FILE_CLIENT.disconnect();
                                // TODO start a new file to upload
                                setTimeout(function(){ connectWSFile(); }, 1000);
                                 
                            }
                        });
                        
                        var chunk = CURRENT_FILE_CHUNKS.shift();
                        console.log('1st chunk to send: ' + chunk);
                        setTimeout(function(){
                            sendFileChunks(STOMP_FILE_CLIENT, fileId, (typeof chunk === 'undefined') ? "" : chunk);
                        }, 500);
                    }; 

                    var errorCallback = function(error) {
                         // display the error's message header:
                         alert(error.headers.message);
                    };
                    console.log('Stomp client connecting... ');
                    STOMP_FILE_CLIENT = Stomp.over(socket);
                    STOMP_FILE_CLIENT.connect({username: "${pageContext.request.userPrincipal.name}"}, connectCallback, errorCallback);
                };
                console.log('FILE_READER.readAsDataURL(file)');
                FILE_READER.readAsDataURL(file); 
                //FILE_READER.readAsArrayBuffer(file);
                //FILE_READER.readAsBinaryString(file);     //deprecated.
            }
            
            /**
             * Send a base 64 chunk using web socket
             * @param stomp         Stomp Client instance 
             * @param fileId        UUID of file being transfered
             * @param chunk         base 64 chunk to be transferred
             * @returns
             */
            function sendFileChunks(stomp, fileId, chunk) {
                stomp.send("/appName/fileShareMapping", {}, fileId + chunk);
            }
            
            /**
             * Create connection to Stomp Client and subscribe to available messaging service
             * @returns
             */
            function connect() {
                var options = {protocols_whitelist: ["websocket", "xhr-streaming", "xdr-streaming", "xhr-polling", "xdr-polling", "iframe-htmlfile", "iframe-eventsource", "iframe-xhr-polling"], debug: true};
                var socket = new SockJS("<c:url value='/ws/messageMapping'/>", undefined, options);
                socket.addEventListener('open', function () {
                    console.log('socket addEventListener open ');
		});
                
                var connectCallback = function(frame) {
                    setConnected(true);
                    console.log('Connected: ' + frame);
                    STOMP_CLIENT.subscribe('/topic/showResult', function(calResult){
                            messageMappingResult(calResult.body);
                    });
                    
                    STOMP_CLIENT.subscribe('/topic/showResult/12345-dest-var', function(calResult){
                            messageMappingTargetResult(calResult.body);
                    });
                    
                    STOMP_CLIENT.subscribe('/user/topic/showResult', function(calResult){
                            messageMappingTargetUser(calResult.body);
                    });
                    
                    STOMP_CLIENT.subscribe('/user/topic/showResult/12345-dest-var', function(calResult){
                            messageMappingTargetUserTargetResult(calResult.body);
                    });

                    STOMP_CLIENT.subscribe('/topic/imageFileShareResponse', function(calResult){
                            document.getElementById("calculationDiv").innerHTML += '<img src="' + calResult.body + '"/>';
                    });

                    STOMP_CLIENT.subscribe('/topic/fileShareResponse', function(calResult){alert();
                            document.getElementById("calculationDiv").innerHTML += '<img src="' + calResult.body + '"/>';
                    });
                }; 

                var errorCallback = function(error) {
                     alert(error.headers.message);
                };

                //var socket = new SockJS("<c:url value='/messageMapping'/>");
                STOMP_CLIENT = Stomp.over(socket);
                STOMP_CLIENT.connect({username: "${pageContext.request.userPrincipal.name}"}, connectCallback, errorCallback);
                //STOMP_CLIENT.connect({}, function(frame) {
//                STOMP_CLIENT.connect({username: "${pageContext.request.userPrincipal.name}"}, function(frame) {
//                    
//                });
                
//                var socket2 = new SockJS("<c:url value='/messageMappingSpecificUser'/>");
//                            STOMP_CLIENTToUser = Stomp.over(socket2);
//                STOMP_CLIENTToUser.connect({}, function(frame) {
//                    setConnected(true);
//                    console.log('Connected: ' + frame);
//                    STOMP_CLIENTToUser.subscribe('/user/topic/showResult', function(calResult){
//                            showResult(calResult);
//                    });
//                    
//                    STOMP_CLIENTToUser.subscribe('/user/topic/showResult/12345-dest-var', function(calResult){
//                            showResult(calResult);
//                    });
//                });
            }
            
            /**
             * Disconnect from current Stomp Client
             * @type Arguments
             */
            function disconnect() {
                STOMP_CLIENT.disconnect();
                setConnected(false);
                console.log("Disconnected");
            }
            
            /**
             * Send a message to "/appName/messageMapping"
             */
            function messageMapping() {
                var messageText = document.getElementById('message-text').value;
                STOMP_CLIENT.send("/appName/messageMapping", {}, messageText);
            }
            
            /**
             * Send a message to "/appName/messageMapping2"
             */
            function messageMapping2() {
                var messageText = document.getElementById('message-text').value;
                STOMP_CLIENT.send("/appName/messageMapping2", {}, messageText);
            }
            
            /**
             * Send a message to "/appName/messageMapping2/12345-dest-var"
             */
            function messageMapping2PathVariable() {
                var messageText = document.getElementById('message-text').value;
                STOMP_CLIENT.send("/appName/messageMapping2/12345-dest-var", {}, messageText);
            }
            
            /**
             * Send a message to "/appName/messageMappingSpecificUser"
             * This mapping targets a specific user. Only the traget user will receive the message
             */
            function messageMappingSpecificUser() {
                var messageText = document.getElementById('message-text').value;
                STOMP_CLIENT.send("/appName/messageMappingSpecificUser", {}, messageText);
            }
            
            /**
             * Send a message to "/appName/messageMappingSpecificUser/12345-dest-var"
             * This mapping targets a specific user. Only the traget user will receive the message
             */
            function messageMappingSpecificUserPathVariable() {
                var messageText = document.getElementById('message-text').value;
                STOMP_CLIENT.send("/appName/messageMappingSpecificUser/12345-dest-var", {}, messageText);
            }
            
//            function showResult(message) {
//                var str = ''; 
//                for (var p in message) { 
//                    if (message.hasOwnProperty(p)) { 
//                        str += p + '::' + message[p] + '\n'; 
//                    } 
//                } 
//                //alert(str);
//                var response = document.getElementById('calResponse');
//                var p = document.createElement('p');
//                p.style.wordWrap = 'break-word';
//                p.appendChild(document.createTextNode(message.body));
//                response.appendChild(p);
//            }

            function messageMappingResult(message) {
                var response = document.getElementById('calResponse');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.appendChild(document.createTextNode(message));
                response.appendChild(p);
            }
            
            function messageMappingTargetResult(message) {
                var response = document.getElementById('calResponse');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.appendChild(document.createTextNode(message));
                response.appendChild(p);
            }
            
            function messageMappingTargetUser(message) {
                var response = document.getElementById('calResponse');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.appendChild(document.createTextNode(message));
                response.appendChild(p);
            }
            
            function messageMappingTargetUserTargetResult(message) {
                var response = document.getElementById('calResponse');
                var p = document.createElement('p');
                p.style.wordWrap = 'break-word';
                p.appendChild(document.createTextNode(message));
                response.appendChild(p);
            }
            
            /**
             * http://www.html5rocks.com/en/tutorials/file/dndfiles/
             * on change of browse files Button
             * @returns
             */
            function addFilesToList() {
                var files = document.getElementById("fileImageShare");
                var message = "";
                if ('files' in files) {
                    if (files.files.length === 0) {
                        message = "Please browse for one or more files.";
                        console.log("Message: " + message);
                    } else {
                        if (window.File && window.FileReader && window.FileList && window.Blob) {
                            for (var i = 0; i < files.files.length; i++) {
                                var file = files.files[i]
                                CURRENT_FILES.push(file);
                                var fileName = null;
                                var fileSize = null;
                                if ('name' in file) {
                                    fileName = file.name;
                                } else {
                                    fileName = file.fileName;
                                }
                                if ('size' in file) {
                                    fileSize = file.size;
                                } else {
                                    fileSize = file.fileSize;
                                }
                                
                                var start = 0;
                                var stop = fileSize - 1;
                                var fileReader = new FileReader();
                                
                                fileReader.onerror = function(evt) {
                                    alert('File read onerror');
                                };
                                
                                fileReader.onprogress = function(evt) {
                                    alert('File read onprogress');
                                };
                                
                                fileReader.onabort = function(e) {
                                    alert('File read cancelled');
                                };
                                
                                fileReader.onloadstart = function(e) {
                                    alert('File read onloadstart');
                                };
                        
                                fileReader.loadend = function(evt) {
                                    alert('File read loadend');
                                    if (evt.target.readyState == FileReader.DONE) { // DONE == 2
                                        alert();
                                        document.getElementById('byte_content').textContent = evt.target.result;
                                        document.getElementById('byte_range').textContent = 
                                            ['Read bytes: ', start + 1, ' - ', stop + 1,
                                             ' of ', file.size, ' byte file'].join('');
                                    }
                                };
                                fileReader.onload = function(e) {
                                    var rawData  = e.target.result;
                                    
                                    var html = document.getElementById("list-of-files").innerHTML;
                                    html += "<tr>"
                                            +   "<td style=\"padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;\">"
                                            +       "<span><img src=\"" + rawData + "\" width=\"80\" height=\"60\"/></span>"
                                            +   "</td>"
                                            +   "<td style=\"padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;\">"
                                            +       "<p>" + fileName + "</p>"
                                            +       "<strong></strong>"
                                            +   "</td>"
                                            +   "<td style=\"padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;\">"
                                            +       "<p class=\"size\">" + fileSize + " bytes</p>"
                                            +   "<div style=\"height: 20px;margin-bottom: 20px;overflow: hidden;background-color: #f5f5f5;border-radius: 4px;\">"
                                            +   "<div style=\"width:0%;\"></div></div>"
                                            +   "</td>"
                                            +   "<td style=\"padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;\">"
                                            +       "<button class=\"btn btn-primary start\">"
                                            +           "<i></i>"
                                            +           "<span>Start</span>"
                                            +       "</button>"
                                            +       "<button class=\"btn btn-warning cancel\">"
                                            +           "<i class=\"glyphicon glyphicon-ban-circle\"></i>"
                                            +           "<span>Cancel</span>"
                                            +   "</button>"
                                            +   "</td>"
                                            + "</tr>";
                                     document.getElementById("list-of-files").innerHTML = html;
                                };
                                fileReader.readAsDataURL(file);
                            }
                        } else {
                            console.log('The File APIs are not fully supported by your browser.');
                            alert('The File APIs are not fully supported by your browser.');
                        }
                    }
                }              
            }
            
        </script>
    </head>
<body>
    
    <div class="container">
        <h3>Admin Page!</h3>
        <p>
          Hello <b><c:out value="${pageContext.request.remoteUser}"/></b>
        </p>
    </div>
        
    <h4>Title : ${title}</h4>
    <h4>Message : ${message}</h4>
    
     <c:url var="logoutUrl" value="/logout"/>
    <form class="form-inline" action="${logoutUrl}" method="post">
      <input type="submit" value="Log out" />
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>

    <c:if test="${pageContext.request.userPrincipal.name != null}">
            <h4>Welcome : ${pageContext.request.userPrincipal.name} | <a href="<c:url value="/logout" />" > Logout</a></h4>  
    </c:if>

    <h3>App Using Spring 4 WebSocket</h3>
    <div>
        <div>
            <button id="connect" onclick="connect();">Connect</button>
            <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button><br/><br/>
        </div>
        <div>
            <!-- The multiple attribute of the input tag is not supported in Internet Explorer 9 and earlier versions. -->
            <input type="file" id="fileImageShare" multiple onchange="addFilesToList()"/>
            <input type="button" value="Upload selected" onclick="javascript:prepareShareImageFile();"/>
            <input type="button" value="Upload all" onclick="javascript:prepareShareFile();"/>
        </div>
        
        
        
        <table style="width: 100%;max-width: 100%;margin-bottom: 20px;background-color: transparent;border-spacing: 0;border-collapse: collapse;display:table;">
            <tbody id="list-of-files" style="display:table-row-group; vertical-align:middle; border-color: inherit;">
                <!--tr style="opacity: 1;">
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <span class="preview"><img src="" width="80" height="60"/></canvas></span>
                    </td>
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <p class="name">Lighthouse.jpg</p>
                        <strong class="error text-danger"></strong>
                    </td>
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <p class="size">561.28 KB</p>
                        <div style="height: 20px;margin-bottom: 20px;overflow: hidden;background-color: #f5f5f5;border-radius: 4px;"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
                    </td>
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <button class="btn btn-primary start">
                            <i class="glyphicon glyphicon-upload"></i>
                            <span>Start</span>
                        </button>
                        <button class="btn btn-warning cancel">
                            <i class="glyphicon glyphicon-ban-circle"></i>
                            <span>Cancel</span>
                        </button>
                    </td>
                </tr>
                <tr  style="opacity: 1;">
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <span class="preview"><img src="" width="80" height="60"></canvas></span>
                    </td>
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <p class="name">Penguins.jpg</p>
                        <strong class="error text-danger"></strong>
                    </td>
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <p class="size">777.84 KB</p>
                        <div style="height: 20px;margin-bottom: 20px;overflow: hidden;background-color: #f5f5f5;border-radius: 4px;"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
                    </td>
                    <td style="padding: 8px;line-height: 1.42857143;vertical-align: top;border-top: 1px solid #ddd;">
                        <button class="btn btn-primary start">
                            <i class="glyphicon glyphicon-upload"></i>
                            <span>Start</span>
                        </button>
                        <button class="btn btn-warning cancel">
                            <i class="glyphicon glyphicon-ban-circle"></i>
                            <span>Cancel</span>
                        </button>
                    </td>
                </tr-->
            </tbody>
        </table>
        
        
        
        <div id="container" style="width:50%; height:18px;border: 1px solid black;">
            <div id="progress-bar-text"></div><div id="progress-bar" style="width:0%; height:18px;background-color: green;"></div>
        </div>
        <div id="calculationDiv">
            <label>Message:</label><input type="text" id="message-text" /><br/>
            <button onclick="messageMapping();">Message Mapping</button>
            <br/>
            <button onclick="messageMapping2();">Message Mapping 2</button>
            <br/>
            <button onclick="messageMapping2PathVariable();">Message Mapping 2 Using Path Variable</button>
            <br/>
            <button onclick="messageMappingSpecificUser();">Message Mapping to User "admin1"</button>
            <br/>
            <button onclick="messageMappingSpecificUserPathVariable();">Message Mapping to User "admin1" using path variable</button>
            <br/>
            <p id="calResponse"></p>
        </div>
    </div>
</body>
</html>