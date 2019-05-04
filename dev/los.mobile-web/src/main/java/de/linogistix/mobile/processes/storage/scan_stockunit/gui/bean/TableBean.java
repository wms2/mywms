/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.storage.scan_stockunit.gui.bean;

import java.util.ArrayList;
import java.util.List;

public class TableBean {

    public List<RowObject> rowList;
    

    public TableBean() {
        rowList = new ArrayList<RowObject>();
    }
    

    public List<RowObject> getRowList() {        
        return rowList;
    }
    
    public RowObject getRowObject(String stockunit, String articel, String storagelocation) {
        return new RowObject(stockunit,articel, storagelocation);
    }
    
    public class RowObject {


        String unitLoad;
        String articel;
        String storageLocation;
        
        

        public RowObject(String stockunit, String articel, String storagelocation) {
            this.unitLoad = stockunit;
            this.articel = articel;
            this.storageLocation = storagelocation;
        }

        public String getUnitLoad() {
            return unitLoad;
        }

        public String getArticel() {
            return articel;
        }
        
         public String getStorageLocation() {
            return storageLocation;
        }


    }
}
