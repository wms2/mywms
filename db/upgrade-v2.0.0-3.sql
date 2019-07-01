-- add state to stockunit 
ALTER TABLE stockunit ADD COLUMN state integer;
update stockunit set state = 300 where state is null;
update stockunit set state = 450 where state = 300 and entity_lock = 100;
update stockunit set state = 650 where state = 300 and entity_lock = 405;
ALTER TABLE stockunit ALTER COLUMN state set not null ;

-- add state to unitload
ALTER TABLE unitload ADD COLUMN state integer;
update unitload set state = 300 where state is null;
update unitload set state = 450 where state = 300 and entity_lock = 100;
update unitload set state = 650 where state = 300 and entity_lock = 405;
ALTER TABLE unitload ALTER COLUMN state set not null ;

-- remove entity_locks for goods out
update stockunit set entity_lock = 0 where state = 450 and entity_lock = 100;
update unitload set entity_lock = 0 where state = 450 and entity_lock = 100;
