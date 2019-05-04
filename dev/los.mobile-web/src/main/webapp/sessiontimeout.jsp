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
        <%--    <link rel="stylesheet" type="text/css" href="pages/picking/stylesheet.css" />--%>
        <%--  <link rel="stylesheet" type="text/css" href="stylesheet.css" /> --%>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>

    <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />
    

    <body scroll="no" class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">                    

        <f:view>
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />             
            <form method="post" action="/los-mobile/">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                <h:outputText id="pagetitle" value="#{bundle.Information}" styleClass="pagetitle" />
                <%--<p id="pForm">--%>
                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td width="0" > <h:graphicImage id="infoImg" url="/pics/information.gif" /></td>                                                                                                            
                            <th scope="col">&nbsp;</th>
                            <td width="100%"><h:outputText id="message" value="#{bundle.SessionInvalidateMessage}" escape="false" styleClass="label"/></td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                    <div align="center" style="padding-top:5px" >
                                        <p class="buttonbar">
                                    <input type="submit" value=<jsp:getProperty name="JSPLogoutBeanID" property="login"/>  align="right" style="background-color: #ff9900;padding-top:10px;padding-bottom:10px;width:90px;"> 
                                    </div>
                            </td>
                        </tr>
                    </table>
                    
                    <%-- Command Buttons --%>
                    
                </div>                
            </form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {

            }    
            
        </script>

    </body>
</html>

<%--
  response.setHeader("Cache-Control","no-cache,post-check=0,pre-check=0");
  response.setHeader("Pragma","no-cache");
  response.setHeader("Expires","Thu,01Dec199416:00:00GMT");
--%>


