ALTER TABLE los_customerorder RENAME TO deliveryorder;
ALTER TABLE deliveryorder RENAME delivery TO deliverydate;
ALTER TABLE deliveryorder DROP COLUMN dtype;
ALTER TABLE deliveryorder RENAME number TO ordernumber;
ALTER TABLE deliveryorder RENAME strategy_id TO orderstrategy_id;
ALTER TABLE deliveryorder ADD COLUMN carriername character varying(255);
ALTER TABLE deliveryorder ADD COLUMN finished timestamp without time zone;
ALTER TABLE deliveryorder ADD COLUMN pickinghint character varying(2000);
ALTER TABLE deliveryorder ADD COLUMN started timestamp without time zone;
ALTER TABLE deliveryorder ADD COLUMN volume numeric(19,6);
ALTER TABLE deliveryorder ADD COLUMN weight numeric(16,3);


ALTER TABLE los_customerpos RENAME TO deliveryorderline;
ALTER TABLE deliveryorderline RENAME amountpicked TO pickedamount;
ALTER TABLE deliveryorderline RENAME number TO linenumber;
ALTER TABLE deliveryorderline RENAME order_id TO deliveryorder_id;
ALTER TABLE deliveryorderline DROP COLUMN index;
ALTER TABLE deliveryorderline DROP COLUMN partitionallowed;
ALTER TABLE deliveryorderline ADD COLUMN externalnumber character varying(255);
ALTER TABLE deliveryorderline ADD COLUMN finished timestamp without time zone;
ALTER TABLE deliveryorderline ADD COLUMN pickinghint character varying(2000);
ALTER TABLE deliveryorderline ADD COLUMN started timestamp without time zone;


ALTER TABLE los_fixassgn RENAME TO fixassignment;
ALTER TABLE fixassignment RENAME desiredamount TO maxamount;
ALTER TABLE fixassignment ALTER COLUMN maxamount DROP NOT NULL;
ALTER TABLE fixassignment RENAME assignedlocation_id TO storagelocation_id;
ALTER TABLE fixassignment ADD COLUMN maxpickamount numeric(17,4);
ALTER TABLE fixassignment ADD COLUMN minamount numeric(17,4);
ALTER TABLE fixassignment ADD COLUMN orderindex integer;
UPDATE fixassignment SET orderindex=0;
ALTER TABLE fixassignment ALTER COLUMN orderindex SET NOT NULL;

ALTER TABLE los_orderstrat RENAME TO orderstrategy;
ALTER TABLE orderstrategy RENAME preferunopened TO prefercomplete;
ALTER TABLE orderstrategy RENAME creategoodsoutorder TO createshippingorder;
ALTER TABLE orderstrategy RENAME prefermatchingstock TO prefermatching;
ALTER TABLE orderstrategy ADD COLUMN completeonly boolean;
ALTER TABLE orderstrategy ADD COLUMN createpackingorder boolean;
ALTER TABLE orderstrategy ADD COLUMN createtypeorders boolean;
UPDATE orderstrategy SET completeonly=false,createpackingorder=false,createtypeorders=false;
ALTER TABLE orderstrategy ALTER COLUMN completeonly SET NOT NULL;
ALTER TABLE orderstrategy ALTER COLUMN createpackingorder SET NOT NULL;
ALTER TABLE orderstrategy ALTER COLUMN createtypeorders SET NOT NULL;

ALTER TABLE los_pickingorder RENAME TO pickingorder;
ALTER TABLE pickingorder RENAME manualcreation TO createfollowuppicks;
UPDATE pickingorder SET createfollowuppicks = not createfollowuppicks;
ALTER TABLE pickingorder RENAME strategy_id TO orderstrategy_id;
ALTER TABLE pickingorder RENAME number TO ordernumber;
ALTER TABLE pickingorder ADD COLUMN externalnumber character varying(255);
ALTER TABLE pickingorder ADD COLUMN finished timestamp without time zone;
ALTER TABLE pickingorder ADD COLUMN pickinghint character varying(2000);
ALTER TABLE pickingorder ADD COLUMN started timestamp without time zone;
ALTER TABLE pickingorder ADD COLUMN volume numeric(19,6);
ALTER TABLE pickingorder ADD COLUMN weight numeric(16,3);
ALTER TABLE pickingorder ADD COLUMN unitloadtype_id bigint;
ALTER TABLE pickingorder ADD COLUMN deliveryorder_id bigint;
UPDATE pickingorder SET deliveryorder_id = (SELECT id from deliveryorder WHERE deliveryorder.ordernumber=customerordernumber);
ALTER TABLE pickingorder DROP COLUMN customerordernumber;

	
ALTER TABLE los_pickingpos RENAME TO pickingorderline;
ALTER TABLE pickingorderline RENAME amountpicked TO pickedamount;
ALTER TABLE pickingorderline RENAME customerorderposition_id TO deliveryorderline_id;
ALTER TABLE pickingorderline RENAME strategy_id TO orderstrategy_id;
ALTER TABLE pickingorderline DROP COLUMN pickingordernumber;
ALTER TABLE pickingorderline ADD COLUMN finished timestamp without time zone;
ALTER TABLE pickingorderline ADD COLUMN pickinghint character varying(2000);
ALTER TABLE pickingorderline ADD COLUMN pickedlotnumber character varying(255);
UPDATE pickingorderline SET pickedlotnumber = (SELECT lot.name from lot WHERE lot.id=lotpicked_id);
ALTER TABLE pickingorderline DROP COLUMN lotpicked_id;

ALTER TABLE los_pickingunitload RENAME TO pickingunitload;
ALTER TABLE pickingunitload RENAME customerordernumber TO deliveryordernumber;

ALTER TABLE los_sequencenumber RENAME TO sequencenumber;
ALTER TABLE sequencenumber RENAME classname TO name;
ALTER TABLE sequencenumber RENAME sequencenumber TO counter;
ALTER TABLE sequencenumber ADD COLUMN id bigint;
UPDATE sequencenumber SET id = nextval('seqentities');
ALTER TABLE sequencenumber ALTER COLUMN id SET NOT NULL;
ALTER TABLE sequencenumber ADD COLUMN created timestamp without time zone;
ALTER TABLE sequencenumber ADD COLUMN modified timestamp without time zone;
ALTER TABLE sequencenumber ADD COLUMN additionalcontent character varying(255);
ALTER TABLE sequencenumber ADD COLUMN entity_lock integer;
ALTER TABLE sequencenumber ADD COLUMN startcounter bigint;
UPDATE sequencenumber SET startcounter = 1;
ALTER TABLE sequencenumber ALTER COLUMN startcounter SET NOT NULL;
UPDATE sequencenumber SET entity_lock = 0;
ALTER TABLE sequencenumber ADD COLUMN endcounter bigint;
UPDATE sequencenumber SET endcounter = 999999;
ALTER TABLE sequencenumber ALTER COLUMN endcounter SET NOT NULL;
ALTER TABLE sequencenumber ADD COLUMN format character varying(255);
UPDATE sequencenumber SET name='GoodsReceipt', format='WE %1$06d' WHERE name='de.linogistix.los.inventory.model.LOSGoodsReceipt';
UPDATE sequencenumber SET name='Stocktaking', format='IV %1$08d',endcounter=99999999 WHERE name='de.linogistix.los.stocktaking.model.LOSStockTaking';
UPDATE sequencenumber SET name='DeliveryOrder', format='ORDER %1$06d' WHERE name='de.linogistix.los.inventory.model.LOSCustomerOrder';
UPDATE sequencenumber SET name='ReplenishOrder', format='REPL %1$06d' WHERE name='de.linogistix.los.inventory.pick.model.PickReceipt';
UPDATE sequencenumber SET name='PickingOrder', format='PICK %1$06d' WHERE name='de.linogistix.los.inventory.model.LOSPickingOrder';
UPDATE sequencenumber SET name='Shipment', format='GOUT %1$06d' WHERE name='de.linogistix.los.inventory.model.LOSGoodsOutRequest';
UPDATE sequencenumber SET name='Inventory', format='IMAN %1$06d' WHERE name='de.linogistix.los.inventory.ws.ManageInventory';
UPDATE sequencenumber SET name='Advice', format='AVIS %1$06d' WHERE name='de.linogistix.los.inventory.model.LOSAdvice';
UPDATE sequencenumber SET name='UnitLoad', format='%1$06d' WHERE name='org.mywms.model.UnitLoadType';
UPDATE sequencenumber SET name='Storage', format='STORE %1$06d' WHERE name='de.linogistix.los.inventory.model.LOSStorageRequest';
UPDATE sequencenumber SET name='UnitLoadAdvice', format='UAV %1$08d',endcounter=99999999 WHERE name='de.linogistix.los.inventory.model.LOSUnitLoadAdvice';
UPDATE sequencenumber SET format='%1$06d' WHERE format is null;

ALTER TABLE sequencenumber DROP CONSTRAINT los_sequencenumber_pkey;
ALTER TABLE sequencenumber ADD CONSTRAINT sequencenumber_pkey PRIMARY KEY (id);
ALTER TABLE sequencenumber ADD CONSTRAINT uk_in7h0g6t2vt4u9fd1m61fm4d4 UNIQUE (name);

ALTER TABLE unitloadtype ADD COLUMN usages character varying(255);

