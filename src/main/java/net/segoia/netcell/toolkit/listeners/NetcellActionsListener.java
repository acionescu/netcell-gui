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
package net.segoia.netcell.toolkit.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.segoia.java.forms.Form;
import net.segoia.java.forms.model.ObjectFormModel;
import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.toolkit.actions.AddCustomComponentAction;
import net.segoia.netcell.toolkit.actions.AddDataAccessComponentAction;
import net.segoia.netcell.toolkit.actions.AddDatasourceAction;
import net.segoia.netcell.toolkit.actions.AddNewWorkflowAction;
import net.segoia.netcell.toolkit.actions.CommitSelectedEntityAction;
import net.segoia.netcell.toolkit.actions.DisplayExecuteCurrentFlowForm;
import net.segoia.netcell.toolkit.actions.ReloadEngineAction;
import net.segoia.netcell.toolkit.actions.RemoveEntityAction;
import net.segoia.netcell.toolkit.actions.UpdateCustomComponentAction;
import net.segoia.netcell.toolkit.actions.UpdateDataAccessComponent;
import net.segoia.netcell.toolkit.actions.UpdateDataSource;
import net.segoia.netcell.toolkit.actions.UpdateScheduledJobAction;
import net.segoia.netcell.vo.definitions.EntityDefinition;

public class NetcellActionsListener implements ActionListener {
    private NetcellGuiController controller;
    private Map<String, Action> actions;

    public NetcellActionsListener(NetcellGuiController c) {
	controller = c;
	actions = new HashMap<String, Action>();
	actions.put("add.workflow", new AddNewWorkflowAction(c));
	actions.put("remove.entity", new RemoveEntityAction(c));
	actions.put("commit", new CommitSelectedEntityAction(c));
	actions.put("display.execute.flow.form", new DisplayExecuteCurrentFlowForm(c));
	actions.put("add.datasource", new AddDatasourceAction(c));
	actions.put("update.datasource", new UpdateDataSource(c));
	actions.put("add.datacomponent", new AddDataAccessComponentAction(c));
	actions.put("update.datacomponent", new UpdateDataAccessComponent(c));
	actions.put("add.customcomponent", new AddCustomComponentAction(c));
	actions.put("update.customcomponent", new UpdateCustomComponentAction(c));
	actions.put("update.scheduledjob", new UpdateScheduledJobAction(c));
	actions.put("engine.reload", new ReloadEngineAction(c));
	actions.put("execute", new DisplayExecuteCurrentFlowForm(c));
    }

    public void actionPerformed(ActionEvent e) {
	if (actions.containsKey(e.getActionCommand())) {
	    actions.get(e.getActionCommand()).actionPerformed(e);
	}
    }
   
}
