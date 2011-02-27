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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.impl.MapDataSource;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.vo.definitions.DataSourceDefinition;

public class AddDatasourceAction extends NetcellAbstractAction {
    private static final long serialVersionUID = 7904661127265108443L;

    public AddDatasourceAction(NetcellGuiController controller) {
	super(controller);
    }

    public void actionPerformedDelegate(ActionEvent e) throws Exception {
	DataSourceDefinition dsDef = new DataSourceDefinition();
	Form form = controller.getFormForObject(dsDef, "create");
	List<String> datasourcesTypes = controller.getDatasourcesTypes();
	Map<String, Object> auxiliaryData = new HashMap<String, Object>();
	auxiliaryData.put("datasourcesTypes", datasourcesTypes);
//	form.setAuxiliaryData(auxiliaryData);
	form.setFormDataSource(new MapDataSource(auxiliaryData));
	form.initialize();
	boolean exists = false;
	String name = null;
	boolean selectedEmpty= false;
	boolean cancelled = false;
	do {
	    JOptionPane pane = new JOptionPane(form.getUi().getHolder());
	    JDialog dialog = pane.createDialog("Select datasource type");
	    dialog.setVisible(true);
	    name = dsDef.getId();
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
//	    System.out.println("dst = "+dsTemplates);
//	    System.out.println("ds type ="+dsDef.getDatasourceType());
//	    DataSourceDefinition template = dsTemplates.get(dsDef.getDatasourceType().toString());
//	    dsDef.setConfigData((ConfigurationData)ObjectsUtil.copy(template.getConfigData()));
	    controller.addNewDatasource(dsDef);
	    controller.fireDatasourceUpdate(name);
	}
	

    }

}
