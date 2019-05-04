/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

/**
 *
 * @author trautm
 */
public enum DockingMode {

     /**
     * No component or parameter is requiered
     */
    PLAIN,
    /**
     * One additional text parameter is requiered that will be passed from the search combobox
     */
    INLPLACE,
    /**
     *  show query parameter in panel that is integrated in BOQueryTopComponent
     */
    QUERYPANEL,
    /**
     * show component in generic Wizard 
     */
    WIZARD_GENERIC,
    /**
     * Component is a wizard
     */
    WIZARD_OWN;
   
   
}
