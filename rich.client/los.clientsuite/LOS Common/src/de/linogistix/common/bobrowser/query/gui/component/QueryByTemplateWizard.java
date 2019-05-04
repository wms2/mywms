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
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.gui.object.BOQueryByTemplateProperty;
import de.linogistix.common.bobrowser.query.*;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * A Wizard for querying existing BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class QueryByTemplateWizard extends WizardDescriptor {

  private static final Logger log = Logger.getLogger(QueryByTemplateWizard.class.getName());
  private BOQueryByTemplateNode node;

  /**
   * Creates a new instance of OrderByWizard
   */
  public QueryByTemplateWizard(BOQueryByTemplateNode node) throws InstantiationException {
    super(createPanels());

    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));

    setTitle(NbBundle.getMessage(CommonBundleResolver.class, "QueryByTemplateWizard.Title"));
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(BundleResolver.class,"QueryByTemplateWizard.HelpCtx"));

    CursorControl.showWaitCursor();
    try {
      this.node = node;
    } catch (Throwable t) {
      ExceptionAnnotator.annotate(t);
    } finally {
      CursorControl.showNormalCursor();
    }

  }

  //-------------------------------------------------------------------------------
  public final static Panel[] createPanels() throws InstantiationException {
    List<Panel> panels = new ArrayList();

    panels.add(new QueryByTemplatePanel1());
    return (Panel[]) panels.toArray(new Panel[0]);
  }

  public BOQueryByTemplateNode getNode() {
    return node;
  }

  public TemplateQuery createTemplateQuery() {
    TemplateQuery q;
    List<TemplateQueryWhereToken> tokens = new ArrayList();
    q = new TemplateQuery();
    q.setBoClass(getNode().getBean().getClass());
    for (Node.PropertySet s : node.getPropertySets()) {
      for (Node.Property prop : s.getProperties()) {
        TemplateQueryWhereToken t;
        try {
          if (prop != null) {
            t = new TemplateQueryWhereToken();
            BOQueryByTemplateProperty p = (BOQueryByTemplateProperty) prop;
            if (p == null || p.getOperator() == null) {
              continue;
            } else if (p.getOperator().isNOP()) {
              continue;
            } else if (TemplateQueryWhereToken.isUnaryOperator(p.getOperator().getOperator())) {
              t.setParameter(p.getPropertyWrapper().getProperty().getName());
              t.setOperator(p.getOperator().getOperator());
              t.setValue(null);
              tokens.add(t);
              continue;
            } 
            else if(p.getPropertyWrapper().getProperty().getValue() instanceof Date
                    && p.getPropertyWrapper().getOperator().getOperator().equals(TemplateQueryWhereToken.OPERATOR_AFTER))
            {               
                // user chose "after 28.01.2009"
                
                // DatePickerDialog selects 28.01.2009 00:00:00
                Date dateVal = (Date) p.getPropertyWrapper().getProperty().getValue();
                
                Calendar cal24 = Calendar.getInstance();
        	cal24.setTime(dateVal);
        	
                // user wants to see results starting from 29.01.2009 00:00:00
        	cal24.add(Calendar.HOUR_OF_DAY, 23);
                cal24.add(Calendar.MINUTE, 59);
                
                t.setOperator(p.getPropertyWrapper().getOperator().getOperator());
                t.setParameter(p.getPropertyWrapper().getProperty().getName());
                t.setValue(cal24.getTime());
                
                tokens.add(t);
                
                continue;
            }
            else{
                t.setOperator(p.getPropertyWrapper().getOperator().getOperator());
                t.setParameter(p.getPropertyWrapper().getProperty().getName());
                t.setValue(p.getPropertyWrapper().getProperty().getValue());
                tokens.add(t);
            }
          }
        } catch (Throwable ex) {
          //        log.log(Level.SEVERE,ex.getMessage(),ex);
          ExceptionAnnotator.annotate(ex);
        }
      }

    }

    q.setWhereTokens(tokens);
    return q;
  }
}


