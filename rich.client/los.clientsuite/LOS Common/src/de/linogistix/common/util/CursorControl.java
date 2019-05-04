/*
 * CursorControl.java
 *
 * Created on 18. September 2006, 06:03
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.util;

import java.awt.Component;
import java.awt.Cursor;
import javax.swing.JFrame;
import org.openide.ErrorManager;
import org.openide.util.Mutex;
import org.openide.windows.WindowManager;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public final class CursorControl {
  
  
  //-------------------------------------------------------------------------------
  public static  void showWaitCursor() {
    //
    // waiting times
    //
    Mutex.EVENT.writeAccess(new Runnable() {
      public void run() {
        try {
          JFrame f = (JFrame)WindowManager.getDefault().getMainWindow();
          Component c = f.getGlassPane();
          c.setVisible(true);
          c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } catch (NullPointerException npe) {
          ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, npe);
        }
        
      }
    });
  }
  
  public static void showNormalCursor() {
    //
    // normal times
    //
    Mutex.EVENT.writeAccess(new Runnable() {
      public void run() {
        try {
          JFrame f = (JFrame)WindowManager.getDefault().getMainWindow();
          Component c = f.getGlassPane();
          c.setCursor(null);
          c.setVisible(false);
        } catch (NullPointerException npe) {
          ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, npe);
        }
      }
    });
  }
  
  
}
