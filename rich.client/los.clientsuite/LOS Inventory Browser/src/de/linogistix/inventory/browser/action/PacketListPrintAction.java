/* 
Copyright 2019 Matthias Krane
info@krane.engineer

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

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.dialog.CustomerOrderPrintDialog;
import de.linogistix.inventory.browser.dialog.UnitLoadPrintDialog;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.facade.LOSPickingFacade;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.inventory.query.dto.LOSPickingUnitLoadTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.globals.Role;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author krane
 */
public final class PacketListPrintAction extends NodeAction {

    private static final Logger log = Logger.getLogger(PacketListPrintAction.class.getName());
    J2EEServiceLocator loc = null;
    LOSPickingOrderQueryRemote orderQuery = null;
    LOSPickingFacade pickingFacade = null;


   private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),Role.INVENTORY_STR,Role.FOREMAN_STR,Role.OPERATOR_STR
   };

    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "PacketPrintDialog.action");
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

        UnitLoadPrintDialog dialog = null;
        BODTO to = null;
        BOMasterNode node = null;
        try {
            List<BODTO> orders = new ArrayList<BODTO>();
            for (Node n : activatedNodes) {
                if( n == null || !(n instanceof BOMasterNode)) {
                    continue;
                }

                node = (BOMasterNode)n;

                BODTO bodto = ((BOMasterNode)n).getEntity();
                orders.add(bodto);
            }

            dialog = new UnitLoadPrintDialog(orders);
            dialog.setVisible(true);

        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }

    }
}

