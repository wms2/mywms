/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.report;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.linogistix.los.example.LocationTopologyException;
import de.wms2.mywms.document.Document;
import junit.framework.TestCase;

/**
 * 
 * @author trautm
 */
public class StorageLocationLabelReportBeanTest extends TestCase {

	private static final Logger logger = Logger
			.getLogger(StorageLocationLabelReportBeanTest.class);

	public StorageLocationLabelReportBeanTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// public void testGenerateStorageLocationLabels() {
	// try {
	// StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
	// int offset = 3;
	// List<StorageLocationLabelTO> l = new ArrayList();
	// for (int r = 1; r<5;r++){
	// for (int i=1;i<8;i++){
	// StorageLocationLabelTO dto = new StorageLocationLabelTO("A1-"+r+"-" +
	// i,r-3);
	// l.add(dto);
	// }
	// }
	// StorageLocationLabel s = b.generateStorageLocationLabels(l);

	// byte[] pdf = s.getDocument();

	// if (pdf == null){
	// fail();
	// }
	// String filename = "testout/" + s.getName() + ".pdf";
	// logger.info("going to write "+ filename);
	// OutputStream out = new FileOutputStream(filename);
	// out.write(pdf);
	// out.close();
	// logger.info("wrote "+ filename);

	// } catch (Throwable ex) {
	// logger.error(ex, ex);
	// fail(ex.getMessage());
	// }

	// }

	/*
	 * public void testGenerateStorageLocationLabelsA1_10Komm() { try {
	 * StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
	 * Integer offset = 3; List<StorageLocationLabelTO> l = new ArrayList();
	 * for (int rack = 1; rack < 11; rack++) { for (int r = 1; r < 4; r++) { for
	 * (int i = 1; i < 12; i++) { if (r == 1) offset = -1; else offset = 0; if
	 * (r == 1) continue; StorageLocationLabelTO dto = new
	 * StorageLocationLabelTO( "A" + rack + "-" + r + "-" + i, offset);
	 * l.add(dto); } } } StorageLocationLabel s =
	 * b.generateStorageLocationLabels(l);
	 * 
	 * byte[] pdf = s.getDocument();
	 * 
	 * if (pdf == null) { fail(); } String filename = "testout/" + s.getName() +
	 * "Komm.pdf"; logger.info("going to write " + filename); OutputStream out =
	 * new FileOutputStream(filename); out.write(pdf); out.close();
	 * logger.info("wrote " + filename); } catch (Throwable ex) {
	 * logger.error(ex, ex); fail(ex.getMessage()); } }
	 * 
	 * public void testGenerateStorageLocationLabelsA1_10KommR1() { try {
	 * StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
	 * Integer offset = 3; List<StorageLocationLabelTO> l = new ArrayList();
	 * for (int rack = 1; rack < 11; rack++) { for (int r = 1; r < 4; r++) { for
	 * (int i = 1; i < 12; i++) { if (r == 1) offset = -1; else offset = 0; if
	 * (r > 1) continue; StorageLocationLabelTO dto = new
	 * StorageLocationLabelTO( "A" + rack + "-" + r + "-" + i, offset);
	 * l.add(dto); } } } StorageLocationLabel s =
	 * b.generateStorageLocationLabels(l);
	 * 
	 * byte[] pdf = s.getDocument();
	 * 
	 * if (pdf == null) { fail(); } String filename = "testout/" + s.getName() +
	 * "KommR1.pdf"; logger.info("going to write " + filename); OutputStream out =
	 * new FileOutputStream(filename); out.write(pdf); out.close();
	 * logger.info("wrote " + filename); } catch (Throwable ex) {
	 * logger.error(ex, ex); fail(ex.getMessage()); } }
	 * 
	 * public void testGenerateStorageLocationLabelsA1_10Palette() { try {
	 * StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
	 * Integer offset = 3; List<StorageLocationLabelTO> l = new ArrayList();
	 * for (int rack = 1; rack < 11; rack++) { for (int r = 4; r < 6; r++) { for
	 * (int i = 1; i < 4; i++) { offset = 0;
	 * 
	 * StorageLocationLabelTO dto = new StorageLocationLabelTO( "A" + rack + "-" +
	 * r + "-" + i, offset); l.add(dto); } } } StorageLocationLabel s =
	 * b.generateStorageLocationLabels(l);
	 * 
	 * byte[] pdf = s.getDocument();
	 * 
	 * if (pdf == null) { fail(); } String filename = "testout/" + s.getName() +
	 * "Palette.pdf"; logger.info("going to write " + filename); OutputStream
	 * out = new FileOutputStream(filename); out.write(pdf); out.close();
	 * logger.info("wrote " + filename); } catch (Throwable ex) {
	 * logger.error(ex, ex); fail(ex.getMessage()); } }
	 */

	// ----------------------------------------------------------------------
	/*
	 * public void testGenerateStorageLocationLabelsA11_33() { try {
	 * StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
	 * Integer offset = 3; List<StorageLocationLabelTO> l = new ArrayList();
	 * for(int rack=11;rack<34;rack++){ for (int r = 2; r<5;r++){ for (int
	 * i=1;i<4;i++){ offset = 0; StorageLocationLabelTO dto = new
	 * StorageLocationLabelTO("A"+rack+"-"+r+"-" + i,offset); l.add(dto); } } }
	 * StorageLocationLabel s = b.generateStorageLocationLabels(l);
	 * 
	 * byte[] pdf = s.getDocument();
	 * 
	 * if (pdf == null){ fail(); } String filename = "testout/" + s.getName() +
	 * "A11_33.pdf"; logger.info("going to write "+ filename); OutputStream out =
	 * new FileOutputStream(filename); out.write(pdf); out.close();
	 * logger.info("wrote "+ filename); } catch (Throwable ex) {
	 * logger.error(ex, ex); fail(ex.getMessage()); } }
	 * 
	 * public void testGenerateStorageLocationLabelsA11_33R1() { try {
	 * StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
	 * Integer offset = 3; List<StorageLocationLabelTO> l = new ArrayList();
	 * for(int rack=11;rack<34;rack++){ for (int r = 1; r<2;r++){ for (int
	 * i=1;i<4;i++){ offset = -1; StorageLocationLabelTO dto = new
	 * StorageLocationLabelTO("A"+rack+"-"+r+"-" + i,offset); l.add(dto); } } }
	 * StorageLocationLabel s = b.generateStorageLocationLabels(l);
	 * 
	 * byte[] pdf = s.getDocument();
	 * 
	 * if (pdf == null){ fail(); } String filename = "testout/" + s.getName() +
	 * "A11_33R1.pdf"; logger.info("going to write "+ filename); OutputStream
	 * out = new FileOutputStream(filename); out.write(pdf); out.close();
	 * logger.info("wrote "+ filename); } catch (Throwable ex) {
	 * logger.error(ex, ex); fail(ex.getMessage()); } }
	 */

//	public void testcreateLabelsARacks() throws TopologyException {
//		List<StorageLocationLabelTO> weiss = new ArrayList();
//		List<StorageLocationLabelTO> gelb = new ArrayList();
//		Vector<int[]> rackIdents = new Vector();
//		rackIdents.add(new int[] { 1, 5 });
//		rackIdents.add(new int[] { 2, 5 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//			for (int rackNo = 1; rackNo <= ident[1]; rackNo++) {
//				String rackName = "A" + aisle + "-" + rackNo;
//				for (int x = 1; x < 12; x++) {
//					for (int y = 1; y < 4; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//				for (int x = 1; x < 4; x++) {
//					for (int y = 4; y < 6; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						StorageLocationLabelTO dto = new StorageLocationLabelTO(
//								locName, 0);
//						weiss.add(dto);
//					}
//				}
//			}
//		}
//		rackIdents = new Vector();
//		// 11-12
//		rackIdents.add(new int[] { 3, 1, 2 });
//		// 16-16
//		rackIdents.add(new int[] { 4, 1, 2 });
//		// 21-25
//		rackIdents.add(new int[] { 5, 1, 5 });
//		// 28-30
//		rackIdents.add(new int[] { 6, 3, 5 });
//		// 31-33
//		rackIdents.add(new int[] { 7, 1, 3 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "A" + aisle + "-" + rackNo;
//				for (int x = 1; x < 4; x++) {
//					for (int y = 1; y < 5; y++) {
//
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//
//		rackIdents = new Vector();
//		// 13-15
//		rackIdents.add(new int[] { 3, 3, 5 });
//		// 18-20
//		rackIdents.add(new int[] { 4, 3, 5 });
//		// 21-25
//		rackIdents.add(new int[] { 5, 1, 5 });
//		// 27
//		rackIdents.add(new int[] { 6, 2, 2 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "A" + aisle + "-" + rackNo;
//
//				for (int x = 1; x < 4; x++) {
//					for (int y = 1; y < 4; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//
//		rackIdents = new Vector();
//		// 26
//		rackIdents.add(new int[] { 6, 1, 1 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "A" + aisle + "-" + rackNo;
//
//				for (int x = 1; x < 4; x++) {
//					for (int y = 1; y < 2; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//
//				for (int x = 1; x < 12; x++) {
//					for (int y = 2; y < 4; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//				for (int x = 1; x < 4; x++) {
//					for (int y = 4; y < 6; y++) {
//						LOSRackLocation rl;
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//
//		// -------------------------
//
//		StorageLocationLabelTO[] weissA = weiss
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(weissA, new LabelComparator());
//		weiss = Arrays.asList(weissA);
//
//		StorageLocationLabelTO[] gelbA = gelb
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(gelbA, new LabelComparator());
//		gelb = Arrays.asList(gelbA);
//
//		StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
//
//		StorageLocationLabel s;
//		try {
//			s = b.generateStorageLocationLabels(weiss);
//
//			byte[] pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			String filename = "testout/RackA_" + s.getName() + "Weiss.pdf";
//			logger.info("going to write " + filename);
//			OutputStream out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//
//			// -------------------------
//			s = b.generateStorageLocationLabels(gelb);
//
//			pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			filename = "testout/RackA_" + s.getName() + "Gelb.pdf";
//			logger.info("going to write " + filename);
//			out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//		} catch (Throwable e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//	}
//
//	public void testcreateLabelsTRacks() throws TopologyException {
//		List<StorageLocationLabelTO> weiss = new ArrayList();
//		List<StorageLocationLabelTO> gelb = new ArrayList();
//		Vector<int[]> rackIdents = new Vector();
//		rackIdents.add(new int[] { 1, 2 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//			for (int rackNo = 1; rackNo <= ident[1]; rackNo++) {
//				String rackName = "T" + aisle + "-" + rackNo;
//				for (int x = 1; x < 3; x++) {
//					for (int y = 1; y < 4; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//				for (int x = 1; x < 3; x++) {
//					for (int y = 4; y < 6; y++) {
//						String locName = rackName + "-" + y + "-" + x;
//						StorageLocationLabelTO dto = new StorageLocationLabelTO(
//								locName, 0);
//						weiss.add(dto);
//					}
//				}
//			}
//		}
//
//		StorageLocationLabelTO dto = new StorageLocationLabelTO("TESTWA", 0);
//
//		weiss.add(dto);
//
//		// -------------------------
//
//		StorageLocationLabelTO[] weissA = weiss
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(weissA, new LabelComparator());
//		weiss = Arrays.asList(weissA);
//
//		StorageLocationLabelTO[] gelbA = gelb
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(gelbA, new LabelComparator());
//		gelb = Arrays.asList(gelbA);
//
//		StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
//
//		StorageLocationLabel s;
//		try {
//			s = b.generateStorageLocationLabels(weiss);
//
//			byte[] pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			String filename = "testout/RackT_" + s.getName() + "Weiss.pdf";
//			logger.info("going to write " + filename);
//			OutputStream out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//
//			// -------------------------
//			s = b.generateStorageLocationLabels(gelb);
//
//			pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			filename = "testout/RackT_" + s.getName() + "Gelb.pdf";
//			logger.info("going to write " + filename);
//			out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//		} catch (Throwable e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//	}
//
//	public void testcreateLabelsCRacks() throws TopologyException {
//		List<StorageLocationLabelTO> weiss = new ArrayList();
//		List<StorageLocationLabelTO> gelb = new ArrayList();
//		Vector<int[]> rackIdents = new Vector();
//
//		rackIdents.add(new int[] { 1, 1, 4 });
//		rackIdents.add(new int[] { 1, 6, 7 });
//		rackIdents.add(new int[] { 2, 1, 6 });
//		rackIdents.add(new int[] { 3, 1, 6 });
//		rackIdents.add(new int[] { 4, 1, 4 });
//		rackIdents.add(new int[] { 5, 1, 4 });
//		rackIdents.add(new int[] { 6, 1, 5 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "C" + aisle + "-" + rackNo;
//				for (int x = 1; x < 4; x++) {
//					for (int y = 1; y < 4; y++) {
//
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//		 rackIdents = new Vector();
//		rackIdents.add(new int[] { 1, 5, 5 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "C" + aisle + "-" + rackNo;
//				for (int x = 1; x < 4; x++) {
//					for (int y = 2; y < 4; y++) {
//
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//		rackIdents = new Vector();
//		rackIdents.add(new int[] { 1, 8, 8 });
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "C" + aisle + "-" + rackNo;
//				for (int x = 1; x < 2; x++) {
//					for (int y = 1; y < 4; y++) {
//
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//
//
//		// -------------------------
//
//		StorageLocationLabelTO[] weissA = weiss
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(weissA, new LabelComparator());
//		weiss = Arrays.asList(weissA);
//
//		StorageLocationLabelTO[] gelbA = gelb
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(gelbA, new LabelComparator());
//		gelb = Arrays.asList(gelbA);
//
//		StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
//
//		StorageLocationLabel s;
//		try {
//			s = b.generateStorageLocationLabels(weiss);
//
//			byte[] pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			String filename = "testout/RackC_" + s.getName() + "Weiss.pdf";
//			logger.info("going to write " + filename);
//			OutputStream out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//
//			// -------------------------
//			s = b.generateStorageLocationLabels(gelb);
//
//			pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			filename = "testout/RackC_" + s.getName() + "Gelb.pdf";
//			logger.info("going to write " + filename);
//			out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//		} catch (Throwable e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//	}
//	
//	public void testcreateLabelsDRacks() throws TopologyException {
//		List<StorageLocationLabelTO> weiss = new ArrayList();
//		List<StorageLocationLabelTO> gelb = new ArrayList();
//		Vector<int[]> rackIdents = new Vector();
//
//		//aisle - rack from - rack to
//		rackIdents.add(new int[] { 1, 1, 3 });
//		rackIdents.add(new int[] { 3, 1, 3 });
//		rackIdents.add(new int[] { 4, 1, 5 });
//		rackIdents.add(new int[] { 6, 1, 5 });
//		rackIdents.add(new int[] { 7, 1, 5 });
//		rackIdents.add(new int[] { 9, 1, 6 });
//		rackIdents.add(new int[] { 10, 1, 3 });
//		
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "D" + aisle + "-" + rackNo;
//				for (int x = 1; x < 4; x++) {
//					for (int y = 1; y < 5; y++) {
//
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//
//		
//		rackIdents = new Vector();
//		rackIdents.add(new int[] { 2, 1, 3 });
//		rackIdents.add(new int[] { 5, 1, 5 });
//		rackIdents.add(new int[] { 8, 1, 5 });
//		
//
//		for (int[] ident : rackIdents) {
//			int aisle = ident[0];
//
//			for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
//				String rackName = "D" + aisle + "-" + rackNo;
//				for (int x = 1; x < 4; x++) {
//					for (int y = 1; y < 4; y++) {
//
//						String locName = rackName + "-" + y + "-" + x;
//						if (y == 1) {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, -1);
//							gelb.add(dto);
//						} else {
//							StorageLocationLabelTO dto = new StorageLocationLabelTO(
//									locName, 0);
//							weiss.add(dto);
//						}
//					}
//				}
//			}
//		}
//
//		// -------------------------
//
//		StorageLocationLabelTO[] weissA = weiss
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(weissA, new LabelComparator());
//		weiss = Arrays.asList(weissA);
//
//		StorageLocationLabelTO[] gelbA = gelb
//				.toArray(new StorageLocationLabelTO[0]);
//		Arrays.sort(gelbA, new LabelComparator());
//		gelb = Arrays.asList(gelbA);
//
//		StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();
//
//		StorageLocationLabel s;
//		try {
//			s = b.generateStorageLocationLabels(weiss);
//
//			byte[] pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			String filename = "testout/RackD_" + s.getName() + "Weiss.pdf";
//			logger.info("going to write " + filename);
//			OutputStream out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//
//			// -------------------------
//			s = b.generateStorageLocationLabels(gelb);
//
//			pdf = s.getDocument();
//
//			if (pdf == null) {
//				fail();
//			}
//			filename = "testout/RackD_" + s.getName() + "Gelb.pdf";
//			logger.info("going to write " + filename);
//			out = new FileOutputStream(filename);
//			out.write(pdf);
//			out.close();
//			logger.info("wrote " + filename);
//		} catch (Throwable e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//	}

	
	public void testcreateLabelsSpeedyRacks() throws LocationTopologyException {
	List<StorageLocationLabelTO> weiss = new ArrayList<StorageLocationLabelTO>();
	List<StorageLocationLabelTO> gelb = new ArrayList<StorageLocationLabelTO>();
	Vector<int[]> rackIdents = new Vector<int[]>();

	//aisle - rack from - rack to
	rackIdents.add(new int[] { 1, 1, 3 });
	
	
	for (int[] ident : rackIdents) {
		int aisle = ident[0];

		for (int rackNo = ident[1]; rackNo <= ident[2]; rackNo++) {
			String rackName = "M" + aisle + "-" + rackNo;
			for (int x = 1; x < 4; x++) {
				for (int y = 1; y < 5; y++) {

					String locName = rackName + "-" + y + "-" + x;
					if (y == 1) {
						StorageLocationLabelTO dto = new StorageLocationLabelTO(
								locName, -1);
						gelb.add(dto);
					} else {
						StorageLocationLabelTO dto = new StorageLocationLabelTO(
								locName, 0);
						weiss.add(dto);
					}
				}
			}
		}
	}

	// -------------------------

	StorageLocationLabelTO[] weissA = weiss
			.toArray(new StorageLocationLabelTO[0]);
	Arrays.sort(weissA, new LabelComparator());
	weiss = Arrays.asList(weissA);

	StorageLocationLabelTO[] gelbA = gelb
			.toArray(new StorageLocationLabelTO[0]);
	Arrays.sort(gelbA, new LabelComparator());
	gelb = Arrays.asList(gelbA);

	StorageLocationLabelReportBean b = new StorageLocationLabelReportBean();

	Document s;
	try {
		s = b.generateStorageLocationLabels(weiss);

		byte[] pdf = s.getData();

		if (pdf == null) {
			fail();
		}
		String filename = "testout/RackD_" + s.getName() + "Weiss.pdf";
		logger.info("going to write " + filename);
		OutputStream out = new FileOutputStream(filename);
		out.write(pdf);
		out.close();
		logger.info("wrote " + filename);

		// -------------------------
		s = b.generateStorageLocationLabels(gelb);

		pdf = s.getData();

		if (pdf == null) {
			fail();
		}
		filename = "testout/RackD_" + s.getName() + "Gelb.pdf";
		logger.info("going to write " + filename);
		out = new FileOutputStream(filename);
		out.write(pdf);
		out.close();
		logger.info("wrote " + filename);
	} catch (Throwable e) {
		logger.error(e, e);
		fail(e.getMessage());
	}
}
	class LabelComparator implements Comparator<StorageLocationLabelTO> {

		public int compare(StorageLocationLabelTO o1, StorageLocationLabelTO o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}

}
