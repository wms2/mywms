/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.bobrowser.bo.editor.BOEditorChooseFromServicePanel;
import de.linogistix.common.bobrowser.bo.editor.BOEditorChoosePanel;
import de.linogistix.common.gui.component.gui_builder.AbstractBOChooser;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.common.util.GraphicUtil;
import de.linogistix.los.query.BODTO;
import java.util.List;
import javax.swing.JLabel;
import org.mywms.facade.FacadeException;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class BOChooser extends AbstractBOChooser {

    BOEditorChoosePanel bo;

    public BOChooser(Class c) {
        try {
            bo = new BOEditorChoosePanel(c);
            add(bo);
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }
    
    public BOChooser(Class entityClass, BOQueryModel model){
        
        bo = new BOEditorChoosePanel(entityClass, model);
        add(bo);
    }
    
    public BOChooser(List dtos) {
        try {
            if (dtos != null && dtos.size() > 0){
                bo = new BOEditorChooseFromServicePanel(dtos);
                add(bo);
            } else{
                getOKButton().setEnabled(false);
                JLabel l = new JLabel(
                        NbBundle.getMessage(CommonBundleResolver.class,"AbstractBOChooser.NO_ENTITY"), 
                        GraphicUtil.getInstance().getIcon(IconType.WARNING),
                        JLabel.CENTER);
                add(l);
            }
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    public void actionPerformed(final java.awt.event.ActionEvent ev) {
        
        if(ev.getActionCommand().equals(OK_BUTTON) 
           && (getValue() == null || getValue().getName().equals("Template")))
        {
            FacadeException ex = new FacadeException("No value selected", "NO_VALUE_SELECTED", new Object[0]);
            ex.setBundleResolver(de.linogistix.common.res.CommonBundleResolver.class);
            ExceptionAnnotator.annotate(ex);
            return;
        }
        
        dialog.dispose();
    }

    public BODTO getValue() {
        BODTO obj = bo.getBoDTO();
        return obj;
    }
    
    public void setSelection(String name){
        bo.setSelection(name);
    }
}
