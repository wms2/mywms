
ALTER TABLE los_replenishorder
	ADD COLUMN requestedrack character varying(255);
update los_replenishorder set requestedrack = (select los_rack.rname from los_rack where los_rack.id=requestedrack_id);
ALTER TABLE los_replenishorder
	DROP COLUMN requestedrack_id;

ALTER TABLE los_storagelocationtype RENAME sltname TO name;

ALTER TABLE los_storloc RENAME type_id TO locationtype_id;
ALTER TABLE los_storloc RENAME cluster_id TO locationcluster_id;
ALTER TABLE los_storloc RENAME currenttcc TO currenttypecapacityconstraint_id;
ALTER TABLE los_storloc	ADD COLUMN rack character varying(255);
update los_storloc set rack = (select los_rack.rname from los_rack where los_rack.id=rack_id);
ALTER TABLE los_storloc DROP COLUMN rack_id;

ALTER TABLE los_typecapacityconstraint RENAME storagelocationtype_id TO locationtype_id;

ALTER TABLE mywms_area ADD COLUMN usages character varying(255);
update mywms_area set usages = '';
update mywms_area set usages = usages||',GOODS_IN' where useforgoodsin=true;
update mywms_area set usages = usages||',GOODS_OUT' where useforgoodsout=true;
update mywms_area set usages = usages||',PICKING' where useforpicking=true;
update mywms_area set usages = usages||',REPLENISH' where useforreplenish=true;
update mywms_area set usages = usages||',STORAGE' where useforstorage=true;
update mywms_area set usages = usages||',TRANSFER' where usefortransfer=true;
ALTER TABLE mywms_area
	DROP COLUMN dtype,
	DROP COLUMN useforgoodsin,
	DROP COLUMN useforgoodsout,
	DROP COLUMN useforpicking,
	DROP COLUMN useforreplenish,
	DROP COLUMN useforstorage,
	DROP COLUMN usefortransfer,
	DROP COLUMN client_id;

ALTER TABLE mywms_itemdata RENAME item_nr TO number;
ALTER TABLE mywms_itemdata RENAME descr TO description;
ALTER TABLE mywms_itemdata ALTER COLUMN description type character varying(2000);
ALTER TABLE mywms_itemdata RENAME serialrectype TO serialnorecordtype;
ALTER TABLE mywms_itemdata RENAME rest_usage_gi TO shelflife;
ALTER TABLE mywms_itemdata RENAME defultype_id TO defaultunitloadtype_id;
ALTER TABLE mywms_itemdata RENAME handlingunit_id TO itemunit_id;
ALTER TABLE mywms_itemdata ADD COLUMN defaultstoragestrategy_id bigint;

ALTER TABLE mywms_itemunit RENAME unitname TO name;

ALTER TABLE mywms_unitload RENAME location_index TO index;
ALTER TABLE mywms_unitload RENAME carrier TO iscarrier;
ALTER TABLE mywms_unitload RENAME type_id TO unitloadtype_id;
ALTER TABLE mywms_unitload DROP COLUMN dtype;

ALTER TABLE mywms_zone
	DROP COLUMN client_id,
	ADD COLUMN nextzone_id bigint;

ALTER TABLE mywms_area rename TO area;
ALTER TABLE mywms_itemdata rename TO itemdata;
ALTER TABLE mywms_itemunit rename TO itemunit;
ALTER TABLE mywms_lot rename TO lot;
ALTER TABLE mywms_stockunit rename TO stockunit;
ALTER TABLE mywms_unitload rename TO unitload;
ALTER TABLE mywms_unitloadtype rename TO unitloadtype;
ALTER TABLE mywms_zone rename TO zone;

ALTER TABLE los_itemdata_number rename TO itemdatanumber;
ALTER TABLE los_locationcluster rename TO locationcluster;
ALTER TABLE los_storagelocationtype rename TO locationtype;
ALTER TABLE los_storagestrat rename TO storagestrategy; 
ALTER TABLE los_storloc rename TO storagelocation;
ALTER TABLE los_typecapacityconstraint rename TO typecapacityconstraint;

DROP TABLE los_rack;


ALTER TABLE itemdatanumber
	DROP CONSTRAINT los_itemdata_number_pkey;

ALTER TABLE locationtype
	DROP CONSTRAINT los_storagelocationtype_pkey;

ALTER TABLE storagestrategy
	DROP CONSTRAINT los_storagestrat_pkey;

ALTER TABLE typecapacityconstraint
	DROP CONSTRAINT los_typecapacityconstraint_pkey;

ALTER TABLE itemdata
	DROP CONSTRAINT fkh1ysnegaqptt40wl3xu88acq1;

ALTER TABLE itemdata
	DROP CONSTRAINT fkjtrm7hkcgmglcrsduk3lo5w1j;

ALTER TABLE itemdatanumber
	DROP CONSTRAINT uks51cdyy3inb9wg8p7omntfj3a;

ALTER TABLE itemdatanumber
	DROP CONSTRAINT fk1bfuj4q13hga0twf6dwsjq2ka;

ALTER TABLE itemdatanumber
	DROP CONSTRAINT fkeeh126fwcj3c4ifk6qk7ejri2;

ALTER TABLE itemunit
	DROP CONSTRAINT fkme7wbvampbhto8kgtbt168uwl;

ALTER TABLE locationcluster
	DROP CONSTRAINT uk_32ljdcfc9texdfh39rjouem4k;

ALTER TABLE los_avisreq
	DROP CONSTRAINT fkbralx1s8uninwv16vcan774g8;

ALTER TABLE los_avisreq
	DROP CONSTRAINT fkpgk84t2x38j3bt8x5hfpj4lj5;

ALTER TABLE los_bom
	DROP CONSTRAINT fka11uqu4t8osv14nxfm351p9cl;

ALTER TABLE los_bom
	DROP CONSTRAINT fkgur71e6ndv7demnrg1h2anf;

ALTER TABLE los_customerorder
	DROP CONSTRAINT fksc9e20qgmhn3hs1rlf1b3ier6;

ALTER TABLE los_customerpos
	DROP CONSTRAINT fk43ff85i6l6teopf1yjl0e1u6n;

ALTER TABLE los_customerpos
	DROP CONSTRAINT fkqvkd9jqco55c4jk5f8d59twng;

ALTER TABLE los_fixassgn
	DROP CONSTRAINT fkpxpybdjqdnl65okhffffvrdrk;

ALTER TABLE los_fixassgn
	DROP CONSTRAINT fktfthcrl3qrakeydwll6k0gwyp;

ALTER TABLE los_goodsreceipt
	DROP CONSTRAINT fka5phtwn0m3ryljxh5fc9qygx8;

ALTER TABLE los_grrposition
	DROP CONSTRAINT fkph3mwi7e1tj1no7u4up1f31l9;

ALTER TABLE los_orderstrat
	DROP CONSTRAINT fkgvtrbok1jdvj9n3p39ai3k5qd;

ALTER TABLE los_outpos
	DROP CONSTRAINT fkp6v6ysy88wkw940rsxlkng3gv;

ALTER TABLE los_outreq
	DROP CONSTRAINT fkc957lnb4g8dw20fvhonq8ebyp;

ALTER TABLE los_pickingorder
	DROP CONSTRAINT fkg1xjeaqs6crcukbe7mvkpf5du;

ALTER TABLE los_pickingpos
	DROP CONSTRAINT fk1chxygj5bdbll4xdywua3s17f;

ALTER TABLE los_pickingpos
	DROP CONSTRAINT fkg39071ioo44h7gjk0q8a7bepj;

ALTER TABLE los_pickingpos
	DROP CONSTRAINT fkp1tfa1wav83fhsq76avoaxqbr;

ALTER TABLE los_pickingunitload
	DROP CONSTRAINT fk9a3llpoxyw6gnkrdx3c03ma8o;

ALTER TABLE los_replenishorder
	DROP CONSTRAINT fk2tql86h8wx8xcw1nd2frsx7ft;

ALTER TABLE los_replenishorder
	DROP CONSTRAINT fk86y0ii98mwgv8jo2tldcsr821;

ALTER TABLE los_replenishorder
	DROP CONSTRAINT fkbf7rik0wm8v2jyjgullxgjogp;

ALTER TABLE los_replenishorder
	DROP CONSTRAINT fkq9apu4l59w15r0ts7gw0bu942;

ALTER TABLE los_replenishorder
	DROP CONSTRAINT fkqvsxr5iojb38tp6w3axbabm3q;

ALTER TABLE los_storagereq
	DROP CONSTRAINT fkbouo846osytaqqak1g1v5xys;

ALTER TABLE los_storagereq
	DROP CONSTRAINT fkoepbusqm6i2wc8enmk3yq29ws;

ALTER TABLE los_uladvice
	DROP CONSTRAINT fklc656k2at5j0hysj2ihyhgt0b;

ALTER TABLE los_uladvicepos
	DROP CONSTRAINT fkc9jsfttu3ir5bj9spbn66xfee;

ALTER TABLE los_uladvicepos
	DROP CONSTRAINT fkqi53hu0mv7ftrufc6kvm90u2q;

ALTER TABLE los_workingareapos
	DROP CONSTRAINT fkmpwlh4tpa7ifngbj9f5vc9f4k;

ALTER TABLE lot
	DROP CONSTRAINT uk1dquqp8irl3iv016945iwmsw;

ALTER TABLE lot
	DROP CONSTRAINT fkmum9nojlc2viw1s3al8bfrhxl;

ALTER TABLE lot
	DROP CONSTRAINT fkoi5k9f7xttll6lrx2aqob3x94;

ALTER TABLE stockunit
	DROP CONSTRAINT fk30u4j0segd7ynlt2fs6ifneoh;

ALTER TABLE stockunit
	DROP CONSTRAINT fk8hgiromwb24t9gbwl5y3662n;

ALTER TABLE stockunit
	DROP CONSTRAINT fk9jrmdmyjurv3sblktgos460vh;

ALTER TABLE stockunit
	DROP CONSTRAINT fkjxhkhcun5ky3nwml3d66qwi4i;

ALTER TABLE storagelocation
	DROP CONSTRAINT uk_emolvlq25quglbkx8qd5s8vhh;

ALTER TABLE storagelocation
	DROP CONSTRAINT fk1lb3uwrr906sm8ixs5ct9lt3e;

ALTER TABLE storagelocation
	DROP CONSTRAINT fkm8n23nfwosv6que8222id8op0;

ALTER TABLE storagelocation
	DROP CONSTRAINT fkr106k9wpeu2rflobq4iw841sy;

ALTER TABLE storagestrategy
	DROP CONSTRAINT uk_qrejohv6ou22mnsdephm1mp60;

ALTER TABLE storagestrategy
	DROP CONSTRAINT fkl6kn2aijx3qg5q6560glpmuyu;

ALTER TABLE typecapacityconstraint
	DROP CONSTRAINT fkkyjkr6n77dhrh4x6vdrar0xlv;

ALTER TABLE unitload
	DROP CONSTRAINT uk_pdjgcqlreqny7lsy5ip7jo43r;

ALTER TABLE unitload
	DROP CONSTRAINT fk7rxe89gn06h0m3t8of446h9gu;

ALTER TABLE unitload
	DROP CONSTRAINT fkbh9vchm4lvihtn4rkx9t2skl7;

ALTER TABLE unitload
	DROP CONSTRAINT fks7k00i0k7bxonknlnvk6xaupq;

ALTER TABLE unitloadtype
	DROP CONSTRAINT ukeifxf4sfjlf4wf3qw8tudqkpf;

ALTER TABLE itemdatanumber
	ADD CONSTRAINT itemdatanumber_pkey PRIMARY KEY (id);

ALTER TABLE locationtype
	ADD CONSTRAINT locationtype_pkey PRIMARY KEY (id);

ALTER TABLE storagestrategy
	ADD CONSTRAINT storagestrategy_pkey PRIMARY KEY (id);

ALTER TABLE typecapacityconstraint
	ADD CONSTRAINT typecapacityconstraint_pkey PRIMARY KEY (id);

ALTER TABLE area
	ADD CONSTRAINT uk_ko3jng7qbcbcntx2lutxgan0w UNIQUE (name);

ALTER TABLE itemdata
	ADD CONSTRAINT uk7gddr19nerfmulfoa4vip3csu UNIQUE (client_id, number);

ALTER TABLE itemdata
	ADD CONSTRAINT uk_33o1eiho4lcb2rdj7d3b0ry7p UNIQUE (number);

ALTER TABLE itemdata
	ADD CONSTRAINT fk79i61skp6okjveyk1o9via6f3 FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

ALTER TABLE itemdata
	ADD CONSTRAINT fkj5fkwah8gbtjc8b00vo85ijmc FOREIGN KEY (defaultstoragestrategy_id) REFERENCES public.storagestrategy(id);

ALTER TABLE itemdata
	ADD CONSTRAINT fkpuncnyf56vrqvybn53ch8ifl7 FOREIGN KEY (defaultunitloadtype_id) REFERENCES public.unitloadtype(id);

ALTER TABLE itemdata
	ADD CONSTRAINT fkq3lpu1hjms8gvgtmrlg0jdg9e FOREIGN KEY (zone_id) REFERENCES public.zone(id);

ALTER TABLE itemdata
	ADD CONSTRAINT fkrt4d0kmt2glyrv1pno7362lce FOREIGN KEY (itemunit_id) REFERENCES public.itemunit(id);

ALTER TABLE itemdatanumber
	ADD CONSTRAINT ukki2aylcfei235esq1fii8s9q1 UNIQUE (number, itemdata_id);

ALTER TABLE itemdatanumber
	ADD CONSTRAINT fkipwkuu4cywt4kp86xim5xo7fu FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE itemdatanumber
	ADD CONSTRAINT fkotb0sf9p18xdermdm89wx1qv2 FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

ALTER TABLE itemunit
	ADD CONSTRAINT uk_15km3tq170l7mgfqfa5ncxmkm UNIQUE (name);

ALTER TABLE itemunit
	ADD CONSTRAINT fkh4fovvdoutxjiw2g0pg3cijxi FOREIGN KEY (baseunit_id) REFERENCES public.itemunit(id);

ALTER TABLE locationcluster
	ADD CONSTRAINT uk_mw8yy44fxmijp4v0kawpi61hd UNIQUE (name);

ALTER TABLE locationtype
	ADD CONSTRAINT uk_jk8lxi2j9kt4l5jgmlsvvq7nn UNIQUE (name);

ALTER TABLE los_avisreq
	ADD CONSTRAINT fk3fo1lv3aq2yx0uqklv6h1eajg FOREIGN KEY (lot_id) REFERENCES public.lot(id);

ALTER TABLE los_avisreq
	ADD CONSTRAINT fkixsifdltie3ipjrr83qo34hgw FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE los_bom
	ADD CONSTRAINT fk2pvhtqlvxqn5rs1lj3x83pc01 FOREIGN KEY (parent_id) REFERENCES public.itemdata(id);

ALTER TABLE los_bom
	ADD CONSTRAINT fkqoyjcgwb44byxqbpcjqyg2pc0 FOREIGN KEY (child_id) REFERENCES public.itemdata(id);

ALTER TABLE los_customerorder
	ADD CONSTRAINT fkbv7qetb5m6kqtxoh7llmmp62u FOREIGN KEY (destination_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_customerpos
	ADD CONSTRAINT fkn9a8ghri6lry04maxbigkk05a FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE los_customerpos
	ADD CONSTRAINT fkqpc72s51sageoiuvq3wt9phvn FOREIGN KEY (lot_id) REFERENCES public.lot(id);

ALTER TABLE los_fixassgn
	ADD CONSTRAINT fk1t7649vir9pvd3w6wj34lkl4f FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE los_fixassgn
	ADD CONSTRAINT fkntvd8id14uslhwk6miqalw1u6 FOREIGN KEY (assignedlocation_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_goodsreceipt
	ADD CONSTRAINT fkrw1lhwyvs4rajwhtct655bxf7 FOREIGN KEY (goodsinlocation_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_grrposition
	ADD CONSTRAINT fkganaauhog3xc3v5fw0fqsu6ud FOREIGN KEY (stockunit_id) REFERENCES public.stockunit(id);

ALTER TABLE los_orderstrat
	ADD CONSTRAINT fkp2vmnsj6goulr0d7b297vuoku FOREIGN KEY (defaultdestination_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_outpos
	ADD CONSTRAINT fk3mwimwj731tjw8vlow6rc39lw FOREIGN KEY (source_id) REFERENCES public.unitload(id);

ALTER TABLE los_outreq
	ADD CONSTRAINT fkacjiqi2321m5blmai2fi5s3k1 FOREIGN KEY (outlocation_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_pickingorder
	ADD CONSTRAINT fkbe3ikrf9hq804aim456qv6i93 FOREIGN KEY (destination_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_pickingpos
	ADD CONSTRAINT fk6dgc0y1okrc7vlb8h7clonjko FOREIGN KEY (pickfromstockunit_id) REFERENCES public.stockunit(id);

ALTER TABLE los_pickingpos
	ADD CONSTRAINT fkbr3p87eafo1ob4pppv7trxbsd FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE los_pickingpos
	ADD CONSTRAINT fkipgxrd76bqwnbqqf5c6yofu54 FOREIGN KEY (lotpicked_id) REFERENCES public.lot(id);

ALTER TABLE los_pickingunitload
	ADD CONSTRAINT fkdv5hmsdd6pfgx0h9nfm7tba0h FOREIGN KEY (unitload_id) REFERENCES public.unitload(id);

ALTER TABLE los_replenishorder
	ADD CONSTRAINT fk4ma7d9hm14em33fogaoapxbk4 FOREIGN KEY (stockunit_id) REFERENCES public.stockunit(id);

ALTER TABLE los_replenishorder
	ADD CONSTRAINT fka0mxdcem217urovkdavxh7wic FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE los_replenishorder
	ADD CONSTRAINT fkcritcxb6j7dvgc4ebqh0s9d9 FOREIGN KEY (requestedlocation_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_replenishorder
	ADD CONSTRAINT fksboh7xb50v4cyrxp8wfcgy519 FOREIGN KEY (lot_id) REFERENCES public.lot(id);

ALTER TABLE los_replenishorder
	ADD CONSTRAINT fkt6qno8a3xypb1k4bqmk38nqqu FOREIGN KEY (destination_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_storagereq
	ADD CONSTRAINT fkcnj1vfeonm3nprq2b977qxj41 FOREIGN KEY (destination_id) REFERENCES public.storagelocation(id);

ALTER TABLE los_storagereq
	ADD CONSTRAINT fkgnbn02pf1m0x7v7y014bkin7t FOREIGN KEY (unitload_id) REFERENCES public.unitload(id);

ALTER TABLE los_uladvice
	ADD CONSTRAINT fkeeif24tn2snupqsky3q5yxlq7 FOREIGN KEY (unitloadtype_id) REFERENCES public.unitloadtype(id);

ALTER TABLE los_uladvicepos
	ADD CONSTRAINT fke5b1hnj2qn8y3j1pgs4qmimjm FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE los_uladvicepos
	ADD CONSTRAINT fkes75m29iasos2klmfabhxf3fc FOREIGN KEY (lot_id) REFERENCES public.lot(id);

ALTER TABLE los_workingareapos
	ADD CONSTRAINT fk3sccxgjaffmdog8a6yomf33hp FOREIGN KEY (cluster_id) REFERENCES public.locationcluster(id);

ALTER TABLE lot
	ADD CONSTRAINT ukiv57hwwitjmrc44rsi4gcrih4 UNIQUE (name, itemdata_id);

ALTER TABLE lot
	ADD CONSTRAINT fkajxc097bmaf203jmtg0cj4nyk FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE lot
	ADD CONSTRAINT fke1en5ww5iwinsdc9o6vhgjfnt FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

ALTER TABLE stockunit
	ADD CONSTRAINT fk4bvivow1t00u5mfo02yggqer3 FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

ALTER TABLE stockunit
	ADD CONSTRAINT fkbl59stghjvryxmki4a2he2n4b FOREIGN KEY (lot_id) REFERENCES public.lot(id);

ALTER TABLE stockunit
	ADD CONSTRAINT fkm4pxfing959u10q7gg33vf4as FOREIGN KEY (itemdata_id) REFERENCES public.itemdata(id);

ALTER TABLE stockunit
	ADD CONSTRAINT fkpsjs9o85unc4j8mb3phwwmqpu FOREIGN KEY (unitload_id) REFERENCES public.unitload(id);

ALTER TABLE storagelocation
	ADD CONSTRAINT uk_6va8u50ui5i8gk516co4xm39n UNIQUE (name);

ALTER TABLE storagelocation
	ADD CONSTRAINT fk2i6qq6obym379t2nq2fu5fofv FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

ALTER TABLE storagelocation
	ADD CONSTRAINT fk6wse36d5f3dyeqsx8yblf7022 FOREIGN KEY (locationtype_id) REFERENCES public.locationtype(id);

ALTER TABLE storagelocation
	ADD CONSTRAINT fkey1yulw46e6y1cf6jwn6disd7 FOREIGN KEY (zone_id) REFERENCES public.zone(id);

ALTER TABLE storagelocation
	ADD CONSTRAINT fki5vl1yobysf1uaegx97mxct40 FOREIGN KEY (locationcluster_id) REFERENCES public.locationcluster(id);

ALTER TABLE storagelocation
	ADD CONSTRAINT fko9lkunkit2d5f905ewmujrd2v FOREIGN KEY (currenttypecapacityconstraint_id) REFERENCES public.typecapacityconstraint(id);

ALTER TABLE storagelocation
	ADD CONSTRAINT fkrdl4fyxriios2qbaa2xvxhk8d FOREIGN KEY (area_id) REFERENCES public.area(id);

ALTER TABLE storagestrategy
	ADD CONSTRAINT uk_k9pl1os27dv52qtvsvtkwse64 UNIQUE (name);

ALTER TABLE storagestrategy
	ADD CONSTRAINT fkjtp6l9om9nmc3i2s91cv2npfc FOREIGN KEY (zone_id) REFERENCES public.zone(id);

ALTER TABLE typecapacityconstraint
	ADD CONSTRAINT uk43l3ten33a99v38qti8erayyy UNIQUE (locationtype_id, unitloadtype_id);

ALTER TABLE typecapacityconstraint
	ADD CONSTRAINT fk6x9kfwcc80apt8mxu4bv7o65e FOREIGN KEY (locationtype_id) REFERENCES public.locationtype(id);

ALTER TABLE typecapacityconstraint
	ADD CONSTRAINT fkfqfg3xxgowimutam6ngi3nbcq FOREIGN KEY (unitloadtype_id) REFERENCES public.unitloadtype(id);

ALTER TABLE unitload
	ADD CONSTRAINT uk_o6dvblig2h3hhgprjfnfpw32n UNIQUE (labelid);

ALTER TABLE unitload
	ADD CONSTRAINT fk355vsisx395stc52xqiy9p9uc FOREIGN KEY (unitloadtype_id) REFERENCES public.unitloadtype(id);

ALTER TABLE unitload
	ADD CONSTRAINT fk3n4u799orsde8ni6o8rbjccyu FOREIGN KEY (client_id) REFERENCES public.mywms_client(id);

ALTER TABLE unitload
	ADD CONSTRAINT fkprkq785coq6pj338s0mawxlvm FOREIGN KEY (storagelocation_id) REFERENCES public.storagelocation(id);

ALTER TABLE unitload
	ADD CONSTRAINT fksass5hqn98ptrck5724cgbfit FOREIGN KEY (carrierunitload_id) REFERENCES public.unitload(id);

ALTER TABLE unitloadtype
	ADD CONSTRAINT uk_3khd4s5g56ylxy2sd8yvtg5i0 UNIQUE (name);

ALTER TABLE "zone"
	ADD CONSTRAINT uk_g4d41s4c3kkaybidpcps0w0xw UNIQUE (name);

ALTER TABLE "zone"
	ADD CONSTRAINT fk48vkaphj824118xr3nlxj5lkm FOREIGN KEY (nextzone_id) REFERENCES public.zone(id);
