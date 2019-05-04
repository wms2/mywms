package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.BundleSupport;
import de.linogistix.reference.welcome.ui.Utils;
import de.linogistix.reference.welcome.ui.WebLink;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/** 
 * The enterprise section of the start page. 
 */
public final class Enterprise extends AbstractContent {

    public Enterprise() {
        super( new GridBagLayout() );
        setOpaque(false);
        
        buildContent();
    }
    
    @Override
    protected void buildContent() {
        final float CONTENT_FONT_SIZE = 12.0F;

        //GridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady)
       
        
        String bundleKey = "EnterpriseText";
        JComponent txtContactText = createTextArea(bundleKey);
        WebLink myWmsOnlineCommunityLink = new WebLink(bundleKey, Utils.getLinkColor(), false);        
        bundleKey = "mailText";
//        JComponent txtMail = createTextArea(bundleKey);
        JLabel mailText = new JLabel(BundleSupport.getText(bundleKey));
        mailText.setFont(mailText.getFont().deriveFont(CONTENT_FONT_SIZE));

        this.add(txtContactText, new GridBagConstraints(0, row++, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ));

        this.add( mailText, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0 ) );

        this.add( myWmsOnlineCommunityLink, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0, 0 ) );

        this.add( new JLabel(), new GridBagConstraints(0, row++, 1, 1, 0.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,0), 0, 0 ) ); 
        
    }

}
