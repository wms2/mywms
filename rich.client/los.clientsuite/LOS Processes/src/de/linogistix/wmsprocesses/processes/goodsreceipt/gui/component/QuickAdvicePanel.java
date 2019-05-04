/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.ItemDataComboBoxModel;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractQuickAdvicePanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.ParseException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.openide.util.NbBundle;

/**
 *
 * @author Trautmann
 */
public class QuickAdvicePanel extends AbstractQuickAdvicePanel{

    public QuickAdvicePanel() {
        super();
        try{
            this.clientCombo.setBoClass(Client.class);
            this.clientCombo.initAutofiltering();
            this.clientCombo.setEnabled(false);
            this.clientCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "Client"));

            this.itemDataCombo.setBoClass(Client.class);
            this.itemDataCombo.setComboBoxModel(new ItemDataComboBoxModel());
            this.itemDataCombo.initAutofiltering();
            this.itemDataCombo.setEnabled(true);
            this.itemDataCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "ItemData"));
            this.itemDataCombo.setMandatory(true);

             this.itemDataCombo.addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {

                    ItemData selItemData = (ItemData) itemDataCombo.getSelectedAsEntity();

                    if(selItemData != null){
    //                    getAmountTextField().setEnabled(true);
                        amountTextField.setUnitName(selItemData.getHandlingUnit().getUnitName());
                        amountTextField.setScale(selItemData.getScale());
                    }
    //                else {
    //                    getAmountTextField().setEnabled(false);
    //                }

                }
            });

            this.itemDataCombo.requestFocusInWindow();
        } catch (Throwable t){
            ExceptionAnnotator.annotate(t);
        }

    }

    void setClient(Client client) {
        this.clientCombo.addItem(client.getNumber());
        ((ItemDataComboBoxModel)(this.itemDataCombo.getComboBoxModel())).setClientTO(new BODTO<Client>(client));

    }

    public String getClient() {
        return this.clientCombo.getSelectedAsText();
    }

    public String getItemData() {
        return this.itemDataCombo.getSelectedAsText();
    }

    public BigDecimal getAmount() {
        try {
            return this.amountTextField.getValue();
        } catch (ParseException ex) {
            ExceptionAnnotator.annotate(ex);
            return BigDecimal.ZERO;
        }
    }

    String getComment() {
        return this.descriptionArea.getText();
    }



}
