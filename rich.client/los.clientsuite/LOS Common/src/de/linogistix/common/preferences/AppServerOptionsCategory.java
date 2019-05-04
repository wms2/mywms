/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.preferences;

import de.linogistix.common.res.CommonBundleResolver;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public final class AppServerOptionsCategory extends OptionsCategory {
    
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage("de/linogistix/common/res/icon/UserLogin32.png"));
    }
    
    public String getCategoryName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "AppServerOptionsCategory_Name");
    }
    
    public String getTitle() {
        return NbBundle.getMessage(CommonBundleResolver.class, "AppServerOptionsCategory_Title");
    }
    
    public OptionsPanelController create() {
        return new AppPreferencesController("appserver");
    }

//    public String getDisplayName() {
//        return NbBundle.getMessage(BundleResolver.class, "JNDIOptionsCategory_Title");
//    }
//
//    public String getTooltip() {
//        return NbBundle.getMessage(BundleResolver.class, "JNDIOptionsCategory_Title");
//    }
    
}
