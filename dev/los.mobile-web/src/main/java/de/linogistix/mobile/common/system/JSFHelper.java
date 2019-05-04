/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.system;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.object.Constants;
import de.linogistix.mobile.processes.login.NavigationEnum;


/**
 *
 * @author artur
 */
public class JSFHelper extends BasicBackingBean {

    private static JSFHelper instance = null;
    private javax.swing.Timer timer;

    public synchronized static JSFHelper getInstance() {
        if (instance == null) {
            instance = new JSFHelper();
        }
        return instance;
    }

    private JSFHelper() { }

    /**
     * Put a Object to the session Object name. Important this Object should have no name form session bean
     * which are register in faces-config.
     * @param name
     * @param bean
     */
    public void setRequestBean(String name, Object bean) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        sessionMap.put(name, bean);
    }

    /**
     * Will be set as key the Class name (without package structure). Useful for beans which are under the same
     * name registered how they self called. e.g NotifyDescriptorBean
     * @param bean
     */
    public void setRequestBean(Object bean) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        sessionMap.put(bean.getClass().getSimpleName(), bean);
    }

    public Object getSessionBean(String name) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Object managedBean = (Object) sessionMap.get(name); // Retrieves the session scoped bean.
        return managedBean;
    }

    @SuppressWarnings("unchecked")
	public <T> T getSessionBean(Class<T> clazz ) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        String name = getSessionBeanName(clazz);
        T managedBean = (T) sessionMap.get(name); // Retrieves the session scoped bean.
        return managedBean;
    }
    
    @SuppressWarnings("rawtypes")
	public String getSessionBeanName(Class clazz) {
        String name = clazz.getName();
        name = name.replaceAll("\\.", "_");
        
        return name;
    }
    

//    public Object getSessionBeanForce(String name, Object bean) {
    @SuppressWarnings("unchecked")
	public <T> T getSessionBeanForce(Class<T> clazz) { 
        try {
            //(JInternalFrame)Class.forName(screen).newInstance();
            Object bean = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            Object sessionBean = (Object) sessionMap.get(getSessionBeanName(clazz)); 
            if (sessionBean == null) {
                setSessionBean(getSessionBeanName(clazz), bean);
//            setSessionBean("picking_chooseOrder_CenterBean",new de.linogistix.mobile.processes.picking.chooseOrder.gui.bean.CenterBean());
            }
            T managedBean = (T)sessionMap.get(getSessionBeanName(clazz)); // Retrieves the session scoped bean.
            return managedBean;
        } catch (Throwable ex) {
            Logger.getLogger(JSFHelper.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return null;
    }

    /**
     * Put a Object to the session Object name. Important this Object should have no name form session bean
     * which are register in faces-config.
     * @param name
     * @param bean
     */
    public void setSessionBean(String name, Object bean) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(name, bean);
    }

    /**
     * Will be set as key the Class name (without package structure). Useful for beans which are under the same
     * name registered how they self called. e.g NotifyDescriptorBean
     * @param bean
     */
    public void setSessionBean(Object bean) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(bean.getClass().getSimpleName(), bean);
    }

    public void setRequestAtrribute(String key, String param) {
        FacesContext fC = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) fC.getExternalContext().getRequest();
        req.setAttribute(key, param);
    }

    public String getRequestAtrribute(String key) {
        FacesContext fC = FacesContext.getCurrentInstance();
        Map<String, String> reqPar = fC.getExternalContext().getRequestParameterMap();
        return reqPar.get(key);
    }

    public void setSessionAtrribute(String key, Object param) {
        FacesContext fC = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fC.getExternalContext().getSession(true);
        if (session.getAttribute(key) != null) {
            session.removeAttribute(key);
        }
        session.setAttribute(key, param);
    }

    public Object getSessionAtrribute(String key) {
        FacesContext fC = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fC.getExternalContext().getSession(false);
        Object s = (Object) session.getAttribute(key);
        return s;
    }

    public void setSessionAtrribute(Object param) {
        FacesContext fC = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fC.getExternalContext().getSession(true);
        if (session.getAttribute(param.getClass().toString()) != null) {
            session.removeAttribute(param.getClass().toString());
        }
        
        session.setAttribute(param.getClass().toString(), param);
    }

    public Object getSessionAtrribute(Object key) {
        FacesContext fC = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fC.getExternalContext().getSession(false);
        
        Object s = (Object) session.getAttribute(key.getClass().toString());
        return s;
    }

    @SuppressWarnings("rawtypes")
	public void printRequest() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            System.out.println("Parameter = " + e.nextElement());
        }
        Enumeration ea = request.getAttributeNames();
        while (ea.hasMoreElements()) {
            System.out.println("Attribute = " + ea.nextElement());
        }
        System.out.println("Requested URI = " + request.getRequestURI());
        System.out.println("Requested URL = " + request.getRequestURL());
        System.out.println("QueryString = " + request.getQueryString());


    }

    public void printRequestMap() {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
    /*         Iterator iter = requestMap.values().iterator();
    String name = (String) requestMap.get("Login");
    System.out.println("name = "+name);
    while (iter.hasNext()) {
    System.out.println("values = "+iter.next());
    }
    iter = requestMap.keySet().iterator();
    while (iter.hasNext()) {
    System.out.println("keys = "+iter.next());
    }*/

    }

    public String getAttribute(ActionEvent event) {
        String s = (String) event.getComponent().getAttributes().get("dynamicStr");
        return s;
    }

    public String getAttribute(String attributeName) {
        ServletRequest request = (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String s = (String) request.getAttribute(attributeName);
        return s;
    }

    public void logout() {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
        ((HttpSession) fc.getExternalContext().getSession(false)).invalidate();
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();        
        Application ap = fc.getApplication();
        NavigationHandler nh = ap.getNavigationHandler();
        //Generate Logout Dialog
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Login", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(Constants.INFORMATION_ICON, resolve("Logouted", new Object[]{}), resolve("LogoutMessage", new Object[]{}), buttonTextList);
        String page = n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                return NavigationEnum.controller_CenterPanel.toString();
            }
        });
        //Load the NotifyDescriptor because after logout the JSF logic is expired.
        nh.handleNavigation(fc, null, page);
    }


    ActionListener taskPerformer = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent e) {
            FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
            Object obj = fc.getExternalContext().getSession(false);
            if (obj == null) {
                timer.stop();
/*                Application ap = fc.getApplication();
                NavigationHandler nh = ap.getNavigationHandler();
                List<String> buttonTextList = new ArrayList<String>();
                buttonTextList.add(resolve("Login", new Object[]{}));
                NotifyDescriptorExt n = new NotifyDescriptorExt(Constants.INFORMATION_ICON, resolve("SessionTimeout", new Object[]{}), resolve("LogoutMessage", new Object[]{}), buttonTextList);
                String page = n.setCallbackListener(new ButtonListener() {

                    public String buttonClicked(final int buttonId) {
                        return NavigationEnum.controller_CenterPanel.toString();
                    }
                });
                nh.handleNavigation(fc, null, page);*/
            }
        }
    };

    public void redirect(String page) {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
        try {
            fc.getExternalContext().redirect(page + ".jsp");
        } catch (Exception e) {
            Logger.getLogger(JSFHelper.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void resetBean(Class<?> clazz) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();        
        if (sessionMap.get(getSessionBeanName(clazz)) != null) {
            sessionMap.remove(getSessionBeanName(clazz));
        }
    }

    public void message(String key) {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();       
        fc.addMessage(null,new FacesMessage(resolve(key, new Object[]{})));
    }    
    
    
    public boolean hasMessage() {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();       
        Iterator<FacesMessage> iter = fc.getMessages();        
        return iter.hasNext();
    }
    
    public Iterator<FacesMessage> getMessages(){
    	FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();       
        Iterator<FacesMessage> iter = fc.getMessages();        
        return iter;
    }
    
    
    public String getRequestParameter(String param) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
//        HttpSession session = ((HttpServletRequest) request).getSession(false);
        return request.getParameter(param);        
    }
    
    public String getRequestQueryString() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        return request.getQueryString();        
    }
    
    public Object getParameter(ActionEvent event, String name) {
        UIParameter component = (UIParameter) event.getComponent().findComponent(name);
        return component.getValue();
    }
    
    public void setParameter(ActionEvent event, String name, Object value) {
        UIParameter component = (UIParameter) event.getComponent().findComponent(name);
        component.setValue(value);
    }
    
    /**
     * usage in jsf side <f:attribute  name="testx" value="kk" />
     * @param event
     * @param name
     * @return
     */
    public Object getAttribute(ActionEvent event, String name) {
        return event.getComponent().getAttributes().get(name).toString();
    }
    
    public void setAttribute(ActionEvent event, String name, Object value) {
        UIComponent comp = event.getComponent();
        Map<String, Object> map = event.getComponent().getAttributes();
        map.put(name, comp.getClientId(FacesContext.getCurrentInstance()));
    }
    

    public UIComponent findComponent(final String id) {
        return this.findComponent(FacesContext.getCurrentInstance().getViewRoot(), id);
    }
    
    
    /**
     * Finds a component in the component tree.
     * @param component UIComponent
     * @param id Component Id.
     * @return The component or null if not found.
     */
    public UIComponent findComponent(UIComponent component,
            final String id) {


        final String componentId = component.getClientId(FacesContext.getCurrentInstance());
        Iterator<UIComponent> kids;
        
        UIComponent found;

        if (componentId.endsWith(id)) {
            return component;
        }

        kids = component.getChildren().iterator();

        while (kids.hasNext()) {
            
            found = findComponent(kids.next(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
}
