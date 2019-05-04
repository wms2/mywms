/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.preferences;

import de.linogistix.common.exception.InternalErrorException;
import de.linogistix.common.util.ExceptionAnnotator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * AppPreferencesController controls preference settings.
 * 
 * 
 * Preferences are stored
 * in and read from properties file in Settings folder under build\testuserdir\config\.
 * 
 * Fallback is loading properties from class path as resource.
 * 
 * Using this controller externally (i.e. it is not called from Option Dialog infrastructure)
 * needs a call to update() for initialisation.
 */
public final class AppPreferencesController extends OptionsPanelController implements PropertyChangeListener {

  private AppPreferencesPanel panel;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private boolean changed;
  private AppPreferences prefs;
  private AppPreferencesNode aNode;
  private String propertyFileBaseName;
  Lookup lookup;

  private String[] noStoreProps = new String[0];
  
  public AppPreferencesController(String propertyFileBaseName) {
    this.propertyFileBaseName = propertyFileBaseName;
  }
  
  /**
   * 
   * @param propertyFileBaseName
   * @param noStoreProps won't be stored in file (e.g. password information)
   */
  public AppPreferencesController(String propertyFileBaseName, String[] noStoreProps) {
    this.propertyFileBaseName = propertyFileBaseName;
    this.noStoreProps = noStoreProps;
  }

  public void update() {

    Properties p = new Properties();
    try {
      //Just Fallback for default settings
//      p = AppPreferences.loadFromClasspath(propertyFileBaseName + ".properties");
      if (prefs == null) {
        // Retreieve from file or new with default settings
        prefs = AppPreferences.getSettings(propertyFileBaseName, p);
        aNode = new AppPreferencesNode(prefs, propertyFileBaseName);
        aNode.addPropertyChangeListener(this);
        getPanel().update();

      } else {
        prefs.load(p);
      }
    } catch (Exception ex) {
//      ex.printStackTrace();
      ExceptionAnnotator.annotate(new InternalErrorException(ex));
    }
    //panel.load
    getPanel().update();
    changed = false;
  }
  
  public void applyChanges() {
    prefs.setNoStoreProps(this.noStoreProps);
// The setting must be read from config-file. The only thing one can do here,
// is to prevent the next start. So do not store changes.
//      prefs.store();


    //panel.store
    changed = false;
  }

  public void cancel() {
  // need not do anything special, if no changes have been persisted yet
  }

  public boolean isValid() {
    return prefs.valid();
  }

  public boolean isChanged() {
    return changed;
  }

  public HelpCtx getHelpCtx() {
    return null; // new HelpCtx("...ID") if you have a help set
  }

  public JComponent getComponent(Lookup masterLookup) {
    lookup = masterLookup;
    return getPanel();
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }

  private AppPreferencesPanel getPanel() {
    if (panel == null) {
      panel = new AppPreferencesPanel(this);
    }
    return panel;
  }

  void changed() {
    if (!changed) {
      changed = true;
      pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
    }
    pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    changed();
  }

  public AppPreferences getPrefs() {
    return prefs;
  }

  public AppPreferencesNode getANode() {
    return aNode;
  }

  public String getPropertyFileBaseName() {
    return propertyFileBaseName;
  }
}
