<%-- 
  Copyright (c) 2006 - 2010 LinogistiX GmbH

  www.linogistix.com
  
  Project: myWMS-LOS
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%-- <%@ taglib prefix="f" uri="http://xmlns.jcp.org/jsf/core" %>
<%@ taglib prefix="h" uri="http://xmlns.jcp.org/jsf/jstl/html" %> --%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@page language="java" %>
<%--@page session="false"--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="expires" content="Mon, 01 Jan 1990 00:00:01 GMT">
   		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>LOS</title>
        <jsp:useBean id="JSPLoginBeanID" scope="request" class="de.linogistix.mobile.common.gui.jsp.JSPLoginBean" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <f:loadBundle basename ="de.linogistix.mobile.res.Bundle" var="bundle" />
    

    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">                    
        
        <%--            <f:view beforePhase="#{de.linogistix.mobile.common.listener.SessionPhaseListener.beforePhase}"> --%>
        <f:view>
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />             

             <form action='<%= response.encodeURL("/los-mobile/faces/login_redirect.jsp")%>' onsubmit="return checkInput()" method="POST" class="form">
 
                <%-- Page Title--%>
                <p id="pHeader" class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.Login}" styleClass="pagetitle" />
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <%--<p id="pForm">--%>
                
                <div class="space">                    
                    <jsp:getProperty name="JSPLoginBeanID" property="message"/>
                    
                    <table width="100%" style="padding-right:10px;" border="0" cellspacing="0" cellpadding="0"  >
                        <tr>
                            <td style="margin-left:5px">
                            <h:outputLabel  id="loginLabel" value="#{bundle.User}" styleClass="label" /> </td>
                        </tr>
                        <tr>
                            <td style="margin-left:5px"><input id="j_username2" name="j_username2" style="background-color: #ffffff;width:100%;" onblur="enableSend()"/></td>
                        </tr>
                        <tr>
                            <td width="50%" style="margin-left:5px"><h:outputLabel id="passwordLabel" value="#{bundle.Password}" styleClass="label" /></td>
                        </tr>
                        <tr>
                            <td style="margin-left:5px;">
                                
                                
                                <%--   <f:validateLength minimum="8" maximum="500"/> --%>                                       
                                
                                <input id="j_password2" name="j_password2" type="password" style="background-color: #ffffff;width:100%;"/>
                            </td>	
                        </tr>
                    </table>
                </div>
                <div class="buttonbar">
                    <%-- Command Buttons --%>
                    <input type="submit" 
                    		value=<jsp:getProperty name="JSPLoginBeanID" property="login"/>  
                    		class="commandButton"> 
                </div>                
            </form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
			}
            
    		function setFocus() {
				document.getElementById('j_username2').focus();
			}

            lock = true;
            function enableSend() {
                lock = false;
            }
            
            function checkInput() {
                if( lock ) {
                    lock = false;
                    document.getElementById('j_password2').focus();
                    return false;
                }
                return true;
            }
            
    		function doRedirect() {
			}    
            
        </script>


        <%
            boolean hasError = false;
            if (request.getQueryString() != null) {
                if (request.getQueryString().equals("errors=true")) {
                    hasError = true;
                    if (session != null) {
                        session.setAttribute("AUTHORIZED", null);
                    }    
                }
            }
            if (hasError == false) {
                if (request.getSession(false) != null) {
                    if (session != null) {
                        if (session.isNew() == false) {
                            if (session.getAttribute("AUTHORIZED") != null) {
                                response.sendRedirect("/los-mobile/faces/login_redirect.jsp");
                            }
                        }
                    }
                }
            }
        %>
        
    </body>
</html>

 
