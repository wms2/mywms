/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.gui.component.gui_builder.AbstractBOChooser;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import java.util.List;

/**
 *
 * @author jordan
 */
public class BOMultiSelectionChooser extends AbstractBOChooser {

    BOMultiSelectionChoosePanel boChoosePanel;

    public BOMultiSelectionChooser(Class c) {
        try {
            boChoosePanel = new BOMultiSelectionChoosePanel(c);
            add(boChoosePanel);
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }
    
    public BOMultiSelectionChooser(Class c, BOQueryModel qm) {
        try {
            boChoosePanel = new BOMultiSelectionChoosePanel(c, qm);
            add(boChoosePanel);
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    public void actionPerformed(final java.awt.event.ActionEvent ev) {
                
        dialog.dispose();
    }

    public List<BODTO> getSelectedValues() {
        return boChoosePanel.getSelectedValues();
    }

    public void reload() {
        boChoosePanel.queryPanel.reload();
    }

    public BO getBo() {
        return boChoosePanel.boNode.getBo();
    }
}
