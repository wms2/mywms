/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.example;

import java.io.IOException;

import org.mywms.ejb.BeanLocator;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.location.crud.LOSAreaCRUDRemote;
import de.linogistix.los.location.crud.LOSRackCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote;
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadCRUDRemote;
import de.linogistix.los.location.crud.UnitLoadTypeCRUDRemote;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
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
import junit.framework.TestCase;

/**
 *
 * @author trautm
 */
public class TopologyBeanTest extends TestCase {

    private static BeanLocator beanLocator;
    
    private static ClientCRUDRemote clCrud;
    private static LOSStorageLocationCRUDRemote slCrud;
    private static UnitLoadCRUDRemote ulCrud;
    
    private static UnitLoadTypeCRUDRemote typeCrud;
    private static LOSStorageLocationTypeCRUDRemote slTypeCrud;
    private static LOSTypeCapacityConstraintCRUDRemote capacityCrud;
    private static LOSAreaCRUDRemote areaCrud;
    private static LOSRackCRUDRemote rackCrud;
    private static ClientQueryRemote clQuery;
    private static LOSStorageLocationQueryRemote slQuery;
    private static UnitLoadQueryRemote ulQuery;
    
    private static UnitLoadTypeQueryRemote typeQuery;
    private static LOSStorageLocationTypeQueryRemote slTypeQuery;
    private static LOSTypeCapacityConstraintQueryRemote capacityQuery;
    private static LOSAreaQueryRemote areaQuery;
    private static RackQueryRemote rackQuery;
    
    
    protected static LocationTestTopologyRemote topology;
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
        
        topology = getBeanLocator().getStateless(LocationTestTopologyRemote.class);

        clCrud = getBeanLocator().getStateless(ClientCRUDRemote.class);
        slCrud = getBeanLocator().getStateless(LOSStorageLocationCRUDRemote.class);
        ulCrud = getBeanLocator().getStateless(UnitLoadCRUDRemote.class);
        
        typeCrud = getBeanLocator().getStateless(UnitLoadTypeCRUDRemote.class);
        slTypeCrud = getBeanLocator().getStateless(LOSStorageLocationTypeCRUDRemote.class);
        capacityCrud = getBeanLocator().getStateless(LOSTypeCapacityConstraintCRUDRemote.class);
        areaCrud = getBeanLocator().getStateless(LOSAreaCRUDRemote.class);
        rackCrud = getBeanLocator().getStateless(LOSRackCRUDRemote.class);

        clQuery = getBeanLocator().getStateless(ClientQueryRemote.class);
        slQuery = getBeanLocator().getStateless(LOSStorageLocationQueryRemote.class);
        ulQuery = getBeanLocator().getStateless(UnitLoadQueryRemote.class);
        
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
       
        topology.create();
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

     public static Client getTESTCLIENT() throws BusinessObjectNotFoundException {
        return getClQuery().queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
    }
     
     public static Client getTESTMANDANT() throws BusinessObjectNotFoundException {
         return getClQuery().queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
     }

     public static Client getSYSTEMCLIENT() throws BusinessObjectNotFoundException {
        return getClQuery().getSystemClient();
    }

//     public static LOSStorageLocation getSL_WE() throws BusinessObjectNotFoundException {
//        return slQuery.queryByIdentity(LocationTestTopologyBean.SL_WE_NAME);
//    }

     public static LOSRack getTEST_RACK_1() throws BusinessObjectNotFoundException {
        return rackQuery.queryByIdentity(LocationTestTopologyBean.TEST_RACK_1_NAME);
    }

     public static UnitLoadType getPALETTE() throws BusinessObjectNotFoundException {
        return typeQuery.queryByIdentity(LocationTestTopologyBean.PALETTE_NAME);
    }

     public static UnitLoadType getDUMMY_KOMM_ULTYPE() throws BusinessObjectNotFoundException {
        return typeQuery.getPickLocationUnitLoadType();
    }

     public static LOSTypeCapacityConstraint getVIELE_PALETTEN() throws BusinessObjectNotFoundException {
        return capacityQuery.queryByIdentity(LocationTestTopologyBean.VIELE_PALETTEN_NAME);
    }

     public static LOSTypeCapacityConstraint getKOMM_FACH_DUMMY_LHM_CONSTR() throws BusinessObjectNotFoundException {
        return capacityQuery.queryByIdentity(LocationTestTopologyBean.KOMM_FACH_DUMMY_LHM_CONSTR_NAME);
    }

     public static LOSArea getSTORE_AREA() throws BusinessObjectNotFoundException {
        return areaQuery.queryByIdentity(LocationTestTopologyBean.STORE_AREA_NAME);
    }

     public static LOSArea getKOMM_AREA() throws BusinessObjectNotFoundException {
         return areaQuery.queryByIdentity(LocationTestTopologyBean.KOMM_AREA_NAME);
    }

     public static LOSArea getWE_BEREICH() throws BusinessObjectNotFoundException {
         return areaQuery.queryByIdentity(LocationTestTopologyBean.WE_BEREICH_NAME);
    }

     public static LOSArea getWA_BEREICH() throws BusinessObjectNotFoundException {
         return areaQuery.queryByIdentity(LocationTestTopologyBean.WA_BEREICH_NAME);
    }

     public static LOSStorageLocationType getKOMMISIONIER_FACH() throws BusinessObjectNotFoundException {
         return slTypeQuery.queryByIdentity(LocationTestTopologyBean.KOMMPLATZ_TYP_NAME);
    }

     //-----------------------------------------------------------------------
     
     public static ClientCRUDRemote getClService() {
        return clCrud;
    }

     public static LOSStorageLocationCRUDRemote getSlService() {
        return slCrud;
    }

     public static UnitLoadCRUDRemote getUlService() {
        return ulCrud;
    }

     public static UnitLoadTypeCRUDRemote getTypeService() {
        return typeCrud;
    }

     public static LOSStorageLocationTypeCRUDRemote getSlTypeService() {
        return slTypeCrud;
    }

     public static LOSTypeCapacityConstraintCRUDRemote getCapacityService() {
        return capacityCrud;
    }

     public static LOSAreaCRUDRemote getAreaService() {
        return areaCrud;
    }

     public static LOSRackCRUDRemote getRackService() {
        return rackCrud;
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
}
