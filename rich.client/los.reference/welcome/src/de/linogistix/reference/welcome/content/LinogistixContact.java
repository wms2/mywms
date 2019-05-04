package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.Utils;
import de.linogistix.reference.welcome.ui.WebLink;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/** 
 * The Contact section of the Start Page. 
 */
public final class LinogistixContact extends AbstractContent {    

    public LinogistixContact() {
        super( new GridBagLayout() );
        setOpaque(false);
        buildContent();
    }

    @Override
    protected void buildContent() {
        
        String contactTextBundleKey = "ContactText";
        JComponent txtContactText = createTextArea(contactTextBundleKey);
        this.add(txtContactText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ));
        
        String addressCompanyNameBundleKey = "AddressCompanyName";
        JComponent txtCompanyName = createTextArea(addressCompanyNameBundleKey);
        txtCompanyName.setFont(txtCompanyName.getFont().deriveFont( Font.BOLD ) );
        this.add(txtCompanyName, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0, 0 ));
              
        String addressBundleKey = "Address";
        JComponent txtAdress = createTextArea(addressBundleKey);
        this.add(txtAdress, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0, 0 ));
                        
        WebLink emailLink = new WebLink("AddressEmail", Utils.getLinkColor(), false);        
        this.add( emailLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0, 0 ) );
        
        WebLink addressWebLink = new WebLink("AddressWeb", Utils.getLinkColor(), false);        
        this.add( addressWebLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0, 0 ) );
                
        WebLink contactFormWebLink = new WebLink("LinogistixContactForm", Utils.getLinkColor(), false);        
        this.add( contactFormWebLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,0,0,0), 0, 0 ) );      
    }
 
}
