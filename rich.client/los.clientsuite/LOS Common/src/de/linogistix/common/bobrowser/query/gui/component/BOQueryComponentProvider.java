/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.*;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import org.openide.WizardDescriptor;

/**
 *
 * @author trautm
 */
public interface BOQueryComponentProvider { 

    /**
     * The query service to be used
     * @return
     */
    BusinessObjectQueryRemote getQueryRemote();
    
    /**
     * Method that is invoked on BusinessObjectQueryRemote
     * @return
     */
    Method getMethod();
    
    /**
     * How does this provider obtain necessary parameters for invoking the method?
     * @return
     */
    DockingMode getDockingMode();
    
    /**
     * Depending on the DockingMode additional parameters might be obtained from another component that 
     * is integrated into an query panel, i.e. DockingMode is QUERYPANEL
     * @return
     */
    JComponent createComponent();
    
    /**
     * In DockingMode WIZARD_OWN let the provider create a wizard to ask the user
     * for query parameters.
     * 
     * @return
     * @throws java.lang.InstantiationException
     */
    WizardDescriptor createWizard() throws InstantiationException;
        
    /**
     * 
     * @param detail the QueryDetail from the BOQueryHeaderPanel, should be returned in Object Array
     * @param inplaceString  User input string typed in query combobox
     * @return an array of objects containig all parameters needed for the query method
     */
    public Object[] getQueryMethodParameters(QueryDetail detail, String inplaceString);
    

    public Class[] getQueryMethodParameterTypes();
    
    
    /**
     * 
     * @param qNode
     */
    public void setBOQueryNode(BOQueryNode qNode);

    /**
     * Adds a listener to changes in a provider implementing this interface
     * @param providerChangeEventListener
     */
    public void setProviderChangeEventListener(ProviderChangeEventListener providerChangeEventListener);
    
    
}
