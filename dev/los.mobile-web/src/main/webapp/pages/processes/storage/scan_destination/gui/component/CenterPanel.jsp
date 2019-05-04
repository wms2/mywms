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
        <f:view locale="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="Form" styleClass="form">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                	<h:outputText id="pagetitle" 
                		value="#{bundle.StorageTitleDestination}"
                		styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>

                <div class="space">
                    <h:messages id="messages" styleClass="error"/> 
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td style="margin-left:5px"><h:outputLabel id="unitLoadLabel" value="#{bundle.UnitLoad}"  styleClass="label"/></td>
                            <td style="text-align: right;"><h:outputLabel id="unitLoadLabelMessage" value="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.unitLoadLabelMessage}" styleClass="labelBoldBlue"/></td>
                        </tr>
                        <tr>
                            <td colspan="4" style="margin-left:5px;width:100%">
                                <table width="100%" border="0">
                                    <tr >
                                        <th scope="col" style="width:100%"><h:inputText id="unitLoadTextField" value="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.unitLoadTextField}" disabled="#{!de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.unitLoadTextFieldEnable}" validator="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.unitLoadTextFieldValidator}" styleClass="input" /></th>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        
                        <tr>
                            <td style="margin-left:5px">
                            	<h:outputLabel id="storageLocationLabel" 
                            		value="#{bundle.StorageLabelLocationOrUnitLoad}" styleClass="label"/>
                            </td>
                            <td style="text-align: right;">
                            	<h:outputLabel id="storageLocationLabelMessage" 
                            	value="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.storageLocationLabelMessage}" 
                            	styleClass="labelBoldBlue"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4" style="margin-left:5px;width:100%">
                                <table width="100%" border="0">
                                    <tr >
                                        <th scope="col" style="width:100%"><h:inputText id="storageLocationTextField" value="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.storageLocationTextField}" disabled="#{!de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.storageLocationTextFieldEnable}" styleClass="input" /></th>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
	                        <td colspan="4" class="nopadding" id="mycheckbox" >
	                        	<h:selectBooleanCheckbox 
	                                value="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.consolidateCheckBox}" disabled="#{!de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.consolidateCheckBoxEnable}" 
	                                onclick="setFocus()"
	                                > 
	                            </h:selectBooleanCheckbox> 
	                                <h:outputText value="#{bundle.StorageLabelAdd}"  styleClass="label" /> 
	                        </td>
                        </tr>
                    </table>
                </div>
                    
                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="finishedButton" value="#{bundle.Done}" action="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.finishActionPerformedListener}" disabled="#{!de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.finishButtonEnable}" styleClass="commandButton"  />
                    <h:commandButton id="cancelButton" value="#{bundle.Cancel}" action="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.cancelActionPerformedListener}" styleClass="commandButton"  />
                    <h:commandButton id="forwardButton" value="#{bundle.Forward}" action="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.forwardActionPerformedListener}" disabled="#{!de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.forwardButtonEnable}" styleClass="commandButton"  />
                    <h:commandButton id="backButton" value="#{bundle.Back}" action="#{de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.backwardActionPerformedListener}" disabled="#{!de_linogistix_mobile_processes_storage_scan_destination_gui_bean_CenterBean.backwardButtonEnable}" styleClass="commandButton"  />

                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:storageLocationTextField').focus();
                document.getElementById('Form:storageLocationTextField').select();
            }    
            
        </script>
        
    </body>
</html>
