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
import org.mywms.model.UnitLoad;
import org.mywms.model.UnitLoadType;

/**
 * This interface declares the service for the entity UnitLoad. For this
 * service the method <code>get(String name)</code> will overwrite,
 * because the entity <code>UnitLaod</code> does not contain the
 * property <code>name</code>.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface UnitLoadService
    extends BasicService<UnitLoad>
{
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Searches for UnitLoads, which are of the specified UnitLoadType.
     * If specified client is the system client all existing UnitLoads
     * will be returned, otherwise the list will be limited to the
     * UnitLoads which belong to Client client.
     * 
     * @param client the Client UnitLoads are assigned to or the system
     *            client.
     * @param type the UnitLoadType the UnitLoads should have.
     * @return list of UnitLoads, which maybe empty. The list will be
     *         ordered by labels ascending.
     */
    List<UnitLoad> getListByUnitLoadType(Client client, UnitLoadType type);

    /**
     * Resolves a UnitLoad by its label and owning client.
     * 
     * @param client the client, the searched UnitLoad is assigned to
     * @param labelId the label to search for
     * @return
     * @throws EntityNotFoundException if there is no UnitLoad with
     *             label labelId assigned to Client client.
     */
    UnitLoad getByLabelId(Client client, String labelId)
        throws EntityNotFoundException;
}
