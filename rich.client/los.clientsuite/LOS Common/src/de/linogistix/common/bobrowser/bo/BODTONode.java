/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.los.query.BODTO;
import org.mywms.model.BasicEntity;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Jordan
 */
public class BODTONode<E extends BasicEntity> extends AbstractNode{
    
    private BODTO<E> entityTO;
    
    public BODTONode(BODTO<E> entityTO){
        super(Children.LEAF);
        
        this.entityTO = entityTO;
    }

    @Override
    public String getDisplayName() {
        return entityTO.getName();
    }

    public BODTO<E> getBODTO(){
        return entityTO;
    }
}
