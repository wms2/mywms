<%-- 
  Copyright (c) 2006 - 2013 LinogistiX GmbH

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
<%-- <%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%> --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="expires" content="Mon, 01 Jan 1990 00:00:01 GMT">

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>LOS</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/pages/stylesheet.css" type="text/css" />
    </head>
    <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" />
    
    <body class="verticalscroll" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" onload="load()">                    
        <f:view locale="#{MenuBean.locale}">
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.processes.replenish.ReplenishBundle" /> 
            
            
                        
            <h:form id="Form" styleClass="form">

                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.TitleReplenish}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>

                <div class="space">
                    <h:messages id="messages" styleClass="error"/> 
                    <table width="100%" border="0" style="padding-top:5px">
                        
                        <tr>
                            <td>
                                <h:commandButton id="BUTTON_0" 
                                	value="#{bundle.LabelReplenishRequest}"
                                	action="#{ReplenishBean.processStartRequest}" 
                                	style="width:100%" styleClass="commandButton"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <h:commandButton id="BUTTON_1" 
                                	value="#{bundle.LabelReplenishProcess}" 
                                	action="#{ReplenishBean.processStartProcess}" 
                                	style="width:100%" styleClass="commandButton"/>
                            </td>
                        </tr>
                    </table>     
                </div>
                
                <%-- Command Buttons --%>
                <div class="buttonbar">  
                    <h:commandButton id="BUTTON_LOGOUT" 
	                				 value="#{bundle.ButtonMainMenu}" 
	                				 action="#{ReplenishBean.processStartCancel}" 
	                				 styleClass="commandButton"  />
	            </div>
                                
            </h:form>            
        </f:view>     
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    

            function setFocus() {
                document.getElementById('Form:BUTTON_0').focus();
            }    
            
        </script>
    </body>
</html>





