/*
 * BOConfigureAction.java
 *
 * Created on 26. Juli 2006, 02:22
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.browse;

import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.browse.*;
import de.linogistix.common.bobrowser.bo.binding.DescriptorBinder;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.preferences.ConfigurationFileNotFound;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOConfigureAction extends NodeAction {
  
  private Logger log = Logger.getLogger(BOConfigureAction.class.getName());
  
  private static String[] allowedRoles = new String[]{
    Role.ADMIN.toString()
  };
  
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
      
      FileObject file = DescriptorBinder.getDescriptorFile(n.getBusinessObjectTemplate().getClass());
      if (file == null){
       ExceptionAnnotator.annotate(new ConfigurationFileNotFound(n.getDisplayName()));
       return;
      }
      
      OpenCookie ok = (OpenCookie)DataObject.find(file).getCookie(OpenCookie.class);
      file.addFileChangeListener(n.getBo());
      ok.open();
      
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
    return NbBundle.getMessage(CommonBundleResolver.class, "configureView");
  }
  
  protected boolean asynchronous() {
    return false;
  }
  
  
  
  
}


