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
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/pages/stylesheet.css"
	type="text/css" />
</head>


<body class="verticalscroll" topmargin="0" leftmargin="0"
	marginwidth="0" marginheight="0" onload="load()">
<f:view locale="#{de_linogistix_mobile_processes_storage_scan_unitload_gui_bean_CenterBean.locale}">
	<f:loadBundle var="bundle" basename="de.linogistix.mobile.res.Bundle" />

	<h:form id="Form" styleClass="form">
		<%-- Page Title--%>
		<p id="pHeader" class="pageheader">
 		 <h:outputText id="pagetitle"
			value="#{bundle.StorageTitleUnitLoad}" 
			styleClass="pagetitle" /> 
         <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
        </p>
			
		<%-- Form --%> <%-- User (Driver) --%>
		<div class="space">
			<h:messages id="messages" styleClass="error" />
			<table width="100%" border="0" cellspacing="0">
	            <tr><td>&#160;</td></tr>
	
				<tr>
					<td scope="col" style="width: 100%">
						<h:outputLabel id="scanLabel" value="#{bundle.StorageLabelUnitLoad}" styleClass="label" />
					</td>
				</tr>
	
				<tr>
					<td scope="col" style="width: 100%">
						<h:inputText id="ulTextField"
						value="#{de_linogistix_mobile_processes_storage_scan_unitload_gui_bean_CenterBean.ulTextField}"
						styleClass="input" />
					</td>
				</tr>
				
			</table>
			<h:inputText value="IE-Dummy" style="display:none" />
        </div>

		<%-- Command Buttons --%>
		<div class="buttonbar">
			<h:commandButton id="forwardButton"
				value="#{bundle.Forward}"
				action="#{de_linogistix_mobile_processes_storage_scan_unitload_gui_bean_CenterBean.forwardActionPerformedListener}"
				disabled="#{!de_linogistix_mobile_processes_storage_scan_unitload_gui_bean_CenterBean.forwardButtonEnabled}"
				styleClass="commandButton" /> 
			<h:commandButton id="cancelButton"
				value="#{bundle.back_to_menu}"
				action="#{de_linogistix_mobile_processes_storage_scan_unitload_gui_bean_CenterBean.cancelActionPerformedListener}"
				styleClass="commandButton" />
		</div>
	</h:form>
</f:view>

<script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:ulTextField').focus();
            }    

        </script>
</body>
</html>
