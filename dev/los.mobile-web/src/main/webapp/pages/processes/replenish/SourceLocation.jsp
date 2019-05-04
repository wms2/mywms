<%-- 
  Copyright (c) 2010 LinogistiX GmbH

  www.linogistix.com
  
  Project: myWMS-LOS
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
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
        <f:view locale="#{ReplenishBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.replenish.ReplenishBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleEnterSource}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
               	</p>
                
                <div class="space">
                    <h:messages id="messages"  styleClass="error"/> 
                    
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="10%"/>
							<col width="90%"/>
						</colgroup>
                    	
                   		<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="clientLabel" value="#{bundle.LabelClient}:" styleClass="param" rendered="#{ReplenishBean.showClient}"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="clientData" value="#{ReplenishBean.clientNumber}" styleClass="label" rendered="#{ReplenishBean.showClient}"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="sourceLabel" value="#{bundle.LabelSourceLocation}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="sourceData" value="#{ReplenishBean.sourceName}" styleClass="labelBold"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="ulLabel" value="#{bundle.LabelUnitLoad}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="ulData" value="#{ReplenishBean.sourceUnitLoad}" styleClass="labelBold" />
                            </td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="destinationLabel" value="#{bundle.LabelDestination}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="destinationData" value="#{ReplenishBean.destinationName}" styleClass="label"/>
                            </td>
                        </tr>

                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px;padding-top:5px;">
                            	<h:outputLabel id="matLabel" value="#{bundle.LabelItemData}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap" style="padding-top:5px;">
                              	<h:outputLabel id="matData" value="#{ReplenishBean.itemNumber}" styleClass="label"/>
                            </td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap" colspan="2" style="padding-right:20px; padding-left:20px; font-size:smaller;">
                               	<h:outputLabel id="matName" value="#{ReplenishBean.itemName}" />
                            </td>
                        </tr>
                    </table>

                    <table  width="100%" border="0" cellspacing="0" style="padding-top:15px;">

                        <tr>
                            <td>
                            	<h:outputLabel id="input1Label" value="#{bundle.LabelScanSourceUnitLoad}" styleClass="label" />
                            </td>
                        </tr>
                        <tr ">
                            <td width="100%" style="padding-right:4px;" >
                               	<h:inputText id="input1" 
                             			 value="#{ReplenishBean.inputCode}" 
                             			 styleClass="input" /> 
                            </td>
                        </tr>

                    </table>
                    
					<h:inputText value="IE-Dummy" style="display:none" />
                </div>
                

                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="BUTTON_CONTINUE"
                    	 	value="#{bundle.ButtonContinue}" 
              				 action="#{ReplenishBean.processSourceLocation}" 
              				 styleClass="commandButton"  />
					&#160;
                    <h:commandButton id="BUTTON_SPECIAL" 
	                				 value="#{bundle.ButtonCancel}" 
	                				 action="#{ReplenishBean.processSourceLocationCancel}" 
	                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:input1').focus();
                document.getElementById('Form:input1').select();
            }    
            
        </script>
        
    </body>
</html>
