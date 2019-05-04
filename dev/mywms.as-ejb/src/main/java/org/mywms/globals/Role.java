/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.globals;

/**
 * Remark: This class maybe renamed to RoleName in future. This
 * enumeration contains basic roles used to access the facades in myWMS.
 * Other types may be used as well.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
public enum Role {
    /** The admin of whole system. */
    ADMIN,
    /** The client of the owner of the warehouse. */
    CLIENT,
    /**
     * The admin of a client, operates all of a single client's admin
     * properties.
     */
    CLIENT_ADMIN,
    /** Operates the inventory.*/
    INVENTORY,
    /** Warehouse staff, allowed to transport things, picking, etc. */
    OPERATOR,
    /** The foreman of the warehouse. */
    FOREMAN,
    /** May query reports */
    REPORT,
    /** May operate documents */
    DOCUMENT,
    /** Operates the Logging events. */
    LOGGING,
    /** Operates the clearing events. */
    CLEARING,
    /** The guest role. */
    GUEST;

    public String toString() {
        switch (this) {
        case ADMIN:
            return ADMIN_STR;
        case CLIENT:
            return CLIENT_STR;
        case CLIENT_ADMIN:
            return CLIENT_ADMIN_STR;
        case INVENTORY:
            return INVENTORY_STR;
        case OPERATOR:
            return OPERATOR_STR;
        case FOREMAN:
            return FOREMAN_STR;
        case REPORT:
            return REPORT_STR;
        case DOCUMENT:
            return DOCUMENT_STR;
        case LOGGING:
            return LOGGING_STR;
        case CLEARING:
            return CLEARING_STR;
        default:
            return GUEST_STR;
        }
    }

    public static final String ADMIN_STR = "Admin";
    public static final String CLIENT_STR = "Client";
    public static final String CLIENT_ADMIN_STR = "Client's Admin";
    public static final String INVENTORY_STR = "Inventory";
    public static final String OPERATOR_STR = "Operator";
    public static final String FOREMAN_STR = "Foreman";
    public static final String REPORT_STR = "Report";
    public static final String CLEARING_STR = "Clearing";
    public static final String LOGGING_STR = "Logging";
    public static final String DOCUMENT_STR = "Document";
    public static final String GUEST_STR = "Guest";
}