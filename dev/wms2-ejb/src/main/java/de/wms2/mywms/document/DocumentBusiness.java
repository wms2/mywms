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
package de.wms2.mywms.document;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.res.BundleResolver;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.util.ImageUtils;

/**
 * Handling of documents
 * 
 * @author krane
 *
 */
@Stateless
public class DocumentBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private DocumentEntityService documentService;

	/**
	 * Read the image assigned to an entity as Document
	 */
	public Document readImage(Class<?> entityType, Long entityId) throws BusinessException {
		if (entityId == null) {
			return null;
		}
		String imageName = generateDocumentName(entityType, entityId, "image", null);
		Document doc = documentService.read(imageName);
		return doc;
	}

	/**
	 * Read the scaled image assigned to an entity
	 */
	public Document readImage(Class<?> entityType, Long entityId, int imageSize) throws BusinessException {
		Document doc = readImage(entityType, entityId);
		if (doc == null) {
			return null;
		}

		byte[] data = null;
		try {
			data = ImageUtils.scaleImage(doc.getData(), imageSize, false);
			doc.setData(data);
		} catch (IOException e) {
		}
		return doc;
	}

	/**
	 * Remove the image of the entity
	 */
	public void deleteImage(Class<?> entityType, Long entityId) throws BusinessException {
		String logStr = "deleteImage ";
		logger.log(Level.INFO, logStr + "entityType=" + entityType + ", entityId=" + entityId);

		if (entityId == null) {
			return;
		}

		String imageName = generateDocumentName(entityType, entityId, "image", null);
		documentService.delete(imageName);
	}

	/**
	 * Remove all documents of the entity
	 */
	public void deleteDocuments(Class<?> entityType, Long entityId) throws BusinessException {
		String logStr = "deleteDocuments ";
		logger.log(Level.INFO, logStr + "entityType=" + entityType + ", entityId=" + entityId);

		if (entityId == null) {
			return;
		}

		String namePrefix = entityType.getSimpleName() + ":" + entityId.toString();
		documentService.deleteAll(namePrefix);
	}

	/**
	 * Save an image for an entity. The old image is removed.
	 */
	public Document saveImage(Class<?> entityType, Long entityId, String documentType, byte[] data)
			throws BusinessException {
		String logStr = "saveImage ";
		logger.log(Level.INFO, logStr + "entityType=" + entityType + ", entityId=" + entityId);

		if (entityId == null) {
			return null;
		}

		deleteImage(entityType, entityId);

		String imageName = generateDocumentName(entityType, entityId, "image", null);
		Document doc = documentService.create(imageName, documentType, data);

		return doc;
	}

	/**
	 * Save an image for an entity. The old image is removed.
	 */
	public Document saveImage(Class<?> entityType, Long entityId, String documentType, byte[] data, int imageSize)
			throws BusinessException {
		String logStr = "saveImage ";
		logger.log(Level.INFO, logStr + "entityType=" + entityType + ", entityId=" + entityId);

		if (entityId == null) {
			return null;
		}

		deleteImage(entityType, entityId);

		try {
			data = ImageUtils.scaleImage(data, imageSize, true);
		} catch (Throwable ex) {
			logger.log(Level.WARNING, logStr + "Cannot scale image. abort", ex);
			throw new BusinessException(BundleResolver.class, "Document.cannotScale");
		}

		String imageName = generateDocumentName(entityType, entityId, "image", null);
		Document doc = documentService.create(imageName, documentType, data);

		return doc;
	}

	/**
	 * Save a document for an entity. The old document is removed.
	 */
	public Document saveDocument(Class<?> entityType, Long entityId, String displayName, String documentType,
			byte[] data, String path) throws BusinessException {
		String logStr = "saveDocument ";
		logger.log(Level.INFO, logStr + "entityType=" + entityType + ", entityId=" + entityId);

		String documentName = generateDocumentName(entityType, entityId, path, displayName);

		documentService.delete(documentName);

		Document doc = documentService.create(documentName, documentType, data);

		return doc;
	}

	/**
	 * Generate the name of an attached document.
	 * <p>
	 * This name is used to store the document.
	 */
	public String generateDocumentName(Class<?> entityType, Long entityId, String path, String displayName)
			throws BusinessException {
		String documentName = entityType.getSimpleName();
		if (entityId != null) {
			documentName += ":" + entityId.toString();
		}
		if (!StringUtils.isBlank(path)) {
			documentName += "/" + path;
		}
		if (!StringUtils.isBlank(displayName)) {
			documentName += "/" + displayName;
		}

		return documentName;
	}

}
