/*
 * BOCreateNode.java
 *
 * Created on 14. Januar 2007, 20:54
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.crud.gui.object;

import de.linogistix.common.bobrowser.bo.BOEntityNode;
import java.beans.IntrospectionException;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;

/**
 * Node for querying BusinessObjects by template.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOCreateNode extends BOEntityNode{
  
  private static final Logger log = Logger.getLogger(BOCreateNode.class.getName());
  
  public BOCreateNode(BasicEntity e) throws IntrospectionException{
    super(e);
  }
  
  public boolean canDestroy() {
    return false;
  }
   
  protected void initActions(){
    //
  }
  
  
  
  
  
}
