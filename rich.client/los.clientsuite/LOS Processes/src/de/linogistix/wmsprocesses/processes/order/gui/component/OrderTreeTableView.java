/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.component;

import de.linogistix.wmsprocesses.processes.order.gui.object.OrderItem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import de.linogistix.logviewer.processes.clearing.gui.gui_builder.AbstractClearingDialog;
//import de.linogistix.logviewer.processes.log.gui.object.ClearingItemNode;
//import de.linogistix.logviewer.processes.log.gui.object.LogItemNode;
import de.linogistix.wmsprocesses.processes.order.gui.object.OrderItemNode;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author artur
 */
public class OrderTreeTableView extends JPanel implements ExplorerManager.Provider {

    private final ExplorerManager mgr = new ExplorerManager();
    Node rootNode;
    BrowserTreeTableView orderTreeView;
    List<OrderItem> orderItem;
    
    public OrderTreeTableView() {
        setLayout(new java.awt.BorderLayout());
        orderTreeView = new BrowserTreeTableView();       
        add(orderTreeView, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    public void setNodes(final List<OrderItem> orderItem) {
        this.orderItem = orderItem;
        Children.Keys<Object> keys = new Children.Keys<Object>() {

            @Override
            protected Node[] createNodes(Object arg0) {
                    return new Node[]{new OrderItemNode((OrderItem) arg0)};
            }

            @Override
            protected void addNotify() {
                try {
                    setKeys(orderItem);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }        
            }
            };
            
        Node root = new AbstractNode(keys);
        orderTreeView.setProperties(OrderItemNode.templateProperties());
        orderTreeView.setRootVisible(false);
        mgr.setRootContext(root);
//            mgr.setSelectedNodes(new Node[]{root.getChildren().getNodes()[0]});
    }

    @SuppressWarnings("unchecked")

    public boolean hasTableFocus() {
        return orderTreeView.getTable().hasFocus();
    }

/*    public void addMouseListener() {
        logTreeView.getTable().addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
                    PropertySheetView d;
                    Node[] nodes = mgr.getSelectedNodes();
                    for (Node n : nodes) {
                        if (n instanceof LogItemNode) {

                        }
                        if (n instanceof ClearingItemNode) {
                            ClearingItemNode clearingNode = (ClearingItemNode) n;
                            ClearingItem item = clearingNode.getItem();
                            ClearingDialog dialog = new ClearingDialog(item);
                            dialog.showDialog();
                            if (dialog.dialogDescriptor.getValue() instanceof AbstractClearingDialog.CustomButton) {
                                ClearingDialog.CustomButton button = (ClearingDialog.CustomButton) dialog.dialogDescriptor.getValue();
                                if (button.getActionCommand() == ClearingDialog.OK_BUTTON) {

                                }
                            }
                        }
                    }
                }
            }
        });
    }*/
    
    private boolean isSelected(OrderItem searchItem) {
         Node[] nodes = mgr.getSelectedNodes();
         for (Node node : nodes) {
             if (node instanceof OrderItemNode) {
                 OrderItem item = ((OrderItemNode)node).getItem();
                    if (item == searchItem) {
                        return true;
                    }    
             }
         }
        return false;
    }

    public boolean contains(String itemData, String lot){
        
        for(OrderItem oi:orderItem){
            
            if(oi.getArticel().equals(itemData)
               && oi.getPrintnorm().equals(lot))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public void addRow(String position, String printnorm, String articel, String amount) {
        OrderItem item = new OrderItem();
        item.setPosition(position);
        item.setPrintnorm(printnorm);
        item.setArticel(articel);
        item.setAmount(amount);
        orderItem.add(item);
        setNodes(orderItem);        
    }
    
    public void delSelectedRows() {
        List<OrderItem> l = new ArrayList<OrderItem>();    
        for (OrderItem item : orderItem) {
            if (isSelected(item) == false) {
                     l.add(item);
            }         
        }
         setNodes(l);
    }
    
    public void clear(){
        List<OrderItem> l = new ArrayList<OrderItem>();    
         
        setNodes(l);
    }

    public List<OrderItem> getOrderItems(){
        return orderItem;
    }
    
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    private class BrowserTreeTableView extends TreeTableView {

/*        BrowserTreeTableView() {
        }*/

        JTree getTree() {
            return tree;
        }

        JTable getTable() {
            return treeTable;
        }
    }
}
