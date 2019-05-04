/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.GraphicUtil;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author trautm
 */
public class LOSIconButton extends JButton{

    public LOSIconButton(IconType type){
        Icon icon;
        icon = GraphicUtil.getInstance().getIcon(type);
        
        setMargin(new Insets(2, 2, 2, 2));
        setIcon(icon);
    }
    
    
            
            
}
