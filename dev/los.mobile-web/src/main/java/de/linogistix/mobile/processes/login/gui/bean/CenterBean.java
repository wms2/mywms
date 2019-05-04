/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.login.gui.bean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.login.NavigationEnum;

/**
 * 
 * @author artur
 */
public class CenterBean extends BasicBackingBean implements ActionListener {
	String login;
	String password = "";
	String test;

	public String test() {
		System.out.println("test");
		return "";
	}

	public String getTest() {
		System.out.println("getTest");
		return "j_security_check";
	}

	public void setTest(String test) {
		System.out.println("setTest");
		this.test = test;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String loginActionPerformedListener() {
		/*
		 * ServletRequest request = (ServletRequest)
		 * FacesContext.getCurrentInstance().getExternalContext().getRequest();
		 * String s = (String)request.getAttribute("test");
		 * System.out.println("test = "+s);
		 */
		JSFHelper.getInstance().printRequest();
		JSFHelper.getInstance().printRequestMap();

		return NavigationEnum.controller_CenterPanel.toString();
	}

	public void validatePassword(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

	}

	public void beforePhase(javax.faces.event.PhaseEvent e) {
		HttpServletRequest req = (HttpServletRequest) e.getFacesContext()
				.getExternalContext().getRequest();
		req.getSession(true);

	}

	public void processAction(ActionEvent e) {

	}

}
