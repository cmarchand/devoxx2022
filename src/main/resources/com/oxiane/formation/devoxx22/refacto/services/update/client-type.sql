# create column client.nature

DELIMITER ;
ALTER TABLE CLIENT ADD NATURE VARCHAR(15);
UPDATE CLIENT SET NATURE='PARTICULIER';
ALTER TABLE CLIENT ALTER NATURE SET NOT NULL;