/*
 * OrderByWizard.java
 *
 * Editd on 27. Juli 2006, 00:27
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.crud.gui.component;

import de.linogistix.common.bobrowser.crud.gui.object.BOEditNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.TemplateQuery;
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
public class BOEditWizard extends WizardDescriptor {
  
  private static final Logger log = Logger.getLogger(BOEditWizard.class.getName());
   
  private TemplateQuery templateQuery;
  
  private BOEditNode node;
  
  /**
   * Edits a new instance of OrderByWizard
   */
  public BOEditWizard(BOEditNode node) throws InstantiationException {
    super(createPanels());
        
    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));
    
    setTitle(NbBundle.getMessage(CommonBundleResolver.class,"BOEditWizard.Title"));
    
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(BundleResolver.class,"BOEditWizard.HelpCtx"));
    CursorControl.showWaitCursor();
    try{
      this.node = node;
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }finally{
      CursorControl.showNormalCursor();
    }
    
  }
  
  //-------------------------------------------------------------------------------

  public final static Panel[] createPanels() throws InstantiationException{
    List<Panel> panels = new ArrayList();
    
    panels.add(new BOEditPanel());
    return (Panel[])panels.toArray(new Panel[0]);
  }

  public BOEditNode getNode() {
    return node;
  }

}


