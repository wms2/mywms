/*
 * Copyright (c) 2009 - 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.dialog;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.query.dto.LOSPickingOrderTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.mywms.facade.FacadeException;
import org.mywms.model.User;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author krane
 */
public class PickingOrderEditDialog extends javax.swing.JDialog {
    List<LOSPickingOrderTO> orders;
    private J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

    private boolean changed = false;

    /** Creates new form SaveDialog */
    public PickingOrderEditDialog(List<LOSPickingOrderTO> orders) {
        super(WindowManager.getDefault().getMainWindow(), true);
        this.orders = orders;

        initComponents();

        btSave.setText( NbBundle.getMessage(InventoryBundleResolver.class,"PickingOrderEditDialog.buttonSave") );
        btCancel.setText( NbBundle.getMessage(InventoryBundleResolver.class,"PickingOrderEditDialog.buttonCancel") );
        setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"PickingOrderEditDialog.title") );

        String userName = null;
        boolean userDiff = false;
        String destinationName = null;
        boolean destinationDiff = false;
        Integer prio = null;
        boolean prioDiff = false;
        int numOrder = 0;
        boolean orderStarted = false;

        for( LOSPickingOrderTO o : orders ) {
            if( numOrder == 0 ) {
                userName = o.getUserName();
                destinationName = o.getDestinationName();
                prio = o.getPrio();
            }
            else {
                if( StringTools.compare(userName, o.getUserName())!=0 ) {
                    userDiff = true;
                }
                if( StringTools.compare(destinationName, o.getDestinationName())!=0 ) {
                    destinationDiff = true;
                }
                if( prio != o.getPrio() ) {
                    prioDiff = true;
                }
            }
            if( o.getState() >= State.STARTED ) {
                orderStarted = true;
            }
            numOrder++;
        }

        cbDestination.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fDestination.setEnabled(cbDestination.isSelected());
            }
        });
        fDestination.setBoClass(LOSStorageLocation.class);
        fDestination.initAutofiltering();
        fDestination.setEditorLabelTitle( NbBundle.getMessage(InventoryBundleResolver.class,"PickingOrderEditDialog.labelDestination") );
        if( !destinationDiff ) {
            fDestination.addItem(destinationName);
        }
        cbDestination.setSelected(numOrder==1);
        fDestination.setEnabled(numOrder==1);
        
        cbUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fUser.setEnabled(cbUser.isSelected());
            }
        });
        fUser.setBoClass(User.class);
        fUser.initAutofiltering();
        fUser.setEditorLabelTitle( NbBundle.getMessage(InventoryBundleResolver.class,"PickingOrderEditDialog.labelUser") );
        if( !userDiff ) {
            fUser.addItem(userName);
        }
        cbUser.setSelected(numOrder==1);
        fUser.setEnabled(numOrder==1);

        cbPrio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fPrio.setEnabled(cbPrio.isSelected());
            }
        });
        cbPrio.setSelected(!prioDiff);
        fPrio.setEnabled(!prioDiff);
        if( prio != null && !prioDiff ) {
            fPrio.setText(prio.toString());
        }
        fPrio.setTitle( NbBundle.getMessage(InventoryBundleResolver.class,"PickingOrderEditDialog.labelPrio") );

        try {
            setIconImage(new ImageIcon(getClass().getResource("/de/linogistix/common/res/icon/los.gif")).getImage());
        }
        catch( Throwable t ) {
            System.out.println("Error loading icon: "+t.getMessage());
        }

        if( orderStarted || orders.size() == 0 ) {
            btSave.setEnabled(false);
        }

        getRootPane().setDefaultButton(btSave);
        pack();
        setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
        fPrio.selectAll();
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

        jPanel1 = new javax.swing.JPanel();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        fDestination = new de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox();
        fPrio = new de.linogistix.common.gui.component.controls.LOSTextField();
        fUser = new de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox();
        cbDestination = new javax.swing.JCheckBox();
        cbUser = new javax.swing.JCheckBox();
        cbPrio = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Change Picking Order Properties");
        setResizable(false);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btSave.setText("Save");
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

        fDestination.setEditorLabelTitle("Destination");

        fPrio.setColumns(3);
        fPrio.setTitle("Prio");

        fUser.setEditorLabelTitle("User");

        cbPrio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPrioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbPrio)
                    .addComponent(cbDestination)
                    .addComponent(cbUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fPrio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .addComponent(fDestination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbPrio)
                    .addComponent(fPrio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fDestination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDestination))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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

    private void cbPrioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPrioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPrioActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.JCheckBox cbDestination;
    private javax.swing.JCheckBox cbPrio;
    private javax.swing.JCheckBox cbUser;
    private de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox fDestination;
    private de.linogistix.common.gui.component.controls.LOSTextField fPrio;
    private de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox fUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables



    private boolean save() {

        LOSPickingFacade pickingFacade;
        try {
            pickingFacade = (LOSPickingFacade) loc.getStateless(LOSPickingFacade.class);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        changed = true;
        
        Integer prio = null;
        String sPrio = fPrio.getText();
        if( sPrio != null && sPrio.length()>0 ) {
            try {
                prio = Integer.valueOf(sPrio);
            }
            catch( Throwable t ) {}
        }

        String locationName = fDestination.getSelectedAsText();
        String userName = fUser.getSelectedAsText();

        try {
            for( LOSPickingOrderTO order : orders ) {
                if( cbPrio.isSelected() ) {
                    pickingFacade.changePickingOrderPrio(order.getId(), prio);
                }
                if( cbDestination.isSelected() ) {
                    pickingFacade.changePickingOrderDestination(order.getId(), locationName);
                }
                if( cbUser.isSelected() ) {
                    pickingFacade.changePickingOrderUser(order.getId(), userName);
                }
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
            return false;
        }

        return true;
    }



    public boolean isChanged() {
        return changed;
    }



}
