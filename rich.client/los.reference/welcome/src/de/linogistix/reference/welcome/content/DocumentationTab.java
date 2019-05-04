package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.BundleSupport;
import de.linogistix.reference.welcome.ui.ContentSection;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 'Documentation & Community' tab of the Start Page.
 */
public class DocumentationTab extends AbstractTab {

    public DocumentationTab() {
        super(BundleSupport.getLabel( "DocumentationTab" )); 
    }

        @Override
    protected JComponent buildContent() {
        GridBagConstraints constr = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        // JPanel panel = new JPanel(new GridBagLayout(2,2));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder());
        
        constr.gridx=0;
        constr.gridy=0;
        constr.weightx=1;
        constr.weighty=0;
        constr.fill=GridBagConstraints.HORIZONTAL;
        constr.anchor=GridBagConstraints.NORTHWEST;
        constr.insets=new Insets(0,0,0,35);
        panel.add( new ContentSection( BundleSupport.getLabel("SectionCommunity"), new Community(), false ), constr); 
        constr.gridx=1;
        constr.gridy=0;
        constr.insets=new Insets(0,0,0,0);
        panel.add( new ContentSection( BundleSupport.getLabel("SectionDocumentation"), new Documentation(), false ), constr);       

        
        constr.gridx=0;
        constr.gridy=1;
        constr.insets=new Insets(15,0,0,0);
        constr.gridwidth=2;
       panel.add( new ContentSection( BundleSupport.getLabel("SectionEnterprise"), new Enterprise(), false ), constr); 


        return panel;
    }
    
    protected JComponent buildContentAlt() {
        JPanel panel = new JPanel(new GridLayout(1,0,35,15));
        // JPanel panel = new JPanel(new GridBagLayout(2,2));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add( new ContentSection( BundleSupport.getLabel("SectionDocumentation"), new Documentation(), false ));       

        panel.add( new ContentSection( BundleSupport.getLabel("SectionCommunity"), new Community(), false )); 

       
        JPanel panel2 = new JPanel(new GridLayout(0,1,35,15));
        panel2.setOpaque(false);
        panel2.setBorder(BorderFactory.createEmptyBorder());
        
        panel2.add(panel);
        
        panel2.add( new ContentSection( BundleSupport.getLabel("SectionEnterprise"), new Enterprise(), false )); 


        return panel2;
    }
}
