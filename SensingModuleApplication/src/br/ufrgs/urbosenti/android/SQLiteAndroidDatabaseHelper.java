package br.ufrgs.urbosenti.android;

import java.sql.SQLException;

import android.database.sqlite.SQLiteDatabase;
import urbosenti.core.data.DataManager;
import urbosenti.core.data.UrboSentiDatabaseHelper;
import android.content.Context;

public class SQLiteAndroidDatabaseHelper extends UrboSentiDatabaseHelper {

	private Context context;
	private SQLiteDatabase db;
	
	public SQLiteAndroidDatabaseHelper(DataManager dataManager,Context context) {
		super(dataManager);
		this.context = context;
	}

	@Override
	public Object openDatabaseConnection() throws ClassNotFoundException, SQLException {
		this.db = context.openOrCreateDatabase("urbosenti.db", Context.MODE_PRIVATE, null);
		return db;
	}

	@Override
    public void createDatabase() throws SQLException {
		db.execSQL("CREATE TABLE IF NOT EXISTS devices (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	generalDefinitionsVersion double not null default 0.0,\n"
                + "	deviceVersion double not null default 0.0,\n"
                + "	agentModelVersion double not null default 0.0\n"
                + ");");
		db.execSQL("CREATE TABLE IF NOT EXISTS agent_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
		db.execSQL("CREATE TABLE IF NOT EXISTS service_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
		db.execSQL("CREATE TABLE IF NOT EXISTS entity_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS data_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	initial_value varchar(20) not null\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS implementation_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS agent_communication_languages (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");\n");
      db.execSQL("CREATE TABLE IF NOT EXISTS communicative_acts (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	agent_communication_language_id integer not null,\n"
                + "	foreign key (agent_communication_language_id) references agent_communication_languages (id)\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS interaction_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS interaction_directions (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS targets_origins (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS agent_address_types (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS agents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	address varchar (100) not null default '/',\n"
                + "	layer integer not null default 1,\n"
                + "	agent_type_id integer not null,\n"
                + "	service_id integer not null,\n"
                + "	foreign key (agent_type_id) references agent_types (id),\n"
                + "	foreign key (layer) references targets_origins (id),\n"
                + "	foreign key (service_id) references services (id)\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS services (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	service_uid varchar (200) not null,\n"
                + "	application_uid varchar (200) not null default \"\",\n"
                + "	address varchar (200) not null,\n"
                + "	service_type_id integer not null,\n"
                + "	device_id integer not null,\n"
                + "	foreign key (service_type_id) references service_types (id),\n"
                + "	foreign key (device_id) references devices (id)\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS components (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	code_class varchar (100) not null,\n"
                + "	device_id integer not null,\n"
                + "	foreign key (device_id) references devices (id)\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS entities (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null,\n"
                + "	entity_type_id integer not null,\n"
                + "	component_id integer not null,\n"
                + "	foreign key (component_id) references components (id),\n"
                + "	foreign key (entity_type_id) references entity_types (id)\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS instances (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	model_id integer detault null,\n"
                + "	representative_class varchar(100) not null,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (entity_id) references entities (id)\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS instance_states (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null, \n"
                + "	user_can_change boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	data_type_id integer not null,\n"
                + "	instance_id integer not null,\n"
                + "	state_model_id integer not null,\n"
                + "	foreign key (instance_id) references instances (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS possible_instance_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null, \n"
                + "	default_value boolean not null default false,\n"
                + "	instance_state_id integer not null,\n"
                + "	foreign key (instance_state_id) references instance_states (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS instance_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	monitored_user_instance_id integer default null, \n"
                + "	instance_state_id integer not null,\n"
                + "	foreign key (monitored_user_instance_id) references instances (id), \n"
                + "	foreign key (instance_state_id) references instance_states (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS entity_states (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null, \n"
                + "	user_can_change boolean not null default false,\n"
                + "	instance_state boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	data_type_id integer not null,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (entity_id) references entities (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS possible_entity_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	entity_state_id integer not null,\n"
                + "	foreign key (entity_state_id) references entity_states (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS entity_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	monitored_user_instance_id integer default null,  /* -- para os estados dos normais, o cara que est� sendo monitorado */\n"
                + "	entity_state_id integer not null,\n"
                + "	foreign key (monitored_user_instance_id) references instances (id), \n"
                + "	foreign key (entity_state_id) references entity_states (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS events (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null, \n"
                + "	synchronous boolean not null default false,\n"
                + "     store boolean not null default false, "
                + "	implementation_type_id integer not null,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (implementation_type_id) references implementation_types (id), \n"
                + "	foreign key (entity_id) references entities (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS event_targets_origins (\n"
                + "	event_id integer not null,\n"
                + "	target_origin_id integer not null,\n"
                + "	mandatory boolean not null default true,\n"
                + "	primary key (event_id, target_origin_id),\n"
                + "	foreign key (event_id) references events (id),\n"
                + "	foreign key (target_origin_id) references targets_origins (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS event_parameters (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null, \n"
                + "	optional boolean not null default false,\n"
                + "	parameter_label varchar (100) not null,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	entity_state_id integer default null,\n"
                + "	data_type_id integer not null,\n"
                + "	event_id integer not null,\n"
                + "	foreign key (event_id) references events (id),\n"
                + "	foreign key (data_type_id) references data_types (id),\n"
                + "	foreign key (entity_state_id) references entity_states (id)	\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS possible_event_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	event_parameter_id integer not null,\n"
                + "	foreign key (event_parameter_id) references event_parameters (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS event_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	event_parameter_id integer not null,\n"
                + "     generated_event_id integer not null DEFAULT 0, \n"
                + "     foreign key (generated_event_id) references generated_events (id),"
                + "	foreign key (event_parameter_id) references event_parameters (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS actions (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	model_id integer not null,\n"
                + "	description varchar(100) not null, \n"
                + "	has_feedback boolean not null default false,\n"
                + "	entity_id integer not null,\n"
                + "	foreign key (entity_id) references entities (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS action_parameters (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null, \n"
                + "	label varchar(100) not null,\n"
                + "	optional boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	entity_state_id integer default null,\n"
                + "	data_type_id integer not null,\n"
                + "	action_id integer not null,\n"
                + "	foreign key (action_id) references actions (id),\n"
                + "	foreign key (data_type_id) references data_types (id),\n"
                + "	foreign key (entity_state_id) references entity_states (id)	\n"
                + ");");
      db.execSQL("CREATE TABLE IF NOT EXISTS possible_action_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	action_parameter_id integer not null,\n"
                + "	foreign key (action_parameter_id) references action_parameters (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS action_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	score double precision not null default 0.0,\n"
                + "	action_parameter_id integer not null,\n"
                + "	generated_action_id integer not null DEFAULT 0,\n"
                + "	foreign key (action_parameter_id) references action_parameters (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS action_feedback_answer (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null,\n"
                + "	action_id integer not null,\n"
                + "	foreign key (action_id) references actions (id)\n"
                + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS interactions (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	agent_type_id integer not null,\n"
                + "	communicative_act_id integer not null,\n"
                + "	interaction_type_id integer not null,\n"
                + "	direction_id integer not null,\n"
                + "	interaction_id integer default null,\n"
                + "	foreign key (agent_type_id) references agent_types (id),\n"
                + "	foreign key (communicative_act_id) references communicative_acts (id),\n"
                + "	foreign key (interaction_type_id) references interaction_types (id),\n"
                + "	foreign key (direction_id) references interaction_directions (id),\n"
                + "	foreign key (interaction_id) references interactions (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS agent_states (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) not null,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	data_type_id integer not null,\n"
                + "	agent_type_id integer not null,\n"
                + "	foreign key (agent_type_id) references agent_types (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS possible_agent_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	agent_state_id integer not null,\n"
                + "	foreign key (agent_state_id) references agent_states (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS agent_state_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	agent_state_id integer not null,\n"
                + "	foreign key (agent_state_id) references agent_states (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS interaction_parameters (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	description varchar(100) default null,\n"
                + "	label varchar(100) not null,\n"
                + "	optional boolean not null default false,\n"
                + "	superior_limit varchar (100) default null,\n"
                + "	inferior_limit varchar (100) default null,\n"
                + "	initial_value varchar (100) default null,\n"
                + "	agent_state_id integer default null,\n"
                + "	data_type_id integer not null,\n"
                + "	interaction_id integer not null,\n"
                + "	foreign key (agent_state_id) references agent_states (id),\n"
                + "	foreign key (interaction_id) references interactions (id),\n"
                + "	foreign key (data_type_id) references data_types (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS possible_interaction_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	possible_value varchar(100) not null,\n"
                + "	default_value boolean not null default false,\n"
                + "	interaction_parameter_id integer not null,\n"
                + "	foreign key (interaction_parameter_id) references interaction_parameters (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS conversations (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	created_time varchar(100) not null ,\n"
                + "	agent_id integer not null,\n"
                + "	finished_time varchar(100) default null,\n"
                + "	foreign key (agent_id) references agents (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS messages (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	message_time varchar(100) not null ,\n"
                + "	interaction_id integer not null,\n"
                + "	conversation_id integer not null,\n"
                + "	foreign key (interaction_id) references interactions (id),\n"
                + "	foreign key (conversation_id) references conversations (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS interaction_contents (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "	reading_value varchar(100) not null,\n"
                + "	reading_time varchar(100) not null ,\n"
                + "	message_id integer not null,\n"
                + "	interaction_parameter_id integer not null,\n"
                + "     generated_event_id integer not null default 0,"
                + "	foreign key (interaction_parameter_id) references interaction_parameters (id),\n"
                + "	foreign key (message_id) references messages (id)\n"
                + ");");
       db.execSQL("CREATE TABLE IF NOT EXISTS reports (\n"
                + "	id integer not null primary key autoincrement,\n"
                + "     subject integer not null,\n"
                + "	content_type varchar(100) not null,\n"
                + "     priority integer not null,\n"
                + "	content text not null ,\n"
                + "	anonymous_upload boolean not null,\n"
                + "	created_time varchar(100) not null,\n"
                + "	uses_urbosenti_xml_envelope boolean not null,\n"
                + "     content_size integer,\n"
                + "	target_uid varchar(100) not null,\n"
                + "	target_layer integer not null,\n"
                + "	target_address varchar(100),\n"
                + "	origin_uid varchar(100) not null,\n"
                + "	origin_layer integer not null,\n"
                + "	origin_address varchar(100),\n"
                + "	checked boolean not null,\n"
                + "	sent boolean not null,\n"
                + "	timeout integer,\n"
                + "	service_id integer not null,\n"
                + "	foreign key (service_id) references services (id)\n"
                + ");");
        db.execSQL(" CREATE TABLE IF NOT EXISTS generated_events (\n"
                + "   id integer not null primary key autoincrement,\n"
                + "   event_id integer not null,\n"
                + "   entity_id integer,\n"
                + "   component_id integer,\n"
                + "   time varchar(100) not null,\n"
                + "   timeout integer not null,\n"
                + "   event_type integer not null,"
                + "   foreign key (event_id) references events (id)\n"
                + ");");
        db.execSQL(" CREATE TABLE IF NOT EXISTS generated_actions (\n"
                + "   id integer not null primary key autoincrement,\n"
                + "   action_model_id integer not null,\n"
                + "   entity_id integer not null,\n"
                + "   component_id integer not null,\n"
                + "   action_type integer not null,\n"
                + "   parameters text,\n"
                + "   response_time varchar (100),\n"
                + "   feedback_id integer,\n"
                + "   feedback_description text,\n"
                + "   execution_plan_id int,\n"
                + "   event_id int not null,\n"
                + "   event_type int not null,\n"
                + "   foreign key (action_model_id) references actions (id),\n"
                + "   foreign key (event_id) references generated_events (id)\n"
                + ");");
    }

    @Override
    public void dropDatabase() throws SQLException {
    	db.execSQL("DROP TABLE agent_address_types;\n");
        db.execSQL("DROP TABLE communicative_acts;\n");
        db.execSQL("DROP TABLE agent_communication_languages;\n");
        db.execSQL("DROP TABLE agent_types;\n");
        db.execSQL("DROP TABLE data_types;\n");
        db.execSQL("DROP TABLE implementation_types;\n");
        db.execSQL("DROP TABLE devices;\n");
        db.execSQL("DROP TABLE targets_origins;\n");
        db.execSQL("DROP TABLE agents;\n");
        db.execSQL("DROP TABLE services;\n");
        db.execSQL("DROP TABLE interaction_directions;\n");
        db.execSQL("DROP TABLE interaction_types;\n");
        db.execSQL("DROP TABLE entity_types;\n");
        db.execSQL("DROP TABLE service_types;\n");
        db.execSQL("DROP TABLE \"main\".\"action_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"action_feedback_answer\";\n");
        db.execSQL("DROP TABLE \"main\".\"action_parameters\";\n");
        db.execSQL("DROP TABLE \"main\".\"actions\";\n");
        db.execSQL("DROP TABLE \"main\".\"components\";\n");
        db.execSQL("DROP TABLE \"main\".\"entities\";\n");
        db.execSQL("DROP TABLE \"main\".\"entity_state_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"entity_states\";\n");
        db.execSQL("DROP TABLE \"main\".\"event_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"event_parameters\";\n");
        db.execSQL("DROP TABLE \"main\".\"event_targets_origins\";\n");
        db.execSQL("DROP TABLE \"main\".\"events\";\n");
        db.execSQL("DROP TABLE \"main\".\"instance_state_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"instance_states\";\n");
        db.execSQL("DROP TABLE \"main\".\"instances\";\n");
        db.execSQL("DROP TABLE \"main\".\"possible_action_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"possible_event_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"possible_instance_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"possible_entity_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"conversations\";\n");
        db.execSQL("DROP TABLE \"main\".\"messages\";\n");
        db.execSQL("DROP TABLE \"main\".\"possible_interaction_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"possible_agent_state_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"interaction_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"interaction_parameters\";\n");
        db.execSQL("DROP TABLE \"main\".\"agent_state_contents\";\n");
        db.execSQL("DROP TABLE \"main\".\"agent_states\";\n");
        db.execSQL("DROP TABLE \"main\".\"interactions\";\n");
        db.execSQL("DROP TABLE \"main\".\"reports\";\n");
        db.execSQL("DROP TABLE \"main\".\"generated_events\";\n");
        db.execSQL("DROP TABLE \"main\".\"generated_actions\";");
    }

}
