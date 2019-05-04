/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.BOQueryNode;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;

/**
 *
 * @author trautm
 */
public class QueryCombobox extends JComboBox {

    private static final Logger log = Logger.getLogger(QueryCombobox.class.getName());
    BOQueryNode bOQueryNode;
    private BOQueryComponentProvider provider;
    private String searchString;

    
    List<ProviderChangeEventListener> listeners = new ArrayList<ProviderChangeEventListener>();

    public QueryCombobox(BOQueryNode boQueryNode) {
        this.bOQueryNode = boQueryNode;
        initComponent();
        initQueryServices();
    }

    private void initComponent() {

        setEditable(false);
        setPreferredSize(new java.awt.Dimension(150, 22));
        addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //queriesActionPerformed(evt);
                providerChangedEvent(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyReleased(java.awt.event.KeyEvent evt) {
                //queriesKeyReleased(evt);
                providerChangedEvent(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                //queriesMouseClicked(evt);
                providerChangedEvent(evt);
            }
        });
        addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                //queriesPopupMenuWillBecomeInvisible(evt);
                providerChangedEvent(evt);
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

    }

    boolean testEditable() {
        boolean ret = false;
        switch(this.provider.getDockingMode()){
            case INLPLACE:
                ret = true;
                break;
            default:
                ret = false;    
        }
        
        return ret;
    }

    private void initQueryServices() {

        List<BOQueryComponentProvider> providerList;
        providerList = bOQueryNode.getModel().getQueryComponentProviders();
        
        try {
            for (BOQueryComponentProvider m : providerList) {
                addItem(m);
            }
            invalidate();
            validate();
            setSelectedIndex(0);
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
        }
    }

    private void providerChangedEvent(EventObject evt) {

        BOQueryComponentProvider prov = (BOQueryComponentProvider) getSelectedItem();
        String s = "";
        
        
        if (getEditor().getItem() instanceof String){
            s = (String) getEditor().getItem();
        }
        
        if (prov == this.provider) {
            if (s.equals(this.searchString)){
                
            } else{
                this.searchString = s;
                fireSearchStringChanged(provider);
            }
        } else {
            this.provider = prov;
            setEditable(testEditable());
            fireProviderSelected(this.provider);
        }
       
    }

    private void fireProviderSelected(BOQueryComponentProvider provider) {
        for (ProviderChangeEventListener l : listeners) {
            l.providerSelected(provider);
        }
    }
    
    private void fireSearchStringChanged(BOQueryComponentProvider provider) {
        for (ProviderChangeEventListener l : listeners) {
            l.searchStringChanged(provider, searchString);
        }
    }

    public void addProviderChangeEventListener(ProviderChangeEventListener l) {
        this.listeners.add(l);
    }

    public void removeProviderChangeEventListener(ProviderChangeEventListener l) {
        this.listeners.remove(l);
    }
    
}
