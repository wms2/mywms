/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import de.linogistix.los.inventory.exception.InventoryException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.mywms.facade.FacadeException;
import org.mywms.service.EntityNotFoundException;

/**
 * Facade for managing ItemData/articles.
 * 
 * @author trautm
 *
 */
@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ManageInventory extends java.rmi.Remote {

    /**
     * Creates a new ItemData/aarticle in the wms.
     * 
     * @param clientRef a reference to the client
     * @param articleRef a reference to the article
     * @return true if the ItemData could have been created successfully
     */
    @WebMethod
    boolean createItemData(
            
            @WebParam(name = "username") String username,
            
            @WebParam(name = "password") String password,
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "articleRef") String articleRef) throws InventoryException;

    /**
     * Deletes an existing ItemData/article in the wms.
     * 
     * @param clientRef a reference to the client
     * @param articleRef a reference to the article
     * @return true if the ItemData could have been deleted successfully
     */
    @WebMethod
    boolean deleteItemData(
            
            @WebParam(name = "username") String username,
            
            @WebParam(name = "password") String password,
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "articleRef") String articleRef);

    /**
     * Updates/changes the reference
     * 
     * @param clientRef a reference to the client
     * @param existingRef existing reference to the article
     * @param existingRef new reference for this article
     * @return true if the reference could have been updated successfully
     */
    @WebMethod
    boolean updateItemReference(
            
            @WebParam(name = "username") String username,
            
            @WebParam(name = "password") String password,
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "existingRef") String existingRef,
            
            @WebParam(name = "newRef") String newRef);

    /**
     * Creates an avis for the given article. 
     * 
     * All existing instances of this article belonging to another/old batch might be send to
     * the extinguishing process depending on the expireBatch flag.
     * 
     * @param clientRef a reference to the client
     * @param articleRef a reference to the article
     * @param expectedDelivery date when the delivery is expected
     * @param bestBeforeEnd best before end date
     * @param useNotBefore do not use this article before this date
     * @param expireBatch if true, existing batches of this article must be extinguished after arrival of this article
     * @return true if the avis is created successfully
     */
    @WebMethod
    boolean createAvis(
            
            @WebParam(name = "username") String username,
            
            @WebParam(name = "password") String password,
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "articleRef") String articleRef,
            
            @WebParam(name = "batchRef") String batchRef,
            
            @WebParam(name = "amount") BigDecimal amount,
            
            @WebParam(name = "expectedDelivery") Date expectedDelivery,
            
            @WebParam(name = "bestBeforeEnd") Date bestBeforeEnd,
            
            @WebParam(name = "useNotBefore") Date useNotBefore,
            
            @WebParam(name = "expireBatch") boolean expireBatch);

    @WebMethod
    void createStockUnitOnStorageLocation(
            @WebParam( name="username") String username, 
            
            @WebParam( name="password") String password, 
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "slName") String slName,
            
            @WebParam(name = "articleRef") String articleRef,
            
            @WebParam(name = "lotRef") String lotRef,
            
            @WebParam(name = "amount") BigDecimal amount,
            
            @WebParam(name = "unitLoadRef") String unitLoadRef) throws InventoryException, FacadeException, EntityNotFoundException;
}
