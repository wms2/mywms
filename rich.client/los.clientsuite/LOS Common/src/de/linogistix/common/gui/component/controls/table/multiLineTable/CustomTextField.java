/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.multiLineTable;

import java.beans.*;
import java.io.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.JTextComponent.*;

public class CustomTextField
    extends JTextComponent
    implements SwingConstants {

  /**
   * Constructs a new TextField.  A default model is created, the initial
   * string is null, and the number of columns is set to 0.
   */
  public CustomTextField() {
    this(null, null, 0);
  }

  public CustomTextField(boolean b) {
    this(null, null, 0);
  }

  /**
   * Constructs a new TextField initialized with the specified text.
   * A default model is created and the number of columns is 0.
   *
   * @param text the text to be displayed, or null
   */
  public CustomTextField(String text) {
    this(null, text, 0);
  }

  /**
   * Constructs a new empty TextField with the specified number of columns.
   * A default model is created and the initial string is set to null.
   *
   * @param columns  the number of columns to use to calculate
   *   the preferred width.  If columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation.
   */
  public CustomTextField(int columns) {
    this(null, null, columns);
  }

  /**
   * Constructs a new TextField initialized with the specified text
   * and columns.  A default model is created.
   *
   * @param text the text to be displayed, or null
   * @param columns  the number of columns to use to calculate
   *   the preferred width.  If columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation.
   */
  public CustomTextField(String text, int columns) {
    this(null, text, columns);
  }

  /**
   * Constructs a new CustomTextField that uses the given text storage
   * model and the given number of columns.  This is the constructor
   * through which the other constructors feed.  If the document is null,
   * a default model is created.
   *
   * @param doc  the text storage to use.  If this is null, a default
   *   will be provided by calling the createDefaultModel method.
   * @param text  the initial string to display, or null
   * @param columns  the number of columns to use to calculate
   *   the preferred width >= 0.  If columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation.
   * @exception IllegalArgumentException if columns < 0
   */
  public CustomTextField(Document doc, String text, int columns) {
    if (columns < 0) {
      throw new IllegalArgumentException("columns less than zero.");
    }
    visibility = new DefaultBoundedRangeModel();
    visibility.addChangeListener(new ScrollRepainter());
    this.columns = columns;
    if (doc == null) {
      doc = createDefaultModel();
    }
    setDocument(doc);
    if (text != null) {
      setText(text);
    }
  }

  /**
   * Gets the class ID for a UI.
   *
   * @return the ID ("TextFieldUI")
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  public String getUIClassID() {

    return uiClassID;
  }

  /**
   * Calls to revalidate that come from within the textfield itself will
   * be handled by validating the textfield, unless the receiver
   * is contained within a JViewport, in which case this returns false.
   *
   * @see JComponent#revalidate
   * @see JComponent#isValidateRoot
   */
  public boolean isValidateRoot() {
    Component parent = getParent();
    if (parent instanceof JViewport) {
      return false;
    }
    return true;
  }

  /**
   * Returns the horizontal alignment of the text.
   * Valid keys: CustomTextField.LEFT, CustomTextField.CENTER, CustomTextField.RIGHT,
   * CustomTextField.LEADING and CustomTextField.TRAILING
   *
   * @return the alignment
   */
  public int getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Sets the horizontal alignment of the text.
   * Valid keys: CustomTextField.LEFT, CustomTextField.CENTER, CustomTextField.RIGHT,
   * CustomTextField.LEADING (the default) and CustomTextField.TRAILING.
   * invalidate() and repaint() are called when the alignment is set,
   * and a PropertyChange event ("horizontalAlignment") is fired.
   *
   * @param alignment the alignment
   * @exception IllegalArgumentException if the alignment
   *  specified is not a valid key.
   * @beaninfo
   *   preferred: true
   *       bound: true
   * description: Set the field alignment to LEFT, CENTER, RIGHT,
   *              LEADING (the default) or TRAILING
   *        enum: LEFT CustomTextField.LEFT CENTER CustomTextField.CENTER RIGHT CustomTextField.RIGHT
   *              LEADING CustomTextField.LEADING TRAILING CustomTextField.TRAILING
   */
  public void setHorizontalAlignment(int alignment) {
    if (alignment == horizontalAlignment) {
      return;
    }
    int oldValue = horizontalAlignment;
    if ( (alignment == LEFT) || (alignment == CENTER) ||
        (alignment == RIGHT) || (alignment == LEADING) ||
        (alignment == TRAILING)) {
      horizontalAlignment = alignment;
    }
    else {
      throw new IllegalArgumentException("horizontalAlignment");
    }
    firePropertyChange("horizontalAlignment", oldValue, horizontalAlignment);
    invalidate();
    repaint();
  }

  /**
   * Creates the default implementation of the model
   * to be used at construction if one isn't explicitly
   * given.  An instance of PlainDocument is returned.
   *
   * @return the default model implementation
   */
  protected Document createDefaultModel() {
    return new PlainDocument();
  }

  /**
   * Returns the number of columns in this TextField.
   *
   * @return the number of columns >= 0
   */
  public int getColumns() {
    return columns;
  }

  /**
   * Sets the number of columns in this TextField, and then invalidate
   * the layout.
   *
   * @param columns the number of columns >= 0
   * @exception IllegalArgumentException if columns is less than 0
   * @beaninfo
   * description: the number of columns preferred for display
   */
  public void setColumns(int columns) {
    int oldVal = this.columns;
    if (columns < 0) {
      throw new IllegalArgumentException("columns less than zero.");
    }
    if (columns != oldVal) {
      this.columns = columns;
      invalidate();
    }
  }

  /**
   * Gets the column width.
   * The meaning of what a column is can be considered a fairly weak
   * notion for some fonts.  This method is used to define the width
   * of a column.  By default this is defined to be the width of the
   * character <em>m</em> for the font used.  This method can be
   * redefined to be some alternative amount
   *
   * @return the column width >= 1
   */
  protected int getColumnWidth() {
    if (columnWidth == 0) {
      FontMetrics metrics = getFontMetrics(getFont());
      columnWidth = metrics.charWidth('m');
    }
    return columnWidth;
  }

  /**
   * Returns the preferred size Dimensions needed for this
   * TextField.  If a non-zero number of columns has been
   * set, the width is set to the columns multiplied by
   * the column width.
   *
   * @return the dimensions
   */
  public Dimension getPreferredSize() {
    synchronized (getTreeLock()) {
      Dimension size = super.getPreferredSize();
      if (columns != 0) {
        size.width = columns * getColumnWidth();
      }
      return size;
    }
  }

  /**
   * Sets the current font.  This removes cached row height and column
   * width so the new font will be reflected.  revalidate() is called
   * after setting the font.
   *
   * @param f the new font
   */
  public void setFont(Font f) {
    super.setFont(f);
    columnWidth = 0;
  }

  /**
   * Adds the specified action listener to receive
   * action events from this textfield.
   *
   * @param l the action listener
   */
  public synchronized void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
  }

  /**
   * Removes the specified action listener so that it no longer
   * receives action events from this textfield.
   *
   * @param l the action listener
   */
  /*    public synchronized void removeActionListener(ActionListener l) {
          if ((l != null) && (getAction() == l)) {
              setAction(null);
          } else {
              listenerList.remove(ActionListener.class, l);
          }
      }*/

  /**
   * Notifies all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.  The listener list is processed in last to
   * first order.
   * @see EventListenerList
   */
  protected void fireActionPerformed() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                    (command != null) ? command : getText());
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        ( (ActionListener) listeners[i + 1]).actionPerformed(e);
      }
    }
  }

  /**
   * Sets the command string used for action events.
   *
   * @param command the command string
   */
  public void setActionCommand(String command) {
    this.command = command;
  }

  private Action action;
  private PropertyChangeListener actionPropertyChangeListener;

  /**
   * Sets the Action for the ActionEvent source. The new Action replaces
   * any previously set Action but does not affect ActionListeners
   * independantly added with addActionListener().  If the Action is already
   * a registered ActionListener for the ActionEvent source, it is not re-registered.
   *
   * A side-effect of setting the Action is that the ActionEvent source's properties
   * are immediately set from the values in the Action (performed by the method
   * configurePropertiesFromAction()) and subsequently updated as the Action's
   * properties change (via a PropertyChangeListener created by the method
   * createActionPropertyChangeListener().
   *
   * @param a the Action for the CustomTextField, or null.
   * @since 1.3
   * @see Action
   * @see #getAction
   * @see #configurePropertiesFromAction
   * @see #createActionPropertyChangeListener
   * @beaninfo
   *        bound: true
   *    attribute: visualUpdate true
   *  description: the Action instance connected with this ActionEvent source
   */
  /*    public void setAction(Action a) {
          Action oldValue = getAction();
          if (action==null || !action.equals(a)) {
              action = a;
              if (oldValue!=null) {
                  removeActionListener(oldValue);
                  oldValue.removePropertyChangeListener(actionPropertyChangeListener);
                  actionPropertyChangeListener = null;
              }
              configurePropertiesFromAction(action);
              if (action!=null) {
                  // Don't add if it is already a listener
                  if (!isListener(ActionListener.class, action)) {
                      addActionListener(action);
                  }
                  // Reverse linkage:
                  actionPropertyChangeListener = createActionPropertyChangeListener(action);
                  action.addPropertyChangeListener(actionPropertyChangeListener);
              }
              firePropertyChange("action", oldValue, action);
              revalidate();
              repaint();
          }
      }*/

  private boolean isListener(Class c, ActionListener a) {
    boolean isListener = false;
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == c && listeners[i + 1] == a) {
        isListener = true;
      }
    }
    return isListener;
  }

  /**
   * Returns the currently set Action for this ActionEvent source,
   * or null if no Action is set.
   *
   * @return the Action for this ActionEvent source, or null.
   * @since 1.3
   * @see Action
   * @see #setAction
   */
  public Action getAction() {
    return action;
  }

  /**
   * Factory method which sets the ActionEvent source's properties
   * according to values from the Action instance.  The properties
   * which are set may differ for subclasses.
   * By default, the properties which get set are
   * Enabled and ToolTipText.
   *
   * @param a the Action from which to get the properties, or null
   * @since 1.3
   * @see Action
   * @see #setAction
   */
  protected void configurePropertiesFromAction(Action a) {
    setEnabled( (a != null ? a.isEnabled() : true));
    setToolTipText( (a != null ? (String) a.getValue(Action.SHORT_DESCRIPTION) : null));
  }

  /**
   * Factory method which creates the PropertyChangeListener
   * used to update the ActionEvent source as properties change on
   * its Action instance.  Subclasses may override this in order
   * to provide their own PropertyChangeListener if the set of
   * properties which should be kept up to date differs from the
   * default properties (Text, Enabled, ToolTipText).
   *
   * Note that PropertyChangeListeners should avoid holding
   * strong references to the ActionEvent source, as this may hinder
   * garbage collection of the ActionEvent source and all components
   * in its containment hierarchy.
   *
   * @since 1.3
   * @see Action
   * @see #setAction
   */
  /*    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
          return new AbstractActionPropertyChangeListener(this, a) {
              public void propertyChange(PropertyChangeEvent e) {
                  String propertyName = e.getPropertyName();
                  CustomTextField textField = (CustomTextField)getTarget();
                  if (textField == null) {   //WeakRef GC'ed in 1.2
                      Action action = (Action)e.getSource();
                      action.removePropertyChangeListener(this);
                  } else {
                      if (e.getPropertyName().equals(Action.SHORT_DESCRIPTION)) {
                          String text = (String) e.getNewValue();
                          textField.setToolTipText(text);
                      } else if (propertyName.equals("enabled")) {
                          Boolean enabledState = (Boolean) e.getNewValue();
                          textField.setEnabled(enabledState.booleanValue());
                          textField.repaint();
                      }
                  }
              }
          };
      }*/

  /**
   * Fetches the command list for the editor.  This is
   * the list of commands supported by the plugged-in UI
   * augmented by the collection of commands that the
   * editor itself supports.  These are useful for binding
   * to events, such as in a keymap.
   *
   * @return the command list
   */
  public Action[] getActions() {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }

  /**
   * Processes action events occurring on this textfield by
   * dispatching them to any registered ActionListener objects.
   * This is normally called by the controller registered with
   * textfield.
   */
  public void postActionEvent() {
    fireActionPerformed();
  }

  // --- Scrolling support -----------------------------------

  /**
   * Gets the visibility of the text field.  This can
   * be adjusted to change the location of the visible
   * area if the size of the field is greater than
   * the area that was allocated to the field.
   *
   * The fields look-and-feel implementation manages
   * the values of the minimum, maximum, and extent
   * properties on the BoundedRangeModel.
   *
   * @return the visibility
   * @see BoundedRangeModel
   */
  public BoundedRangeModel getHorizontalVisibility() {
    return visibility;
  }

  /**
   * Gets the scroll offset.
   *
   * @return the offset >= 0
   */
  public int getScrollOffset() {
    return visibility.getValue();
  }

  /**
   * Sets the scroll offset.
   *
   * @param scrollOffset the offset >= 0
   */
  public void setScrollOffset(int scrollOffset) {
    visibility.setValue(scrollOffset);
  }

  /**
   * Scrolls the field left or right.
   *
   * @param r the region to scroll
   */
  public void scrollRectToVisible(Rectangle r) {
    // convert to coordinate system of the bounded range
    int x = r.x + visibility.getValue();
    if (x < visibility.getValue()) {
      // Scroll to the left
      visibility.setValue(x - 2);
    }
    else if (x > visibility.getValue() + visibility.getExtent()) {
      // Scroll to the right
      visibility.setValue(x - visibility.getExtent() + 2);
    }
  }

  /**
   * Returns true if the receiver has an ActionListener installed.
   */
  boolean hasActionListener() {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        return true;
      }
    }
    return false;
  }

  // --- variables -------------------------------------------

  /**
   * Name of the action to send notification that the
   * contents of the field have been accepted.  Typically
   * this is bound to a carriage-return.
   */
  public static final String notifyAction = "notify-field-accept";

  private BoundedRangeModel visibility;
  private int horizontalAlignment = LEADING;
  private int columns;
  private int columnWidth;
  private String command;

  private static final Action[] defaultActions = {
      new NotifyAction()
  };

  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "TextFieldUI";

  // --- Action implementations -----------------------------------

  static class NotifyAction
      extends TextAction {

    NotifyAction() {
      super(notifyAction);
    }

    public void actionPerformed(ActionEvent e) {
      JTextComponent target = getFocusedComponent();
      if (target instanceof CustomTextField) {
        CustomTextField field = (CustomTextField) target;
        field.postActionEvent();
      }
    }

    public boolean isEnabled() {
      JTextComponent target = getFocusedComponent();
      if (target instanceof CustomTextField) {
        return ( (CustomTextField) target).hasActionListener();
      }
      return false;
    }
  }

  class ScrollRepainter
      implements ChangeListener {

    public void stateChanged(ChangeEvent e) {
      repaint();
    }

  }

  /**
   * See readObject() and writeObject() in JComponent for more
   * information about serialization in Swing.
   */
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    if ( (ui != null) && (getUIClassID().equals(uiClassID))) {
      ui.installUI(this);
    }
  }

  /**
   * Returns a string representation of this CustomTextField. This method
   * is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not
   * be <code>null</code>.
   *
   * @return  a string representation of this CustomTextField.
   */
  protected String paramString() {
    String horizontalAlignmentString;
    if (horizontalAlignment == LEFT) {
      horizontalAlignmentString = "LEFT";
    }
    else if (horizontalAlignment == CENTER) {
      horizontalAlignmentString = "CENTER";
    }
    else if (horizontalAlignment == RIGHT) {
      horizontalAlignmentString = "RIGHT";
    }
    else if (horizontalAlignment == LEADING) {
      horizontalAlignmentString = "LEADING";
    }
    else if (horizontalAlignment == TRAILING) {
      horizontalAlignmentString = "TRAILING";
    }
    else {
      horizontalAlignmentString = "";
    }
    String commandString = (command != null ?
                            command : "");

    return super.paramString() +
        ",columns=" + columns +
        ",columnWidth=" + columnWidth +
        ",command=" + commandString +
        ",horizontalAlignment=" + horizontalAlignmentString;
  }

/////////////////
// Accessibility support
////////////////

  /**
   * Gets the AccessibleContext associated with this CustomTextField.
   * For CustomTextFields, the AccessibleContext takes the form of an
   * AccessibleCustomTextField.
   * A new AccessibleCustomTextField instance is created if necessary.
   *
   * @return an AccessibleCustomTextField that serves as the
   *         AccessibleContext of this CustomTextField
   */
  public AccessibleContext getAccessibleContext() {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleCustomTextField();
    }
    return accessibleContext;
  }

  /**
   * This class implements accessibility support for the
   * <code>CustomTextField</code> class.  It provides an implementation of the
   * Java Accessibility API appropriate to text field user-interface
   * elements.
   * <p>
   * <strong>Warning:</strong>
   * Serialized objects of this class will not be compatible with
   * future Swing releases.  The current serialization support is appropriate
   * for short term storage or RMI between applications running the same
   * version of Swing.  A future release of Swing will provide support for
   * long term persistence.
   */
  protected class AccessibleCustomTextField
      extends AccessibleJTextComponent {

    /**
     * Gets the state set of this object.
     *
     * @return an instance of AccessibleStateSet describing the states
     * of the object
     * @see AccessibleState
     */
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet states = super.getAccessibleStateSet();
      states.add(AccessibleState.SINGLE_LINE);
      return states;
    }
  }
}