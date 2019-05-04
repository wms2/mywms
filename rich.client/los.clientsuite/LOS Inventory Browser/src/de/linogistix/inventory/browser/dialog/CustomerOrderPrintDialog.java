/*
 * Copyright (c) 2012 LinogistiX GmbH
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
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
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
public class CustomerOrderPrintDialog extends javax.swing.JDialog {
    List<LOSCustomerOrderTO> orders;
    private J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

    /** Creates new form SaveDialog */
    public CustomerOrderPrintDialog(List<LOSCustomerOrderTO> orders) {
        super(WindowManager.getDefault().getMainWindow(), true);
        this.orders = orders;

        initComponents();

        btSave.setText( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.buttonSave") );
        btCancel.setText( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.buttonCancel") );
        setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.title") );


        fUrl1.setText("");
        fUrl2.setText("");
        fPrintExternal.setSelected(false);
        if( orders.size()==1 ) {
            try {
                LOSCustomerOrderQueryRemote orderQuery;
                orderQuery = loc.getStateless(LOSCustomerOrderQueryRemote.class);
                LOSCustomerOrder order = orderQuery.queryById(orders.get(0).getId());
                if( !StringTools.isEmpty(order.getDocumentUrl()) ) {
                    fUrl1.setText(order.getDocumentUrl());
                    fPrintExternal.setSelected(true);
                }
                if( !StringTools.isEmpty(order.getLabelUrl()) ) {
                    fUrl2.setText(order.getLabelUrl());
                    fPrintExternal.setSelected(true);
                }
            } catch (Exception ex) {
                ExceptionAnnotator.annotate(ex);
            }
        }


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
            fPrintExternal.setSelected(false);
            fPrintReceipt.setSelected(false);
            fSaveReceipt.setSelected(true);
        }

        fPrintExternal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fUrl1.setEnabled(fPrintExternal.isSelected());
                fUrl2.setEnabled(fPrintExternal.isSelected());
                fPrinter.setEnabled( fPrintReceipt.isSelected() || fPrintExternal.isSelected() );
            }
        });

        fSaveReceipt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fFile.setEnabled(fSaveReceipt.isSelected());
            }
        });

        fPrintReceipt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fPrinter.setEnabled( fPrintReceipt.isSelected() || fPrintExternal.isSelected() );
            }
        });

        fPrinter.setEnabled( fPrintReceipt.isSelected() || fPrintExternal.isSelected() );
        fFile.setEnabled(fSaveReceipt.isSelected());





        try {
            setIconImage(new ImageIcon(getClass().getResource("/de/linogistix/common/res/icon/los.gif")).getImage());
        }
        catch( Throwable t ) {
            System.out.println("Error loading icon: "+t.getMessage());
        }

        if( orders.size() <= 0 ) {
            btSave.setEnabled(false);
        }


        fUrl1.setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.labelUrl") );
        fUrl2.setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.labelUrl") );
        fPrinter.setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.labelPrinter") );
        fSaveReceipt.setText( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.labelSaveReceipt") );
        fPrintReceipt.setText( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.labelPrintReceipt") );
        fPrintExternal.setText( NbBundle.getMessage(InventoryBundleResolver.class,"CustomerOrderPrintDialog.labelPrintExternal") );


        getRootPane().setDefaultButton(btSave);
        pack();
        setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
        fUrl1.selectAll();
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
        fUrl1 = new de.linogistix.common.gui.component.controls.LOSTextField();
        fSaveReceipt = new javax.swing.JCheckBox();
        fPrintReceipt = new javax.swing.JCheckBox();
        fPrintExternal = new javax.swing.JCheckBox();
        fPrinter = new de.linogistix.common.gui.component.controls.LOSComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        fFile = new de.linogistix.common.gui.component.controls.LOSTextField();
        btFile = new javax.swing.JButton();
        fUrl2 = new de.linogistix.common.gui.component.controls.LOSTextField();

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
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

        fUrl1.setColumns(20);
        fUrl1.setTitle("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 34, 0, 10);
        getContentPane().add(fUrl1, gridBagConstraints);

        fSaveReceipt.setText("Save Receipt");
        fSaveReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fSaveReceiptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(fSaveReceipt, gridBagConstraints);

        fPrintReceipt.setSelected(true);
        fPrintReceipt.setText("Print Receipt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(fPrintReceipt, gridBagConstraints);

        fPrintExternal.setText("Print External Documents");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(fPrintExternal, gridBagConstraints);

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

        fUrl2.setColumns(20);
        fUrl2.setTitle("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 34, 0, 10);
        getContentPane().add(fUrl2, gridBagConstraints);

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

    private void fSaveReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fSaveReceiptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fSaveReceiptActionPerformed

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
    private javax.swing.JCheckBox fPrintExternal;
    private javax.swing.JCheckBox fPrintReceipt;
    private de.linogistix.common.gui.component.controls.LOSComboBox fPrinter;
    private javax.swing.JCheckBox fSaveReceipt;
    private de.linogistix.common.gui.component.controls.LOSTextField fUrl1;
    private de.linogistix.common.gui.component.controls.LOSTextField fUrl2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables



    private boolean save() {

        LOSOrderFacade orderFacade;
//        LOSCustomerOrderQueryRemote orderQuery;
        try {
            orderFacade = loc.getStateless(LOSOrderFacade.class);
//            orderQuery = loc.getStateless(LOSCustomerOrderQueryRemote.class);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        PrintService printService = (PrintService)fPrinter.getSelectedItem();
        

        try {
            for( LOSCustomerOrderTO orderTO : orders ) {
                Document receipt = null;
//                LOSCustomerOrder order = orderQuery.queryById(orderTO.getId());

                if( fPrintReceipt.isSelected() || fSaveReceipt.isSelected() ) {
                    receipt = orderFacade.generateReceipt(orderTO.getId(), false);
                }

                if( fSaveReceipt.isSelected() && receipt != null ) {
                    if (receipt.getDocument().length == 0){
                        FacadeException ex = new FacadeException("Document is empty", "BusinessException.DocumentEmpty", null);
                        ex.setBundleResolver(CommonBundleResolver.class);
                        ExceptionAnnotator.annotate(ex);
                        return false;
                    }
                    File outf = new File(fFile.getText(), receipt.getName() + ".pdf");
                    FileOutputStream out = new FileOutputStream(outf);
                    out.write(receipt.getDocument());
                    out.flush();
                    out.close();
                }
                
                if( fPrintReceipt.isSelected() && receipt != null && printService != null ) {
                    print( receipt.getDocument(), printService );
                }

                if( fPrintExternal.isSelected() ) {
                    String url = fUrl1.getText();
                    if( !StringTools.isEmpty(url) ) {
                        byte[] bytes = httpGet(url);
                        print( bytes, printService );
                    }
                    url = fUrl2.getText();
                    if( !StringTools.isEmpty(url) ) {
                        byte[] bytes = httpGet(url);
                        print( bytes, printService );
                    }
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


    public byte[] httpGet(String urlStr) throws Exception {
        InputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            URL url = new URL(urlStr); // Create the URL
            in = url.openStream(); // Open a stream to it
            // Now copy bytes from the URL to the output stream
            byte[] buffer = new byte[4096];
            int bytes_read;
            while ((bytes_read = in.read(buffer)) != -1){
                    out.write(buffer, 0, bytes_read);
            }

            return out.toByteArray();
        }
        catch (Exception e) {
            throw e;
        } finally { // Always close the streams, no matter what.
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }


}
