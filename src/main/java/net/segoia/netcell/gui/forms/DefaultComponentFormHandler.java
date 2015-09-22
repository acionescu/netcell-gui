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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.segoia.java.forms.Form;
import net.segoia.java.forms.impl.MapDataSource;
import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.gui.WorkflowComponentConfigFormListener;
import net.segoia.netcell.vo.OutputParameter;
import net.segoia.netcell.vo.configurations.ComponentConfiguration;
import net.segoia.netcell.vo.configurations.ComponentExitPointsMapping;
import net.segoia.netcell.vo.configurations.FixedExitPoints;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;
import net.segoia.netcell.vo.definitions.ExecutableEntityDefinition;
import net.segoia.util.data.GenericNameValue;

public class DefaultComponentFormHandler extends BaseFormHandler<WorkFlowComponentConfiguration>{

    public DefaultComponentFormHandler(){
	
    }
    public DefaultComponentFormHandler(FormManager manger){
	super(manger);
    }
    
    public Form createForm(WorkFlowComponentConfiguration dataObject) {
	Form form = createFormForObject(dataObject);
//	form.setAuxiliaryData(getAuxiliaryDataForWfComp(dataObject));
	form.setFormDataSource(new MapDataSource(getAuxiliaryDataForWfComp(dataObject)));
	form.addPropertyChangeListener(new WorkflowComponentConfigFormListener(getFormManager().getController(), form));
	form.addPropertyChangeListener(new LocalFormPropertyListener(form));
	try {
	    form.initialize();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return form;
    }

    private Map<String, Object> getAuxiliaryDataForWfComp(WorkFlowComponentConfiguration wfCompConfig) {
	NetcellGuiController controller = getFormManager().getController();
	Map<String, Object> auxiliaryData = new HashMap<String, Object>();
	/* get the configuration for the component */
	ComponentConfiguration compConfig = wfCompConfig.getComponentConfig();
	/* get the actual component definition */
	ExecutableEntityDefinition compDefinition = (ExecutableEntityDefinition) controller.getEntityDefinitionById(compConfig.getComponent());
//	List<GenericNameValue> availableContextParameters = controller.getContextParamsAvailableToComponent(wfCompConfig);
	List<GenericNameValue> availableContextParameters = controller.getContextParamsList();
	auxiliaryData.put("inputParameters", compDefinition.getInputParameters());
	auxiliaryData.put("availableParameters", availableContextParameters);
//	auxiliaryData.put("outputParameters", compDefinition.getOutputParameterNames());
//	auxiliaryData.put("outputParameters", compDefinition.getOutputParameters());
	
	List<OutputParameter> outParams = new ArrayList<OutputParameter>();
	Map<String,List<String>> contextParamsByType = controller.getContextParameterNameByType();
	for(OutputParameter op : controller.getOutputParameters(wfCompConfig)){
	    if(contextParamsByType.containsKey(op.getType())){
		outParams.add(op.copy());
	    }
	}
	
	auxiliaryData.put("outputParameters", outParams);
	auxiliaryData.put("contextParamNamesByType", contextParamsByType);
	auxiliaryData.put("componentIds", controller.getAllComponentsIds());
	List<String> mapableExitParameters = compDefinition.getMapableExitParams();
	FixedExitPoints fixedExitPoints = compDefinition.getFixedExitPoints();
	ComponentExitPointsMapping cmap = wfCompConfig.getComponentMapping();
	if (mapableExitParameters.size() > 0) {
	    auxiliaryData.put("exitParameters", mapableExitParameters);
	    auxiliaryData.put("valuesForParameter", compDefinition.getPossibleValuesForExitParam());
	    cmap.setComplexMappingTypes();
	} 
//	else if(fixedExitPoints != null && cmap.getFixedMappings().size() == 0) {
//	    cmap.setFixedMappings(fixedExitPoints.asMap());
//	    cmap.setFixedMappingTypes();
//	}
	else {
	    cmap.setSimpleMappingTypes();
	}
	
	return auxiliaryData;
    }
    
    class LocalFormPropertyListener implements PropertyChangeListener{
	private Form form;
	
	public LocalFormPropertyListener(Form form){
	    this.form = form;
	}

	public void propertyChange(PropertyChangeEvent evt) {
	    if(evt.getPropertyName().startsWith("componentConfig.inputParameters")){
//		/* reconstruct the output params when input params change */
//		Map<String,Object> auxData = form.getModel().getAuxiliaryData();
//		NetcellGuiController controller = getFormManager().getController();
//		List<OutputParameter> outParams = new ArrayList<OutputParameter>();
//		Map<String,List<String>> contextParamsByType = controller.getContextParameterNameByType();
//		for(OutputParameter op : controller.getOutputParameters((WorkFlowComponentConfiguration)form.getModel().getDataObject())){
//		    if(contextParamsByType.containsKey(op.getType())){
//			outParams.add(op.copy());
//		    }
//		}
//		
//		auxData.put("outputParameters", outParams);
//		auxData.put("contextParamNamesByType", contextParamsByType);
		form.setFormDataSource(new MapDataSource(getAuxiliaryDataForWfComp((WorkFlowComponentConfiguration)form.getModel().getDataObject())));
	    }
	}
	
    }
    
}
