/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.exception;

import de.linogistix.common.res.CommonBundleResolver;
import org.mywms.facade.FacadeException;

/**
 *
 * @author trautm
 */
public class NoViewerException extends FacadeException {

    public static final String key = "BusinessException.NoViewerException";
    
    public NoViewerException(String string) {
        super("No Viewer found for " + string, key, new String[]{key});
        setBundleResolver(CommonBundleResolver.class);
    }

}
