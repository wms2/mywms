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
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">
        <f:view locale="#{StockTakingBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.stocktaking.StockTakingBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.TitleEnterUnitload}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    <h:messages id="messages"  styleClass="error"/> 
                    
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="20%"/>
							<col width="80%"/>
						</colgroup>
                    	
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="storageLocationLabel" 
                            				   value="#{bundle.LabelLocation_}" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="storageLocation" 
                              				   value="#{StockTakingBean.stockTakingLocationName}" 
                              				   styleClass="value" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="countedUnitLoadsLabel" 
                            				   value="#{bundle.LabelCountedUnitloads_}" 
                            				   styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                            	<h:outputLabel id="countedUnitLoadsList" 
                            				   value="#{StockTakingBean.unitLoadCount}" 
                            				   styleClass="value" /> 
                            </td>
                        </tr>
                        
                    	<tr >
                    		<td style="margin-top:50px">
                    			&nbsp;
                    		</td>
                    	</tr>
                        <tr>
                            <td colspan="2">
                            	<h:outputLabel id="unitLoadLabel" value="#{bundle.LabelEnterUnitload}" styleClass="label" /> 
                         	</td>
                        </tr>
                        <tr>
                            <td colspan="2">
                               	<h:inputText id="INPUT_UNITLOAD" 
                           				 value="#{StockTakingBean.unitLoadLabel}" 
                           				 styleClass="input"/>
                            </td>
                        </tr>
                        
                    </table>
                </div>
                    
				<h:inputText value="IE-Dummy" style="display:none" />

                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_COUNT_UNITLOAD" 
		                				 value="#{bundle.ButtonCountUnitload}" 
		                				 action="#{StockTakingBean.countStocks}" 
		                				 styleClass="commandButton"  />
	                    <h:commandButton id="BUTTON_LOCATION_FINISHED" 
	                    				 value="#{bundle.ButtonLocationFinished}" 
	                    				 action="#{StockTakingBean.processLocationFinished}" 
	                    				 styleClass="commandButton"  />
	                    <h:commandButton id="BUTTON_CANCEL" 
		                				 value="#{bundle.ButtonCancel}" 
		                				 action="#{StockTakingBean.cancelCounting}" 
		                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:INPUT_UNITLOAD').focus();
                document.getElementById('Form:INPUT_UNITLOAD').select();
            }    
            
        </script>
        
    </body>
</html>

