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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>LOS</title>
        <%--    <link rel="stylesheet" type="text/css" href="pages/picking/stylesheet.css" />--%>
        <%--  <link rel="stylesheet" type="text/css" href="stylesheet.css" /> --%>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view>
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="successForm" styleClass="form">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{NotifyDescriptorExtBean.pageTitle}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                <%-- Form --%>
                <p></p>
                <%-- User (Driver) --%>   
                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td width="0" > <h:graphicImage id="infoImg" url="#{NotifyDescriptorExtBean.icon}" /></td>                                                                                                               
                            <th scope="col">&nbsp;</th>
                            <td width="100%"><h:outputText id="message" value="#{NotifyDescriptorExtBean.message}" escape="false" styleClass="label"/></td>
                        </tr>
                        <tr>
                        	<td width="0" > <h:outputText id="paramTextFieldLabel" value="" escape="false" styleClass="label"/></td>                   
                            <th scope="col">&nbsp;</th>
                            <td width="100%">
                            	<h:inputText id="paramTextField" 
                            		validator="#{NotifyDescriptorExtBean.paramValidator}" 
                            		value="#{NotifyDescriptorExtBean.param}" 
                            		styleClass="input" 
                            		rendered="#{NotifyDescriptorExtBean.visibleParam}"> 
	                            </h:inputText>
                            </td>
                        </tr>
                        <tr>
                    </table>
                </div>
                                
				<h:inputText value="IE-Dummy" style="display:none" rendered="#{NotifyDescriptorExtBean.visibleParam}"/>

                <div class="buttonbar">
                    <h:commandButton id="button1" value="#{NotifyDescriptorExtBean.button1Text}" action="#{NotifyDescriptorExtBean.button1ActionPerformedListener}" styleClass="commandButton"  rendered="#{NotifyDescriptorExtBean.visible1}" />
                    <h:commandButton id="button2" value="#{NotifyDescriptorExtBean.button2Text}" action="#{NotifyDescriptorExtBean.button2ActionPerformedListener}" styleClass="commandButton"  rendered="#{NotifyDescriptorExtBean.visible2}" />
                    <h:commandButton id="button3" value="#{NotifyDescriptorExtBean.button3Text}" action="#{NotifyDescriptorExtBean.button3ActionPerformedListener}" styleClass="commandButton"  rendered="#{NotifyDescriptorExtBean.visible3}" />                                        
                    <h:commandButton id="button4" value="#{NotifyDescriptorExtBean.button4Text}" action="#{NotifyDescriptorExtBean.button4ActionPerformedListener}" styleClass="commandButton"  rendered="#{NotifyDescriptorExtBean.visible4}" />
                    <h:commandButton id="button5" value="#{NotifyDescriptorExtBean.button5Text}" action="#{NotifyDescriptorExtBean.button5ActionPerformedListener}" styleClass="commandButton"  rendered="#{NotifyDescriptorExtBean.visible5}" />
                </div>
                    
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
               	if( document.getElementById('successForm:paramTextField') != null ) {
               		document.getElementById('successForm:paramTextField').focus();
               	}
               	else {
                   	document.getElementById('successForm:button1').focus();
               	}
            }    
            
        </script>

    </body>
</html>
