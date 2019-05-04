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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public interface Constants {

    static final String LINOGISTIX_LOGO_IMAGE = "de/linogistix/reference/welcome/resources/linogistix_logo.jpg"; 
    static final String MYWMS_LOGO_IMAGE = "de/linogistix/reference/welcome/resources/mywms_logo.png"; 

    static final String COLOR_BACKGROUND = "BackgroundColor"; 
    static final String COLOR_BORDER = "BorderColor"; 
    static final String COLOR_HEADER = "HeaderForegroundColor"; 
    static final String COLOR_TAB_BACKGROUND = "TabBackgroundColor"; 
    static final String COLOR_TAB_BORDER1 = "TabBorder1Color"; 
    static final String COLOR_TAB_BORDER2 = "TabBorder2Color";         
    static final String COLOR_SECTION_HEADER = "SectionHeaderColor"; 
    static final String COLOR_BIG_BUTTON = "BigButtonColor"; 
    static final String COLOR_BOTTOM_BAR = "BottomBarColor"; 
    
    static final int FONT_SIZE = Utils.getDefaultFontSize();
    static final String FONT_NAME = BundleSupport.getLabel( "FONT_NAME" ); 
    static final Font BUTTON_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 );
    static final Font TAB_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 ); 
    static final Font SECTION_HEADER_FONT = new Font( FONT_NAME, Font.BOLD, FONT_SIZE+7 ); 
    static final Font GET_STARTED_FONT = new Font( FONT_NAME, Font.PLAIN, FONT_SIZE+1 ) ; 
    static final Font CONTENT_HEADER_FONT = new Font( FONT_NAME, Font.BOLD, FONT_SIZE+13 ) ; 

    static final String IMAGE_CONTENT_BANNER = "de/linogistix/reference/welcome/resources/content_banner.png"; 

    static final String IMAGE_TAB_SELECTED = "de/linogistix/reference/welcome/resources/tab_selected.png"; 
    static final String IMAGE_TAB_ROLLOVER = "de/linogistix/reference/welcome/resources/tab_rollover.png"; 

    static final String BROKEN_IMAGE = "de/linogistix/reference/welcome/resources/broken_image.gif"; 

    static final String LINK_COLOR = "LinkColor"; 
    static final String VISITED_LINK_COLOR = "VisitedLinkColor";
    static final String LINK_IN_FOCUS_COLOR = "LinkInFocusColor"; 
    static final Stroke LINK_IN_FOCUS_STROKE = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[] {0, 2}, 0);
            
    static final int TEXT_INSETS_LEFT = 10;
    static final int TEXT_INSETS_RIGHT = 10;

    static final Border HEADER_TEXT_BORDER = BorderFactory.createEmptyBorder( 1, TEXT_INSETS_LEFT, 1, TEXT_INSETS_RIGHT );
    
    static final int START_PAGE_MIN_WIDTH = 700;
}
