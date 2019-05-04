/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.openide.util.NbBundle;

/**
 *
 * Componenten for standardized JTextField and a JLabel within one JPanel.
 * 
 * Careful: when added as JPanel having a BoderLayout via Matisse, component 
 * might not been shown properly.
 *
 * Add this component to mantisse using palette manager.
 *
 * Important BeanProperties for configuring this component via mantisse:
 * <ul>
 *      <li> {@link #setEditable(boolean) }
 *      <li> {@link #setEnabled(boolean) }
 *      <li> {@link #setFont(java.awt.Font)  }
 *      <li> {@link #setMandatory(boolean)  }
 *      <li> {@link #setSuppressWarnings(boolean)   }
 *      <li> {@link #setText(java.lang.String) (boolean)   }
 *      <li> {@link #setTitle(java.lang.String)}
 *      <li> {@link #setPreferredTextfieldSize(java.awt.Dimension)  }
 *
 * </ul>
 * 
 * @author Jordan
 */
public class LOSTextField extends JPanel implements FocusListener{

    

    private JTextField myTextField;
    
    private LosLabel textFieldLabel = new LosLabel();
    
    private boolean mandatory = false;
    
    private boolean suppressWarnings = false;
    
    private boolean upperCase = false;
    
    private LOSTextFieldListener keyListener = null;
    private List<PropertyChangeListener> changeListeners = new ArrayList<PropertyChangeListener>();
    private String oldValue;

    private int fontIncrement;

    private boolean consumeEnter = false;
    
    public LOSTextField() {
        
        setLayout(new GridBagLayout());
        //setBorder(new EmptyBorder(2, 2, 2, 2));
        myTextField = new JTextField();
        //myTextField.setMargin(new Insets(2, 2, 2, 2));
        myTextField.addFocusListener(this);
        
        myTextField.addKeyListener(new KeyAdapter() {


            @Override
            public void keyPressed(KeyEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        checkSanity();
                        fireItemChangedEvent();
                    }
                }
                );
                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    myTextField.selectAll();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fireItemEnteredEvent();
                    if (isConsumeEnter())
                        e.consume();
                    myTextField.transferFocus();
                }
            }
            
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(myTextField, gbc);
    }


    public boolean checkSanity(){
        
        if(myTextField.getText().startsWith(" ")){
            if(!suppressWarnings){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "NO_LEADING_SPACE"), IconType.ERROR);
            }
            return false;
        }
        
        if(upperCase){
            myTextField.setText(myTextField.getText().toUpperCase());
        }
        
        if(mandatory && myTextField.getText().length() == 0){
            if(!suppressWarnings){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "INPUT_REQUIRED"), IconType.ERROR);
            }
            return false;
        }
        else if(keyListener != null && keyListener.checkValue(myTextField.getText(),textFieldLabel) == false ) {
            // the label text has to be set by the keyListener
            return false;
        }
        else{
            textFieldLabel.setText();
            return true;
        }
        
    }
    
    public LosLabel getTextFieldLabel() {
        return textFieldLabel;
    }
    
    public void setText(String text){
        myTextField.setText(text);
    }
    
    public String getText(){
        return myTextField.getText();
    }
    
    public void setTitle(String text){
        textFieldLabel.setTitleText(text);
    }
    
    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean isMandatory) {
        textFieldLabel.setShowMandatoryFlag(isMandatory);
        this.mandatory = isMandatory;
    }
    
    public boolean isSuppressWarnings() {
        return suppressWarnings;
    }

    public void setSuppressWarnings(boolean suppressWarnings) {
        this.suppressWarnings = suppressWarnings;
    }
    
    public boolean isUpperCase() {
        return upperCase;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        myTextField.setEnabled(enabled);
        textFieldLabel.setEnabled(enabled);
    }
        
    /**
     *       Implementation of FocusListener
     */
    public void focusGained(FocusEvent e) {
        myTextField.selectAll();
        oldValue = myTextField.getText();
        myTextField.selectAll();
    }
    
    public void focusLost(FocusEvent e) {
        myTextField.select(1,1);
        
        checkSanity();
        
        fireItemChangedEvent();
        
    }
    
    /**
     * Registering a listener for the field. It is called each time a key is pressed.
     * @param listener
     */
    public void setKeyListener(LOSTextFieldListener listener) {
        this.keyListener = listener;
    }

    public void removeKeyListener() {
        this.keyListener = null;
    }	  

    /**
     * Registering a listener for the field.
     * It is called, when the input of the field is finished (focusLost).
     * @param PropertyChangeListener
     */
    public void addItemChangeListener(PropertyChangeListener l) {
        if(!changeListeners.contains(l))
            changeListeners.add(l);
    }
    public void removeItemChangedListener(PropertyChangeListener l) {
        changeListeners.remove(l);
    }
    
    public void fireItemChangedEvent(){
        
        if( changeListeners.size() > 0 ) {
            if( oldValue != null && !oldValue.equals(myTextField.getText()) ) {
                PropertyChangeEvent pce = new PropertyChangeEvent(this, LOSComponentEvent.ITEM_CHANGED, null, myTextField.getText());
                for (PropertyChangeListener p : changeListeners) {
                    p.propertyChange(pce);
                }
            }
        }
        oldValue = myTextField.getText();
    }

    public void fireItemEnteredEvent(){

        if (changeListeners.size() > 0) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this, LOSComponentEvent.ITEM_ENTERED, null, myTextField.getText());
            for (PropertyChangeListener p : changeListeners) {
                p.propertyChange(pce);
            }
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        return myTextField.requestFocusInWindow();
    }


    @Override
    public void requestFocus() {
        myTextField.requestFocus();
    }

    @Override
    public boolean hasFocus() {
        return myTextField.hasFocus();
    }


    public void selectAll() {
        myTextField.selectAll();
    }

    public JTextField getTextField() {
        return myTextField;
    }

    public void setEditable(boolean editable){
        myTextField.setEditable(editable);
    }

    public boolean isEditable(){
        return myTextField.isEditable();
    }

    @Override
    public Font getFont(){
        if (myTextField != null)
            return myTextField.getFont();
        else
            return super.getFont();
    }

    @Override
    public void setFont(Font font){
        if (myTextField != null)
            myTextField.setFont(font);
    }

//    public void setPreferredTextfieldSize(Dimension preferredSize) {
//        myTextField.setPreferredSize(preferredSize);
//    }
//
//    public Dimension getPreferredTextfieldSize() {
//        return myTextField.getPreferredSize();
//    }


    public void setColumns(int num) {
        this.myTextField.setColumns(num);
    }
    public int getColumns() {
        return this.myTextField.getColumns();
    }

    /**
     * @return the consumeEnter
     */
    public boolean isConsumeEnter() {
        return consumeEnter;
    }

    /**
     * @param consumeEnter the consumeEnter to set
     */
    public void setConsumeEnter(boolean consumeEnter) {
        this.consumeEnter = consumeEnter;
    }
}
