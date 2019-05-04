/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import javax.ejb.Local;

@Local
public interface LOSMailService {


	/** Sending an email.
	 * The parameters for sender, mailServer, authorization are read from system-properties 
	 * 
     * @param recipients. A String array with the recipients
     * @param subject
     * @param messageText
     */
    public void sendSMTPMail(String recipients[], String subject, String messageText);
    	
	/** Sending an email.
     * @param sender
     * @param recipients. A String array with the recipients
     * @param subject
     * @param messageText
     * @param mailServer
     * @param authorize
     * @param hostUser
     * @param hostPasswd
     */
    public void sendSMTPMail(
    		String sender, String recipients[], 
            String subject, String messageText, 
            String mailServer, boolean authorize, String hostUser, String hostPasswd);

}
