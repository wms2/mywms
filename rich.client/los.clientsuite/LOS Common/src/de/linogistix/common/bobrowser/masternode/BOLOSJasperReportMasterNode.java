/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.dto.LOSJasperReportTO;
import de.linogistix.los.query.BODTO;

import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * @author krane
 *
 */
public class BOLOSJasperReportMasterNode extends BOMasterNode {

    LOSJasperReportTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSJasperReportMasterNode(BODTO d) throws IntrospectionException {
        super(d);
        to = (LOSJasperReportTO) d;
    }

    /** Creates a new instance of BODeviceNode */
    public BOLOSJasperReportMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSJasperReportTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {

            sheet = new Sheet.Set();

            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), CommonBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<Boolean> source = new BOMasterNodeProperty<Boolean>("sourceAttached", Boolean.class, to.isSourceAttached(), CommonBundleResolver.class);
            sheet.put(source);
            BOMasterNodeProperty<Boolean> compiled = new BOMasterNodeProperty<Boolean>("compiled", Boolean.class, to.isCompiled(), CommonBundleResolver.class);
            sheet.put(compiled);


        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<Boolean> compiled = new BOMasterNodeProperty<Boolean>("compiled", Boolean.class, Boolean.FALSE, CommonBundleResolver.class);
        BOMasterNodeProperty<Boolean> source = new BOMasterNodeProperty<Boolean>("sourceAttached", Boolean.class, Boolean.FALSE, CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber, source, compiled
        };

        return props;
    }
}
