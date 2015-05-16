drop table if exists artifact_template;
drop table if exists db_repository_version;
drop table if exists db_repository_item;
drop table if exists db_repository_sequence;
drop table if exists cost_installment;
drop table if exists cost;
drop table if exists cost_status;
drop table if exists invoice;
drop table if exists invoice_item;
drop table if exists invoice_status;
drop table if exists invoice_history;
drop table if exists repository_file_plan;
drop table if exists repository_file_project;
drop table if exists repository_file;
drop table if exists customer_function;
drop table if exists resource_capacity;
drop table if exists question_answer;
drop table if exists question_alternative;
drop table if exists survey_question;
drop table if exists survey;
drop table if exists custom_form;
drop table if exists meta_form;
drop table if exists custom_node_template;
drop table if exists step_node_template;
drop table if exists decision_node_template;
drop table if exists node_template;
drop table if exists template;
drop table if exists plan_relation;
drop table if exists risk_history;
drop table if exists risk;
drop table if exists risk_status;
drop table if exists attachment_history;
drop table if exists attachment;
drop table if exists occurrence_kpi;
drop table if exists occurrence_history;
drop table if exists occurrence_field;
drop table if exists occurrence_dependency; 
drop table if exists occurrence;
drop table if exists additional_table;
drop table if exists additional_field;
drop table if exists meta_field;
drop table if exists preference;
drop table if exists report_result;
drop table if exists report;
drop table if exists notification_field;
drop table if exists notification;
drop table if exists resource_task_alloc;
drop table if exists task_history;
drop table if exists project_history;
drop table if exists requirement_history;
drop table if exists resource_task;
drop table if exists task;
drop table if exists requirement;
drop table if exists task_status;
drop table if exists requirement_status;
drop table if exists root;
drop table if exists leader;
drop table if exists resource;
drop table if exists customer;
drop table if exists discussion_topic;
drop table if exists discussion;
drop table if exists category;
drop table if exists repository_policy;
drop table if exists project;
drop table if exists project_status;
drop table if exists planning;
drop table if exists tool_user;
drop table if exists area;
drop table if exists function;
drop table if exists department;
drop table if exists event_log;

drop table if exists p_sequence;


create table p_sequence (id bigint(9) not null, primary key pk_p_sequence (id));

create table event_log (
	summary varchar(10) not null,
	description text,
	creation_date timestamp(14) not null,	
	username varchar(30) not null
);

create table tool_user (
	id varchar(10) not null,
	username varchar(30) not null,
	password varchar(70) null,
	name varchar(50) not null,
	email varchar(70),
	phone varchar(20),
	color varchar(10),
	department_id varchar(10) not null,
	area_id varchar(10) not null,
	function_id varchar(10) null,
	country varchar(2) not null,
	language varchar(2) not null,
	auth_mode varchar(200) null,
	birth date null,
	permission text null,
	pic_file mediumblob null,
	final_date timestamp(14) null,	
primary key pk_user (id) 
);
 
create table department (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
primary key pk_department (id) 
);

create table function (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
primary key pk_function (id)
);

create table area (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
primary key pk_area (id)
);

create table category (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
	type int(1) null,
	project_id varchar (10) null,
    billable int(1) NULL,
	disable_view int(1) NULL,
	is_defect int(1) NULL,
	is_testing int(1) NULL,
	is_developing int(1) NULL,
	category_order int(2) null,
primary key pk_category (id)
);

create table customer (
	id varchar (10) not null,
	project_id varchar (10) not null,
	is_disable int(1) null,	
	is_req_acceptable int(1) null,
	can_see_tech_comment int(1) null,
    pre_approve_req int(1) null,
	can_see_discussion int(1) null,
	function_id varchar(10) null,
	can_see_other_reqs int(1) null,
	can_open_otherowner_reqs int(1) null,
primary key pk_customer (id, project_id)
);

create table resource (
	id varchar (10) not null,
	project_id varchar (10) not null,
	can_self_alloc int(1) null,
	can_see_repository int(1) null,
	can_see_invoice int(1) null,
	cost_per_hour decimal(9,2) null,
	can_see_customer int(1) null,
	capacity_per_day decimal(9,2) null,
primary key pk_resource (id, project_id)
);

create table leader (
	id varchar (10) not null,
	project_id varchar (10) not null,
primary key pk_leader (id, project_id)
);

create table root (
	id varchar (10) not null,
	project_id varchar (10) not null,
primary key pk_root (id, project_id)
);

create table project_status (
	id varchar(10) not null,
	name varchar (50),
	note varchar (100),
	state_machine_order bigint(4) ,
primary key pk_proj_status (id)
);

create table requirement_status (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
	state_machine_order bigint(4),
	accept_project_id varchar(10) null,
	parent_id varchar(10) null,
primary key pk_req_status (id)
);

create table task_status (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
	state_machine_order bigint(4),
primary key pk_task_status (id)
);

create table project (
	id varchar(10) not null,
	name varchar (30),
	parent_id varchar (10),
	can_alloc varchar(1) null,
	project_status_id varchar (10) not null,
	repository_url varchar (200),
	repository_class varchar (200),
	repository_user varchar(40),
	repository_pass varchar(40),
	estimated_closure_date timestamp(14) null,
	allow_billable int(1) null,
primary key pk_project (id)
) ;

create table requirement (
	id varchar(10) not null,
	suggested_date timestamp(14) null,
	deadline_date timestamp(14) null,	
	project_id varchar (10) not null,
	user_id varchar (10) not null,
	requirement_status_id varchar (10) not null,
	priority int(1) not null,
	is_acceptance int(1) null,
	category_id varchar (10) null,
	reopening bigint(6) null,
primary key pk_req (id)
);

create table task (
	id varchar(10) not null,
	name varchar (50),
	project_id varchar (10) not null,
	requirement_id varchar (10),
	category_id varchar (10) not null,
	task_id varchar (10),
	is_parent_task int(1),
	created_by varchar (10) null,
	is_unpredictable int(1) null,
primary key pk_task (id)
);

create table resource_task (
	task_id varchar (10) not null,
	resource_id varchar (10) not null,
	project_id varchar (10) not null,
	start_date timestamp(14) not null,
	estimated_time bigint(6) not null,
	actual_date timestamp(14) null,
	actual_time bigint(6) null,	
	task_status_id varchar (10) not null,
	is_acceptance_task int(1),
	billable int(1) null,
primary key pk_resourcetask (task_id, resource_id, project_id)
);


create table planning (
	id varchar(10) not null,
	description text,
	creation_date timestamp(14) not null,
	final_date timestamp(14) null,
	iteration varchar(10) null,
 	rich_text_desc text null,
primary key pk_planning (id)
);

create table requirement_history (
	requirement_id varchar (10) not null,
	requirement_status_id varchar (10) not null,
	creation_date timestamp(14) not null,
	user_id varchar (10),	
	iteration varchar(10) null,
	comment text,		
primary key pk_req_hist (requirement_id, requirement_status_id, creation_date)
);

create table project_history (
	project_id varchar (10) not null,
	project_status_id varchar (10) not null,
	creation_date timestamp(14) not null,
primary key pk_prj_hist (project_id, project_status_id, creation_date)
);

create table task_history (
	task_id varchar (10) not null,
	resource_id varchar (10) not null,
	project_id varchar (10) not null,
	task_status_id varchar (10) not null,
	creation_date timestamp(14) not null,	
	start_date timestamp(14) not null,
	estimated_time bigint(6) not null,
	actual_date timestamp(14) null,
	actual_time bigint(6) null,		
	user_id varchar (10),
	iteration varchar(10) null,	
	comment text,
primary key pk_task_hist (task_id, resource_id, project_id, task_status_id, creation_date)
);

create table resource_task_alloc (
	task_id varchar (10) not null,
	resource_id varchar (10) not null,
	project_id varchar (10) not null,
	sequence bigint(6) not null,
	alloc_time bigint(6) not null,
primary key pk_resourcetaskalloc (task_id, resource_id, project_id, sequence)
);

create table report (
	id varchar(10) not null,
	name varchar(100) null,
	type int(2) not null,
	report_perspective_id varchar(10) not null,
	sql_text text null,
	execution_hour int(2) not null,
	last_execution timestamp(14) null,
	project_id varchar(10) not null,
	final_date timestamp(14) null,
	data_type int(2) not null,
	file_name varchar(100) null,
	category_id varchar(10) null,
	profile_view varchar(1) null,
	goal varchar(25) null,
	tolerance varchar(25) null,
	tolerance_type varchar(2) null,
primary key pk_report (id)
);

create table report_result (
	report_id varchar(10) not null,
	last_execution timestamp(14) null,
	value varchar(25) not null,
primary key pk_report_result (report_id, last_execution)
);


create table notification (
	id varchar(10) not null,
	name varchar(50) not null,
	description varchar(100) null,	
	notification_class varchar(100) not null,
	sql_text text not null,
	retry_number int(2) not null,
	next_notification timestamp(14) not null,
	final_date timestamp(14) null,
	last_check timestamp(14) not null,	
	period_minute int(3) null,	
	period_hour int(3) null,		
	periodicity int(2) null not null,
primary key pk_notification (id)
);

create table notification_field (
	notification_id varchar(10) not null,
	name varchar(100) not null,
	value varchar(500) null,
primary key pk_notif_field (notification_id, name)
);

create table preference (
	user_id varchar(10) not null,
	id varchar(50) null,
	value text not null,
primary key pk_preference (user_id, id)
);

create table meta_field (
	id varchar(10) not null,
	name varchar(50) null,
	type int(1) not null,
	apply_to int(1) not null,	
	project_id varchar (10) not null,	
	category_id varchar (10) null,		
	domain text not null,
	final_date timestamp(14) null,
	meta_form_id varchar(10) null,
	help_content varchar(300) null,	
primary key pk_metafield (id)
);

create table additional_field (
	planning_id varchar(10) not null,
	meta_field_id varchar (10) not null,	
	value text not null,
	date_value timestamp(14) null,
primary key pk_additional_field (planning_id, meta_field_id)
);

create table additional_table (
	planning_id varchar(10) not null,
	meta_field_id varchar (10) not null,	
    line int(6) not null,
    col int(6) not null,
	value text not null,
	date_value timestamp(14) null,
primary key pk_additional_table (planning_id, meta_field_id, line, col)
);

create table occurrence (
	id varchar(10) not null,
	source varchar(100) not null,
	project_id varchar(10) not null,
	name varchar(255) null,
	visible varchar(1) null,
	loc varchar(7) null,
	occurrence_status varchar(10) null,	
	occurrence_status_label varchar(60) null,
primary key pk_occurrence (id)
);

create table occurrence_dependency (
	occurrence_id varchar(10) not null,
	planning_id varchar(10) not null,
primary key pk_occur_dependency (occurrence_id, planning_id)
);

create table occurrence_field (
	occurrence_id varchar(10) not null,
	field varchar(10) not null,	
	value text not null,
	date_value timestamp(14) null,
primary key pk_occur_field (occurrence_id, field)
);

create table occurrence_history (
	occurrence_id varchar (10) not null,
	occurrence_status varchar (10) not null,
	occurrence_status_label varchar (60) not null,	
	creation_date timestamp(14) not null,
	user_id varchar (10) not null,
	history TEXT,
primary key pk_occur_history (occurrence_id, occurrence_status, creation_date, user_id)
);

create table occurrence_kpi (
	report_id varchar(10) not null,
	occurrence_id varchar(10) not null,
	creation_date timestamp(14) null,
	weight int(1) null,
primary key pk_occurrence_kpi (report_id, occurrence_id)
);

create table risk (
	id varchar (10) not null,
	project_id varchar(10) not null,
	category_id varchar(10) not null,
	name varchar(50) not null,
	probability varchar(2) not null,
	impact varchar(2) not null,
	tendency varchar(2) not null,	
	responsible varchar(40) null,	
	risk_status_id varchar (10) not null,
	strategy TEXT null,	
	contingency TEXT null,
	impact_cost varchar(1) null,
	impact_time varchar(1) null,
	impact_quality varchar(1) null,
	impact_scope varchar(1) null,
	risk_type varchar(1) null,
primary key pk_risk (id)
);

create table risk_history (
	risk_id varchar (10) not null,
	risk_status_id varchar (10) not null,
	risk_status_label varchar (60) null,	
	creation_date timestamp(14) not null,
	user_id varchar (10) not null,
	history TEXT,
	probability varchar(2) null,
	impact varchar(2) null,
	tendency varchar(2) null,	
	impact_cost varchar(1) null,
	impact_time varchar(1) null,
	impact_quality varchar(1) null,
	impact_scope varchar(1) null,
	risk_type varchar(1) null,	
primary key pk_risk_history (risk_id, risk_status_id, creation_date, user_id)
);

create table risk_status (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
	status_type varchar(1) not null,
primary key pk_risk_status (id)
);

create table attachment (
	id varchar(10) not null,
	planning_id varchar (10) null,
	name varchar (50),
	status varchar(2) not null,
	visibility varchar(2) not null,
	type varchar(40) not null,
	content_type varchar(40) not null,	
	creation_date timestamp(14) not null,	
	comment text null,
	binary_file mediumblob not null,
primary key pk_attachment (id)
);

create table attachment_history (
	attachment_id varchar(10) not null,
	status varchar(2) not null,
	creation_date timestamp(14) not null,	
	user_id varchar (10) not null,
	history TEXT,
primary key pk_attach_history (attachment_id, status, creation_date, user_id)
);

create table discussion_topic (
	id varchar(10) not null,
	planning_id varchar (10) not null,
	content text null,
	parent_topic varchar(10) null,
	user_id varchar(10) null,
	creation_date timestamp(14) not null,
primary key pk_discussion_topic (id)
);

create table discussion (
	id varchar(10) not null,
	project_id varchar (10) null,
	name varchar (255) not null,
	owner varchar(10) null,
	category_id varchar(10) not null,
	is_blocked int(1) null,
primary key pk_discussion (id)
);

create table plan_relation (
	planning_id varchar(10) not null,
	plan_related_id varchar (10) not null,
	plan_type varchar(2) not null,
	plan_related_type varchar(2) not null,
	relation_type varchar(2) not null,
primary key pk_plan_relation (planning_id, plan_related_id)
);

create table template (
	id varchar(10) not null,
	name varchar(50) not null,
	deprecated_date timestamp(14) null,
	root_node_id varchar(10) not null,
	category_id varchar(10) null,
primary key pk_template (id)
);

create table node_template (
	id varchar(10) not null,
	name varchar(50) not null,
	description text null,
	node_type varchar(2) not null,
	planning_id varchar(10) not null,
	project_id varchar (10) null,	
	next_node_id varchar(10) null,	
primary key pk_node_template (id)
);

create table step_node_template (
	id varchar(10) not null,
	category_id varchar(10) not null,
	resource_list text null,	
	category_regex varchar(40) null,
primary key pk_step_node_template (id)
);

create table decision_node_template (
	id varchar(10) not null,
	question_content text null,
	next_node_id_if_false varchar(10) null,
primary key pk_decision_node_template (id)
);

create table custom_node_template (
	node_template_id varchar(10) not null,
	template_id varchar(10) not null,
	instance_id varchar(10) not null,	
	name varchar(50) not null,
	description text null,
	planning_id varchar(10) null,
	project_id varchar (10) null,	
	category_id varchar(10) null,
	related_task_id varchar(10) null,
	resource_list text null,
	question_content text null,
	is_parent_task integer null,
	decision_answer text null,
primary key pk_custom_node_template (node_template_id, template_id, instance_id)
);


create table meta_form (
	id varchar(10) not null,
	name varchar (30),
	viewable_cols text null,
	grid_row_num integer NULL,
	filter_col_id varchar(10) null,
	js_before_save text null,
	js_after_save text null,
	js_after_load text null,
primary key pk_meta_form (id)
);

create table custom_form (
	id varchar(10) not null,
	meta_form_id varchar (10),
primary key pk_custom_form (id)
);

create table survey (
	id varchar(10) NOT NULL,
	name varchar(50) NOT NULL,
	description TEXT NULL,
	is_template varchar(1) NOT NULL,
	is_anonymous varchar(1) NOT NULL,
	project_id varchar(10) NOT NULL,
	creation_date timestamp NOT NULL,
	final_date timestamp NULL,
	date_publishing timestamp NOT NULL,
	anonymous_key varchar(70) NOT NULL,
primary key pk_survey (id) 
);

create table survey_question (
	survey_id varchar(10) NOT NULL,
	id varchar(10) NOT NULL,
	type varchar(1) NOT NULL,
	content TEXT NOT NULL,
	position integer NOT NULL,
	sub_title varchar(50) NULL,
	is_mandatory varchar(1) NULL,
primary key pk_survey_question (survey_id, id) 	
);

create table question_alternative (
	survey_id varchar(10) NOT NULL,
	question_id varchar(10) NOT NULL,
	sequence integer NOT NULL,
	content TEXT NOT NULL,
primary key pk_survey_question (survey_id, question_id, sequence)
);

create table question_answer (
	survey_id varchar(10) NOT NULL,
	question_id varchar(10) NOT NULL,
    answer_date timestamp NOT NULL,
	user_id varchar(10) NULL,
	value TEXT NOT NULL,
primary key pk_question_answer (survey_id, question_id, answer_date)	
);

create table resource_capacity (
	resource_id varchar(10) NOT NULL,
	project_id varchar(10) NOT NULL,
    cap_year int(4) NOT NULL,
	cap_month int(2) NOT NULL,
	cap_day int(2) NOT NULL,
	capacity int(4) NOT NULL,
	cost_per_hour int(10) NULL,
primary key pk_resource_capacity (resource_id, project_id, cap_year, cap_month, cap_day)	
);

create table customer_function (
	customer_id varchar(10) NOT NULL,
	project_id varchar(10) NOT NULL,
	function_id varchar(10) NOT NULL,
	creation_date timestamp(14) null,	
primary key pk_customer_function (customer_id, project_id, function_id)	
);

create table repository_file (
	id varchar(10) NOT NULL,
	repository_file_path TEXT NOT NULL,
	artifact_template_type TEXT NULL,
primary key pk_rep_file (id)	
);

create table repository_file_project (
	repository_file_id varchar(10) NOT NULL,
	project_id varchar(10) NOT NULL,
	is_disable int(1) NULL,
    is_indexable int(1) NULL,
	last_update timestamp(14) NOT NULL,	
primary key pk_rep_file_project (repository_file_id, project_id)	
);

create table repository_file_plan (
	repository_file_id varchar(10) NOT NULL,
	planning_id varchar(10) NOT NULL,
primary key pk_rep_file_plan (repository_file_id, planning_id)	
);

create table cost_status (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
	state_machine_order bigint(4),
primary key pk_cost_status (id)
);

create table cost (
	id varchar(10) NOT NULL,
	name varchar(30) NOT NULL,
	project_id varchar(10) NOT NULL,
    category_id varchar(10) NOT NULL,
	account_code varchar(30) NULL,
	expense_id varchar(10) NULL,
primary key pk_cost (id)	
);

create table cost_installment (
	cost_id varchar(10) NOT NULL,
	installment_num int(4) NOT NULL,
	due_date timestamp(14) NOT NULL,
	cost_status_id varchar(10) NOT NULL,
	approver varchar(10) NULL,
	value int(8) NOT NULL,
primary key pk_cost_installment (cost_id, installment_num)	
);

create table cost_history (
	cost_id varchar(10) NOT NULL,
	installment_num int(4) NOT NULL,
	creation_date timestamp(14) NULL,
	name varchar(30) NOT NULL,
	project_id varchar(10) NOT NULL,
    category_id varchar(10) NOT NULL,
	account_code varchar(30) NULL,
	expense_id varchar(10) NULL,
	due_date timestamp(14) NOT NULL,
	cost_status_id varchar(10) NOT NULL,
	value int(8) NOT NULL,
	user_id varchar(10) NULL,	
primary key pk_cost_history (cost_id, installment_num, creation_date)	
);


create table expense (
	id varchar(10) NOT NULL,
	project_id varchar(10) NOT NULL,
    user_id varchar(10) NOT NULL,
primary key pk_expense (id)
);

create table invoice_status (
	id varchar(10) not null,
	name varchar (50),
	description varchar (100),
	state_machine_order bigint(4),
primary key pk_invoice_status (id)
);

create table invoice (
	id varchar(10) NOT NULL,
	name varchar(50) NOT NULL,
	project_id varchar(10) NOT NULL,
    category_id varchar(10) NOT NULL,
	due_date timestamp(14) NOT NULL,
	invoice_status_id varchar(10) NOT NULL,
	invoice_date timestamp(14) NULL,
	invoice_number varchar(40) NULL,
	purchase_order varchar(40) NULL,
	contact varchar(70) NULL,
primary key pk_invoice (id)	
);

create table invoice_item (
	invoice_id varchar(10) NOT NULL,
	invoice_item_id varchar(10) NOT NULL,
	name varchar(50) null,
	type int(1) NOT NULL,
	price int(10) NOT NULL,
    amount int(3) NOT NULL,
    type_index int(2) NOT NULL,
primary key pk_invoice_item (invoice_id, invoice_item_id)	
);

create table invoice_history (
	invoice_id varchar (10) not null,
	creation_date timestamp(14) not null,
	name varchar(50) NOT NULL,
    category_id varchar(10) NOT NULL,
    invoice_status_id varchar(10) NOT NULL,
	due_date timestamp(14) NOT NULL,
	invoice_date timestamp(14) NULL,
	invoice_number varchar(40) NULL,
	purchase_order varchar(40) NULL,
	contact varchar(70) NULL,
	description TEXT NULL,
	total_price int(10) NULL,
	user_id varchar (10) not null,
primary key pk_invoice_hist (invoice_id, creation_date)
);

create table db_repository_sequence (
	id int(10) not null,
	project_id varchar(10) NOT NULL, 
primary key pk_rep_sequence (id, project_id)
);

create table db_repository_item (
	id varchar(10) NOT NULL,
	repository_file_path TEXT NOT NULL,
	repository_file_name TEXT NOT NULL,
	project_id varchar(10) NOT NULL,
	is_directory int(1) NULL,
	parent_id varchar(10) NULL,
primary key pk_rep_item (id)	
);

create table db_repository_version (
	repository_item_id varchar(10) not null,
	version int(10) not null,
	content_type varchar(40) not null,	
	creation_date timestamp(14) not null,	
	comment text null,
	user_id varchar (10) not null,
	file_size int(13) not null,
	binary_file mediumblob null,
primary key pk_rep_version (repository_item_id, version)	
);

create table repository_policy (
	project_id varchar(10) not null,
	type_policy varchar(30) not null,
	value text null,
primary key pk_repos_policies (project_id, type_policy)
);

create table artifact_template (
	id varchar(10) not null,
	name varchar(70) not null,
	description varchar(255) null,
	category_id varchar(10) not null,
	header_html text null,
	body_html text not null,
	footer_html text null,
	profile_view varchar(1) null,
primary key pk_artif_templ (id)
);

alter table artifact_template add constraint artif_templ_category foreign key (category_id) references category (id);

alter table invoice add constraint invoice_category foreign key (category_id) references category (id);

alter table invoice add constraint invoice_status foreign key (invoice_status_id) references invoice_status (id);

alter table invoice add constraint invoice_planning foreign key (id) references planning (id);

alter table invoice_item add constraint inv_item_invoice foreign key (invoice_id) references invoice (id);

alter table tool_user add constraint user_department foreign key (department_id) references department (id);

alter table tool_user add constraint user_area foreign key (area_id) references area (id);

alter table tool_user add constraint user_function foreign key (function_id) references function (id); 

alter table customer add constraint customer_user foreign key (id) references tool_user (id);

alter table resource add constraint resource_customer foreign key (id, project_id) references customer (id, project_id);

alter table leader add constraint leader_resource foreign key (id, project_id) references resource (id, project_id);

alter table root add constraint root_leader foreign key (id, project_id) references leader (id, project_id);

alter table customer add constraint customer_project foreign key (project_id) references project (id);

alter table project add constraint project_parent_project foreign key (parent_id) references project (id); 

alter table project add constraint project_status_project foreign key (project_status_id) references project_status (id); 

alter table requirement add constraint req_parent foreign key (id) references planning (id); 

alter table requirement add constraint req_cat foreign key (category_id) references category (id); 

alter table project add constraint project_parent foreign key (id) references planning (id); 

alter table requirement add constraint req_project foreign key (project_id) references project (id); 

alter table requirement add constraint req_status_req foreign key (requirement_status_id) references requirement_status (id); 

alter table requirement add constraint req_user foreign key (user_id) references tool_user (id); 

alter table requirement_history add constraint req_hist_req foreign key (requirement_id) references requirement (id); 

alter table requirement_history add constraint req_hist_status foreign key (requirement_status_id) references requirement_status (id); 

alter table requirement_history add constraint req_hist_user foreign key (user_id) references tool_user (id); 

alter table project_history add constraint prj_hist_prj foreign key (project_id) references project (id); 

alter table project_history add constraint prj_hist_status foreign key (project_status_id) references project_status (id); 

alter table task add constraint task_parent foreign key (id) references planning (id); 

alter table task add constraint task_proj foreign key (project_id) references project (id); 

alter table task add constraint task_req foreign key (requirement_id) references requirement (id); 

alter table task add constraint task_cat foreign key (category_id) references category (id); 

alter table task add constraint task_task foreign key (task_id) references task (id); 

alter table resource_task add constraint task_res_task foreign key (task_id) references task (id); 

alter table resource_task add constraint res_res_task foreign key (resource_id, project_id) references resource (id, project_id); 

alter table resource_task add constraint res_task_status_task foreign key (task_status_id) references task_status (id); 

alter table task_history add constraint task_hist_res_task foreign key (task_id, resource_id, project_id) references resource_task (task_id, resource_id, project_id); 

alter table task_history add constraint task_hist_status_task foreign key (task_status_id) references task_status (id);

alter table task_history add constraint task_hist_user foreign key (user_id) references tool_user (id); 

alter table resource_task_alloc add constraint alloc_res_task foreign key (task_id, resource_id, project_id) references resource_task (task_id, resource_id, project_id); 

alter table report add constraint report_project foreign key (project_id) references project (id); 

alter table report add constraint report_cat foreign key (category_id) references category (id); 

alter table report_result add constraint rep_result_rep_id foreign key (report_id) references report (id); 

alter table notification_field add constraint notif_field_notif_id foreign key (notification_id) references notification (id); 

alter table preference add constraint user_preference foreign key (user_id) references tool_user (id);

alter table meta_field add constraint meta_field_project foreign key (project_id) references project (id); 

alter table meta_field add constraint meta_field_category foreign key (category_id) references category (id); 
	
alter table additional_field add constraint addinfo_field_planning foreign key (planning_id) references planning (id); 

alter table additional_field add constraint addinfo_field_meta_field foreign key (meta_field_id) references meta_field (id); 

alter table category add constraint cat_project foreign key (project_id) references project (id); 

alter table occurrence add constraint occur_project foreign key (project_id) references project (id); 

alter table occurrence add constraint occurrence_parent foreign key (id) references planning (id); 

alter table occurrence_history add constraint occur_occur_hist foreign key (occurrence_id) references occurrence (id); 

alter table occurrence_field add constraint occur_occur_field foreign key (occurrence_id) references occurrence (id); 

alter table occurrence_dependency add constraint occur_dependency foreign key (occurrence_id) references occurrence (id); 

alter table occurrence_dependency add constraint occur_depend_planning foreign key (planning_id) references planning (id); 

alter table occurrence_kpi add constraint occur_kpi_occur foreign key (occurrence_id) references occurrence (id); 

alter table occurrence_kpi add constraint occur_kpi_rep foreign key (report_id) references report (id); 

alter table risk add constraint risk_project foreign key (project_id) references project (id); 

alter table risk_history add constraint risk_risk_hist foreign key (risk_id) references risk (id); 

alter table risk_history add constraint risk_status_hist foreign key (risk_status_id) references risk_status (id); 

alter table attachment add constraint planning_attach foreign key (planning_id) references planning (id);

alter table attachment_history add constraint attach_hist foreign key (attachment_id) references attachment (id); 

alter table discussion_topic add constraint planning_disc_topic foreign key (planning_id) references planning (id);

alter table discussion_topic add constraint parent_disc_topic foreign key (parent_topic) references discussion_topic (id);

alter table discussion_topic add constraint user_disc_topic foreign key (user_id) references tool_user (id);

alter table discussion add constraint discussion_project foreign key (project_id) references project (id);

alter table discussion add constraint discussion_category foreign key (category_id) references category (id);

alter table plan_relation add constraint plan_plan_relation foreign key (planning_id) references planning (id);

alter table custom_form add constraint custom_meta foreign key (meta_form_id) references meta_form (id);

alter table survey_question add constraint question_survey foreign key (survey_id) references survey (id);

alter table question_alternative add constraint alternat_question foreign key (survey_id, question_id) references survey_question (survey_id, id);

alter table question_answer add constraint answer_question foreign key (survey_id, question_id) references survey_question (survey_id, id);

alter table resource_capacity add constraint res_cap_resource foreign key (resource_id, project_id) references resource (id, project_id);

alter table customer_function add constraint cus_func_customer foreign key (customer_id) references customer (id);

alter table customer_function add constraint cus_func_project foreign key (project_id) references project (id);

alter table customer_function add constraint cus_func_function foreign key (function_id) references function (id);

alter table repository_file_project add constraint proj_rep_file_proj foreign key (project_id) references project (id);

alter table repository_file_project add constraint re_file_rep_file_proj foreign key (repository_file_id) references repository_file (id);

alter table repository_file_plan add constraint rep_file_plan_rep_file foreign key (repository_file_id) references repository_file (id);

alter table repository_file_plan add constraint rep_file_plan_plan foreign key (planning_id) references planning (id);

alter table repository_policy add constraint rep_polic_prj foreign key (project_id) references project (id); 

alter table cost add constraint cost_project foreign key (project_id) references project (id);

alter table cost add constraint cost_planning foreign key (id) references planning (id);

alter table cost_installment add constraint cost_inst_cost foreign key (cost_id) references cost (id);

insert into area (id,name,description) values ('1','Development','Development'); 
insert into area (id,name,description) values ('10','Purchasing','Purchasing'); 
insert into area (id,name,description) values ('2','Maintenance','Maintenance'); 
insert into area (id,name,description) values ('3','Support','Support'); 
insert into area (id,name,description) values ('4','Research','Research'); 
insert into area (id,name,description) values ('5','Accounting','Accounting'); 
insert into area (id,name,description) values ('6','Sales','Sales'); 
insert into area (id,name,description) values ('7','Customer Care','Customer Care'); 
insert into area (id,name,description) values ('8','Finance','Finance '); 
insert into area (id,name,description) values ('9','Management','Management'); 

insert into department (id,name,description) values ('1','Purchasing','Purchasing Department'); 
insert into department (id,name,description) values ('2','Finance','Finance Department'); 
insert into department (id,name,description) values ('3','HR','Human Resouce Department'); 
insert into department (id,name,description) values ('4','IT','IT Department'); 
insert into department (id,name,description) values ('5','Marketing','Marketing Department'); 
insert into department (id,name,description) values ('6','Management','Front Office'); 
insert into department (id,name,description) values ('7','Accounting','Accounting Department'); 
insert into department (id,name,description) values ('8','Commercial','Commercial Department'); 

insert into function (id,name,description) values ('1','Purchase Analyst','Purchase Analyst'); 
insert into function (id,name,description) values ('2','Admin Staff','Administrative Assistance'); 
insert into function (id,name,description) values ('3','Coordinator','Coordinator'); 
insert into function (id,name,description) values ('4','Director','Director'); 
insert into function (id,name,description) values ('5','President','President'); 
insert into function (id,name,description) values ('6','Management','Management Department'); 
insert into function (id,name,description) values ('7','System Analyst','System Analyst'); 
insert into function (id,name,description) values ('8','Purchasing','Purchasing'); 
insert into function (id,name,description) values ('9','Seller','Seller'); 
insert into function (id,name,description) values ('10','CSR','Customer Support Rep'); 
insert into function (id,name,description) values ('11','Finance Analyst','Finance Analyst'); 
insert into function (id,name,description) values ('12','Admin staff','Administrative Staff'); 
insert into function (id,name,description) values ('13','DBA','Data Base administrator'); 
insert into function (id,name,description) values ('14','Support Analyst','Support Analyst (Helpdesk)'); 
insert into function (id,name,description) values ('15','Computer Programmer','Computer Programmer');
insert into function (id,name,description) values ('16','End User','End User');

insert into project_status (id,name,note,state_machine_order) values ('1','Open','Open Project',1); 
insert into project_status (id,name,note,state_machine_order) values ('2','Closed','Closed Project',3); 
insert into project_status (id,name,note,state_machine_order) values ('3','Aborted','Aborted Project',3); 
insert into project_status (id,name,note,state_machine_order) values ('4','on-Hold','On-hold Project',2); 

insert into task_status (id,name,description,state_machine_order) values ('1','Open','Open',1); 
insert into task_status (id,name,description,state_machine_order) values ('2','Closed','Close',100); 
insert into task_status (id,name,description,state_machine_order) values ('3','Cancelled','Cancelled',101); 
insert into task_status (id,name,description,state_machine_order) values ('4','In-Progress','In-Progress',20); 
insert into task_status (id,name,description,state_machine_order) values ('5','On Hold','On Hold',50); 
insert into task_status (id,name,description,state_machine_order) values ('6','Reopen','Reopen',2);

insert into requirement_status (id,name,description,state_machine_order) values ('1','Waiting Approval','Waiting Approval',1); 
insert into requirement_status (id,name,description,state_machine_order) values ('3','Refused','Refused',202); 
insert into requirement_status (id,name,description,state_machine_order) values ('4','Closed','Closed',201); 
insert into requirement_status (id,name,description,state_machine_order) values ('5','Cancelled','Cancelled',200); 
insert into requirement_status (id,name,description,state_machine_order) values ('6','Planned','Planned',100); 
insert into requirement_status (id,name,description,state_machine_order) values ('7','in-Progress','in-Progress', 300); 

insert into tool_user (id,username,password,name,email,phone,color,department_id,area_id,function_id,country,language) values ('2','franz','','Franz Kafka',NULL,NULL,'C0c0c0','2','4','11','BR','pt'); 
insert into tool_user (id,username,password,name,email,phone,color,department_id,area_id,function_id,country,language) values ('1','root','','System Root', 'DO NOT REMOVE THIS USER',NULL,'000000','4','2','7','BR','pt'); 
insert into tool_user (id,username,password,name,email,phone,color,department_id,area_id,function_id,country,language) values ('4','camus','','Albert Camus',NULL,NULL,'1199AA','6','9','3','BR','pt'); 
insert into tool_user (id,username,password,name,email,phone,color,department_id,area_id,function_id,country,language) values ('5','noam','','Noam Chomsky',NULL,NULL,'F1E3AB','4','4','3','BR','pt'); 

insert into invoice_status (id,name,description,state_machine_order) values ('1','Bugdet','Bugdet',1); 
insert into invoice_status (id,name,description,state_machine_order) values ('2','Paid','Paid',100); 
insert into invoice_status (id,name,description,state_machine_order) values ('3','Cancelled','Cancelled',101); 
insert into invoice_status (id,name,description,state_machine_order) values ('4','Reviewed','Reviewed',40); 
insert into invoice_status (id,name,description,state_machine_order) values ('5','Submitted','Submitted',50); 

insert into planning (id,description,creation_date,final_date) values ('0','Root Project - do NOT remove this project!','2007-01-01 00:00:00',null); 
insert into planning (id,description,creation_date,final_date) values ('1','Help Desk Example Description','2007-01-01 00:00:00',null); 
insert into planning (id,description,creation_date,final_date) values ('2','Development Example Description','2007-10-10 00:00:00',null); 
insert into project (id,name,parent_id,project_status_id) values ('0','',NULL,'1'); 
insert into project (id,name,parent_id,project_status_id) values ('1','Help Desk Project Example','0','1'); 
insert into project (id,name,parent_id,project_status_id) values ('2','Development Project Example','0','1'); 

insert into category (id,name,description, type, project_id) values ('0','',NULL,NULL,NULL); -- MANDATORY
insert into category (id,name,description, type, project_id, billable) values ('1','Implementation' ,NULL,0, NULL, 1); 
insert into category (id,name,description, type, project_id, billable) values ('2','Analysis'       ,NULL,0, NULL, 1); 
insert into category (id,name,description, type, project_id, billable, is_defect, is_testing) values ('3','Testing',NULL,0, NULL, 0, 0, 1); 
insert into category (id,name,description, type, project_id, billable, is_defect, is_testing) values ('4','Maintenance',NULL,0, NULL, 0, 1, 0); 
insert into category (id,name,description, type, project_id, billable) values ('5','Support Service',NULL,0, NULL, 1); 
insert into category (id,name,description, type, project_id, billable) values ('6','Training'      ,NULL,0, NULL, 0); 
insert into category (id,name,description, type, project_id) values ('7','Bug'            ,NULL,1, 2); 
insert into category (id,name,description, type, project_id) values ('8','Enhancement'    ,NULL,1, 2); 
insert into category (id,name,description, type, project_id) values ('9','Change'         ,NULL,1, 2); 
insert into category (id,name,description, type, project_id) values ('10','Failure-Software-Application',NULL,1,1); 
insert into category (id,name,description, type, project_id) values ('11','Failure-Software-ERP', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('12','Failure-Software-eMail', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('13','Failure-Software-Internet', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('14','Failure-Software-Other', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('15','Failure-Hardware-Desktop', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('16','Failure-Hardware-Printer', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('17','Failure-Hardware-Connectivity', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('18','Failure-Hardware-Other', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('19','Service-HelpDesk-Password', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('20','Service-HelpDesk-Connectivity', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('21','Service-HelpDesk-Software', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('22','Service-HelpDesk-Other', NULL,1, 1); 
insert into category (id,name,description, type, project_id) values ('23','Other', NULL,1, NULL); 
insert into category (id,name,description, type, project_id) values ('24','Technical Risk', NULL,4, NULL); 
insert into category (id,name,description, type, project_id) values ('25','Project Management Risk', NULL,4, NULL); 
insert into category (id,name,description, type, project_id) values ('26','Organizational Risk', NULL,4, NULL); 
insert into category (id,name,description, type, project_id) values ('27','External Risk', NULL,4, NULL);
insert into category (id,name,description, type, project_id) values ('28','Project Management', NULL, 2, NULL); 
insert into category (id,name,description, type, project_id) values ('29','Resource Management', NULL, 2, NULL); 
insert into category (id,name,description, type, project_id) values ('30','Business Management', NULL, 2, NULL); 
insert into category (id,name,description, type, project_id) values ('31','Management', NULL, 3, NULL); 
insert into category (id,name,description, type, project_id) values ('32','Customer Relationship', NULL, 3, NULL); 
insert into category (id,name,description, type, project_id) values ('33','Cost Center A', NULL, 7, NULL);
insert into category (id,name,description, type, project_id) values ('34','Cost Center B', NULL, 7, NULL);
insert into category (id,name,description, type, project_id) values ('35','Cost Center N', NULL, 7, NULL); 

insert into customer (id,project_id) values ('2','0'); 
insert into customer (id,project_id) values ('1','0'); 
insert into customer (id,project_id) values ('4','0'); 
insert into customer (id,project_id) values ('5','0'); 
insert into customer (id,project_id) values ('2','1'); 
insert into customer (id,project_id) values ('4','1'); 
insert into customer (id,project_id) values ('5','1'); 
insert into customer (id,project_id) values ('2','2'); 
insert into customer (id,project_id) values ('4','2'); 
insert into customer (id,project_id) values ('5','2'); 

insert into resource (id,project_id,capacity_per_day) values ('1','0', 480); 
insert into resource (id,project_id,capacity_per_day) values ('5','1', 480); 
insert into resource (id,project_id,capacity_per_day) values ('2','2', 480); 
insert into resource (id,project_id,capacity_per_day) values ('4','2', 480); 

insert into customer (id,project_id) values ('1','1'); 
insert into customer (id,project_id) values ('1','2'); 
insert into resource (id,project_id,capacity_per_day) values ('1','1', 480); 
insert into resource (id,project_id,capacity_per_day) values ('1','2', 480); 


insert into leader (id,project_id) values ('1','0'); 
insert into leader (id,project_id) values ('5','1'); 
insert into leader (id,project_id) values ('2','2'); 

insert into risk_status (id, name, description, status_type) values ('1', 'Identified', 'Identified', '0'); 
insert into risk_status (id, name, description, status_type) values ('2', 'Materialized', 'Materialized', '1'); 
insert into risk_status (id, name, description, status_type) values ('3', 'Cancelled', 'Cancelled', '2'); 

insert into root (id,project_id) values ('1','0'); 

insert into p_sequence (id) values (200);

insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('1', 'Project Issues Analysis Report', 0, '', 'select distinct project_id, p.name from requirement r, project p where p.id = r.project_id and (p.id = ?#PROJECT_ID# or p.id in (select id from project where parent_id=?#PROJECT_ID#))', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/projectReq.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('2', 'Project KPI Histogram', 0, '', 'select rr.report_id as report_id , r.name as name, cast(rr.value as SIGNED) as val, count(rr.value) as count from report_result rr, report r where rr.report_id = r.id and r.report_perspective_id <> 0 and r.data_type = 0 and rr.last_execution >= ?#Initial Date{}(2)# and rr.last_execution <= ?#Final Date{}(2)# and rr.value <> "0" group by report_id, val', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/KPIHistogram.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('3', 'Backlog Report', 0, '', 'select u.id as user_id, u.name, c.name as category_name, c.id as category_id, ts.name as task_status, ts.id as statusId, t.name as task_name, rt.project_id, p.name as project_name, rt.start_date, rt.estimated_time, rt.actual_date, rt.actual_time from resource_task rt, task t, tool_user u, project p, task_status ts, category c where rt.task_id=t.id and rt.resource_id = u.id and rt.project_id = p.id and rt.task_status_id = ts.id and t.category_id = c.id and (ts.state_machine_order <> 100 and ts.state_machine_order <> 102) and (p.id = ?#PROJECT_ID# or p.id in (select id from project WHERE parent_id=?#PROJECT_ID#)) order by u.id, ts.state_machine_order, c.id', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/backlog.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('4', 'Requirement Overview', 0, '', 'select r.id, r.suggested_date, r.deadline_date, r.priority, r.reopening, rs.name as status_name, c.name as category_name, p.description, p.creation_date, pr.name as project_name from requirement r, requirement_status rs, category c, planning p, project pr where r.category_id=c.id and r.requirement_status_id = rs.id and r.project_id = pr.id and r.id = p.id and r.id in (?#Requirement ID{ }(1)#)', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/ReqOverview.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('5', 'Estimated Time X Actual Time', 0, '', 'select rt.estimated_time/60 estimated_time, rt.actual_time/60 actual_time, rt.project_id from planning p, resource_task rt where rt.task_id = p.id and p.final_date is not null and (rt.project_id=?#PROJECT_ID# or rt.project_id in (select id from project WHERE parent_id=?#PROJECT_ID#))', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/estimatedActualAnalysis.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('6', 'Occurrences Book', 0, '', 'select o.id, o.source, o.project_id, p.name as project_name from occurrence o, project p where p.id = o.project_id and (o.project_id=?#PROJECT_ID# or o.project_id in (select id from project WHERE parent_id=?#PROJECT_ID#))', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/OccurrenceBook.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('7', 'My Tasks Report', 0, '', 'select u.username, u.name as fullname, p.id as project_id, p.name as project, c.name as category, sub.task_id, t.name, sub.bucket_date, sum(sub.alloc_time) as s from ( select rt.task_id, rt.resource_id, rt.project_id, rta.sequence, rta.alloc_time, rt.actual_date, ADDDATE(rt.actual_date, rta.sequence-1) as bucket_date from resource_task rt, resource_task_alloc rta where rt.task_id = rta.task_id and rt.resource_id = rta.resource_id and rt.project_id = rta.project_id and rta.alloc_time > 0 and rt.actual_date is not null ) as sub, tool_user u, task t, project p, category c where sub.resource_id = u.id and sub.task_id = t.id and sub.project_id = p.id and t.category_id = c.id and sub.bucket_date >= ?#Initial Date{}(2)# and sub.bucket_date <= ?#Final Date{}(2)# and u.id=?#USER_ID# group by p.name, u.username, p.id , u.name, sub.task_id, t.name, c.name, sub.bucket_date order by u.username, sub.bucket_date, p.name, c.name', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/MyTaskReport.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('8', 'Survey Report', 0, '', 'select s.id, s.name, s.description, s.is_anonymous, s.project_id, s.creation_date, s.final_date, s.date_publishing, p.name as project_name, q.content, q.is_mandatory, q.type, q.id as question_id from survey s, project p, survey_question q where s.project_id=p.id and q.survey_id = s.id and s.id = ?#Survey{!select id, name from survey where project_id=?#PROJECT_ID#!}(1)# order by s.id, q.position, q.sub_title', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/SurveyReport.jasper', '2');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('9', 'Expense Report', 0, '', 'select e.id, t.name, pr.name as PROJECT_NAME, p.description, p.creation_date, c.id as COST_ID, c.name as COST_NAME, g.name as CATEGORY, c.account_code, ci.due_date, ci.value, at.username as approver, cs.name as STATUS, cs.state_machine_order from expense e, tool_user t, project pr, planning p, cost c, category g, cost_status cs, cost_installment ci LEFT OUTER JOIN tool_user at on (at.id = ci.approver) where e.user_id = t.id and e.project_id = pr.id and e.id = p.id and c.category_id = g.id and c.expense_id = e.id and c.id = ci.cost_id and ci.installment_num=1 and ci.cost_status_id = cs.id and e.id=?#Expense{!select id, id as lbl from expense where user_id in (select id from resource where project_id in (?#PROJECT_DESCENDANT#)) or user_id=?#USER_ID#!}(1)# order by e.id', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/ExpenseReport.jasper', '3');

insert into cost_status (id, name, description, state_machine_order) values ('1', 'New', 'New', 1);
insert into cost_status (id, name, description, state_machine_order) values ('2', 'Denied', 'Denied', 101);

insert into resource_capacity
select distinct r.id, r.project_id, EXTRACT(YEAR FROM p.creation_date), 
      EXTRACT(MONTH FROM p.creation_date), EXTRACT(DAY FROM p.creation_date),
      r.capacity_per_day, (r.cost_per_hour * 100)
from resource as r, customer as c, planning p
where r.id = c.id and r.project_id = c.project_id
and c.project_id = p.id
and (c.is_disable is null or c.is_disable=0) 
and r.capacity_per_day is not null