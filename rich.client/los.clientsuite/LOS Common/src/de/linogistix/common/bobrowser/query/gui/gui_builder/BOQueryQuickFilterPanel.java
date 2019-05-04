/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.gui_builder;

import de.linogistix.common.bobrowser.query.gui.component.ProviderChangeEventListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author krane
 */
public class BOQueryQuickFilterPanel extends AbstractBOQueryQuickSearchPanel {
    private Map<Integer,JComboBox> filterMap = null;
    private static final Logger log = Logger.getLogger(BOQueryQuickFilterPanel.class.getName());

    private void selectAction(java.awt.event.ActionEvent evt) {
        for (ProviderChangeEventListener l : getProviderChangeEventListeners()) {
            if( l == null ) {
                log.info("listener == null");
                continue;
            }
            l.reloadRequest();
        }
    }

    public void addFilter(int filterId, String title) {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));

        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(10,25));
        sep.setOrientation(JSeparator.VERTICAL);
        sep.setBorder(new EmptyBorder(1,2,1,8));
        filterPanel.add(sep);

        JLabel filterLabel = new JLabel();
        filterLabel.setText(title);
        filterPanel.add(filterLabel);

        JComboBox filterCombo = new JComboBox();
        filterCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                selectAction(ae);
            }
        });
        filterPanel.add(filterCombo);

        if( filterMap == null ) {
            filterMap = new HashMap<Integer, JComboBox>();
        }

        filterMap.put(filterId, filterCombo);

        add(filterPanel);
        this.invalidate();
    }

    public void addFilterValue(int filterId, String text, String key) {
        if( filterMap == null ) {
            log.severe("No filter registered!");
            return;
        }
        JComboBox combo = filterMap.get(filterId);
        if( combo == null ) {
            log.severe("filter not registered! id="+filterId);
            return;
        }
        combo.addItem(new SelectItem(text,key));
    }

    public void setFilterSelected( int filterId, int selectedIndex ) {
        if( filterMap == null ) {
            log.severe("No filter registered!");
            return;
        }
        JComboBox combo = filterMap.get(filterId);
        if( combo == null ) {
            log.severe("filter not registered! id="+filterId);
            return;
        }
        if( selectedIndex >= combo.getItemCount() ) {
            log.severe("filter index not available! id="+filterId+", index="+selectedIndex);
            return;
        }
        combo.setSelectedIndex(selectedIndex);
    }

    public String[] getFilterStrings() {
        if( filterMap == null || filterMap.size()==0 ) {
            return null;
        }
        String[] ret = new String[filterMap.size()];

        int i = 0;
        for( JComboBox combo : filterMap.values() ) {
            ret[i] = ((SelectItem)combo.getSelectedItem()).key;
            i++;
        }

        return ret;
    }


    private class SelectItem {
        private String text;
        private String key;
        SelectItem(String text, String key) {
            this.text = text;
            this.key = key;
        }
        @Override
        public String toString() {
            return text;
        }
        public String getKey() {
            return key;
        }
    }

}
