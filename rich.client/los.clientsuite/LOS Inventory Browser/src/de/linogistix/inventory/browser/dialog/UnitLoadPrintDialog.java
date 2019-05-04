/*
 * Copyright (c) 2013 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.dialog;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.inventory.query.dto.LOSPickingUnitLoadTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.StringTools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.mywms.facade.FacadeException;
import org.mywms.model.Document;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author krane
 */
public class UnitLoadPrintDialog extends javax.swing.JDialog {
    List<BODTO> orders;
    private J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

    /** Creates new form SaveDialog */
    public UnitLoadPrintDialog(List<BODTO> orders) {
        super(WindowManager.getDefault().getMainWindow(), true);
        this.orders = orders;

        initComponents();

        btSave.setText( NbBundle.getMessage(InventoryBundleResolver.class,"UnitLoadPrintDialog.buttonSave") );
        btCancel.setText( NbBundle.getMessage(InventoryBundleResolver.class,"UnitLoadPrintDialog.buttonCancel") );
        setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"UnitLoadPrintDialog.title") );



        fFile.setText( System.getProperty("user.home") );



        PrintService[] services;

        // get all print services for this machine
        services = PrintServiceLookup.lookupPrintServices(null, null);
        if( services != null ) {
            for( PrintService service : services ) {
                fPrinter.addItem(service);
            }
            PrintService serviceDefault = PrintServiceLookup.lookupDefaultPrintService();
            if( serviceDefault != null ) {
                fPrinter.setSelectedItem(serviceDefault);
            }
        }
        if( fPrinter.getItemCount() == 0 ) {
            fPrintLabel.setSelected(false);
            fSaveLabel.setSelected(true);
        }


        fSaveLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fFile.setEnabled(fSaveLabel.isSelected());
            }
        });

        fPrintLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fPrinter.setEnabled( fPrintLabel.isSelected() );
            }
        });

        fPrinter.setEnabled( fPrintLabel.isSelected() );
        fFile.setEnabled(fSaveLabel.isSelected());





        try {
            setIconImage(new ImageIcon(getClass().getResource("/de/linogistix/common/res/icon/los.gif")).getImage());
        }
        catch( Throwable t ) {
            System.out.println("Error loading icon: "+t.getMessage());
        }

        if( orders.size() <= 0 ) {
            btSave.setEnabled(false);
        }


        fPrinter.setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"UnitLoadPrintDialog.labelPrinter") );
        fSaveLabel.setText( NbBundle.getMessage(InventoryBundleResolver.class,"UnitLoadPrintDialog.labelSaveLabel") );
        fPrintLabel.setText( NbBundle.getMessage(InventoryBundleResolver.class,"UnitLoadPrintDialog.labelPrintLabel") );


        getRootPane().setDefaultButton(btSave);
        pack();
        setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
    }

    @Override
    protected JRootPane createRootPane() {
        ActionListener actionListener = new ActionListener() {
          public void actionPerformed(ActionEvent actionEvent) {
            setVisible(false);
          }
        };
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        fSaveLabel = new javax.swing.JCheckBox();
        fPrintLabel = new javax.swing.JCheckBox();
        fPrinter = new de.linogistix.common.gui.component.controls.LOSComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        fFile = new de.linogistix.common.gui.component.controls.LOSTextField();
        btFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Change Picking Order Properties");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btSave.setText("Print");
        btSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveActionPerformed(evt);
            }
        });
        jPanel1.add(btSave);

        btCancel.setText("Cancel");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });
        jPanel1.add(btCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(jSeparator1, gridBagConstraints);

        fSaveLabel.setText("Save Label");
        fSaveLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fSaveLabelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(fSaveLabel, gridBagConstraints);

        fPrintLabel.setSelected(true);
        fPrintLabel.setText("Print Label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(fPrintLabel, gridBagConstraints);

        fPrinter.setTitle("Printer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(fPrinter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(jSeparator2, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        fFile.setColumns(20);
        fFile.setTitle("Verzeichnis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(fFile, gridBagConstraints);

        btFile.setText("...");
        btFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        jPanel2.add(btFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 34, 0, 10);
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
        if( save() ) {
            setVisible(false);
        }
    }//GEN-LAST:event_btSaveActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btCancelActionPerformed

    private void fSaveLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fSaveLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fSaveLabelActionPerformed

    private void btFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFileActionPerformed
        File f;
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = chooser.showSaveDialog(null);
        if ((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) {
            f = chooser.getSelectedFile();
            if (!f.isDirectory()){
                FacadeException ex = new FacadeException("Please choose a director", "BusinessException.ChooseDirectory", null);
                ex.setBundleResolver(CommonBundleResolver.class);
                ExceptionAnnotator.annotate(ex);
                return;
            }

            fFile.setText(f.getPath());
        } else{
            return;
        }
    }//GEN-LAST:event_btFileActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btFile;
    private javax.swing.JButton btSave;
    private de.linogistix.common.gui.component.controls.LOSTextField fFile;
    private javax.swing.JCheckBox fPrintLabel;
    private de.linogistix.common.gui.component.controls.LOSComboBox fPrinter;
    private javax.swing.JCheckBox fSaveLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables



    private boolean save() {

        LOSOrderFacade orderFacade;
        LOSCustomerOrderQueryRemote orderQuery;
        try {
            orderFacade = loc.getStateless(LOSOrderFacade.class);
            orderQuery = loc.getStateless(LOSCustomerOrderQueryRemote.class);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        PrintService printService = (PrintService)fPrinter.getSelectedItem();
        

        try {
            for( BODTO bodto : orders ) {
                String label = null;
                if( bodto instanceof LOSPickingUnitLoadTO ) {
                    label = ((LOSPickingUnitLoadTO)bodto).getLabel();
                }
                else {
                    label = bodto.getName();
                }
                if( label == null ) {
                    continue;
                }

                Document doc = null;

                if( fPrintLabel.isSelected() || fSaveLabel.isSelected() ) {
                    doc = orderFacade.generateUnitLoadLabel(label, false);
                }

                if( fSaveLabel.isSelected() && doc != null ) {
                    if (doc.getDocument().length == 0){
                        FacadeException ex = new FacadeException("Document is empty", "BusinessException.DocumentEmpty", null);
                        ex.setBundleResolver(CommonBundleResolver.class);
                        ExceptionAnnotator.annotate(ex);
                        return false;
                    }
                    File outf = new File(fFile.getText(), doc.getName() + ".pdf");
                    FileOutputStream out = new FileOutputStream(outf);
                    out.write(doc.getDocument());
                    out.flush();
                    out.close();
                }
                
                if( fPrintLabel.isSelected() && doc != null && printService != null ) {
                    print( doc.getDocument(), printService );
                }

            }
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        return true;
    }

    public void print( byte[] bytes, PrintService printService ) throws Exception {
        if( bytes == null || bytes.length<=0 ) {
            System.out.println("Will not print. Missing document");
            return;
        }

        DocFlavor fl;
        fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc;
        DocAttributeSet das = new HashDocAttributeSet();
        DocPrintJob job;

        doc = new SimpleDoc(bytes, fl, das);
        job = printService.createPrintJob();
        job.print(doc, null);
    }



}
