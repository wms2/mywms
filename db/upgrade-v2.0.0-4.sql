-- only one journal
CREATE TABLE inventoryjournal (
        id bigint NOT NULL,
        additionalcontent character varying(255),
        created timestamp without time zone,
        entity_lock integer,
        modified timestamp without time zone,
        version integer NOT NULL,
        activitycode character varying(255),
        amount numeric(17,4),
        bestbefore timestamp without time zone,
        fromstoragelocation character varying(255),
        fromunitload character varying(255),
        lotnumber character varying(255),
        operator character varying(255),
        productname character varying(255),
        productnumber character varying(255),
        recordtype character varying(255) NOT NULL,
        scale integer NOT NULL,
        serialnumber character varying(255),
        stockunitamount numeric(17,4),
        tostoragelocation character varying(255),
        tounitload character varying(255),
        unitloadtype character varying(255),
        unitname character varying(255),
        client_id bigint NOT NULL
);

ALTER TABLE inventoryjournal
        ADD CONSTRAINT inventoryjournal_pkey PRIMARY KEY (id);

ALTER TABLE inventoryjournal
        ADD CONSTRAINT fkq730my0fcnfuhie8maafo8bli FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

insert into inventoryjournal (
    id,additionalcontent,created,entity_lock,modified,version,activitycode,
    operator,recordtype,client_id,unitloadtype,
    amount,stockunitamount,
    fromstoragelocation,tostoragelocation,fromunitload,tounitload,
    lotnumber,productnumber,scale,serialnumber)
select
    id,additionalcontent,created,entity_lock,modified,version,activitycode,
    operator,type,client_id,unitloadtype,
    amount,amountstock,
    fromstoragelocation,tostoragelocation,fromunitload,tounitload,
    lot,itemdata,scale,serialnumber
from los_stockrecord
;


insert into inventoryjournal (
    id,additionalcontent,created,entity_lock,modified,version,activitycode,
    operator,recordtype,client_id,unitloadtype,scale,
    fromstoragelocation,tostoragelocation,fromunitload,tounitload)
select
    id,additionalcontent,created,entity_lock,modified,version,activitycode,
    operator,recordtype,client_id,unitloadtype,0,
    fromlocation,tolocation,label,label
from los_ul_record
;
  
update inventoryjournal set recordtype = 'CREATED' where recordtype = 'STOCK_CREATED';
update inventoryjournal set recordtype = 'CHANGED' where recordtype = 'STOCK_SPLITTED';
update inventoryjournal set recordtype = 'CREATED' where recordtype = 'STOCK_ALTERED';
update inventoryjournal set recordtype = 'REMOVED' where recordtype = 'STOCK_REMOVED';
update inventoryjournal set recordtype = 'TRANSFERED' where recordtype = 'STOCK_TRANSFERRED';
update inventoryjournal set recordtype = 'COUNTED' where recordtype = 'STOCK_COUNTED';


	
	
