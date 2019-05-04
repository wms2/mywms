/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.gui_builder;

import de.linogistix.common.bobrowser.query.gui.component.ProviderChangeEventListener;
import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import org.mywms.model.Client;

/**
 *
 * @author krane
 */
public class BOQueryQuickClientFilterPanel extends BOQueryQuickFilterPanel {
    private Map<Integer,JComboBox> filterMap = null;
    private static final Logger log = Logger.getLogger(BOQueryQuickClientFilterPanel.class.getName());

    public BOQueryQuickClientFilterPanel() {
        addClient();
    }
    
    private void selectClientAction(PropertyChangeEvent pce) {
        for (ProviderChangeEventListener l : getProviderChangeEventListeners()) {
            if( l == null ) {
                log.info("listener == null");
                continue;
            }
            l.reloadRequest();
        }
    }
    
    private void selectAction(java.awt.event.ActionEvent evt) {
        for (ProviderChangeEventListener l : getProviderChangeEventListeners()) {
            if( l == null ) {
                log.info("listener == null");
                continue;
            }
            l.reloadRequest();
        }
    }

    private BOAutoFilteringComboBox<Client> clientComboBox;
    public void addClient() {

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));

        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(10,25));
        sep.setOrientation(JSeparator.VERTICAL);
        sep.setBorder(new EmptyBorder(1,2,1,8));
        filterPanel.add(sep);

        JLabel filterLabel = new JLabel();
        filterLabel.setText("Client:");
        filterPanel.add(filterLabel);

        clientComboBox = new BOAutoFilteringComboBox<Client>();
        clientComboBox.setBoClass(Client.class);
        clientComboBox.initAutofiltering();
        clientComboBox.setEditorLabelTitle("");
        clientComboBox.addItemChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                selectClientAction(pce);
            }
        });

        filterPanel.add(clientComboBox);

        add(filterPanel);
        this.invalidate();
    }


    public Client getClient() {
        return clientComboBox == null ? null : clientComboBox.getSelectedAsEntity();
    }

    public void setClient( Client client) {
        if( clientComboBox == null ) {
            return;
        }
        clientComboBox.addItem(client);
    }

//    public String[] getFilterStrings() {
//        if( filterMap == null || filterMap.size()==0 ) {
//            return null;
//        }
//        String[] ret = new String[filterMap.size()];
//
//        int i = 0;
//        for( JComboBox combo : filterMap.values() ) {
//            ret[i] = ((SelectItem)combo.getSelectedItem()).key;
//            i++;
//        }
//
//        return ret;
//    }


//    private class SelectItem {
//        private String text;
//        private String key;
//        SelectItem(String text, String key) {
//            this.text = text;
//            this.key = key;
//        }
//        @Override
//        public String toString() {
//            return text;
//        }
//        public String getKey() {
//            return key;
//        }
//    }

}
