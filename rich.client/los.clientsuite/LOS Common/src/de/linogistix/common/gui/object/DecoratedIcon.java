/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.object;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
 * Taken from a code snipplet of R.J. Lorimer from
 * http://www.javalobby.org/forums/thread.jspa?threadID=16326&tstart=15
 *
 * @author trautm
 */
public class DecoratedIcon implements Icon {
  
  private Icon originalIcon;
  private Icon decorationIcon;
  private int xDiff;
  private int yDiff;
  private Location location;
 
  public final static Icon LOCK_ICON =  new ImageIcon(ImageUtilities.loadImage("de/linogistix/common/res/icon/lockedstate.gif"));  
  
  
  // Java 1.5 enumeration
  public enum Location { UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT };
 
  public DecoratedIcon(Icon original, Icon decoration) {
    this(original, decoration, Location.LOWER_RIGHT);
  }
  
  public DecoratedIcon(Icon original, Icon decoration, Location loc) {
  
    this.location = location;
    this.originalIcon = original;
    this.decorationIcon = decoration;
    if(
      decoration.getIconHeight() > original.getIconHeight() ||
      decoration.getIconWidth() > original.getIconWidth()) {
      throw new IllegalArgumentException("Decoration must be smaller than the original");
    }
    this.xDiff = originalIcon.getIconWidth() - decorationIcon.getIconWidth();
    this.yDiff = originalIcon.getIconHeight() - decorationIcon.getIconHeight();
  }
  
  public int getIconHeight() {
    return originalIcon.getIconHeight();
  }
  
  public int getIconWidth() {
    return originalIcon.getIconWidth();
  }
 
  public void paintIcon(Component owner, Graphics g, int x, int y) {
    // paint original first
    originalIcon.paintIcon(owner, g, x, y);
    
    int decorationX = x;
    int decorationY = y;
    // augment x.
    if(location == Location.UPPER_RIGHT || location == Location.LOWER_RIGHT) {
      decorationX += xDiff;
    }
    // augment y.
    if(location == Location.LOWER_LEFT || location == Location.LOWER_RIGHT) {
      decorationY += yDiff;
    }
    
    decorationIcon.paintIcon(owner, g, decorationX, decorationY);
  }
  
   public Image getImage() {
        int w = getIconWidth();
        int h = getIconHeight();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h);
        Graphics2D g = image.createGraphics();
        paintIcon(null, g, 0, 0);
        g.dispose();
        return image;
    }
  
  public final static class LockedIcon extends DecoratedIcon{
      public LockedIcon(Icon orig){
          super(orig, LOCK_ICON);
      }
  }
  
 
}
