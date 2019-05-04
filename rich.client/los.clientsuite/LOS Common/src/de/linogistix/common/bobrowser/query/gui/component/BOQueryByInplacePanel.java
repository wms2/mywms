/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.gui.object.BOQueryByTemplateWrapper;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorDate;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorBool;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorEnum;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorBO;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorCollection;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorString;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperator;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorNop;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorNumeric;
import de.linogistix.common.bobrowser.bo.editor.EnumPropertyEditorI18N;
import de.linogistix.common.bobrowser.util.TypeResolver;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;

/**
 * A Panel that is used in {@link QueryByTemplateWizard}. It has one PropertyPanel
 * (ComboBox style)  for selecting a query operator and the original PropertyEditor
 * for selecting a value.
 *
 * @author  trautm
 */
final class BOQueryByInplacePanel extends javax.swing.JPanel implements FocusListener, KeyListener, PropertyChangeListener {

  private static final Logger log = Logger.getLogger(BOQueryByInplacePanel.class.getName());
  /** Left hand margin for properties in the right column of the sheet */
  private static int textMargin = -1;
  protected BOQueryByTemplateWrapper boProperty;
  WrapperEditorPanel propertyPanel;
  WrapperEditorPanel operatorPropertyPanel;

  /** Creates new form BOQueryByInplacePanel */
  public BOQueryByInplacePanel(BOQueryByTemplateWrapper boProperty) {
    this.boProperty = boProperty;
    initComponents();
    addFocusListener(this);
  }

  protected void initEditorPanel() {
    if (this.propertyPanel == null) {
      
        
      if (TypeResolver.isBooleanType(this.boProperty.getProperty().getValueType())) {
          //value is implicitly set with operator (true/false)
          this.propertyPanel = new WrapperEditorPanel(this);
      } else if (TypeResolver.isBusinessObjectType(this.boProperty.getProperty().getValueType())) {
        this.propertyPanel = new WrapperEditorPanel(this.boProperty.getProperty(), this);
      } else{
        this.propertyPanel = new WrapperEditorPanel(this.boProperty.getProperty(), this);
      }

      valuePanel.setOpaque(false);
      valuePanel.add(this.propertyPanel);
    }
  }

  protected void initComboPanel() {
    if (this.operatorPropertyPanel == null) {
      OperatorSupport s = new OperatorSupport(operatorClass());
      try {
        s.setValue(boProperty.getOperator());
      } catch (Exception ex) {
        log.log(Level.SEVERE, ex.getMessage(), ex);
      }
      operatorPropertyPanel = new WrapperEditorPanel(s, this);
//      operatorPropertyPanel.addPropertyChangeListener(this);
      operatorPanel.add(operatorPropertyPanel);
    }
  }

  private void selectDefaultOperator() {
    QueryOperator defaultOp;
    if (operatorPropertyPanel != null && boProperty.getOperator().equals(QueryOperatorNop.OPERATOR_NOP)) {
      OperatorSupport s = (OperatorSupport) (operatorPropertyPanel.getProperty());
      if (s == null || s.op == null) {
        return;
      } else {
        defaultOp = s.op.getDefault();
      }

      try {
        s.setValue(defaultOp);
      } catch (Exception ex) {
        log.log(Level.SEVERE, ex.getMessage(), ex);
      }
      this.operatorPropertyPanel.setProperty(s);
      operatorPropertyPanel.setChangeImmediate(false);
      operatorPropertyPanel.updateValue();
      operatorPropertyPanel.setChangeImmediate(true);
    }
  }

  public void addNotify() {
    if (boProperty != null) {
      initComboPanel();
      initEditorPanel();
    }
    super.addNotify();
  }

  protected Class operatorClass() {
    if (boProperty.isNumericType()) {
      return QueryOperatorNumeric.class;
    } else if (boProperty.isStringType()) {
      return QueryOperatorString.class;
    } else if (boProperty.isDateType()) {
      return QueryOperatorDate.class;
    } else if (boProperty.isBoolType()) {
      return QueryOperatorBool.class;
    } else if (boProperty.isBusinessObjectType()) {
      return QueryOperatorBO.class;
    } else if (boProperty.isCollectionType()){
        return QueryOperatorCollection.class;
    } else if (boProperty.isEnumType()){
        return QueryOperatorEnum.class;
    } else {
      return QueryOperatorNop.class;
    }
  }

  public BOQueryByTemplateWrapper getValue() {
    return boProperty;
  }

  /** Fetch the margin*/
  static int getTextMargin() {
    textMargin = 1;
    return textMargin;
  }

  public Insets getInsets(Insets insets) {
    return new Insets(0, 0, 0, 0);
  }

  public void setBackground(Color bg) {
    super.setBackground(bg);
    if (this.valuePanel != null) {
      this.valuePanel.setBackground(bg);
    }
    if (this.operatorPanel != null) {
      this.operatorPanel.setBackground(bg);
    }
    if (this.propertyPanel != null) {
      this.propertyPanel.setBackground(bg);
    }
    if (this.operatorPropertyPanel != null) {
      this.operatorPropertyPanel.setBackground(bg);
    }
  }

  public void setForeground(Color bg) {
    super.setBackground(bg);
    if (this.valuePanel != null) {
      this.valuePanel.setForeground(bg);
    }
    if (this.operatorPanel != null) {
      this.operatorPanel.setForeground(bg);
    }
    if (this.propertyPanel != null) {
      this.propertyPanel.setForeground(bg);
    }
    if (this.operatorPropertyPanel != null) {
      this.operatorPropertyPanel.setForeground(bg);
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    operatorPanel = new javax.swing.JPanel();
    valuePanel = new javax.swing.JPanel();

    setBackground(new java.awt.Color(255, 255, 255));
    setAlignmentX(0.0F);
    setAlignmentY(0.0F);
    setLayout(new java.awt.BorderLayout());

    operatorPanel.setAlignmentX(0.0F);
    operatorPanel.setAlignmentY(0.0F);
    operatorPanel.setMaximumSize(new java.awt.Dimension(76, 32767));
    operatorPanel.setMinimumSize(new java.awt.Dimension(76, 10));
    operatorPanel.setName("w"); // NOI18N
    operatorPanel.setPreferredSize(new java.awt.Dimension(76, 100));
    operatorPanel.setLayout(new java.awt.BorderLayout());
    add(operatorPanel, java.awt.BorderLayout.WEST);

    valuePanel.setAlignmentX(0.0F);
    valuePanel.setAlignmentY(0.0F);
    valuePanel.setLayout(new java.awt.BorderLayout());
    add(valuePanel, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  protected javax.swing.JPanel operatorPanel;
  protected javax.swing.JPanel valuePanel;
  // End of variables declaration//GEN-END:variables
  public void keyTyped(KeyEvent e) {
    log.info(e.toString());
  }

  /**
   * Consumes VK_TAB to request Focus for PropertyPanel in editorPanel.
   * @see  #focusLost
   */
  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

  public void focusGained(FocusEvent e) {
    if (this == e.getComponent()) {
      operatorPropertyPanel.requestFocus();
    }
  }

  /**
   */
  public void focusLost(FocusEvent e) {
  }

  void updateInner() {
    log.info("");
    if (propertyPanel != null) {
      propertyPanel.setChangeImmediate(false);
      propertyPanel.updateValue();
      propertyPanel.setChangeImmediate(true);
    }
    if (operatorPropertyPanel != null) {
      try {
        OperatorSupport s = (OperatorSupport) operatorPropertyPanel.getProperty();
        boProperty.setOperator((QueryOperator) s.getValue());
      } catch (Throwable t) {
        log.severe(t.getMessage());
        log.log(Level.INFO, t.getMessage(), t);
      }
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    log.info(evt.toString() + "; propertyName " + evt.getPropertyName());
    if ("valueChange".equals(evt.getPropertyName())) {
      if (!evt.getNewValue().equals("")) {
        if (evt.getNewValue() == evt.getOldValue()) {
          log.info("Going to  update inner because of valueChange");
          updateInner();
        }
      } else if ("ancestor".equals(evt.getPropertyName())) {
        if (this.propertyPanel == evt.getSource()) {
        }
        log.info("Going to  update inner because of ancestor");
        updateInner();
      }
    }
  }

  static final class OperatorSupport extends PropertySupport.ReadWrite {

    QueryOperator op;
//    List<PropertyChangeListener> listeners = new ArrayList();

    OperatorSupport(Class queryOperatorEnum) {
      super("operator", queryOperatorEnum, "operator", "operator");
    }

    public Object getValue() throws IllegalAccessException, InvocationTargetException {
      return op;
    }

    public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      boolean fireyes = false;
      if (object == null) {
        fireyes = true;
        this.op = QueryOperatorNop.OPERATOR_NOP;
      } else if (object instanceof QueryOperatorNop) {
        fireyes = object.equals(this.op);
        this.op = (QueryOperatorNop) object;
      } else if (object instanceof QueryOperator) {
        this.op = (QueryOperator) object;
      } else {
        throw new IllegalArgumentException();
      }
      if (fireyes) {
        firePropertyChangeEvent();
      }
    }

    @Override
    public PropertyEditor getPropertyEditor() {
      return new EnumPropertyEditorI18N(getValueType());
    }

    public QueryOperator getOperatorEntry() {
      return op;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
//      listeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
//      listeners.remove(l);
    }

    void firePropertyChangeEvent() {

//      PropertyChangeEvent e = new PropertyChangeEvent(this, "operatorChanged", -1, this.op.toString());
//      log.info("fire " + e.toString());
//      for (PropertyChangeListener l : listeners) {
//        l.propertyChange(e);
//      }
    }
  }

  static final class WrapperEditorPanel extends PropertyPanel {

    BOQueryByInplacePanel parent;

    WrapperEditorPanel(BOQueryByInplacePanel parent) {
        super();
        this.parent = parent;
        setChangeImmediate(true);
        putClientProperty("flat", Boolean.TRUE);
    }
    
    WrapperEditorPanel(Node.Property p, BOQueryByInplacePanel parent) {
      super(p, 0);
      this.parent = parent;
      setChangeImmediate(true);
      putClientProperty("flat", Boolean.TRUE);
    }

    public void addNotify() {
      super.addNotify();
    }
    
    public void removeNotify() {
      super.removeNotify();
    }

    protected void processFocusEvent(FocusEvent focusEvent) {
      super.processFocusEvent(focusEvent);
    }

    public void requestFocus() {
      super.requestFocus();
    }

    public boolean requestFocus(boolean temporary) {
      boolean ret = super.requestFocus(temporary);
      return ret;
    }

    public void updateValue() {
      try {
        super.updateValue();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    @Override
    public Dimension getPreferredSize() {
      try{
        return super.getPreferredSize();
      } catch (Exception ex){
        log.severe(ex.getMessage());
        return null;
      }
    }
    
    
  }


  static class FocusNextInPanelPoliy extends FocusTraversalPolicy {

    BOQueryByInplacePanel panel;

    FocusNextInPanelPoliy(BOQueryByInplacePanel panel) {
      this.panel = panel;
    }

    public Component getComponentAfter(Container aContainer, Component aComponent) {
      if (panel.operatorPropertyPanel.isAncestorOf(aComponent)) {
        return panel.propertyPanel;
      } else if (panel.propertyPanel.isAncestorOf(aComponent)) {
        return null;
      } else {
        return panel;
      }
    }

    public Component getComponentBefore(Container aContainer, Component aComponent) {
      return panel;
    }

    public Component getFirstComponent(Container aContainer) {
      return panel.operatorPropertyPanel;
    }

    public Component getLastComponent(Container aContainer) {
      return panel.operatorPanel;
    }

    public Component getDefaultComponent(Container aContainer) {
      return panel.operatorPropertyPanel;
    }

    public Component getInitialComponent(Window window) {
      return panel.operatorPropertyPanel;
    }
  }
}
