<%-- 
  Copyright (c) 2010 LinogistiX GmbH

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
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{InfoBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.info.InfoBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleShowUnitLoad}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
               	</p>
                
                <div class="space">
                    <h:messages id="messages"  styleClass="error"/> 
                    
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="20%"/>
							<col width="80%"/>
						</colgroup>
                    	
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="clientLabel" value="#{bundle.LabelClient}:" styleClass="param" rendered="#{InfoBean.showClient}"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="clientData" value="#{InfoBean.infoUnitLoad.clientNumber}" styleClass="label" rendered="#{InfoBean.showClient}"/>
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:commandLink id="locationLabel" value="#{bundle.LabelLocation}:" action="#{InfoBean.processUnitLoadLocation}" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="locationData" value="#{InfoBean.infoUnitLoad.locationName}" styleClass="label" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="unitLoadLabel" value="#{bundle.LabelUnitLoad}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="unitLoadData" value="#{InfoBean.infoUnitLoad.label}" styleClass="label" />
                            </td>
                        </tr>

                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="unitLoadTypeLabel" value="#{bundle.LabelType}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="unitLoadTypeData" value="#{InfoBean.infoUnitLoad.type}" styleClass="label" />
                            </td>
                        </tr>

                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:commandLink id="contentLabel" value="#{bundle.LabelContent}:" action="#{InfoBean.processUnitLoadStockUnit}" disabled="#{!InfoBean.unitLoadHasContent}"/>
                            </td> 
                            <td nowrap="nowrap">
                               	<h:outputLabel id="contentData" value="#{InfoBean.unitLoadContent}" styleClass="label" /> 
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:commandLink id="pickLabel" value="#{InfoBean.unitLoadOrderLabel}:" action="#{InfoBean.processUnitLoadOrder}" disabled="#{!InfoBean.unitLoadHasOrderContent}" />
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="pickData" value="#{InfoBean.unitLoadOrder}" styleClass="label"/>
                            </td>
                        </tr>
                        
                        
                  	</table>
                </div>
                

                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="BUTTON_CONTINUE" 
	                				 value="#{bundle.ButtonDone}" 
	                				 action="#{InfoBean.processStart}" 
	                				 styleClass="commandButton"  />
					&#160;
                    <h:commandButton id="BUTTON_BACK" 
									value="#{bundle.ButtonBack}" 
									onclick="goBack(); return false;" 
	                				styleClass="commandButton" 
	                				rendered="#{InfoBean.showUnitLoadBack}" />
                   	<h:commandButton id="BUTTON_PREV" 
	                				 value="<" 
	                				 action="#{InfoBean.processUnitLoadPrev}" 
	                				 styleClass="navigateButton"
	                				 rendered="#{InfoBean.unitLoadHasPrev || InfoBean.unitLoadHasNext}"
	                				 disabled="#{!InfoBean.unitLoadHasPrev}" /> 
                    <h:commandButton id="BUTTON_NEXT" 
	                				 value=">" 
	                				 action="#{InfoBean.processUnitLoadNext}" 
	                				 styleClass="navigateButton" 
	                				 rendered="#{InfoBean.unitLoadHasPrev || InfoBean.unitLoadHasNext}"
	                				 disabled="#{!InfoBean.unitLoadHasNext}" /> 
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:BUTTON_CONTINUE').focus();
            }    

            function goBack() {
                window.history.back();
                window.location.href = window.location.pathname + window.location.search;
            }
            
        </script>
        
    </body>
</html>
