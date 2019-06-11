# myWMS

A fork of myWMS LOS Warehouse Management System

This software is licensed under the GNU General Public License

myWMS LOS is a professional open source Warehouse Management System WMS under GPL. It is running in industrial 24/7 environments and supports all basic processes. It runs on mobile terminals, works with barcodes & RFID. The software is a JEE architecture.


Features:

* Ready to use warehouse management system (WMS)
* Designed for manual driven warehouses
* Barcode/RFID identification, mobile terminals for paperless processes
* Modular/ SOA architecture
* Supported technologies: JEE7, Wildfly, Netbeans RCP, iReport, SOAP
* Supports Wildfly 16 and Java 11


## Sources

This fork is based on the latest public available version of the Logata SVN sources: https://svn.logistics-mall.com/svn/los.reference

Another location of the original sources, but a little outdated, is located on sourceforge: https://sourceforge.net/projects/mywmslos

The documentation and additional resources of the project are located on: http://www.mywms.org


## Changes within this fork

The first action is to set the project to an actual environment (2019)

- Support of Java 11 runtime
- Support of wildfly 16 version
- Support of netbeans 10 rich client
- Support of JasperReports 6
- Change build system from ant to maven
- Change version control from svn to git


## Future changes

It is planned to keep the project on the current development and runtime platforms. At least the LTS releases of the Java platform and used tools like wildfly application server should be supported.

Furthermore functional and technical enhancements will be added to the project.

Here some ideas.
This are only ideas of what could be done. There is no actual plan to do it at all nor a specific roadmap.
Please contribute to the project. Bring your ideas, code and modules. Vote for new functionalities.

- Advice as master/detail
- Only one journal for stock unit and unit load changes
- Add images and attachments to master data objects
- Change-history
- Configurable sequence numbers
- Multiple locks on one entity
- More powerful sorting criteria in storage strategies
- More powerful criteria in storage location capacity configuration
- Configurable picking strategy
- Stocktaking orders for single unit loads
- Distinction of different stock states
- Consigned goods
- Pick-to-pack functionality in picking process.
- Packing. Dialogues to handle packing operations.
- Direct new stock registration without receive delivery or stocktaking operations
- Direct removal registration. Without delivery-, customer-, or pickingorder.
- Manual picking order registration. Without delivery- or customerorder.
- Summary dialogues to view information of current stocks and current storage location occupation.
- Input Interfaces. Input of data. Write systemdata, masterdata, goods-in and goods-out orders into the system.
- Output Interfaces. Output of stock, goods-in and goods-out information. Connect to external web services or other interfaces.
- Remote connector. Connect to and use the backend via remote interfaces or other interfaces. E.g. REST Services.
- Easy to use capability to introduce individual add-ons
- Switch netbeans rich-client to a web-client
- Switch customization beans to CDI events
- Make a set of entity services with equal behavior
- Only one BundleHelper/Translator
- Move remote services into separate modules



__If you have any enhancements, please contribute to the project!__

__If you have any idea of functional or technical enhancement, please contribute to the project!__

__If you plan to introduce this software into you own business matters, please contribute to the project!__
