/**
 * netcell-gui - A Swing GUI for netcell ESB
 * Copyright (C) 2009  Adrian Cristian Ionescu - https://github.com/acionescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.segoia.netcell.toolkit;

import java.awt.AWTEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import net.segoia.commons.exceptions.ExceptionHandler;
import net.segoia.util.execution.ExecutionEntity;

public class EventProcessor implements ExecutionEntity<EventObject, EventProcessingReport> {
    private ExceptionHandler<EventProcessingReport> exceptionHandler;
    /**
     * key -full class name of the registered event </br> value - the registered event processor
     */
    private Map<String, ExecutionEntity<EventObject, EventProcessingReport>> actions = new HashMap<String, ExecutionEntity<EventObject, EventProcessingReport>>();

    private String getTypes(Object o) {
	if (o == null) {
	    return "";
	}
	if (o instanceof GenericEvent) {
	    GenericEvent ge = (GenericEvent) o;
	    return ge.getType() + "." + getTypes(ge.getSource());
	} else if (o instanceof AWTEvent) {
	    AWTEvent awe = (AWTEvent) o;
	    return getTypes(awe.getSource()) +"." + o.getClass().getSimpleName() + "." + awe.getID();
	} else if (o instanceof EventObject) {
	    EventObject eo = (EventObject) o;
	    return getTypes(eo.getSource()) +"." + eo.getClass().getSimpleName();
	}
	else {
	    return o.getClass().getSimpleName();
	}
	
    }

    public void registerAction(String type, ExecutionEntity<EventObject, EventProcessingReport> action) {
	actions.put(type, action);
    }

    public EventProcessingReport execute(EventObject input) throws Exception {
	String types = getTypes(input);
	ExecutionEntity<EventObject, EventProcessingReport> action = actions.get(types);
	if (action != null) {
	    return action.execute(input);
	}
	return null;
    }

    // public void processEvent(GenericEvent input) {
    // try {
    // execute(input);
    // } catch (ContextAwareException e) {
    // if (exceptionHandler != null) {
    // try {
    // exceptionHandler.handle(e);
    // } catch (ContextAwareException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // }
    // } catch (Exception e) {
    // if (exceptionHandler != null) {
    // try {
    // exceptionHandler.handle(new ContextAwareException("SWING_EVENT_PROCESSING_ERROR", e));
    // } catch (ContextAwareException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // }
    // }
    // }

}
