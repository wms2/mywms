/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

/**
 *
 * @author krane
 */
public interface AutoFilteringComboBoxListener {
    public void requestProposalData( String searchString );
    public boolean selectionChanged();
}
