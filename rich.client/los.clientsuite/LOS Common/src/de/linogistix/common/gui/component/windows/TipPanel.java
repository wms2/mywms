/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.windows;

import de.linogistix.common.gui.gui_builder.windows.AbstractTipPanel;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.GraphicUtil;
import java.awt.Dimension;
import javax.swing.ImageIcon;

/**
 *
 * @author artur
 */
public class TipPanel extends AbstractTipPanel {


    public TipPanel(IconType icon, String text) {
        setPreferredSize(GraphicUtil.getInstance().getIconPreferredSize(icon));
        setIcon(icon);
        setText(text);       
    }
    
    public TipPanel(IconType icon, boolean visible) {
        setPreferredSize(GraphicUtil.getInstance().getIconPreferredSize(icon));
        if (visible == false) {
            tipLabel.setText("");
            super.setVisible(false);
        }
    }
    
    public void setIcon(IconType icon) {
        canvas.setIcon(icon);
        refresh();
    }
    
    public void setText(String text) {
        tipLabel.setText(text);
        refresh();        
    }
    
    public void setTip(IconType icon, String text) {
        setIcon(icon);
        setText(text);  
        refresh();
    }
    
    private void refresh() {
        canvas.repaint();
        super.setVisible(true);                
    }
    
/*    private void showTip() {
        setTipPanelBackground(Color.WHITE);        
        visiblePanel(true);  
        setVisible(true);
        canvas.repaint();
    }*/
    
/*    public void reset(IconType icon) {
        ImageIcon image = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(icon)));        
        setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
        setTipPanelBackground();        
        visiblePanel(false);
    }*/
    
}
