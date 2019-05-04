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
        <f:view locale="#{ReplenishBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.replenish.ReplenishBundle" /> 
            
            <h:form id="Form" styleClass="form" >
                <%-- Page Title--%>
                <p class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleLocation}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>


                <div class="space">
                    <h:messages id="messages" styleClass="error"/>
                     
                    <table  width="100%" border="0" cellspacing="0">
                    	<colgroup>
							<col width="10%"/>
							<col width="90%"/>
						</colgroup>
						
						<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="locationLabel" value="#{bundle.LabelLocation}:" styleClass="param" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="locationData" value="#{ReplenishBean.destinationName}" styleClass="value" />
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
                            <td colspan="2">
                            	<h:outputLabel id="input1Label" value="#{bundle.LabelEnterLocation}" styleClass="label" />
                            </td>
                        </tr>
						<tr>
                            <td colspan="2">
                               	<h:inputText id="input1" 
                             			 value="#{ReplenishBean.inputLocationName}" 
                             			 styleClass="input" /> 
                            </td>
                        </tr>
                    </table>
                </div>
                
                <h:inputText value="IE-Dummy" style="display:none" />
                
                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_CONTINUE" 
		                				 value="#{bundle.ButtonContinue}" 
		                				 action="#{ReplenishBean.processRequestLocation}" 
		                				 styleClass="commandButton"  />
						&#160;
	                    <h:commandButton id="BUTTON_CANCEL" 
		                				 value="#{bundle.ButtonMenu}" 
		                				 action="#{ReplenishBean.processRequestLocationCancel}" 
		                				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        
        <script type="text/javascript">
            
            <%--            function setRow() {
                document.getElementById("view<%=renderResponse.get Namespace()%>:dt1").rows[1].style.backgroundColor="red";            
            }    --%>
                
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
