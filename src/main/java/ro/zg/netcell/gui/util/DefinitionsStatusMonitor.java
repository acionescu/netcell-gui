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
package ro.zg.netcell.gui.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ro.zg.netcell.vo.definitions.EntityDefinition;
import ro.zg.util.data.ObjectsUtil;

public class DefinitionsStatusMonitor {
    /**
     * Holds the original versions of the input definitions
     */
    private Map<String, EntityDefinition> originalVersions = new HashMap<String, EntityDefinition>();

    private Map<String, EntityDefinition> newDefinitions = new LinkedHashMap<String, EntityDefinition>();

    private Map<String, EntityDefinition> modifiedDefinitions = new LinkedHashMap<String, EntityDefinition>();

    private Map<String, EntityDefinition> removedDefinitions = new LinkedHashMap<String, EntityDefinition>();

    public DefinitionsStatusMonitor(Map<String, EntityDefinition> definitions) {
	if (definitions != null) {
	    populate(definitions.values());
	}
    }
    
    public DefinitionsStatusMonitor(Collection<EntityDefinition> definitions) {
	if (definitions != null) {
	    populate(definitions);
	}
    }
    
    private void populate(Collection<EntityDefinition> definitions) {
	if (definitions != null) {
	    for (EntityDefinition def : definitions) {
		/* create a deep copy for the input definitions */
		originalVersions.put(def.getId(), (EntityDefinition) ObjectsUtil.copy(def));
	    }
	}
    }

    public void onDefinitionChanged(EntityDefinition definition) {
	System.out.println("Definition changed: "+definition);
	String defId = definition.getId();
	/* it's a new definition if it's not contained in the original map and not already marked as new */
	if (!originalVersions.containsKey(defId)) {
	    if (!newDefinitions.containsKey(defId)) {
		newDefinitions.put(defId, definition);
	    }
	}
	else{
	    if(originalVersions.get(defId).equals(definition)){
		/* if the same as the original remove it from modified if exists */
		modifiedDefinitions.remove(defId);
	    }
	    else{
		if(!modifiedDefinitions.containsKey(defId)){
		    modifiedDefinitions.put(defId, definition);
		}
	    }
	}
    }
    
    public void onDefinitionRemoved(EntityDefinition definition){
	String defId = definition.getId();
	/* if contained in the original map, mark it as removed */
	if(originalVersions.containsKey(defId)){
	    removedDefinitions.put(defId, definition);
	}
    }
    
    public boolean isChanged(String defId){
	return (newDefinitions.containsKey(defId) || modifiedDefinitions.containsKey(defId) || removedDefinitions.containsKey(defId));
    }
    
    public boolean isNew(String defId){
	return newDefinitions.containsKey(defId);
    }

    public void createCheckPoint(EntityDefinition def){
	EntityDefinition re = removedDefinitions.remove(def.getId());
	if(re != null){
	    originalVersions.remove(re.getId());
	    return;
	}
	EntityDefinition ne = newDefinitions.remove(def.getId());
	if(ne != null){
	    originalVersions.put(ne.getId(), (EntityDefinition) ObjectsUtil.copy(ne));
	    return;
	}
	
	EntityDefinition me = modifiedDefinitions.remove(def.getId());
	if(me != null){
	    originalVersions.put(me.getId(), (EntityDefinition) ObjectsUtil.copy(me));
	    return;
	}
    }
    
}
