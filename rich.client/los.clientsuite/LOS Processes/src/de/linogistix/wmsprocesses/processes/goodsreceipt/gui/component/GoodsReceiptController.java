/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.bobrowser.bo.editor.BOCollectionEditorSelectionListener;
import de.linogistix.common.exception.VetoException;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.common.util.GraphicUtil;
import de.linogistix.los.common.businessservice.LOSPrintService;
import de.linogistix.los.inventory.crud.LOSGoodsReceiptCRUDRemote;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class GoodsReceiptController implements BOCollectionEditorSelectionListener{

    private static final Logger log = Logger.getLogger(GoodsReceiptController.class.getName());
    private State state = State.UNDEFINED;
    private TopComponentPanel topComponent;
    J2EEServiceLocator loc;
    LOSGoodsReceiptFacade goodsReceiptFacade;
    GoodsReceiptNode goodsReceiptNode;
    LOSGoodsReceiptQueryRemote goodsReceiptQuery;
    LOSGoodsReceiptCRUDRemote goodsReceiptCrud;
    LOSAdviceQueryRemote adviceQuery;
    LOSSystemPropertyServiceRemote propertyService;
    boolean readUnitLoad = false;
    int defaultLock = 0;

    GoodsReceiptController(TopComponentPanel p) {
        this.topComponent = p;
    }

    void addAssignedAdvice(LOSAdviceTO adv) {
                
        try {
            goodsReceiptFacade.assignAdvice(adv, goodsReceiptNode.getGoodsReceipt());
            reloadGoodsReceipt();
            updateAdviceView();
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
            
        }
    }

    void createPosition(
            BODTO<Client> client,
            BODTO<Lot> lot,
            BODTO<ItemData> item,
            String labelId,
            BODTO<UnitLoadType> ulType, 
            BigDecimal amount,
            BODTO<AdviceLine> adv,
            int lock,
            String info) throws FacadeException {
        
            
            goodsReceiptFacade.createGoodsReceiptLine(client, 
                    this.goodsReceiptNode.getGoodsReceipt(),
                    lot,
                    item,
                    labelId, 
                    ulType,
                    amount, adv, 
                    lock, 
                    info
                    );
            
            reloadGoodsReceipt();
            
        
            goodsReceiptFacade.acceptGoodsReceipt(this.goodsReceiptNode.getGoodsReceipt());
    }
    
    GoodsReceiptLine createPositionAndLot(
            BODTO<Client> client,
            String lotName,
            Date validFrom,
            Date validTo,
            boolean expireLot,
            BODTO<ItemData> item,
            String labelId,
            BODTO<UnitLoadType> ulType, 
            BigDecimal amount,
            BODTO<AdviceLine> adv,
            int lock,
            String info) throws FacadeException {
        
            GoodsReceiptLine grPos;
            grPos = goodsReceiptFacade.createGoodsReceiptLineAndLot(client, 
                                        this.goodsReceiptNode.getGoodsReceipt(),
                                        lotName,
                                        validFrom,
                                        validTo,
                                        item,
                                        labelId, 
                                        ulType,
                                        amount, adv, 
                                        lock, 
                                        info
                                        );
            
            reloadGoodsReceipt();
            
            return grPos;
        
    }

    void edit(BODTO<Client> client, String string, String string0, String deliverer, String externNumber, Date date, BODTO<StorageLocation> gate, String info, int orderType, String senderName) throws FacadeException {
        
        LOSGoodsReceiptCRUDRemote crud = loc.getStateless(LOSGoodsReceiptCRUDRemote.class);
        ClientQueryRemote clQuery = loc.getStateless(ClientQueryRemote.class);
        LOSStorageLocationQueryRemote slQuery = loc.getStateless(LOSStorageLocationQueryRemote.class);
        
        GoodsReceipt r = goodsReceiptNode.getGoodsReceipt();
        Client cl = clQuery.queryById(client.getId());
        
        
        r.setClient(cl);
        r.setDeliveryNoteNumber(externNumber);
        r.setCarrierName(deliverer);
        r.setStorageLocation(slQuery.queryById(gate.getId()));
        r.setAdditionalContent(info);
        r.setOrderType(orderType);
        r.setSenderName(senderName);
        
        crud.update(r);
        reloadGoodsReceipt();
    }
    
    void removeAssignedAdvice(LOSAdviceTO adv) {
        try {
            goodsReceiptFacade.removeAssigendAdvice(adv, goodsReceiptNode.getGoodsReceipt());
            this.goodsReceiptNode.removeAssignedAdvice(adv);
            updateAdviceView();
        } catch (BusinessException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    public void updateAdviceView() {
        if (goodsReceiptNode != null) {
            try {
                Node root = this.goodsReceiptNode.getAssignedAdvicesRoot();
                Node[] nodes = root.getChildren().getNodes();
                topComponent.centerPanel.getAdviceView().setRootVisible(false);
                topComponent.centerPanel.getAdviceManager().setRootContext(root);
                topComponent.centerPanel.getAdviceManager().setSelectedNodes(new Node[]{});
            } catch (PropertyVetoException ex) {
                ExceptionAnnotator.annotate(ex);
            }
        } else {
            topComponent.centerPanel.getAdviceView().setRootVisible(false);
            Node n = new AbstractNode(Children.LEAF);
            topComponent.centerPanel.getAdviceManager().setRootContext(n);
            try {
                topComponent.centerPanel.getAdviceManager().setSelectedNodes(new Node[]{});
            } catch (PropertyVetoException ex) {
                ExceptionAnnotator.annotate(ex);
            }
        }
    }

    void createGoodsReceipt(BODTO<Client> client, String licence, String driver, String deliverer, String externNumber, Date date, BODTO<StorageLocation> gate, String info, int orderType, String senderName) {
        if( goodsReceiptFacade == null ) {
            log.info("Facade not loaded");
            init();
        }
        
        GoodsReceipt r = goodsReceiptFacade.createGoodsReceipt(client, senderName, deliverer, externNumber, date, gate, info, orderType);
        
        try {
            if (r != null) {
                this.goodsReceiptNode = new GoodsReceiptNode(r);
                Node[] nodes = this.goodsReceiptNode.children.getNodes(); // init
                switchState(Event.CREATED);
            } else {
                this.goodsReceiptNode = null;
            }
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }

    }

    public void init() {
        try {
            loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

            if( goodsReceiptFacade==null ) {
                goodsReceiptFacade = loc.getStateless(LOSGoodsReceiptFacade.class);
            }
            if( goodsReceiptQuery==null ) {
                goodsReceiptQuery = loc.getStateless(LOSGoodsReceiptQueryRemote.class);
            }
            if( goodsReceiptCrud==null ) {
                goodsReceiptCrud = loc.getStateless(LOSGoodsReceiptCRUDRemote.class);
            }
            if( adviceQuery==null ) {
                adviceQuery = loc.getStateless(LOSAdviceQueryRemote.class);
            }
            if( propertyService==null ) {
                propertyService = loc.getStateless(LOSSystemPropertyServiceRemote.class);
            }

            switchState(Event.INITIALZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    AdviceLine getAdvice(Long id) throws BusinessObjectNotFoundException, BusinessObjectSecurityException {
        return adviceQuery.queryById(id);
    }

    List<BODTO<StorageLocation>> getGoodsReceiptLocations() {
        if (goodsReceiptFacade == null) {
            return new ArrayList<BODTO<StorageLocation>>();
        } else {
            try {
                return goodsReceiptFacade.getGoodsReceiptLocations();
            } catch (LOSLocationException ex) {
                ExceptionAnnotator.annotate(ex);
                return new ArrayList<BODTO<StorageLocation>>();
            }
        }
    }

    public void finishGoodsReceipt() {

        if (goodsReceiptNode == null || topComponent.centerPanel.wePosManager.getRootContext().getChildren().getNodesCount() == 0) {
            topComponent.centerPanel.getInfoLabel().setIcon(GraphicUtil.getInstance().getIcon(IconType.ERROR));
            //validate();
            return;
        }

        GoodsReceipt gr = null;
        
        try {
            goodsReceiptFacade.finishGoodsReceipt(gr);
            
        } catch (InventoryException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch(Exception e){
            ExceptionAnnotator.annotate(e);
        }
        

        reload();
        
    }

    public void printGoodsReceipt() {

        if (goodsReceiptNode == null || topComponent.centerPanel.wePosManager.getRootContext().getChildren().getNodesCount() == 0) {
            topComponent.centerPanel.getInfoLabel().setIcon(GraphicUtil.getInstance().getIcon(IconType.ERROR));
            //validate();
            return;
        }

        try {
            GoodsReceipt gr = this.goodsReceiptNode.getGoodsReceipt();
            if( gr == null ) {
                return;
            }

            loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            String ws = loc.getWorkstationName();
            LOSSystemPropertyServiceRemote sysProp = loc.getStateless(LOSSystemPropertyServiceRemote.class);
            String printerName = LOSPrintService.DEFAULT_PRINTER;
            try {
                printerName = sysProp.getStringDefault(gr.getClient(), ws, LOSInventoryPropertyKey.GOODS_RECEIPT_PRINTER, LOSPrintService.NO_PRINTER);
            }
            catch( Throwable t ) {}
            goodsReceiptFacade.createStockUnitLabel(gr, printerName);

        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }

        reload();
        
    }

    public void reload(){
        reloadGoodsReceipt();
        
        this.topComponent.centerPanel.checkState_AllButtons();
    }
    
//    public void reset() {
//
//        this.goodsReceiptNode = null;
//
//        try {
//            loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
//            goodsReceiptFacade = (LOSGoodsReceiptFacade) loc.getStateless(LOSGoodsReceiptFacade.class);
//            switchState(GoodsReceiptController.Event.INITIALZED);
//
//        } catch (J2EEServiceLocatorException ex) {
//            ExceptionAnnotator.annotate(ex);
//        }
//    }

    void removePosition(GoodsReceiptLine p) {
        try{
            goodsReceiptFacade.removeGoodsReceiptLine(p);
            
            reloadGoodsReceipt();
            
        } catch (Exception ex){
            ExceptionAnnotator.annotate(ex);
        }
    }

    private void clear() {
        this.goodsReceiptNode = null;
        topComponent.centerPanel.getGoodsReceiptComboBox().clear();
        topComponent.centerPanel.getClientTextfield().setText("");
        topComponent.centerPanel.getDelivererTextField().setText("");
        topComponent.centerPanel.getExternNumberTextField().setText("");
        topComponent.centerPanel.getInfoTextArea().setText("");
        topComponent.centerPanel.getStateTextField().setText("");
        topComponent.centerPanel.getDateTextField().setText("");

    }
    
    public void reloadGoodsReceipt() {
        if( goodsReceiptNode == null ) {
            return;
        }
        GoodsReceipt gr = goodsReceiptFacade.getGoodsReceipt(goodsReceiptNode.getGoodsReceipt());
        try{
            goodsReceiptNode = new GoodsReceiptNode(gr);
        } catch (Throwable t){
            goodsReceiptNode = null;
        }
        
        if (goodsReceiptNode != null &&goodsReceiptNode.getGoodsReceipt() != null){
            switchState(Event.SELECTED);
        } else{
            switchState(Event.INITIALZED);
        }
    }

    private void updatePositionView() {
        if (goodsReceiptNode != null) {
            try {
                Node root = this.goodsReceiptNode.getPositionsRoot();
                Node[] nodes = root.getChildren().getNodes();

                topComponent.centerPanel.getPositionView().setRootVisible(false);
                topComponent.centerPanel.getPositionManager().setRootContext(root);
                topComponent.centerPanel.getPositionManager().setSelectedNodes(new Node[]{root});
            } catch (PropertyVetoException ex) {
                ExceptionAnnotator.annotate(ex);
            }
        } else {
            topComponent.centerPanel.getPositionView().setRootVisible(false);
            AbstractNode n = new AbstractNode(Children.LEAF);
            topComponent.centerPanel.getPositionManager().setRootContext(n);
            try {
                topComponent.centerPanel.getPositionManager().setSelectedNodes(new Node[]{});
            } catch (PropertyVetoException ex) {
                ExceptionAnnotator.annotate(ex);
            }
        }
    }

    protected void onGoodsReceiptChanged(PropertyChangeEvent evt) {
        BODTO<GoodsReceipt> to;
        to = (BODTO<GoodsReceipt>) evt.getNewValue();
//        log.info("onGoodsReceiptChanged");
        try {
            GoodsReceipt goodsReceipt = null;
            if (to != null) {
                goodsReceipt = goodsReceiptQuery.queryById(to.getId());
            }
            if( goodsReceipt != null ) {
                this.goodsReceiptNode = new GoodsReceiptNode(goodsReceipt);
                Node[] nodes = this.goodsReceiptNode.children.getNodes(); // init
                defaultLock = (int)propertyService.getLongDefault(goodsReceipt.getClient(), loc.getWorkstationName(), LOSInventoryPropertyKey.GOODS_IN_DEFAULT_LOCK, 0);
            } else {
                this.goodsReceiptNode = null;
            }
            switchState(Event.SELECTED);
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }

    }

    //------------------------------------------------------------------------
    enum State {

        UNDEFINED,
        CREATION,
        SELECTED;
    }

    enum Event {

        INITIALZED,
        CREATED,
        SELECTED;
    }

    void switchState(Event e) {

        log.info("State: " + state + " >>> Event: " + e);
        State newState;

        switch (e) {
            case CREATED:
                switch (state) {
                    default:
                        newState = State.CREATION;
                        break;
                }
                break;
            case SELECTED:
                newState = State.SELECTED;
                break;
            default:
                newState = State.UNDEFINED;
                break;
        }

        if (newState == State.CREATION && state != State.CREATION) {
            switchCreated();
            newState = State.SELECTED;
        } else if (newState == State.SELECTED) {
            switchSelected();
        } else {
            switchUndefined();
        }

        state = newState;

    }

    private void switchCreated() {
        log.info("switchCreation");

        GoodsReceipt gr = this.goodsReceiptNode.getGoodsReceipt();
        
        BODTO<GoodsReceipt> to = new BODTO<GoodsReceipt>(gr.getId(), gr.getVersion(), gr.toUniqueString());
        topComponent.centerPanel.getGoodsReceiptComboBox().addItem(to);
        
        topComponent.centerPanel.getClientTextfield().setText(gr.getClient().toUniqueString());
        topComponent.centerPanel.getClientTextfield().setEditable(false);
        
        topComponent.centerPanel.getAdviceTabelPanel().setEnabled(true);

        topComponent.centerPanel.getLocationTextfield().setText(gr.getStorageLocation().toUniqueString());
        topComponent.centerPanel.getLocationTextfield().setEditable(false);

        topComponent.centerPanel.getInfoTextArea().setText(gr.getAdditionalContent());
        topComponent.centerPanel.getInfoTextArea().setEditable(false);

        topComponent.centerPanel.getDelivererTextField().setText(gr.getCarrierName());
        topComponent.centerPanel.getDelivererTextField().setEditable(false);

        topComponent.centerPanel.getExternNumberTextField().setText(gr.getDeliveryNoteNumber());
        topComponent.centerPanel.getExternNumberTextField().setEditable(false);

        topComponent.centerPanel.getStateTextField().setText(NbBundle.getMessage(
                CommonBundleResolver.class,"state." + gr.getState()));
        topComponent.centerPanel.getStateTextField().setEditable(false);

        topComponent.centerPanel.getDateTextField().setText(new SimpleDateFormat("dd.MM.yyyy").format(gr.getReceiptDate()));
        topComponent.centerPanel.getDateTextField().setEditable(false);
        
        updateAdviceView();
        updatePositionView();

    }

    private void switchUndefined() {
        log.info("switchUndefined");

        clear();

        topComponent.centerPanel.getClientTextfield().setEditable(false);
        
        topComponent.centerPanel.getAdviceTabelPanel().setEnabled(false);

        topComponent.centerPanel.getLocationTextfield().setEditable(false);
        topComponent.centerPanel.getInfoTextArea().setEditable(false);
        topComponent.centerPanel.getDelivererTextField().setEditable(false);
        topComponent.centerPanel.getExternNumberTextField().setEditable(false);
        topComponent.centerPanel.getStateTextField().setEditable(false);
        topComponent.centerPanel.getDateTextField().setEditable(false);
        
        updateAdviceView();
        updatePositionView();

    }

    private void switchSelected() {
        log.info("switchSelected");

        if (this.goodsReceiptNode == null){
            return;
        }
        
        GoodsReceipt gr = this.goodsReceiptNode.getGoodsReceipt();

        topComponent.centerPanel.getClientTextfield().setText(gr.getClient().toUniqueString());
        topComponent.centerPanel.getClientTextfield().setEditable(false); 
        
        topComponent.centerPanel.getAdviceTabelPanel().setEnabled(true);

        topComponent.centerPanel.getLocationTextfield().setText(gr.getStorageLocation().toUniqueString());
        topComponent.centerPanel.getLocationTextfield().setEditable(false);

        topComponent.centerPanel.getInfoTextArea().setText(gr.getAdditionalContent());
        topComponent.centerPanel.getInfoTextArea().setEditable(false);

        topComponent.centerPanel.getDelivererTextField().setText(gr.getCarrierName());
        topComponent.centerPanel.getDelivererTextField().setEditable(false);

        topComponent.centerPanel.getExternNumberTextField().setText(gr.getDeliveryNoteNumber());
        topComponent.centerPanel.getExternNumberTextField().setEditable(false);
        
        topComponent.centerPanel.getStateTextField().setText(NbBundle.getMessage(
                CommonBundleResolver.class,"state." + gr.getState()));
        topComponent.centerPanel.getStateTextField().setEditable(false);

        topComponent.centerPanel.getDateTextField().setText(gr.getReceiptDate()==null?"":new SimpleDateFormat("dd.MM.yyyy").format(gr.getReceiptDate()));
        topComponent.centerPanel.getDateTextField().setEditable(false);
        
        updateAdviceView();
        updatePositionView();

    }

    //--------------------------------------------------------------------------
    private void onNeedSave(PropertyChangeEvent evt) {
        topComponent.centerPanel.getInfoLabel().setIcon(GraphicUtil.getInstance().getIcon(IconType.WARNING));
        topComponent.centerPanel.getInfoLabel().setText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "GoodsreceiptCenterPanel.infoLabel.needSave"));

    }

    public void addSelection(BODTO selectedValue) throws VetoException {
        try {
            goodsReceiptFacade.assignAdvice(selectedValue, goodsReceiptNode.getGoodsReceipt());
            reloadGoodsReceipt();
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
            throw new VetoException();
        }
    }

    public void removeSelection(BODTO selectedValue) throws VetoException {
        try {
            goodsReceiptFacade.removeAssigendAdvice(selectedValue, goodsReceiptNode.getGoodsReceipt());
            this.goodsReceiptNode.removeAssignedAdvice((LOSAdviceTO) selectedValue);
        } catch (BusinessException ex) {
            ExceptionAnnotator.annotate(ex);
            throw new VetoException();
        }
    }
}
