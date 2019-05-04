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
	                <h:outputText id="pagetitle" value="#{bundle.TitleCountStock}" styleClass="pagetitle"/>
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
                            	<h:outputLabel id="storageLocationLabel" value="#{bundle.LabelLocation_}" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="storageLocation" value="#{StockTakingBean.stockTakingLocationName}" styleClass="value" />
                            </td>
                        </tr>
                        
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="unitLoadLabel" value="#{bundle.LabelUnitload_}" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                               	<h:outputLabel id="unitLoad" value="#{StockTakingBean.unitLoadLabel}" styleClass="value" />
                            </td>
                        </tr>
                        
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="stockunitLabel" value="#{bundle.LabelStockunit_}" styleClass="param" />
                            </td> 
                            <td nowrap="nowrap">
                               	<h:outputLabel id="stockunitTextField" value="#{StockTakingBean.stockUnitCount}" styleClass="value" /> 
                            </td>
                        </tr>
                        
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="articelLabel" value="#{bundle.LabelItemdata_}" styleClass="param" />
                            </td> 
                            <td nowrap="nowrap">
                               	<h:outputLabel id="articelTextField" value="#{StockTakingBean.currentItem}" styleClass="value" />
                            </td>
                        </tr>
                        
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="chargeLabel" 
                            			value="#{bundle.LabelLot_}" 
                            			rendered="#{!empty StockTakingBean.currentLot}" 
                            			styleClass="param" />
                            </td>
                            <td nowrap="nowrap">
                               	<h:outputLabel id="chargeTextField" 
                               			value="#{StockTakingBean.currentLot}" 
                               			rendered="#{!empty StockTakingBean.currentLot}" 
                               			styleClass="value" /> 
                            </td>
                        </tr>
                
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="serialLabel" 
                            			value="#{bundle.LabelSerial_}" 
                            			rendered="#{!empty StockTakingBean.currentSerial}" 
                            			styleClass="param" />
                            </td>
                            <td nowrap="nowrap">
                               	<h:outputLabel id="serialTextField" 
                               			value="#{StockTakingBean.currentSerial}" 
                               			rendered="#{!empty StockTakingBean.currentSerial}" 
                               			styleClass="value" /> 
                            </td>
                        </tr>
                        
                        <tr >
                    		<td> &nbsp; </td>
                    	</tr>
                    	
                  	</table>
                  	
                    <table  width="100%" border="0" cellspacing="0">
                        <tr>
                            <td colspan="2">
                            	<h:outputLabel id="amountLabel" value="#{bundle.LabelEnterAmount}" styleClass="label" />
                            </td>
                        </tr><tr>
                            <td width="80%">
								<h:inputText id="amountTextField" value="#{StockTakingBean.countedAmount}" styleClass="input" />
                            </td>
                            <td nowrap="nowrap">
                            	&nbsp;
								<h:outputLabel id="unitLabel" value="#{StockTakingBean.currentUnit}" styleClass="label" />
                            </td>
                        </tr>
                        
                    </table>
                </div>
                    
				<h:inputText value="IE-Dummy" style="display:none" />

                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
		                    <h:commandButton id="BUTTON_FINISH_UNITLOAD" 
											 value="#{bundle.ButtonUnitloadFinished}" 
											 action="#{StockTakingBean.processStockCounted_FinishUnitLoad}" 
											 disabled="#{!StockTakingBean.enableFinishUl}"
											 styleClass="commandButton"  />
		                    <h:commandButton id="BUTTON_NEXT_STOCK" 
		                    			     value="#{bundle.ButtonNextStock}" 
		                    			     action="#{StockTakingBean.processStockCounted_Next}"
		                    			     styleClass="commandButton"  />
		                    <h:commandButton id="BUTTON_CANCEL" 
			                				 value="#{bundle.ButtonCancel}" 
			                				 action="#{StockTakingBean.cancelNew}" 
			                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:amountTextField').focus();
            }    
            
        </script>
        
    </body>
</html>

