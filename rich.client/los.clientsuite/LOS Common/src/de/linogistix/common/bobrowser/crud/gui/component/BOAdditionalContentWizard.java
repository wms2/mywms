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

package de.linogistix.common.bobrowser.crud.gui.component;

import de.linogistix.common.res.CommonBundleResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOAdditionalContentWizard extends WizardDescriptor {
  
  private static final Logger log = Logger.getLogger(BOAdditionalContentWizard.class.getName());
 
  private String comment;
  
  /**
   * Creates a new instance of OrderByWizard
   */
  public BOAdditionalContentWizard() throws InstantiationException {
    super(createPanels());
        
    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));
    
    setTitle(NbBundle.getMessage(CommonBundleResolver.class,"AdditionalContentWizard.title"));
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(BundleResolver.class,"BOCreateWizard.HelpCtx"));
    
  }
  
  //-------------------------------------------------------------------------------

  public final static Panel[] createPanels() throws InstantiationException{
    List<Panel> panels = new ArrayList();
    
    panels.add(new BOAdditionalContentPanel1());
    return (Panel[])panels.toArray(new Panel[0]);
  }

  
    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    void stateChanged() {
        updateState();
    }

}


