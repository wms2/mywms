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
                	<h:outputText id="pagetitle" value="#{bundle.TitleShowOrder}" styleClass="pagetitle"/>
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
                              	<h:outputLabel id="clientData" value="#{InfoBean.infoOrder.clientNumber}" styleClass="label" rendered="#{InfoBean.showClient}"/>
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="orderLabel" value="#{bundle.LabelOrder}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="orderData" value="#{InfoBean.infoOrder.number}" styleClass="label" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="typeLabel" value="#{bundle.LabelType}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="typeData" value="#{InfoBean.orderType}" styleClass="label" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="stateLabel" value="#{bundle.LabelState}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="stateData" value="#{InfoBean.orderState}" styleClass="label" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="posLabel" value="#{bundle.LabelNumPos}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="posData" value="#{InfoBean.infoOrder.numPos}" styleClass="label" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="destLabel" value="#{bundle.LabelTarget}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="destData" value="#{InfoBean.infoOrder.destinationName}" styleClass="label" />
                            </td>
                        </tr>
                        
                  	</table>
                  	
                </div>
                

                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="BUTTON_CONTINUE" 
	                				 value="#{bundle.ButtonBack}" 
	                				 action="#{InfoBean.processOrderBack}" 
	                				 styleClass="commandButton"  />
					&#160;
                   	<h:commandButton id="BUTTON_PREV" 
	                				 value="<" 
	                				 action="#{InfoBean.processOrderPrev}" 
	                				 styleClass="navigateButton"
	                				 rendered="#{InfoBean.orderHasPrev || InfoBean.orderHasNext}"
	                				 disabled="#{!InfoBean.orderHasPrev}" /> 
                    <h:commandButton id="BUTTON_NEXT" 
	                				 value=">" 
	                				 action="#{InfoBean.processOrderNext}" 
	                				 styleClass="navigateButton" 
	                				 rendered="#{InfoBean.orderHasPrev || InfoBean.orderHasNext}" 
	                				 disabled="#{!InfoBean.orderHasNext}" /> 
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
            
        </script>
        
    </body>
</html>
