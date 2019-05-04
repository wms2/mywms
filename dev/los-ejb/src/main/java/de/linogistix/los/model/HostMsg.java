package de.linogistix.los.model;

import java.beans.BeanInfo;
import java.beans.Introspector;

public abstract class HostMsg {

	private String dummy = "XXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    public String toString() {
        StringBuffer b = new StringBuffer();
        boolean append=false;
        try {
            BeanInfo info = Introspector.getBeanInfo(getClass());
            java.beans.PropertyDescriptor[] d = info.getPropertyDescriptors();

            b.append(getClass().getSimpleName());
            b.append(": ");

            for (int i = 0; i < d.length; i++) {
                try {
                	
                	if (d[i].getName().equals("class")){
                		continue;
                	}
                	if( append ) {
                		b.append(", ");
                	}
                	append = true;
                    b.append(d[i].getName());
                    b.append("=");
                    try {
                        b.append(d[i].getReadMethod().invoke(this, new Object[0]).toString());
                    }
                    catch (Throwable t) {
                        b.append("?");
                    }
                }
                catch (Throwable ex) {
                    continue;
                }
            }
            return new String(b);
        }
        catch (Throwable t) {
            return super.toString();
        }
    }

}
