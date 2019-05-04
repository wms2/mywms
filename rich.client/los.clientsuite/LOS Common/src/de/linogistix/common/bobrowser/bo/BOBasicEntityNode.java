/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import java.beans.IntrospectionException;
import org.mywms.model.BasicEntity;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

/**
 * A special Node for pure BasicEntities. No reflection is neededed. 
 * 
 * Used i.e. for displaying entities in a master view.
 * 
 * @author trautm
 */
public class BOBasicEntityNode extends AbstractNode{
  
  /** Creates a new instance of BOBasicEntityNode */
  public BOBasicEntityNode(BasicEntity entity, BO bo) throws IntrospectionException {
    super(Children.LEAF);
  
    setName(entity.toUniqueString());
    
    if (bo != null){      
      if (bo.getIconPathWithExtension() != null){
        setIconBaseWithExtension(bo.getIconPathWithExtension());
      }
    }    
  }
  

}
