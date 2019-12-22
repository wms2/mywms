/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.queryinventory.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.inventory.report.queryinventory.gui.gui_builder.AbstractCenterPanel;

import de.linogistix.common.gui.component.other.LiveHelp;
import de.linogistix.common.gui.component.windows.JSaveDialog;
import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.ClientItemDataLotFilteringComponent;
import de.linogistix.inventory.report.queryinventory.gui.gui_builder.AbstractTreeTableViewPanel.InventoryNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.mywms.model.Client;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class CenterPanel extends AbstractCenterPanel implements TopComponentListener {

    private static final Logger log = Logger.getLogger(CenterPanel.class.getName());
    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
    TopComponentPanel topComponentPanel;
    ClientItemDataLotFilteringComponent cilCombo;
    private BOAutoFilteringComboBox<Client> clientCombo;

    public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
    }

    public TreeTableViewPanel getTreeTableViewPanel() {
        return treeTablePanel;
    }

    private void initAutofiltering() throws Exception{
        
        this.cilCombo = new ClientItemDataLotFilteringComponent(autofilteringPanel);
        clientCombo = new BOAutoFilteringComboBox<Client>();
        clientCombo.setBoClass(Client.class);
        clientCombo.initAutofiltering();
        clientCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "Client"));

//        this.cilCombo.setClientMandatory(true);
        clientCombo.setMandatory(true);
        if( clientCombo.getSelectedItem() == null ) {
            LoginService login = Lookup.getDefault().lookup(LoginService.class);
            Client client = login.getUsersClient();
            try {
                clientCombo.clear();
                clientCombo.addItem(client);
            }
            catch( Exception e) {}
        }
        Dimension d = new Dimension(5,5);
       
        this.clientComboBoxPanel.add(clientCombo);
        
        this.lotComboBoxPanel.add(this.cilCombo.getLotCombo());
        
        this.itemDataComboBoxPanel.add(this.cilCombo.getItemDataCombo());

        fWithAmountOnly.setSelected(true);
        
        invalidate();
        validate();
        LiveHelp.getInstance().addFocusListener(this, this);
    }

    public void process() {

//        cilCombo.processLiveHelp();

        
        if( clientCombo.getSelectedItem() == null ) {
            NotifyDescriptor.Message nd = new NotifyDescriptor.Message(NbBundle.getMessage(CommonBundleResolver.class, "Please fixed all errors"), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        
        treeTablePanel.setNodes(
                clientCombo.getSelectedItem() != null ? clientCombo.getSelectedItem().getName() : null,
                cilCombo.getItemData() != null ? cilCombo.getItemData().getName() : null,
                cilCombo.getLot() != null ? cilCombo.getLot().getName() : null,
                articelRadioButton.isSelected(),
                fWithAmountOnly.isSelected());
    }

    public void focusLost(FocusEvent e) {
    }

    @Override
    protected void reloadButtonActionPerformedListener(ActionEvent evt) {
        process();
    }

    public void clear() {
        if (cilCombo != null) {
            clientCombo.clear();
        }

    }

    public void componentOpened() {
        clear();
        if (cilCombo == null) {
            try{
                initAutofiltering();
            }catch(Exception ex){
                ExceptionAnnotator.annotate(ex);
                return;
            }
        }
    }

    public void componentClosed() {
        clear();
    }

    public void componentActivated() {
    }

    public void componentDeactivated() {
    }

    public void componentHidden() {
    }

    public void componentShowing() {
    }

    public void focusGained(FocusEvent e) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createExcelReport() {
        // TODO
//            ReportService reportService = Lookup.getDefault().lookup(ReportService.class);
//            ReportService reportService = new ReportServiceBean();
//            JRException ex;

        String title = "export";
        Map pMap = new HashMap();
        List<Node> nodes = treeTablePanel.getNodes();
        Property[] props = InventoryNode.templateProperties();
        for (Property p : props) {
            pMap.put(p.getName(), p.getDisplayName());
        }
        try {
//            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(java.lang.System.getProperty("user.dir"));
            JSaveDialog chooser = new JSaveDialog(".csv", "*.csv", "export.csv");
            int returnValue = chooser.showSaveDialog(null);
            if ((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) {
                File f = chooser.getSelectedFile();
                FileOutputStream out = new FileOutputStream(f);
                log.info("going to write " + f.getName());

                out.write('"');
                out.write(NbBundle.getMessage(InventoryBundleResolver.class, "client").getBytes());
                out.write('"');
                out.write(';');
                
                for (Property p : props) {
                    out.write('"');
                    out.write(p.getDisplayName().getBytes());
                    out.write('"');
                    out.write(';');
                }
                out.write("\n".getBytes());

                for (Node n : nodes) {
                    InventoryNode inv = (InventoryNode) n;
                    String s;

                    out.write('"');
                    out.write(n.getDisplayName().getBytes());
                    out.write('"');
                    out.write(';');

                    Node.PropertySet ps = n.getPropertySets()[0];
                    for (Property p : ps.getProperties()) {
                        if (p == null){
                            continue;
                        }
                        out.write('"');
                        if( p.getValue()!=null ) {
                            out.write(p.getValue().toString().getBytes());
                        }
                        out.write('"');
                        out.write(';');
                    }
                    out.write("\n".getBytes());
                }

                out.close();
            }   
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
    }
}
