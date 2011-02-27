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
package ro.zg.netcell.toolkit;

import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.Map;

import ro.zg.netcell.toolkit.listeners.SwingEventsListener;

public class Toolkit extends EventProcessor{
    /**
     * The registered tools
     */
    private Map<String,Tool> tools = new LinkedHashMap<String, Tool>();
    
    private static Toolkit _instance = new Toolkit();
    
    private Toolkit(){
	
    }
    
    public static Toolkit getInstance(){
	return _instance;
    }
    
    /**
     * Current active tool
     */
    private Tool selectedTool;

    public EventProcessingReport execute(EventObject input) throws Exception {
	return selectedTool.execute(input);
    }
    
    public SwingEventsListener createSwingListenerForTool(String eventType){
	SwingEventsListener sel = new SwingEventsListener(eventType,this);
	return sel;
    }
        
    public void registerTool(Tool t){
	tools.put(t.getName(), t);
	if(selectedTool == null){
	    selectedTool = t;
	}
    }

    public void removeTool(Tool t){
	tools.remove(t.getName());
    }
    
    public void selectTool(String toolName){
	selectedTool = tools.get(toolName);
    }
}
