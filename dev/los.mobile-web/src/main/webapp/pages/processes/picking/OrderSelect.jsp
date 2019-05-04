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
        <f:view locale="#{PickingBean.locale}">	
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.picking.PickingMobileBundle" /> 

            <h:form id="Form" styleClass="form" >

                <p id="pHeader" class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleChooseOrder}" styleClass="pagetitle" />
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <h:messages id="messages" styleClass="error"/>

                    <table width="100%" border="0" cellspacing="0">
                        <tr>
                            <td>
                                <h:outputLabel value="#{bundle.LabelChooseOrder}" styleClass="label"/>
                            </td>
                        </tr><tr>
                            <td>
                                <h:selectOneMenu id="orderComboBox" 
                                				onchange="this.form.submit();"
                                				valueChangeListener="#{PickingBean.orderSelectionChanged}"
                                                value="#{PickingBean.selectedOrder}" style="width:100%;" >
                                    <f:selectItems value="#{PickingBean.orderList}" />
                                </h:selectOneMenu>
                            </td>
                        </tr>
					</table>

                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="10%"/>
							<col width="90%"/>
						</colgroup>
                        
                   		<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="clientLabel" value="#{bundle.LabelClient}:" styleClass="param" rendered="#{PickingBean.showClient}"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="clientData" value="#{PickingBean.selOrderClient}" styleClass="label" rendered="#{PickingBean.showClient}"/>
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="selOrderLabel" value="#{bundle.LabelOrder}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="selOrderData" value="#{PickingBean.selCustomerOrderNumber}" styleClass="label"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="selStratLabel" value="#{bundle.LabelType}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="selStratData" value="#{PickingBean.selStrategy}" styleClass="label"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="selCreLabel" value="#{bundle.LabelCreated}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="selCreData" value="#{PickingBean.selOrderCreated}" styleClass="label"/>
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="selPosLabel" value="#{bundle.LabelPositions}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="selPosData" value="#{PickingBean.selOrderNumPos}" styleClass="label"/>
                            </td>
                        </tr>
					</table>
                        
                    <table width="100%" border="0" cellspacing="0">
                        <tr><td>&#160;</td></tr>

                        <tr>
                            <td>
                            	<h:outputLabel id="input1Label" value="#{bundle.LabelEnterCode}" styleClass="label" />
                            </td>
                        </tr><tr>
                            <td>
                               	<h:inputText id="input1" 
                             			 value="#{PickingBean.inputCode}" 
                             			 styleClass="input" /> 
                            </td>
                        </tr>
                    </table>
                    
                </div>
                                        
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                 <h:commandButton id="forwardButton" 
	                 				 value="#{bundle.ButtonForward}" 
	                 				 action="#{PickingBean.processOrderSelect}" 
	                 				 styleClass="commandButton"  />
					&#160;
	                 <h:commandButton id="backButton" 
	                 				 value="#{bundle.ButtonMenu}" 
	                 				 action="#{PickingBean.processOrderSelectCancel}" 
	                 				 styleClass="commandButton" />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:input1').focus();
            }    
            
        </script>
    </body>
</html>
