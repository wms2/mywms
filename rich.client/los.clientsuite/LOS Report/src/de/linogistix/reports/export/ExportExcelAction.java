/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.export;

import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Action which shows BOBrowser component.
 */
public class ExportExcelAction extends AbstractAction {

    static final String ICON_PATH = "de/linogistix/reports/res/icon/Excel.png";

    public ExportExcelAction() {
        super(NbBundle.getMessage(ReportsBundleResolver.class, "ExcelExportAction"));
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(ExportExcelAction.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
       //which topcomponent?
    }
}
