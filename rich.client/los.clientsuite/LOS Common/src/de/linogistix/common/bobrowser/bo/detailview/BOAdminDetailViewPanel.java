/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.common.bobrowser.bo.detailview;

import de.linogistix.common.bobrowser.bo.editor.BOEditorButtons;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.BorderLayout;
import org.mywms.globals.Role;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author andreas
 */
public class BOAdminDetailViewPanel extends PropertyDetailViewPanel{

    private BOEditorButtons editButtons;

    public BOEditorButtons getEditButtons() {
        return editButtons;
    }

    public void addEditButtons() {

        editButtons = new BOEditorButtons(getBoNode(), getLookup(), getListLookup());
        this.propertySheetPanel.add(getEditButtons(), BorderLayout.SOUTH);
    }

    @Override
    protected void initializeGUI() {
        try {
            super.initializeGUI();
            LoginService login = Lookup.getDefault().lookup(LoginService.class);
            
            if (login.checkRolesAllowed(new String[]{Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR})) {
                addEditButtons();
            }
          
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
        
        
    }
    

    public void removeEditButtons() {
        if (getEditButtons() != null) {
            this.propertySheetPanel.remove(getEditButtons());
        }
        editButtons = null;
    }

    @Override
    public void setNode(Node node) {
        super.setNode(node);

    }


}
