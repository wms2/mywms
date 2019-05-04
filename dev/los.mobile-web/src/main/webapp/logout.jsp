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
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@page language="java" %>
<%@page session="false"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>LOS</title>
    <jsp:useBean id="JSPLogoutBeanID" scope="request" class="de.linogistix.mobile.common.gui.jsp.JSPLogoutBean" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>

    <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />
    

    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">                    

        <f:view>
  
            <f:loadBundle basename ="de.linogistix.mobile.res.Bundle" var="bundle" />    
                  
            <form method="post" action="/los-mobile/" class="form">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.Information}" styleClass="pagetitle" />
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <%--<p id="pForm">--%>
                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td width="0" > <h:graphicImage id="infoImg" url="/pics/information.gif" /></td>                                                                                                            
                            <th scope="col">&nbsp;</th>
                            <td width="100%"><h:outputText id="message" value="#{bundle.LogoutMessage}" escape="false" styleClass="label"/></td>
                        </tr>
                        <tr>
                            <td colspan="3">
                            </td>
                        </tr>
                    </table>
                </div>                
                    
                <%-- Command Buttons --%>
				<div class="buttonbar" >
					<input type="submit" 
						id="button1"
						value=<jsp:getProperty name="JSPLogoutBeanID" property="login"/>  
						class="commandButton"/> 
				</div>
                    
            </form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('button1').focus();
            }    
            
        </script>

    </body>
</html>


