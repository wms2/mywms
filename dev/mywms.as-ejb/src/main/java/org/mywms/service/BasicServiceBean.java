/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;

/**
 * This class defines the basic service for any of the myWMS entities.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class BasicServiceBean<E>
    implements BasicService<E>
{
    private static final Logger log =
        Logger.getLogger(BasicServiceBean.class.getName());

    private Class<E> entityClass;

    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

    /**
     * Creates a new instance of BasicServiceBean.
     */
    @SuppressWarnings("unchecked")
    public BasicServiceBean() {
        Class c = getClass();

//        log.debug("class is: " + c.getName());
        // walking back the derivation stack of the class
        while (!(c.getGenericSuperclass() instanceof ParameterizedType)) {
            c = c.getSuperclass();
            log.debug("...next class is: " + c.getName());
        }
//        log.debug("...final class is: " + c.getName());
        this.entityClass =
            (Class<E>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
    }

//    /**
//     * @see org.mywms.service.BasicService#create()
//     */
//    protected E create() {
//        try {
//            E entity = (E) entityClass.newInstance();
//
//            manager.persist(entity);
//            manager.flush();
//
//            return entity;
//        }
//        catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }

    /**
     * @see org.mywms.service.BasicService#get(long)
     */
    public E get(long id) throws EntityNotFoundException {
        try {
            E entity = (E) manager.find(entityClass, id);
    
            if (entity != null) {
                return entity;
            }
            else {
                throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_ID);
            }
        }
        catch(javax.persistence.EntityNotFoundException e) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ENTITY_WITH_ID);
        }
    }

    /**
     * @see org.mywms.service.BasicService#delete
     */
    public void delete(E entity) throws ConstraintViolatedException {
        E mergedEntity = manager.merge(entity);
        manager.remove(mergedEntity);
    }

    /**
     * @see org.mywms.service.BasicService#getChronologicalList(org.mywms.model.Client)
     */
    @SuppressWarnings("unchecked")
    public List<E> getChronologicalList(Client client) {
        Query query;

        if (client.isSystemClient()) {
            query =
                manager.createQuery("SELECT e FROM "
                    + entityClass.getSimpleName()
                    + " e "
                    + "ORDER BY e.created, e.id");
        }
        else {
            query =
                manager.createQuery("SELECT e FROM "
                    + entityClass.getSimpleName()
                    + " e "
                    + "WHERE e.client=:client "
                    + "ORDER BY e.created, e.id");

            query.setParameter("client", client);
        }

        return (List<E>) query.getResultList();
    }

    /**
     * @see org.mywms.service.BasicService#getChronologicalList(org.mywms.model.Client,
     *      int)
     */
    @SuppressWarnings("unchecked")
    public List<E> getChronologicalList(Client client, int limit) {
        Query query;

        if (client.isSystemClient()) {
            query =
                manager.createQuery("SELECT e FROM "
                    + entityClass.getSimpleName()
                    + " e "
                    + "ORDER BY e.created DESC, e.id DESC");
        }
        else {
            query =
                manager.createQuery("SELECT e FROM "
                    + entityClass.getSimpleName()
                    + " e "
                    + "WHERE e.client=:client "
                    + "ORDER BY e.created DESC, e.id DESC");

            query.setParameter("client", client);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return (List<E>) query.getResultList();
    }

    /**
     * @see org.mywms.service.BasicService#getList(org.mywms.model.Client,
     *      int, java.lang.String[])
     */
    @SuppressWarnings("unchecked")
    public List<E> getList(Client client, int limit, String[] orderBy) {
        StringBuffer strb = new StringBuffer();
        strb.append("SELECT e FROM ")
            .append(entityClass.getSimpleName())
            .append(" e ");

        if (client!=null && !client.isSystemClient()) {
            strb.append(" WHERE e.client=:client ");
        }

        boolean orderByUsed = false;
        for (String order: orderBy) {
            if (!orderByUsed) {
                strb.append("ORDER BY ");
                orderByUsed = true;
            }
            else {
                strb.append(", ");
            }
            strb.append(order);
        }

        Query query = manager.createQuery(strb.toString());

        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return (List<E>) query.getResultList();
    }

    /**
     * @see org.mywms.service.BasicService#getList(Client)
     */
    @SuppressWarnings("unchecked")
    public List<E> getList(Client client) {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append("SELECT e FROM " + entityClass.getSimpleName() + " e ");
        if (!client.isSystemClient()) {
            queryStr.append("WHERE e.client=:client ");
        }
        queryStr.append("ORDER BY e.created, e.id");

        Query query = manager.createQuery(queryStr.toString());

        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }

        return (List<E>) query.getResultList();
    }

    /**
     * Returns the class of the entity, this service provides.
     * 
     * @return the class of the entity, this service provides
     * @see org.mywms.service.BasicService#getEntityClass()
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

    /**
     * @see org.mywms.service.BasicService#flush()
     */
    public void flush() {
        manager.flush();
    }

    /**
     * @see org.mywms.service.BasicService#merge(java.lang.Object)
     */
    public E merge(E entity) {
        return manager.merge(entity);
    }

    public void deleteAll() {
        Query query =
            manager.createQuery("DELETE FROM " + entityClass.getSimpleName());
        query.executeUpdate();
    }
}
