/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.Document;

/**
 * @see org.mywms.service.DocumentService
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class DocumentServiceBean
    extends BasicServiceBean<Document>
    implements DocumentService
{
    /**
     * @see org.mywms.service.DocumentService#getByName(org.mywms.model.Client,
     *      java.lang.String)
     */
    public Document getByName(Client client, String name)
        throws EntityNotFoundException
    {
        if (client == null || name == null) {
            throw new NullPointerException("getByName: parameter == null");
        }

        Query query =
            manager.createQuery("SELECT d FROM "
                + Document.class.getSimpleName()
                + " d "
                + "WHERE d.name=:name "
                + "AND d.client=:client ");

        query.setParameter("name", name);
        query.setParameter("client", client);

        try {
            Document d = (Document) query.getSingleResult();
            return d;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_DOCUMENT_WITH_NAME);
        }
    }

    /**
     * @see org.mywms.service.DocumentService#create(Client, String)
     */
    @SuppressWarnings("unchecked")
    public Document create(Client client, String name)
        throws UniqueConstraintViolatedException
    {
        if (client == null || name == null) {
            throw new NullPointerException();
        }

        Query query =
            manager.createQuery("SELECT do FROM "
                + Document.class.getSimpleName()
                + " do "
                + "WHERE do.client=:cl AND do.name=:na");
        query.setParameter("cl", client);
        query.setParameter("na", name);

        List<Document> docs = query.getResultList();

        if (docs.size() > 0) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.DOCUMENT_ALREADY_EXISTS);
        }

        Document doc = new Document();
        doc.setClient(client);
        doc.setName(name);

        manager.persist(doc);
        manager.flush();

        return doc;
    }

    /**
     * @see org.mywms.service.DocumentService#getList(org.mywms.model.Client,
     *      java.util.Date, java.util.Date, java.lang.String,
     *      java.lang.String, int)
     */
    @SuppressWarnings("unchecked")
    public List<Document> getList(
        Client client,
        Date startDate,
        Date endDate,
        String name,
        String type,
        int limit)
    {
        if (client == null) {
            throw new NullPointerException();
        }

        StringBuffer strb = new StringBuffer();

        strb.append("SELECT do FROM ")
            .append(Document.class.getSimpleName())
            .append(" do ");

        // build the query string
        boolean whereClauseUsed = false;
        if (!client.isSystemClient()) {
            strb.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;

            strb.append(" do.client=:client ");
        }
        if (startDate != null) {
            strb.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;

            strb.append(" do.created>=:startDate ");
        }
        if (endDate != null) {
            strb.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;

            strb.append(" do.created<=:endDate ");
        }
        if (name != null) {
            strb.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;

            strb.append(" do.name=:name ");
        }
        if (type != null) {
            strb.append(whereClauseUsed ? "AND" : "WHERE");
            whereClauseUsed = true;

            strb.append(" do.type=:type ");
        }

        strb.append("ORDER BY do.created DESC, do.id DESC");

        Query query = manager.createQuery(strb.toString());

        // prepare the query
        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }
        if (name != null) {
            query.setParameter("name", name);
        }
        if (type != null) {
            query.setParameter("type", type);
        }

        // set the limit
        if (limit > 0) {
            query.setMaxResults(limit);
        }

        // return the result
        return query.getResultList();
    }
}
