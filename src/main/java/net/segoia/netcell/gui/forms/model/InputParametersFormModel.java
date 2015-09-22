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
package net.segoia.netcell.gui.forms.model;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.segoia.java.forms.model.DefaultFormModel;
import net.segoia.java.forms.model.FormDataContext;
import net.segoia.java.forms.model.FormDataSource;
import net.segoia.netcell.constants.InputParameterLogicTypes;
import net.segoia.netcell.vo.InputParameter;
import net.segoia.netcell.vo.InputParameterConfig;
import net.segoia.netcell.vo.configurations.ComponentConfiguration;
import net.segoia.util.data.GenericNameValue;
import net.segoia.util.data.ValueType;
import net.segoia.util.data.type.ParameterType;

public class InputParametersFormModel extends DefaultFormModel {
    private ComponentConfiguration componentConfig;
    private List<InputParameterConfig> paramConfigs;
//    private Map<String, String> paramTypes = new HashMap<String, String>();
//    private Map<String, List<String>> allowedParamTypes = new HashMap<String, List<String>>();
    private Map<String,InputParameter> inputParamsMap=new HashMap<String, InputParameter>();

    public void setDataObject(Object dataObject) {
	super.setDataObject(dataObject);
	componentConfig = (ComponentConfiguration) dataObject;
	constructInputParamsConfigs();
    }

//    public void setAuxiliaryData(Map<String, Object> ad) {
//	super.setAuxiliaryData(ad);
//	constructInputParamsConfigs();
//    }

    public void setDataContext(FormDataContext fdc) {
	super.setDataContext(fdc);
	constructInputParamsConfigs();
    }
    
    public void setDatasource(FormDataSource fds) {
	super.setDatasource(fds);
	constructInputParamsConfigs();
    }
    
    
    private void constructInputParamsConfigs() {
	paramConfigs = new ArrayList<InputParameterConfig>();
//	List<InputParameter> inputParams = (List) getSourceData("inputParameters");
//	List<GenericNameValue> availableParams = (List) getSourceData("availableParameters");
	List<InputParameter> inputParams = (List)dataContext.getValue("inputParameters");
	List<GenericNameValue> availableParams = (List)dataContext.getValue("availableParameters");
	Map<String, ValueType> staticParams = componentConfig.getStaticInputParams();
	Map<String, String> dynamicParams = componentConfig.getDynamicInputParams();

	if (inputParams == null || availableParams == null) {
	    return;
	}

	for (InputParameter ip : inputParams) {
	    InputParameterConfig ipc = new InputParameterConfig(ip);
	    List<String> configTypes = new ArrayList<String>();
	    inputParamsMap.put(ip.getName(), ip);
//	    paramTypes.put(ip.getName(), ip.getType());
//	    allowedParamTypes.put(ip.getName(), ip.getAllowedTypes());
	    /* allow all types of mappings */
	    if (ip.getLogicType() == null || ip.getLogicType().equals(InputParameterLogicTypes.GENERIC_VALUE.name())) {
		if (!ip.isMandatory()) {
		    configTypes.add("EMPTY");
		    ipc.setConfigType("EMPTY");
		} else {
		    ipc.setConfigType("STATIC");
		}
		if (ip.getValue() != null) {
		    configTypes.add("DEFAULT");
		    ipc.setConfigType("DEFAULT");
		    ipc.setDefaultValue(ip.getValue());
		}
		configTypes.add("STATIC");
		if (staticParams.containsKey(ip.getName())) {
		    ipc.setConfigType("STATIC");
		    ipc.setValue(staticParams.get(ip.getName()).getValue());
		} else if (dynamicParams.containsKey(ip.getName())) {
		    ipc.setConfigType("DYNAMIC");
		    ipc.setValue(dynamicParams.get(ip.getName()));
		}
	    } else {
		/* allow only dynamic values */
		ipc.setConfigType("DYNAMIC");
		ipc.setValue(dynamicParams.get(ip.getName()));
	    }
	    List<String> availableParamsForType = getContextParamNamesForType(availableParams, ip.getName());
	    if (availableParamsForType.size() > 0) {
		configTypes.add("DYNAMIC");
		ipc.setAvailableValues(availableParamsForType);
	    }
	    paramConfigs.add(ipc);
	    ipc.setConfigTypes(configTypes);

	}
//	auxiliaryData.put("inputParamsConfigs", paramConfigs);
	dataContext.setValue("inputParamsConfigs", paramConfigs);
    }

    private List<String> getContextParamNamesForType(List<GenericNameValue> contextParams, String prmName) {
	List<String> params = new ArrayList<String>();
//	String type = paramTypes.get(prmName);
//	boolean anyTypeAllowed = type.equals(ParameterType.GENERIC_TYPE);
//	for (GenericNameValue contextParam : contextParams) {
//	    boolean addIt = false;
//	    if(anyTypeAllowed) {
//		addIt=true;
//	    }
//	    else if ((contextParam.getType() != null && contextParam.getType().equals(type))) {
//		addIt = true;
//	    } else if (type == null) {
//		List<String> allowedTypes = allowedParamTypes.get(prmName);
//		if (allowedTypes != null) {
//		    for (String at : allowedTypes) {
//			if (contextParam.getType().matches(at)) {
//			    addIt = true;
//			    break;
//			}
//		    }
//		}
//	    }
//
//	    if (addIt) {
//		params.add(contextParam.getName());
//	    }
//	}
	InputParameter parameter = inputParamsMap.get(prmName);
	if(parameter != null) {
	    return parameter.getMatchingParams(contextParams);
	}
	return params;
    }

    private void syncData() {
	Map<String, ValueType> staticParams = componentConfig.getStaticInputParams();
	Map<String, String> dynamicParams = componentConfig.getDynamicInputParams();
//	List<GenericNameValue> availableParams = (List) getSourceData("availableParameters");
	List<GenericNameValue> availableParams = (List)dataContext.getValue("availableParameters");
	staticParams.clear();
	dynamicParams.clear();

	for (InputParameterConfig ipc : paramConfigs) {
	    String configType = ipc.getConfigType();
	    String name = ipc.getName();
	    // System.out.println(ipc.getName()+"="+ipc.getValue());
	    if (configType.equals(InputParameterConfig.EMPTY)) {
		ipc.setValue(null);
	    } else if (configType.equals(InputParameterConfig.DEFAULT)) {
		ipc.setValue(ipc.getDefaultValue());
	    } else if (configType.equals(InputParameterConfig.STATIC)) {
		staticParams.put(name, new ValueType(ipc.getValue(), ipc.getType()));
	    } else if (configType.equals(InputParameterConfig.DYNAMIC)) {
		if (ipc.getValue() == null) {
		    List<String> availableParamsForType = getContextParamNamesForType(availableParams, ipc.getName());
		    if (availableParamsForType.size() > 0) {
			ipc.setValue(availableParamsForType.get(0));
		    }
		}
		if (ipc.getValue() != null) {
		    dynamicParams.put(name, ipc.getValue().toString());
		}
	    }
	}
    }

    private void resetParamValue(PropertyChangeEvent event) {
	int index1 = event.getPropertyName().indexOf(".") + 1;
	if (index1 > 0) {
	    int index2 = event.getPropertyName().indexOf(".", index1);
	    if (index2 > 0) {
		InputParameterConfig ipc = paramConfigs.get(Integer.parseInt(event.getPropertyName().substring(index1,
			index2)));
		ipc.setValue(null);
	    }
	}
    }

    public PropertyChangeEvent onNestedValueChanged(PropertyChangeEvent event) {
	// int index1 = event.getPropertyName().indexOf(".") + 1;
	// if (index1 > 0) {
	// int index2 = event.getPropertyName().indexOf(".", index1);
	// if (index2 > 0) {
	// InputParameterConfig ipc = paramConfigs.get(Integer.parseInt(event.getPropertyName().substring(index1,
	// index2)));
	// String name = ipc.getName();
	// System.out.println(name + "=" + ipc.getValue());
	// Map<String, ValueType> staticParams = componentConfig.getStaticInputParams();
	// Map<String, String> dynamicParams = componentConfig.getDynamicInputParams();
	// String configType = ipc.getConfigType();
	// if (configType.equals(InputParameterConfig.DEFAULT) || configType.equals(InputParameterConfig.EMPTY)) {
	// staticParams.remove(name);
	// dynamicParams.remove(name);
	// } else if (configType.equals(InputParameterConfig.STATIC)) {
	// dynamicParams.remove(name);
	// staticParams.put(name, new ValueType(ipc.getValue(), ipc.getType()));
	// } else if (configType.equals(InputParameterConfig.DYNAMIC)) {
	// staticParams.remove(name);
	// if (ipc.getValue() != null) {
	// dynamicParams.put(name, ipc.getValue().toString());
	// }
	// }
	// }
	// }
	if (event.getPropertyName().contains("configType")) {
	    resetParamValue(event);
	}
	syncData();
	
	return event;
    }

}
