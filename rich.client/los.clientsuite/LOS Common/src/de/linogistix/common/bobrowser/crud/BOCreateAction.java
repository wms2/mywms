/*
 * BOCreateAction.java
 *
 * Created on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.crud;

import de.linogistix.common.bobrowser.crud.gui.object.BOCreateNode;
import de.linogistix.common.bobrowser.crud.gui.component.BOCreateWizard;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import java.awt.Dialog;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOCreateAction extends NodeAction {
  
  private Long createdID = 0L;
  
  private static String[] allowedRoles = new String[]{
      Role.ADMIN.toString()
  };
  
  /**
   *@param node must be of type BONode
   */
  protected boolean enable(Node[] node) {
    if ((node == null) || (node.length != 1)){
      //System.out.println("--> BONode " + node.length);
      return false;
    }
    
    if (! (node[0] instanceof BONode)){
      return false;
    }
    
    BONode boNode = (BONode)node[0];
    
    LoginService login = (LoginService)Lookup.getDefault().lookup(LoginService.class);  
    return login.checkRolesAllowed(allowedRoles);
    
  }
  
  protected void performAction(Node[] node) {
    
    BONode n;
    
    this.createdID = 0L;
    
    if (node.length == 1){
      try{
        n = (BONode)node[0];
      } catch (ClassCastException ex){
        throw new RuntimeException("Unexpected node: " + node[0]);
      }
    } else{
      throw new RuntimeException("Expected 1 key but got " + node.length);
    }
    try{
      
      BusinessObjectCRUDRemote crud = n.getCrudService();
      
      BOCreateWizard w = new BOCreateWizard(new BOCreateNode((BasicEntity)n.getBusinessObjectTemplate()));
      Dialog d = DialogDisplayer.getDefault().createDialog(w);
      
      d.setVisible(true);
      
      if (w.getValue().equals(NotifyDescriptor.OK_OPTION)){
        CursorControl.showWaitCursor();
        BasicEntity e = crud.create(w.getNode().getBo());
        n.setBusinessObjectTemplate(e);
        n.getBo().fireOutdatedEvent(w.getNode());
      }
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    } finally {
      CursorControl.showNormalCursor();
    }
    
  }
  
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }
  
  public String getName() {
    return NbBundle.getMessage(CommonBundleResolver.class, "create");
  }
  
  protected boolean asynchronous() {
    return false;
  }
  
  
  public Long getCreatedID(){
    return createdID;
  }
  
  
  
}


