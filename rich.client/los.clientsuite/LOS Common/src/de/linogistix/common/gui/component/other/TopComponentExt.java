/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import de.linogistix.common.gui.listener.TopComponentListenerImpl;
import de.linogistix.common.gui.listener.TopComponentListenerImpl.InformType;
import org.openide.windows.TopComponent;

/**
 *
 * @author artur
 * So after Login it not be longer necessary to close and open the dialog to generate
 * an componendOpended event again. Instead it can use this componentOpened override
 * method.
 */
public class TopComponentExt extends TopComponent {
    private TopComponentListenerImpl listener = new TopComponentListenerImpl();    
    
    @Override
    protected void componentDeactivated() {
        listener.informComponents(this,InformType.COMPONENT_DEACTIVATED);                
    }

    @Override
    protected void componentHidden() {
        listener.informComponents(this,InformType.COMPONENT_HIDDEN);                
    }

    @Override
    protected void componentShowing() {
        listener.informComponents(this,InformType.COMPONENT_SHOWING);                
    }
   
    
    @Override
    protected void componentClosed() {
        listener.informComponents(this,InformType.COMPONENT_CLOSED);                
    }

    @Override
    protected void componentActivated() {
        listener.informComponents(this,InformType.COMPONENT_ACTIVATED);        
    }

    @Override
    public void componentOpened() {
        listener.informComponents(this,InformType.COMPONENT_OPENED);
    }
    
    
}
