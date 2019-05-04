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
                	<h:outputText id="pagetitle" value="#{bundle.TitleEnterLoc}" styleClass="pagetitle"/>
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
                            	<h:outputLabel id="paramOrder" value="#{bundle.LabelOrder}:" styleClass="param" rendered="#{GRDirectBean.showOrder}" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataOrder" value="#{GRDirectBean.orderNo}" styleClass="label" rendered="#{GRDirectBean.showOrder}" />
                            </td>
                        </tr>

                        <tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramItemData" value="#{bundle.LabelItemData}:" styleClass="param" />
                            </td> 
                            <td nowrap="nowrap">
                               	<h:outputLabel id="dataItemData" value="#{GRDirectBean.itemDataNumber}" styleClass="label" /> 
                            </td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap" colspan="2" style="padding-right:20px; padding-left:20px; font-size:smaller;">
                               	<h:outputLabel id="itemDataName" value="#{GRDirectBean.itemDataName}" />
                            </td>
                        </tr>


                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramAmount" value="#{bundle.LabelAmountStock}:" styleClass="param"/> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataAmount" value="#{GRDirectBean.amount}" styleClass="label" />
                            </td>
                        </tr>

                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramLot" value="#{bundle.LabelLot}:" styleClass="param" rendered="#{GRDirectBean.showLot}" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataLot" value="#{GRDirectBean.lot}" styleClass="label" rendered="#{GRDirectBean.showLot}" />
                            </td>
                        </tr>

                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramLoc" value="#{bundle.LabelLoc}:" styleClass="param" rendered="#{GRDirectBean.hasTargetLocation}" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataLoc" value="#{GRDirectBean.targetLocation}" styleClass="label" rendered="#{GRDirectBean.hasTargetLocation}" />
                            </td>
                        </tr>
                        
                    	<tr>
                            <td nowrap="nowrap" style="padding-right:20px">
                            	<h:outputLabel id="paramUl" value="#{bundle.LabelUnitLoad}:" styleClass="param" rendered="#{GRDirectBean.currentUlLabelSelected}" /> 
                            </td>
                            <td nowrap="nowrap">
                              	<h:outputLabel id="dataUl" value="#{GRDirectBean.currentUlLabel}" styleClass="label" rendered="#{GRDirectBean.currentUlLabelSelected}" />
                            </td>
                        </tr>
                        
                  	</table>
                  	
                    <table  width="100%" border="0" cellspacing="0">

	            		<tr><td>&#160;</td></tr>

                        <tr>
                            <td>
                            	<h:outputLabel id="input1Label" value="#{bundle.LabelEnterLoc}" styleClass="label" />
                            </td>
                        </tr><tr>
                            <td>
                               	<h:inputText id="input1" 
                             			 value="#{GRDirectBean.inputCode}" 
                             			 styleClass="input" /> 
                            </td>
                        </tr>

                    </table>
                    
					<h:inputText value="IE-Dummy" style="display:none" />
                  	
                </div>
                
                <div class="buttonbar">  
	                 <h:commandButton id="forwardButton" 
	                 				 value="#{bundle.ButtonForward}" 
	                 				 action="#{GRDirectBean.processEnterTargetLoc}" 
	                 				 styleClass="commandButton"  />
	                 				 
	                 <h:commandButton id="backButton" 
	                 				 value="#{bundle.ButtonCancel}" 
	                 				 action="#{GRDirectBean.processEnterTargetCancel}" 
	                 				 styleClass="commandButton"  />
	                 				 
	                 <h:commandButton id="addtoButton" 
	                 				 value="#{bundle.ButtonAddToUl}" 
	                 				 action="#{GRDirectBean.processEnterTargetAdd}" 
	                 				 styleClass="commandButton" 
	                 				 disabled="#{!GRDirectBean.targetHasAddOption}" />

	                 <h:commandButton id="storageButton" 
	                 				 value="#{bundle.ButtonStorage}" 
	                 				 action="#{GRDirectBean.processEnterTargetStore}" 
	                 				 styleClass="commandButton"
	                 				 disabled="#{!GRDirectBean.targetHasStoreOption}" />

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
