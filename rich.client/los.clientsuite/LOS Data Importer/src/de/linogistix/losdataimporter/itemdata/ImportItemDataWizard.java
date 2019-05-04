/*
 * OrderByWizard.java
 *
 * Created on 27. Juli 2006, 00:27
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.losdataimporter.itemdata;

import de.linogistix.common.util.CursorControl;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.losdataimporter.res.BundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class ImportItemDataWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(ImportItemDataWizard.class.getName());
    
    private File file;

    private int noe;
    
    private List<ItemDataTO> importedItemData;

    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public ImportItemDataWizard(BODTO unitLoadTo) throws InstantiationException {
        super(createPanels());

        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        putProperty("WizardPanel_image", ImageUtilities.loadImage(""));
        putProperty("WizardDescriptor.setTitle", NbBundle.getMessage(BundleResolver.class, "importItemData"));
        setTitle(NbBundle.getMessage(BundleResolver.class, "importItemData"));
        
        setHelpCtx(new HelpCtx("de.linogistix.losdataimporter.itemdatawizard"));

        CursorControl.showWaitCursor();

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        FinishablePanel p2 = new ImportItemDataFilePanel();   
        panels.add(p2);
        
        FinishablePanel p3 = new ImportItemDataHintPanel();   
        panels.add(p3);
        
        
        return (Panel[]) panels.toArray(new Panel[0]);
    }

    public void stateChanged(ChangeEvent e) {
        putProperty("WizardPanel_errorMessage", null);
        updateState();
    }

    public JButton getFinishOption() {
        for (Object o : getClosingOptions()) {
            if (o instanceof JButton) {
                JButton b = (JButton) o;
                return b;
            }
        }

        return null;
    }
    //-----------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
        //
    }

    @SuppressWarnings("unchecked")
    public void setImportedItemData(List list) {
       this.importedItemData = list;
    }
    
    public List<ItemDataTO> getImportedItemData(){
        return this.importedItemData;
    }


    private String[] getContentData() {
       return new String[]{
        NbBundle.getMessage(BundleResolver.class, "ImportItemDataFilePanel.contentData"),
        NbBundle.getMessage(BundleResolver.class, "ImportItemDataHintPanel.contentData")
       };
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("de.linogistix.losdataimporter.itemdata.wizard");
        
    }

    public

    File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public int getNumberOfEntries() {
        return this.noe;
    }

    public void setNumberOfEntries(int noe) {
        this.noe = noe;
    }

    
}


