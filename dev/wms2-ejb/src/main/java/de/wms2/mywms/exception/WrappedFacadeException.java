/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.exception;

import org.mywms.facade.FacadeException;

/**
 * Wrapper for mywms FacadeException.
 * <p>
 * Used to propagate FacadeExceptions as BusinessException
 * 
 * @author krane
 *
 */
public class WrappedFacadeException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public WrappedFacadeException(FacadeException facadeException) {
		super(facadeException.getBundleResolver(),
				facadeException.getBundleResolver().getPackage().getName() + ".Bundle", facadeException.getKey(),
				facadeException.getParameters());
	}
}
