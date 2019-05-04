/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.listener;

/**
 *
 * @author artur
 */
public interface TopComponentListener {

    public void componentOpened();
    
    public void componentClosed();
    
    public void componentActivated();   
    
    public void componentDeactivated();
    
    public void componentHidden();

    public void componentShowing();
    
}
