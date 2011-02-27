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

import ro.zg.netcell.entities.util.collections.GetCollectionSizeEntity;
import ro.zg.netcell.gui.NetcellGuiController;

public class RemoveEntityAction extends NetcellAbstractAction{
    
    /**
     * 
     */
    private static final long serialVersionUID = 7434970871391597551L;

    public RemoveEntityAction(NetcellGuiController controller) {
	super(controller);
    }

    @Override
    protected void actionPerformedDelegate(ActionEvent e) throws Exception {
	controller.removeSelectedEntity();	
    }

}
