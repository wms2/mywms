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
                <%-- Page Title--%>
                <p id="pHeader"class="pageheader">
	                <h:outputText id="pagetitle" value="#{bundle.GoodsReceipt_confirm_new_lot}" styleClass="pagetitle"/>
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
                            	<h:outputLabel id="itemLabel" value="#{bundle.GoodsReceipt_item_number}" styleClass="param" />
                            </td>
                            <td nowrap="nowrap">
                            	<h:outputLabel id="item" value="#{GoodsReceiptBean.currentItemNumber}" styleClass="value" />
                            </td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="lotLabel" value="#{bundle.GoodsReceipt_lot_label}" styleClass="param" />
                            </td> 
                            <td nowrap="nowrap">
                            	<h:outputLabel id="lot" value="#{GoodsReceiptBean.lotName_Input}" styleClass="value" />
                            </td>
                        </tr>                        
                    </table>
					<br>                    
                  	<h:outputLabel id="questionLabel" 
                  				   value="#{bundle.GoodsReceipt_unkown_lot}" 
                  				   styleClass="label" /> 
                </div>
                    
				<%-- Command Buttons --%>
				<div class="buttonbar">  
	                                    				 
                    <h:commandButton id="BUTTON_CONFIRM" 
	                				 value="#{bundle.Yes}" 
	                				 action="#{GoodsReceiptBean.confirmNewLot}" 
	                				 styleClass="commandButton"  />

                    <h:commandButton id="BUTTON_NOCONFIRM" 
                    				 value="#{bundle.No}" 
                    				 action="#{GoodsReceiptBean.cancelNewLot}" 
                    				 styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:BUTTON_CONFIRM').focus();
            }    
            
        </script>
        
    </body>
</html>

