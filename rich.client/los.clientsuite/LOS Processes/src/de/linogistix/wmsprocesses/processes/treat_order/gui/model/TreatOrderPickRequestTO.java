/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.model;

import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import java.io.Serializable;

/**
 *
 * @author Jordan
 */
public class TreatOrderPickRequestTO extends BODTO<LOSPickingOrder>
                                 implements Serializable
                                 
{
    public String pickRequestNumber;
    
    public BODTO<LOSStorageLocation> targetPlace;

    public TreatOrderPickRequestTO(Long id, int version, String name) {
        super(id, version, name);
    }
}
