/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.GraphicUtil;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author artur
 */
public final class UIPanel extends JPanel implements ActionListener {


        public UITextField textField;
        public JLabel label;
        public ActionListener listener;
        
        private CommonObject co;
        private SuggestionComboBox combo;
        
        public UIPanel(CommonObject co_, SuggestionComboBox combo_) {
            this.co = co_;
            this.combo = combo_;
            this.listener = this;
            label = new JLabel();
//            iconLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
            label.setIcon(GraphicUtil.getInstance().getIcon(IconType.HELP));
            textField = new UITextField(20,co);
            textField.setBorder(new EmptyBorder(0, 4, 0, 4));
            textField.addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(KeyEvent e) {
                    co.setKeyEvent(e);
                    
                    if (co.getKeyListener() != null) {
                        co.setAllowSetText(true);                     
                        combo.restartTimer();
                    }
                    //necessary for setting the text from popup in the editfield without waiting on the timer
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        combo.setKeyHandle(e);
                    }
                }
            });


            /**
             * if you pressed in the textfield so it make sure that the popup
             * will be closed.
             */
            textField.addMouseListener(new java.awt.event.MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    //close the popup if user clicked in the edit field
                    if (combo.isPopupVisible()) {
                        combo.setPopupVisible(false);
                    }
                }
            });
            setLayout(new BorderLayout());

            label.addMouseListener(new java.awt.event.MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    UIPopup popup = new UIPopup(listener);
                    popup.show(label, e.getX(), e.getY());
                    //close the popup if user clicked in the edit field
                    System.out.println("icon pressed");
//                    setPopupVisible(false);
//                    hidePopup();
                }
            });
            add(label, BorderLayout.WEST);
            add(textField, BorderLayout.CENTER);
        }

        public void actionPerformed(ActionEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource();
            System.out.println("Command = " + e.getActionCommand());
        }


}
