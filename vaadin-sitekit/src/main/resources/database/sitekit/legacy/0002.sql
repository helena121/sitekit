\connect site

ALTER TABLE user_ ADD EMAILADDRESSVALIDATED BOOLEAN;
UPDATE user_ SET EMAILADDRESSVALIDATED = true;
ALTER TABLE user_ ALTER COLUMN emailaddressvalidated SET NOT NULL;

INSERT INTO schemaversion VALUES (NOW(), 'bare', '0002');
