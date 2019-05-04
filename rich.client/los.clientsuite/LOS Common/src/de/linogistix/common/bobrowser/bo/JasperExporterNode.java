/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo;

import org.openide.nodes.Node.PropertySet;

/**
 *Siganals that node can be exported using Jasper
 * @author andreas
 */
public interface JasperExporterNode {

    /** The bean holding the property sets, see getProeprtySets*/
    public Object getExportBeanObject();

    /**the propertySet, each property result in one export column */
    public PropertySet[] getExportPropertySets();
    

}
