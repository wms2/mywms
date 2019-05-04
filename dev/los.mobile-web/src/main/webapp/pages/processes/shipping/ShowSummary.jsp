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
        <f:view locale="#{ShippingBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.shipping.ShippingBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleSummary}" styleClass="pagetitle"/>
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
                            	<h:outputLabel id="orderLabel" value="#{bundle.LabelOrder}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap" >
                              	<h:outputLabel id="orderData" value="#{ShippingBean.orderNumber}" styleClass="value" />
                            </td>
                        </tr>
                    </table>
                    
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="20%"/>
							<col width="80%"/>
						</colgroup>
						
						<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="ul2Label" value="#{bundle.LabelNumPosOpen}:" styleClass="param" rendered="#{ShippingBean.numPosOpen != ''}"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="ul2Data" value="#{ShippingBean.numPosOpen}" styleClass="value" />
                            </td>
                        </tr>
						<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="ul1Label" value="#{bundle.LabelNumPosDone}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="ul1Data" value="#{ShippingBean.numPosDone}" styleClass="value" />
                            </td>
                        </tr>
                        <tr><td>&#160;</td></tr>
                    </table>

                  	<h:outputLabel id="finishMsg" value="#{bundle.MsgDone}" styleClass="value" rendered="#{ShippingBean.numPosOpen == '0'}"/> 

                </div>
                
                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_CONTINUE" 
		                				 value="#{bundle.LabelContinue}" 
		                				 action="#{ShippingBean.processSummaryFinish}" 
		                				 styleClass="commandButton"  />
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
