<%-- 
  Copyright (c) 2012 LinogistiX GmbH

  www.linogistix.com
  
  Project: myWMS-LOS
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>LOS</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    <f:view locale="#{PickingBean.locale}" >
        <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.picking.PickingMobileBundle" /> 
        
        <body onLoad="document.getElementById('form:start').click();">
			<h:form id="form"> 
               	<h:commandButton id="start" 
	                			value="#{bundle.ButtonStart}" 
	                			action="#{PickingBean.processStartPicking}"
								styleClass="commandButton" />  
		    </h:form>
	    </body>
    </f:view>
</html>
