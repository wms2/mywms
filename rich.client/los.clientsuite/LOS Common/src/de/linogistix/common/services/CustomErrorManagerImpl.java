/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import org.openide.util.Exceptions;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.openide.util.Enumerations;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.WeakSet;
import org.openide.ErrorManager;

/**
 *
 * @author artur
 */
public class CustomErrorManagerImpl extends org.openide.ErrorManager implements de.linogistix.common.services.CustomErrorManager {
    /**
     * TracingErrorManager overrides the Netbeans ErrorManager to
     * intercept messages and route them to Farrago tracing.
     */

        // implement ErrorManager
        public Throwable attachAnnotations(
            Throwable t, Annotation[] arr)
        {
            return null;
        }

        // implement ErrorManager
        public Annotation[] findAnnotations(Throwable t)
        {
            return null;
        }

        // implement ErrorManager
        public Throwable annotate(
            Throwable t, int severity,
            String message, String localizedMessage,
            Throwable stackTrace, java.util.Date date)
        {
            System.out.println("annotate = "+message);
            return t;
        }

        // implement ErrorManager
        public void notify(int severity, Throwable t)                
        {
            System.out.println("notify");
        }

    @Override
    public void log(int severity, String s) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ErrorManager getInstance(String name) {
//        throw new UnsupportedOperationException("Not supported yet.");
           return this;
    }

    @Override
    public boolean isNotifiable(int severity) {
        System.out.println("severity = "+severity);
        return super.isNotifiable(severity);
    }

    @Override
    public boolean isLoggable(int severity) {
        System.out.println("loggable = +severity");
        return super.isLoggable(severity);
    }

       
    

}
