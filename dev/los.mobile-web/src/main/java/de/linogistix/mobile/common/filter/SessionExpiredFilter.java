/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.filter;

/**
 *
 * @author artur
 */
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.controller.MenuBean;

public class SessionExpiredFilter implements Filter {

	private static final Logger log = Logger.getLogger(MenuBean.class.getName());

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		String logStr = "doFilter ";
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (request.getParameter("j_username2") != null) {
			HttpSession session = req.getSession(false);
			// Make sure that ghost session will be closed. (for secure reason)
			if (session == null) {
				session = req.getSession();
				session.invalidate();
				session = req.getSession();
			}
			if (session.getAttribute("AUTHORIZED") == null) {

				String url = null;
			try {
					// dgrys portierung wildfly 8.2 - add login method
					req.login(request.getParameter("j_username2"), request.getParameter("j_password2"));
					
					session.setAttribute("AUTHORIZED", "TRUE");
					session.setAttribute("AUTHORIZED_FIRST", "TRUE");
					//doesn't work with wildfly 8.2
//					url = "j_security_check?j_username="
//							+ java.net.URLEncoder.encode(request.getParameter("j_username2"), "UTF-8") + "&j_password="
//							+ java.net.URLEncoder.encode(request.getParameter("j_password2"), "UTF-8");
					//workaround to login if authentication successful
					url="/los-mobile/faces/pages/processes/controller/MainMenu.jsp";
			}

				catch (ServletException e) {
					log.error(logStr + e.getMessage());
					//workaround to login if authentication failed
					url="/los-mobile/faces/login.jsp?errors=true";
				}
			
				res.sendRedirect(res.encodeRedirectURL(url));

			}
			return;

		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		/*
		 * if (filterConfig.getInitParameter("page") != null) { page =
		 * filterConfig.getInitParameter("page"); }
		 */
	}

	public void destroy() {
		Thread.currentThread().interrupt();
	}
}
