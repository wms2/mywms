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
    
    <body>
        <f:view>
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="cancelForm" styleClass="form">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.DIFFERENCE_AMOUNT}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                <p id="pForm">
                <%-- Form --%>
                <%-- User (Driver) --%>   
                <div style=  padding-left:5px;>
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    
                    <table width="500" border="0">
                        <tr>
                            <td width="40"><h:graphicImage id="infoImg" url="/pics/error.gif" /></td>
                            <td width="450"><h:outputText id="message" value="#{bundle.DIFFERENCE_AMOUNT_MESSAGE}" escape="false" styleClass="label"/></td>
                        </tr>
                        <tr>
                            <td colspan="2"><form name="form1" method="post" action="">
                                    <div align="center" style="padding-top:5px" >
                                        <h:commandButton id="forwardButton" value="#{bundle.TO_CONTINUE}" action="#{PickingBean.forwardDifferenceAmountDialogActionPerformedListener}" styleClass="commandButton"  />
                                        <h:commandButton id="correctButton" value="#{bundle.CORRECT_AMOUNT}" action="#{PickingBean.correctDifferenceAmountDialogActionPerformedListener}" styleClass="commandButton"  />
                                        <h:commandButton id="nullpointerButton" value="#{bundle.UNEXPECTED_NULLPOINTER_TRANSIT}" action="#{PickingBean.nullpointerDifferenceAmountDialogActionPerformedListener}" styleClass="commandButton"  />
                                    </div>
                            </td>
                        </tr>
                    </table>
                    
                    <%-- Command Buttons --%>
                    
                </div>                
            </h:form>
        </f:view> 
    </body>
</html>
