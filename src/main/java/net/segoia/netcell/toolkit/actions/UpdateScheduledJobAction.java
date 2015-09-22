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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.segoia.java.forms.Form;
import net.segoia.java.forms.model.ObjectFormModel;
import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.vo.definitions.EntityDefinition;
import net.segoia.netcell.vo.definitions.ScheduledJobDefinition;

public class UpdateScheduledJobAction  extends NetcellAbstractAction{

    /**
     * 
     */
    private static final long serialVersionUID = -6548730117087648299L;

    public UpdateScheduledJobAction(NetcellGuiController controller) {
	super(controller);
	// TODO Auto-generated constructor stub
    }

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	System.out.println("update scheduled job");
	ScheduledJobDefinition sjd = (ScheduledJobDefinition)e.getSource();
	Form form = controller.getFormForObject(sjd, "update");
	form.initialize();
	JOptionPane pane = new JOptionPane(new JScrollPane((Component)form.getUi().getHolder()));
	JDialog dialog = pane.createDialog("Configure scheduled job");
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
