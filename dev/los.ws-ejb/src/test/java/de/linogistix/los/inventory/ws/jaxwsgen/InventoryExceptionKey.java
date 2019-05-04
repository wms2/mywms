/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.ws.jaxwsgen;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for inventoryExceptionKey.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="inventoryExceptionKey">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOT_A_GOODSIN_LOCATION"/>
 *     &lt;enumeration value="NO_SUCH_CLIENT"/>
 *     &lt;enumeration value="NO_SUCH_ITEMDATA"/>
 *     &lt;enumeration value="NO_SUCH_LOT"/>
 *     &lt;enumeration value="ORDER_CREATION_KEY"/>
 *     &lt;enumeration value="ORDER_NOT_FINIHED"/>
 *     &lt;enumeration value="CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED"/>
 *     &lt;enumeration value="CREATE_ORDERREQUEST_FAILED"/>
 *     &lt;enumeration value="CREATE_ORDERREQUEST_FAILED_WRONG_CLIENT"/>
 *     &lt;enumeration value="CREATE_GOODSRECEIPT"/>
 *     &lt;enumeration value="CREATE_AVIS_FAILED"/>
 *     &lt;enumeration value="NO_STOCKUNIT"/>
 *     &lt;enumeration value="LOT_ISLOCKED"/>
 *     &lt;enumeration value="LOT_NAME_NULL"/>
 *     &lt;enumeration value="NO_LOT_WITH_NAME"/>
 *     &lt;enumeration value="UNSUFFICIENT_AMOUNT"/>
 *     &lt;enumeration value="ITEMDATA_ISLOCKED"/>
 *     &lt;enumeration value="NO_SUITABLE_LOT"/>
 *     &lt;enumeration value="NOT_ACCEPTED"/>
 *     &lt;enumeration value="ITEMDATA_LOT_MISMATCH"/>
 *     &lt;enumeration value="ITEMDATA_EXISTS"/>
 *     &lt;enumeration value="ITEMDATA_NOT_ON_UNITLOAD"/>
 *     &lt;enumeration value="ITEMDATA_NOT_FOUND"/>
 *     &lt;enumeration value="LOT_MISMATCH"/>
 *     &lt;enumeration value="POSITION_ALREADY_ASSIGNED_ADVICE"/>
 *     &lt;enumeration value="POSITION_NO_ADVICE"/>
 *     &lt;enumeration value="CLIENT_NULL"/>
 *     &lt;enumeration value="ARTICLE_NULL"/>
 *     &lt;enumeration value="CREATE_UNITLOAD"/>
 *     &lt;enumeration value="LABEL_NOT_PRINTED"/>
 *     &lt;enumeration value="NO_STOCKUNIT_ON_FIXED_ASSIGNED_LOC"/>
 *     &lt;enumeration value="NO_SUITABLE_LOCATION"/>
 *     &lt;enumeration value="STORAGE_WRONG_LOCATION_BUT_ALLOWED"/>
 *     &lt;enumeration value="STORAGE_WRONG_LOCATION_NOT_ALLOWED"/>
 *     &lt;enumeration value="STORAGE_FAILED"/>
 *     &lt;enumeration value="STOCKUNIT_TRANSFER_FAILED"/>
 *     &lt;enumeration value="STOCKUNIT_TRANSFER_NOT_ALLOWED"/>
 *     &lt;enumeration value="STOCKUNIT_HAS_RESERVATION"/>
 *     &lt;enumeration value="STOCKUNIT_IS_LOCKED"/>
 *     &lt;enumeration value="STOCKUNIT_CONSTRAINT_VIOLATED"/>
 *     &lt;enumeration value="STORAGE_ADD_TO_EXISTING"/>
 *     &lt;enumeration value="CANNOT_BE_DELETED"/>
 *     &lt;enumeration value="STORAGE_NO_DESTINATION_FOUND"/>
 *     &lt;enumeration value="PICKREQUEST_CREATION"/>
 *     &lt;enumeration value="PICKREQUEST_NOT_FINISHED"/>
 *     &lt;enumeration value="PICKREQUEST_CONSTRAINT_VIOLATED"/>
 *     &lt;enumeration value="PICK_UNEXPECTED_NULL"/>
 *     &lt;enumeration value="PICK_WRONG_SOURCE"/>
 *     &lt;enumeration value="PICK_WRONG_AMOUNT"/>
 *     &lt;enumeration value="PICKED_TOO_MANY"/>
 *     &lt;enumeration value="UNIT_LOAD_CONSTRAINT_VIOLATED"/>
 *     &lt;enumeration value="STORAGELOCATION_CONSTRAINT_VIOLATED"/>
 *     &lt;enumeration value="CREATE_STOCKUNIT_ONSTOCK"/>
 *     &lt;enumeration value="NO_SUCH_UNITLOAD"/>
 *     &lt;enumeration value="PICK_POSITION_CONTRAINT_VIOLATED"/>
 *     &lt;enumeration value="NO_EXTINGUISHORDER_WITH_NUMBER"/>
 *     &lt;enumeration value="NOT_A_FIXED_ASSIGNED_LOCATION"/>
 *     &lt;enumeration value="ORDER_CONSTRAINT_VIOLATED"/>
 *     &lt;enumeration value="REPLENISH_ALREADY_COMES"/>
 *     &lt;enumeration value="ORDER_CANNOT_BE_STARTED"/>
 *     &lt;enumeration value="ORDER_ALREADY_STARTED"/>
 *     &lt;enumeration value="NO_SUCH_STORAGELOCATION"/>
 *     &lt;enumeration value="STOCKUNIT_NO_LOT"/>
 *     &lt;enumeration value="PICKREQUEST_ALREDAY_FINISHED"/>
 *     &lt;enumeration value="NO_PICKREQUEST"/>
 *     &lt;enumeration value="MUST_SCAN_STOCKUNIT"/>
 *     &lt;enumeration value="CANNOT_RESERVE_MORE_THAN_AVAILABLE"/>
 *     &lt;enumeration value="INVENTORY_CREATE_STOCKUNIT_ON_TOP"/>
 *     &lt;enumeration value="UNIT_LOAD_EXISTS"/>
 *     &lt;enumeration value="DESTINATION_UNITLOAD_LOCKED"/>
 *     &lt;enumeration value="AMOUNT_MUST_BE_GREATER_THAN_ZERO"/>
 *     &lt;enumeration value="ERROR_GETTING_DEFAULT_UNITLOADTYPE"/>
 *     &lt;enumeration value="LOT_ALREADY_EXIST"/>
 *     &lt;enumeration value="LOT_MANDATORY"/>
 *     &lt;enumeration value="ADVICE_MANDATORY"/>
 *     &lt;enumeration value="NO_SUCH_ORDERPOSITION"/>
 *     &lt;enumeration value="ARGUMENT_NULL"/>
 *     &lt;enumeration value="LOT_NOT_UNIQUE"/>
 *     &lt;enumeration value="UNSUFFICIENT_RESERVED_AMOUNT"/>
 *     &lt;enumeration value="AMBIGUOUS_SCAN"/>
 *     &lt;enumeration value="CLIENT_MISMATCH"/>
 *     &lt;enumeration value="CONSTRAINT_VIOLATION"/>
 *     &lt;enumeration value="NO_INVENTORY_FOR_LOT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "inventoryExceptionKey", namespace = "http://ws.inventory.los.linogistix.de/")
@XmlEnum
public enum InventoryExceptionKey {

    NOT_A_GOODSIN_LOCATION,
    NO_SUCH_CLIENT,
    NO_SUCH_ITEMDATA,
    NO_SUCH_LOT,
    ORDER_CREATION_KEY,
    ORDER_NOT_FINIHED,
    CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED,
    CREATE_ORDERREQUEST_FAILED,
    CREATE_ORDERREQUEST_FAILED_WRONG_CLIENT,
    CREATE_GOODSRECEIPT,
    CREATE_AVIS_FAILED,
    NO_STOCKUNIT,
    LOT_ISLOCKED,
    LOT_NAME_NULL,
    NO_LOT_WITH_NAME,
    UNSUFFICIENT_AMOUNT,
    ITEMDATA_ISLOCKED,
    NO_SUITABLE_LOT,
    NOT_ACCEPTED,
    ITEMDATA_LOT_MISMATCH,
    ITEMDATA_EXISTS,
    ITEMDATA_NOT_ON_UNITLOAD,
    ITEMDATA_NOT_FOUND,
    LOT_MISMATCH,
    POSITION_ALREADY_ASSIGNED_ADVICE,
    POSITION_NO_ADVICE,
    CLIENT_NULL,
    ARTICLE_NULL,
    CREATE_UNITLOAD,
    LABEL_NOT_PRINTED,
    NO_STOCKUNIT_ON_FIXED_ASSIGNED_LOC,
    NO_SUITABLE_LOCATION,
    STORAGE_WRONG_LOCATION_BUT_ALLOWED,
    STORAGE_WRONG_LOCATION_NOT_ALLOWED,
    STORAGE_FAILED,
    STOCKUNIT_TRANSFER_FAILED,
    STOCKUNIT_TRANSFER_NOT_ALLOWED,
    STOCKUNIT_HAS_RESERVATION,
    STOCKUNIT_IS_LOCKED,
    STOCKUNIT_CONSTRAINT_VIOLATED,
    STORAGE_ADD_TO_EXISTING,
    CANNOT_BE_DELETED,
    STORAGE_NO_DESTINATION_FOUND,
    PICKREQUEST_CREATION,
    PICKREQUEST_NOT_FINISHED,
    PICKREQUEST_CONSTRAINT_VIOLATED,
    PICK_UNEXPECTED_NULL,
    PICK_WRONG_SOURCE,
    PICK_WRONG_AMOUNT,
    PICKED_TOO_MANY,
    UNIT_LOAD_CONSTRAINT_VIOLATED,
    STORAGELOCATION_CONSTRAINT_VIOLATED,
    CREATE_STOCKUNIT_ONSTOCK,
    NO_SUCH_UNITLOAD,
    PICK_POSITION_CONTRAINT_VIOLATED,
    NO_EXTINGUISHORDER_WITH_NUMBER,
    NOT_A_FIXED_ASSIGNED_LOCATION,
    ORDER_CONSTRAINT_VIOLATED,
    REPLENISH_ALREADY_COMES,
    ORDER_CANNOT_BE_STARTED,
    ORDER_ALREADY_STARTED,
    NO_SUCH_STORAGELOCATION,
    STOCKUNIT_NO_LOT,
    PICKREQUEST_ALREDAY_FINISHED,
    NO_PICKREQUEST,
    MUST_SCAN_STOCKUNIT,
    CANNOT_RESERVE_MORE_THAN_AVAILABLE,
    INVENTORY_CREATE_STOCKUNIT_ON_TOP,
    UNIT_LOAD_EXISTS,
    DESTINATION_UNITLOAD_LOCKED,
    AMOUNT_MUST_BE_GREATER_THAN_ZERO,
    ERROR_GETTING_DEFAULT_UNITLOADTYPE,
    LOT_ALREADY_EXIST,
    LOT_MANDATORY,
    ADVICE_MANDATORY,
    NO_SUCH_ORDERPOSITION,
    ARGUMENT_NULL,
    LOT_NOT_UNIQUE,
    UNSUFFICIENT_RESERVED_AMOUNT,
    AMBIGUOUS_SCAN,
    CLIENT_MISMATCH,
    CONSTRAINT_VIOLATION,
    NO_INVENTORY_FOR_LOT;

    public String value() {
        return name();
    }

    public static InventoryExceptionKey fromValue(String v) {
        return valueOf(v);
    }

}
