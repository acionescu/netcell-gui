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
package net.segoia.netcell.toolkit.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.segoia.java.forms.Form;
import net.segoia.java.forms.impl.MapDataSource;
import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.vo.definitions.DataAccessComponentDefinition;
import net.segoia.netcell.vo.definitions.DataSourceDefinition;
import net.segoia.scriptdao.constants.DataSourceConfigParameters;
import net.segoia.util.data.ObjectsUtil;

public class AddDataAccessComponentAction extends NetcellAbstractAction {
    private static final long serialVersionUID = -7242995267898099589L;

    public AddDataAccessComponentAction(NetcellGuiController controller) {
	super(controller);
    }

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	Map<String,DataSourceDefinition> datasources = controller.getDatasources();
	List<String> datasourcesNames = new ArrayList<String>(datasources.keySet());
	Map<String,Object> auxiliaryData = new HashMap<String, Object>();
	auxiliaryData.put("datasourcesNames", datasourcesNames);
	
	DataAccessComponentDefinition dacd = new DataAccessComponentDefinition();
	Form form = controller.getFormForObject(dacd,"create");
//	form.setAuxiliaryData(auxiliaryData);
	form.setFormDataSource(new MapDataSource(auxiliaryData));
	form.initialize();
	
	boolean exists = false;
	String name = null;
	boolean selectedEmpty= false;
	boolean cancelled = false;
	do {
	    JOptionPane pane = new JOptionPane(form.getUi().getHolder());
	    JDialog dialog = pane.createDialog("Configure data access component");
	    dialog.setVisible(true);
	    name = dacd.getId();
	    boolean isEmpty = (name == null || name.trim().equals(""));
	    exists = false;
	    if(!isEmpty){
		exists = controller.checkEntityExists(name);
	    }
	    Object paneValue = pane.getValue();
	    cancelled = paneValue == null;
	    selectedEmpty = isEmpty && !cancelled;
	} while (exists || selectedEmpty);
	
	if(!cancelled){
	    DataSourceDefinition ds = datasources.get(dacd.getDataSourceName());
	    dacd.setDataSourceType(ds.getDatasourceType());
	    Map configParams =  (Map)ds.getConfigData().getParameterValue(DataSourceConfigParameters.DATA_ACCESS_COMPONENT_PARAMS);
	    if(configParams != null){
		dacd.getConfigData().setUserInputParams((Map)ObjectsUtil.copy(configParams));
	    }
	    
	    controller.addNewDataAccessComponent(dacd);
	    controller.fireDataAccessComponentUpdate(dacd.getId());
	}
	
    }
}
