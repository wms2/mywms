/*
 * ServiceParameterWizard.java
 *
 * Created on 27. Juli 2006, 00:27
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.services;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.ArrayIterator;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class ServiceParameterWizard extends WizardDescriptor {
  
  private static final Logger log = Logger.getLogger(ServiceParameterWizard.class.getName());
  

  private static Panel[] createPanels(Method service){
    List<Panel> ret = new ArrayList();
    
    service.getAnnotations();
    
    Class[] types = service.getParameterTypes();
    
    return null;
  }
  
  /**
   * Creates a new instance of ServiceParameterWizard
   */
  public ServiceParameterWizard(Method service) {
    super(createPanels(service));
    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
    
    CursorControl.showWaitCursor();
    try{
      
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }finally{
      CursorControl.showNormalCursor();
    }
    
  }
 

  
  
  //-------------------------------------------------------------------------------
  
  
  
  //---------------------------------------------------------------------------
  
  /** Special iterator that works on an array of <code>Panel</code>s.
   *
   * Source taken from the openide project.
   *
   *@see
   */
  public static class InstantiatingArrayIterator extends ArrayIterator
      implements InstantiatingIterator{
    
    
    private ServiceParameterWizard wizardInstance;
    
    
    public InstantiatingArrayIterator(Panel[] panels){
      super(panels);
    }
    
    
    public Set/*<DataObject>*/ instantiate() throws IOException {
      assert wizardInstance != null : "wizardInstance cannot be null when instantiate() called."; // NOI18N
     return null;
    }
    
    public void initialize(WizardDescriptor wizardDescriptor) {
      if (!(wizardDescriptor instanceof ServiceParameterWizard)) {
        throw new IllegalArgumentException("WizardDescriptor must be instance of BOCreateWizard, but is " + wizardDescriptor); // NOI18N
      }
      this.wizardInstance = (ServiceParameterWizard)wizardDescriptor;
    }
    
    public void uninitialize(WizardDescriptor wizardDescriptor) {
      //
    }
    
    
  }
  
  
}


