/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import de.linogistix.common.gui.component.controls.AutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.LosLabel;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class LiveHelp {

    private static LiveHelp instance = null;
    private SortedMap<Integer, Component[]> map;
    private boolean hasEmptyFields = false;

    /** Creates a new instance of LiveHelp */
    private LiveHelp() {
    // Exists only to defeat instantiation.
    }

    public synchronized static LiveHelp getInstance() {
        if (instance == null) {
            instance = new LiveHelp();
        }
        return instance;
    }

    public void addFocusListener(Container aTop, FocusListener listener) {
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i].isFocusable()) {
                    if (comp[i] instanceof AutoFilteringComboBox) {
                        //if already be set remove it first, to prevent double event calls
                        ((AutoFilteringComboBox) comp[i]).getEditor().getEditorComponent().removeFocusListener(listener);
                        ((AutoFilteringComboBox) comp[i]).getEditor().getEditorComponent().addFocusListener(listener);
                    } else {
                        //if already be set remove it first, to prevent double event calls
                        comp[i].removeFocusListener(listener);
                        comp[i].addFocusListener(listener);
                    }
                }
                if (comp[i] instanceof JPanel) {
                    addFocusListener((JPanel) comp[i], listener);
                }
            }
        }
    }
    
   private boolean findComponent(Component findComp, Component[] comp) {       
       for (Component pos: comp) {
           if (pos == findComp) {
               return true;
           }
       }
       return false;
   }
    
   public Component[] getComponents(Container aTop,Component[] disable) {
        Component[] comp = getComponents(aTop);
        List<Component> list = new ArrayList<Component>();
        for (Component pos: comp) {
            if (findComponent(pos,disable) == false) {
                list.add(pos);
            }
        }
        return (Component[])list.toArray(new Component[0]);
    }

    /**
     * Give the components for the given Container back (recursive search)
     * @param aTop
     * @return
     */
    private Component[] getComponents(Container aTop) {
        List<Component> list = new ArrayList<Component>();
        Component[] type = null; 
        list = getComponents(aTop, list);
        return (Component[])list.toArray(new Component[0]);
//        return (Component[])list.toArray(Component.class);
    }

    /**
     * 
     * @param aTop
     * @param list In the list will be stored all components which would be found
     * @return
     */
    private List getComponents(Container aTop, List list) {
        final Component[] comp = aTop.getComponents();
        for (int i = 0; i < comp.length; i++) {
            list.add(comp[i]);
            if (comp[i] instanceof JPanel) {
                getComponents((JPanel) comp[i], list);
            }
        }
        return list;
    }

    /**
     * Look for Emptyfields   
     * @param aTop Given container (JPanel), to search recursive the Textfields
     * @param showMessageDialog If a default message Dialog should be show
     * @return True, if found empty fields
     */
    public boolean hasEmptyFields(Container aTop, boolean showMessageDialog) {
        if (showMessageDialog) {
            return hasEmptyFields(aTop, new String(""));
        } else {
            return hasEmptyFields(aTop);
        }
    }

    /**
     * Look for Emptyfield
     * @param aTop Given container (JPanel), to search recursive the Textfields
     * @param message Message to set to the MessageDialog
     * @return True, if found empty fields
     */
    public boolean hasEmptyFields(Container aTop, String message) {
        boolean result = hasEmptyFields(aTop);
        if (result) {
            if (message.equals("")) {
                message = new String(NbBundle.getMessage(CommonBundleResolver.class, "Fields not filled"));
            }
            NotifyDescriptor.Message nd = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        return result;
    }

    /**
     * Look for emtyfields and show the error in the given JLabel
     * @param aTop aTop Given container (JPanel), to search recursive the Textfields
     * @param comp A vectro of JTextField, JLabel or AutoFilteringComboBox, JLabel pair 
     * @param showMessageDialog If a default message Dialog should be show
     * @return True, if found empty fields
     */
    public boolean hasEmptyFields(Container aTop, Vector<Component[]> comp, boolean showMessageDialog) {
        hasEmptyFields = false;
        boolean result = hasEmptyFields(aTop, comp);
        if (result && showMessageDialog) {
            NotifyDescriptor.Message nd = new NotifyDescriptor.Message(NbBundle.getMessage(CommonBundleResolver.class, "Red fields not filled"), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        return result;
    }

    /**
     * Check the whole component and under component recursive for emptry fields 
     * in JTextField components and AutoFilteringComboBox
     * @param aTop JPanel to check
     * @return true, if found empty fields
     */
    public boolean hasEmptyFields(Container aTop) {
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i].isFocusable() & comp[i].isEnabled()) {
                    if (comp[i] instanceof AutoFilteringComboBox && ((AutoFilteringComboBox) comp[i]).getText().equals("")) {
                        return true;
                    }
                    if (comp[i] instanceof JTextField) {
                        if (((JTextField) comp[i]).getText().trim().equals("")) {
                            return true;
                        }
                    }
                }
                if (comp[i] instanceof JPanel) {
                    if (hasEmptyFields((JPanel) comp[i])) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    /**
     * Look for emtyfields and show the error in the given JLabel
     * @param aTop aTop Given container (JPanel), to search recursive the Textfields
     * @param comp A vector of "JTextField, JLabel" or "AutoFilteringComboBox, JLabel" pair 
     * @return True, if found empty fields
     */
    public boolean hasEmptyFields(Container aTop, Vector<Component[]> compVector) {
        boolean exists;
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                //search for empty-fields components
                if (compVector != null) {
                    Enumeration<Component[]> enumVector = compVector.elements();
                    while (enumVector.hasMoreElements()) {
                        exists = false;
                        Component[] enumComp = enumVector.nextElement();
                        if (comp[i].isFocusable() & comp[i].isEnabled()) {
                            for (Component n : enumComp) {
                                if (comp[i] == n) {
                                    if (comp[i] instanceof AutoFilteringComboBox && ((AutoFilteringComboBox) comp[i]).getText().equals("")) {
                                        exists = true;
                                        hasEmptyFields = true;
                                    }
                                    if (comp[i] instanceof JTextField) {
                                        if (((JTextField) comp[i]).getText().trim().equals("")) {
                                            exists = true;
                                            hasEmptyFields = true;
                                        }
                                    }

                                }

                            }
                            if (exists) {
                                for (Component n2 : enumComp) {
                                    if (n2 instanceof LosLabel) {
                                        ((LosLabel) n2).setText("Empty field", CommonBundleResolver.class, IconType.ERROR);
                                    }
                                }
                            }
                        }
                    }
                }
                if (comp[i] instanceof JPanel) {
                    hasEmptyFields((JPanel) comp[i], compVector);
                }
            }
        }
        return hasEmptyFields;
    }

    public boolean hasErrorFields(Container aTop) {
        return hasIconTypeFields(aTop, IconType.ERROR);
    }

    public boolean hasWarningFields(Container aTop) {
        return hasIconTypeFields(aTop, IconType.WARNING);
    }

    public boolean hasErrorFields(Container aTop, boolean showMessageDialog) {
        boolean result = hasIconTypeFields(aTop, IconType.ERROR);
        if (result && showMessageDialog) {
            NotifyDescriptor.Message nd = new NotifyDescriptor.Message(NbBundle.getMessage(CommonBundleResolver.class, "Please fixed all errors"), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        return result;
    }

    private boolean hasIconTypeFields(Container aTop, IconType icon) {
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i].isFocusable() && comp[i].isEnabled()) {
                    if (comp[i] instanceof LosLabel) {
                        IconType iconType = ((LosLabel) comp[i]).getIconType();
                        if (iconType != null) {
                            if (iconType == icon) {
                                return true;
                            }
                        }
                    }
                }
                if (comp[i] instanceof JPanel) {
                    if (hasIconTypeFields((JPanel) comp[i], icon)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public LosLabel[] getWarningLabels(Container aTop) {
        return getIconTypeFields(aTop, IconType.WARNING);
    }

    private LosLabel[] getIconTypeFields(Container aTop, IconType icon) {
        List result = new ArrayList();
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i].isFocusable() && comp[i].isEnabled()) {
                    if (comp[i] instanceof LosLabel) {
                        IconType iconType = ((LosLabel) comp[i]).getIconType();
                        if (iconType != null) {
                            if (iconType == icon) {
                                result.add((LosLabel) comp[i]);
                            }
                        }
                    }
                }
                if (comp[i] instanceof JPanel) {
                    LosLabel[] list = getIconTypeFields((JPanel) comp[i], icon);
                    for (LosLabel n : list) {
                        result.add(n);
                    }
                }
            }
        }
//        (String[]) al.toArray(new String[0]); 
        return (LosLabel[]) result.toArray(new LosLabel[0]);
    }

    public boolean findMessage(String message, String[] messageArray) {
        for (String n : messageArray) {
            if (n.equals(message)) {
                return true;
            }
        }
        return false;
    }

    public boolean findKey(String message, String[][] messageArray) {
        for (int i = 0; i < messageArray.length; i++) {
            if (messageArray[i].equals(message)) {
                return true;
            }
        }
        return false;
    }

    public String findMessage(String message, Class bundle, Hashtable<String, String[]> h) {
        return findMessage(message, bundle, h, false);
    }

    public String findMessageDialog(String message, Class bundle, Hashtable<String, String[]> h) {
        return findMessage(message, bundle, h, true);
    }

    /**
     * 
     * @param message They key. The arguments it get from Hashtable with the same key
     * @param bundle From which bundle the message to get
     * @param h The hastable with key and arguments
     * @param isDialog If yes the Dialog Message will be hold (same like label key +"NOTIFYDESCRIPTOR"
     * @return The message
     */
    private String findMessage(String message, Class bundle, Hashtable<String, String[]> h, boolean isDialog) {
        String[] result = h.get(message);
        if (result != null) {
            if (isDialog) {
                return replacePlaceholder(NbBundle.getMessage(bundle, message + " NOTIFYDESCRIPTOR"), result);
            } else {
                return replacePlaceholder(NbBundle.getMessage(bundle, message), result);
            }
        }
        return null;
    }

    public String replacePlaceholder(String message, Object[] arguments) {
        MessageFormat formatter = new MessageFormat("");
        formatter.applyPattern(message);
        return formatter.format(arguments);
    }

    public JComponent findComponent(Container aTop, Component component) {
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i] == component) {
                    return (JComponent)comp[i];
                }
                if (comp[i] instanceof JPanel) {
                        findComponent((JPanel) comp[i],component);
                 }
            }
        }
        return null;      
    }
    
    
    
    private void setEnableComponents(Container aTop, Component[] c, List compList) {
        boolean exists = false;
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (c != null) {
                    for (Component n : c) {
                        if (comp[i] == n) {
                            addComponent(comp[i], true, compList);
                            exists = true;
                        }
                    }
                    if (comp[i] instanceof JPanel) {
                        setEnableComponents((JPanel) comp[i], c, compList);
                    }
                }
            }
        }
    }
    

    /**
     * Disable all components which are not listet in the compList
     * @param aTop
     * @param compList
     */
    private void setDisableComponents(Container aTop, List compList) {
        boolean exists;
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                exists = false;
                Iterator iter = compList.iterator();
                while (iter.hasNext()) {
                    ComponentObject acomp = (ComponentObject) iter.next();
                    if (acomp.getComponent() == comp[i]) {
                        exists = true;
                    }
                }
                if (exists == false) {
                    addComponent(comp[i], false, compList);
                }
                if (comp[i] instanceof JPanel) {
                    setDisableComponents((JPanel) comp[i], compList);
                }
            }
        }
    }

    /**
     * Start the process to enable components which would be added before in the 
     * SortedMap. (Note: Keys with the same value will be overwrite)
     * @param aTop is the JPanel on which the components are added.
     */
    public void processEnable(Container aTop) {
        List compList = new ArrayList();
        for (Integer key : map.keySet()) {
            setEnableComponents(aTop, map.get(key), compList);
        }
        setDisableComponents(aTop, compList);
        Iterator iter = compList.iterator();
        while (iter.hasNext()) {
            ComponentObject acomp = (ComponentObject) iter.next();
            if (acomp.isEnable() != acomp.getComponent().isEnabled()) {
                acomp.getComponent().setEnabled(acomp.isEnable());
            }
        }
        map.clear();
        compList.clear();
    }

    /**
     * Return a new map, in which the components added. if key in the TreeMap already exist,
     * the value will be override (see java documentation)
     * @return a Sortemap
     */
    public SortedMap<Integer, Component[]> getComponentMap() {
        return map = new TreeMap<Integer, Component[]>();
    }

    /**
     * Add the components to the compList. If the comp already exist the value will be override.
     * This compList will be needed because the components will be not enabled or disabled directly.
     * So side-effects, that cause, will be prevent for e.g. (Componet was disabled, the focus would 
     * loosed and in another step will be enabled again).
     * @param comp
     * @param enable
     * @param compList
     */
    private void addComponent(Component comp, boolean enable, List compList) {
        Iterator iter = compList.iterator();
        boolean exists = false;
        while (iter.hasNext()) {
            ComponentObject acomp = (ComponentObject) iter.next();
            if (acomp.getComponent() == comp) {
                exists = true;
                acomp.setEnable(enable);
            }
        }
        if (exists == false) {
            compList.add(new ComponentObject(comp, enable));
        }
    }

    private class ComponentObject {

        Component component;
        boolean enable;

        public ComponentObject(Component component, boolean enable) {
            this.component = component;
            this.enable = enable;
        }

        public Component getComponent() {
            return component;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }
}
