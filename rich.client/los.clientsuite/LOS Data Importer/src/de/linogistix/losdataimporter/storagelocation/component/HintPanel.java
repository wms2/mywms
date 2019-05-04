/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.losdataimporter.storagelocation.component;

import de.linogistix.losdataimporter.res.BundleResolver;
import de.linogistix.losdataimporter.storagelocation.gui_builder.AbstractHintPanel;
import java.awt.event.ActionEvent;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class HintPanel extends AbstractHintPanel {

    @Override
    public String getName() {
        return NbBundle.getMessage(BundleResolver.class, "HintPanel.name");
    }

    @Override
    public void onImportButtonActionPerformed(ActionEvent evt) {
       //
    }
    
    
    
}
