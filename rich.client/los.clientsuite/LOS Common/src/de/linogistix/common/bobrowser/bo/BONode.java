/*
 * BONode.java
 *
 * Created on 1. Dezember 2006, 00:50
 *
 * Copyright (c) 2006.2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * One node represents one type (class) of BasicEntity, eg.
 * StorageLocation.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BONode extends AbstractNode {

    private final static Logger log = Logger.getLogger(BONode.class.getName());
    private BO bo;

    public BONode(BO bo) throws IOException, IntrospectionException {
        super(Children.LEAF);
        this.bo = bo;
        if (bo == null) {
            log.log(Level.SEVERE, "The bo of the BONode cannot be null. Is the BO defined in the layer.xml?");
            throw new NullPointerException();
        }
        setName(bo.getName());
        setDisplayName(BundleResolve.resolve(
                new Class[]{bo.getBundleResolver(), CommonBundleResolver.class, CommonBundleResolver.class},
                getName(),
                null));

        setIconBaseWithExtension(bo.getIconPathWithExtension());

    }

    @Override
    public Action getPreferredAction() {
        return bo.getPreferredAction();
    }

    @Override
    public Action[] getActions(boolean b) {
        return bo.getActions(b);
    }

    public BOBeanNode getBoBeanNodeTemplate() {
        return getBo().getBoBeanNodeTemplate();
    }

    public Object getBusinessObjectTemplate() {
        return getBo().getBusinessObjectTemplate();
    }

    public void setBusinessObjectTemplate(Object e) {
        getBo().setBusinessObjectTemplate(e);
    }

    public BusinessObjectQueryRemote getQueryService() {
        return getBo().getQueryService();
    }

    public List<BOQueryComponentProvider> getQueryComponentProviders() {
        List<BOQueryComponentProvider> retList = new ArrayList<BOQueryComponentProvider>();
        retList.add(getBo().getDefaultBOQueryProvider());

        for (BOQueryComponentProvider cp : getBo().getQueryComponentProviders()) {
            if (!cp.equals(getBo().getDefaultBOQueryProvider())) {
                retList.add(cp);
            }
        }
        return retList;
    }

    public BusinessObjectCRUDRemote getCrudService() {
        return getBo().getCrudService();
    }

    public BO getBo() {
        return bo;
    }
}


