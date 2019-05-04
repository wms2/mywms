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
        <f:view locale="#{GoodsReceiptBean.locale}">	
        
            <f:loadBundle var="bundle" basename ="de.linogistix.mobile.res.Bundle" /> 
            
            <h:form id="Form" styleClass="form" >

                <p id="pHeader" class="pageheader">
                	<h:outputText id="pagetitle" value="#{bundle.GoodsReceipt_choose_goodsreceipt}" styleClass="pagetitle" />
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
                
                <div class="space">
                    <%--                <h:outputFormat id="infoMsg"  value="" styleClass="info"/> --%>
                    <h:messages id="messages" styleClass="error"/> 
                    <table width="100%" border="0" cellspacing="0">
                        <tr><td>&#160;</td></tr>
                        <tr>
                            <td>
                                <h:outputText value="#{bundle.GoodsReceipt_OpenReceipts}"></h:outputText>
                            </td>
                        </tr>
                        
                        <tr>
                            <td>
                                <h:selectOneMenu id="orderComboBox"
                                                 value="#{GoodsReceiptBean.selectedGoodsReceipt}" style="width:100%;" >
                                    <f:selectItems
                                        value="#{GoodsReceiptBean.goodsReceiptList}" />
                                </h:selectOneMenu>
                                
                            </td>
                            
                        </tr>
                    </table>
                </div>
                                        
                <%-- Command Buttons --%>
                <div class="buttonbar">  
	                 <h:commandButton id="forwardButton" 
	                 				 value="#{bundle.Forward}" 
	                 				 action="#{GoodsReceiptBean.processGoodsReceiptChoosen}" 
	                 				 styleClass="commandButton"  />
	                 				 
	                 <h:commandButton id="backButton" 
	                 				 value="#{bundle.back_to_menu}" 
	                 				 action="#{GoodsReceiptBean.backToMenu}" 
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
