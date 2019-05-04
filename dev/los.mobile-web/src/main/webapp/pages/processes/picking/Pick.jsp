<%-- 
  Copyright (c) 2010 LinogistiX GmbH

  www.linogistix.com
  
  Project: myWMS-LOS
--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%-- <%@ taglib prefix="rich" uri="http://richfaces.org/rich" %> --%>

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
        <f:view locale="#{PickingBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.picking.PickingMobileBundle" /> 
            
            <h:form id="Form" styleClass="form" onsubmit="return validate();">
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitlePickAmount}" styleClass="pagetitle"/>
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
                            	<h:outputLabel id="orderLabel" value="#{bundle.LabelOrder}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="orderData" value="#{PickingBean.orderInfo}" styleClass="label"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="locLabel" value="#{bundle.LabelLocation}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="locData" value="#{PickingBean.pickFromName}" styleClass="label"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="ulLabel" value="#{bundle.LabelUnitLaod}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="ulData" value="#{PickingBean.pickFromLabel}" styleClass="label" />
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="matLabel" value="#{bundle.LabelItemData}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="matData" value="#{PickingBean.pickItemNumber}" styleClass="label"/>
                            </td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap" colspan="2" style="padding-right:20px; padding-left:20px; font-size:smaller;">
                               	<h:outputLabel id="matName" value="#{PickingBean.pickItemName}" />
                            </td>
                        </tr>
					</table>
				</div>
                <div class="space">
					
                    <table  width="100%" border="0" cellspacing="0" style="padding-top:20px; padding-bottom:10px">
                    	<tr>
                            <td nowrap="nowrap" width="1%" style="padding-right:10px; vertical-align:middle;">
                            	<h:outputLabel id="labelAmount" value="#{bundle.LabelAmount}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap" width="1%" style="padding-right:2px; vertical-align:middle;">
                    			<h:outputLabel id="amountLabelMessage" 
                     				value="#{PickingBean.pickAmount}"
                     				style="color:#008000; font-weight:bold; font-size:3em;}" />
                            </td>
                            <td nowrap="nowrap" style="vertical-align:middle;">
                       			<h:outputLabel id="labelUnitName" styleClass="label" 
                  					value="#{PickingBean.pickUnit}"/>
                            </td>
                        </tr>
					</table>
				</div>
				    

				<h:inputText value="#{PickingBean.currentId}" style="display:none" />
                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="BUTTON_OK" 
	                				 value="#{bundle.ButtonOK}" 
	                				 action="#{PickingBean.processPick}" 
                                     onclick="noticeButton()"
	                				 styleClass="commandButton"  />
	                				 
					&#160;
                    <h:commandButton id="BUTTON_CHANGE" 
	                				 value="#{bundle.ButtonChange}" 
	                				 action="#{PickingBean.processPickFunction}" 
	                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:BUTTON_OK').focus();
            }    
            
            isEnableSubmit = true;
            isOkPressed = false;

            function noticeButton() {
                isButtonPressed = true;
            }

            function validate() {
                if( !isEnableSubmit ) {
                    // Disable server opertion only.
                    // With the disabled element the server method is not called. So the disabled attribute is set with the 2nd click.
                    document.getElementById('Form:BUTTON_OK').disabled = true;
                    document.getElementById('Form:BUTTON_CHANGE').disabled = true;
                    return false;
                }
                if( isButtonPressed ) {
                    isEnableSubmit = false;
                }
                return true;
            }
        </script>
        
    </body>
</html>
