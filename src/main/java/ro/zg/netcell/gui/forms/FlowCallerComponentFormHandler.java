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
import java.util.List;
import java.util.Map;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.impl.MapDataSource;
import ro.zg.java.forms.model.FormDataContext;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.gui.WorkflowComponentConfigFormListener;
import ro.zg.netcell.vo.OutputParameter;
import ro.zg.netcell.vo.configurations.ComponentConfiguration;
import ro.zg.netcell.vo.configurations.ComponentExitPointsMapping;
import ro.zg.netcell.vo.configurations.WorkFlowComponentConfiguration;
import ro.zg.netcell.vo.definitions.ComponentDefinition;
import ro.zg.netcell.vo.definitions.WorkFlowDefinition;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.ValueType;

public class FlowCallerComponentFormHandler extends BaseFormHandler<WorkFlowComponentConfiguration>{

    public FlowCallerComponentFormHandler(){
	
    }
    
    public FlowCallerComponentFormHandler(FormManager fm){
	super(fm);
    }
    
    public Form createForm(WorkFlowComponentConfiguration dataObject) {
	NetcellGuiController controller = getFormManager().getController();
	/* create a form using the configuration for the used component */
	Form form = getFormManager().createFormForId(dataObject.getComponentConfig().getComponent());
	form.setDataObject(dataObject);
//	form.setAuxiliaryData(getAuxiliaryDataForWfComp(dataObject));
	form.setFormDataSource(new MapDataSource(getAuxiliaryDataForWfComp(dataObject)));
	form.addPropertyChangeListener(new FormDataChangedListener(form));
	form.addPropertyChangeListener(new WorkflowComponentConfigFormListener(getFormManager().getController(), form));
//	createInputOutputParams(form,(String)form.getModel().getAuxiliaryData().get("flowId"));
	createInputOutputParams(form, (String)form.getFormDataContext().getValue("flowId"));
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
	ComponentDefinition compDefinition = (ComponentDefinition) controller.getEntityDefinitionById(compConfig.getComponent());
//	List<GenericNameValue> availableContextParameters = controller.getContextParamsAvailableToComponent(wfCompConfig);
	List<GenericNameValue> availableContextParameters = controller.getContextParamsList();
	List<String> allFlowsIds = controller.getAllFlowsIds();
	auxiliaryData.put("allFlowsIds", allFlowsIds);
	String flowId = allFlowsIds.get(0);
	ValueType flowIdVt = compConfig.getStaticInputParams().get("fid");
	if(flowIdVt != null){
	    flowId = flowIdVt.getValue();
	}
	auxiliaryData.put("flowId", flowId);

	auxiliaryData.put("availableParameters", availableContextParameters);

	auxiliaryData.put("componentIds", controller.getAllComponentsIds());
	List<String> mapableExitParameters = compDefinition.getMapableExitParams();
	ComponentExitPointsMapping cmap = wfCompConfig.getComponentMapping();
	if (mapableExitParameters.size() > 0) {
	    auxiliaryData.put("exitParameters", mapableExitParameters);
	    cmap.setComplexMappingTypes();
	    auxiliaryData.put("valuesForParameter", compDefinition.getPossibleValuesForExitParam());
	} else {
	    cmap.setSimpleMappingTypes();
	}
	
	return auxiliaryData;
    }
    
    private void createInputOutputParams(Form form, String flowId){
//	Map<String,Object> ad = form.getModel().getAuxiliaryData();
	FormDataContext formDataContext = form.getFormDataContext();
	NetcellGuiController controller = getFormManager().getController();
	/* get the workflow definition by the selected id */
	WorkFlowDefinition selectedFlowDef = (WorkFlowDefinition)controller.getEntityDefinitionById(flowId);
	/* 
	 * set the input and output parameters to correspond with the selected flow 
	 * input and output parameters
	 */
//	ad.put("inputParameters", selectedFlowDef.getInputParameters());
	formDataContext.setValue("inputParameters", selectedFlowDef.getInputParameters());
	
	List<OutputParameter> outParams = new ArrayList<OutputParameter>();
	Map<String,List<String>> contextParamsByType = controller.getContextParameterNameByType();
	for(OutputParameter op : selectedFlowDef.getOutputParameters()){
	    if(contextParamsByType.containsKey(op.getType())){
		outParams.add(op.copy());
	    }
	}
//	ad.put("outputParameters", outParams);
//	ad.put("contextParamNamesByType", contextParamsByType);
//	form.setAuxiliaryData(ad);
	formDataContext.setValue("outputParameters", outParams);
	formDataContext.setValue("contextParamNamesByType", contextParamsByType);
	form.setDataContext(formDataContext);
    }
    
    private void updateInputOutputParams(Form form, String flowId){
	createInputOutputParams(form, flowId);
	WorkFlowComponentConfiguration wfConf = (WorkFlowComponentConfiguration)form.getModel().getDataObject();
	/* set the selected flow id on the static parameters of the workflow component */
	wfConf.getComponentConfig().getStaticInputParams().put("fid", new ValueType(flowId,"String"));
	form.getModel().refresh();
    }

    class FormDataChangedListener implements PropertyChangeListener{
	private Form form;
	
	public FormDataChangedListener(Form f){
	    this.form = f;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
	    String propName = evt.getPropertyName();
	    if(propName.equals("flowId")){
		String flowId = (String)evt.getNewValue();
		updateInputOutputParams(form, flowId);
	    }
	    else if(propName.contains("inputParameters")){
		/* 
		 * make sure to reset the flow id on the static parameters, because it was deleted
		 * by the form mechanism
		 */
		WorkFlowComponentConfiguration wfConf = (WorkFlowComponentConfiguration)form.getModel().getDataObject();
//		String flowId = (String)form.getModel().getAuxiliaryData().get("flowId");
		String flowId = (String)form.getFormDataContext().getValue("flowId");
		wfConf.getComponentConfig().getStaticInputParams().put("fid", new ValueType(flowId,"String"));
	    }
	}
    }
    
}
