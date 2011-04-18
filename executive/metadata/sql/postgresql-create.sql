-- drop the existing database
drop database executive_db;

-- create the test user
create user test password 'test';

-- create the database
create database executive_db owner test;
