/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.linogistix.common.bobrowser.bo.detailview;

import de.linogistix.common.bobrowser.bo.BONode;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * Implements an Explorer View showing details of a node.
 * Used in master detail views.
 *
 * Code relies on {@link org.openide.explorer.view.PropertySheetView}
 *
 * @author andreas
 */
public abstract class AbstractDetailViewPanel extends JPanel {

    /** helper flag for avoiding multiple initialization of the GUI */
    transient private boolean guiInitialized = false;
    /** The Listener that tracks changes in explorerManager */
    transient private PropertyIL managerListener;
    /** manager to use */
    transient private ExplorerManager explorerManager;

    private BONode boNode;
    
    private Lookup lookup;

    public AbstractDetailViewPanel() {
        setLayout(new BorderLayout());
    }

    /** Initializes the GUI of the view */
    protected void initializeGUI() {
        guiInitialized = true;
        managerListener = new PropertyIL();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.explorerManager = ExplorerManager.find(this);
        if (!isGuiInitialized()) {
            initializeGUI();
        }

        // add propertyChange listeners to the explorerManager
        explorerManager.addPropertyChangeListener(managerListener);
        if (explorerManager.getSelectedNodes() == null || explorerManager.getSelectedNodes().length == 0) {
            setNode(null);
        } else {
            setNode(explorerManager.getSelectedNodes()[0]);
        }

    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (explorerManager != null) { //[PENDING] patch for bug in JDK1.3 Window
            explorerManager.removePropertyChangeListener(managerListener);
            explorerManager = null;
            setNode(null);
        }

    }

    /**
     * @return the explorerManager
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    /**
     * @return the guiInitialized
     */
    public boolean isGuiInitialized() {
        return guiInitialized;
    }

    public void setPanelVisible(boolean visible) {
    }

    public void onRefreshDetail() {
        
    }

    /**
     * @return the boNode
     */
    public BONode getBoNode() {
        return boNode;
    }

    /**
     * @param boNode the boNode to set
     */
    public void setBoNode(BONode boNode) {
        this.boNode = boNode;
    }

    /**
     * @return the lookup
     */
    public Lookup getLookup() {
        return lookup;
    }

    /**
     * @param lookup the lookup to set
     */
    public void setLookup(Lookup lookup) {
        this.lookup = lookup;
    }

    /**
     * The inner adaptor class for listening to the ExplorerManager's property and
     * vetoable changes.
     */
    class PropertyIL implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                if ((Node[]) evt.getNewValue() == null || ((Node[]) evt.getNewValue()).length == 0) {
                    setNode(null);
                } else {
                    setNode(((Node[]) evt.getNewValue())[0]);
                }
            }
        }
    }

    /**
     * Sets the actual node selected in the explorer manager
     */
    public abstract void setNode(Node node);

    /**
     * a name for this panel, e.g. used in tab. Should be i18n.
     * @return
     */
    public abstract String getPanelName();
}
