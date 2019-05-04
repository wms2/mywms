/*
 * BOQueryUpdateEventListener.java
 *
 * Created on 28. May 2012, 06:42
 *
 * Copyright (c) 2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

/**
 * A Listener to update events from BOQueryModel.
 *
 * @author andreas
 */
public interface BOQueryUpdateListener {

    public void onUpdateResults(BOQueryUpdateEvent evt);

}
