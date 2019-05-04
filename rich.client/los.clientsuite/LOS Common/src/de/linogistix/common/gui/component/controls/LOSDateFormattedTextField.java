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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class LOSDateFormattedTextField 
        extends JPanel 
        implements FocusListener, PropertyChangeListener
{
    
    List<SimpleDateFormat> formatList = new ArrayList<SimpleDateFormat>();
    MaskFormatter maskFormat;
    SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
    
    private JButton datePickerButton;
    private JFormattedTextField myTextField;
    private LosLabel textFieldLabel = new LosLabel();
    private LOSDatePickerDialog datePicker;
    
    private boolean mandatory;
    
    private boolean datePickerButtonPressed = false;

    
    
    public LOSDateFormattedTextField(){
        //setBorder(new EmptyBorder(2, 2, 2, 2));
        // Initialize default format
        try {
            this.maskFormat = new MaskFormatter("##.##.####");
        } catch (ParseException pe) {
            this.maskFormat = new MaskFormatter();
        }
        
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        format.setLenient(false);
        addInputFormat(format);
        
        format = new SimpleDateFormat("dd.MM.yy");
        format.setLenient(false);
        addInputFormat(format);
        

        setLayout(new GridBagLayout());

        textFieldLabel.setText("");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);
        
        myTextField = new JFormattedTextField(getMaskFormat());
//        myTextField.setMargin(new Insets(2, 2, 2, 2));
        myTextField.addFocusListener(this);
        myTextField.setColumns(8);
        myTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isEditable())
                    return;

                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    myTextField.selectAll();
                }
                if (e.getKeyCode() == KeyEvent.VK_F3) {
                    datePickerButtonPressed = true;
                    showDialog();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if( getDate()!=null )
                        myTextField.transferFocus();
                    else
                        myTextField.selectAll();
                }
            }
        });

        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(myTextField, gbc);
        
        datePickerButton = new JButton("..."){
            
            @Override
            public Dimension getPreferredSize() {
                Dimension pfs = super.getPreferredSize();
                pfs.setSize(24, 22);
                return pfs;
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(24, 22);
            }
            
        };
        
        datePickerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                datePickerButtonPressed = true;
                showDialog();
            }
        });
        
        datePickerButton.setFocusable(false);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 2, 2, 0);
        gbc.anchor = GridBagConstraints.SOUTHWEST;

        add(datePickerButton, gbc);
    }

    /**
     * Reset the field to default value
     */
    public void reformat() {
        myTextField.setValue(""); // if the value is on then formatting won't occur, haven't figured it why but it might have something to do with below commented method call.
        DefaultFormatterFactory factory = new DefaultFormatterFactory(maskFormat); // here is the change, you transform the formatter to a factory.
        myTextField.setFormatterFactory(factory); // and reset the text field with that!           
    }

    public boolean checkSanity(){
        if(getDate()!=null){
            textFieldLabel.setText();
            return true;
        }
        else{
            if(mandatory){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "NO_VALID_DATE"), IconType.ERROR);
                return false;
            }
            else{
                return true;
            }
        }
    }
    
    private void showDialog(){
        datePicker = new LOSDatePickerDialog(this);
        datePicker.showDialog(datePickerButton);
        datePickerButtonPressed = false;
    }
    

    /**
     * 
     * @return It returns null if wrong date or no date would be set
     */
    public Date getDate() {
        Date date = null;
        String input = myTextField.getText().trim();
        for( SimpleDateFormat format:formatList) {
            if (input.length() == format.toPattern().length()) {
                try {
                    date = format.parse(input);
                    myTextField.setText(outputFormat.format( date ));
                    break;
                } catch (ParseException pe) {}
            }
        }
        return date;
    }

    public void setDate( Date date ) {
        myTextField.setText(outputFormat.format( date ));
    }
    
    public LosLabel getTextFieldLabel() {
        return textFieldLabel;
    }
    
    public void setText(String text){
        myTextField.setText(text);
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
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        myTextField.setEnabled(enabled);
//
//        if(enabled){
//            myTextField.setBackground(new Color(255, 255, 255));
//        }
//        else{
//            myTextField.setBackground(new Color(212, 208, 200));
//        }
        
        datePickerButton.setEnabled(enabled);
    }
    
    
    /**
     *       Implementation of FocusListener
     */
    public void focusGained(FocusEvent e) {
        myTextField.setCaretPosition(0);
    }
    
    public void focusLost(FocusEvent e) {
        if (getDate() == null) {
            reformat();
        }
        
        if(!datePickerButtonPressed){
            checkSanity();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        myTextField.setText(datePicker.getSelectedDateString());
        checkSanity();
    }

    public void clear() {
        myTextField.setText("");
    }
    
    @Override
    public void requestFocus() {
        myTextField.requestFocus();
    }
    
    public void addInputFormat( SimpleDateFormat format ) {
        this.formatList.add(format);
    }
    public void clearInputFormat() {
        this.formatList.clear();
    }
    public List<SimpleDateFormat> getInputFormatList() {
        return formatList;
    }
    
    public void setOutputFormat( SimpleDateFormat format ) {
        this.outputFormat = format;
    }
    public SimpleDateFormat getOutputFormat() {
        return outputFormat;
    }
    
    public void setMaskFormat( MaskFormatter formatter ) {
        this.maskFormat = formatter;
    }
    public MaskFormatter getMaskFormat() {
        return maskFormat;
    }

     public void setEditable(boolean editable){
        myTextField.setEditable(editable);
        datePickerButton.setEnabled(editable);
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
    
    public JFormattedTextField getTextField() {
        return myTextField;
    }
}
