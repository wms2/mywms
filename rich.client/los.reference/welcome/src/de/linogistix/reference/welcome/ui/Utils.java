/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package de.linogistix.reference.welcome.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.UIManager;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

public class Utils {

    /** Creates a new instance of Utils */
    private Utils() {
    }

    public static Graphics2D prepareGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Map rhints = (Map)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
        if( rhints == null && Boolean.getBoolean("swing.aatext") ) {
             g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        } else if( rhints != null ) {
            g2.addRenderingHints( rhints );
        }
        return g2;
    }

    public static void showURL(String href) {
        try {
            HtmlBrowser.URLDisplayer displayer = HtmlBrowser.URLDisplayer.getDefault();
            if (displayer != null) {
                displayer.showURL(new URL(href));
            }
        } catch (Exception e) {}
    }

    static int getDefaultFontSize() {
        Integer customFontSize = (Integer)UIManager.get("customFontSize");
        if (customFontSize != null) {
            return customFontSize.intValue();
        } else {
            Font systemDefaultFont = UIManager.getFont("TextField.font");
            return (systemDefaultFont != null)
                ? systemDefaultFont.getSize()
                : 11;
        }
    }

    public static Action findAction( String key ) {
        FileObject fo = FileUtil.getConfigFile(key);
        
        if (fo != null && fo.isValid()) {
            try {
                DataObject dob = DataObject.find(fo);
                InstanceCookie ic = dob.getCookie(InstanceCookie.class);
                
                if (ic != null) {
                    Object instance = ic.instanceCreate();
                    if (instance instanceof Action) {
                        Action a = (Action) instance;
                        return a;
                    }
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
                return null;
            }
        }
        return null;
    }
    
    public static Color getColor( String resId ) {
        ResourceBundle bundle = NbBundle.getBundle("de.linogistix.reference.welcome.Bundle");
        try {
            Integer rgb = Integer.decode(bundle.getString(resId));
            return new Color(rgb.intValue());
        } catch( NumberFormatException nfE ) {
            ErrorManager.getDefault().notify( ErrorManager.INFORMATIONAL, nfE );
            return Color.BLACK;
        }
    }

    public static Color getBackgroundColor() {
        return getColor( Constants.COLOR_BACKGROUND );
    }

    public static Color getBorderColor() {
        return getColor( Constants.COLOR_BORDER );
    } 
    
    public static Color getTopBarColor() {
        return getColor( Constants.COLOR_TAB_BACKGROUND );
    }
    
    public static Color getBottomBarColor() {
        return getColor( Constants.COLOR_BOTTOM_BAR ); 
    }

    public static Color getTabBorder1Color() {
        return getColor( Constants.COLOR_TAB_BORDER1 );
    }

    public static Color getTabBorder2Color() {
        return getColor( Constants.COLOR_TAB_BORDER2 );
    }    
    
    public static Color getLinkColor() {
        return getColor( Constants.LINK_COLOR );
    }

    public static Color getFocusedLinkColor() {
        return getColor( Constants.LINK_IN_FOCUS_COLOR );
    }

    public static Color getVisitedLinkColor() { 
        return getColor( Constants.VISITED_LINK_COLOR );
    }

    public static boolean isDefaultButtons() {
        return UIManager.getBoolean( "nb.startpage.defaultbuttonborder" );
    }

    public static boolean isSimpleTabs() {
        return UIManager.getBoolean( "nb.startpage.simpletabs" );
    }

    /**
     * Try to extract the URL from the given DataObject using reflection.
     * (The DataObject should be URLDataObject in most cases)
     */
    public static String getUrlString(DataObject dob) {
        try {
            Method m = dob.getClass().getDeclaredMethod( "getURLString", new Class[] {} );
            m.setAccessible( true );
            Object res = m.invoke( dob );
            if( null != res ) {
                return res.toString();
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }
}
