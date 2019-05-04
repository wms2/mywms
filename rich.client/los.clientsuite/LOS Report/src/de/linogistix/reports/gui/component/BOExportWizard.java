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

package de.linogistix.reports.gui.component;

import de.linogistix.common.res.CommonBundleResolver; 
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOExportWizard extends WizardDescriptor implements ActionListener, ChangeListener{
  
  private static final Logger log = Logger.getLogger(BOExportWizard.class.getName());
  
  private SelectionMode selectionMode = SelectionMode.ALL;
  
  private String fileName;
  
  private boolean open;
    String fileEnding;
    String description;
    String defaultFileName;
    static BOExportWizardFilePanel p2;
  /**
   * Creates a new instance of OrderByWizard
   */
  @SuppressWarnings("unchecked")
  public BOExportWizard(BusinessObjectQueryRemote query, String fileEnding, String description, String defaultFileName) throws InstantiationException {
    super(createPanels());
    p2.setParams(fileEnding, description, defaultFileName);
    this.fileEnding = fileEnding;
    this.description = description;
    this.defaultFileName = defaultFileName;

    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));
      putProperty("WizardDescriptor.setTitleFormat", 
             "{0} " + NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "wizard") + "  ({1})");
      putProperty("WizardDescriptor.setTitle",NbBundle.getMessage(CommonBundleResolver.class,"BOExportWizard"));
    setTitle(NbBundle.getMessage(CommonBundleResolver.class,"BOExportWizard"));
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(BundleResolver.class,"BOCreateWizard.HelpCtx"));
     
    setButtonListener(this);
    
  }
  
  //-------------------------------------------------------------------------------

    public final static Panel[] createPanels() throws InstantiationException{
    List<Panel> panels = new ArrayList<Panel>();
    
    ValidatingPanel p = new BOExportWizardSelectionPanel(); 
    panels.add(p);
    p2 = new BOExportWizardFilePanel();
    panels.add(p2);
    return (Panel[])panels.toArray(new Panel[0]);
  }

   

    public void stateChanged(ChangeEvent e) {
        putProperty("WizardPanel_errorMessage", null);
        updateState();
    }
    
    public JButton getFinishOption(){
        for (Object o : getClosingOptions()){
            if (o instanceof JButton){
                JButton b  = (JButton) o;
                return b;
            }
        }
        
        return null;
    }
    //-----------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
        //
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public enum SelectionMode{
        ALL,
        SELECTED
    }
}


