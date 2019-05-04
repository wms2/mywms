/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.windows;

/**
 *
 * @author artur
 */
/*
 * PreviewCanvas.java
 *
 * Created on 3. September 2007, 12:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.GraphicUtil;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 *
 * @author artur
 */
public class TipPanelCanvas extends JComponent {
//    private ImageSettings imageSettings = ImageSettings.getInstance();
//    private String imageText = NbBundle.getMessage(BundleResolver.class,"NoImageText");
    private String imageText = "No Icon";
    private ImageIcon imageCanvas = null;

    public TipPanelCanvas() {
        setDoubleBuffered(true);
        setOpaque(true);
        addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(java.awt.event.MouseEvent evt) {
                okButtonMousePressed(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
    }

    private void formMouseMoved(java.awt.event.MouseEvent evt) {
    }

    private void okButtonMousePressed(java.awt.event.MouseEvent evt) {
    }

    /*    public void setImage(String name) {
    ImageIcon img = imageSettings.getImage(name);
    imageCanvas = img;
    repaint();
    }*/
    
    public void setIcon(IconType icon) {
        imageCanvas = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(icon)));        
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getPreferredSize();
//        g.setColor(UIManager.getColor("Panel.background"));
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (imageCanvas == null) {
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(imageText);
            int h = fm.getAscent();
            g.setColor(UIManager.getColor("Panel.foreground"));
            g.drawRect(0, 0, dim.width - 1, dim.height - 1);
            g.drawString(imageText, (dim.width / 2) - (w / 2), (dim.height / 2) - (h / 2));
        } else {
            int w = imageCanvas.getIconWidth();
            int h = imageCanvas.getIconHeight();
            imageCanvas.paintIcon(this, g, (dim.width / 2) - (w / 2), (dim.height / 2) - (h / 2));
        }
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
        reset();
    }

    public Dimension getImagePreferredSize() {
        if (imageCanvas == null) {
            return new Dimension(32, 32);            
        } else {
            return new Dimension(imageCanvas.getIconWidth(), imageCanvas.getIconHeight());
        }    
    }
    
    public Dimension getPreferredSize() {
        if (imageCanvas == null) {
            return new Dimension(32, 32);
        } else {
            return new Dimension(imageCanvas.getIconWidth(),imageCanvas.getIconHeight());
        }   
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void reset() {
        imageCanvas = null;
        repaint();
    }
}