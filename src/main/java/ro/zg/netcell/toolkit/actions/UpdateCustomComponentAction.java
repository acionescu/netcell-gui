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

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.model.ObjectFormModel;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.vo.definitions.ConfigurableComponentDefinition;
import ro.zg.netcell.vo.definitions.EntityDefinition;

public class UpdateCustomComponentAction extends NetcellAbstractAction{

    public UpdateCustomComponentAction(NetcellGuiController controller) {
	super(controller);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3142931681304460109L;

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	ConfigurableComponentDefinition ccd = (ConfigurableComponentDefinition)e.getSource();
	Form form = controller.getFormForObject(ccd, "update");
	form.initialize();
	JOptionPane pane = new JOptionPane(form.getUi().getHolder());
	JDialog dialog = pane.createDialog("Configure custom component");
	form.getModel().addPropertyChangeListener(new PropertyChangeMonitor());
	dialog.setVisible(true);
    }

    class PropertyChangeMonitor implements PropertyChangeListener{
	public void propertyChange(PropertyChangeEvent evt) {
	    ObjectFormModel fm = (ObjectFormModel)evt.getSource();
	    controller.onEntityChanged((EntityDefinition)fm.getDataObject());
	}
    }
}
