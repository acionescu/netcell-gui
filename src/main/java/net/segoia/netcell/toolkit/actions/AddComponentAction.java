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

import java.awt.event.MouseEvent;
import java.util.EventObject;

import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.toolkit.EventProcessingReport;

public class AddComponentAction extends BaseNetcellGuiAction {

    public AddComponentAction(NetcellGuiController controller) {
	super(controller);
    }

    public EventProcessingReport execute(EventObject input) throws Exception {
	System.out.println("AddComponentAction: "+input);
	MouseEvent me = (MouseEvent)input.getSource();
	controller.addComponent(me.getPoint());
	return null;
    }

}
