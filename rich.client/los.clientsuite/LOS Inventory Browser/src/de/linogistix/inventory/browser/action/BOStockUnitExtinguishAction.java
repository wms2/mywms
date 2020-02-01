package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.LOSExtinguishFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * @author krane
 *
 */
public final class BOStockUnitExtinguishAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOStockUnitExtinguishAction.class.getName());
    private static String[] allowedRoles = new String[]{
        Role.ADMIN.toString(),Role.INVENTORY_STR,Role.FOREMAN_STR
    };

    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "extinguishStockUnit");
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
        return login.checkRolesAllowed(allowedRoles);
    }

    @SuppressWarnings("unchecked")
    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }

        try {
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.ReallyExtinguishStockUnit"),
                    NbBundle.getMessage(InventoryBundleResolver.class, "extinguishStockUnit"),
                    NotifyDescriptor.OK_CANCEL_OPTION);

            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                List<Long> ids = new ArrayList<Long>();
                for (Node n : activatedNodes) {
                    if (n == null) {
                        continue;
                    }
                    if (!(n instanceof BOMasterNode)) {
                        log.warning("Not a BOMasterNodeType: " + n.toString());
                    }
                    ids.add(((BOMasterNode)n).getId());
                }
                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                LOSExtinguishFacade extinguishFacade = loc.getStateless(LOSExtinguishFacade.class);
                extinguishFacade.generateStockUnitOrder(ids);
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

