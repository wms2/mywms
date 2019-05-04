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
 * The News section of the Start Page. 
 */   
public final class LinogistixNews extends AbstractContent {    
         
    public LinogistixNews() {
        super( new GridBagLayout() );
        setOpaque(false);
        buildContent();
    }

    @Override
    protected void buildContent() {
        
        // LinogistiX News
        String linogistixNewsBundleKey = "LinogistixNews";
        JComponent txtNewsText = createTextArea(linogistixNewsBundleKey);       
        this.add(txtNewsText, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ));                                
        
        WebLink newsLink = new WebLink(linogistixNewsBundleKey, Utils.getLinkColor(), false);        
        this.add(newsLink, new GridBagConstraints(0, row++, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0 ) );    
        
        
        this.add( new JLabel(), new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,0,0,0), 0, 0 ) );  
    }
 
}
