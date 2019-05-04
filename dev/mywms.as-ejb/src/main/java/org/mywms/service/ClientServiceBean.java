/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;

/**
 * @see org.mywms.service.ClientService
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class ClientServiceBean
    extends BasicServiceBean<Client>
    implements ClientService
{
    private static final Logger log =
        Logger.getLogger(ClientServiceBean.class.getName());

    /**
     * @see org.mywms.service.ClientService#create(java.lang.String,
     *      java.lang.String)
     */
    public Client create(String name, String number, String code) {
        Client client = new Client();

        client.setName(name);
        client.setNumber(number);
        client.setCode(code);
        
        manager.persist(client);
//        manager.flush();

        return client;
    }

    /**
     * Returns the client with the specified number.
     * 
     * @param clientNumber the number of the clients
     * @return the client with the specified number
     * @throws EntityNotFoundException if the client could not be found
     */
    public Client getByNumber(String clientNumber){
    	
        Query query =
            manager.createQuery("SELECT c FROM "
                + Client.class.getSimpleName()
                + " c "
                + "WHERE c.number=:number");

        query.setParameter("number", clientNumber);

        try {
            Client c = (Client) query.getSingleResult();
            return c;
        }
        catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * This implementation looks for a client with id = 0 as the system
     * client. Currently this client as to be inserted in the database
     * with a sql script. There is a sql script tested on a postgres
     * database, but it should work on any database that adheres to the
     * sql standard. See postgres_insert_basicdata.sql
     * 
     * @see org.mywms.service.ClientService#getSystemClient()
     * @return the system client or null if there is no client with id =
     *         0
     */
    public Client getSystemClient() {
        Client cl;
        cl = manager.find(Client.class, new Long(0));

        if (cl == null) {
            // dump a comment
            log.error("no system client configured - myWMS may not operate properly at all - trying to create one");

            StringBuffer strb = new StringBuffer();

            strb.append("INSERT INTO mywms_client ").append(
                "(id, version, \"lock\", created, modified, ").append(
                "additionalcontent, name, number, email, phone, fax)").append(
                " VALUES ").append("(0, 0, 0, ").append("'2007-04-01'").append(
                ", ").append("'2007-04-01'").append(
                ", '', 'System Client', '0', '', '', '')");

            log.info("trying to insert the system client: " + strb.toString());

            Query systemClientQuery =
                manager.createNativeQuery(strb.toString());
            int entityCount = systemClientQuery.executeUpdate();
            if (entityCount == 0) {
                log.fatal("failed to insert the system client - error is unrecoverable");
                return null;
            }

            // flush the new insert
            manager.flush();

            // 2nd try to find the new client
            cl = manager.find(Client.class, new Long(0));
            if (cl == null) {
                log.fatal("failed to find the freshly inserted system client - this is very strange! myWMS may not operate properly at all");
                return null;
            }
        }

        return cl;
    }

    /**
     * @see org.mywms.service.ClientService#getByName(java.lang.String)
     */
    public Client getByName(String name) throws EntityNotFoundException {
        if (name == null) {
            throw new NullPointerException("getByName: parameter == null");
        }

        Query query =
            manager.createQuery("SELECT c FROM "
                + Client.class.getSimpleName()
                + " c "
                + "WHERE c.name=:name");

        query.setParameter("name", name);

        try {
            Client c = (Client) query.getSingleResult();
            return c;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_CLIENT_WITH_NAME);
        }
    }
}
