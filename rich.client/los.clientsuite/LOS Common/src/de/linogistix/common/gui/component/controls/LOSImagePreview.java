/*
 * Copyright (c) 2011 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */

package de.linogistix.common.gui.component.controls;

import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;

public class LOSImagePreview extends JComponent implements PropertyChangeListener {
    ImageIcon icon = null;

    public LOSImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 75));
        fc.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if( JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName) ) {
            icon = null;
            repaint();
        } 
        else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
            File file = (File) e.getNewValue();
            if (file == null) {
                icon = null;
            }
            else {
                icon = new ImageIcon(file.getPath());
                if( icon != null &&  icon.getIconWidth() > 100 ) {
                    icon = new ImageIcon(icon.getImage().getScaledInstance(100, -1, Image.SCALE_FAST));
                }
            }
            repaint();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        if( icon == null ) {
            return;
        }

        int x = getWidth()/2 - icon.getIconWidth()/2;
        if( x < 10 ) {
            x = 10;
        }

        int y = getHeight()/2 - icon.getIconHeight()/2;
        if( y < 10 ) {
            y = 10;
        }
        icon.paintIcon(this, g, x, y);
    }
}
