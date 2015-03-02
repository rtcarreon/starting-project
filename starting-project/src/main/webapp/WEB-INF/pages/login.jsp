<%@page session="false"%>



<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/fmt.tld" prefix="fmt" %>

<html>
<body>
    <div class="container">
        <h1>Login Page! <fmt:message key="key.one"/></h1>
    </div>
    
    <!-- 
        Append the language
        http://localhost:8090/login?language=zh_CN 
    -->
    <br/>
    <p>Current Locale : ${pageContext.response.locale}</p>
    <fmt:message key="key.one"/>
    <br/>
    <fmt:message key="key.two"/>
    <br/>
    
    <c:url var="logoutUrl" value="/logout"/>
    <form class="form-inline" action="${logoutUrl}" method="post">
        <input type="submit" value="Log out" />
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    
    <div>
        <c:url value="/login" var="loginUrl"/>
        <form name="f" action="${loginUrl}" method="post">               
            <fieldset>
                <legend>Please Login</legend>
                <c:if test="${param.error != null}"> 
                    <div class="alert alert-error">    
                        Failed to login.
                        <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
                            Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
                        </c:if>
                    </div>
                </c:if>
                <c:if test="${param.logout != null}"> 
                    <div class="alert alert-success">    
                        You have been logged out.
                    </div>
                </c:if>
                <c:if test="${param.processing != null}"> 
                    <div class="alert alert-success">    
                        Processing.
                    </div>
                </c:if>    
                <label for="username">Username</label>
                <input type="text" id="username" name="username" value="admin"/>
                <br/>
                <label for="password">Password</label>
                <input type="password" id="password" name="password" value="admin"/>
                <br/>
                <label for="remember-me">Remember Me?</label>
                <input type="checkbox" id="remember-me" name="remember_me_checkbox"/>
                <br/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="form-actions">
                    <button type="submit" class="btn">Log in</button>
                </div>
            </fieldset>
        </form>
    </div>
</body>
</html>