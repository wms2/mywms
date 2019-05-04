/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.crud;

import java.util.List;

import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

public interface BusinessObjectCRUDRemote<T extends BasicEntity> {

	/**
	 *  Creates an empty instance
	 * 
	 * @throws BusinessObjectExistsException
	 * @throws BusinessObjectCreationException
	 */
	T create() throws BusinessObjectExistsException,
			BusinessObjectCreationException, BusinessObjectSecurityException;

	/**
	 * Creates a new entity and copies properties of given entity.
	 * 
	 * @param entity 
	 * @throws BusinessObjectExistsException
	 * @throws BusinessObjectCreationException
	 */
	T create(T entity) throws BusinessObjectExistsException,
			BusinessObjectCreationException, BusinessObjectSecurityException;

	/**
	 * Deletes given entity from database
	 * 
	 * @param entity 
	 * @throws BusinessObjectDeleteException if removal failed
	 * @throws BusinessObjectNotFoundException if there is nothing to delete
	 */
	void delete(T entity)
			throws BusinessObjectNotFoundException, BusinessObjectDeleteException, BusinessObjectSecurityException;

	/**
	 * Deletes given entities
	 * 
	 * @param list
	 */
	void delete(List<BODTO<T>> list) 
			throws BusinessObjectNotFoundException, BusinessObjectDeleteException, BusinessObjectSecurityException;
	
	/**
	 * Updates given entity to the database
	 * 
	 * @param entity 
	 * @throws BusinessObjectExistsException
	 * @throws BusinessObjectCreationException
	 * @throws BusinessObjectMergeException
	 * @throws FacadeException 
	 */
	void update(T entity)
			throws BusinessObjectNotFoundException,
			BusinessObjectModifiedException,
			BusinessObjectMergeException,
      BusinessObjectSecurityException, FacadeException;

	/**
	 * Retrieve entity with given id.
	 * 
	 * @param entity 
	 * @throws BusinessObjectNotFoundException
	 */
	T retrieve(long id) throws BusinessObjectNotFoundException, BusinessObjectSecurityException;

	/**
	 * Merges data from one entity into the other.
	 * 
	 * @param from
	 * @param to
	 * @throws BusinessObjectMergeException
	 */
	void mergeInto(T from, T to) throws BusinessObjectMergeException, BusinessObjectSecurityException;
	
	/**
	 * Locks the given Entity .
	 * 
	 * @see BusinessObjectLockState
	 * @param entity
	 * @param lock
	 * @param lockCause
	 * @throws BusinessObjectSecurityException
	 */
	public void lock(T entity, int lock, String lockCause) throws BusinessObjectSecurityException;
}