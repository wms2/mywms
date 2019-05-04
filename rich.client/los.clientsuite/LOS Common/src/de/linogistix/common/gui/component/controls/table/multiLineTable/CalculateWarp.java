/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.multiLineTable;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CalculateWarp extends Component {

    String s = new String();
    int height = -1;
    int offsetHeight = 6;

    public CalculateWarp() {
    }

    public void calculate(JTable table, String text, int kind) {
        s = "";
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        TableColumn column = table.getColumnModel().getColumn(kind);
        int width = column.getWidth();
        final int TRIM_PIXELS = 0;
        setSize(width, 10000);
        if (metrics == null) {
            height = metrics.getAscent() + offsetHeight;
            return;
        }
        int height = metrics.getHeight();
        int y = TRIM_PIXELS + metrics.getAscent(); // erste Grundlinie
        int x = TRIM_PIXELS;
        int max_x = getSize().width - (2 * TRIM_PIXELS);
        int max_y = getSize().height + metrics.getDescent() - TRIM_PIXELS;
        if (metrics.stringWidth(text) <= max_x) {
            // alles in einer zeile
            s = text;
            this.height = height + offsetHeight;
            return;
        }
        int word = 0;
        int space = 0;
        int word_width;
        String a_word;
        while (word != -1) {
            space = text.indexOf(' ', word);
            if (space == -1) { // an Ende
                a_word = text.substring(word);
            } else {
                a_word = text.substring(word, ++space);
            }
            word = space;
            word_width = metrics.stringWidth(a_word);
            if (x + word_width > max_x) { // Naechste Zeile
                if (y + height > max_y) {
                    break;
                }
                x = TRIM_PIXELS;
                y += height;
                s = s + "\n";
            }
            x += word_width;
            s = s + a_word;
        } //end while
        this.height = y + offsetHeight;
    }

    public String getString() {
        return s;
    }

    public int getHeight() {
        return height;
    }


    public String deleteReturns(String textStr) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(textStr.length());
        ByteArrayInputStream in = new ByteArrayInputStream(textStr.getBytes());
        int readbyte = 0;
        readbyte = in.read();
        while (readbyte != -1) {
            switch (readbyte) {
                case 10:
                    break;
                case 13:
                    break;
                default:
                    out.write(readbyte);
            }
            readbyte = in.read();
        }
        return out.toString();
    }
}
