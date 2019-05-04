/*
 * TopologyRemote.java
 *
 * Created on 12. September 2006, 11:36
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.example;

import javax.ejb.Remote;

import org.mywms.model.BasicEntity;

/**
 * Creates a topology.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote()
public interface LocationTestTopologyRemote {
  
    String PALETTE_NAME = "EuroPalette, bis 1,2m hoch";
		
	String KLT_NAME = "KLT Behaelter, 400 x 600 mm";
	
	String PALETTENPLATZ_TYP_2_NAME = "Palettenplatz, Europalette TYP 2";
		
	String KOMMPLATZ_TYP_NAME = "Kommissionierplatz";
	
	String VIELE_PALETTEN_NAME = "Viele Paletten";
	
	String EINE_PALETTE_NAME = "Eine Palette";
	
	String KOMM_FACH_DUMMY_LHM_CONSTR_NAME = "Ein virtuelles LHM fuer Kommfach";
	
	String STORE_AREA_NAME = "Lager";
	
	String KOMM_AREA_NAME = "Kommissionierung";
	
	String WE_BEREICH_NAME = "Wareneingang";
	
	String WA_BEREICH_NAME = "Warenausgang";
	
	String CLEARING_BEREICH_NAME = "Clearing";
	
	String PRODUCTION_BEREICH_NAME = "Produktion";
	
	String SL_WE_TESTCLIENT_NAME = "Test Wareneingang 1";
	
	String SL_WE_TESTMANDANT_NAME = "Test Wareneingang 2";
	
	String SL_PRODUCTION_TESTCLIENT_NAME = "Test Produktion 1";
	
	String SL_PRODUCTION_TESTMANDANT_NAME = "Test Produktion 2";
	
	String TEST_RACK_1_NAME = "T1";
	
	String TEST_RACK_2_NAME = "T2";
	
	String SL_WA_TESTCLIENT_NAME = "TESTWA 1";
	
	String SL_WA_TESTMANDANT_NAME = "TESTWA 2";
	
	String SL_CLEARING_NAME = "Klaerplatz";
		
	String UL_NIRWANA_NAME = "Nirwana";
	
	String EINE_DEFAULT_PALETTE_NAME = "Fachbeschraenkung 1 Standardpalette";
	
	void clear() throws LocationTopologyException;

    void create() throws LocationTopologyException;

    void remove(Class<BasicEntity> clazz) throws LocationTopologyException;
  
}
