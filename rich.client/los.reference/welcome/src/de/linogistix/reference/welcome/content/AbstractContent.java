package de.linogistix.reference.welcome.content;

import de.linogistix.reference.welcome.ui.BundleSupport;
import de.linogistix.reference.welcome.ui.Constants;
import de.linogistix.reference.welcome.ui.Utils;
import java.awt.Color;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Abstract class for creating content panels in the myWMS welcome page.
 */
public abstract class AbstractContent extends JPanel implements Constants {
    
    protected int row;

    public AbstractContent(LayoutManager layout) {
        super(layout);
    }

    /**
     * Add the creation of a concrete content panel to this method.
     */
    protected abstract void buildContent();

    /**
     * Create a non-editable JTextArea with word wrapping and content from the 
     * resource bundle property with the given name.
     * @param resourceName The property name in the resource bundle.
     * @return The JTextArea
     */
    protected JTextArea createTextArea(final String resourceName) {
        final Color BACKGROUND_COLOR = Utils.getBackgroundColor();
        this.setBackground(BACKGROUND_COLOR);
        final float CONTENT_FONT_SIZE = 12.0F;
        
        JTextArea txtTextAreaContent = new JTextArea(BundleSupport.getText(resourceName));
        txtTextAreaContent.setEditable(false);
        txtTextAreaContent.setLineWrap(true);
        txtTextAreaContent.setWrapStyleWord(true);
        txtTextAreaContent.setBackground(BACKGROUND_COLOR);
        txtTextAreaContent.setFont(txtTextAreaContent.getFont().deriveFont(CONTENT_FONT_SIZE));
        return txtTextAreaContent;
    }
    
}
