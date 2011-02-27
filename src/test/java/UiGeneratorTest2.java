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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import ro.zg.cfgengine.core.configuration.ConfigurationManager;
import ro.zg.cfgengine.util.PackageCfgLoader;
import ro.zg.java.forms.DefaultFormFactory;
import ro.zg.java.forms.Form;
import ro.zg.java.forms.FormConfig;
import ro.zg.java.forms.impl.MapDataSource;
import ro.zg.java.forms.swing.SwingFormUiFactory;
import ro.zg.netcell.vo.InputParameter;
import ro.zg.util.data.GenericNameValue;


public class UiGeneratorTest2 {
    public static void main(String[] args) throws Exception {
	ConfigurationManager cfgBuilder = PackageCfgLoader.getInstance().load("xmls/cfg/java-forms-handlers.xml",
		"xmls/ui-config-entrypoint.xml");
	
	
	
	
	Object[] types = {"String","Integer","Float","Date"};
	Map<String,Object> sources = new HashMap<String, Object>();
	sources.put("types", types);
	
	List availablePrams= new ArrayList();
	availablePrams.add("param1");
	availablePrams.add("param2");
	availablePrams.add("param3");
	availablePrams.add("param4");
//	sources.put("availableParameters", availablePrams);
	
	List<GenericNameValue> availableParameters = new ArrayList<GenericNameValue>();
	availableParameters.add(new GenericNameValue("param1",1));
	availableParameters.add(new GenericNameValue("param2",2f));
	availableParameters.add(new GenericNameValue("param3","3"));
	availableParameters.add(new GenericNameValue("param4",new BigDecimal("4")));
	sources.put("availableParameters", availableParameters);
	
	List<InputParameter> inputParams = new ArrayList<InputParameter>();
	inputParams.add(new InputParameter("input1",null,true,"Integer"));
	inputParams.add(new InputParameter("input2",null,false,"Float"));
	inputParams.add(new InputParameter("input3","are valoare",true,"String"));
	inputParams.add(new InputParameter("input4",null,false,"BigDecimal"));
	sources.put("inputParameters", inputParams);
	
	List<String> componentIds = new ArrayList<String>();
	componentIds.add("comp1");
	componentIds.add("comp2");
	componentIds.add("comp3");
	componentIds.add("comp4");
	componentIds.add("comp5");
	sources.put("componentIds", componentIds);
	
	List<String> exitParams = new ArrayList<String>();
	exitParams.add("exitParam1");
	exitParams.add("exitParam2");
	sources.put("exitParameters", exitParams);
	
	Map<String,List<String>> valuesForParamMap = new LinkedHashMap<String, List<String>>();
	List<String> valuesForParameter = new ArrayList<String>();
	valuesForParameter.add("val1");
	valuesForParameter.add("val2");
	valuesForParameter.add("val3");
	valuesForParameter.add("val4");
	valuesForParamMap.put("exitParam1", valuesForParameter);
	List<String> valuesForParameter2 = new ArrayList<String>();
	valuesForParameter2.add("val5");
	valuesForParameter2.add("val6");
	valuesForParamMap.put("exitParam2", valuesForParameter2);
	sources.put("valuesForParameter", valuesForParamMap);
	
	DefaultFormFactory dff = new DefaultFormFactory();
	dff.setUiFactory(new SwingFormUiFactory());
	
//	FormConfig fc = (FormConfig) cfgBuilder.getObjectById("WorkFlowDefinition");
	FormConfig fc = (FormConfig) cfgBuilder.getObjectById("ro.zg.netcell.vo.configurations.WorkFlowComponentConfiguration");
	Form f = dff.createForm(fc);
//	f.getModel().setAuxiliaryData(sources);
	f.setFormDataSource(new MapDataSource(sources));
	f.initialize();
	
	JFrame jf = new JFrame();
	jf.setSize(800, 600);
	jf.setVisible(true);
	jf.setLayout(new GridBagLayout());
	GridBagConstraints con = new GridBagConstraints();
	con.anchor = GridBagConstraints.NORTHWEST;
	con.fill = GridBagConstraints.BOTH;
	con.weightx = 1.0;
	jf.add((JComponent)f.getUi().getHolder(),con);
	
	jf.validate();
    }
}
