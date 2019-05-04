/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.crud;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class BODeleteAction extends NodeAction{
  
  private static final Logger log = Logger.getLogger(BODeleteAction.class.getName());
  
  private static String[] allowedRoles = new String[]{
      Role.ADMIN.toString()
  };
  
  public String getName() {
    return NbBundle.getMessage(CommonBundleResolver.class, "delete");
  }
  
  protected String iconResource() {
    return "de/linogistix/bobrowser/res/icon/Delete.png";
  }
  
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }
  
  protected boolean asynchronous() {
    return false;
  }
  
  protected boolean enable(Node[] activatedNodes) {
    LoginService login = (LoginService)Lookup.getDefault().lookup(LoginService.class);  
    return login.checkRolesAllowed(allowedRoles);
  }
  
  protected void performAction(Node[] activatedNodes) {
    
    BasicEntity e;
    
    if (activatedNodes == null){
      return;
    }
    
    for (Node n : activatedNodes){
      if (n == null) continue;
      BusinessObjectCRUDRemote crud;
      BusinessObjectQueryRemote query;
      BOEntityNodeReadOnly bobn;
      BO bo ;
      Long id;
      Node notifier;
      if (n instanceof BOEntityNodeReadOnly){
          bobn = (BOEntityNodeReadOnly)n;
          BOLookup l = (BOLookup)Lookup.getDefault().lookup(BOLookup.class);
          bo = (BO)l.lookup(bobn.getBo().getClass());
          id = bobn.getBo().getId();
          notifier = bobn;
      }else if (n instanceof BOMasterNode){
          BOMasterNode ma = (BOMasterNode)n;
          bo = ma.getBo();
          id = ma.getId();
          notifier = ma;
      } else{
          return;
      }
      crud = bo.getCrudService();
      query = bo.getQueryService();
      try {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(CommonBundleResolver.class, "NotifyDescriptor.ReallyDelete"),
            NbBundle.getMessage(CommonBundleResolver.class, "Delete"),
            NotifyDescriptor.OK_CANCEL_OPTION);
        
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
          CursorControl.showWaitCursor();
          BasicEntity entity = query.queryById(id);
          crud.delete(entity);
          bo.fireOutdatedEvent(notifier);
        }
       
      } catch (FacadeException ex) {
        ExceptionAnnotator.annotate(ex);
      } finally{
        CursorControl.showNormalCursor();
      }
      
    }
  }
  
  
}

