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
                	<h:outputText id="pagetitle" value="#{bundle.TitleDone}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>


                <div class="space">
                     
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
                    
                    <table  width="100%" border="0" cellspacing="0" style="padding-top:10px;">
                    	<colgroup>
							<col width="10%"/>
							<col width="90%"/>
						</colgroup>
						
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="amount2Label" value="#{bundle.LabelAmount}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="amount2Data" value="#{ReplenishBean.amountRequested} #{ReplenishBean.itemUnit}" styleClass="label"/>
                            </td>
                        </tr>

                    </table>
                    
                </div>
                    
                <div class="space" style="padding-left:20px;padding-top:15px;">
					<h:outputLabel id="info1" value="#{bundle.MsgRequestDone}" styleClass="labelBold"/>
                
                </div>
	                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                    <h:commandButton id="BUTTON_CONTINUE" 
		                				 value="#{bundle.ButtonYes}" 
		                				 action="#{ReplenishBean.processRequestInfoYes}" 
		                				 styleClass="commandButton"  />
						&#160;
	                    <h:commandButton id="BUTTON_MENU" 
		                				 value="#{bundle.ButtonNo}" 
		                				 action="#{ReplenishBean.processRequestInfoNo}" 
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
                    document.getElementById('Form:BUTTON_CONTINUE').focus();
                }    
                
        </script>
        
    </body>
</html>
