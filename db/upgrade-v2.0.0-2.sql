 
CREATE TABLE packagingunit (
	id bigint NOT NULL,
	additionalcontent character varying(255),
	created timestamp without time zone,
	entity_lock integer,
	modified timestamp without time zone,
	version integer NOT NULL,
	amount numeric(17,4) NOT NULL,
	"depth" numeric(15,2),
	height numeric(15,2),
	name character varying(255),
	packinglevel integer NOT NULL,
	width numeric(15,2),
	itemdata_id bigint NOT NULL
);


ALTER TABLE los_sysprop RENAME workstation TO propertycontext;
ALTER TABLE los_sysprop ALTER COLUMN propertycontext DROP NOT NULL;
ALTER TABLE los_sysprop RENAME groupname TO propertygroup;
ALTER TABLE los_sysprop RENAME syskey TO propertykey;
ALTER TABLE los_sysprop RENAME sysvalue TO propertyvalue;
ALTER TABLE los_sysprop ALTER COLUMN description type varchar(2000);
update los_sysprop set propertycontext = null where propertycontext='DEFAULT';
ALTER TABLE los_sysprop DROP COLUMN hidden;

ALTER TABLE los_sysprop rename TO systemproperty;

ALTER TABLE systemproperty
	DROP CONSTRAINT los_sysprop_pkey;

ALTER TABLE systemproperty
	ADD CONSTRAINT systemproperty_pkey PRIMARY KEY (id);

ALTER TABLE itemdata
	ADD COLUMN defaultpackagingunit_id bigint;

ALTER TABLE itemdatanumber
	ADD COLUMN packagingunit_id bigint;

ALTER TABLE stockunit
	ADD COLUMN packagingunit_id bigint;

ALTER TABLE packagingunit
	ADD CONSTRAINT packagingunit_pkey PRIMARY KEY (id);

ALTER TABLE itemdata
	ADD CONSTRAINT fkq1ic5hb52clkae89oj2xufigx FOREIGN KEY (defaultpackagingunit_id) REFERENCES public.packagingunit(id);

ALTER TABLE itemdatanumber
	ADD CONSTRAINT fkcvclm8s2kogim65q5vt24c5aw FOREIGN KEY (packagingunit_id) REFERENCES public.packagingunit(id);

ALTER TABLE packagingunit
	ADD CONSTRAINT fkfgp26kuwh5yhy23wf3b9c69aq FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE stockunit
	ADD CONSTRAINT fkgle6vhopupmtunxrlm3v8q3ys FOREIGN KEY (packagingunit_id) REFERENCES public.packagingunit(id);

ALTER TABLE systemproperty
	ADD CONSTRAINT uke8rtndo649std4yerhe7kv5u0 UNIQUE (propertykey, propertycontext, client_id);

ALTER TABLE systemproperty
	DROP CONSTRAINT fkbne30dcnyty9r016sh5glxukx;

ALTER TABLE systemproperty
	ADD CONSTRAINT fk94kodx5gfhgnlbslyba5yv9ki FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

