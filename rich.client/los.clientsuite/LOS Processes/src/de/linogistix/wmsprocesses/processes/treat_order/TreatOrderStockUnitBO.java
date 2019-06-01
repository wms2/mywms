/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.inventory.StockUnit;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class TreatOrderStockUnitBO extends BO{

    @Override
    protected BusinessObjectQueryRemote initQueryService() {
        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(StockUnitQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    @Override
    protected BusinessObjectCRUDRemote initCRUDService() {
        return null;
    }

    @Override
    protected String initName() {
        return "StockUnits";
    }

    @Override
    protected BasicEntity initEntityTemplate() {
        StockUnit o = new StockUnit();
        //dgrys portierung wildfly 8.2
        //o.setLabelId("");

        return o;
    }
    
    @Override
    protected Property[] initBoMasterNodeProperties() {
        return TreatOrderStockUnitMasterNode.boMasterNodeProperties();
    }
    
    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return TreatOrderStockUnitMasterNode.class;
    }

}
