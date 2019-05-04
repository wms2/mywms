package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.Utils;
import de.linogistix.reference.welcome.ui.WebLink;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/** 
 * The Documentation section of the Start Page.
 */
public final class Documentation extends AbstractContent {

    public Documentation() {
        super( new GridBagLayout() );
        setOpaque(false);
        buildContent();
    }
    
    @Override
    protected void buildContent() {

        // myWMS online tutorial
        String myWmsOnlineTutorialBundleKey = "MyWmsOnlineTutorial";
        JComponent txtOnlineTutorialText = createTextArea(myWmsOnlineTutorialBundleKey);
        this.add(txtOnlineTutorialText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ));
        
        WebLink myWmsTutorialLink = new WebLink(myWmsOnlineTutorialBundleKey, Utils.getLinkColor(), false);        
        this.add( myWmsTutorialLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ) );

        // myWMS online documentation
        String myWmsOnlineDocsBundleKey = "MyWmsOnlineDocs";
        JComponent txtOnlineDocsText = createTextArea(myWmsOnlineDocsBundleKey);
        this.add(txtOnlineDocsText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,0,0,0), 0, 0 ));
        
        WebLink myWmsDocsLink = new WebLink(myWmsOnlineDocsBundleKey, Utils.getLinkColor(), false);
        // myWmsDocsLink.setFont( myWmsDocsLink.getFont().deriveFont( Font.BOLD ) );
        this.add( myWmsDocsLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ) );

        
        this.add( new JLabel(), new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,0), 0, 0 ) );                
    }
    
}
