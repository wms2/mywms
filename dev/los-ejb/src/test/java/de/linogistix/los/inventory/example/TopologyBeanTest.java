/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.example;

import java.io.IOException;

import junit.framework.TestCase;

import org.mywms.ejb.BeanLocator;
import org.mywms.model.Client;

import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.example.InventoryTestTopologyRemote;
import de.linogistix.los.example.LocationTestTopologyRemote;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.location.crud.LOSAreaCRUDRemote;
import de.linogistix.los.location.crud.LOSRackCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote;
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote;
import de.linogistix.los.location.query.LOSAreaQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.test.TestUtilities;

/**
 *
 * @author trautm
 */
public class TopologyBeanTest extends TestCase {

    private static BeanLocator beanLocator;
    
    private static ClientCRUDRemote clService;
    private static LOSStorageLocationCRUDRemote slService;
    private static UnitLoadCRUDRemote ulService;
    private static StockUnitCRUDRemote suService;
    private static UnitLoadTypeCRUDRemote typeService;
    private static LOSStorageLocationTypeCRUDRemote slTypeService;
    private static LOSTypeCapacityConstraintCRUDRemote capacityService;
    private static LOSAreaCRUDRemote areaService;
    private static LOSRackCRUDRemote rackService;
    private static ClientQueryRemote clQuery;
    private static LOSStorageLocationQueryRemote slQuery;
    private static UnitLoadQueryRemote ulQuery;
    private static StockUnitQueryRemote suQuery;
    private static UnitLoadTypeQueryRemote typeQuery;
    private static LOSStorageLocationTypeQueryRemote slTypeQuery;
    private static LOSTypeCapacityConstraintQueryRemote capacityQuery;
    private static LOSAreaQueryRemote areaQuery;
    private static RackQueryRemote rackQuery;
    
    protected static LocationTestTopologyRemote locTopology;
    protected static InventoryTestTopologyRemote invTopology;
    protected static CommonTestTopologyRemote commonTopology;
    //-----------------------------------------------------------------------

    public TopologyBeanTest(String testName) {
        super(testName);
    }
    
    public TopologyBeanTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        beanLocator = TestUtilities.beanLocator;
        initServices();
    }
    
    public void initServices() throws IOException {
        
    	locTopology = getBeanLocator().getStateless(LocationTestTopologyRemote.class);
        invTopology = getBeanLocator().getStateless(InventoryTestTopologyRemote.class);
        commonTopology = getBeanLocator().getStateless(CommonTestTopologyRemote.class);
        clService = getBeanLocator().getStateless(ClientCRUDRemote.class);
        slService = getBeanLocator().getStateless(LOSStorageLocationCRUDRemote.class);
        ulService = getBeanLocator().getStateless(UnitLoadCRUDRemote.class);
        suService = getBeanLocator().getStateless(StockUnitCRUDRemote.class);
        typeService = getBeanLocator().getStateless(UnitLoadTypeCRUDRemote.class);
        slTypeService = getBeanLocator().getStateless(LOSStorageLocationTypeCRUDRemote.class);
        capacityService = getBeanLocator().getStateless(LOSTypeCapacityConstraintCRUDRemote.class);
        areaService = getBeanLocator().getStateless(LOSAreaCRUDRemote.class);
        rackService = getBeanLocator().getStateless(LOSRackCRUDRemote.class);

        clQuery = getBeanLocator().getStateless(ClientQueryRemote.class);
        slQuery = getBeanLocator().getStateless(LOSStorageLocationQueryRemote.class);
        ulQuery = getBeanLocator().getStateless(UnitLoadQueryRemote.class);
        suQuery = getBeanLocator().getStateless(StockUnitQueryRemote.class);
        typeQuery = getBeanLocator().getStateless(UnitLoadTypeQueryRemote.class);
        slTypeQuery = getBeanLocator().getStateless(LOSStorageLocationTypeQueryRemote.class);
        capacityQuery = getBeanLocator().getStateless(LOSTypeCapacityConstraintQueryRemote.class);
        areaQuery = getBeanLocator().getStateless(LOSAreaQueryRemote.class);
        rackQuery = getBeanLocator().getStateless(RackQueryRemote.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of create method, of class TopologyBean.
     */
    public void testTopology() throws Exception {
    	commonTopology.create();
    	locTopology.create();
        invTopology.create();
    }

    //-----------------------------------------------------------------------
    // static getters for other tests
    //-----------------------------------------------------------------------
     public static BeanLocator getBeanLocator() {
        if (beanLocator == null){
            beanLocator = TestUtilities.beanLocator;
        }
        return beanLocator;
    }

     //-----------------------------------------------------------------------
     
     public static ClientCRUDRemote getClService() {
        return clService;
    }

     public static LOSStorageLocationCRUDRemote getSlService() {
        return slService;
    }

     public static UnitLoadCRUDRemote getUlService() {
        return ulService;
    }

     public static StockUnitCRUDRemote getSuService() {
        return suService;
    }

     public static UnitLoadTypeCRUDRemote getTypeService() {
        return typeService;
    }

     public static LOSStorageLocationTypeCRUDRemote getSlTypeService() {
        return slTypeService;
    }

     public static LOSTypeCapacityConstraintCRUDRemote getCapacityService() {
        return capacityService;
    }

     public static LOSAreaCRUDRemote getAreaService() {
        return areaService;
    }

     public static LOSRackCRUDRemote getRackService() {
        return rackService;
    }

     public static ClientQueryRemote getClQuery() {
        return clQuery;
    }

     public static LOSStorageLocationQueryRemote getSlQuery() {
        return slQuery;
    }

     public static UnitLoadQueryRemote getUlQuery() {
        return ulQuery;
    }

     public static StockUnitQueryRemote getSuQuery() {
        return suQuery;
    }

     public static UnitLoadTypeQueryRemote getTypeQuery() {
        return typeQuery;
    }

     public static LOSStorageLocationTypeQueryRemote getSlTypeQuery() {
        return slTypeQuery;
    }

     public static LOSTypeCapacityConstraintQueryRemote getCapacityQuery() {
        return capacityQuery;
    }

     public static LOSAreaQueryRemote getAreaQuery() {
        return areaQuery;
    }

     public static RackQueryRemote getRackQuery() {
        return rackQuery;
    }

	public static Client getTESTCLIENT() throws BusinessObjectNotFoundException {
		return getClQuery().queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
		
	}

	public static Client getTESTMANDANT() throws BusinessObjectNotFoundException {
		return getClQuery().queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
	}
}
