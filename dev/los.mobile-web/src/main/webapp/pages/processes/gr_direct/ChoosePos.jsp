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
        <f:view locale="#{GRDirectBean.locale}">	
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.gr_direct.GRDirectBundle" /> 

            <h:form id="Form" styleClass="form" >

                <p id="pHeader" class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleChoosePos}" styleClass="pagetitle" />
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <h:messages id="messages" styleClass="error"/>
                     
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="20%"/>
							<col width="80%"/>
						</colgroup>
                    	
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramOrder" value="#{bundle.LabelOrder}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataOrder" value="#{GRDirectBean.orderNo}" styleClass="label" />
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramDeliverer" value="#{bundle.LabelDeliverer}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataDeliverer" value="#{GRDirectBean.deliverer}" styleClass="label" />
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramDelNote" value="#{bundle.LabelDeliveryNote}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataDelNote" value="#{GRDirectBean.deliveryNote}" styleClass="label" />
                            </td>
                        </tr>
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramDelDate" value="#{bundle.LabelReceiptDate}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataDelDate" value="#{GRDirectBean.receiptDate}" styleClass="label" />
                            </td>
                        </tr>
					</table>
					
                    <table width="100%" border="0" cellspacing="0">
                        <tr><td>&#160;</td></tr>
                        <tr>
                            <td>
                                <h:outputLabel value="#{bundle.LabelChoosePos}" styleClass="label"/>
                            </td>
                        </tr><tr>
                            <td>
                                <h:selectOneMenu id="orderComboBox"
                                                 value="#{GRDirectBean.selectedAdvice}" style="width:100%;" >
                                    <f:selectItems
                                        value="#{GRDirectBean.assignedAdviceList}" />
                                </h:selectOneMenu>
                            </td>
                        </tr>
                        
                        <tr>
                            <td>
                            	<h:outputLabel id="input1Label" value="#{bundle.LabelEnterCode}" styleClass="label" />
                            </td>
                        </tr><tr>
                            <td>
                               	<h:inputText id="input1" 
                             			 value="#{GRDirectBean.inputCode}" 
                             			 styleClass="input" /> 
                            </td>
                        </tr>
                        
                    </table>
                    
                </div>
                                        
                <div class="buttonbar">  
	                 <h:commandButton id="forwardButton" 
	                 				 value="#{bundle.ButtonForward}" 
	                 				 action="#{GRDirectBean.processChoosePos}" 
	                 				 styleClass="commandButton"  />
	                 				 
	                 <h:commandButton id="backButton" 
	                 				 value="#{bundle.ButtonCancel}" 
	                 				 action="#{GRDirectBean.processChoosePosCancel}" 
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
            }    
            
        </script>
    </body>
</html>
