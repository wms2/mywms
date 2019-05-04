/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.model.Document;

/**
 * This interface declares the service for the entity
 * PluginConfiguration. For this service it is save to call the
 * <code>get(String name)</code> method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Local
public interface DocumentService
    extends BasicService<Document>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Returns the document with the specified name
     * 
     * @parem client the client this document is assigned to
     * @param name the name of the document
     * @return the found document
     * @throws EntityNotFoundException if the specified document was not
     *             found
     */
    Document getByName(Client client, String name)
        throws EntityNotFoundException;

    /**
     * Checks if there is already a document with Name name assigned to
     * Client client. If the name is valid, a new Document will be
     * created, assigned to Client client and added to persistence
     * context.
     * 
     * @param client the owning client of the document
     * @param name the name of the new document
     * @return the new Document
     */
    Document create(Client client, String name)
        throws UniqueConstraintViolatedException;

    /**
     * Returns a list of the documents matching the specified
     * parameters.
     * 
     * @param client the client of the caller
     * @param startDate the earliest creation date of documents to be
     *            found; may be null if not applicable
     * @param endDate the latest creation date of documents to be found;
     *            may be null if not applicable
     * @param name the exact name of the document; may be null if not
     *            applicable
     * @param type the type of the document; may be null if not
     *            applicable
     * @param limit the maximum number of documents returned; will
     *            return the full set if 0
     * @return a list of matching documents
     */
    List<Document> getList(
        Client client,
        Date startDate,
        Date endDate,
        String name,
        String type,
        int limit);
}
