/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.BOQueryNode; 
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import org.mywms.model.BasicEntity;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class SingleResultQueryProvider<T extends BasicEntity> implements BOQueryComponentProvider{

    private BOQueryNode bOQueryNode;
    
    private BODTO<T> entityTO;

    public SingleResultQueryProvider(BODTO<T> to){
        
        entityTO = to;
    }
    
    public BODTO<T> getEntityTO() {
        return entityTO;
    }
    
    public BusinessObjectQueryRemote getQueryRemote() {
        return null;
    }

    public Method getMethod() {
        return null;
    }

    public DockingMode getDockingMode() {
        return DockingMode.PLAIN;
    }

    public JComponent createComponent() {
        return null;
    }

    public WizardDescriptor createWizard() throws InstantiationException {
        return null;
    }

    public Object[] getQueryMethodParameters(QueryDetail detail, String inplaceString) {
        return null;
    }

    public BOQueryNode getBOQueryNode() {
        return bOQueryNode;
    }

    public void setBOQueryNode(BOQueryNode bOQueryNode) {
        this.bOQueryNode = bOQueryNode;
    }
    
    @Override
    public String toString() {
        
        return NbBundle.getMessage(CommonBundleResolver.class, "SINGLE_RESULT_PROVIDER");
    }

    public void setProviderChangeEventListener(ProviderChangeEventListener providerChangeEventListener) {
        return ;
    }

    public Class[] getQueryMethodParameterTypes() {
        return new Class[0];
    }
    
    

}
