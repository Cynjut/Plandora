drop table if exists project_qualifier_value;
drop table if exists user_edi_link;
drop table if exists external_data_attach;
drop table if exists external_data;
drop table if exists artifact_template;
drop table if exists artifact;
drop table if exists db_repository_version;
drop table if exists db_repository_item;
drop table if exists db_repository_sequence;
drop table if exists expense;
drop table if exists cost_history;
drop table if exists cost_installment;
drop table if exists cost;
drop table if exists cost_status;
drop table if exists invoice_history;
drop table if exists invoice_item;
drop table if exists invoice;
drop table if exists invoice_status;
drop table if exists repository_history;
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
drop table if exists occurrence_field_table;
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
drop table if exists project_report;
drop table if exists report;
drop table if exists notification_field;
drop table if exists notification;
drop table if exists resource_task_alloc;
drop table if exists resource_task_alloc_plann;
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
drop table if exists company;
drop table if exists event_log;

drop table if exists p_sequence;


create table p_sequence (
	id bigint(9) not null, 
	primary key pk_p_sequence (id)
);

create table event_log (
	summary bigint(9) not null,
	description text,
	creation_date timestamp not null,	
	username varchar(30) not null
);

create table company (
	id bigint(9) not null,
	name varchar(25) not null,
	full_name varchar(255) null,
	company_number varchar(255) null,
	address varchar(255) null,
	city varchar(255) null,
	state_province varchar(50) null,
	country varchar(100) null,
primary key pk_company (id) 
);

create table tool_user (
	id bigint(9) not null,
	username varchar(30) not null,
	password varchar(70) null,
	name varchar(50) not null,
	email varchar(70),
	phone varchar(20),
	color varchar(10),
	department_id bigint(9) not null,
	area_id bigint(9) not null,
	function_id bigint(9) null,
	company_id bigint(9) null,
	country varchar(2) not null,
	language varchar(2) not null,
	auth_mode varchar(200) null,
	birth date null,
	permission text null,
	pic_file mediumblob null,
	final_date timestamp null,
	creation_date timestamp null,	
primary key pk_user (id) 
);
 
create table department (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
primary key pk_department (id) 
);

create table function (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
primary key pk_function (id)
);

create table area (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
primary key pk_area (id)
);

create table category (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
	type int(2) null,
	project_id bigint(9) null,
    billable int(1) NULL,
	disable_view int(1) NULL,
	is_defect int(1) NULL,
	is_testing int(1) NULL,
	is_developing int(1) NULL,
	category_order int(2) null,
primary key pk_category (id)
);

create table customer (
	id bigint(9) not null,
	project_id bigint(9) not null,
	is_disable int(1) null,	
	is_req_acceptable int(1) null,
	can_see_tech_comment int(1) null,
    	pre_approve_req int(1) null,
	can_see_discussion int(1) null,
	function_id bigint(9) null,
	can_see_other_reqs int(1) null,
	can_open_otherowner_reqs int(1) null,
primary key pk_customer (id, project_id)
);

create table resource (
	id bigint(9) not null,
	project_id bigint(9) not null,
	can_self_alloc int(1) null,
	can_see_repository int(1) null,
	can_see_invoice int(1) null,
	cost_per_hour decimal(9,2) null,
	can_see_customer int(1) null,
	capacity_per_day decimal(9,2) null,
primary key pk_resource (id, project_id)
);

create table leader (
	id bigint(9) not null,
	project_id bigint(9) not null,
primary key pk_leader (id, project_id)
);

create table root (
	id bigint(9) not null,
	project_id bigint(10) not null,
primary key pk_root (id, project_id)
);

create table project_status (
	id bigint(9) not null,
	name varchar(50),
	note varchar(100),
	state_machine_order bigint(4) ,
primary key pk_proj_status (id)
);

create table requirement_status (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
	state_machine_order bigint(4),
	accept_project_id bigint(9) null,
	parent_id bigint(9) null,
primary key pk_req_status (id)
);

create table task_status (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
	state_machine_order bigint(4),
primary key pk_task_status (id)
);

create table project (
	id bigint(9) not null,
	name varchar(30),
	parent_id bigint(10),
	can_alloc varchar(1) null,
	project_status_id bigint(10) not null,
	repository_url varchar(200),
	repository_class varchar(200),
	repository_user varchar(40),
	repository_pass varchar(40),
	estimated_closure_date timestamp null,
	allow_billable int(1) null,
	company_id bigint(9) null,
	budget bigint(20) NOT NULL,
primary key pk_project (id)
) ;

create table requirement (
	id bigint(9) not null,
	suggested_date timestamp null,
	deadline_date timestamp null,	
	project_id bigint(10) not null,
	user_id bigint(10) not null,
	requirement_status_id bigint(10) not null,
	priority int(1) not null,
	is_acceptance int(1) null,
	category_id bigint(10) null,
	reopening bigint(6) null,
primary key pk_req (id)
);

create table task (
	id bigint(9) not null,
	name varchar(50),
	project_id bigint(10) not null,
	requirement_id bigint(10),
	category_id bigint(10) not null,
	task_id bigint(10),
	is_parent_task int(1),
	created_by bigint(10) null,
	is_unpredictable int(1) null,
primary key pk_task (id)
);

create table resource_task (
	task_id bigint(10) not null,
	resource_id bigint(10) not null,
	project_id bigint(10) not null,
	start_date timestamp not null,
	estimated_time bigint(6) not null,
	actual_date timestamp null,
	actual_time bigint(6) null,	
	task_status_id bigint(10) not null,
	is_acceptance_task int(1),
	billable int(1) null,
primary key pk_resourcetask (task_id, resource_id, project_id)
);


create table planning (
	id bigint(9) not null,
	description text,
	creation_date timestamp not null,
	final_date timestamp null,
	iteration bigint(10) null,
 	rich_text_desc text null,
	visible varchar(1) null,
primary key pk_planning (id)
);

create table requirement_history (
	requirement_id bigint(10) not null,
	requirement_status_id bigint(10) not null,
	creation_date timestamp not null,
	user_id bigint(10),	
	iteration bigint(10) null,
	comment text,		
primary key pk_req_hist (requirement_id, requirement_status_id, creation_date)
);

create table project_history (
	project_id bigint(10) not null,
	project_status_id bigint(10) not null,
	creation_date timestamp not null,
primary key pk_prj_hist (project_id, project_status_id, creation_date)
);

create table task_history (
	task_id bigint(10) not null,
	resource_id bigint(10) not null,
	project_id bigint(10) not null,
	task_status_id bigint(10) not null,
	creation_date timestamp not null,	
	start_date timestamp not null,
	estimated_time bigint(6) not null,
	actual_date timestamp null,
	actual_time bigint(6) null,		
	user_id bigint(10),
	iteration bigint(10) null,	
	comment text,
primary key pk_task_hist (task_id, resource_id, project_id, task_status_id, creation_date)
);

create table resource_task_alloc (
	task_id bigint(10) not null,
	resource_id bigint(10) not null,
	project_id bigint(10) not null,
	sequence bigint(6) not null,
	alloc_time bigint(6) not null,
primary key pk_resourcetaskalloc (task_id, resource_id, project_id, sequence)
);

create table resource_task_alloc_plann (
	task_id bigint(10) not null,
	resource_id bigint(10) not null,
	project_id bigint(10) not null,
	sequence bigint(6) not null,
	alloc_time bigint(6) not null,
primary key pk_resourcetaskallocplann (task_id, resource_id, project_id, sequence)
);

create table report (
	id bigint(9) not null,
	name varchar(100) null,
	description text null,
	type int(2) not null,
	kpi_type int(2) null,
	report_perspective_id bigint(9) not null,
	sql_text text null,
	execution_hour int(2) not null,
	last_execution timestamp null,
	project_id bigint(9) not null,
	final_date timestamp null,
	data_type int(2) not null,
	file_name varchar(100) null,
	category_id bigint(9) null,
	profile_view varchar(1) null,
	goal varchar(25) null,
	tolerance varchar(25) null,
	tolerance_type varchar(2) null,
primary key pk_report (id)
);


create table report_result (
	report_id bigint(9) not null,
	project_id bigint(9) not null,
	last_execution timestamp null,
	value varchar(25) not null,
primary key pk_report_result (report_id, project_id, last_execution)
);


create table project_report (
	report_id bigint(9) not null,
	project_id bigint(9) not null,
primary key pk_project_report (report_id, project_id)
);


create table notification (
	id bigint(9) not null,
	name varchar(50) not null,
	description varchar(100) null,	
	notification_class varchar(100) not null,
	sql_text text not null,
	retry_number int(2) not null,
	next_notification timestamp not null,
	final_date timestamp null,
	last_check timestamp not null,	
	period_minute int(3) null,	
	period_hour int(3) null,		
	periodicity int(2) null not null,
primary key pk_notification (id)
);

create table notification_field (
	notification_id bigint(9) not null,
	name varchar(100) not null,
	value text null,
primary key pk_notif_field (notification_id, name)
);

create table preference (
	user_id bigint(9) not null,
	id varchar(50) null,
	value text not null,
primary key pk_preference (user_id, id)
);

create table meta_field (
	id bigint(9) not null,
	name varchar(50) null,
	type int(1) not null,
	apply_to int(1) not null,	
	project_id bigint(9) not null,	
	category_id bigint(9) null,		
	domain text not null,
	final_date timestamp null,
	meta_form_id bigint(9) null,
	help_content varchar(300) null,
	field_order int(2) null,	
	is_mandatory varchar(1),
	is_qualifier varchar(1),
primary key pk_metafield (id)
);

create table additional_field (
	planning_id bigint(9) not null,
	meta_field_id bigint(9) not null,	
	value text not null,
	date_value timestamp null,
	numeric_value decimal(13,5) null,
primary key pk_additional_field (planning_id, meta_field_id)
);

create table additional_table (
	planning_id bigint(9) not null,
	meta_field_id bigint(9) not null,	
    line int(6) not null,
    col int(6) not null,
	value text not null,
	date_value timestamp null,
primary key pk_additional_table (planning_id, meta_field_id, line, col)
);

create table occurrence (
	id bigint(9) not null,
	source varchar(100) not null,
	project_id bigint(9) not null,
	name varchar(255) null,
	loc varchar(7) null,
	occurrence_status varchar(10) null,	
	occurrence_status_label varchar(60) null,
primary key pk_occurrence (id)
);

create table occurrence_dependency (
	occurrence_id bigint(9) not null,
	planning_id bigint(9) not null,
primary key pk_occur_dependency (occurrence_id, planning_id)
);

create table occurrence_field (
	occurrence_id bigint(9) not null,
	field varchar(10) not null,	
	value text not null,
	date_value timestamp null,
primary key pk_occur_field (occurrence_id, field)
);

create table occurrence_field_table (
	occurrence_id bigint(9) not null,
	occ_field varchar(10) not null,
	field varchar(10) not null,	
    line int(6) not null,
    col int(6) not null,
	value text null,
	date_value timestamp null,
primary key pk_occurrence_field_table (occurrence_id, occ_field, field, line, col)
);

create table occurrence_history (
	occurrence_id bigint(9) not null,
	occurrence_status varchar(10) not null,
	occurrence_status_label varchar(60) not null,	
	creation_date timestamp not null,
	user_id bigint(9) not null,
	history TEXT,
primary key pk_occur_history (occurrence_id, occurrence_status, creation_date, user_id)
);

create table occurrence_kpi (
	report_id bigint(9) not null,
	occurrence_id bigint(9) not null,
	creation_date timestamp null,
	weight int(1) null,
primary key pk_occurrence_kpi (report_id, occurrence_id)
);

        
create table risk (
	id bigint(9) not null,
	project_id bigint(9) not null,
	category_id bigint(9) not null,
	name varchar(50) not null,
	probability varchar(2) not null,
	impact varchar(2) not null,
	tendency varchar(2) not null,	
	responsible varchar(40) null,	
	risk_status_id bigint(9) not null,
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
	risk_id bigint(9) not null,
	risk_status_id bigint(9) not null,
	risk_status_label varchar(60) null,	
	creation_date timestamp not null,
	user_id bigint(9) not null,
	history TEXT,
	probability varchar(2) null,
	impact varchar(2) null,
	tendency varchar(2) null,	
	impact_cost varchar(1) null,
	impact_time varchar(1) null,
	impact_quality varchar(1) null,
	impact_scope varchar(1) null,
	risk_type varchar(1) null,	
	name text null,
	description text null,
	responsible varchar(40) null,
    category_id bigint(9) null,
    strategy TEXT null,
    contingency TEXT null,	
primary key pk_risk_history (risk_id, risk_status_id, creation_date, user_id)
);

create table risk_status (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
	status_type varchar(1) not null,
primary key pk_risk_status (id)
);

create table attachment (
	id bigint(9) not null,
	planning_id bigint(9) null,
	name varchar(255),
	status varchar(2) not null,
	visibility varchar(2) not null,
	type varchar(40) not null,
	content_type varchar(255) not null,	
	creation_date timestamp not null,	
	comment text null,
	binary_file mediumblob not null,
primary key pk_attachment (id)
);

create table attachment_history (
	attachment_id bigint(9) not null,
	status varchar(2) not null,
	creation_date timestamp not null,	
	user_id bigint(9) not null,
	history TEXT,
primary key pk_attach_history (attachment_id, status, creation_date, user_id)
);

create table discussion_topic (
	id bigint(9) not null,
	planning_id bigint(9) not null,
	content text null,
	parent_topic bigint(9) null,
	user_id bigint(9) null,
	creation_date timestamp not null,
primary key pk_discussion_topic (id)
);

create table discussion (
	id bigint(9) not null,
	project_id bigint(9) null,
	name varchar(255) not null,
	owner varchar(10) null,
	category_id bigint(9) not null,
	is_blocked int(1) null,
primary key pk_discussion (id)
);

create table plan_relation (
	planning_id bigint(9) not null,
	plan_related_id bigint(9) not null,
	plan_type varchar(2) not null,
	plan_related_type varchar(2) not null,
	relation_type varchar(2) not null,
primary key pk_plan_relation (planning_id, plan_related_id)
);

create table template (
	id bigint(9) not null,
	name varchar(50) not null,
	deprecated_date timestamp null,
	root_node_id bigint(9) not null,
	category_id bigint(9) null,
primary key pk_template (id)
);

create table node_template (
	id bigint(9) not null,
	name varchar(50) not null,
	description text null,
	node_type varchar(2) not null,
	planning_id bigint(9) not null,
	project_id bigint(9) null,	
	next_node_id bigint(9) null,	
primary key pk_node_template (id)
);

create table step_node_template (
	id bigint(9) not null,
	category_id bigint(9) not null,
	resource_list text null,	
	category_regex varchar(40) null,
	iteration varchar(10) null,
primary key pk_step_node_template (id)
);

create table decision_node_template (
	id bigint(9) not null,
	question_content text null,
	next_node_id_if_false varchar(10) null,
primary key pk_decision_node_template (id)
);

create table custom_node_template (
	node_template_id bigint(9) not null,
	template_id bigint(9) not null,
	instance_id bigint(9) not null,	
	name varchar(50) not null,
	description text null,
	planning_id bigint(9) null,
	project_id bigint(9) null,	
	category_id bigint(9) null,
	related_task_id bigint(9) null,
	resource_list text null,
	question_content text null,
	is_parent_task integer null,
	decision_answer text null,
	iteration varchar(10) null,
primary key pk_custom_node_template (node_template_id, template_id, instance_id)
);


create table meta_form (
	id bigint(9) not null,
	name varchar(30) not null,
	viewable_cols text null,
	grid_row_num integer NULL,
	filter_col_id bigint(9) null,
	js_before_save text null,
	js_after_save text null,
	js_after_load text null,
primary key pk_meta_form (id)
);

create table custom_form (
	id bigint(9) not null,
	meta_form_id bigint(9) not null,
primary key pk_custom_form (id)
);

create table survey (
	id bigint(9) NOT NULL,
	name varchar(50) NOT NULL,
	description TEXT NULL,
	is_template varchar(1) NOT NULL,
	is_anonymous varchar(1) NOT NULL,
	project_id bigint(9) NOT NULL,
	creation_date timestamp NOT NULL,
	final_date timestamp NULL,
	date_publishing timestamp NOT NULL,
	anonymous_key varchar(70) NOT NULL,
primary key pk_survey (id) 
);

create table survey_question (
	survey_id bigint(9) NOT NULL,
	id bigint(9) NOT NULL,
	type varchar(1) NOT NULL,
	content TEXT NOT NULL,
	position integer NOT NULL,
	sub_title varchar(50) NULL,
	is_mandatory varchar(1) NULL,
primary key pk_survey_question (survey_id, id) 	
);

create table question_alternative (
	survey_id bigint(9) NOT NULL,
	question_id bigint(9) NOT NULL,
	sequence integer NOT NULL,
	content TEXT NOT NULL,
primary key pk_survey_question (survey_id, question_id, sequence)
);

create table question_answer (
	survey_id bigint(9) NOT NULL,
	question_id bigint(9) NOT NULL,
    answer_date timestamp NOT NULL,
	user_id bigint(9) NULL,
	value TEXT NOT NULL,
primary key pk_question_answer (survey_id, question_id, answer_date)	
);

create table resource_capacity (
	resource_id bigint(9) NOT NULL,
	project_id bigint(9) NOT NULL,
    cap_year int(4) NOT NULL,
	cap_month int(2) NOT NULL,
	cap_day int(2) NOT NULL,
	capacity int(4) NOT NULL,
	cost_per_hour int(10) NULL,
primary key pk_resource_capacity (resource_id, project_id, cap_year, cap_month, cap_day)	
);

create table customer_function (
	customer_id bigint(9) NOT NULL,
	project_id bigint(9) NOT NULL,
	function_id bigint(9) NOT NULL,
	creation_date timestamp null,	
primary key pk_customer_function (customer_id, project_id, function_id)	
);

create table repository_file (
	id bigint(9) NOT NULL,
	repository_file_path TEXT NOT NULL,
primary key pk_rep_file (id)	
);

create table repository_file_project (
	repository_file_id bigint(9) NOT NULL,
	project_id bigint(9) NOT NULL,
	is_disable int(1) NULL,
    is_indexable int(1) NULL,
    is_downloadable int(1) null,
	last_update timestamp NOT NULL,	
primary key pk_rep_file_project (repository_file_id, project_id)	
);

create table repository_file_plan (
	repository_file_id bigint(9) NOT NULL,
	planning_id bigint(9) NOT NULL,
primary key pk_rep_file_plan (repository_file_id, planning_id)	
);

create table repository_history (
	repository_file TEXT NOT NULL,
	repository_file_path TEXT NOT NULL,
	creation_date timestamp NOT NULL,
	project_id bigint(9) NOT NULL,
	user_id bigint(9) NULL,
	hist_type varchar(10) NOT NULL,
	comment TEXT NULL	
);

create table cost_status (
	id bigint(9) not null,
	name varchar(50) not null,
	description varchar(100),
	state_machine_order bigint(4) not null,
primary key pk_cost_status (id)
);

create table cost (
	id bigint(9) NOT NULL,
	name varchar(30) NOT NULL,
	project_id bigint(9) NOT NULL,
    category_id bigint(9) NOT NULL,
	account_code varchar(30) NULL,
	expense_id bigint(9) NULL,
primary key pk_cost (id)	
);

create table cost_installment (
	cost_id bigint(9) NOT NULL,
	installment_num int(4) NOT NULL,
	due_date timestamp NOT NULL,
	cost_status_id bigint(9) NOT NULL,
	approver varchar(10) NULL,
	value bigint(20) NOT NULL,
primary key pk_cost_installment (cost_id, installment_num)	
);

create table cost_history (
	cost_id bigint(9) NOT NULL,
	installment_num int(4) NOT NULL,
	creation_date timestamp NOT NULL,
	name varchar(30) NOT NULL,
	project_id bigint(9) NOT NULL,
    category_id bigint(9) NOT NULL,
	account_code varchar(30) NULL,
	expense_id bigint(9) NULL,
	due_date timestamp NOT NULL,
	cost_status_id bigint(9) NOT NULL,
	value bigint(20) NOT NULL,
	user_id bigint(9) NULL,	
primary key pk_cost_history (cost_id, installment_num, creation_date)	
);


create table expense (
	id bigint(9) NOT NULL,
	project_id bigint(9) NOT NULL,
    user_id bigint(9) NOT NULL,
primary key pk_expense (id)
);

create table invoice_status (
	id bigint(9) not null,
	name varchar(50),
	description varchar(100),
	state_machine_order bigint(4),
primary key pk_invoice_status (id)
);

create table invoice (
	id bigint(9) NOT NULL,
	name varchar(50) NOT NULL,
	project_id bigint(9) NOT NULL,
    category_id bigint(9) NOT NULL,
	due_date timestamp NOT NULL,
	invoice_status_id bigint(9) NOT NULL,
	invoice_date timestamp NULL,
	invoice_number varchar(40) NULL,
	purchase_order varchar(40) NULL,
	contact varchar(70) NULL,
primary key pk_invoice (id)	
);

create table invoice_item (
	invoice_id bigint(9) NOT NULL,
	invoice_item_id bigint(9) NOT NULL,
	name varchar(50) null,
	type int(1) NOT NULL,
	price bigint(20) NOT NULL,
    amount int(3) NOT NULL,
    type_index int(2) NOT NULL,
primary key pk_invoice_item (invoice_id, invoice_item_id)	
);

create table invoice_history (
	invoice_id bigint(9) not null,
	creation_date timestamp not null,
	name varchar(50) NOT NULL,
    category_id bigint(9) NOT NULL,
    invoice_status_id bigint(9) NOT NULL,
	due_date timestamp NOT NULL,
	invoice_date timestamp NULL,
	invoice_number varchar(40) NULL,
	purchase_order varchar(40) NULL,
	contact varchar(70) NULL,
	description TEXT NULL,
	total_price bigint(20) NULL,
	user_id bigint(9) not null,
primary key pk_invoice_hist (invoice_id, creation_date)
);

create table db_repository_sequence (
	id int(10) not null,
	project_id bigint(9) NOT NULL, 
primary key pk_rep_sequence (id, project_id)
);

create table db_repository_item (
	id bigint(9) NOT NULL,
	repository_file_path TEXT NOT NULL,
	repository_file_name TEXT NOT NULL,
	project_id bigint(9) NOT NULL,
	is_directory int(1) NULL,
	parent_id bigint(9) NULL,
primary key pk_rep_item (id)	
);

create table db_repository_version (
	repository_item_id bigint(9) not null,
	version int(10) not null,
	content_type varchar(40) not null,	
	creation_date timestamp not null,	
	comment text null,
	user_id bigint(9) not null,
	file_size int(13) not null,
	binary_file mediumblob null,
primary key pk_rep_version (repository_item_id, version)	
);

create table repository_policy (
	project_id bigint(9) not null,
	type_policy varchar(30) not null,
	value text null,
primary key pk_repos_policies (project_id, type_policy)
);

create table artifact_template (
	id bigint(9) not null,
	name varchar(70) not null,
	description varchar(255) null,
	category_id bigint(9) not null,
	header_html text null,
	body_html text not null,
	footer_html text null,
	profile_view varchar(1) null,
primary key pk_artif_templ (id)
);

create table external_data (
	id bigint(9) not null,
	external_id text not null,
	external_sys varchar(50) not null,
	external_host varchar(255) not null,
	external_accont varchar(255) not null,
	planning_id bigint(9) null,
	msg_date timestamp NULL,	
	event_date timestamp NULL,
	destination text null,
	source text null,
	summary text null,
	body text null,
	priority int(1) null,
	due_date timestamp null,
primary key pk_external_data (id)
);


create table external_data_attach (
	external_data_id bigint(9) not null,
	attachment_id varchar(255) not null,
	content_type varchar(255) not null,	
	creation_date timestamp not null,
	file_size bigint(9) not null,	
	file_name text not null,
	binary_file mediumblob not null,
primary key pk_external_data_attach (external_data_id, attachment_id)	
);


create table artifact (
    id bigint(9) NOT NULL,
	repository_file_id bigint(9) NOT NULL,
	last_update timestamp NOT NULL,	
	artifact_template_type TEXT NULL,
	project_id bigint(9) not null,
primary key pk_artifact (id)
);

create table user_edi_link (
    user_id bigint(9) not null,
	edi_id bigint(9) not null,
	last_update timestamp not null,	
	edi_uuid TEXT not null,
primary key pk_user_edi_link (user_id, edi_id)
);


alter table user_edi_link add constraint user_user_edi_link foreign key (user_id) references tool_user (id);

alter table external_data_attach add constraint ext_data_attach_ext_data foreign key (external_data_id) references external_data (id);

alter table artifact_template add constraint artif_templ_category foreign key (category_id) references category (id);

alter table artifact add constraint reposit_file_artif foreign key (repository_file_id) references repository_file (id);

alter table artifact add constraint artifact_planning foreign key (id) references planning (id);

alter table invoice add constraint invoice_category foreign key (category_id) references category (id);

alter table invoice add constraint invoice_status foreign key (invoice_status_id) references invoice_status (id);

alter table invoice add constraint invoice_planning foreign key (id) references planning (id);

alter table invoice_item add constraint inv_item_invoice foreign key (invoice_id) references invoice (id);

alter table tool_user add constraint user_department foreign key (department_id) references department (id);

alter table tool_user add constraint user_area foreign key (area_id) references area (id);

alter table tool_user add constraint user_function foreign key (function_id) references function (id); 

alter table tool_user add constraint user_company foreign key (company_id) references company (id);

alter table customer add constraint customer_user foreign key (id) references tool_user (id);

alter table resource add constraint resource_customer foreign key (id, project_id) references customer (id, project_id);

alter table leader add constraint leader_resource foreign key (id, project_id) references resource (id, project_id);

alter table root add constraint root_leader foreign key (id, project_id) references leader (id, project_id);

alter table customer add constraint customer_project foreign key (project_id) references project (id);

alter table project add constraint project_parent_project foreign key (parent_id) references project (id); 

alter table project add constraint project_status_project foreign key (project_status_id) references project_status (id); 

alter table project add constraint proj_company foreign key (company_id) references company (id);

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

alter table resource_task_alloc_plann add constraint alloc_plan_res_task foreign key (task_id, resource_id, project_id) references resource_task (task_id, resource_id, project_id); 

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

alter table occurrence_field_table add constraint occur_occur_field_tbl foreign key (occurrence_id) references occurrence (id);

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

alter table project_report add constraint proj_rep_report foreign key (report_id) references report (id);

alter table project_report add constraint proj_rep_proj foreign key (project_id) references project (id);

alter table db_repository_version add constraint db_rep_vers_item foreign key (repository_item_id) references db_repository_item (id);

alter table step_node_template add constraint step_node_templ foreign key (id) references node_template (id);

alter table decision_node_template add constraint decis_node_templ foreign key (id) references node_template (id);

INSERT INTO `company` VALUES 
(1,'STZP','55th Strategic Communications Squadron','','Bldg 41','Offutt AFB','NE','68113');
INSERT INTO `company` VALUES 
(2,'Legacy','Legacy Programming','','','55SCS','','');
INSERT INTO `company` VALUES 
(3,'PC','PC Programming','','','55SCS','','');
INSERT INTO `company` VALUES 
(4,'CM','Configuration Management','','','55SCS','','');
INSERT INTO `company` VALUES 
(5,'Test','Testing','','','55SCS','','');
INSERT INTO `company` VALUES 
(6,'QA','Quality Assurance','','','55SCS','','');
INSERT INTO `company` VALUES 
(7,'Contr','ITT, NG, etc.','','','55SCS','','');

INSERT INTO `area` VALUES (1,'Development','Development'),(2,'Maintenance','Maintenance'),(3,'Support','Support'),(4,'Research','Research'),(5,'Accounting','Accounting'),(6,'Sales','Sales'),(7,'Review','Review'),(8,'Finance','Finance'),(9,'Management','Management'),(10,'Purchasing','Purchasing');

INSERT INTO `department` VALUES (1,'Operations','Operations'),(2,'SANDS','SANDS Development'),(3,'KSR','KSR'),(4,'PEP','PEP'),(5,'Legacy','Legacy'),(6,'Legacy Development','Legacy Development'),(7,'Legacy Database','Legacy Database'),(8,'Management','Management');

INSERT INTO `function` VALUES (1,'Legacy Programmer','Legacy Programmer'),(2,'Database Programmer','Database Programmer'),(3,'Chief - Legacy Programming','Chief - Legacy Programming'),(4,'Chief - PC Programming','Chief - PC Programming'),(5,'PC Programming','PC Programming'),(6,'Tester','Tester'),(7,'QA','Quality Assurance'),(8,'Legacy CM','Legacy Configuration Management'),(9,'PC CM','PC Configuration Management'),(10,'LAN Engineer','LAN Engineer'),(11,'NCOIC/Superintendent','NCOIC/Superintendent'),(12,'Chief of Testing','Chief of Testing'),(13,'NOVA Engineer','NOVA Engineer'),(14,'Computer Programmer','Computer Programmer'),(15,'Customer/SPO User','Customer/SPO User');

INSERT INTO `project_status` VALUES (1,'Open','Open Project',2),(2,'Closed','Closed Project',4),(3,'Cancelled','Cancelled Project',4),(4,'On-Hold','On-hold Project',3),(5,'In Review','Waiting on a Review',3),(6,'Approved','Approved',1);

INSERT INTO `task_status` VALUES (1,'Open','Open',1),(2,'Closed','Close',100),(3,'Cancelled','Cancelled',101),(4,'In-Progress','In-Progress',20),(5,'On-Hold','On-Hold',50),(6,'Reopen','Reopen',2);

INSERT INTO `requirement_status` VALUES (1,'Waiting Approval','Waiting Approval',1,NULL,NULL),(2,'Approved','Approved',2,NULL,NULL),(3,'Refused','Refused',202,NULL,NULL),(4,'Closed','Closed',201,NULL,NULL),(5,'Cancelled','Cancelled',200,NULL,NULL),(6,'Planned','Planned',100,NULL,NULL),(7,'In-Progress','In-Progress',300,NULL,NULL);

INSERT INTO `tool_user` VALUES 
(1,'root','','System Root','DO NOT REMOVE THIS USER','','000000',4,2,7,1,'US','en',NULL,NULL,NULL,NULL,NULL,'0000-00-00 00:00:00');

insert into invoice_status (id,name,description,state_machine_order) values ('1','Bugdet','Bugdet',1); 
insert into invoice_status (id,name,description,state_machine_order) values ('2','Paid','Paid',100); 
insert into invoice_status (id,name,description,state_machine_order) values ('3','Cancelled','Cancelled',101); 
insert into invoice_status (id,name,description,state_machine_order) values ('4','Reviewed','Reviewed',40); 
insert into invoice_status (id,name,description,state_machine_order) values ('5','Submitted','Submitted',50); 

INSERT INTO `planning` VALUES 
(0,'Master SACCS Project','2012-12-31 18:00:00.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(190,'BCR Process','2015-05-08 13:23:09.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(191,'Program Problem Identified','2015-05-08 13:25:28.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(192,'Assign Project Number','2015-05-08 13:25:28.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(193,'Requirements Analysis','2015-05-08 13:25:28.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(194,'SDD Development','2015-05-08 13:25:28.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(195,'Code and Unit Test','2015-05-08 13:25:28.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(196,'Build release package','2015-05-08 13:25:28.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(197,'CM Final Build','2015-05-08 16:00:57.000',NULL,NULL,NULL,NULL);
INSERT INTO `planning` VALUES 
(198,'Implementation Phase','2015-05-08 16:00:57.000',NULL,NULL,NULL,NULL);

INSERT INTO `project` VALUES (0,'SACCS',NULL,'1',1,NULL,NULL,NULL,NULL,NULL,NULL,1,0);

INSERT INTO `category` VALUES (0,'Master','Category not selected.',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1,'Specification','Reviewing existing specs and making updates',0,NULL,0,0,1,0,1,1),(2,'Analysis','Analysis tasks',0,NULL,0,0,1,1,1,2),(3,'Code and Unit Testing','Code and Unit Testing',0,NULL,0,0,0,1,1,3),(4,'Testing','Integration and Regression Testing',0,NULL,0,0,0,1,0,4),(5,'Implementation','Transition from testing to implementation, builds, etc.',0,NULL,0,0,0,0,1,5),(6,'CM','Configuration Management projects and tasks',0,NULL,0,0,1,1,1,6),(7,'Quality Assurance','QA Reviews, audits, and other tasks',0,NULL,0,0,0,0,0,7),(8,'Training','Mandatory training',0,NULL,0,0,0,0,0,100),(9,'Project Management','Reviewing project performance',2,NULL,2,0,0,0,0,100),(10,'Resource Management','Reviewing resource performance',2,NULL,2,0,0,0,0,100),(11,'Mgmt Review','Manage Reviews',5,NULL,2,0,0,0,0,100),(12,'Research','Research (code, requirements, etc.)',0,NULL,0,0,0,0,1,2),(13,'BCR','New requirement from BCR',1,NULL,0,0,0,0,0,0),(14,'TAR','Test Anomoly Report',1,NULL,0,0,0,0,0,1),(15,'Testing','Requirements for Test Planning',1,NULL,0,0,0,0,0,3),(16,'CM','CM Build Requirements',1,NULL,0,0,0,0,0,3);

INSERT INTO `customer` VALUES (1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `resource` VALUES (1,0,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `leader` VALUES (1,0);


insert into risk_status (id, name, description, status_type) values ('1', 'Identified', 'Identified', '0'); 
insert into risk_status (id, name, description, status_type) values ('2', 'Materialized', 'Materialized', '1'); 
insert into risk_status (id, name, description, status_type) values ('3', 'Cancelled', 'Cancelled', '2'); 

insert into root (id,project_id) values ('1','0'); 

insert into p_sequence (id) values (300);

insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('1', 'Project Issues Analysis Report', 0, '', 'select distinct project_id, p.name from requirement r, project p where p.id = r.project_id and (p.id = ?#PROJECT_ID# or p.id in (select id from project where parent_id=?#PROJECT_ID#))', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/projectReq.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('2', 'Project KPI Histogram', 0, '', 'select rr.report_id as report_id , r.name as name, cast(rr.value as SIGNED) as val, count(rr.value) as count from report_result rr, report r where rr.report_id = r.id and r.report_perspective_id <> 0 and r.data_type = 0 and rr.last_execution >= ?#Initial Date{}(2)# and rr.last_execution <= ?#Final Date{}(2)# and rr.value <> "0" group by report_id, val', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/KPIHistogram.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('3', 'Backlog Report', 0, '', 'select u.id as user_id, u.name, c.name as category_name, c.id as category_id, ts.name as task_status, ts.id as statusId, t.name as task_name, rt.project_id, p.name as project_name, rt.start_date, rt.estimated_time, rt.actual_date, rt.actual_time from resource_task rt, task t, tool_user u, project p, task_status ts, category c where rt.task_id=t.id and rt.resource_id = u.id and rt.project_id = p.id and rt.task_status_id = ts.id and t.category_id = c.id and (ts.state_machine_order <> 100 and ts.state_machine_order <> 102) and (p.id = ?#PROJECT_ID# or p.id in (select id from project WHERE parent_id=?#PROJECT_ID#)) order by u.id, ts.state_machine_order, c.id', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/backlog.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('4', 'Requirement Overview', 0, '', 'select r.id, r.suggested_date, r.deadline_date, r.priority, r.reopening, rs.name as status_name, c.name as category_name, p.description, p.creation_date, pr.name as project_name from requirement r, requirement_status rs, category c, planning p, project pr where r.category_id=c.id and r.requirement_status_id = rs.id and r.project_id = pr.id and r.id = p.id and r.id in (?#Requirement ID{ }(1)#)', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/ReqOverview.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('5', 'Estimated Time X Actual Time', 0, '', 'select rt.estimated_time/60 estimated_time, rt.actual_time/60 actual_time, rt.project_id from planning p, resource_task rt where rt.task_id = p.id and p.final_date is not null and (rt.project_id=?#PROJECT_ID# or rt.project_id in (select id from project WHERE parent_id=?#PROJECT_ID#))', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/estimatedActualAnalysis.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('7', 'My Tasks Report', 0, '', 'select u.username, t.name, u.name as fullname, p.id as project_id, p.name as project, c.name as category, sub.task_id, sub.bucket_date, sum(sub.alloc_time) as s from ( select rt.task_id, rt.resource_id, rt.project_id, rta.sequence, rta.alloc_time, rt.actual_date, ADDDATE(rt.actual_date, rta.sequence-1) as bucket_date from resource_task rt, resource_task_alloc rta where rt.task_id = rta.task_id and rt.resource_id = rta.resource_id and rt.project_id = rta.project_id and rta.alloc_time > 0 and rt.actual_date is not null ) as sub, tool_user u, task t, project p, category c where sub.resource_id = u.id and sub.task_id = t.id and sub.project_id = p.id and t.category_id = c.id and sub.bucket_date >= ?#Initial Date{}(2)# and sub.bucket_date <= ?#Final Date{}(2)# and u.id=?#USER_ID# group by p.name, u.username, p.id , u.name, sub.task_id, t.name, c.name, sub.bucket_date order by u.username, sub.bucket_date, p.name, c.name', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/MyTaskReport.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('8', 'Survey Report', 0, '', 'select s.id, s.name, s.description, s.is_anonymous, s.project_id, s.creation_date, s.final_date, s.date_publishing, p.name as project_name, q.content, q.is_mandatory, q.type, q.id as question_id from survey s, project p, survey_question q where s.project_id=p.id and q.survey_id = s.id and s.id = ?#Survey{!select id, name from survey where project_id=?#PROJECT_ID#!}(1)# order by s.id, q.position, q.sub_title', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/SurveyReport.jasper', '2');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('9', 'Expense Report', 0, '', 'select e.id, t.name, pr.name as PROJECT_NAME, p.description, p.creation_date, c.id as COST_ID, c.name as COST_NAME, g.name as CATEGORY, c.aCount_code, ci.due_date, ci.value, at.username as approver, cs.name as STATUS, cs.state_machine_order from expense e, tool_user t, project pr, planning p, cost c, category g, cost_status cs, cost_installment ci LEFT OUTER JOIN tool_user at on (at.id = ci.approver) where e.user_id = t.id and e.project_id = pr.id and e.id = p.id and c.category_id = g.id and c.expense_id = e.id and c.id = ci.cost_id and ci.installment_num=1 and ci.cost_status_id = cs.id and e.id=?#Expense{!select id, id as lbl from expense where user_id in (select id from resource where project_id in (?#PROJECT_DESCENDANT#)) or user_id=?#USER_ID#!}(1)# order by e.id', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/ExpenseReport.jasper', '3');
insert into report (id, name, type, report_perspective_id, sql_text, execution_hour, last_execution, project_Id, final_date, data_type, file_name, profile_view) values ('10', 'ACountability Report', 0, '', 'select p.id as project_id, p.name as project_name, sub.id, sub.name, sub.due_date, sub.item_name, sub.value, sub.is_inv from ( select i.project_id, i.id, i.name, i.due_date, ii.name as item_name, (ii.amount * ii.price) as value, (ii.type in (1, 2)) as is_inv from invoice i, invoice_item ii, invoice_status s where ii.invoice_id = i.id and invoice_status_id = s.id and s.state_machine_order=100 union select c.project_id, c.id, cat.name, i.due_date, c.name, i.value, 0  as is_inv from cost c, cost_installment i, category cat where cat.id = c.category_id and c.id = i.cost_id ) as sub, project p where sub.project_id = p.id and p.id in (?#PROJECT_DESCENDANT#) order by p.id, sub.due_date, sub.is_inv', 0, null, 0, null, 0, '#CLASS_PATH#/WEB-INF/classes/ACountabilityReport.jasper', '2');

insert into project_report (report_id, project_id) values ('1' ,'0');
insert into project_report (report_id, project_id) values ('2' ,'0');
insert into project_report (report_id, project_id) values ('3' ,'0');
insert into project_report (report_id, project_id) values ('4' ,'0');
insert into project_report (report_id, project_id) values ('5' ,'0');
insert into project_report (report_id, project_id) values ('7' ,'0');
insert into project_report (report_id, project_id) values ('8' ,'0');
insert into project_report (report_id, project_id) values ('9' ,'0');
insert into project_report (report_id, project_id) values ('10','0');

insert into cost_status (id, name, description, state_machine_order) values ('1', 'Waiting Approve', 'Waiting Approve', 1);
insert into cost_status (id, name, description, state_machine_order) values ('2', 'Budgeted', 'Budgeted', 10);
insert into cost_status (id, name, description, state_machine_order) values ('3', 'Paid', 'Paid', 100);
insert into cost_status (id, name, description, state_machine_order) values ('4', 'Cancelled', 'Cancelled', 101);


insert into resource_capacity
select distinct r.id, r.project_id, EXTRACT(YEAR FROM p.creation_date), 
      EXTRACT(MONTH FROM p.creation_date), EXTRACT(DAY FROM p.creation_date),
      r.capacity_per_day, (r.cost_per_hour * 100)
from resource as r, customer as c, planning p
where r.id = c.id and r.project_id = c.project_id
and c.project_id = p.id
and (c.is_disable is null or c.is_disable=0) 
and r.capacity_per_day is not null;

INSERT INTO `artifact_template` VALUES (1,'Blank Document','This is a template for a blank document.',0,'<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><style type=\"text/css\">body, td, pre {color:#000; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px; margin:8px;} body {background:#FFF;} body.mceForceColors {background:#FFF; color:#000;} h1 {font-size: 2em} h2 {font-size: 1.5em} h3 {font-size: 1.17em} h4 {font-size: 1em} h5 {font-size: .83em} h6 {font-size: .75em} .mceItemTable, .mceItemTable td, .mceItemTable th, .mceItemTable caption, .mceItemVisualAid {border: 1px dashed #BBB;} a.mceItemAnchor {display:inline-block; width:11px !important; height:11px  !important; background:url(img/items.gif) no-repeat 0 0;} td.mceSelected, th.mceSelected {background-color:#3399ff !important} img {border:0;} table {cursor:default} table td, table th {cursor:text} ins {border-bottom:1px solid green; text-decoration: none; color:green} del {color:black; text-decoration:line-through} cite {border-bottom:1px dashed blue} acronym {border-bottom:1px dotted #CCC; cursor:help} abbr {border-bottom:1px dashed #CCC; cursor:help} span.bold {font-weight: bold;} span.italic {font-style:italic;} span.underline{text-decoration: underline} p.right {text-align:right;} p.left {text-align:left;} p.center {text-align:center;} p.full {text-align:justify;} * html body {scrollbar-3dlight-color:#F0F0EE;scrollbar-arrow-color:#676662;scrollbar-base-color:#F0F0EE;scrollbar-darkshadow-color:#DDD;scrollbar-face-color:#E0E0DD;scrollbar-highlight-color:#F0F0EE;scrollbar-shadow-color:#F0F0EE;scrollbar-track-color:#F5F5F5;} img:-moz-broken {-moz-force-broken-image-icon:1; width:24px; height:24px} font[face=mceinline] {font-family:inherit !important}</style>','<body></body>','</html>',NULL),(2,'Use Case Template','This is a template for a Use Case suite. Each Use Case focuses on describing how to achieve a goal or a task into a project. This template was inspired at <a href=\"http://readyset.tigris.org/nonav/templates/frameset.html\">ReadSET templates</a>',1,'<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><style type=\"text/css\">body, td, pre {color:#000; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px; margin:8px;} body {background:#FFF;} body.mceForceColors {background:#FFF; color:#000;} h1 {font-size: 2em} h2 {font-size: 1.5em} h3 {font-size: 1.17em} h4 {font-size: 1em} h5 {font-size: .83em} h6 {font-size: .75em} .mceItemTable, .mceItemTable td, .mceItemTable th, .mceItemTable caption, .mceItemVisualAid {border: 1px dashed #BBB;} a.mceItemAnchor {display:inline-block; width:11px !important; height:11px  !important; background:url(img/items.gif) no-repeat 0 0;} td.mceSelected, th.mceSelected {background-color:#3399ff !important} img {border:0;} table {cursor:default} table td, table th {cursor:text} ins {border-bottom:1px solid green; text-decoration: none; color:green} del {color:black; text-decoration:line-through} cite {border-bottom:1px dashed blue} acronym {border-bottom:1px dotted #CCC; cursor:help} abbr {border-bottom:1px dashed #CCC; cursor:help} span.bold {font-weight: bold;} span.italic {font-style:italic;} span.underline{text-decoration: underline} p.right {text-align:right;} p.left {text-align:left;} p.center {text-align:center;} p.full {text-align:justify;} * html body {scrollbar-3dlight-color:#F0F0EE;scrollbar-arrow-color:#676662;scrollbar-base-color:#F0F0EE;scrollbar-darkshadow-color:#DDD;scrollbar-face-color:#E0E0DD;scrollbar-highlight-color:#F0F0EE;scrollbar-shadow-color:#F0F0EE;scrollbar-track-color:#F5F5F5;} img:-moz-broken {-moz-force-broken-image-icon:1; width:24px; height:24px} font[face=mceinline] {font-family:inherit !important}</style>','<body><h2>Use Cases</h2><h3>Project: <span style=\"background-color: #ffff00\">PROJECT_NAME</span></h3><p><span style=\"background-color: #ffff00\"><i>TODO: Note any aspects that are common to all use cases here. This helps keep the use cases themselves short. If any use case is an exception, note it\'s specific value to replace or add to the default.</i></span></p><p><span style=\"background-color: #ffff00\"><i>TIP: It is OK to simply list the names of a lot of use cases without actually writing the use case in detail. Document the most important use cases first and come back to less important ones later.</i></p></span></p><h3>Default Aspects of All Use Cases</h3><table border=\"1\" cellpadding=\"0\" ><tr><td ><p><b>Direct Actors:</b></p></td><td><p><span style=\"background-color: #ffff00\">User: end-user in any role</span></p><p><span style=\"background-color: #ffff00\">System: The system being built</span></p><p><span style=\"background-color: #ffff00\">When actors are not listed, assume User is doing it.</span></p><p><span style=\"background-color: #ffff00\">Items beginning with see indicate that System has presented a new screen.</span></p></td></tr><tr><td><p><b>Stakeholders:</b></p></td><td><p><span style=\"background-color: #ffff00\">Project Owners and other members</span></p></td></tr><tr><td><p><b>Prereq:</b></p></td><td><p><span style=\"background-color: #ffff00\">User is logged in</span></p></td></tr></table></p><h3><span style=\"background-color: #ffff00\">UC-00: Configure the site</span></h3><table border=\"1\" cellpadding=\"0\" ><tr><td><p><b>Summary:</b></p></td><td><p><span style=\"background-color: #ffff00\">The administrator navigates to the site  configuration page and uses it to change the behavior of the web application.  Specifically, the user-visible timezone is being set.</span></p></td></tr><tr><td><p><b>Priority:</b></p></td><td><p><span style=\"background-color: #ffff00\">Essential</span></p></td></tr><tr><td><p><b>Use Frequency:</b></p></td><td><p><span style=\"background-color: #ffff00\">Rarely</span></p></td></tr><tr><td ><p><b>Direct Actors:</b></p></td><td><p><span style=\"background-color: #ffff00\">Admin: Web-site administrator</span></p></td></tr><tr><td><p><b>Main Success  Scenario:</b></p></td><td><ol><li><span style=\"background-color: #ffff00\">visit SiteConfiguration page</span></li><li><span style=\"background-color: #ffff00\">see site configuration options</span></li><li><span style=\"background-color: #ffff00\">enter timezone abbreviation for date displays</span></li><li><span style=\"background-color: #ffff00\">submit form</span></li><li><span style=\"background-color: #ffff00\">confirm changes</span></li><li><span style=\"background-color: #ffff00\">see SiteConfiguration page again, with updated values</span></li></ol></td></tr><tr><td><p><b>Alternative</b></p><p><b>Scenario Extensions:</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">If the timezone abbreviation is incorrect, an error message will be displayed and no changes will be made.</span></li></ul></td></tr><tr><td><p><b>Notes and Questions</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">How will administrators know the right timezone abbreviation? They would know it if they live in that timezone. Maybe we could provide a drop-down list of all choices, but each would need some explanation.</span></li></ul></td></tr></table><h3><span style=\"background-color: #ffff00\">UC-nn: USE CASE NAME</span></h3><table border=\"1\" cellpadding=\"0\" ><tr><td><p><b>Summary:</b></p></td><td><p><span style=\"background-color: #ffff00\">1-3 SENTENCES</span></p></td></tr><tr><td><p><b>Priority:</b></p></td><td><p><span style=\"background-color: #ffff00\">Essential | Expected | Desired | Optional</span></p></td></tr><tr><td><p><b>Use Frequency:</b></p></td><td><p><span style=\"background-color: #ffff00\">Always | Often | Sometimes | Rarely |  Once</span></p></td></tr><tr><td><p><b>Main Success  Scenario:</b></p></td><td><ol><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">STEP</span></li></ol></td></tr><tr><td><p><b>Alternative</b></p><p><b>Scenario  Extensions:</b></p></td><td><ul type=disc><li><span style=\"background-color: #ffff00\">If CONDITION, then ALTERNATIVE STEPS.</span></li><ul><li><span style=\"background-color: #ffff00\">NOTES or DETAILS.</span></li></ul><li><span style=\"background-color: #ffff00\">If CONDITION, then ALTERNATIVE STEPS.</span></li><ul><li><span style=\"background-color: #ffff00\">NOTES or DETAILS.</span></li></ul></ul></td></tr><tr><td><p><b>Notes and Questions</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">NOTE</span></li><li><span style=\"background-color: #ffff00\">QUESTION</span></li></ul></td></tr></table></p></div></body>','</html>',NULL),(3,'Test Case Template','This is a template for a Test Case suite. A test case describes a set of conditions and their expected results. This template was inspired at <a href=\"http://readyset.tigris.org/nonav/templates/frameset.html\">ReadSET templates</a>',1,'<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><style type=\"text/css\">body, td, pre {color:#000; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px; margin:8px;} body {background:#FFF;} body.mceForceColors {background:#FFF; color:#000;} h1 {font-size: 2em} h2 {font-size: 1.5em} h3 {font-size: 1.17em} h4 {font-size: 1em} h5 {font-size: .83em} h6 {font-size: .75em} .mceItemTable, .mceItemTable td, .mceItemTable th, .mceItemTable caption, .mceItemVisualAid {border: 1px dashed #BBB;} a.mceItemAnchor {display:inline-block; width:11px !important; height:11px  !important; background:url(img/items.gif) no-repeat 0 0;} td.mceSelected, th.mceSelected {background-color:#3399ff !important} img {border:0;} table {cursor:default} table td, table th {cursor:text} ins {border-bottom:1px solid green; text-decoration: none; color:green} del {color:black; text-decoration:line-through} cite {border-bottom:1px dashed blue} acronym {border-bottom:1px dotted #CCC; cursor:help} abbr {border-bottom:1px dashed #CCC; cursor:help} span.bold {font-weight: bold;} span.italic {font-style:italic;} span.underline{text-decoration: underline} p.right {text-align:right;} p.left {text-align:left;} p.center {text-align:center;} p.full {text-align:justify;} * html body {scrollbar-3dlight-color:#F0F0EE;scrollbar-arrow-color:#676662;scrollbar-base-color:#F0F0EE;scrollbar-darkshadow-color:#DDD;scrollbar-face-color:#E0E0DD;scrollbar-highlight-color:#F0F0EE;scrollbar-shadow-color:#F0F0EE;scrollbar-track-color:#F5F5F5;} img:-moz-broken {-moz-force-broken-image-icon:1; width:24px; height:24px} font[face=mceinline] {font-family:inherit !important}</style>','<body><h2>Test Cases</h2><h3>Project: <span style=\"background-color: #ffff00\">PROJECT_NAME</span></h3></p><h3><span style=\"background-color: #ffff00\">login-1: Normal User Login</span></h3><table border=\"1\" cellpadding=\"0\"><tr><td><p><b>Purpose:</b></p></td><td><p><span style=\"background-color: #ffff00\">Test that users can log in with the proper username or email address and their password.</span></p></td></tr><tr><td><p><b>Prereq:</b></p></td><td><p><span style=\"background-color: #ffff00\">User is not already logged in.</span></p><p><span style=\"background-color: #ffff00\">User testuser exists, and account is in  good standing.</span></p></td></tr><tr><td><p><b>Test Data:</b></p></td><td><p><span style=\"background-color: #ffff00\">usernameOrEmail = {testuser, bogususer, testuser@nospam.com, test@user@nospam.com, empty}</span></p><p><span style=\"background-color: #ffff00\">password = {valid, invalid, empty}</span></p></td></tr><tr><td><p><b>Steps:</b></p></td><td><ol><li><span style=\"background-color: #ffff00\">visit LoginPage</span></li><li><span style=\"background-color: #ffff00\">enter usernameOrEmail</span></li><li><span style=\"background-color: #ffff00\">enter password</span></li><li><span style=\"background-color: #ffff00\">click Login</span></li><li><span style=\"background-color: #ffff00\">see the terms-of-use page</span></li><li><span style=\"background-color: #ffff00\">click Agree at page bottom</span></li><li><span style=\"background-color: #ffff00\">click Submit</span></li><li><span style=\"background-color: #ffff00\">see PersonalPage</span></li><li><span style=\"background-color: #ffff00\">verify welcome message is correct username</span></li></ol></td></tr><tr><td><p><b>Notes and Questions:</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">This assumes that user has not agreed to terms-of-use already.</span></li><li><span style=\"background-color: #ffff00\">Does this work without browser cookies?</span></li></ul></td></tr></table></p><h3><span style=\"background-color: #ffff00\">login-2: Locked-out User Login</span></h3><table border=\"1\" cellpadding=\"0\"><tr><td><p><b>Purpose:</b></p></td><td><p><span style=\"background-color: #ffff00\">Test that a user who has been locked out by a moderator, cannot log in, They should see a message indicating that they were locked out.</span></p></td></tr><tr><td><p><b>Prereq:</b></p></td><td><p><span style=\"background-color: #ffff00\">User is not already logged in.</span></p><p><span style=\"background-color: #ffff00\">User testuser2 exists, and has been locked out</span></p></td></tr><tr><td><p><b>Test Data:</b></p></td><td><p><span style=\"background-color: #ffff00\">usernameOrEmail = {testuser2, testuser2@nospam.com}</span></p><p><span style=\"background-color: #ffff00\">password = {valid}</span></p></td></tr><tr><td><p><b>Steps:</b></p></td><td><ol><li><span style=\"background-color: #ffff00\">visit LoginPage</span></li><li><span style=\"background-color: #ffff00\">enter usernameOrEmail</span></li><li><span style=\"background-color: #ffff00\">enter password</span></li><li><span style=\"background-color: #ffff00\">click Login</span></li><li><span style=\"background-color: #ffff00\">see LoginPage</span></li><li><span style=\"background-color: #ffff00\">verify warning message is the locked-out message</span></li></ol></td></tr><tr><td><p><b>Notes and Questions:</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">Does this work without browser cookies?</span></li></ul></td></tr></table></p><h3><span style=\"background-color: #ffff00\">unique-test-case-id1: Test Case Title</span></h3><table border=\"1\" cellpadding=\"0\"><tr><td><p><b>Purpose:</b></p></td><td><p> </p></td></tr><tr><td><p><b>Prereq:</b></p></td><td><p> </p></td></tr><tr><td><p><b>Test Data:</b></p></td><td><p><span style=\"background-color: #ffff00\">VAR = {VALUES}</span></p><p><span style=\"background-color: #ffff00\">VAR = {VALUES}</span></p></td></tr><tr><td><p><b>Steps:</b></p></td><td><ol><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">verify ...</span></li></ol></td></tr><tr><td><p><b>Notes and Questions:</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">NOTE</span></li><li><span style=\"background-color: #ffff00\">QUESTION</span></li></ul></td></tr></table></p><h3><span style=\"background-color: #ffff00\">unique-test-case-id2: Test Case Title</span></h3><table border=\"1\" cellpadding=\"0\"><tr><td><p><b>Purpose:</b></p></td><td><p> </p></td></tr><tr><td><p><b>Prereq:</b></p></td><td><p> </p></td></tr><tr><td><p><b>Test Data:</b></p></td><td><p><span style=\"background-color: #ffff00\">VAR = {VALUES}</span></p><p><span style=\"background-color: #ffff00\">VAR = {VALUES}</span></p></td></tr><tr><td><p><b>Steps:</b></p></td><td><ol><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">STEP</span></li><li><span style=\"background-color: #ffff00\">verify ...</span></li></ol></td></tr><tr><td><p><b>Notes and Questions:</b></p></td><td><ul><li><span style=\"background-color: #ffff00\">NOTE</span></li><li><span style=\"background-color: #ffff00\">QUESTION</span></li></ul></td></tr></table></p></body>','</html>',NULL),(4,'Software Requirements Specification','This is a template for a SRS. A SRS contain a understanding of a customer requirements and dependencies. This template was inspired at <a href=\"http://readyset.tigris.org/nonav/templates/frameset.html\">ReadSET templates</a>',1,'<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><style type=\"text/css\">body, td, pre {color:#000; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px; margin:8px;} body {background:#FFF;} body.mceForceColors {background:#FFF; color:#000;} h1 {font-size: 2em} h2 {font-size: 1.5em} h3 {font-size: 1.17em} h4 {font-size: 1em} h5 {font-size: .83em} h6 {font-size: .75em} .mceItemTable, .mceItemTable td, .mceItemTable th, .mceItemTable caption, .mceItemVisualAid {border: 1px dashed #BBB;} a.mceItemAnchor {display:inline-block; width:11px !important; height:11px  !important; background:url(img/items.gif) no-repeat 0 0;} td.mceSelected, th.mceSelected {background-color:#3399ff !important} img {border:0;} table {cursor:default} table td, table th {cursor:text} ins {border-bottom:1px solid green; text-decoration: none; color:green} del {color:black; text-decoration:line-through} cite {border-bottom:1px dashed blue} acronym {border-bottom:1px dotted #CCC; cursor:help} abbr {border-bottom:1px dashed #CCC; cursor:help} span.bold {font-weight: bold;} span.italic {font-style:italic;} span.underline{text-decoration: underline} p.right {text-align:right;} p.left {text-align:left;} p.center {text-align:center;} p.full {text-align:justify;} * html body {scrollbar-3dlight-color:#F0F0EE;scrollbar-arrow-color:#676662;scrollbar-base-color:#F0F0EE;scrollbar-darkshadow-color:#DDD;scrollbar-face-color:#E0E0DD;scrollbar-highlight-color:#F0F0EE;scrollbar-shadow-color:#F0F0EE;scrollbar-track-color:#F5F5F5;} img:-moz-broken {-moz-force-broken-image-icon:1; width:24px; height:24px} font[face=mceinline] {font-family:inherit !important}</style>','<body><h2>Software Requirements Specification</h2><B>Project: <span style=\"background-color: #ffff00\">PROJECT_NAME</span></B></p><h3>Introduction</h3><p><i><span style=\"background-color: #ffff00\">TODO: Provide a brief overview of thisrelease of the product. You can copy text from the project proposal, paste ithere, and shorten it.</span></i></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p></p><h3>Use Cases</h3><p><span style=\"background-color: #ffff00\">ONE PARAGRAPH OVERVIEW</span></p><ul> <li><span style=\"background-color: #ffff00\">The use case suite lists all use cases in an organized way.</span></li></ul></p><h3>Functional Requirements</h3><p><span style=\"background-color: #ffff00\">ONE PARAGRAPH OVERVIEW</span></p></p><h3>Non-Functional Requirements</h3><p><i><span style=\"background-color: #ffff00\">TODO: Describe the non-functional requirements for this release. Some examples are provided below.</span></i></p></p><p><b>What are the usability requirements?</b></p><p><span style=\"background-color: #ffff00\">Our main criteria for making the system usable is the difficulty of performing each high-frequency use case. Difficultydepends on the number of steps, the knowledge that the user must have at each step, the decisions that the user must make at each step, and the mechanics ofeach step (e.g., typing a book title exactly is hard, clicking on a title in a list is easy).</span></p><p><span style=\"background-color: #ffff00\">The user interface should be as familiar as possible to users who have used other web applications and Windows desktop applications. E.g., we will follow the UI guidelines for naming menus,buttons, and dialog boxes whenever possible.</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">Government compliance</span></p><li><span style=\"background-color: #ffff00\">The customer wants extensive on-line help, but is not demanding a printed manual.</span></p><p><b>What are the reliability and up-timerequirements?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the safety requirements?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the security requirements?</b></p><p><span style=\"background-color: #ffff00\">Access will be controlled with usernames and passwords.</span></p><p><span style=\"background-color: #ffff00\">Only administrator users will have access to administrative functions, average users will not.</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">Passwords must be 4-14 characters long</span></li><li><span style=\"background-color: #ffff00\">We will not use encrypted communications (SSL) for this website</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the performance and scalability requirements requirements?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the maintainability and upgradability requirements?</b></p><p><span style=\"background-color: #ffff00\">Maintainability is our ability to make changes to the product over time. We need strong maintainability in order to retain our early customers. We will address this by anticipating several types of change, and by carefully documenting our design andimplementation.</span></p><p><span style=\"background-color: #ffff00\">Upgradability is our ability to cost-effectively deploy new versions of the product to customers with minimal downtime or disruption. A key feature supporting this goal is automaticdownload of patches and upgrade of the end-user machine. Also, we shall use data file formats that include enough meta-data to allow us to reliably transform existing customer data during an upgrade.</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the supportability and operability requirements?</b></p><p><span style=\"background-color: #ffff00\">Supportability is our ability to provide cost effective technical support. Our goal is to limit our support costs to only 5% of annual licensing fees. The products automatic upgrade feature will help us easily deploy defect fixes to end-users. The user guideand product website will include a troubleshooting guide and checklist of information to have at hand before contacting technical support.</span></p><p><span style=\"background-color: #ffff00\">Operability is our ability to host and operate the software as an ASP (Application Service Provider). The product features should help us achieve our goal of 99.9% uptime (at most 43minutes downtime each month). Key features supporting that are the ability to do hot data backups, and application monitoring.</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the business life-cycle requirements?</b></p><p><span style=\"background-color: #ffff00\">The business life-cycle of a product includes everything that happens to that product over a period of several years, from initial purchase decision, through important but infrequentuse cases, until product retirement. Key life-cycle requirements are listed below.</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">Customers must be able to manage the number of licenses that they have and make informed decisions to purchase more licenses when needed</span></li><li><span style=\"background-color: #ffff00\">The product shall support daily operations and our year-end audit</span></li><li><span style=\"background-color: #ffff00\">The customer data shall be stored in a format that is still accessible even after the application has been retired</span></li></p><h3>Environmental Requirements</h3><p><i><span style=\"background-color: #ffff00\">TODO: Describe the environmental requirements for this release. Environmental requirements describe the larger system of hardware, software, and data that this product must work within. Someexamples are provided below.</span></i></p><p><b>What are the system hardware requirements?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the system software requirements?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What application program interfaces (APIs) must be provided?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">We must implement this standard API</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li><p><b>What are the data import and export requirements?</b></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">PARAGRAPH</span></p><p><span style=\"background-color: #ffff00\">Details:</span></p><li><span style=\"background-color: #ffff00\">The system will store all data in a standard SQL database, where it can be accessed by other programs.</span></li><li><span style=\"background-color: #ffff00\">The system will store all data in an XML file, using a standard DTD.</span></li><li><span style=\"background-color: #ffff00\">The system will read and write valid .XYZ files used by OTHER APPLICATION</span></li><li><span style=\"background-color: #ffff00\">DETAIL</span></li></body>','</html>',NULL),(5,'STZP Reqirements Document','This is a template for an STZP SRS',0,'<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><style type=\"text/css\">body, td, pre {color:#000; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px; margin:8px;} body {background:#FFF;} body.mceForceColors {background:#FFF; color:#000;} h1 {font-size: 2em} h2 {font-size: 1.5em} h3 {font-size: 1.17em} h4 {font-size: 1em} h5 {font-size: .83em} h6 {font-size: .75em} .mceItemTable, .mceItemTable td, .mceItemTable th, .mceItemTable caption, .mceItemVisualAid {border: 1px dashed #BBB;} a.mceItemAnchor {display:inline-block; width:11px !important; height:11px  !important; background:url(img/items.gif) no-repeat 0 0;} td.mceSelected, th.mceSelected {background-color:#3399ff !important} img {border:0;} table {cursor:default} table td, table th {cursor:text} ins {border-bottom:1px solid green; text-decoration: none; color:green} del {color:black; text-decoration:line-through} cite {border-bottom:1px dashed blue} acronym {border-bottom:1px dotted #CCC; cursor:help} abbr {border-bottom:1px dashed #CCC; cursor:help} span.bold {font-weight: bold;} span.italic {font-style:italic;} span.underline{text-decoration: underline} p.right {text-align:right;} p.left {text-align:left;} p.center {text-align:center;} p.full {text-align:justify;} * html body {scrollbar-3dlight-color:#F0F0EE;scrollbar-arrow-color:#676662;scrollbar-base-color:#F0F0EE;scrollbar-darkshadow-color:#DDD;scrollbar-face-color:#E0E0DD;scrollbar-highlight-color:#F0F0EE;scrollbar-shadow-color:#F0F0EE;scrollbar-track-color:#F5F5F5;} img:-moz-broken {-moz-force-broken-image-icon:1; width:24px; height:24px} font[face=mceinline] {font-family:inherit !important}</style>','<body>\r\n<P><B>2.0</b>&nbsp;Requirements</p>\r\n<P><B>2.1</b>Requirement Text</p>\r\n<P><B>2.2</b>Additional Requirement Text</p>\r\n</body>','</html>',NULL);
update artifact_template set header_html='<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN\" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /><style type="text/css">body, td, pre {color:#000; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px; margin:8px;} body {background:#FFF;} body.mceForceColors {background:#FFF; color:#000;} h1 {font-size: 2em} h2 {font-size: 1.5em} h3 {font-size: 1.17em} h4 {font-size: 1em} h5 {font-size: .83em} h6 {font-size: .75em} .mceItemTable, .mceItemTable td, .mceItemTable th, .mceItemTable caption, .mceItemVisualAid {border: 1px dashed #BBB;} a.mceItemAnchor {display:inline-block; width:11px !important; height:11px  !important; background:url(img/items.gif) no-repeat 0 0;} td.mceSelected, th.mceSelected {background-color:#3399ff !important} img {border:0;} table {cursor:default} table td, table th {cursor:text} ins {border-bottom:1px solid green; text-decoration: none; color:green} del {color:black; text-decoration:line-through} cite {border-bottom:1px dashed blue} acronym {border-bottom:1px dotted #CCC; cursor:help} abbr {border-bottom:1px dashed #CCC; cursor:help} span.bold {font-weight: bold;} span.italic {font-style:italic;} span.underline{text-decoration: underline} p.right {text-align:right;} p.left {text-align:left;} p.center {text-align:center;} p.full {text-align:justify;} * html body {scrollbar-3dlight-color:#F0F0EE;scrollbar-arrow-color:#676662;scrollbar-base-color:#F0F0EE;scrollbar-darkshadow-color:#DDD;scrollbar-face-color:#E0E0DD;scrollbar-highlight-color:#F0F0EE;scrollbar-shadow-color:#F0F0EE;scrollbar-track-color:#F5F5F5;} img:-moz-broken {-moz-force-broken-image-icon:1; width:24px; height:24px} font[face=mceinline] {font-family:inherit !important}</style>';
update artifact_template set footer_html='</html>';

INSERT INTO `template` VALUES 
(190,'BCR Process',NULL,90,1),(191,'Program Problem Identified',NULL,92,2),(192,'Assign project number',NULL,97,6),(193,'Requirements Analysis',NULL,98,2),(194,'SDD Development',NULL,110,3),(195,'Code and Unit Test',NULL,120,3),(196,'Build Release Package',NULL,130,5),(197,'CM FInal Build',NULL,140,6),(198,'Implementation',NULL,150,5);

INSERT INTO `node_template` VALUES (90,'Customer Identifies Prob','Customer documents problem on BCR and submits to 625th STOS.','1',190,NULL,91),(91,'STOS Review','STOS Reviews problem and validates it.\n','1',190,NULL,95),(92,'System problem','Operations or prorgamming personnel identify problem. The request is written up by STZP for STOS review.','1',191,NULL,93),(93,'STZP BCR VET Meeting','Internal BCRs are vetted through peer review','1',191,NULL,94),(94,'BCR ready for STOS review','Ready for STOS?','2',191,NULL,91),(95,'STOS forwards BCR to STZP for action','STOS packages and forwards BCR and other documentation to STZP\r\n','1',190,NULL,NULL),(97,'STZP CM creates project','Project number for new BCR gets assigned by CM','1',192,NULL,NULL),(98,'Section Chief assigns project','Project is assigned to one or more programmer in section chief\'s area','1',193,NULL,99),(99,'Develop SRS','Resource analyzes problem and develops and proposed design','1',193,NULL,100),(100,'Informal peer review and approval','Programmers and section chief hold peer reviews of proposed design followed by a formal design review.','1',193,NULL,101),(101,'Design Review approve','If problems are found and cannot be resolved during the formal review, the SDD is sent back to the developer for rework. ','2',193,NULL,102),(102,'Formal Requirements Review','A formal requirements review is held. If problems are found, the ','1',193,NULL,103),(103,'Requirement Approved','If the requirements are not approved at this review, they are sent back to the program for more work.','2',193,NULL,104),(104,'Project assigned to release','The proposed release for this project is selected.','1',193,NULL,NULL),(110,'Assign resources','Section chief assigns programmers to develop Software Design Description.','1',194,NULL,111),(111,'Develop SDD','Programming resources develop software design document','1',194,NULL,112),(112,'Design review','Formal design review.','1',194,NULL,113),(113,'Design approved','Does the design meet the needs of the customer and solve the immediate problem documented in the BCR','2',194,NULL,NULL),(120,'Code and Unit Test','Code and Unit test tasks','1',195,NULL,121),(121,'Code','Develop code for update','1',195,NULL,122),(122,'Unit Test','Unit Test','1',195,NULL,123),(123,'Unit Test Passed?','Results from unit testing.','2',195,NULL,124),(124,'SCR and TCR report','Generate the Software Change Report and Test Report for Engineering review','1',195,NULL,125),(125,'Submit SCR to SPO','Submit the project SCR and other support documentation for SPO review.','1',195,NULL,NULL),(130,'Build Release Package','CM Build of the initial testing package','1',196,NULL,131),(131,'Update System/Integration Test Plan','Update the STP and ITP.','1',196,NULL,132),(132,'Send to ITF','Send the package to the Indepent Test Facility and the SPO.','1',196,NULL,133),(133,'Test Plan Approved?','Is the Test Plan approved?','2',196,NULL,134),(134,'Test Readiness Review','Perform the Test Readiness Review','1',196,NULL,135),(135,'Integration Testing','Perform the testing for Integration and generate a test report.','1',196,NULL,136),(136,'Test Anomolies identified','Were there Test Anomalies?','2',196,NULL,137),(137,'Test Report Review','Perform Test Report review.','1',196,NULL,NULL),(138,'Develop Test Anomoly Report','Write the Test Anomaly Report','1',196,NULL,137),(140,'CM Final Build Kickoff','CM Final Build Kickoff Meeting.','1',197,NULL,141),(141,'Build software to support System Testing','CM builds software for final system testing.','1',197,NULL,142),(142,'System Test','Perform System Testing','1',197,NULL,143),(143,'Write Test Report','Write final test report, including any accepted and known anomalies.','1',197,NULL,144),(144,'Formal TR Review','Final Test Report Review','1',197,NULL,145),(145,'Send TR to SPO','Forward final Test Report to Program Office','1',197,NULL,NULL),(150,'Implementation','Perform Implementation Steps','1',198,NULL,151),(151,'TCR','Perform Trusted Code Review','1',198,NULL,152),(152,'Trusted Code OK?','Is the Trusted Code correct?','2',198,NULL,153),(153,'Create Gold Master','CM Creates the Gold Master disks in anticipation of release.','1',198,NULL,154),(154,'Produce and Coordinate Release Letter','produce and coordinate the release letter.','1',198,NULL,155),(155,'Produce and coordinate Fall-back Plan','Produce and coordinate the Fall-Back plan.','1',198,NULL,156),(156,'Release Coordination Meeting','Hold the release coordination meeting.','1',198,NULL,157),(157,'QC Checks','Perform Final Quality Control Checks on all products.','1',198,NULL,158),(158,'Archive work records','Archive all work records, close any open tasks associated with system development and produce KPI reports for release.','1',198,NULL,159),(159,'Establish new development baseline','CM produces new development baseline for next software development cycle.','1',198,NULL,NULL);

INSERT INTO `step_node_template` (id,category_id,resource_list,category_regex) VALUES (90,1,NULL,NULL),(92,1,NULL,NULL),(93,1,NULL,NULL),(95,0,NULL,NULL),(97,0,NULL,NULL),(98,0,NULL,NULL),(99,0,NULL,NULL),(100,0,NULL,NULL),(102,0,NULL,NULL),(104,0,NULL,NULL),(110,0,NULL,NULL),(111,0,NULL,NULL),(112,0,NULL,NULL),(120,0,NULL,NULL),(121,0,NULL,NULL),(122,0,NULL,NULL),(124,0,NULL,NULL),(125,0,NULL,NULL),(130,0,NULL,NULL),(131,0,NULL,NULL),(132,0,NULL,NULL),(133,0,NULL,NULL),(134,0,NULL,NULL),(135,0,NULL,NULL),(137,0,NULL,NULL),(138,0,NULL,NULL),(140,0,NULL,NULL),(141,0,NULL,NULL),(142,0,NULL,NULL),(143,0,NULL,NULL),(144,0,NULL,NULL),(145,0,NULL,NULL),(150,0,NULL,NULL),(151,0,NULL,NULL),(153,0,NULL,NULL),(154,0,NULL,NULL),(155,0,NULL,NULL),(156,0,NULL,NULL),(157,0,NULL,NULL),(158,0,NULL,NULL),(159,0,NULL,NULL);

INSERT INTO `decision_node_template` VALUES (91,'Valid bug?','90'),(94,'BCR ready?','92'),(101,'Design approve?','99'),(103,'Peers approve?','99'),(113,'Design approved?','111'),(123,'Unit Testing Complete?','121'),(136,'Anomalies Found?','138'),(152,'Trusted Code OK?','98');

