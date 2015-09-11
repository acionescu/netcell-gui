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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import ro.zg.java.forms.Form;
import ro.zg.java.forms.model.ObjectFormModel;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.vo.definitions.EntityDefinition;

public abstract class NetcellAbstractAction extends AbstractAction {
    private static final long serialVersionUID = -8540938922353762078L;
    protected NetcellGuiController controller;

    public NetcellAbstractAction(NetcellGuiController controller) {
	this.controller = controller;
    }

    protected abstract void actionPerformedDelegate(ActionEvent e) throws Exception;

    public void actionPerformed(ActionEvent e) {
	try {
	    actionPerformedDelegate(e);
	} catch (Exception e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
    }

    protected void handleDialog(Form form, String title) {
	PropertyChangeMonitor pcm = new PropertyChangeMonitor();
	form.getModel().addPropertyChangeListener(pcm);
	int value = JOptionPane.showOptionDialog(null, new JScrollPane((Component)form.getUi().getHolder()), title, JOptionPane.OK_CANCEL_OPTION,
		JOptionPane.PLAIN_MESSAGE, null, null, null);

	if (value == 0 && pcm.hasChanged()) {
	    ObjectFormModel fm = (ObjectFormModel) form.getModel();
	    controller.onEntityChanged((EntityDefinition) fm.getDataObject());
	    System.out.println("submit");
	}
    }

    class PropertyChangeMonitor implements PropertyChangeListener {
	private JDialog dialog;
	private String[] repackParams;
	private boolean checkForRepack;
	private boolean changed;

	public PropertyChangeMonitor() {

	}

	public PropertyChangeMonitor(JDialog d, String[] repackParams) {
	    dialog = d;
	    this.repackParams = repackParams;
	    if (repackParams != null && repackParams.length > 0) {
		checkForRepack = true;
	    }
	}

	public void propertyChange(PropertyChangeEvent evt) {
	    changed = true;
	    String propName = evt.getPropertyName();
	    if (checkForRepack && repackNeeded(propName)) {
		dialog.pack();
	    }
	}

	public boolean hasChanged() {
	    return changed;
	}

	private boolean repackNeeded(String changedParam) {
	    for (String p : repackParams) {
		if (changedParam.contains(p)) {
		    int index = changedParam.lastIndexOf(".");
		    if (index > 0 && p.equals(changedParam.substring(0, index))) {
			return true;
		    }
		}
	    }
	    return false;
	}
    }

}
