/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.querystockunits.gui.component;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOStockUnitMasterNode;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.inventory.report.queryinventory.gui.component.TreeTableViewPanel;
import de.linogistix.inventory.report.querystockunits.gui.gui_builder.AbstractStockUnitTreeTabelPanel;
import java.awt.BorderLayout;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class StockUnitTreeTablePanel extends AbstractStockUnitTreeTabelPanel implements TopComponentListener{

    private static final Logger log = Logger.getLogger(TreeTableViewPanel.class.getName());
    protected ExplorerManager manager = new ExplorerManager();
    TreeTableView v;

    public StockUnitTreeTablePanel() {
        super();
        v = new TreeTableView();
        //JScrollPane p = v;
        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(v);
        add(scroll, BorderLayout.CENTER);
        //Make shure that the table-header will be shown.
        setNodes(null, null, null);
    }

    @SuppressWarnings("unchecked")
    public void setNodes(final BODTO<ItemData> idat, final BODTO<Lot> lot, final BODTO<LOSStorageLocation> sl) {
        Children.Keys<Object> keys = new Children.Keys<Object>() {

            @Override
            protected Node[] createNodes(Object arg0) {
                if (arg0 instanceof StockUnitTO) {
                    try {
                        BOLookup bol = Lookup.getDefault().lookup(BOLookup.class);
                        BO bo = (BO) bol.lookup(StockUnit.class);
                        return new Node[]{new BOStockUnitMasterNode(((StockUnitTO) arg0), bo)};
                    } catch (IntrospectionException ex) {
                        ExceptionAnnotator.annotate(ex);
                        return new Node[0];
                    }
                } else {
                    return new Node[0];
                }
            }

            @Override
            protected void addNotify() {

                StockUnitTO[] tos = null;
                List<StockUnitTO> list;
                GetMode mode = GetMode.BY_ITEMDATA;

                if (idat != null) {
                    mode = GetMode.BY_ITEMDATA;
                } else if (lot != null) {
                    mode = GetMode.BY_LOT;
                } else if (sl != null) {
                    mode = GetMode.BY_StorageLocation;
                } else {
                    throw new NullPointerException();
                }

                try {
                    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                    StockUnitQueryRemote r = (StockUnitQueryRemote) loc.getStateless(StockUnitQueryRemote.class);

                    switch (mode) {

                        case BY_ITEMDATA:
                            list = r.queryByItemData(idat, new QueryDetail(Integer.MAX_VALUE, 0));
                            break;
                        case BY_LOT:
                            list = r.queryByLot(lot, new QueryDetail(Integer.MAX_VALUE, 0));
                            break;
                        case BY_StorageLocation:
                            list = r.queryByStorageLocation(sl, new QueryDetail(Integer.MAX_VALUE, 0));
                            break;
                        default:
                            throw new IllegalStateException();
                    }

                    //if clearing is selected
                    if (list.size() > 0) {
                        tos = list.toArray(new StockUnitTO[0]);
                        setKeys(tos);
                    } else {
                        setKeys(new StockUnitTO[]{});
                    }
                } catch (Throwable ex) {
                    ExceptionAnnotator.annotate(ex);
                }
            }
        };
        Property[] p = BOStockUnitMasterNode.boMasterNodePropertiesAll();
        Node root = new AbstractNode(keys);
        v.setProperties(p);
        v.setRootVisible(false);
        manager.setRootContext(root);
//            mgr.setSelectedNodes(new Node[]{root.getChildren().getNodes()[0]});
    }

    protected static class StockUnitProperty<T> extends PropertySupport.ReadOnly<T> {

        T value;

        @Override
        public PropertyEditor getPropertyEditor() {
            return new PlainObjectReadOnlyEditor();
        }

        StockUnitProperty(String name, Class<T> type, String displayName, T value) {
            super(name, type, displayName, "");
            this.value = value;
            setValue("ComparableColumnTTV", Boolean.TRUE);
        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }
    }

     enum GetMode {

        BY_ITEMDATA,
        BY_LOT,
        BY_StorageLocation
    }

     public void clear(){
         setNodes(null, null, null);
     }
    public void componentOpened() {
        clear();
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
}

