/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.gui.gui_builder.BOQueryQuickClientFilterPanel;
import de.linogistix.common.bobrowser.query.gui.gui_builder.BOQueryQuickFilterPanel;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.mywms.model.Client;

/**
 *
 * @author krane
 */
public class AutoCompletionQueryClientFilterProvider extends AutoCompletionQueryFilterProvider implements BOQueryComponentProvider {
    private static final Logger log = Logger.getLogger(AutoCompletionQueryClientFilterProvider.class.getName());
    
    public AutoCompletionQueryClientFilterProvider() {   }

    public AutoCompletionQueryClientFilterProvider(BusinessObjectQueryRemote queryRemote) {
        this.queryRemote = queryRemote;
        try {
            this.m = this.queryRemote.getClass().getDeclaredMethod("autoCompletion", new Class[]{String.class, Client.class, String[].class, QueryDetail.class});
        } catch (Throwable ex) {
            log.severe(ex.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public JComponent createComponent() {

        if (quickSerachPanel == null) {
            quickSerachPanel = new BOQueryQuickClientFilterPanel();
        }

        quickSerachPanel.removeProviderChangeEventListeners();
        quickSerachPanel.addProviderChangeEventListener(getProviderChangeEventListener());

        return quickSerachPanel;
    }


    @Override
    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        if (quickSerachPanel != null) {
            return new Object[]{quickSerachPanel.getQuickSearchString(), ((BOQueryQuickClientFilterPanel)quickSerachPanel).getClient(), ((BOQueryQuickFilterPanel)quickSerachPanel).getFilterStrings(), detail};
        } else {
            return new Object[]{"", null, null, detail};
        }
    }


    @Override
    public Class[] getQueryMethodParameterTypes() {
       return new Class[]{String.class, Client.class, String[].class, QueryDetail.class};
    }

    public void setClient( Client client ) {
        if( quickSerachPanel != null && ( quickSerachPanel instanceof BOQueryQuickClientFilterPanel ) ) {
            ((BOQueryQuickClientFilterPanel)quickSerachPanel).setClient(client);
        }
    }

}
