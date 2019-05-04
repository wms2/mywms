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

import de.linogistix.common.bobrowser.crud.gui.object.BOCreateNode;
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
public class BOCreateWizard extends WizardDescriptor {
  
  private static final Logger log = Logger.getLogger(BOCreateWizard.class.getName());
   
  private TemplateQuery templateQuery;
  
  private BOCreateNode node;
  
  /**
   * Creates a new instance of OrderByWizard
   */
  public BOCreateWizard(BOCreateNode node) throws InstantiationException {
    super(createPanels());
        
    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));
    
    setTitle(NbBundle.getMessage(CommonBundleResolver.class,"BOCreateWizard.Title"));
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(BundleResolver.class,"BOCreateWizard.HelpCtx"));
    
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
    
    panels.add(new BOCreatePanel1());
    return (Panel[])panels.toArray(new Panel[0]);
  }

  public BOCreateNode getNode() {
    return node;
  }

}


