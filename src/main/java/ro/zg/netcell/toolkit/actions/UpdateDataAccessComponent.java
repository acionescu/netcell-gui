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
package ro.zg.netcell.toolkit.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.impl.MapDataSource;
import ro.zg.java.forms.model.ObjectFormModel;
import ro.zg.netcell.constants.InputParameterType;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.vo.definitions.DataAccessComponentDefinition;
import ro.zg.netcell.vo.definitions.DataSourceDefinition;
import ro.zg.netcell.vo.definitions.EntityDefinition;
import ro.zg.scriptdao.constants.DataSourceConfigParameters;
import ro.zg.util.data.ConfigurationData;
import ro.zg.util.data.UserInputParameter;

public class UpdateDataAccessComponent extends NetcellAbstractAction {
    private static final long serialVersionUID = 4094427407180933187L;

    public UpdateDataAccessComponent(NetcellGuiController controller) {
	super(controller);
    }

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	DataAccessComponentDefinition dacd = (DataAccessComponentDefinition) e.getSource();
	/*
	 * set the allowed values for each input param on this component from the used datasource
	 */
	Map<String, DataSourceDefinition> datasources = controller.getDatasources();
	DataSourceDefinition ds = datasources.get(dacd.getDataSourceName());
	Map dsConfigParams = (Map) ds.getConfigData().getParameterValue(
		DataSourceConfigParameters.DATA_ACCESS_COMPONENT_PARAMS);
	ConfigurationData configData = dacd.getConfigData();
	if (configData != null) {
	    Map<String, UserInputParameter> userInputParams = configData.getUserInputParams();
	    if (userInputParams != null) {
		for (UserInputParameter uip : userInputParams.values()) {
		    if (uip != null && dsConfigParams != null) {
			UserInputParameter templateParam = (UserInputParameter) dsConfigParams.get(uip.getName());
			uip.setAllowedValues(templateParam.getAllowedValues());
		    }
		}
	    }
	}
	dacd.setDataSourceType(ds.getDatasourceType());
	Form form = controller.getFormForObject(dacd, "update");
	Map<String, Object> auxiliaryData = new HashMap<String, Object>();
	auxiliaryData.put("types", InputParameterType.valuesAsStringArray());
//	form.setAuxiliaryData(auxiliaryData);
	form.setFormDataSource(new MapDataSource(auxiliaryData));
	form.initialize();
	JOptionPane pane = new JOptionPane(form.getUi().getHolder());
	JDialog dialog = pane.createDialog("Configure data access component");
	form.getModel().addPropertyChangeListener(new PropertyChangeMonitor(dialog, pane));
	dialog.validate();
	dialog.setResizable(true);
	dialog.setVisible(true);
    }

    class PropertyChangeMonitor implements PropertyChangeListener {
	private JDialog dialog;
	private JOptionPane pane;

	public PropertyChangeMonitor(JDialog d, JOptionPane p) {
	    dialog = d;
	    pane = p;
	}

	public void propertyChange(PropertyChangeEvent evt) {
	    String propName = evt.getPropertyName();
	    if (propName.contains("inputParameters")) {
		int index = propName.lastIndexOf(".");
		if (index > 0 && "inputParameters".equals(propName.substring(0, index))) {
		    dialog.pack();
		}
	    }
	    ObjectFormModel fm = (ObjectFormModel) evt.getSource();
	    controller.onEntityChanged((EntityDefinition) fm.getDataObject());
	}
    }

}
