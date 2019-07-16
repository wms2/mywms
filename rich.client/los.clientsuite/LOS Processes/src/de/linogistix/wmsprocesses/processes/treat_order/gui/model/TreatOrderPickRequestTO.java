/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.model;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.picking.PickingOrder;
import java.io.Serializable;

/**
 *
 * @author Jordan
 */
public class TreatOrderPickRequestTO extends BODTO<PickingOrder>
                                 implements Serializable
                                 
{
    public String pickRequestNumber;
    
    public BODTO<StorageLocation> targetPlace;

    public TreatOrderPickRequestTO(Long id, int version, String name) {
        super(id, version, name);
    }
}
