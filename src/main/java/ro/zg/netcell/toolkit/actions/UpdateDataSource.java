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

import javax.swing.JOptionPane;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.model.ObjectFormModel;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.vo.definitions.DataSourceDefinition;
import ro.zg.netcell.vo.definitions.EntityDefinition;

public class UpdateDataSource extends NetcellAbstractAction {
    private static final long serialVersionUID = 5273953308610894696L;

    public UpdateDataSource(NetcellGuiController controller) {
	super(controller);
    }

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	DataSourceDefinition dsDef = (DataSourceDefinition) e.getSource();
	Form form = controller.getFormForObject(dsDef, "update");
	form.initialize();
//	form.getModel().addPropertyChangeListener(new PropertyChangeMonitor());
//	JOptionPane pane = new JOptionPane(form.getUi().getHolder());
//	pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
//	JDialog dialog = pane.createDialog("Configure datasource");
//	int value = JOptionPane.showOptionDialog(null, form.getUi().getHolder(), "Configure datasource", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
//	dialog.setVisible(true);
	handleDialog(form, "Configure datasource");
    }

//    class PropertyChangeMonitor implements PropertyChangeListener{
//	public void propertyChange(PropertyChangeEvent evt) {
//	    System.out.println("what");
//	    ObjectFormModel fm = (ObjectFormModel)evt.getSource();
//	    controller.onEntityChanged((EntityDefinition)fm.getDataObject());
//	}
//    }
    
}
