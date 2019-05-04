/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 *
 * @author arthur
 */
public class AutoFilteringComboBox extends JComboBox  {

    private javax.swing.Timer timer;
    private boolean hasFocus = false;
    private AutoFilteringComboBoxListener searchListener;
    private int minSearchChars = 1;
    private Object selectedItem = null;
    private boolean isSelectionValid = false;
    
    ActionListener dataRequester = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            if( searchListener != null ) {
                String text = getText();
                if( text.length()>=minSearchChars ) {
                    searchListener.requestProposalData(text);
                }
            }
        }
    };

    /** Creates a new instance of AutofilteringComboBox */
    public AutoFilteringComboBox() {
        this(500, 1);
    }

    public AutoFilteringComboBox( int delay, int minSearchChars ) {
        this.minSearchChars=minSearchChars;

        if( delay < 100 ) {
            delay = 100;
        }

        Border border = new EmptyBorder(0,2,0,0);
        if( getEditor().getEditorComponent() instanceof JTextField ) {
            border = ((JTextField)getEditor().getEditorComponent()).getBorder();
        }

        setEditor(new MyEditor(this));
        ((JTextField)getEditor().getEditorComponent()).setBorder(border);
        
        setEditable(true);
        timer = new Timer(delay, dataRequester);
        timer.setInitialDelay(delay);
        timer.setRepeats(false);

        setUI(new MetalComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton bt = new BasicArrowButton(BasicArrowButton.SOUTH) {
                    @Override
                    public int getWidth() {
                        return 0;
                    }
                };
                return bt;
            }
        });


        addPopupMenuListener( new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
            }
            public void popupMenuCanceled(PopupMenuEvent pme) {
                timer.stop();
            }
        });

        addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if( (ae.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0 ) {
                    selectedItem = getSelectedItem();
                    ((MyEditor) getEditor()).forwardChanges();
                    ((MyEditor) getEditor()).selectAll();
                }
            }
        });

    }

    public void addSearchListener( AutoFilteringComboBoxListener listener) {
        this.searchListener = listener;
    }


    private boolean isPopupNavigateKey(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_DOWN) ||
                (e.getKeyCode() == KeyEvent.VK_UP) ||
                (e.getKeyCode() == KeyEvent.VK_LEFT) ||
                (e.getKeyCode() == KeyEvent.VK_END) ||
                (e.getKeyCode() == KeyEvent.VK_BEGIN) ||
                (e.getKeyCode() == KeyEvent.VK_RIGHT)) {
            return true;
        }
        return false;
    }

    public String getText() {
        String ret = ((JTextField) getEditor().getEditorComponent()).getText();
        return ret != null ? ret : "";
    }

    public Object getObject() {
        return selectedItem;
    }

    public void setText(String text) {
        ((JTextField) getEditor().getEditorComponent()).setText(text);
    }

    public void setObject(Object item) {
        ((JTextField) getEditor().getEditorComponent()).setText(item.toString());
        selectedItem = item;
    }

    public boolean isEmpty() {
        return getText().trim().length()==0;
    }

    @Override
    public void removeAllItems() {
        isSelectionValid = false;
        super.removeAllItems();
    }

    public void setProposalList(List selectList) {
        isSelectionValid = false;
        if( ! hasFocus() ) {
            // Got Select list after leaving the field. Do not show the list.
            return;
        }

        setPopupVisible(false);
        removeAllItems();
        if( selectList != null ) {
            for( Object pos : selectList ) {
                super.addItem(pos);
            }
            setPopupVisible(selectList.size()>0);
        }
        isSelectionValid = true;
    }

    public int getColumns() {
        return ((JTextField) getEditor().getEditorComponent()).getColumns();
    }
    public void setColumns(int columns) {
        ((JTextField) getEditor().getEditorComponent()).setColumns(columns);
    }

    @Override
    public void requestFocus() {
        ((JTextField) getEditor().getEditorComponent()).requestFocus();
    }

    @Override
    public boolean hasFocus() {
        return hasFocus;
    }

    @Override
    public void transferFocus() {
        ((JTextField) getEditor().getEditorComponent()).transferFocus();
    }

    public void initStartValue() {
        ((MyEditor) getEditor()).initStartValue();
    }

    /**
     * Custom textfield editor which will be set to the combobox
     */
    public class MyEditor implements ComboBoxEditor {

        private JTextField text;
        private String startValue = null;

        public MyEditor( final AutoFilteringComboBox box) {
            text = new JTextField(10);
            
            text.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    selectedItem = null;
                    timer.stop();
                    
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if( isPopupVisible() && getSelectedItem() != null && isSelectionValid ) {
                            text.setText(getSelectedItem().toString());
                            selectedItem = getSelectedItem();
                        }
                        if( !isChanged() || searchListener == null ) {
                            text.transferFocus();
                            return;
                        }

                        if( searchListener.selectionChanged() ) {
                            text.transferFocus();
                            return;
                        }

                        String s = getText();
                        if( s.trim().length()==0 ) {
                            text.transferFocus();
                            return;
                        }

                        // Enter closes the popup. And newly requested proposals should be displayed
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                searchListener.requestProposalData(getText());
                            }
                        });
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_F2) {
                        text.selectAll();
                    }
                    else if( e.isActionKey() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown() || isPopupNavigateKey(e) ) {
                        return;
                    }
                    else {
                        isSelectionValid = false;
                        timer.restart();
                    }
                }
            });

            text.addFocusListener(new java.awt.event.FocusListener() {

                public void focusLost(FocusEvent e)  {
                    hasFocus = false;
                    forwardChanges();
                    text.select(1,1);
                }

                public void focusGained(FocusEvent e) {
                    hasFocus = true;
                    initStartValue();
                    text.selectAll();
                }

            });

            /**
             * if you pressed in the textfield so it make sure that the popup
             * will be closed.
             */
            text.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    if( e.getClickCount()!=1 ) {
                        return;
                    }

                    if( e.getButton()!=MouseEvent.BUTTON1 ) {
                        return;
                    }
                    //close the popup if user clicked in the edit field
                    if (isPopupVisible()) {
                        setPopupVisible(false);
                    }
                }
            });
        }

        public void initStartValue() {
            this.startValue = getText().trim();
        }

        public boolean isChanged() {
            String endValue = getText().trim();
            return !endValue.equals(startValue);
        }

        public void forwardChanges() {
            if( isChanged() && searchListener != null ) {
                searchListener.selectionChanged();
            }
            initStartValue();
        }

        public Component getEditorComponent() {
            return text;
        }

        /**
         * Here, you can handle the textfield edit entry, which will be shown to
         * the user
         * @param item
         */
        public void setItem(Object item) {
            if( !isSelectionValid ) {
                return;
            }

            //needed by mousepressed in the combobox popup
            String newText = (item == null) ? "" : item.toString();
            if( newText.length()==0 ) {
                return;
            }
            text.setText(newText);
        }

        public Object getItem() {
            return text.getText();
        }

        public void selectAll() {
            text.selectAll();
        }

        public void addActionListener(ActionListener l) {
            text.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            text.removeActionListener(l);
        }
    }

}
