/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.object;

/**
 *
 * @author artur
 */
public class OrderItem {
    String articel = "";
    String position = "";
    String printnorm = "";
    String amount = "";

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getArticel() {
        return articel;
    }

    public void setArticel(String articel) {
        this.articel = articel;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPrintnorm() {
        return printnorm;
    }

    public void setPrintnorm(String printnorm) {
        this.printnorm = printnorm;
    }
    
    
    
}
