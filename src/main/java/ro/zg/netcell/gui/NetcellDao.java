/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.netcell.gui;

import java.util.List;
import java.util.Map;

import ro.zg.commons.exceptions.ContextAwareException;
import ro.zg.commons.exceptions.ExceptionContext;
import ro.zg.netcell.client.NetcellRmiClient;
import ro.zg.netcell.control.Command;
import ro.zg.netcell.control.CommandResponse;
import ro.zg.netcell.control.NetCell;
import ro.zg.netcell.gui.vo.ExecuteFlowRequest;
import ro.zg.netcell.vo.DefinitionsRepository;
import ro.zg.netcell.vo.InputParameter;
import ro.zg.netcell.vo.definitions.DataSourceDefinition;
import ro.zg.netcell.vo.definitions.EntityDefinition;
import ro.zg.netcell.vo.definitions.EntityType;

public class NetcellDao {
    private NetCell client;
    
    public NetcellDao(){
	client = new NetcellRmiClient("localhost", 2000, "NetcellRmiReceiver");
    }
    
    private CommandResponse execute(Command c){
	try {
	    return client.execute(c);
	} catch (Exception e) {
	    // TODO use an exception handler to deal with this error
	    e.printStackTrace();
	    return null;
	}
	
    }
    
    public DefinitionsRepository getDefinitions() throws Exception{
	Command c = new Command();
	c.setName("get_definitions");
	CommandResponse cr = client.execute(c);
	return (DefinitionsRepository) cr.getValue("definitions");
    }
    
    public Map<String, DataSourceDefinition> getDatasourcesTemplates() throws Exception{
	Command c = new Command();
	c.setName("get_datasources_templates");
	CommandResponse cr = client.execute(c);
	return (Map) cr.getValue("datasources-templates");
    }
    
    public Map<String, DataSourceDefinition> getDatasourcesDefinitions() throws Exception{
	Command c = new Command();
	c.setName("get_datasources_definitions");
	CommandResponse cr = client.execute(c);
	return (Map) cr.getValue("datasources");
    }
    
    public EntityDefinition createEntity(EntityDefinition ed) throws Exception{
	Command c = new Command();
	c.setName("create_entity");
	c.put("definition", ed);
	CommandResponse cr = client.execute(c);
	return (EntityDefinition) cr.getValue("definition");
    }
    
    public EntityDefinition createEntityDirectoryStructure(String name, EntityType type) throws Exception{
	Command c = new Command();
	c.setName("create_entity_dir_structure");
	c.put("name", name);
	c.put("type", type);
	CommandResponse cr = client.execute(c);
	return (EntityDefinition) cr.getValue("definition");
    }
    
    public EntityDefinition updateEntity(EntityDefinition ed) throws Exception{
	Command c = new Command();
	c.setName("update_entity");
	c.put("definition", ed);
	CommandResponse cr = client.execute(c);
	return (EntityDefinition) cr.getValue("definition");
    }
    
    public boolean removeEntity(EntityDefinition ed) throws Exception{
	Command c = new Command();
	c.setName("remove_entity");
	c.put("definition", ed);
	CommandResponse cr = client.execute(c);
	if(!cr.isSuccessful()){
	    ExceptionContext ec = new ExceptionContext();
	    cr.copyTo(ec);
	    throw new ContextAwareException("REMOVE_FAILED",ec);
	}
	return (Boolean)cr.getValue("removed");
    }
    
    public CommandResponse execute(ExecuteFlowRequest req){
	Command c = new Command();
	c.setName("execute");
	c.put("fid",req.getFlowId());
	for(InputParameter ip : req.getInputParameters()){
	    c.put(ip.getName(),ip.getValue());
	}
	return execute(c);
    }
    
    public List<String> getDefinitionTypes(String type) throws Exception{
	Command c = new Command();
	c.setName("get_definition_types");
	c.put("parent-type", type);
	CommandResponse cr = client.execute(c);
	return (List)cr.getValue("types");
    }
    
    public List<String> getTemplates(String type) throws Exception{
	Command c = new Command();
	c.setName("get_templates_for_entity");
	c.put("parent-type", type);
	CommandResponse cr = client.execute(c);
	return (List)cr.getValue("templates");
    }
    
    public void reloadEngine() {
	Command c = new Command();
	c.setName("reload_engine");
	try {
	    CommandResponse cr = client.execute(c);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
