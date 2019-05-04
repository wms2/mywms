/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.object;

import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;
import de.linogistix.common.util.BundleResolve;
import java.awt.Image;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author artur
 */
public class OrderItemNode extends AbstractNode {

    private OrderItem item;
    private Sheet.Set sheet = null;
    private static final String POSITION_COLUMN = "Position";
    private static final String ARTICEL_COLUMN = "Article";    
    private static final String LOT_COLUMN = "Lot";    
    private static final String AMOUNT_COLUMN = "Amount";    
    
    @Override
    public Image getIcon(int arg0) {
//        ImageIcon iconImage = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(IconType.INFORMATION)));
//        Image image = iconImage.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);        
//        return image;//testImage.getImage();
        return null;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
//            LogProperty p = new LogProperty<String>("host", String.class, resolve("host"), "", item.getHost());
//            p.setValue("suppressCustomEditor", Boolean.TRUE); //NOI18N            
//            sheet.put(p);
            //Man muss Sheet nehmen da der Konstuktor von PropertySet selber abstract ist.
            sheet.put(new LogProperty<String>(POSITION_COLUMN, String.class, resolve(POSITION_COLUMN), "", item.getPosition()));
            sheet.put(new LogProperty<String>(ARTICEL_COLUMN, String.class, resolve(ARTICEL_COLUMN), "", item.getArticel()));
            sheet.put(new LogProperty<String>(LOT_COLUMN, String.class, resolve(LOT_COLUMN), "", item.getPrintnorm()));
//            sheet.put(new LogProperty<String>("message", String.class, resolve("message"), "", item.getMessageResourceKey() != null ? resolve(item.getMessageResourceKey()) : item.getMessage()));
            sheet.put(new LogProperty<String>(AMOUNT_COLUMN, String.class, resolve(AMOUNT_COLUMN), "", item.getAmount()));
//            resolveMyWMS(item);
        }
        return new PropertySet[]{sheet};
    }

    public static String resolve(String key) {
        return BundleResolve.resolve(new Class[]{de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver.class
            
        }, key, null);
    }

    /**
     * 
     * @return
     */
    public static Property[] templateProperties() {

        Property[] properties = new Property[]{
            new LogProperty<String>(POSITION_COLUMN, String.class, resolve(POSITION_COLUMN), POSITION_COLUMN, null),
            new LogProperty<String>(LOT_COLUMN, String.class, resolve(LOT_COLUMN), LOT_COLUMN, null),
            new LogProperty<String>(ARTICEL_COLUMN, String.class, resolve(ARTICEL_COLUMN), ARTICEL_COLUMN, ""),
            new LogProperty<String>(AMOUNT_COLUMN, String.class, resolve(AMOUNT_COLUMN), AMOUNT_COLUMN, null)
        };
        return properties;
    }

    public OrderItemNode(OrderItem item) {
        super(Children.LEAF);
        if (item == null) {
            throw new NullPointerException("Item null");
        }
        this.item = item;
//        setName(item.articel.toString() + " " + item.toString());
//        setDisplayName(item.articel);
//        "de/linogistix/bobrowser/res/icon/Document.png"
//        setIconBaseWithExtension("de/linogistix/common/res/icon/Exception.png");
//        setIconBaseWithExtension("de/linogistix/common/res/icon/Exception.png");

    }
    
    public OrderItem getItem() {
        return item;
    }

    public Property[] getProperties() {
        return getPropertySets()[0].getProperties();
    }

    private static class LogProperty<T> extends PropertySupport.ReadOnly<T> {

        @Override
        public PropertyEditor getPropertyEditor() {
            return new PlainObjectReadOnlyEditor();
        }
        T value;

        public LogProperty(String name, Class<T> type, String displayName, String shortDescription, T value) {
            super(name, type, displayName, shortDescription);
            this.value = value;
        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }
    }
}
