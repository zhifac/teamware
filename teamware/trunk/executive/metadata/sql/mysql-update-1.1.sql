USE `@DB-NAME@`;
ALTER TABLE app_user Modify city varchar(50) NULL;
ALTER TABLE app_user Modify postal_code varchar(15) NULL;
ALTER TABLE app_user Modify version integer NULL;
