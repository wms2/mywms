/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

import java.util.Date;
import org.openide.ErrorManager.Annotation;

/**
 *
 * @author artur
 */
public interface CustomErrorManager {


    
    
    public Throwable annotate(Throwable arg0, int arg1, String arg2, String arg3, Throwable arg4, Date arg5);


    public Throwable attachAnnotations(Throwable arg0, Annotation[] arg1);


    public Annotation[] findAnnotations(Throwable arg0);


    public org.openide.ErrorManager getInstance(String arg0);


    public void log(int arg0, String arg1);


    public void notify(int arg0, Throwable arg1);
    
}
