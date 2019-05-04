/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.component.other.DatePropertyEditor;
import de.linogistix.common.gui.listener.MinimumSizeComponentListener;
import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jdesktop.swingx.calendar.DateSpan;
import org.jdesktop.swingx.calendar.JXMonthView;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class LOSDatePickerDialog extends JPanel implements ActionListener{

    public DialogDescriptor dialogDescriptor;
    protected Dialog dialog;
    public final static String OK_BUTTON = "Ok";
    public final static String CANCEL_BUTTON = "Cancel";
    private JXMonthView _monthView = new JXMonthView();
    private Date selectedDate = new Date();
    
    private PropertyChangeListener myListener;
    
    DatePropertyEditor editor = new DatePropertyEditor();
    
// Create options
    JButton options[] = new JButton[]{
        new CustomButton(NbBundle.getMessage(CommonBundleResolver.class, OK_BUTTON), NbBundle.getMessage(CommonBundleResolver.class, OK_BUTTON), OK_BUTTON),
        new CustomButton(NbBundle.getMessage(CommonBundleResolver.class, CANCEL_BUTTON), NbBundle.getMessage(CommonBundleResolver.class, CANCEL_BUTTON), CANCEL_BUTTON)
    };
    
    public LOSDatePickerDialog(PropertyChangeListener listener){
        
        myListener = listener;
        
        dialogDescriptor = new DialogDescriptor(this,
                                                NbBundle.getMessage(CommonBundleResolver.class, "DatePicker"),
                                                true,
                                                options,
                                                options[1], //welcher defaultmaessig selektiert sein soll
                                                DialogDescriptor.DEFAULT_ALIGN,                
                                                null,    //getHelpCtx(),
                                                this);

        dialogDescriptor.setClosingOptions(new Object[]{options[1]});
        _monthView.setTraversable(true);
        add(_monthView);
    }
    
    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();
        if (action.equals(OK_BUTTON)) {
            
            DateSpan span = _monthView.getSelectedDateSpan();
            
            if (span != null) {
                selectedDate = new Date(span.getStart());
                myListener.propertyChange(new PropertyChangeEvent(this, "date", "", getSelectedDateString()));
            }
            else {
                NotifyDescriptor.Message nd = new NotifyDescriptor.Message(NbBundle.getMessage(CommonBundleResolver.class, "Please choose a date"), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);  
                return;
            }
        }
        closeDialog();
    }

    public void showDialog(Component relative) {
        dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        new MinimumSizeComponentListener(dialog);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(relative);
        dialog.pack();
        dialog.setVisible(true);
        _monthView.requestFocus();

    }

    public void closeDialog() {
        dialog.dispose();
    }
    
    public String getSelectedDateString(){
        if (selectedDate == null) {
            return null;
        }
        else{
            return new SimpleDateFormat("dd.MM.yyyy").format(selectedDate);
        }
    }
    
    public Date getSelectedDate(){
        return selectedDate;
    }
    
    public class CustomButton extends JButton {

        public CustomButton(String title, String mnemonic, String actionCommand) {
            super(title);
            if( mnemonic != null && mnemonic.length() > 0) {
                setMnemonic(mnemonic.charAt(0));
            }
            setActionCommand(actionCommand);
        }
    }
}
