/* 
Copyright 2019 Matthias Krane
info@krane.engineer

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
package de.wms2.mywms.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.util.NumberUtils;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * @author krane
 *
 */
@Stateless
public class LocationReserver {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private TypeCapacityEntityService capaService;
	@Inject
	private LocationReservationEntityService reservationService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private StorageLocationEntityService locationService;

	/**
	 * Checks whether a transfer of unit load to the given location is allowed.
	 */
	public boolean checkAllocateLocation(StorageLocation location, UnitLoad unitLoad, boolean checkLock,
			boolean throwException) throws BusinessException {

		return checkAllocateLocation(location, unitLoad.getClient(), unitLoad.getUnitLoadType(), checkLock, throwException);
	}

	/**
	 * Checks whether a transfer of unit load to the given location is allowed.
	 */
	public boolean checkAllocateLocation(StorageLocation location, Client client, UnitLoadType unitLoadType,
			boolean checkLock, boolean throwException) throws BusinessException {
		String logStr = "checkAllocateLocation ";
		if (location == null) {
			logger.log(Level.SEVERE, logStr + "No loaction");
			throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingParameterLocation");
		}

		if (client == null) {
			logger.log(Level.SEVERE, logStr + "No client");
			throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingParameterClient");
		}
		if (unitLoadType == null) {
			logger.log(Level.SEVERE, logStr + "No unitLoadType");
			throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingParameterUnitLoadType");
		}

		if (!location.getClient().isSystemClient() && !location.getClient().equals(client)) {
			logger.log(Level.WARNING, logStr + "The client of unitload does not match. location=" + location
					+ ", locations client=" + location.getClient() + ", unitloads client=" + client);
			if (throwException) {
				throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.wrongClient");
			}
			return false;
		}

		// There must be no inbound locks
		if (checkLock && location.isLocked()) {
			if (throwException) {
				throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.locationIsLocked");
			}
			return false;
		}

		BigDecimal locationAllocation = location.getAllocation();
		if (locationAllocation.compareTo(NumberUtils.HUNDRED) >= 0) {
			logger.log(Level.FINE, logStr + "Not enough space on location. location=" + location
					+ ", allocation(before)=" + locationAllocation);
			if (throwException) {
				throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.notEnoughSpace");
			}
			return false;
		}

		TypeCapacityConstraint unitLoadCapa = capaService.read(location.getLocationType(), unitLoadType);
		if (unitLoadCapa == null) {
			if (!capaService.exists(location.getLocationType(), null)) {
				logger.log(Level.FINE, logStr + "No allocation defined for locationType at all. OK. locationType="
						+ location.getLocationType() + " unitLoadType=" + unitLoadType);
				return true;
			}
			logger.log(Level.FINE, logStr + "No allocation defined for location and unitload. locationType="
					+ location.getLocationType() + " unitLoadType=" + unitLoadType);
			if (throwException) {
				throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingTypeCapacity");
			}
			return false;
		}

		BigDecimal unitLoadAllocation = unitLoadCapa.getAllocation();
		if (unitLoadAllocation.compareTo(NumberUtils.HUNDRED) > 0) {
			// Check neighbors
			List<StorageLocation> neighbors = readNeighbors(location, false);
			BigDecimal remainingAllocation = unitLoadAllocation;
			for (StorageLocation neighbor : neighbors) {
				if (remainingAllocation.compareTo(BigDecimal.ZERO) <= 0) {
					// done
					break;
				}
				remainingAllocation = remainingAllocation.subtract(NumberUtils.HUNDRED);

				// There must be no inbound locks
				if (checkLock && neighbor.isLocked()) {
					if (throwException) {
						logger.log(Level.INFO, logStr + "Neighbor is locked. location=" + location + ", neighbor="
								+ neighbor + ", lock=" + neighbor.getLock());
						throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.neighborIsLocked");
					}
					return false;
				}

				if (neighbor.getAllocation().compareTo(BigDecimal.ZERO) > 0) {
					if (throwException) {
						logger.log(Level.INFO,
								logStr + "Neighbor is allocated. location=" + location + ", neighbor=" + neighbor);
						throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.neighborIsAllocated");
					}
					return false;
				}
			}

			if (remainingAllocation.compareTo(BigDecimal.ZERO) > 0) {
				if (throwException) {
					logger.log(Level.INFO, logStr + "Not enough neighbors. location=" + location
							+ ", remainingAllocation=" + remainingAllocation);
					throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.notEnoughNeighbors");
				}
				return false;
			}

			logger.log(Level.FINE, logStr + "Allocation > 100 defined for location. OK. location=" + location
					+ ", capaUnitLoad=" + unitLoadCapa);
			return true;
		}

		locationAllocation = locationAllocation.add(unitLoadAllocation);
		if (locationAllocation.compareTo(NumberUtils.HUNDRED) > 0) {
			logger.log(Level.FINE, logStr + "Not enough space left on location. location=" + location
					+ ", locationType=" + location.getLocationType() + " unitLoadType=" + unitLoadType);
			if (throwException) {
				throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.notEnoughSpace");
			}
			return false;
		}

		return true;
	}

	/**
	 * Sets a reservation and allocation for the location. The reservation is
	 * written to it's own table. It will be removed on allocation.
	 */
	public LocationReservation reserveLocation(StorageLocation location, UnitLoad unitLoad, Class<?> reserverType,
			Long reserverId, String reserverName) throws BusinessException {
		String logStr = "reserveLocation ";

		if (reservationService.exists(location, unitLoad)) {
			logger.log(Level.WARNING, logStr + "location is already reserved for unitload. ignore. location=" + location
					+ ", unitload=" + unitLoad);
			return null;
		}

		LocationReservation res = reservationService.create(location, unitLoad, reserverType.getClass().getName(),
				reserverId, reserverName);

		writeAllocation(location, unitLoad);

		return res;
	}

	/**
	 * Writes an allocation for the location. If the location is reserved, the
	 * allocation has already been written with the reservation. In this case just
	 * the reservation is removed.
	 * 
	 * @param location
	 * @param unitLoad
	 * @throws BusinessException
	 */
	public void allocateLocation(StorageLocation location, UnitLoad unitLoad) throws BusinessException {
		String logStr = "allocateLocation ";

		if (reservationService.exists(location, unitLoad)) {
			reservationService.remove(location, unitLoad);
			logger.log(Level.FINE, logStr + "location is reserved for unitload. do not allocate twice. location="
					+ location + ", unitload=" + unitLoad);
			return;
		}

		writeAllocation(location, unitLoad);
	}

	private void writeAllocation(StorageLocation location, UnitLoad unitLoad) throws BusinessException {
		String logStr = "writeAllocation ";

		if (location == null) {
			logger.log(Level.SEVERE, logStr + "No loaction");
			throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingParameterLocation");
		}

		if (unitLoad == null) {
			logger.log(Level.SEVERE, logStr + "No unitLoad");
			throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingParameterUnitLoad");
		}

		TypeCapacityConstraint unitLoadCapa = capaService.read(location.getLocationType(), unitLoad.getUnitLoadType());
		if (unitLoadCapa == null) {
			// Without a rule no allocation is written.
			return;
		}

		BigDecimal unitLoadAllocation = unitLoadCapa.getAllocation();
		if (unitLoadAllocation.compareTo(NumberUtils.HUNDRED) > 0) {
			// Allocate multiple locations
			BigDecimal remainingAllocation = unitLoadAllocation;
			List<StorageLocation> neighbors = readNeighbors(location, false);
			boolean originalLocation = true;
			for (StorageLocation neighbor : neighbors) {
				if (remainingAllocation.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
				if (originalLocation) {
					neighbor.setAllocation(unitLoadAllocation);
					originalLocation = false;
				} else {
					neighbor.setAllocation(NumberUtils.HUNDRED);
				}
				remainingAllocation = remainingAllocation.subtract(NumberUtils.HUNDRED);
				logger.log(Level.FINE, logStr + "neighbor location=" + neighbor + ", unitLoad=" + unitLoad);
			}

		} else {
			BigDecimal locationAllocation = location.getAllocation();
			locationAllocation = locationAllocation.add(unitLoadAllocation);
			location.setAllocation(locationAllocation);

			logger.log(Level.FINE,
					logStr + "location=" + location + ", unitLoad=" + unitLoad + ", allocation=" + locationAllocation);
		}
	}

	/**
	 * Removes the allocation of a location
	 */
	public void deallocateLocation(StorageLocation location, UnitLoad unitLoad) throws BusinessException {
		deallocateLocation(location, unitLoad, true);
	}
	
	/**
	 * Removes the allocation of a location
	 */
	public void deallocateLocation(StorageLocation location, UnitLoad unitLoad, boolean checkEmptyLocation)
			throws BusinessException {
		String logStr = "deallocateLocation ";

		if (location == null) {
			logger.log(Level.WARNING, logStr + "No loaction");
			return;
		}
		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "No unitLoad");
			return;
		}

		reservationService.remove(location, unitLoad);

		BigDecimal locationAllocation = location.getAllocation();
		if (locationAllocation == null || locationAllocation.compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.FINE,
					logStr + "Location was not allocated. location=" + location + ", unitLoad=" + unitLoad);
			location.setAllocation(BigDecimal.ZERO);
			return;
		}

		TypeCapacityConstraint unitLoadCapa = capaService.read(location.getLocationType(), unitLoad.getUnitLoadType());
		if (unitLoadCapa == null) {
			// Without a rule no allocation is written.
			return;
		}

		BigDecimal unitLoadAllocation = unitLoadCapa.getAllocation();
		if (unitLoadAllocation.compareTo(NumberUtils.HUNDRED) > 0) {
			// Deallocate multiple locations
			// The main location holds the sum allocation of this location and
			// all additional allocated neighbors.
			// So deallocation will remove allocation from the neighbors as long
			// as the complete sum-allocation is deallocated.
			// Only 100 percent steps are supported.
			// Start with locations allocation. Necessary when allocation master data is
			// changed.
			BigDecimal remainingAllocation = locationAllocation;
			List<StorageLocation> neighbors = readNeighbors(location, true);
			for (StorageLocation neighbor : neighbors) {
				if (remainingAllocation.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
				remainingAllocation = remainingAllocation.subtract(NumberUtils.HUNDRED);
				neighbor.setAllocation(BigDecimal.ZERO);
				logger.log(Level.FINE, logStr + "location=" + neighbor + ", unitLoad=" + unitLoad);
			}

		} else {
			BigDecimal oldAllocation = location.getAllocation();

			locationAllocation = locationAllocation.subtract(unitLoadAllocation);
			if (locationAllocation.compareTo(BigDecimal.ZERO) < 0) {
				locationAllocation = BigDecimal.ZERO;
			}
			location.setAllocation(locationAllocation);

			logger.log(Level.FINE, logStr + "location=" + location.getName() + ", unitLoad=" + unitLoad
					+ ", new allocation=" + locationAllocation);

			if (locationAllocation.compareTo(BigDecimal.ZERO) == 0) {
				if (checkEmptyLocation && oldAllocation.compareTo(locationAllocation) != 0) {
					List<UnitLoad> otherUnitLoads = unitLoadService.readList(null, location, null, null, null, null, null);
					for (UnitLoad otherUnitLoad : otherUnitLoads) {
						if (!otherUnitLoad.equals(unitLoad)) {
							logger.log(Level.WARNING,
									logStr + "Found unitLoads on not allocated location. location=" + location);
							recalculateAllocation(location, unitLoad);
							break;
						}
					}
				}
			}
		}

	}

	/**
	 * Removes all allocations from the location
	 * 
	 * @param location
	 * @throws BusinessException
	 */
	public void deallocateLocation(StorageLocation location) throws BusinessException {
		String logStr = "deallocateLocation ";

		if (location == null) {
			logger.log(Level.SEVERE, logStr + "No loaction");
			throw new BusinessException(Wms2BundleResolver.class, "LocationReserver.missingParameterLocation");
		}

		reservationService.remove(location, null);

		if (location.getAllocation().compareTo(BigDecimal.ZERO) > 0) {
			logger.log(Level.WARNING, logStr + "There is a allocation value difference. Set to zero. location="
					+ location + ", allocation=" + location.getAllocation());
		}
		location.setAllocation(BigDecimal.ZERO);
	}

	/**
	 * Recalculate the allocation of a location
	 * 
	 * @param location
	 * @param removedUnitLoads
	 * @throws BusinessException
	 */
	private void recalculateAllocation(StorageLocation location, UnitLoad removedUnitLoad) throws BusinessException {
		String logStr = "recalculateAllocation ";

		Collection<UnitLoad> allocatingUnitLoads = reservationService.readReservingUnitLoadsByLocation(location);
		Collection<UnitLoad> locationsUnitLoads = unitLoadService.readList(null, location, null, null, null, null, null);
		for (UnitLoad unitLoad : locationsUnitLoads) {
			if (!allocatingUnitLoads.contains(unitLoad)) {
				allocatingUnitLoads.add(unitLoad);
			}
		}

		location.setAllocation(BigDecimal.ZERO);

		for (UnitLoad unitLoad : allocatingUnitLoads) {

			if (removedUnitLoad != null && removedUnitLoad.equals(unitLoad)) {
				continue;
			}

			TypeCapacityConstraint unitLoadCapa = capaService.read(location.getLocationType(), unitLoad.getUnitLoadType());
			if (unitLoadCapa == null) {
				logger.log(Level.INFO,
						logStr + "No capacity option for location=" + location + ", locationType="
								+ location.getLocationType() + ", unitLoad=" + unitLoad + ", unitLoadType="
								+ unitLoad.getUnitLoadType());
				continue;
			}

			location.setAllocation(location.getAllocation().add(unitLoadCapa.getAllocation()));
		}
	}

	/**
	 * Generate a sorted list left-to-right. Beginning with the starting location
	 */
	private List<StorageLocation> readNeighbors(StorageLocation masterLocation, boolean readAllocated) {
		List<StorageLocation> neighbors = new ArrayList<>();

		if (masterLocation == null) {
			return neighbors;
		}

		List<StorageLocation> locationsOfField = locationService.readLocationsOfField(masterLocation);
		if (locationsOfField == null) {
			return neighbors;
		}

		boolean foundStartLocation = false;
		for (StorageLocation neighbor : locationsOfField) {

			if (masterLocation.equals(neighbor)) {
				foundStartLocation = true;
			}
			if (!foundStartLocation) {
				continue;
			}

			if (!neighbor.getLocationType().equals(masterLocation.getLocationType())) {
				// The location has a different type.
				// Don't touch it. Stop operation
				break;
			}

			if (!readAllocated && neighbor.getAllocation().compareTo(BigDecimal.ZERO) > 0) {
				break;
			}

			neighbors.add(neighbor);
		}

		return neighbors;
	}
}
