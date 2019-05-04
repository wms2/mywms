package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.Utils;
import de.linogistix.reference.welcome.ui.WebLink;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/** 
 * The Community section of the Start Page. 
 */
public final class Community extends AbstractContent {

    public Community() {
        super( new GridBagLayout() );
        setOpaque(false);
        buildContent();
    }
    
    @Override
    protected void buildContent() {

        // myWMS Community
        String myWmsOnlineCommunityBundleKey = "MyWmsOnlineCommunity";
        JComponent txtCommunityText = createTextArea(myWmsOnlineCommunityBundleKey);
        this.add(txtCommunityText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ));
        
        WebLink myWmsOnlineCommunityLink = new WebLink(myWmsOnlineCommunityBundleKey, Utils.getLinkColor(), false);        
        this.add( myWmsOnlineCommunityLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ) );

        // myWMS ticket system
        String myWmsTicketSystemBundleKey = "MyWmsTicketSystem";
        JComponent txtTicketSystemText = createTextArea(myWmsTicketSystemBundleKey);
        this.add(txtTicketSystemText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0 ));
        
        WebLink myWmsTicketSystemLink = new WebLink(myWmsTicketSystemBundleKey, Utils.getLinkColor(), false);        
        this.add(myWmsTicketSystemLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ) );
        
        // myWMS Wiki
        String myWmsWikiBundleKey = "MyWmsWiki";
        JComponent txtWikiText = createTextArea(myWmsWikiBundleKey);
        this.add(txtWikiText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0 ));
        
        WebLink myWmsWikiLink = new WebLink(myWmsWikiBundleKey, Utils.getLinkColor(), false);        
        this.add( myWmsWikiLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ) );
        
        
        this.add( new JLabel(), new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,0), 0, 0 ) ); 
        
    }

}
