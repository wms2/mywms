/*
 * BOQueryByEditor.java
 *
 * Created on 15. Januar 2007, 01:33
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.query.gui.component;

import de.linogistix.common.bobrowser.query.gui.object.BOQueryByTemplateWrapper;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperator;
import de.linogistix.common.bobrowser.query.gui.object.QueryOperatorNop;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryByEditor implements ExPropertyEditor,
        ActionListener, InplaceEditor.Factory {
  
  private static final Logger log = Logger.getLogger(BOQueryByEditor.class.getName());
  
  private BOQueryByTemplateWrapper value;
  
  private PropertyEnv env;
  
  /** Utility field holding list of PropertyChangeListeners. */
  private transient List propertyChangeListenerList;
  
  private BOQueryByInplaceEditor inp;
  
  /**
   * Creates a new instance of BOQueryByEditor
   */
  public BOQueryByEditor() {
  }
  
  
  public String getAsText() {
    return null;
  }
  
  public void setAsText(String text){
    //
  }
  
  public boolean isPaintable() {
    return true;
  }
  
  public void paintValue(Graphics gfx, Rectangle box) {
    
    BOQueryByTemplateWrapper wrapper;
    QueryOperator op;
    Node.Property orig;
    Color c;
    int m;
    
    if ( value == null){
      op = QueryOperatorNop.OPERATOR_NOP;
      orig = null;
//      log.info("paint property null");
      wrapper = new BOQueryByTemplateWrapper(op,orig);
    } else{
      wrapper = (BOQueryByTemplateWrapper)value;
      op = wrapper.getOperator();
      orig = wrapper.getProperty();
      try{
//        log.info("paint property value " + orig.getValue());
      } catch (Throwable ex){
          log.warning(ex.getMessage());
      }
      wrapper = new BOQueryByTemplateWrapper(op,orig);
    }
    
    c = gfx.getColor();
    BOQueryByInplacePanel p = new BOQueryByInplacePanel(wrapper);
    m = BOQueryByInplacePanel.getTextMargin();
    p.setOpaque(true);
    p.setBounds(box.x, box.y, box.width+m, box.height);
    p.addNotify();
    p.invalidate();
    p.validate();

    Graphics g = gfx.create(box.x-m, box.y,p.operatorPropertyPanel.getWidth()-m, box.height);
//    log.info("g: " + (box.x-m) +"," +  (box.y) +"," +(p.operatorPropertyPanel.getWidth()-m) +","+ box.height);
    Graphics g2 = gfx.create(box.x+p.operatorPropertyPanel.getWidth()-m, box.y,box.width-p.operatorPropertyPanel.getWidth(), box.height);
//    log.info("g2: " + (box.x+p.operatorPropertyPanel.getWidth()-m) +"," +  (box.y) +"," +(box.width-p.operatorPropertyPanel.getWidth()) +","+ box.height);
    p.operatorPanel.setOpaque(false);
    p.operatorPropertyPanel.validate();
    p.operatorPropertyPanel.paint(g);

    if (p.propertyPanel != null){
      p.propertyPanel.setOpaque(true);
      p.propertyPanel.validate();
      p.propertyPanel.paint(g2);
//      log.info("drawn propertyPanel");
    } else if (p.valuePanel != null){
      p.valuePanel.setOpaque(true);
      p.valuePanel.validate();
      p.valuePanel.paint(g2);
//      log.info("drawn valuePanel");
    }
    //g2.drawString("Hallo Welt", box.x+p.operatorPropertyPanel.getWidth()-m, box.height);
    g.dispose();
    g2.dispose();
  }
  
  public String getJavaInitializationString() {
    return null;
  }
  
  public String[] getTags() {
    return null;
  }
  
  public boolean supportsCustomEditor() {
    return false;
  }
  
  public void attachEnv(PropertyEnv propertyEnv) {
    this.env = propertyEnv;
    env.registerInplaceEditorFactory(this);
    
  }
  
  public PropertyEnv getEnv() {
    return env;
  }
  
  public void actionPerformed(ActionEvent e) {
  }
  
  public Component getCustomEditor() {
    return null;
  }
  
  public InplaceEditor getInplaceEditor() {
    this.inp = new BOQueryByInplaceEditor();
    return this.inp;
  }
  
  /** Registers PropertyChangeListener to receive events.
   * @param listener The listener to register.
   *
   */
  public synchronized void addPropertyChangeListener(
          PropertyChangeListener listener) {
    if (propertyChangeListenerList == null ) {
      propertyChangeListenerList = new java.util.ArrayList();
    }
    propertyChangeListenerList.add(listener);
  }
  
  /** Removes PropertyChangeListener from the list of listeners.
   * @param listener The listener to remove.
   *
   */
  public synchronized void removePropertyChangeListener(
          PropertyChangeListener listener) {
    if (propertyChangeListenerList != null ) {
      propertyChangeListenerList.remove(listener);
    }
  }
  
  /** Notifies all registered listeners about the event.
   *
   * @param event The event to be fired
   *
   */
  void fireValueChange(BOQueryByTemplateWrapper oldVal, BOQueryByTemplateWrapper newVal) {
    List list;
    synchronized (this) {
      if (propertyChangeListenerList == null) return;
      list = (List)((ArrayList)propertyChangeListenerList).clone();
    }
    PropertyChangeEvent event = new PropertyChangeEvent(this, "valueChange",oldVal, newVal);
    for (int i = 0; i < list.size(); i++) {
      ((PropertyChangeListener)list.get(i)).propertyChange(event);
    }
  }
  
  public void setValue(Object v) {
    
    if (v == null){
      return;
    } else if ( ! (v instanceof BOQueryByTemplateWrapper)){
      return;
    } else if (v == this.value){   
      fireValueChange((BOQueryByTemplateWrapper)v, value);
//      inp.updateInner();
      return;
    }
    log.info("!!! is: " + v + " was: " + value);
    this.value = (BOQueryByTemplateWrapper)v;
    fireValueChange((BOQueryByTemplateWrapper)v, value);
    
  }
  
  public Object getValue() {
    log.info("!!!" + value);
    return this.value;
  }
  
}
