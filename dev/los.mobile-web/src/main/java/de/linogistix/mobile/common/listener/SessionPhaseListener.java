/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.listener;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author artur
 */
public class SessionPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 1L;


	public void afterPhase(PhaseEvent arg0) {//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void beforePhase(PhaseEvent pe) {
        //make sure that after session-timeout it will be not redirect (after re-login)
        //to the last clicked page. It make sure that you would landing always on the start-side
        FacesContext fc = pe.getFacesContext();
        ExternalContext ec = fc.getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(false);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        boolean hasError = false;
        if (request.getQueryString() != null) {
            if (request.getQueryString().equals("errors=true")) {
                hasError = true;
            }
        }

        if (hasError == false) {
            if (session != null) {
                if (session.isNew() == false) {
                    if (session.getAttribute("AUTHORIZED_FIRST") != null) {
                        session.setAttribute("AUTHORIZED_FIRST", null);
                        try {
                            ec.redirect("/los-mobile/faces/login_redirect.jsp");
                        } catch (IOException ex) {
                            //Something goes wrong. For securereason, make sure that not ending in an endless cascade.
                            session.invalidate();
                            Logger.getLogger(SessionPhaseListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }


    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
        //   return PhaseId.ANY_PHASE;
    }
}
