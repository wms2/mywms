package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.dto.SequenceNumberTO;

import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 *  @author krane
 */
public class BOSequenceNumberMasterNode extends BOMasterNode {

    SequenceNumberTO to;

    /** Creates a new instance of BODeviceNode */
    public BOSequenceNumberMasterNode(BODTO d) throws IntrospectionException {
        super(d);
        to = (SequenceNumberTO) d;
    }

    /** Creates a new instance of BODeviceNode */
    public BOSequenceNumberMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (SequenceNumberTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();

            sheet.put( new BOMasterNodeProperty<String>("format", String.class, to.getFormat(), CommonBundleResolver.class) );
            sheet.put( new BOMasterNodeProperty<Long>("counter", Long.class, to.getCounter(), CommonBundleResolver.class) );
            sheet.put( new BOMasterNodeProperty<Long>("endCounter", Long.class, to.getEndCounter(), CommonBundleResolver.class) );

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> format = new BOMasterNodeProperty<String>("format", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<Long> lastNumber = new BOMasterNodeProperty<Long>("counter", Long.class, 0L, CommonBundleResolver.class);
        BOMasterNodeProperty<Long> endCounter = new BOMasterNodeProperty<Long>("endCounter", Long.class, 0L, CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            format, lastNumber, endCounter
        };

        return props;
    }
}
