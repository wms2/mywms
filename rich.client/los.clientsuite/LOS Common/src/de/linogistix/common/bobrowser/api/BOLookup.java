/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.api;

import de.linogistix.common.bobrowser.bo.BO;
import java.util.Collection;

/**
 * Delivers an instance of {@link BO} for a given class, e.g. StorageLocation.class.
 *
 * @author trautm
 */
public interface BOLookup {

    public Collection<BO> getBOs();

    void addBO(Class c, BO bo);

    void removeBO(Class c);

    Object lookup(Class c);
}
