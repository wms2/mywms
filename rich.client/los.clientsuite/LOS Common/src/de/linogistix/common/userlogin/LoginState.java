/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.userlogin;

/**
 *
 * Three states are known:
 * 
 * <ul>
 * <li> <code>NOT_AUTENTICATED</code> The user is NOT authentificated against the system
 * <li> <code>AUTENTICATED</code> The user is authentificated against the system
 * <li> <code>LOG_IN</code>Authentification is in progress. This state is important for the internal login state machine 
 * </ul>
 * @author trautm
 */
public enum LoginState {
  NOT_AUTENTICATED, LOG_IN, AUTENTICATED
}
