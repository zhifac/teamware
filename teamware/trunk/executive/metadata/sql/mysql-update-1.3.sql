USE `@DB-NAME@`;
-- Variables for role identifiers
select (@super:=id) from role where name = 'superadmin';
select (@admin:=id) from role where name = 'admin';
select (@man:=id) from role where name = 'manager';
select (@curator:=id) from role where name = 'curator';
select (@ann:=id) from role where name = 'annotator';
select (@anon:=id) from role where name = 'ROLE_ANONYMOUS';

delete FROM user_role where role_id=@curator;

DROP TABLE IF EXISTS `resource_role`;

DROP TABLE IF EXISTS `resource`;

delete FROM role where id=@curator;
delete FROM role where id=@anon;

CREATE TABLE `resource` (
  `id` bigint(20) NOT NULL auto_increment,
  `url` varchar(100) NOT NULL,
  `description` varchar(255) NOT NULL,
  `service_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `url` (`url`),
  KEY `FKEBABC40E3A20508D` (`service_id`),
  CONSTRAINT `FKEBABC40E3A20508D` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `resource_role` (
  `resource_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`role_id`,`resource_id`),
  KEY `FK3A62CE071B065BE7` (`role_id`),
  KEY `FK3A62CE075FE1D667` (`resource_id`),
  CONSTRAINT `FK3A62CE071B065BE7` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK3A62CE075FE1D667` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Dumping data for table `resource`
--

/*!40000 ALTER TABLE `resource` DISABLE KEYS */;
INSERT INTO `resource` (`id`,`url`,`description`,`service_id`) VALUES 
 (1,'/users.html*','Users List Page',2),
 (2,'/activeUsers.html*','list all active users',1),
 (3,'/roles.html*','Roles List Page',1),
 (4,'/resources.html*','Resources List Page',1),
 (5,'/reload.html*','Reload Page',1),
 (6,'/flush.html*','Flush Cache Page',1),
 (7,'/clickstreams.jsp*','Click Streams Page',1),
 (8,'/processDefinitionList.html?method=list*','Process Definitions List Page',6),
 (9,'/processInstanceList.html?method=listAll*','All Process Instance List Page',6),
 (10,'/taskInstanceList.html?method=list*','Task Instance List Page',6),
 (11,'/taskInstanceList.html?method=listByActor*','Pending Task Instance Page',6),
 (12,'/corpora.html*','DocService Home Page',5),
 (13,'/editProfile.html*','User Profile Edit Page',2),
 (14,'/mainMenu.html*','Main Menu Page',3),
 (15,'/passwordHint.html*','Password Hint Page',2),
 (16,'/signup.html*','SignUp Page',2),
 (17,'/helpInfo.html*','Help Page',4),
 (18,'/**/*.html*','All the Pages',3),
 (19,'/workflowMenu.html*','Work Flow Home Page',6),
 (20,'/scripts/engine.jsp*','Engine Page',3),
 (21,'/download.html*','Download',4),
 (22,'/corpus.html?method=downloadCorpus*','Download Corpus',5),
 (23,'/ontologyRepositoryList.html*','Ontology Repository List',8),
 (24,'/services.html*','Services List',1),
 (25,'/scripts/util.jsp*','DWR Util Page',3),
 (26,'/schemas.html*','Annotation Schemas',7),
 (27,'/annotator-gui/*','Annotator GUI URL',7),
 (28,'/forum.html*','Forum',4),
 (29,'/adm*','Forum Administration',4),
 (30,'/supportMenu.html*','Support Menu',3),
 (31,'/chat.html*','Chat',4),
 (33,'/resourceMenu.html*','Resource Menu',7),
 (34,'/annotationServices.html*','Annotation Services',7),
 (35,'/projects.html?method=listAll*','Workflow Templates',6),
 (36,'/datastore.html?method=downloadDS*','Download Datastore',5),
 (37,'/aMenu.html*','Admin Menu',1),
 (38,'/loadProject.html?method=load*','New Process Instance',6);
/*!40000 ALTER TABLE `resource` ENABLE KEYS */;


--
-- Dumping data for table `resource_role`
--

/*!40000 ALTER TABLE `resource_role` DISABLE KEYS */;
INSERT INTO `resource_role` (`resource_id`,`role_id`) VALUES 
 (1,@super),
 (2,@super),
 (3,@super),
 (4,@super),
 (5,@super),
 (6,@super),
 (7,@super),
 (8,@super),
 (9,@super),
 (10,@super),
 (11,@super),
 (12,@super),
 (13,@super),
 (14,@super),
 (15,@super),
 (16,@super),
 (17,@super),
 (18,@super),
 (19,@super),
 (20,@super),
 (21,@super),
 (22,@super),
 (23,@super),
 (24,@super),
 (25,@super),
 (26,@super),
 (28,@super),
 (29,@super),
 (30,@super),
 (31,@super),
 (33,@super),
 (34,@super),
 (35,@super),
 (36,@super),
 (37,@super),
 (38,@super),
 (1,@admin),
 (2,@admin),
 (5,@admin),
 (6,@admin),
 (7,@admin),
 (9,@admin),
 (10,@admin),
 (11,@admin),
 (12,@admin),
 (13,@admin),
 (14,@admin),
 (15,@admin),
 (16,@admin),
 (17,@admin),
 (18,@admin),
 (19,@admin),
 (20,@admin),
 (21,@admin),
 (22,@admin),
 (23,@admin),
 (25,@admin),
 (26,@admin),
 (28,@admin),
 (29,@admin),
 (30,@admin),
 (31,@admin),
 (33,@admin),
 (34,@admin),
 (35,@admin),
 (36,@admin),
 (37,@admin),
 (38,@admin),
 (2,@man),
 (9,@man),
 (10,@man),
 (11,@man),
 (12,@man),
 (13,@man),
 (14,@man),
 (15,@man),
 (16,@man),
 (17,@man),
 (18,@man),
 (19,@man),
 (20,@man),
 (21,@man),
 (22,@man),
 (25,@man),
 (26,@man),
 (28,@man),
 (30,@man),
 (31,@man),
 (33,@man),
 (34,@man),
 (35,@man),
 (36,@man),
 (38,@man),
 (13,@ann),
 (14,@ann),
 (15,@ann),
 (16,@ann),
 (17,@ann),
 (18,@ann),
 (20,@ann),
 (21,@ann),
 (25,@ann),
 (27,@ann),
 (28,@ann),
 (30,@ann),
 (31,@ann);
 
 update project set version=1

-- Add entries for new default GaSes
select (@gas:=id) from annotation_service_type where name = 'gas';

INSERT INTO `annotation_service`
    (`name`,`url`,`description`,`parameters`,`annotation_service_type_id`)
  VALUES
  ('annie gas','/annie-service/services/GATEService','ANNIE Annotation Service','<map><entry><string>parameterValue</string><string></string></entry><entry><string>parameterKey</string><string></string></entry><entry><string>asExtraMappings</string><string></string></entry><entry><string>asKey</string><string></string></entry><entry><string>asValue</string><string></string></entry></map>',@gas),
  ('reset gas','/reset-service/services/GATEService','Deletes all annotations','<map><entry><string>parameterValue</string><string></string></entry><entry><string>parameterKey</string><string></string></entry><entry><string>asExtraMappings</string><string></string></entry><entry><string>asKey</string><string></string></entry><entry><string>asValue</string><string></string></entry></map>',@gas);
