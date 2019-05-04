/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.bobrowser.bo.BOEntityNode;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOLOSAdviceMasterNode;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author trautm
 */
class GoodsReceiptNode extends AbstractNode {

    GoodsReceiptNodeChildren children;

    public GoodsReceiptNode(LOSGoodsReceipt r) throws IntrospectionException {
        super(new GoodsReceiptNodeChildren(r));
        children = (GoodsReceiptNodeChildren) getChildren();
    }

    void addAssignedAdvice(LOSAdviceTO a) {
        getAssignedAdvicesRoot().addAdvice(a);
    }

    PositionsRoot getPositionsRoot() {
        Node[] nodes = this.children.getNodes();
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException();
        }

        return (PositionsRoot) nodes[2];
    }

    void removeAssignedAdvice(LOSAdviceTO a) {
        getAssignedAdvicesRoot().removeAdvice(a);
    }

    AssignedAdvicesRoot getAssignedAdvicesRoot() {
        Node[] nodes = this.children.getNodes();
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException();
        }

        return (AssignedAdvicesRoot) nodes[1];
    }

    LOSGoodsReceipt getGoodsReceipt() {
        return this.children.r;
    }

    static class GoodsReceiptNodeChildren extends Children.Keys<LOSGoodsReceipt> {

        LOSGoodsReceipt r;

        GoodsReceiptNodeChildren(LOSGoodsReceipt r) {
            this.r = r;
        }

        @Override
        protected Node[] createNodes(LOSGoodsReceipt arg0) {
            try {
                BOEntityNode entityNode = new BOEntityNode(arg0);

                List<LOSAdviceTO> l = new ArrayList<LOSAdviceTO>();
                for (LOSAdvice adv : arg0.getAssignedAdvices()) {
                    LOSAdviceTO to = new LOSAdviceTO(adv);
                    l.add(to);
                }
                AssignedAdvicesRoot adviceRoot = new AssignedAdvicesRoot(l);

                List<LOSGoodsReceiptPosition> posList = arg0.getPositionList();
                Collections.sort(posList, new GoodsReceiptPositionComparator());
                PositionsRoot posRoot = new PositionsRoot(posList);

                return new Node[]{entityNode, adviceRoot, posRoot};

            } catch (IntrospectionException ex) {
                ExceptionAnnotator.annotate(ex);
                return new Node[0];
            }
        }

        @Override
        protected void addNotify() {
            try {
                List l = new ArrayList();
                l.add(this.r);
                setKeys(l);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    static class AssignedAdvicesRoot extends AbstractNode {

        AssignedAdvicesChildren children;

        public AssignedAdvicesRoot(List<LOSAdviceTO> advices) {
            super(new AssignedAdvicesChildren(advices));
            children = (AssignedAdvicesChildren) getChildren();
        }

        public void addAdvice(LOSAdviceTO adv) {
            if (children.advices.contains(adv)) {
                //
            } else {
                children.advices.add(adv);
                children.update();
            }
        }

        public void removeAdvice(LOSAdviceTO adv) {
            if (children.advices.contains(adv)) {
                children.advices.remove(adv);
                children.update();
            } else {
                //
            }
        }
    };

    static class AssignedAdvicesChildren extends Children.Keys<LOSAdviceTO> {

        List<LOSAdviceTO> advices;

        public AssignedAdvicesChildren(List<LOSAdviceTO> advices) {
            this.advices = advices;
        }

        @Override
        protected Node[] createNodes(LOSAdviceTO arg0) {
            try {
                BOLOSAdviceMasterNode m = new BOLOSAdviceMasterNode(arg0);
                return new Node[]{m};
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
                return new Node[0];
            }
        }

        @Override
        protected void addNotify() {
            try {
                setKeys(this.advices);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        void update() {
            addNotify();
        }
    }

    static class PositionsRoot extends AbstractNode {

        PositionsChildren children;

        public PositionsRoot(List<LOSGoodsReceiptPosition> positions) {
            super(new PositionsChildren(positions));
            children = (PositionsChildren) getChildren();
        }

        public void addPosition(LOSGoodsReceiptPosition pos) {
            if (children.positions.contains(pos)) {
                //
            } else {
                children.positions.add(pos);
                Collections.sort(children.positions, new GoodsReceiptPositionComparator());
                children.update();
            }
        }

        public void removePosition(LOSGoodsReceiptPosition pos) {
            if (children.positions.contains(pos)) {
                //
            } else {
                children.positions.remove(pos);
                children.update();
            }
        }
    }

    static class PositionsChildren extends Children.Keys<LOSGoodsReceiptPosition> {

        List<LOSGoodsReceiptPosition> positions;

        public PositionsChildren(List<LOSGoodsReceiptPosition> positions) {
            this.positions = positions;
            
        }

        @Override
        protected Node[] createNodes(LOSGoodsReceiptPosition pos) {

            GoodsReceiptPositiontNode m = new GoodsReceiptPositiontNode(pos, null);
            return new Node[]{m};

        }

        @Override
        protected void addNotify() {
            try {
                setKeys(this.positions);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        void update() {
            addNotify();
        }
    }
    
    private static class GoodsReceiptPositionComparator implements Comparator<LOSGoodsReceiptPosition> {

        public int compare(LOSGoodsReceiptPosition pos1, LOSGoodsReceiptPosition pos2) {

            if(pos1.getPositionNumber().length() < pos2.getPositionNumber().length()){
                return -1;
            }
            else if(pos1.getPositionNumber().length() > pos2.getPositionNumber().length()){
                return 1;
            }
            else {
                return pos1.getPositionNumber().compareTo(pos2.getPositionNumber());
            }

        }
    }
}

