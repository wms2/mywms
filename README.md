# myWMS - Release 2

A fork of myWMS LOS Warehouse Management System.

This software is licensed under the GNU General Public License.

myWMS is a professional open source WMS (Warehouse Management System) published under the GPL license. It is running in industrial 24/7 environments and supports the basic processes of a warehouse.

The software is written in a multitear JEE architecture.
The server component is running in a JEE application server like wildfly. Client applications are available for PC and mobile devices. 

This project is maintained by [krane.engineer](https://krane.engineer).


## Features

* Ready to use warehouse management system (WMS)
* Designed for manual driven warehouses
* Barcode/RFID identification, mobile terminals for paperless processes
* Modular/ SOA architecture
* Supported technologies: JEE8, Wildfly, Netbeans RCP, SOAP
* Supports Wildfly-18 and Java-11
* Open source software


## Original Sources

This fork is based on the latest public available version of the Logata SVN sources.
Another location of the original sources, but a little outdated, is located on [sourceforge.net](https://sourceforge.net/projects/mywmslos).

A good location with additional information of the preivous versions is [mywms.org](http://www.mywms.org).


## Changes within this fork

The first action is to set the project to an actual environment (2019)

- Support of Java 11 runtime
- Support of wildfly 18 version
- Support of netbeans 11 rich client
- Support of JasperReports 6
- Change build system from ant to maven
- Change version control from svn to git

The current sources and documetations are located on [github.com](https://github.com/wms2/mywms).

For mor detailed information about the changes have a look into the CHANGELOG file.


## Future changes

It is planned to keep the project on the current development and runtime platforms. At least the LTS releases of the Java platform and used tools like wildfly application server should be supported.

Have a look to the CHANGELOG file to get information about new changes.

Furthermore functional and technical enhancements will be added to the project.

__Here some ideas:__
This are only ideas of what could be done. There is no actual plan to do it at all nor a specific roadmap.
Please contribute to the project. Bring your ideas, code and modules to the project. Vote for new functionalities or write issues to communicate your ideas.

- Add images and attachments to master data objects
- Change-history
- Multiple locks on one entity
- More powerful sorting criteria in storage strategies
- More powerful criteria in storage location capacity configuration
- Configurable picking strategy
- Stocktaking orders for single unit loads
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
- Add versions and history to stored jasperreports
- Replenish strategies to distinguish between long term reserve and reserve in the near of a picking location
- Dialogs to consolidate stocks. Manual cleanup.
- Distinguish between active and inactive products


## Bugs, issues and contributions

Contributions and suggestions are welcome. 
If you have any idea of functional or technical enhancement, please contribute to the project.

And if you have any problem, please open an issue.

