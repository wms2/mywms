/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;

/**
 * This interface defines the basic DAO service for any of the myWMS
 * BasicEntity entities. Feel free to derive from this class,
 * implementing your own service with only a small number of lines of
 * code. The BasicService expects to handle entities of class
 * BasicEntity
 * 
 * @see org.mywms.model.BasicEntity
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Local
public interface BasicService<E> {
    // ----------------------------------------------------------------
    // set of standard methods
    // ----------------------------------------------------------------

    /**
     * Returns the entity matching the specified identifyer.
     * 
     * @param id the unique id of the object
     * @return the object
     * @throws EntityNotFoundException if the specified entity could not
     *             be found
     */
    E get(long id) throws EntityNotFoundException;

    /**
     * Removes the specified entiy from the database.
     * 
     * @param entity the entity to be removed
     * @throws ConstraintViolatedException if a database constraint is
     *             violated
     */
    void delete(E entity) throws ConstraintViolatedException;

    /**
     * Returns a list of entities, sorted in chronological order
     * 
     * @param client the pursessor of the entity; maybe null to get all
     *            regardless of the client
     * @return list of entities
     */
    List<E> getChronologicalList(Client client);

    /**
     * Returns a list of entities, sorted in chronological order
     * 
     * @param client the pursessor of the entity; maybe null to get all
     *            regardless of the client
     * @param limit maximum number of elements returned; all if limit<=0
     * @return list of entities
     */
    List<E> getChronologicalList(Client client, int limit);

    /**
     * Returns a list of entities, sorted by the specified criteria.
     * Examples for the orderBy argument are:
     * <ul>
     * <li> <code>created</code>
     * <li> <code>created DESC</code>
     * <li> <code>id</code>
     * <li> <code>mySpecialProperty</code><br>
     * (will work, if the entity contains the specified named property)
     * </ul>
     * 
     * @param client the pursessor of the entity; maybe null to get all
     *            regardless of the client
     * @param limit maximum number of elements returned; all if limit<=0
     * @param orderBy the order criteria
     * @return the list of elements found
     */
    List<E> getList(Client client, int limit, String[] orderBy);

    /**
     * Returns a list of entities, matching the specified client.
     * 
     * @param client the pursessor of the entity; maybe null to get all
     *            regardless of the client
     * @return list of entities
     */
    List<E> getList(Client client);

    /**
     * Can be used to flush eth appropriate EntityManager.
     */
    void flush();

    Class<E> getEntityClass();

    /**
     * Merges an detached entity back to the persistence context.
     * 
     * @param entity the entity
     */
    E merge(E entity);

    /**
     * Deletes all rows in entities database table. Notice: Entities
     * which might be in the persistence context will get inkonsistent.
     * A call to deleteAll() should be performed within a seperate
     * transaction.
     */
    void deleteAll();

}
