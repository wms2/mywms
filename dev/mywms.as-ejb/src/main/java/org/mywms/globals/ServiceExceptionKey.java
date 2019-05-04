/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.globals;

/**
 * Lists the basic ressource keys used for exceptions of the myWMS
 * system.
 * 
 * @author Markus Jordan
 * @version $Revision$ provided by $Author$
 */
public enum ServiceExceptionKey {

    /* General */
    NO_ENTITY_WITH_ID,
    NO_ENTITY_WITH_NAME,
    LOGIN_FAILED,

    /* Client */
    NO_CLIENT_WITH_NUMBER,
    NO_CLIENT_WITH_NAME,

    /* Config */
    CONFIG_KEY_NOT_UNIQUE,

    /* User */
    USER_NAME_NOT_UNIQUE,
    NO_USER_WITH_USERNAME,

    /* Role */
    ROLE_ALREADY_EXISTS,
    NO_ROLE_WITH_NAME,

    /* ItemData */
    ITEMDATA_NUMBER_NOT_UNIQUE,
    NO_ITEMDATA_WITH_ITEMNUMER,
    ITEMDATA_SCALE_VIOLATION,

    /* UnitLoad */
    NO_UNITLOAD_WITH_LABEL,
    UNITLOAD_LABELID_NOT_UNIQUE,

    /* UnitLoadType */
    UNITLOADTYPE_NAME_NOT_UNIQUE,
    NO_UNITLOADTYPE_WITH_NAME,

    /* StorageLocation */
    STORAGELOCATION_NAME_NOT_UNIQUE,
    NO_STORAGELOCATION_WITH_NAME,

    /* Area */
    AREA_NAME_NOT_UNIQUE,
    NO_AREA_WITH_NAME,

    /* Document */
    NO_DOCUMENT_WITH_NAME,
    DOCUMENT_ALREADY_EXISTS,

    /* PickingRequest */
    PICKINGREQUEST_NUMBER_NOT_UNIQUE,
    NO_PICKINGREQUEST_WITH_NUMBER,

    /* TransportRequest */
    NO_TRANSPORTREQUEST_WITH_NUMBER,

    /* GoodsOutRequest */
    NO_GOODSOUTREQUEST_WITH_NUMBER,
    NO_GOODSOUTREQUEST_WITH_PICKINGREQUEST,
    GOODSOUTREQUEST_EXISTS,

    /* PluginConfiguration */
    PLUGINCONFIGURATION_ALREADY_EXISTS,

    /* BasicEntityMerger */
    BASIC_ENTITY_CANNOT_BE_MERGED,

    /* Zone */
    ZONE_NAME_NOT_UNIQUE,
    NO_ZONE_WITH_NAME, 

    /* Inventory */
    
    GENERIC,

}