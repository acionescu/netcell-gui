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
package net.segoia.netcell.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.segoia.java.forms.Form;
import net.segoia.java.forms.model.ObjectFormModel;
import net.segoia.netcell.vo.configurations.ComponentExitPoint;
import net.segoia.netcell.vo.configurations.ComponentExitPointsMapping;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;

public class WorkflowComponentConfigFormListener implements PropertyChangeListener {
    private NetcellGuiController controller;
    private Form form;

    public WorkflowComponentConfigFormListener(NetcellGuiController c, Form f) {
	this.controller = c;
	this.form = f;
    }

    public void propertyChange(PropertyChangeEvent evt) {
	String propName = evt.getPropertyName();
	System.out.println(propName + "=" + evt.getNewValue() + ", source " + evt.getSource());

	ObjectFormModel formModel = (ObjectFormModel) evt.getSource();
	WorkFlowComponentConfiguration wfc = (WorkFlowComponentConfiguration) formModel.getDataObject();
	ComponentExitPointsMapping cepm = wfc.getComponentMapping();

	if (propName.contains("exitValuesMappings")) {
	    if (propName.contains("mappingType")) {
		// ComponentExitPoint ep = (ComponentExitPoint) form.getModel().getObjectForProperty(propName);
		// boolean isExitPoint = ComponentExitPoint.LABEL.equals(evt.getNewValue());
		// controller.onExitPointMappingTypeChanged(ep.getExitPointName(), ep.getExitPointMapping(),
		// isExitPoint);
		// controller.onComponentMappingChanged();
	    } else if (propName.contains("nextComponentId")) {
		if (evt.getNewValue() != null) {
		    if (evt.getOldValue() == null) {
//			formModel.getFormData().setValue(propName, wfc.getLocalId());
			ComponentExitPoint ep = (ComponentExitPoint)formModel.getObjectForProperty(propName);
			ep.setNextComponentId(wfc.getLocalId());
		    }
		    controller.onComponentMappingChanged();
		}
	    } else if (propName.contains("exitPointMapping") && evt.getNewValue() != null) {
		ComponentExitPoint ep = (ComponentExitPoint) form.getModel().getObjectForProperty(propName);
		controller.onExitPointMappingChanged(ep.getExitPointName(), ep.getExitPointMapping());
	    }
	} else if (propName.contains("mappingType")) {

	    if (evt.getNewValue().equals(ComponentExitPointsMapping.NO_MAPPING)) {

		cepm.setExitPointPrmName(null);
		cepm.setNextComponentId(null);
	    } else if (evt.getNewValue().equals(ComponentExitPointsMapping.PARAMETER_MAPPING)) {
		cepm.setNextComponentId(null);
	    }
	    controller.onComponentMappingChanged();
	} else if (propName.contains("exitPointPrmName")) {
	    if (evt.getNewValue() != null) {
		controller.onComponentMappingChanged();
	    }
	} else if (propName.contains("nextComponentId")) {
	    if (evt.getNewValue() != null) {
		if (!propName.contains("errorMapping") && evt.getOldValue() == null) {
		    cepm.setNextComponentId(wfc.getLocalId());
		}
		controller.onComponentMappingChanged();
	    }
	} else if (propName.equals("localId")) {
	    controller.renameWorkflowComponent((String) evt.getOldValue(), (String) evt.getNewValue());
	}
	/* notify the controller that some data changed */
	controller.onPropertyChanged();
    }
}
