/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.gui.gui_builder.BOQueryQuickFilterPanel;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import java.util.logging.Logger;
import javax.swing.JComponent;

/**
 *
 * @author krane
 */
public class AutoCompletionQueryFilterProvider extends AutoCompletionQueryProvider implements BOQueryComponentProvider {
    private static final Logger log = Logger.getLogger(AutoCompletionQueryFilterProvider.class.getName());
    
    public AutoCompletionQueryFilterProvider() {   }

    public AutoCompletionQueryFilterProvider(BusinessObjectQueryRemote queryRemote) {
        this.queryRemote = queryRemote;
        try {
            this.m = this.queryRemote.getClass().getDeclaredMethod("autoCompletion", new Class[]{String.class, String[].class, QueryDetail.class});
        } catch (Throwable ex) {
            log.severe(ex.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public JComponent createComponent() {

        if (quickSerachPanel == null) {
            quickSerachPanel = new BOQueryQuickFilterPanel();
        }

        quickSerachPanel.removeProviderChangeEventListeners();
        quickSerachPanel.addProviderChangeEventListener(getProviderChangeEventListener());

        return quickSerachPanel;
    }


    @Override
    public Object[] getQueryMethodParameters(QueryDetail detail, String queryStr) {
        if (quickSerachPanel != null) {
            return new Object[]{quickSerachPanel.getQuickSearchString(), ((BOQueryQuickFilterPanel)quickSerachPanel).getFilterStrings(), detail};
        } else {
            return new Object[]{"", null, detail};
        }
    }


    @Override
    public Class[] getQueryMethodParameterTypes() {
       return new Class[]{String.class, String[].class, QueryDetail.class};
    }
    
    public void addFilter(int filterId, String title) {
        if (quickSerachPanel == null) {
            createComponent();
        }
        ((BOQueryQuickFilterPanel)quickSerachPanel).addFilter(filterId, title);
    }

    public void addFilterValue(int filterId, String text, String key) {
        if (quickSerachPanel == null) {
            createComponent();
        }
        ((BOQueryQuickFilterPanel)quickSerachPanel).addFilterValue(filterId, text, key);
    }

    public void setFilterSelected( int filterId, int selectedIndex ) {
        if (quickSerachPanel == null) {
            createComponent();
        }
        ((BOQueryQuickFilterPanel)quickSerachPanel).setFilterSelected(filterId, selectedIndex);
    }

}
