package de.linogistix.inventory.browser.dialog;

import de.linogistix.common.gui.component.controls.LosLabel;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.res.CommonBundleResolver;
import de.wms2.mywms.address.Address;
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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
public class AddressTextField 
        extends JPanel 
        implements FocusListener, PropertyChangeListener
{
    
    private Address address;

    private JButton datePickerButton;
    private JTextField myTextField;
    private LosLabel textFieldLabel = new LosLabel();
    private AddressEditDialog datePicker;
    
    private boolean mandatory;
    
    private boolean datePickerButtonPressed = false;

    
    
    public AddressTextField(){

        setLayout(new GridBagLayout());

        textFieldLabel.setText("");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);
        
        myTextField = new JTextField();
//        myTextField.setMargin(new Insets(2, 2, 2, 2));
        myTextField.addFocusListener(this);
        myTextField.setColumns(16);
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
                    if( getAddress()!=null )
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
        
        myTextField.setEditable(false);

    }

    public boolean checkSanity(){
        if(getAddress()!=null){
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
        datePicker = new AddressEditDialog(address, this);
        datePicker.setVisible(true);
        datePickerButtonPressed = false;
    }
    

    /**
     * 
     * @return It returns null if wrong date or no date would be set
     */
    public Address getAddress() {
        return address;
    }

    public void setAddress( Address address ) {
        this.address = address;
        myTextField.setText(address==null?"":address.toUniqueString());
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
//        myTextField.setEnabled(enabled);
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
        if(!datePickerButtonPressed){
            checkSanity();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        setAddress(datePicker.getAddress());
        checkSanity();
    }

    public void clear() {
        address = null;
        myTextField.setText("");
    }
    
    @Override
    public void requestFocus() {
        myTextField.requestFocus();
    }
    
     public void setEditable(boolean editable){
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

    public void setColumns(int num) {
        this.myTextField.setColumns(num);
    }
    public int getColumns() {
        return this.myTextField.getColumns();
    }
    
    public JTextField getTextField() {
        return myTextField;
    }
}
