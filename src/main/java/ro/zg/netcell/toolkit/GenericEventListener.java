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

import ro.zg.util.execution.ExecutionEntity;

public class GenericEventListener implements ExecutionEntity<EventObject, EventProcessingReport>{
    private String eventType;
    private ExecutionEntity<EventObject, EventProcessingReport> eventProcessor;
        
    public GenericEventListener(String type,ExecutionEntity<EventObject, EventProcessingReport> processor){
	this.eventType = type;
	this.eventProcessor = processor;
    }
    
    public void onEvent(EventObject e){
	GenericEvent ge = new GenericEvent(e,eventType);
	try {
	    execute(ge);
	} catch (Exception e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }
    
    public EventProcessingReport execute(EventObject input) throws Exception {
	return eventProcessor.execute(input);
    }
    

}
