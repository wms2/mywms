/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 *
 * @author artur
 */
public class WithoutArrowComboBox extends JComboBox {
    static class MetalComboBoxUIWithoutArrow extends MetalComboBoxUI{

        @Override
        protected JButton createArrowButton() {
            JButton button =  super.createArrowButton();
            button.setVisible(false);
            return button;
        }
        
        
        
    }

    public WithoutArrowComboBox() {
        setUI(new MetalComboBoxUIWithoutArrow());
    }

    public WithoutArrowComboBox(Vector<?> items) {
        super(items);
        setUI(new MetalComboBoxUIWithoutArrow());        
    }

    public WithoutArrowComboBox(Object[] items) {
        super(items);
        setUI(new MetalComboBoxUIWithoutArrow());        
    }

    public WithoutArrowComboBox(ComboBoxModel aModel) {
        super(aModel);
        setUI(new MetalComboBoxUIWithoutArrow());        
    }

    
}
