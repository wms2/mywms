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
                	<h:outputText id="pagetitle" value="#{bundle.GoodsReceipt_process_stock}" styleClass="pagetitle"/>
	                <h:graphicImage id="logo" url="/pics/logo.gif" styleClass="logo"/>
                </p>
               	  
                <div class="space">
                
                    <h:messages id="messages"  styleClass="error"/> 
                    <table  width="100%" border="0" cellspacing="0">
                        
                        <tr>
                            <td style="margin-left:5px">
                            	<h:outputLabel id="itemLabel" value="#{bundle.GoodsReceipt_item_number}" styleClass="label" /> 
                            	<h:outputLabel value="      " styleClass="label" />
                            	<h:outputLabel id="item" value="#{GoodsReceiptBean.currentItemNumber}" styleClass="label" />
                            </td>
                            <td style="margin-left:5px">
                            	<h:outputLabel id="lotLabel" value="#{bundle.GoodsReceipt_lot_label}" styleClass="label" /> 
                            	<h:outputLabel value="      " styleClass="label" />
                            	<h:outputLabel id="lot" value="#{GoodsReceiptBean.lotName_Input}" styleClass="label" />
                            </td>
                        </tr>                        
                       
                    </table>
                    <table width="100%" border="0" cellspacing="0">
                                                
                        <tr>
                            <td style="margin-left:5px">
                            	<h:outputLabel id="stockIdLabel" value="#{bundle.GoodsReceipt_stock_label}" styleClass="label" />
                            </td> 
                        </tr>
                        
                        <tr>
                            <td colspan="4" style="margin-left:5px;">
                                <table width="200">
                                    <tr>
                                        <th scope="col">
                                        	<h:inputText id="stockIdField" 
                                        				 value="#{GoodsReceiptBean.stockLabel_Input}" 
                                        				 styleClass="input" />
                                        </th>
                                    </tr>
                            	</table>
                            </td>
                        </tr>
                        
                        <tr>
                            <td style="margin-left:5px">
                            	<h:outputLabel id="serialLabel" value="#{bundle.GoodsReceipt_serial}" styleClass="label" />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4" style="margin-left:5px;" >
                                <table width="200" >
                                    <tr >
                                        <th scope="col">
                                            <h:inputText id="serialTextField" 
                                            			 value="#{GoodsReceiptBean.serialNo_Input}" 
                                            			 styleClass="input" /> 
                                        </th>
                                    </tr>
                            	</table>
                            </td>
                        </tr>
                                        
                        <tr>
                            <td style="margin-left:5px">
                            	<h:outputLabel id="amountLabel" value="#{bundle.GoodsReceipt_stock_amount}" styleClass="label" />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4" style="margin-left:5px;">
                                <table width="200">
                                    <tr>
                                        <th scope="col">
                                        	<h:inputText id="amountTextField" 
                                        				 value="#{GoodsReceiptBean.amountPerStock_Input}" 
                                        				 styleClass="input" 
                                            			 disabled="#{GoodsReceiptBean.serialRequired}"/>
                                        </th>
                                    </tr>
                            	</table>
                            </td>
                        </tr>
                        
                    </table>
                </div>
                    
				<%-- Command Buttons --%>
				<div class="buttonbar">  
					<h:commandButton id="BUTTON_FINISH_STOCKDATA" 
									 value="#{bundle.GoodsReceipt_next_stock}" 
									 action="#{GoodsReceiptBean.nextStock}" 
									 styleClass="commandButton"  />
                    					
                    <h:commandButton id="BUTTON_FINISH_UNITLOAD" 
									 value="#{bundle.GoodsReceipt_finish_stockprocessing}" 
									 action="#{GoodsReceiptBean.finishStockProcessing}" 
									 styleClass="commandButton"  />
									 
                    <h:commandButton id="BUTTON_CANCLE" 
                    			     value="#{bundle.Cancel}" 
                    			     action="#{GoodsReceiptBean.cancelProcessingSingleStock}" 
                    			     styleClass="commandButton"  />
                </div>
            </h:form>
        </f:view> 
        <script type="text/javascript">
            
            function load() {            
                setFocus();    
            }    
            
            function setFocus() {
                document.getElementById('Form:stockIdTextField').focus();
            }    
            
        </script>
        
    </body>
</html>

