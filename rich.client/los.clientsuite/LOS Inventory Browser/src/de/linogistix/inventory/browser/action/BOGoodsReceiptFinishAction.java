/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.query.LOSGoodsReceiptQueryRemote;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author krane
 */
public final class BOGoodsReceiptFinishAction extends NodeAction {

    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),Role.INVENTORY_STR,Role.FOREMAN_STR
    };
   
    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "BOGoodsReceiptFinishAction.name");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Action.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean allowed =  login.checkRolesAllowed(allowedRoles);
        if( !allowed ) {
            return false;
        }
        return true;
    }

    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes == null) {
            return;
        }

        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(InventoryBundleResolver.class, "BOGoodsReceiptFinishAction.really"),
                NbBundle.getMessage(InventoryBundleResolver.class, "BOGoodsReceiptFinishAction.name"),
                NotifyDescriptor.OK_CANCEL_OPTION);

        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }
        
        BOMasterNode node = null;
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LOSGoodsReceiptFacade orderFacade = loc.getStateless(LOSGoodsReceiptFacade.class);

            for (Node n : activatedNodes) {
                if( n == null || !(n instanceof BOMasterNode)) {
                    continue;
                }
                node = (BOMasterNode)n;
                
                LOSGoodsReceiptQueryRemote goodsReceiptQuery = loc.getStateless(LOSGoodsReceiptQueryRemote.class);
                LOSGoodsReceipt goodsReceipt = goodsReceiptQuery.queryById(node.getEntity().getId());
                LOSGoodsReceiptFacade goodsReceiptFacade = loc.getStateless(LOSGoodsReceiptFacade.class);
                goodsReceiptFacade.finishGoodsReceipt(goodsReceipt);
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
        
        if( node != null ) {
            BO bo = node.getBo();
            bo.fireOutdatedEvent(node);
        }

    }
}

