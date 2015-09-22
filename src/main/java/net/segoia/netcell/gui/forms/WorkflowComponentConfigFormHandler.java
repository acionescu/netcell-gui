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
package net.segoia.netcell.gui.forms;

import net.segoia.java.forms.Form;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;

public class WorkflowComponentConfigFormHandler extends AbstractFormHandler<WorkFlowComponentConfiguration> {

    public WorkflowComponentConfigFormHandler(FormManager fm) {
	super(fm);
	init();
    }

    public WorkflowComponentConfigFormHandler() {
	
    }

    public void init() {
	defaultHandler = new DefaultComponentFormHandler(getFormManager());
//	subHandlers.put("core.logic.embeded-flow", new FlowCallerComponentFormHandler(getFormManager()));
    }

    public Form createForm(WorkFlowComponentConfiguration dataObject) {
	/* try to get a handler for the specific component */
	FormHandler<WorkFlowComponentConfiguration> handler = subHandlers.get(dataObject.getComponentConfig()
		.getComponent());
	if (handler == null) {
	    /* if not found, use default */
	    handler = defaultHandler;
	}
	return handler.createForm(dataObject);

    }

}
