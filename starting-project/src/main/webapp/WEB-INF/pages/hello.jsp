<%@page session="false"%>

<%--@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@page session="true"--%>

<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/fmt.tld" prefix="fmt" %>

<html>
    <head>
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
        
        <script type="text/javascript">
            var metaTags = document.getElementsByTagName("meta");
            var token = "";
            var header = "";

            for (var i = 0; i < metaTags.length; i++) {
                if (metaTags[i].getAttribute("name") === '_csrf') {
                    token = metaTags[i].getAttribute("content");
                }

                if (metaTags[i].getAttribute("name") === '_csrf_header') {
                    header = metaTags[i].getAttribute("content");
                }
            }
            /**
              *
              * param
              * data :: parameters to be sent in the backend
              * onComplete :: function fallback when ajax succeed
              * onError :: function fallback when ajax failed
              * url :: path to controller
              * method :: 'GET', 'POST', 'DELETE'
              * isAsync :: true or false
              * onTimeout :: function fallback on timeout
              * timeout :: a value of 0 (which is the default) means there is no timeout.
              *
              * Ajax Builder
              * created by: Romer Carreon
              */
            var myAjax = {
                ajax : function(param) {
                    try {
                        param['isAsync'] = typeof param['isAsync'] !== 'undefined' ? param['isAsync'] : true;
                        param['method'] = typeof param['method'] !== 'undefined' ? param['method'] : 'GET';
                        var xhReq;
                        /*if (window.XMLHttpRequest) {
                            xhReq = new XMLHttpRequest();   // code for IE7+, Firefox, Chrome, Opera, Safari
                        } else {
                            xhReq = new ActiveXObject("Microsoft.XMLHTTP");
                        }*/

                        /* returns cross-browser XMLHttpRequest, or null if unable */
                        var XMLHttpFactories = [
                            function () {return new XMLHttpRequest()},
                            function () {return new ActiveXObject("Msxml2.XMLHTTP")},
                            function () {return new ActiveXObject("Msxml2.XMLHTTP.3.0")},
                            function () {return new ActiveXObject("Msxml2.XMLHTTP.6.0")},
                            function () {return new ActiveXObject("Msxml3.XMLHTTP")},
                            function () {return new ActiveXObject("Microsoft.XMLHTTP")}
                        ];

                        for (var i = 0; i < XMLHttpFactories.length; i++) {
                            try {
                                xhReq = XMLHttpFactories[i]();
                            } catch (e) {
                                continue;
                            }
                            break;
                        }

                           var queryString = [];
                           for (var key in param['data']) {
                               queryString.push(encodeURIComponent(key) + '=' + encodeURIComponent(param['data'][key]));
                           }

                           if (param["isAsync"]) {
                               param['timeout'] = typeof param['timeout'] !== 'undefined' ? param['timeout'] : 0;
                               //commenting IE bug 
                               //xhReq.timeout = param['timeout'];
                               xhReq.ontimeout = function () {
                                   if (typeof param['onTimeout'] !== 'undefined' && param['onTimeout'] !== null) {
                                       param['onTimeout']("time out error");
                                   } else {
                                       console.log("time out error");
                                   }
                               }

                               xhReq.onreadystatechange = function () {
                                   // 0	UNSENT              open() has not been called yet
                                   // 1	OPENED              send()has not been called yet
                                   // 2	HEADERS_RECEIVED    send() has been called, and headers and status are available.
                                   // 3	LOADING	Downloading; responseText holds partial data.
                                   // 4	DONE	The operation is complete.
                                   //success 200: "OK", 404: Page not found, ...
                                   if (xhReq.readyState === 4 && xhReq.status === 200) {
                                       param['onComplete'](xhReq.statusText, xhReq.responseText);      // responseText , response to the request as text, or null if the request was unsuccessful or has not yet been sent.
                                   }
                                   // on failure
                                   if (xhReq.readyState === 4 && xhReq.status !== 200) {
                                       if (typeof param['onError'] !== 'undefined' && param['onError'] !== null) {
                                           param['onError'](xhReq.statusText, xhReq.responseText);
                                       } else {
                                           console.log("Error status: " + xhReq.statusText + "\n Error response: " + xhReq.responseText);
                                       }
                                   }
                               }
                           }

                           if (!param["isAsync"]) {
                               if (xhReq.readyState === 4 && xhReq.status === 200) {
                                   param['onComplete'](xhReq.statusText, xhReq.responseText);      // responseText , response to the request as text, or null if the request was unsuccessful or has not yet been sent.
                               }

                               // on failure
                               if (xhReq.readyState === 4 && xhReq.status !== 200) {
                                   if (typeof param['onError'] !== 'undefined' && param['onError'] !== null) {
                                       param['onError'](xhReq.statusText, xhReq.responseText);
                                   } else {
                                       console.log("Error status: " + xhReq.statusText + "\n Error response: " + xhReq.responseText);
                                   }
                               }
                           }

                           if (param['method'] === 'POST') {
                               xhReq.open(param['method'], param['url'], param['isAsync']);
                               //xhReq.setRequestHeader('User-Agent','XMLHTTP/1.0');
                               xhReq.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                               xhReq.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                               xhReq.setRequestHeader(header, token);
                               xhReq.send((param['data'] !== null && param['data'] !== 'undefined') ? queryString.join('&') : null);
                           } else {
                               if (param['data'] !== 'undefined' && param['data'] !== null) {
                                   param['url'] = param['url'] + '?' + queryString.join('&');
                               }
                               xhReq.open(param['method'], param['url'], param['isAsync']);
                               xhReq.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                               xhReq.setRequestHeader(header, token);
                               xhReq.send();
                           }
                       } catch (e) {
                           if (typeof param['onError'] !== 'undefined' && param['onError'] !== null) {
                               param['onError']("Exception thrown: ", e);
                           } else {
                               console.log("Error status: Exception thrown. \n Error response: " + e);
                           }

                           alert("Alerting from ajax builder: " + e);
                       }
                   }
               }
               
            function responseEntityMap() {
                myAjax.ajax({
                    method: "POST",
                    onComplete: function(status, response){
                        console.log("***** ONCOMPLETE Response: " + response + "\n\n***** Status:" + status);
                        if (response !== null && response.length > 0) {
                            var jsonObject = eval("(" + response + ")");
                            var result = "\nvalue for key1: " + jsonObject.key1 + "\nvalue for key2: " + jsonObject.key2 +"\nvalue for key3: " + jsonObject.key3;
                            alert('Data: ' + result + '\nStatus: ' + status);
                        } else {
                            console.log("Unknown response from the server. Status:" + status);
                        }
                    }, 
                    onError: function(status, response){
                        console.log("***** ONERROR Response: " + response + "\n\n***** Status:" + status);
                    },
                    url: "/example/responseEntity"
                });
            }
            
            function responseEntityMapList() {
                myAjax.ajax({
                    method: "POST",
                    onComplete: function(status, response){
                        console.log("***** Response: " + response + "\n\n***** Status:" + status);
                        if (response !== null && response.length > 0) {
                            var jsonObjects = eval("(" + response + ")");
                            var result = "";
                            for (var obj in jsonObjects) {
                                result += "\n\nvalue for key1: " + obj.key1 + "\nvalue for key2: " + obj.key2 +"\nvalue for key3: " + obj.key3;
                            }
                            
                            alert('Data: ' + result + '\n\nStatus: ' + status);
                        } else {
                            console.log("Unknown response from the server. Status:" + status);
                        }
                    },   // function name
                    url: "/example/responseEntityList"
                });
            }
            
            function responseEntityMapParam() {
                myAjax.ajax({
                    method: "POST",
                    data: {
                        paramKey : "test key",
                        paramValue : "test value"
                    },
                    onComplete: function(status, response){
                        console.log("***** Response: " + response + "\n\n***** Status:" + status);
                        if (response !== null && response.length > 0) {
                            var jsonObject = eval("(" + response + ")");
                            var result = "\nvalue for key: " + jsonObject.key + "\nvalue for value: " + jsonObject.value;
                            alert('Data: ' + result + '\nStatus: ' + status);
                        } else {
                            console.log("Unknown response from the server. Status:" + status);
                        }
                    },   // function name
                    url: "/example/responseEntityMapParam"
                });
            }
            
            function responseEntityMapParamAndPathVariable() {
                myAjax.ajax({
                    method: "GET",
                    data: {
                        paramKey : "test key",
                        paramValue : "test value"
                    },
                    onComplete: function(status, response){
                        console.log("***** Response: " + response + "\n\n***** Status:" + status);
                        if (response !== null && response.length > 0) {
                            var jsonObject = eval("(" + response + ")");
                            var result = "\nvalue for keyPath: " + jsonObject.keyPath + "\nvalue for valuePath: " + jsonObject.valuePath;
                            result += "\nvalue for keyParam: " + jsonObject.keyParam + "\nvalue for valueParam: " + jsonObject.valueParam;
                            alert('Data: ' + result + '\n\nStatus: ' + status);
                        } else {
                            console.log("Unknown response from the server. Status:" + status);
                        }
                    },
                    url: "/example/responseEntityMapParamAndPathVariable/path_key/path_value"
                });
            }
            
            function webAsyncTask() {
                myAjax.ajax({
                    method: "POST",
                    data: {
                        paramKey : "test key",
                        paramValue : "test value"
                    },
                    onComplete: function(status, response){
                        console.log("***** Response: " + response + "\n\n***** Status:" + status);
                        if (response !== null && response.length > 0) {
                            var jsonObject = eval("(" + response + ")");
                            var result = "\nvalue for key: " + jsonObject.key + "\nvalue for value: " + jsonObject.value;
                            alert('Data: ' + result + '\n\nStatus: ' + status);
                        } else {
                            console.log("Unknown response from the server. Status:" + status);
                        }
                    },   // function name
                    url: "/example/webAsyncTask"
                });
            }
            
            function callable() {
                myAjax.ajax({
                    method: "POST",
                    onComplete: function(status, response){
                        console.log("***** Response: " + response + "\n\n***** Status:" + status);
                        if (response !== null && response.length > 0) {
                            var jsonObject = eval("(" + response + ")");
                            var result = "\nvalue for key1: " + jsonObject.key1 + "\nvalue for key2: " + jsonObject.key2 +"\nvalue for key3: " + jsonObject.key3;
                            alert('Data: ' + result + '\nStatus: ' + status);
                        } else {
                            console.log("Unknown response from the server. Status:" + status);
                        }
                    },   // function name
                    url: "/example/callable"
                });
            }
            
            function upload() {
                try {
                    var metaTags = document.getElementsByTagName("meta");
                    var token = "";
                    var header = "";
                    for (var i = 0; i < metaTags.length; i++) {
                        if (metaTags[i].getAttribute("name") === '_csrf') {
                            token = metaTags[i].getAttribute("content");
                        }

                        if (metaTags[i].getAttribute("name") === '_csrf_header') {
                            header = metaTags[i].getAttribute("content");
                        }
                    }
                    
                    //var boundary = this.generateBoundary();
                    var files = document.getElementById("file");
                    if ('files' in files) {
                        if (files.files.length === 0) {
                            message = "Please browse for one or more files.";
                            console.log("Message: " + message);
                        } else {
                            if (window.File && window.FileReader && window.FileList && window.Blob) {
                                
                                
                                
                            } else {
                                console.log('The File APIs are not fully supported by your browser.');
                                alert('The File APIs are not fully supported by your browser.');
                            }
                        }
                    }

                    
                    //var files = document.getElementsByName("uploadfile");
                    var client = new XMLHttpRequest();

                    client.addEventListener('progress', function(e) {
                        var done = e.position || e.loaded, total = e.totalSize || e.total;
                        console.log('xhr progress: ' + (Math.floor(done/total*1000)/10) + '%');
                    }, false);
                    if ( client.upload ) {
                        client.upload.onprogress = function(e) {
                            var done = e.position || e.loaded, total = e.totalSize || e.total;
                            console.log('xhr.upload progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done/total*1000)/10) + '%');
                        };
                    }
                    
                    /* Create a FormData instance */
                    //var formData = new FormData();
                    var formData = new FormData(document.getElementById('form'));
                    /* Add the file */ 
                    //formData.append("upload", files.files[0]);
                    formData.append("fileUpload", files.files[0]);
                    //formData.append("fileUpload", files.files[0], files.files[0].name);
                    //for (var i = 0; i < files.length; i++) {
                    //    formData.append("file" + i, files[i].files[0]);
                    //    console.log("File name: " + files[i].files[0].name);
                    //    console.log("File size: " + files[i].files[0].size);
                    //    console.log("Binary content: " + files[i].files[0].getAsBinary());
                    // console.log("Text content: " + files[i].files[0].getAsText(""));
                    // var img = document.getElementById("preview");
                    // img.src = file.getAsDataURL();
                    //}
                    
                    client.open("post", "/asyncUpload2?${_csrf.parameterName}=${_csrf.token}", true);
                    //client.open("post", "/asyncUpload", true);
                    //client.setRequestHeader("Content-Type", "multipart/form-data");
                    /*
                     * http://igstan.ro/posts/2009-01-11-ajax-file-upload-with-pure-javascript.html
                    * The value of the boundary doesn't matter as long as no other structure in
                    * the request contains such a sequence of characters. We chose, nevertheless,
                    * a pseudo-random value based on the current timestamp of the browser.
                    */
                    var boundary = "AJAX--------------" + (new Date).getTime();
                    //var boundary = this.generateBoundary();
                    //var contentType = file.files[0].type + "; boundary=" + boundary;
                    //alert(contentType);
                    //var contentType = "multipart/form-data; boundary=" + boundary + ";";
                    //var contentType = "multipart/form-data; boundary=" + boundary + ";";
                    var contentType = "application/octet-stream; boundary=" + boundary + ";";
                    client.setRequestHeader("Content-Type", contentType);
                    //client.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    //client.setRequestHeader("Accept","application/json");
                    //client.setRequestHeader("Content-Type", "application/json");
                    //client.setRequestHeader("Connection", "Keep-Alive");
                    client.setRequestHeader(header, token);
                    //client.setRequestHeader("Content-Disposition", "form-data; name=\"fileUpload\"; filename=\"" + files.files[0].name + "\"");
                    //client.setRequestHeader("Content-Type", "application/octet-stream");
                    //client.setRequestHeader("name", "fileUpload");
                    //client.setRequestHeader("filename", files.files[0].name + "\r\n");
                    //client.setRequestHeader("X-File-Name", files.files[0].name);
                    //client.setRequestHeader("X-File-Size", files.files[0].size);
                    //client.setRequestHeader("X_FILENAME", files.files[0].fileName);
                    //client.setRequestHeader("X-File-Type", files.files[0].type);
                    //client.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                    //client.setRequestHeader('X-CSRF-Token', token);
                    
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        var rawData  = e.target.result;
                        client.send(rawData); 
                        
                    };
                    reader.readAsDataURL(files.files[0]);
                    
                    //client.send(formData);  /* Send to server */ 
                    //client.send(files.files[0]);  /* Send to server */ 
                    //client.sendAsBinary(formData);  /* Send to server */ 
                    client.onreadystatechange = function() {
                        if (client.readyState === 4 && client.status === 200) {
                           alert("Status:" + client.statusText + "\nResponse:" +  client.responseText);
                        }
                     }
                    //alert("upload:" + file.files[0]);
                } catch(e) {
                    alert("exception:" + e);
                }
            }
            
            function formSubmit(e) {
                e.preventDefault(); //This will prevent the default click action.
                
                return false;
            }
            
            function fileSelected() {
                var file = document.getElementById('file').files[0];
                if (file) {
                    var fileSize = 0;
                    if (file.size > 1024 * 1024)
                        fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
                    else
                        fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';

                    document.getElementById('fileName').innerHTML = 'Name: ' + file.name;
                    document.getElementById('fileSize').innerHTML = 'Size: ' + fileSize;
                    document.getElementById('fileType').innerHTML = 'Type: ' + file.type;
                }
            }
            
            function cancelUpload() {
                // recreate iframe to cancel upload
                document.getElementById("formSending").innerHTML = "<iframe name='uploadFrame' ></iframe>";
            }
            
//            addChangeListenerToFile();
//            function addChangeListenerToFile() {
//                var file = document.getElementById('file').files[0];
//                file.addEventListener("change", handleFileChanges, false);
//            }
//            
//            function handleFileChanges() {
//                alert("handleFileChanges");
//            }
            
            function uploadFile() {
                var fd = new FormData();
                fd.append("fileToUpload", document.getElementById('fileUpload').files[0]);
                var xhr = new XMLHttpRequest();
                xhr.upload.addEventListener("progress", uploadProgress, false);
                xhr.addEventListener("load", uploadComplete, false);
                xhr.addEventListener("error", uploadFailed, false);
                xhr.addEventListener("abort", uploadCanceled, false);
                xhr.open("POST", "/asyncUpload2?${_csrf.parameterName}=${_csrf.token}");
                xhr.send(fd);
              }

              function uploadProgress(evt) {
                if (evt.lengthComputable) {
                  var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                  document.getElementById('progressNumber').innerHTML = percentComplete.toString() + '%';
                }
                else {
                  document.getElementById('progressNumber').innerHTML = 'unable to compute';
                }
              }

              function uploadComplete(evt) {
                /* This event is raised when the server send back a response */
                alert(evt.target.responseText);
              }

              function uploadFailed(evt) {
                alert("There was an error attempting to upload the file.");
              }

              function uploadCanceled(evt) {
                alert("The upload has been canceled by the user or the browser dropped the connection.");
              }
              
//              $(function () {
//                var token = $("meta[name='_csrf']").attr("content");
//                var header = $("meta[name='_csrf_header']").attr("content");
//                $(document).ajaxSend(function(e, xhr, options) {
//                    alert();
//                    xhr.setRequestHeader(header, token);
//                });
//              });
        </script>
    </head>
<body>
    <div class="container">
        <h1>Hello Page!</h1>
        <p>
          Hello <b><c:out value="${pageContext.request.remoteUser}"/></b>
        </p>
    </div>
    <h1>Title : ${title}</h1>	
    <h1>Message : ${message}</h1>
    
    <c:url var="logoutUrl" value="/logout"/>
    <form class="form-inline" action="${logoutUrl}" method="post">
      <input type="submit" value="Log out" />
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>

    <!--
    Note:
        isRememberMe() - Returns true if the current principal is a remember-me user
        isFullyAuthenticated() - Returns true if the user is not an anonymous or a remember-me user
    -->
    <%--sec:authorize access="isRememberMe()">
        <h2># This user is login by "Remember Me Cookies".</h2>
    </sec:authorize>

    <sec:authorize access="isFullyAuthenticated()">
        <h2># This user is login by username / password.</h2>
    </sec:authorize--%>
    
    <form method="POST" action="/singleSave" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        File to upload: <input type="file" name="file"><br /> 
        Name: <input type="text" name="name"><br /> <br /> 
        <input type="submit" value="Upload"> Press here to upload the file!
        
    </form>
    
    <!--form method="POST" action="/multipleSave" enctype="multipart/form-data"-->
    <!--form method="POST" action="/doUpload" enctype="multipart/form-data" target="formSending"-->
    <form id="form" method="POST" action="/doUpload?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data" target="formSending">
        <!--input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/-->
        File1 to upload: <input type="file" id="file" name="fileUpload" onchange="fileSelected()"><br /> 
        Name1: <input type="text" name="name"><br /> <br /> 
        File2 to upload: <input type="file" name="fileUpload"><br /> 
        Name2: <input type="text" name="name"><br /> <br />
        <input type="submit" value="Upload"> Press here to upload the file!
    </form>
        <input type="button" value="Async Upload" onclick="upload();">
        <input type="button" value="Cancel Upload" onclick="cancelUpload();">
        <div id="fileName">fileName</div>
        <div id="fileSize">fileSize</div>
        <div id="fileType">fileType</div>
        <iframe id="formSending" name="formSending" style="display: none;"></iframe>
        
        <div><a href="javascript:responseEntityMap();">Async demo ResponseEntity&lt;String&gt; return single  map</a></div>
        <div><a href="javascript:responseEntityMapList();">Async demo ResponseEntity&lt;String&gt; return list of map</a></div>
        <div><a href="javascript:responseEntityMapParam();">Async demo ResponseEntity&lt;String&gt; with parameters</a></div>
        <div><a href="javascript:responseEntityMapParamAndPathVariable();">Async demo ResponseEntity&lt;String&gt; with path variables and parameters</a></div>
        <div><a href="javascript:webAsyncTask();">Async demo WebAsyncTask&lt;String&gt; with parameters</a></div>
        <div><a href="javascript:callable();">Async demo Callable&lt;String&gt;</a></div>
</body>
</html>