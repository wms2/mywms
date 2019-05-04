/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

@Stateless
public class LOSMailServiceBean implements LOSMailService {
    private static Logger logger = Logger.getLogger(LOSMailServiceBean.class);

	@EJB
	private LOSSystemPropertyService propertyService;
	
	
	public void sendSMTPMail( String[] recipients, String subject, String messageText )
	{
		String hostUser = null;
		String hostPasswd = null;
		
		String sender = propertyService.getString(LOSCommonPropertyKey.MAIL_SENDER);
		String mailServer = propertyService.getString(LOSCommonPropertyKey.MAIL_SERVER);
		boolean authorize = propertyService.getBoolean(LOSCommonPropertyKey.MAIL_AUTHOZIZE);
		if( authorize ) {
			hostUser = propertyService.getString(LOSCommonPropertyKey.MAIL_HOST_USER);
			hostPasswd = propertyService.getString(LOSCommonPropertyKey.MAIL_HOST_PASSWD);
		}

		sendSMTPMail(sender, recipients, subject, messageText, mailServer, authorize, hostUser, hostPasswd);
	}

	
	
	public void sendSMTPMail(String sender, String[] recipients,
			String subject, String messageText, String mailServer,
			boolean authorize, String hostUser, String hostPasswd) 
	{
        String methodName = "sendSMTPMail ";
    
        if (sender == null || sender.length() == 0){
        	String msg = "Cannot send mail. Parameter 'MAIL_SENDER' is empty";
            logger.error(methodName+msg);
            return;
        }
        
        if (recipients == null || recipients.length == 0){
        	String msg = "Cannot send mail. Parameter 'RECIPIENTS' is empty";
            logger.error(methodName+msg);
            return;
        }
        
        if (mailServer == null || mailServer.length() == 0){
        	String msg = "Cannot send Mail. Parameter 'MAIL_SERVER' is empty";
            logger.error(methodName+msg);
            return;
       }
        
        if (subject == null || subject.length() == 0){
            logger.warn(methodName+"No subject");
            
        }
        
        if (messageText == null || messageText.length() == 0){
            logger.warn(methodName+"No message text");
            
        }
        
        // Set Server properties
        Properties props = new Properties();
        
        props.put("mail.smtp.host", mailServer);
        

    	// User and password are only necessary, if authentication is required
        if (authorize==true) {
        	
            if (hostUser == null || hostUser.length() == 0){
            	String msg = "Cannot send mail. Parameter 'MAIL_HOST_USER' is empty";
                logger.error(methodName+msg);
                return;
            }
            
            if (hostPasswd == null || hostPasswd.length() == 0){
				String msg = "Cannot send mail. Parameter 'MAIL_HOST_PASSWD' is empty";
				logger.error(methodName+msg);
				return;
           	}
           
           	props.put("mail.smtp.auth","true");
           	
        }
        else { 
            props.put("mail.smtp.auth","false");
            
        }
        
        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(true);

        MimeMessage message = new MimeMessage(mailSession);
        try {

        	message.setFrom(new InternetAddress(sender));
            
            InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
            for(int i=0; i < recipients.length; i++){ 
            	addressTo[i] = new InternetAddress(recipients[i]); 
            }
            message.setRecipients(Message.RecipientType.TO, addressTo);
            
            message.setSubject(subject);
      
            message.setText( messageText );
            

            Transport send = mailSession.getTransport("smtp");
            
            if (authorize)
                send.connect(mailServer, hostUser, hostPasswd);
            else
                send.connect();
            
            send.sendMessage(message, message.getAllRecipients());

        }
        catch (MessagingException ex) {
            logger.error(methodName+"Cannot send mail: " + ex.getMessage(), ex);
            
        }
      
    }


}
