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
package ro.zg.netcell.gui.forms;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.impl.MapDataSource;
import ro.zg.netcell.constants.InputParameterType;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.vo.definitions.WorkFlowDefinition;
import ro.zg.util.data.GenericNameValue;

public class WorkflowDefinitionFormHandler extends BaseFormHandler<WorkFlowDefinition> {

    public WorkflowDefinitionFormHandler() {

    }

    public WorkflowDefinitionFormHandler(FormManager fm) {
	super(fm);
    }

    public Form createForm(WorkFlowDefinition dataObject) {
	NetcellGuiController controller = getFormManager().getController();
	Form form = createFormForObject(dataObject);
	Map<String, Object> auxiliaryData = new HashMap<String, Object>();
	auxiliaryData.put("types", InputParameterType.valuesAsStringArray());
	auxiliaryData.put("componentIds", controller.getAllComponentsIds());
	List<GenericNameValue> outputParams = dataObject.getDefaultParametersList();
//	outputParams.addAll(controller.getContextParamsList());
	outputParams.addAll(dataObject.getPossibleOutputParameters());
	System.out.println(outputParams);
	auxiliaryData.put("contextParameters", outputParams);
	List<String> contextParamNames = new ArrayList<String>();
	Map<String,GenericNameValue> contextParamsMap = new LinkedHashMap<String, GenericNameValue>();
	for (GenericNameValue cp : outputParams) {
	    contextParamNames.add(cp.getName());
	    contextParamsMap.put(cp.getName(), cp);
	}
	auxiliaryData.put("contextParameterNames", contextParamNames);
	auxiliaryData.put("contextParamsMap", contextParamsMap);

//	form.setAuxiliaryData(auxiliaryData);
	form.setFormDataSource(new MapDataSource(auxiliaryData));
	form.addPropertyChangeListener(new WorkflowDefModifiedListener());
	try {
	    form.initialize();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return form;
    }

    class WorkflowDefModifiedListener implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent evt) {
	    getFormManager().getController().onPropertyChanged();
	}

    }
}
