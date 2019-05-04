package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.BundleSupport;
import de.linogistix.reference.welcome.ui.ContentSection;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 'Contact & News' tab of the Start Page.
 */
public class ContactTab extends AbstractTab {
    
    public ContactTab() {
        super( BundleSupport.getLabel( "ContactTab" ) );
    }

    @Override
    protected JComponent buildContent() {
        JPanel panel = new JPanel( new GridLayout(1,0,35,15) );
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder());

        panel.add( new ContentSection( BundleSupport.getLabel( "SectionLinogistixContact" ), new LinogistixContact(), true ));
        
        panel.add( new ContentSection( BundleSupport.getLabel( "SectionLinogistixNews" ), new LinogistixNews(), true ) );

        return panel;
    }
}
