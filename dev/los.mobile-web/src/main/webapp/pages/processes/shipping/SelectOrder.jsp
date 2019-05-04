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
                	<h:outputText id="pagetitle" value="#{bundle.TitleSelectOrder}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <h:messages id="messages" styleClass="error"/>

                    <table  width="100%" border="0" cellspacing="0">
                        <tr><td>&#160;</td></tr>
                        <tr>
                            <td>
                                <h:outputText value="#{bundle.LabelChooseOrder}" styleClass="param"/>
                            </td>
                        </tr>
                        
                        <tr>
                            <td>
                                <h:selectOneMenu id="orderComboBox" 
                                	onchange="submit();" 
                                	valueChangeListener="#{ShippingBean.selectedOrderChanged}"
                                    value="#{ShippingBean.orderNumber}" 
                                    style="width:100%;" >
                                    <f:selectItems value="#{ShippingBean.orderList}" />
                                </h:selectOneMenu>
                            </td>
                            
                        </tr>
                        
                    </table>
                </div>
                    
                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_CONTINUE" 
		                				 value="#{bundle.LabelContinue}" 
		                				 action="#{ShippingBean.processSelectOrder}" 
		                				 styleClass="commandButton"  />
	                    <h:commandButton id="BUTTON_CANCEL" 
		                				 value="#{bundle.LabelMenu}" 
		                				 action="#{ShippingBean.processCancelSelectOrder}" 
		                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:orderComboBox').focus();
            }    
            
        </script>
    </body>
</html>
