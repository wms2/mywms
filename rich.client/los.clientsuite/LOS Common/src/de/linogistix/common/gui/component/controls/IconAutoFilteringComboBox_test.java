/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.component.controls.*;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.GraphicUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import org.openide.util.Utilities;



class ComboBoxEditorExt implements ComboBoxEditor {

    ImagePanel panel;

    public ComboBoxEditorExt() {
        panel = new ImagePanel();
//    questionIcon = new ImageIcon("question.gif");
        panel.setIcon(GraphicUtil.getInstance().getIcon(IconType.HELP));
//    questionIcon = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(IconType.QUESTION)));
    //questionIcon = new ImageIcon("question.gif");
//        image = new ImageIcon(Utilities.loadImage(imagePath));
//        image = new ImageIcon(GraphicUtil.getInstance().scaleIcon(image, 16, 16));


    }

    public void setItem(Object anObject) {
        /*    if (anObject != null) {
        //set text to textfield my onchanged
        panel.setText(anObject.toString());
        BookEntry entry = (BookEntry) map.get(anObject.toString());
        //set icon on onchanged
        if (entry != null)
        panel.setIcon(entry.getImage());
        else
        panel.setIcon(questionIcon);
        }*/
    }

    public Component getEditorComponent() {
        return panel;
    }

    public Object getItem() {
        panel.entry = true;
        return panel.getText();
    }

    public void selectAll() {
        panel.selectAll();
    }

    public void addActionListener(ActionListener l) {
        panel.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        panel.removeActionListener(l);
    }

    //  We create our own inner class to handle setting and
    //  repainting the image and the text.
    class ImagePanel extends JPanel {

        JLabel imageIconLabel;
        JTextField textField;
        boolean entry = true;

        public ImagePanel() {
            setLayout(new BorderLayout());

            imageIconLabel = new JLabel();
            imageIconLabel.setBorder(new BevelBorder(BevelBorder.RAISED));

//      textField = new JTextField(initialEntry.getTitle());
            textField = new JTextField();

            textField.setColumns(45);
//      textField.setBorder(new BevelBorder(BevelBorder.LOWERED));
            textField.setBorder(new EmptyBorder(0, 4, 0, 4));
            add(imageIconLabel, BorderLayout.WEST);
            add(textField, BorderLayout.CENTER);
//      add(textField, BorderLayout.EAST);
        }

        public void setText(String s) {
//      if (entry)  {
            textField.setText(s);
//      }  
        }

        public String getText() {
            return (textField.getText());
        }

        public void setIcon(Icon i) {
            imageIconLabel.setIcon(i);
            repaint();
        }

        public void selectAll() {
            textField.selectAll();
        }

        public void addActionListener(ActionListener l) {
            textField.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            textField.removeActionListener(l);
        }
    }
}