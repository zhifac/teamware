create database if not exists `@DB-NAME@`;
USE `@DB-NAME@`;

create table JBPM_ACTION (ID_ bigint not null auto_increment, class char(1) not null, NAME_ varchar(255), ISPROPAGATIONALLOWED_ bit, ACTIONEXPRESSION_ varchar(255), ISASYNC_ bit, REFERENCEDACTION_ bigint, ACTIONDELEGATION_ bigint, EVENT_ bigint, PROCESSDEFINITION_ bigint, TIMERNAME_ varchar(255), DUEDATE_ varchar(255), REPEAT_ varchar(255), TRANSITIONNAME_ varchar(255), TIMERACTION_ bigint, EXPRESSION_ text, EVENTINDEX_ integer, EXCEPTIONHANDLER_ bigint, EXCEPTIONHANDLERINDEX_ integer, primary key (ID_)) engine=InnoDB;
create table JBPM_BYTEARRAY (ID_ bigint not null auto_increment, NAME_ varchar(255), FILEDEFINITION_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_BYTEBLOCK (PROCESSFILE_ bigint not null, BYTES_ blob, INDEX_ integer not null, primary key (PROCESSFILE_, INDEX_)) engine=InnoDB;
create table JBPM_COMMENT (ID_ bigint not null auto_increment, VERSION_ integer not null, ACTORID_ varchar(255), TIME_ datetime, MESSAGE_ text, TOKEN_ bigint, TASKINSTANCE_ bigint, TOKENINDEX_ integer, TASKINSTANCEINDEX_ integer, primary key (ID_)) engine=InnoDB;
create table JBPM_DECISIONCONDITIONS (DECISION_ bigint not null, TRANSITIONNAME_ varchar(255), EXPRESSION_ varchar(255), INDEX_ integer not null, primary key (DECISION_, INDEX_)) engine=InnoDB;
create table JBPM_DELEGATION (ID_ bigint not null auto_increment, CLASSNAME_ text, CONFIGURATION_ text, CONFIGTYPE_ varchar(255), PROCESSDEFINITION_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_EVENT (ID_ bigint not null auto_increment, EVENTTYPE_ varchar(255), TYPE_ char(1), GRAPHELEMENT_ bigint, PROCESSDEFINITION_ bigint, NODE_ bigint, TRANSITION_ bigint, TASK_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_EXCEPTIONHANDLER (ID_ bigint not null auto_increment, EXCEPTIONCLASSNAME_ text, TYPE_ char(1), GRAPHELEMENT_ bigint, PROCESSDEFINITION_ bigint, GRAPHELEMENTINDEX_ integer, NODE_ bigint, TRANSITION_ bigint, TASK_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_ID_GROUP (ID_ bigint not null auto_increment, CLASS_ char(1) not null, NAME_ varchar(255), TYPE_ varchar(255), PARENT_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_ID_MEMBERSHIP (ID_ bigint not null auto_increment, CLASS_ char(1) not null, NAME_ varchar(255), ROLE_ varchar(255), USER_ bigint, GROUP_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_ID_PERMISSIONS (ENTITY_ bigint not null, CLASS_ varchar(255), NAME_ varchar(255), ACTION_ varchar(255)) engine=InnoDB;
create table JBPM_ID_USER (ID_ bigint not null auto_increment, CLASS_ char(1) not null, NAME_ varchar(255), EMAIL_ varchar(255), PASSWORD_ varchar(255), primary key (ID_)) engine=InnoDB;
create table JBPM_JOB (ID_ bigint not null auto_increment, CLASS_ char(1) not null, VERSION_ integer not null, DUEDATE_ datetime, PROCESSINSTANCE_ bigint, TOKEN_ bigint, TASKINSTANCE_ bigint, ISSUSPENDED_ bit, ISEXCLUSIVE_ bit, LOCKOWNER_ varchar(255), LOCKTIME_ datetime, EXCEPTION_ text, RETRIES_ integer, NAME_ varchar(255), REPEAT_ varchar(255), TRANSITIONNAME_ varchar(255), ACTION_ bigint, GRAPHELEMENTTYPE_ varchar(255), GRAPHELEMENT_ bigint, NODE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_LOG (ID_ bigint not null auto_increment, CLASS_ char(1) not null, INDEX_ integer, DATE_ datetime, TOKEN_ bigint, PARENT_ bigint, MESSAGE_ text, EXCEPTION_ text, ACTION_ bigint, NODE_ bigint, ENTER_ datetime, LEAVE_ datetime, DURATION_ bigint, NEWLONGVALUE_ bigint, TRANSITION_ bigint, CHILD_ bigint, SOURCENODE_ bigint, DESTINATIONNODE_ bigint, VARIABLEINSTANCE_ bigint, OLDBYTEARRAY_ bigint, NEWBYTEARRAY_ bigint, OLDDATEVALUE_ datetime, NEWDATEVALUE_ datetime, OLDDOUBLEVALUE_ double precision, NEWDOUBLEVALUE_ double precision, OLDLONGIDCLASS_ varchar(255), OLDLONGIDVALUE_ bigint, NEWLONGIDCLASS_ varchar(255), NEWLONGIDVALUE_ bigint, OLDSTRINGIDCLASS_ varchar(255), OLDSTRINGIDVALUE_ varchar(255), NEWSTRINGIDCLASS_ varchar(255), NEWSTRINGIDVALUE_ varchar(255), OLDLONGVALUE_ bigint, OLDSTRINGVALUE_ text, NEWSTRINGVALUE_ text, TASKINSTANCE_ bigint, TASKACTORID_ varchar(255), TASKOLDACTORID_ varchar(255), SWIMLANEINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_MODULEDEFINITION (ID_ bigint not null auto_increment, CLASS_ char(1) not null, NAME_ text, PROCESSDEFINITION_ bigint, STARTTASK_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_MODULEINSTANCE (ID_ bigint not null auto_increment, CLASS_ char(1) not null, VERSION_ integer not null, PROCESSINSTANCE_ bigint, TASKMGMTDEFINITION_ bigint, NAME_ varchar(255), primary key (ID_)) engine=InnoDB;
create table JBPM_NODE (ID_ bigint not null auto_increment, CLASS_ char(1) not null, NAME_ varchar(255), DESCRIPTION_ text, PROCESSDEFINITION_ bigint, ISASYNC_ bit, ISASYNCEXCL_ bit, ACTION_ bigint, SUPERSTATE_ bigint, SUBPROCNAME_ varchar(255), SUBPROCESSDEFINITION_ bigint, DECISIONEXPRESSION_ varchar(255), DECISIONDELEGATION bigint, SCRIPT_ bigint, SIGNAL_ integer, CREATETASKS_ bit, ENDTASKS_ bit, NODECOLLECTIONINDEX_ integer, primary key (ID_)) engine=InnoDB;
create table JBPM_POOLEDACTOR (ID_ bigint not null auto_increment, VERSION_ integer not null, ACTORID_ varchar(255), SWIMLANEINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_PROCESSDEFINITION (ID_ bigint not null auto_increment, CLASS_ char(1) not null, NAME_ varchar(255), DESCRIPTION_ text, VERSION_ integer, ISTERMINATIONIMPLICIT_ bit, STARTSTATE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_PROCESSINSTANCE (ID_ bigint not null auto_increment, VERSION_ integer not null, KEY_ varchar(255), START_ datetime, END_ datetime, ISSUSPENDED_ bit, PROCESSDEFINITION_ bigint, ROOTTOKEN_ bigint, SUPERPROCESSTOKEN_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_RUNTIMEACTION (ID_ bigint not null auto_increment, VERSION_ integer not null, EVENTTYPE_ varchar(255), TYPE_ char(1), GRAPHELEMENT_ bigint, PROCESSINSTANCE_ bigint, ACTION_ bigint, PROCESSINSTANCEINDEX_ integer, primary key (ID_)) engine=InnoDB;
create table JBPM_SWIMLANE (ID_ bigint not null auto_increment, NAME_ varchar(255), ACTORIDEXPRESSION_ varchar(255), POOLEDACTORSEXPRESSION_ varchar(255), ASSIGNMENTDELEGATION_ bigint, TASKMGMTDEFINITION_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_SWIMLANEINSTANCE (ID_ bigint not null auto_increment, VERSION_ integer not null, NAME_ varchar(255), ACTORID_ varchar(255), SWIMLANE_ bigint, TASKMGMTINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_TASK (ID_ bigint not null auto_increment, NAME_ varchar(255), DESCRIPTION_ text, PROCESSDEFINITION_ bigint, ISBLOCKING_ bit, ISSIGNALLING_ bit, CONDITION_ varchar(255), DUEDATE_ varchar(255), PRIORITY_ integer, ACTORIDEXPRESSION_ varchar(255), POOLEDACTORSEXPRESSION_ varchar(255), TASKMGMTDEFINITION_ bigint, TASKNODE_ bigint, STARTSTATE_ bigint, ASSIGNMENTDELEGATION_ bigint, SWIMLANE_ bigint, TASKCONTROLLER_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_TASKACTORPOOL (TASKINSTANCE_ bigint not null, POOLEDACTOR_ bigint not null, primary key (TASKINSTANCE_, POOLEDACTOR_)) engine=InnoDB;
create table JBPM_TASKCONTROLLER (ID_ bigint not null auto_increment, TASKCONTROLLERDELEGATION_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_TASKINSTANCE (ID_ bigint not null auto_increment, CLASS_ char(1) not null, VERSION_ integer not null, NAME_ varchar(255), DESCRIPTION_ text, ACTORID_ varchar(255), CREATE_ datetime, START_ datetime, END_ datetime, DUEDATE_ datetime, LASTOPENED_ datetime, LASTSAVED_ datetime, TIMEWORKEDON_ bigint, PRIORITY_ integer, ISCANCELLED_ bit, ISSUSPENDED_ bit, ISOPEN_ bit, ISSIGNALLING_ bit, ISBLOCKING_ bit, DOCUMENT_ varchar(255), ANNOTATIONSETNAME_ varchar(255), TASK_ bigint, TOKEN_ bigint, PROCINST_ bigint, SWIMLANINSTANCE_ bigint, TASKMGMTINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_TOKEN (ID_ bigint not null auto_increment, VERSION_ integer not null, NAME_ varchar(255), START_ datetime, END_ datetime, NODEENTER_ datetime, NEXTLOGINDEX_ integer, ISABLETOREACTIVATEPARENT_ bit, ISTERMINATIONIMPLICIT_ bit, ISSUSPENDED_ bit, LOCK_ varchar(255), NODE_ bigint, PROCESSINSTANCE_ bigint, PARENT_ bigint, SUBPROCESSINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_TOKENVARIABLEMAP (ID_ bigint not null auto_increment, VERSION_ integer not null, TOKEN_ bigint, CONTEXTINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_TRANSITION (ID_ bigint not null auto_increment, NAME_ varchar(255), DESCRIPTION_ text, PROCESSDEFINITION_ bigint, FROM_ bigint, TO_ bigint, CONDITION_ varchar(255), FROMINDEX_ integer, primary key (ID_)) engine=InnoDB;
create table JBPM_VARIABLEACCESS (ID_ bigint not null auto_increment, VARIABLENAME_ varchar(255), ACCESS_ varchar(255), MAPPEDNAME_ varchar(255), PROCESSSTATE_ bigint, TASKCONTROLLER_ bigint, INDEX_ integer, SCRIPT_ bigint, primary key (ID_)) engine=InnoDB;
create table JBPM_VARIABLEINSTANCE (ID_ bigint not null auto_increment, CLASS_ char(1) not null, VERSION_ integer not null, NAME_ varchar(255), CONVERTER_ char(1), TOKEN_ bigint, TOKENVARIABLEMAP_ bigint, PROCESSINSTANCE_ bigint, BYTEARRAYVALUE_ bigint, DATEVALUE_ datetime, DOUBLEVALUE_ double precision, LONGIDCLASS_ varchar(255), LONGVALUE_ bigint, STRINGIDCLASS_ varchar(255), STRINGVALUE_ text, TASKINSTANCE_ bigint, primary key (ID_)) engine=InnoDB;
create index IDX_ACTION_EVENT on JBPM_ACTION (EVENT_);
create index IDX_ACTION_ACTNDL on JBPM_ACTION (ACTIONDELEGATION_);
create index IDX_ACTION_PROCDF on JBPM_ACTION (PROCESSDEFINITION_);
alter table JBPM_ACTION add index FK_ACTION_EVENT (EVENT_), add constraint FK_ACTION_EVENT foreign key (EVENT_) references JBPM_EVENT (ID_);
alter table JBPM_ACTION add index FK_ACTION_EXPTHDL (EXCEPTIONHANDLER_), add constraint FK_ACTION_EXPTHDL foreign key (EXCEPTIONHANDLER_) references JBPM_EXCEPTIONHANDLER (ID_);
alter table JBPM_ACTION add index FK_ACTION_PROCDEF (PROCESSDEFINITION_), add constraint FK_ACTION_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_ACTION add index FK_CRTETIMERACT_TA (TIMERACTION_), add constraint FK_CRTETIMERACT_TA foreign key (TIMERACTION_) references JBPM_ACTION (ID_);
alter table JBPM_ACTION add index FK_ACTION_ACTNDEL (ACTIONDELEGATION_), add constraint FK_ACTION_ACTNDEL foreign key (ACTIONDELEGATION_) references JBPM_DELEGATION (ID_);
alter table JBPM_ACTION add index FK_ACTION_REFACT (REFERENCEDACTION_), add constraint FK_ACTION_REFACT foreign key (REFERENCEDACTION_) references JBPM_ACTION (ID_);
alter table JBPM_BYTEARRAY add index FK_BYTEARR_FILDEF (FILEDEFINITION_), add constraint FK_BYTEARR_FILDEF foreign key (FILEDEFINITION_) references JBPM_MODULEDEFINITION (ID_);
alter table JBPM_BYTEBLOCK add index FK_BYTEBLOCK_FILE (PROCESSFILE_), add constraint FK_BYTEBLOCK_FILE foreign key (PROCESSFILE_) references JBPM_BYTEARRAY (ID_);
create index IDX_COMMENT_TOKEN on JBPM_COMMENT (TOKEN_);
create index IDX_COMMENT_TSK on JBPM_COMMENT (TASKINSTANCE_);
alter table JBPM_COMMENT add index FK_COMMENT_TOKEN (TOKEN_), add constraint FK_COMMENT_TOKEN foreign key (TOKEN_) references JBPM_TOKEN (ID_);
alter table JBPM_COMMENT add index FK_COMMENT_TSK (TASKINSTANCE_), add constraint FK_COMMENT_TSK foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE (ID_);
alter table JBPM_DECISIONCONDITIONS add index FK_DECCOND_DEC (DECISION_), add constraint FK_DECCOND_DEC foreign key (DECISION_) references JBPM_NODE (ID_);
create index IDX_DELEG_PRCD on JBPM_DELEGATION (PROCESSDEFINITION_);
alter table JBPM_DELEGATION add index FK_DELEGATION_PRCD (PROCESSDEFINITION_), add constraint FK_DELEGATION_PRCD foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_EVENT add index FK_EVENT_PROCDEF (PROCESSDEFINITION_), add constraint FK_EVENT_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_EVENT add index FK_EVENT_NODE (NODE_), add constraint FK_EVENT_NODE foreign key (NODE_) references JBPM_NODE (ID_);
alter table JBPM_EVENT add index FK_EVENT_TRANS (TRANSITION_), add constraint FK_EVENT_TRANS foreign key (TRANSITION_) references JBPM_TRANSITION (ID_);
alter table JBPM_EVENT add index FK_EVENT_TASK (TASK_), add constraint FK_EVENT_TASK foreign key (TASK_) references JBPM_TASK (ID_);
alter table JBPM_ID_GROUP add index FK_ID_GRP_PARENT (PARENT_), add constraint FK_ID_GRP_PARENT foreign key (PARENT_) references JBPM_ID_GROUP (ID_);
alter table JBPM_ID_MEMBERSHIP add index FK_ID_MEMSHIP_GRP (GROUP_), add constraint FK_ID_MEMSHIP_GRP foreign key (GROUP_) references JBPM_ID_GROUP (ID_);
alter table JBPM_ID_MEMBERSHIP add index FK_ID_MEMSHIP_USR (USER_), add constraint FK_ID_MEMSHIP_USR foreign key (USER_) references JBPM_ID_USER (ID_);
create index IDX_JOB_TSKINST on JBPM_JOB (TASKINSTANCE_);
create index IDX_JOB_PRINST on JBPM_JOB (PROCESSINSTANCE_);
create index IDX_JOB_TOKEN on JBPM_JOB (TOKEN_);
alter table JBPM_JOB add index FK_JOB_TOKEN (TOKEN_), add constraint FK_JOB_TOKEN foreign key (TOKEN_) references JBPM_TOKEN (ID_);
alter table JBPM_JOB add index FK_JOB_NODE (NODE_), add constraint FK_JOB_NODE foreign key (NODE_) references JBPM_NODE (ID_);
alter table JBPM_JOB add index FK_JOB_PRINST (PROCESSINSTANCE_), add constraint FK_JOB_PRINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE (ID_);
alter table JBPM_JOB add index FK_JOB_ACTION (ACTION_), add constraint FK_JOB_ACTION foreign key (ACTION_) references JBPM_ACTION (ID_);
alter table JBPM_JOB add index FK_JOB_TSKINST (TASKINSTANCE_), add constraint FK_JOB_TSKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE (ID_);
alter table JBPM_LOG add index FK_LOG_SOURCENODE (SOURCENODE_), add constraint FK_LOG_SOURCENODE foreign key (SOURCENODE_) references JBPM_NODE (ID_);
alter table JBPM_LOG add index FK_LOG_TOKEN (TOKEN_), add constraint FK_LOG_TOKEN foreign key (TOKEN_) references JBPM_TOKEN (ID_);
alter table JBPM_LOG add index FK_LOG_OLDBYTES (OLDBYTEARRAY_), add constraint FK_LOG_OLDBYTES foreign key (OLDBYTEARRAY_) references JBPM_BYTEARRAY (ID_);
alter table JBPM_LOG add index FK_LOG_NEWBYTES (NEWBYTEARRAY_), add constraint FK_LOG_NEWBYTES foreign key (NEWBYTEARRAY_) references JBPM_BYTEARRAY (ID_);
alter table JBPM_LOG add index FK_LOG_CHILDTOKEN (CHILD_), add constraint FK_LOG_CHILDTOKEN foreign key (CHILD_) references JBPM_TOKEN (ID_);
alter table JBPM_LOG add index FK_LOG_DESTNODE (DESTINATIONNODE_), add constraint FK_LOG_DESTNODE foreign key (DESTINATIONNODE_) references JBPM_NODE (ID_);
alter table JBPM_LOG add index FK_LOG_TASKINST (TASKINSTANCE_), add constraint FK_LOG_TASKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE (ID_);
alter table JBPM_LOG add index FK_LOG_SWIMINST (SWIMLANEINSTANCE_), add constraint FK_LOG_SWIMINST foreign key (SWIMLANEINSTANCE_) references JBPM_SWIMLANEINSTANCE (ID_);
alter table JBPM_LOG add index FK_LOG_PARENT (PARENT_), add constraint FK_LOG_PARENT foreign key (PARENT_) references JBPM_LOG (ID_);
alter table JBPM_LOG add index FK_LOG_NODE (NODE_), add constraint FK_LOG_NODE foreign key (NODE_) references JBPM_NODE (ID_);
alter table JBPM_LOG add index FK_LOG_ACTION (ACTION_), add constraint FK_LOG_ACTION foreign key (ACTION_) references JBPM_ACTION (ID_);
alter table JBPM_LOG add index FK_LOG_VARINST (VARIABLEINSTANCE_), add constraint FK_LOG_VARINST foreign key (VARIABLEINSTANCE_) references JBPM_VARIABLEINSTANCE (ID_);
alter table JBPM_LOG add index FK_LOG_TRANSITION (TRANSITION_), add constraint FK_LOG_TRANSITION foreign key (TRANSITION_) references JBPM_TRANSITION (ID_);
create index IDX_MODDEF_PROCDF on JBPM_MODULEDEFINITION (PROCESSDEFINITION_);
alter table JBPM_MODULEDEFINITION add index FK_TSKDEF_START (STARTTASK_), add constraint FK_TSKDEF_START foreign key (STARTTASK_) references JBPM_TASK (ID_);
alter table JBPM_MODULEDEFINITION add index FK_MODDEF_PROCDEF (PROCESSDEFINITION_), add constraint FK_MODDEF_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
create index IDX_MODINST_PRINST on JBPM_MODULEINSTANCE (PROCESSINSTANCE_);
alter table JBPM_MODULEINSTANCE add index FK_TASKMGTINST_TMD (TASKMGMTDEFINITION_), add constraint FK_TASKMGTINST_TMD foreign key (TASKMGMTDEFINITION_) references JBPM_MODULEDEFINITION (ID_);
alter table JBPM_MODULEINSTANCE add index FK_MODINST_PRCINST (PROCESSINSTANCE_), add constraint FK_MODINST_PRCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE (ID_);
create index IDX_PSTATE_SBPRCDEF on JBPM_NODE (SUBPROCESSDEFINITION_);
create index IDX_NODE_SUPRSTATE on JBPM_NODE (SUPERSTATE_);
create index IDX_NODE_PROCDEF on JBPM_NODE (PROCESSDEFINITION_);
create index IDX_NODE_ACTION on JBPM_NODE (ACTION_);
alter table JBPM_NODE add index FK_PROCST_SBPRCDEF (SUBPROCESSDEFINITION_), add constraint FK_PROCST_SBPRCDEF foreign key (SUBPROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_NODE add index FK_NODE_PROCDEF (PROCESSDEFINITION_), add constraint FK_NODE_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_NODE add index FK_NODE_SCRIPT (SCRIPT_), add constraint FK_NODE_SCRIPT foreign key (SCRIPT_) references JBPM_ACTION (ID_);
alter table JBPM_NODE add index FK_NODE_ACTION (ACTION_), add constraint FK_NODE_ACTION foreign key (ACTION_) references JBPM_ACTION (ID_);
alter table JBPM_NODE add index FK_DECISION_DELEG (DECISIONDELEGATION), add constraint FK_DECISION_DELEG foreign key (DECISIONDELEGATION) references JBPM_DELEGATION (ID_);
alter table JBPM_NODE add index FK_NODE_SUPERSTATE (SUPERSTATE_), add constraint FK_NODE_SUPERSTATE foreign key (SUPERSTATE_) references JBPM_NODE (ID_);
create index IDX_PLDACTR_ACTID on JBPM_POOLEDACTOR (ACTORID_);
create index IDX_TSKINST_SWLANE on JBPM_POOLEDACTOR (SWIMLANEINSTANCE_);
alter table JBPM_POOLEDACTOR add index FK_POOLEDACTOR_SLI (SWIMLANEINSTANCE_), add constraint FK_POOLEDACTOR_SLI foreign key (SWIMLANEINSTANCE_) references JBPM_SWIMLANEINSTANCE (ID_);
create index IDX_PROCDEF_STRTST on JBPM_PROCESSDEFINITION (STARTSTATE_);
alter table JBPM_PROCESSDEFINITION add index FK_PROCDEF_STRTSTA (STARTSTATE_), add constraint FK_PROCDEF_STRTSTA foreign key (STARTSTATE_) references JBPM_NODE (ID_);
create index IDX_PROCIN_ROOTTK on JBPM_PROCESSINSTANCE (ROOTTOKEN_);
create index IDX_PROCIN_SPROCTK on JBPM_PROCESSINSTANCE (SUPERPROCESSTOKEN_);
create index IDX_PROCIN_KEY on JBPM_PROCESSINSTANCE (KEY_);
create index IDX_PROCIN_PROCDEF on JBPM_PROCESSINSTANCE (PROCESSDEFINITION_);
alter table JBPM_PROCESSINSTANCE add index FK_PROCIN_PROCDEF (PROCESSDEFINITION_), add constraint FK_PROCIN_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_PROCESSINSTANCE add index FK_PROCIN_ROOTTKN (ROOTTOKEN_), add constraint FK_PROCIN_ROOTTKN foreign key (ROOTTOKEN_) references JBPM_TOKEN (ID_);
alter table JBPM_PROCESSINSTANCE add index FK_PROCIN_SPROCTKN (SUPERPROCESSTOKEN_), add constraint FK_PROCIN_SPROCTKN foreign key (SUPERPROCESSTOKEN_) references JBPM_TOKEN (ID_);
create index IDX_RTACTN_PRCINST on JBPM_RUNTIMEACTION (PROCESSINSTANCE_);
create index IDX_RTACTN_ACTION on JBPM_RUNTIMEACTION (ACTION_);
alter table JBPM_RUNTIMEACTION add index FK_RTACTN_PROCINST (PROCESSINSTANCE_), add constraint FK_RTACTN_PROCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE (ID_);
alter table JBPM_RUNTIMEACTION add index FK_RTACTN_ACTION (ACTION_), add constraint FK_RTACTN_ACTION foreign key (ACTION_) references JBPM_ACTION (ID_);
alter table JBPM_SWIMLANE add index FK_SWL_ASSDEL (ASSIGNMENTDELEGATION_), add constraint FK_SWL_ASSDEL foreign key (ASSIGNMENTDELEGATION_) references JBPM_DELEGATION (ID_);
alter table JBPM_SWIMLANE add index FK_SWL_TSKMGMTDEF (TASKMGMTDEFINITION_), add constraint FK_SWL_TSKMGMTDEF foreign key (TASKMGMTDEFINITION_) references JBPM_MODULEDEFINITION (ID_);
create index IDX_SWIMLINST_SL on JBPM_SWIMLANEINSTANCE (SWIMLANE_);
alter table JBPM_SWIMLANEINSTANCE add index FK_SWIMLANEINST_TM (TASKMGMTINSTANCE_), add constraint FK_SWIMLANEINST_TM foreign key (TASKMGMTINSTANCE_) references JBPM_MODULEINSTANCE (ID_);
alter table JBPM_SWIMLANEINSTANCE add index FK_SWIMLANEINST_SL (SWIMLANE_), add constraint FK_SWIMLANEINST_SL foreign key (SWIMLANE_) references JBPM_SWIMLANE (ID_);
create index IDX_TASK_TSKNODE on JBPM_TASK (TASKNODE_);
create index IDX_TASK_PROCDEF on JBPM_TASK (PROCESSDEFINITION_);
create index IDX_TASK_TASKMGTDF on JBPM_TASK (TASKMGMTDEFINITION_);
alter table JBPM_TASK add index FK_TSK_TSKCTRL (TASKCONTROLLER_), add constraint FK_TSK_TSKCTRL foreign key (TASKCONTROLLER_) references JBPM_TASKCONTROLLER (ID_);
alter table JBPM_TASK add index FK_TASK_ASSDEL (ASSIGNMENTDELEGATION_), add constraint FK_TASK_ASSDEL foreign key (ASSIGNMENTDELEGATION_) references JBPM_DELEGATION (ID_);
alter table JBPM_TASK add index FK_TASK_TASKNODE (TASKNODE_), add constraint FK_TASK_TASKNODE foreign key (TASKNODE_) references JBPM_NODE (ID_);
alter table JBPM_TASK add index FK_TASK_PROCDEF (PROCESSDEFINITION_), add constraint FK_TASK_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_TASK add index FK_TASK_STARTST (STARTSTATE_), add constraint FK_TASK_STARTST foreign key (STARTSTATE_) references JBPM_NODE (ID_);
alter table JBPM_TASK add index FK_TASK_TASKMGTDEF (TASKMGMTDEFINITION_), add constraint FK_TASK_TASKMGTDEF foreign key (TASKMGMTDEFINITION_) references JBPM_MODULEDEFINITION (ID_);
alter table JBPM_TASK add index FK_TASK_SWIMLANE (SWIMLANE_), add constraint FK_TASK_SWIMLANE foreign key (SWIMLANE_) references JBPM_SWIMLANE (ID_);
alter table JBPM_TASKACTORPOOL add index FK_TSKACTPOL_PLACT (POOLEDACTOR_), add constraint FK_TSKACTPOL_PLACT foreign key (POOLEDACTOR_) references JBPM_POOLEDACTOR (ID_);
alter table JBPM_TASKACTORPOOL add index FK_TASKACTPL_TSKI (TASKINSTANCE_), add constraint FK_TASKACTPL_TSKI foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE (ID_);
alter table JBPM_TASKCONTROLLER add index FK_TSKCTRL_DELEG (TASKCONTROLLERDELEGATION_), add constraint FK_TSKCTRL_DELEG foreign key (TASKCONTROLLERDELEGATION_) references JBPM_DELEGATION (ID_);
create index IDX_TASKINST_TOKN on JBPM_TASKINSTANCE (TOKEN_);
create index IDX_TASKINST_TSK on JBPM_TASKINSTANCE (TASK_, PROCINST_);
create index IDX_TSKINST_TMINST on JBPM_TASKINSTANCE (TASKMGMTINSTANCE_);
create index IDX_TSKINST_SLINST on JBPM_TASKINSTANCE (SWIMLANINSTANCE_);
create index IDX_TASK_ACTORID on JBPM_TASKINSTANCE (ACTORID_);
alter table JBPM_TASKINSTANCE add index FK_TSKINS_PRCINS (PROCINST_), add constraint FK_TSKINS_PRCINS foreign key (PROCINST_) references JBPM_PROCESSINSTANCE (ID_);
alter table JBPM_TASKINSTANCE add index FK_TASKINST_TMINST (TASKMGMTINSTANCE_), add constraint FK_TASKINST_TMINST foreign key (TASKMGMTINSTANCE_) references JBPM_MODULEINSTANCE (ID_);
alter table JBPM_TASKINSTANCE add index FK_TASKINST_TOKEN (TOKEN_), add constraint FK_TASKINST_TOKEN foreign key (TOKEN_) references JBPM_TOKEN (ID_);
alter table JBPM_TASKINSTANCE add index FK_TASKINST_SLINST (SWIMLANINSTANCE_), add constraint FK_TASKINST_SLINST foreign key (SWIMLANINSTANCE_) references JBPM_SWIMLANEINSTANCE (ID_);
alter table JBPM_TASKINSTANCE add index FK_TASKINST_TASK (TASK_), add constraint FK_TASKINST_TASK foreign key (TASK_) references JBPM_TASK (ID_);
create index IDX_TOKEN_PROCIN on JBPM_TOKEN (PROCESSINSTANCE_);
create index IDX_TOKEN_SUBPI on JBPM_TOKEN (SUBPROCESSINSTANCE_);
create index IDX_TOKEN_NODE on JBPM_TOKEN (NODE_);
create index IDX_TOKEN_PARENT on JBPM_TOKEN (PARENT_);
alter table JBPM_TOKEN add index FK_TOKEN_PARENT (PARENT_), add constraint FK_TOKEN_PARENT foreign key (PARENT_) references JBPM_TOKEN (ID_);
alter table JBPM_TOKEN add index FK_TOKEN_NODE (NODE_), add constraint FK_TOKEN_NODE foreign key (NODE_) references JBPM_NODE (ID_);
alter table JBPM_TOKEN add index FK_TOKEN_PROCINST (PROCESSINSTANCE_), add constraint FK_TOKEN_PROCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE (ID_);
alter table JBPM_TOKEN add index FK_TOKEN_SUBPI (SUBPROCESSINSTANCE_), add constraint FK_TOKEN_SUBPI foreign key (SUBPROCESSINSTANCE_) references JBPM_PROCESSINSTANCE (ID_);
create index IDX_TKVARMAP_CTXT on JBPM_TOKENVARIABLEMAP (CONTEXTINSTANCE_);
create index IDX_TKVVARMP_TOKEN on JBPM_TOKENVARIABLEMAP (TOKEN_);
alter table JBPM_TOKENVARIABLEMAP add index FK_TKVARMAP_CTXT (CONTEXTINSTANCE_), add constraint FK_TKVARMAP_CTXT foreign key (CONTEXTINSTANCE_) references JBPM_MODULEINSTANCE (ID_);
alter table JBPM_TOKENVARIABLEMAP add index FK_TKVARMAP_TOKEN (TOKEN_), add constraint FK_TKVARMAP_TOKEN foreign key (TOKEN_) references JBPM_TOKEN (ID_);
create index IDX_TRANSIT_TO on JBPM_TRANSITION (TO_);
create index IDX_TRANSIT_FROM on JBPM_TRANSITION (FROM_);
create index IDX_TRANS_PROCDEF on JBPM_TRANSITION (PROCESSDEFINITION_);
alter table JBPM_TRANSITION add index FK_TRANSITION_TO (TO_), add constraint FK_TRANSITION_TO foreign key (TO_) references JBPM_NODE (ID_);
alter table JBPM_TRANSITION add index FK_TRANS_PROCDEF (PROCESSDEFINITION_), add constraint FK_TRANS_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION (ID_);
alter table JBPM_TRANSITION add index FK_TRANSITION_FROM (FROM_), add constraint FK_TRANSITION_FROM foreign key (FROM_) references JBPM_NODE (ID_);
alter table JBPM_VARIABLEACCESS add index FK_VARACC_TSKCTRL (TASKCONTROLLER_), add constraint FK_VARACC_TSKCTRL foreign key (TASKCONTROLLER_) references JBPM_TASKCONTROLLER (ID_);
alter table JBPM_VARIABLEACCESS add index FK_VARACC_SCRIPT (SCRIPT_), add constraint FK_VARACC_SCRIPT foreign key (SCRIPT_) references JBPM_ACTION (ID_);
alter table JBPM_VARIABLEACCESS add index FK_VARACC_PROCST (PROCESSSTATE_), add constraint FK_VARACC_PROCST foreign key (PROCESSSTATE_) references JBPM_NODE (ID_);
create index IDX_VARINST_TKVARMP on JBPM_VARIABLEINSTANCE (TOKENVARIABLEMAP_);
create index IDX_VARINST_PRCINS on JBPM_VARIABLEINSTANCE (PROCESSINSTANCE_);
create index IDX_VARINST_TK on JBPM_VARIABLEINSTANCE (TOKEN_);
alter table JBPM_VARIABLEINSTANCE add index FK_VARINST_TK (TOKEN_), add constraint FK_VARINST_TK foreign key (TOKEN_) references JBPM_TOKEN (ID_);
alter table JBPM_VARIABLEINSTANCE add index FK_VARINST_TKVARMP (TOKENVARIABLEMAP_), add constraint FK_VARINST_TKVARMP foreign key (TOKENVARIABLEMAP_) references JBPM_TOKENVARIABLEMAP (ID_);
alter table JBPM_VARIABLEINSTANCE add index FK_VARINST_PRCINST (PROCESSINSTANCE_), add constraint FK_VARINST_PRCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE (ID_);
alter table JBPM_VARIABLEINSTANCE add index FK_VAR_TSKINST (TASKINSTANCE_), add constraint FK_VAR_TSKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE (ID_);
alter table JBPM_VARIABLEINSTANCE add index FK_BYTEINST_ARRAY (BYTEARRAYVALUE_), add constraint FK_BYTEINST_ARRAY foreign key (BYTEARRAYVALUE_) references JBPM_BYTEARRAY (ID_);

--
-- Table structure for table 'jforum_banlist'
--
DROP TABLE IF EXISTS jforum_banlist;
CREATE TABLE jforum_banlist (
  banlist_id INT NOT NULL auto_increment,
  user_id INT,
  banlist_ip varchar(15),
  banlist_email varchar(255),
  PRIMARY KEY  (banlist_id),
  INDEX idx_user (user_id),
  INDEX (banlist_ip),
  INDEX (banlist_email)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_categories'
--
DROP TABLE IF EXISTS jforum_categories;
CREATE TABLE jforum_categories (
  categories_id INT NOT NULL auto_increment,
  title varchar(100) NOT NULL default '',
  display_order INT NOT NULL default '0',
  moderated TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (categories_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_config'
--
DROP TABLE IF EXISTS jforum_config;
CREATE TABLE jforum_config (
  config_name varchar(255) NOT NULL default '',
  config_value varchar(255) NOT NULL default '',
  config_id int not null auto_increment,
  PRIMARY KEY(config_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_forums'
--
DROP TABLE IF EXISTS jforum_forums;
CREATE TABLE jforum_forums (
  forum_id INT NOT NULL auto_increment,
  categories_id INT NOT NULL default '1',
  forum_name varchar(150) NOT NULL default '',
  forum_desc varchar(255) default NULL,
  forum_order INT default '1',
  forum_topics INT NOT NULL default '0',
  forum_last_post_id INT NOT NULL default '0',
  moderated TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (forum_id),
  KEY (categories_id),
  INDEX idx_forums_cats (categories_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_forums_watch'
--
DROP TABLE IF EXISTS jforum_forums_watch;
CREATE TABLE jforum_forums_watch (
  forum_id INT NOT NULL,
  user_id INT NOT NULL,
  INDEX idx_fw_forum (forum_id),
  INDEX idx_fw_user (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_groups'
--
DROP TABLE IF EXISTS jforum_groups;
CREATE TABLE jforum_groups (
  group_id INT NOT NULL auto_increment,
  group_name varchar(40) NOT NULL default '',
  group_description varchar(255) default NULL,
  parent_id INT default '0',
  PRIMARY KEY  (group_id)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS jforum_user_groups;
CREATE TABLE jforum_user_groups (
	group_id INT NOT NULL,
	user_id INT NOT NULL,
	INDEX idx_group (group_id),
	INDEX idx_user (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_roles'
--
DROP TABLE IF EXISTS jforum_roles;
CREATE TABLE jforum_roles (
  role_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_id INT default '0',
  name varchar(255) NOT NULL,
  INDEX idx_group (group_id),
  INDEX idx_name (name)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_role_values'
--
DROP TABLE IF EXISTS jforum_role_values;
CREATE TABLE jforum_role_values (
  role_id INT NOT NULL,
  role_value VARCHAR(255),
  INDEX idx_role(role_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_posts'
--
DROP TABLE IF EXISTS jforum_posts;
CREATE TABLE jforum_posts (
  post_id INT NOT NULL auto_increment,
  topic_id INT NOT NULL default '0',
  forum_id INT NOT NULL default '0',
  user_id INT NOT NULL default '0',
  post_time datetime default NULL,
  poster_ip varchar(15) default NULL,
  enable_bbcode tinyint(1) NOT NULL default '1',
  enable_html tinyint(1) NOT NULL default '1',
  enable_smilies tinyint(1) NOT NULL default '1',
  enable_sig tinyint(1) NOT NULL default '1',
  post_edit_time datetime default NULL,
  post_edit_count INT NOT NULL default '0',
  status tinyint(1) default '1',
  attach TINYINT(1) DEFAULT '0',
  need_moderate TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (post_id),
  KEY (user_id),
  KEY (topic_id),
  KEY (forum_id),
  KEY(post_time),
  INDEX (need_moderate)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_posts_text'
--
DROP TABLE IF EXISTS jforum_posts_text;
CREATE TABLE jforum_posts_text (
	post_id INT NOT NULL PRIMARY KEY,
	post_text TEXT,
	post_subject VARCHAR(100)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_privmsgs'
--
DROP TABLE IF EXISTS jforum_privmsgs;
CREATE TABLE jforum_privmsgs (
  privmsgs_id INT NOT NULL auto_increment,
  privmsgs_type tinyint(4) NOT NULL default '0',
  privmsgs_subject varchar(255) NOT NULL default '',
  privmsgs_from_userid INT NOT NULL default '0',
  privmsgs_to_userid INT NOT NULL default '0',
  privmsgs_date datetime default null,
  privmsgs_ip varchar(15) NOT NULL default '',
  privmsgs_enable_bbcode tinyint(1) NOT NULL default '1',
  privmsgs_enable_html tinyint(1) NOT NULL default '0',
  privmsgs_enable_smilies tinyint(1) NOT NULL default '1',
  privmsgs_attach_sig tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (privmsgs_id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS jforum_privmsgs_text;
CREATE TABLE jforum_privmsgs_text (
	privmsgs_id INT NOT NULL,
	privmsgs_text TEXT,
	PRIMARY KEY ( privmsgs_id )
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_ranks'
--
DROP TABLE IF EXISTS jforum_ranks;
CREATE TABLE jforum_ranks (
  rank_id INT NOT NULL auto_increment,
  rank_title varchar(50) NOT NULL default '',
  rank_min INT NOT NULL default '0',
  rank_special tinyint(1) default NULL,
  rank_image varchar(255) default NULL,
  PRIMARY KEY  (rank_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_sessions'
--
DROP TABLE IF EXISTS jforum_sessions;
CREATE TABLE jforum_sessions (
  session_id varchar(150) NOT NULL default '',
  session_user_id INT NOT NULL default '0',
  session_start datetime default null,
  session_time bigint default '0',
  session_ip varchar(15) NOT NULL default '',
  session_page int(11) NOT NULL default '0',
  session_logged_int tinyint(1) default NULL,
  INDEX idx_sessions_users (session_user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_smilies'
--
DROP TABLE IF EXISTS jforum_smilies;
CREATE TABLE jforum_smilies (
  smilie_id INT NOT NULL auto_increment,
  code varchar(50) NOT NULL default '',
  url varchar(100) default NULL,
  disk_name varchar(255),
  PRIMARY KEY  (smilie_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_themes'
--
DROP TABLE IF EXISTS jforum_themes;
CREATE TABLE jforum_themes (
  themes_id INT NOT NULL auto_increment,
  template_name varchar(30) NOT NULL default '',
  style_name varchar(30) NOT NULL default '',
  PRIMARY KEY  (themes_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_topics'
--
DROP TABLE IF EXISTS jforum_topics;
CREATE TABLE jforum_topics (
  topic_id INT NOT NULL auto_increment,
  forum_id INT NOT NULL default '0',
  topic_title varchar(100) NOT NULL default '',
  user_id INT NOT NULL default '0',
  topic_time datetime default null,
  topic_views INT default '1',
  topic_replies INT default '0',
  topic_status tinyint(3) default '0',
  topic_vote_id INT NOT NULL default '0',
  topic_type tinyint(3) default '0',
  topic_first_post_id INT default '0',
  topic_last_post_id INT NOT NULL default '0',
  topic_moved_id INT DEFAULT 0,
  moderated TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (topic_id),
  KEY (forum_id),
  KEY(user_id),
  KEY(topic_first_post_id),
  KEY(topic_last_post_id),
  KEY(topic_moved_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_topics_watch'
--
DROP TABLE IF EXISTS jforum_topics_watch;
CREATE TABLE jforum_topics_watch (
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  is_read tinyint(1) DEFAULT 1,
  INDEX idx_topic (topic_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_users'
--
DROP TABLE IF EXISTS jforum_users;
CREATE TABLE jforum_users (
  user_id INT NOT NULL auto_increment,
  user_active tinyint(1) default NULL,
  username varchar(50) NOT NULL default '',
  user_password varchar(255) NOT NULL default '',
  user_session_time bigint default 0,
  user_session_page INT NOT NULL default '0',
  user_lastvisit datetime default null,
  user_regdate datetime default null,
  user_level tinyint(4) default NULL,
  user_posts INT NOT NULL default '0',
  user_timezone varchar(5) NOT NULL default '',
  user_style tinyint(4) default NULL,
  user_lang varchar(255) NOT NULL default '',
  user_dateformat varchar(20) NOT NULL default '%d/%M/%Y %H:%i',
  user_new_privmsg INT NOT NULL default '0',
  user_unread_privmsg INT NOT NULL default '0',
  user_last_privmsg datetime NULL,
  user_emailtime datetime default NULL,
  user_viewemail tinyint(1) default '0',
  user_attachsig tinyint(1) default '1',
  user_allowhtml tinyint(1) default '0',
  user_allowbbcode tinyint(1) default '1',
  user_allowsmilies tinyint(1) default '1',
  user_allowavatar tinyint(1) default '1',
  user_allow_pm tinyint(1) default '1',
  user_allow_viewonline tinyint(1) default '1',
  user_notify tinyint(1) default '1',
  user_notify_always tinyint(1) default '0',
  user_notify_text tinyint(1) default '0',
  user_notify_pm tinyint(1) default '1',
  user_popup_pm tinyint(1) default '1',
  rank_id INT default 0,
  user_avatar varchar(100) default NULL,
  user_avatar_type tinyint(4) NOT NULL default '0',
  user_email varchar(255) NOT NULL default '',
  user_icq varchar(15) default NULL,
  user_website varchar(255) default NULL,
  user_from varchar(100) default NULL,
  user_sig text,
  user_sig_bbcode_uid varchar(10) default NULL,
  user_aim varchar(255) default NULL,
  user_yim varchar(255) default NULL,
  user_msnm varchar(255) default NULL,
  user_occ varchar(100) default NULL,
  user_interests varchar(255) default NULL,
  user_biography text DEFAULT NULL,
  user_actkey varchar(32) default NULL,
  gender char(1) default NULL,
  themes_id INT default NULL,
  deleted tinyint(1) default NULL,
  user_viewonline tinyint(1) default '1',
  security_hash varchar(32),
  user_karma DOUBLE,
  user_authhash VARCHAR(32),
  PRIMARY KEY  (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_vote_desc'
--
DROP TABLE IF EXISTS jforum_vote_desc;
CREATE TABLE jforum_vote_desc (
  vote_id INT NOT NULL auto_increment,
  topic_id INT NOT NULL default '0',
  vote_text varchar(255) NOT NULL default '',
  vote_start datetime NOT NULL,
  vote_length int(11) NOT NULL default '0',
  PRIMARY KEY  (vote_id),
  INDEX(topic_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_vote_results'
--
DROP TABLE IF EXISTS jforum_vote_results;
CREATE TABLE jforum_vote_results (
  vote_id INT NOT NULL default '0',
  vote_option_id tinyint(4) NOT NULL default '0',
  vote_option_text varchar(255) NOT NULL default '',
  vote_result int(11) NOT NULL default '0',
  INDEX(vote_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_vote_voters'
--
DROP TABLE IF EXISTS jforum_vote_voters;
CREATE TABLE jforum_vote_voters (
  vote_id INT NOT NULL default '0',
  vote_user_id INT NOT NULL default '0',
  vote_user_ip varchar(15) NOT NULL default '',
  INDEX(vote_id),
  INDEX(vote_user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_words'
--
DROP TABLE IF EXISTS jforum_words;
CREATE TABLE jforum_words (
  word_id INT NOT NULL auto_increment,
  word varchar(100) NOT NULL default '',
  replacement varchar(100) NOT NULL default '',
  PRIMARY KEY  (word_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_karma'
--
DROP TABLE IF EXISTS jforum_karma;
CREATE TABLE jforum_karma (
	karma_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	post_id INT NOT NULL,
	topic_id INT NOT NULL,
	post_user_id INT NOT NULL,
	from_user_id INT NOT NULL,
	points INT NOT NULL,
	rate_date datetime NULL,
	KEY(post_id),
	KEY(topic_id),
	KEY(post_user_id),
	KEY(from_user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_bookmark'
--
DROP TABLE IF EXISTS jforum_bookmarks;
CREATE TABLE jforum_bookmarks (
	bookmark_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	relation_id INT NOT NULL,
	relation_type INT NOT NULL,
	public_visible INT DEFAULT '1',
	title varchar(255),
	description varchar(255),
	INDEX book_idx_relation (relation_id),
	KEY(user_id)
) ENGINE=InnoDB;
--
-- Table structure for table 'jforum_quota_limit'
--
DROP TABLE IF EXISTS jforum_quota_limit;
CREATE TABLE jforum_quota_limit (
	quota_limit_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	quota_desc VARCHAR(50) NOT NULL,
	quota_limit INT NOT NULL,
	quota_type TINYINT(1) DEFAULT '1'
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_extension_groups'
--
DROP TABLE IF EXISTS jforum_extension_groups;
CREATE TABLE jforum_extension_groups (
	extension_group_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	allow TINYINT(1) DEFAULT '1',
	upload_icon VARCHAR(100),
	download_mode TINYINT(1) DEFAULT '1'
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_extensions'
--
DROP TABLE IF EXISTS jforum_extensions;
CREATE TABLE jforum_extensions (
	extension_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	extension_group_id INT NOT NULL,
	description VARCHAR(100),
	upload_icon VARCHAR(100),
	extension VARCHAR(10),
	allow TINYINT(1) DEFAULT '1',
	KEY(extension_group_id),
	INDEX(extension)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_attach'
--
DROP TABLE IF EXISTS jforum_attach;
CREATE TABLE jforum_attach (
	attach_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	post_id INT,
	privmsgs_id INT,
	user_id INT NOT NULL,
	INDEX idx_att_post(post_id),
	INDEX idx_att_priv(privmsgs_id),
	INDEX idx_att_user(user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_attach_desc'
--
DROP TABLE IF EXISTS jforum_attach_desc;
CREATE TABLE jforum_attach_desc (
	attach_desc_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	attach_id INT NOT NULL,
	physical_filename VARCHAR(255) NOT NULL,
	real_filename VARCHAR(255) NOT NULL,
	download_count INT,
	description VARCHAR(255),
	mimetype VARCHAR(50),
	filesize INT,
	upload_time DATETIME,
	thumb TINYINT(1) DEFAULT '0',
	extension_id INT,
	INDEX idx_att_d_att(attach_id),
	INDEX idx_att_d_ext(extension_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_attach_quota'
--
DROP TABLE IF EXISTS jforum_attach_quota;
CREATE TABLE jforum_attach_quota (
	attach_quota_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	group_id INT NOT NULL,
	quota_limit_id INT NOT NULL,
	KEY(group_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_banner'
--
DROP TABLE IF EXISTS jforum_banner;
CREATE TABLE jforum_banner (
	banner_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	banner_name VARCHAR(90),
	banner_placement INT NOT NULL DEFAULT '0',
	banner_description VARCHAR(250),
	banner_clicks INT NOT NULL DEFAULT '0',
	banner_views INT NOT NULL DEFAULT '0',
	banner_url VARCHAR(250),
	banner_weight TINYINT(1) NOT NULL DEFAULT '50',
	banner_active TINYINT(1) NOT NULL DEFAULT '0',
	banner_comment VARCHAR(250),
	banner_type INT NOT NULL DEFAULT '0',
	banner_width INT NOT NULL DEFAULT '0',
	banner_height INT NOT NULL DEFAULT '0',
	KEY(banner_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_mail_integration'
--
DROP TABLE IF EXISTS jforum_mail_integration;
CREATE TABLE jforum_mail_integration (
	forum_id INT NOT NULL,
	forum_email VARCHAR(100) NOT NULL,
	pop_username VARCHAR(100) NOT NULL,
	pop_password VARCHAR(100) NOT NULL,
	pop_host VARCHAR(100) NOT NULL,
	pop_port INT DEFAULT 110,
	pop_ssl TINYINT DEFAULT '0',
	KEY(forum_id)
) ENGINE=InnoDB;


--
-- Table structure for table 'jforum_api'
--
DROP TABLE IF EXISTS jforum_api;
CREATE TABLE jforum_api (
	api_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	api_key VARCHAR(32) NOT NULL,
	api_validity DATETIME NOT NULL
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_moderation_log'
--
DROP TABLE IF EXISTS jforum_moderation_log;
CREATE TABLE jforum_moderation_log (
	log_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	log_description TEXT NOT NULL,
	log_original_message TEXT,
	log_date DATETIME NOT NULL,
	log_type TINYINT DEFAULT 0,
	post_id INT DEFAULT 0,
	topic_id INT DEFAULT 0,
	post_user_id INT DEFAULT 0,
	KEY(user_id),
	KEY(post_user_id)
) ENGINE=InnoDB;

