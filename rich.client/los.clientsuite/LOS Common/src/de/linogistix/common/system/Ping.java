/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.runtime.RuntimeServicesRemote;

import java.util.logging.Logger;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction; 

/**
 * An action that invokes a request to the server similar to a ping to see
 * if the server conneciton is still up.
 * 
 * @author trautm
 */
public final class Ping extends CallableSystemAction {
  
  Logger log = Logger.getLogger(Ping.class.getName());
  
  public void performAction() {
//    CursorControl.showWaitCursor();
    try{

      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      RuntimeServicesRemote sc = (RuntimeServicesRemote)loc.getStateless(RuntimeServicesRemote.class);
      
      sc.ping();
    } catch (Throwable t){
      throw new RuntimeException(new PingException());
    } finally{
//      CursorControl.showNormalCursor();
    }
    
  }
  
  public String getName() {
    return NbBundle.getMessage(CommonBundleResolver.class, "CTL_Ping");
  }
  
  protected void initialize() {
    super.initialize();
    // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
    putValue("noIconInMenu", Boolean.TRUE);
  }
  
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }
  
  protected boolean asynchronous() {
    return false;
  }
  
}
