/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.customization.ImportDataServiceDispatcher;

@Stateless
public class ImportDataServiceBean implements ImportDataService {
	
	private static final Logger log = Logger.getLogger(ImportDataServiceBean.class);
	@EJB
	private ImportDataServiceDispatcher dataServiceDispatcher;
	
	public List<Object> importData(String className, byte[] data) throws ImportDataException, FacadeException {
		
		List<Object> ret = new ArrayList<Object>();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		
		XMLInputFactory factory = XMLInputFactory.newInstance();

		try {
			
			XMLStreamReader reader = factory.createXMLStreamReader(bin);
			
			int event = 0;
			
			// skip Excel header
			
			while(reader.hasNext()){
				event = reader.next();
				if(event == XMLStreamConstants.START_ELEMENT){
					String element = reader.getLocalName();
					
					if(element.equalsIgnoreCase("Row")){
						break;
					}
				}
			}
			
			// first row defines the keys
			ArrayList<String> keyList = new ArrayList<String>();
			// iterate Cell-Elements
			for(int ce = reader.nextTag();
				ce == XMLStreamConstants.START_ELEMENT;
				ce = reader.nextTag())
			{
				reader.require(XMLStreamConstants.START_ELEMENT, null, "Cell");
				
				int de = reader.nextTag();
				if(de == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("Data")){
				
					// set cursor on text element
					reader.next();
					
					String key = reader.getText();
					key = key.toLowerCase();
					key = key.trim();
					
					System.out.println("--- Read key > "+key);
					
					keyList.add(key);
					
					// set cursor on End-Tag of Data-Element
					reader.nextTag();
					
					// set cursor on EndTag of Cell-Element
					reader.nextTag();
				}
			}
			
			// iterate Row-Elements
			for(int e = reader.nextTag(); 
				e == XMLStreamConstants.START_ELEMENT; 
				e = reader.nextTag())
			{
				reader.require(XMLStreamConstants.START_ELEMENT, null, "Row");
				
				// iterate Cell-Elements
				int x = 0;
				HashMap<String, String> attrMap = new HashMap<String, String>(keyList.size());
				
				for(int ce = reader.nextTag();
					ce == XMLStreamConstants.START_ELEMENT;
					ce = reader.nextTag())
				{
					reader.require(XMLStreamConstants.START_ELEMENT, null, "Cell");
					if(reader.getAttributeCount() > 0 && reader.getAttributeLocalName(0).equalsIgnoreCase("index")){
						
						String index = reader.getAttributeValue(0);
						x = Integer.parseInt(index) - 1;
						
						System.out.println("--- Skipping empty attribute : setting index to "+index+" -1");
						
					}
					
					int de = reader.nextTag();
					if(de == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("Data")){
					
						// set cursor on text element
						reader.next();
						
						String value = reader.getText();
						
						value = value.trim();
						
						attrMap.put(keyList.get(x), value);
						
						// set cursor on End-Tag of Data-Element
						reader.nextTag();
						
						// set cursor on EndTag of Cell-Element
						reader.nextTag();
					}
					x++;
					
				}
				
				if(!attrMap.isEmpty()){
					ret.add(dataServiceDispatcher.handleDataRecord(className, attrMap));
				}
					
				
				// set cursor on End-Tag of Row-Element through loop back
			}
			
			System.out.println(" End Document");
			
			return ret;
			
		} catch (XMLStreamException e) {
			log.error(e.getMessage(), e);
			return null;
		}
		
	}

}
