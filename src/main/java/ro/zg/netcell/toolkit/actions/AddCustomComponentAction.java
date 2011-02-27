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
import ro.zg.netcell.vo.definitions.ConfigurableComponentDefinition;
import ro.zg.netcell.vo.definitions.EntitiesTypes;

public class AddCustomComponentAction extends NetcellAbstractAction{

    public AddCustomComponentAction(NetcellGuiController controller) {
	super(controller);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 6339771162351019179L;

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	List<String> templates = controller.getTemplates(EntitiesTypes.CONFIGURABLE_COMPONENT);
	
	ConfigurableComponentDefinition ccd = new ConfigurableComponentDefinition();
	Form form = controller.getFormForObject(ccd, "create");
	Map<String,Object> auxiliaryData = new HashMap<String, Object>();
	auxiliaryData.put("templatesIds", templates);
//	form.setAuxiliaryData(auxiliaryData);
	form.setFormDataSource(new MapDataSource(auxiliaryData));
	form.initialize();
	
	boolean exists = false;
	String name = null;
	boolean selectedEmpty= false;
	boolean cancelled = false;
	do {
	    JOptionPane pane = new JOptionPane(form.getUi().getHolder());
	    JDialog dialog = pane.createDialog("Configure custom component");
	    dialog.setVisible(true);
	    name = ccd.getId();
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
	    controller.addNewCustomComponent(ccd);
	    controller.fireCustomComponentUpdate(name);
	}
    }

}
