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

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.segoia.netcell.gui.NetcellGuiController;

public class AddNewWorkflowAction extends NetcellAbstractAction {
    private static final long serialVersionUID = 639851263676446316L;

    public AddNewWorkflowAction(NetcellGuiController controller) {
	super(controller);
	// TODO Auto-generated constructor stub
    }

    public void actionPerformedDelegate(ActionEvent e) {
	String name = null;
	boolean exists = false;
	boolean isEmpty = false;
	do {
	    name = JOptionPane.showInputDialog("Enter the name of the flow:");
	    isEmpty = (name == null) || name.trim().equals("");
//	    if (isEmpty) {
//		JOptionPane.showMessageDialog((JComponent) e.getSource(),
//			"The name cannot be empty.Pleas enter something.", "Empty name", JOptionPane.ERROR_MESSAGE);
//	    } else {
		exists = controller.checkEntityExists(name);
		if (exists) {
		    JOptionPane.showMessageDialog((JComponent) e.getSource(),
			    "A resource with this name already exists.Please use another.", "Overlapping name",
			    JOptionPane.ERROR_MESSAGE);
		}
//	    }
	} while (exists);
	if (name != null) {
	    controller.addNewWorkflow(name);
	}
    }

}
