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
package ro.zg.netcell.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.TransferHandler;

import ro.zg.netcell.gui.components.WorkFlowUI;

public class ComponentsDragAndDropController {
    private NetcellGuiController mainController;
    private JTree componentsTree;
    private WorkFlowUI workflowUi;

    public ComponentsDragAndDropController(JTree tree, WorkFlowUI workflow, NetcellGuiController c) {
	this.componentsTree = tree;
	this.workflowUi = workflow;
	this.mainController = c;
	init();
    }

    private void init() {
	workflowUi.setTransferHandler(new WorkflowUiTransferHandler());
	componentsTree.setDragEnabled(true);
    }

    class WorkflowUiTransferHandler extends TransferHandler {

	/**
	     * 
	     */
	private static final long serialVersionUID = -4934714244899257315L;

	public WorkflowUiTransferHandler() {
	    super("Tree Path");

	}

	public boolean canImport(TransferSupport support) {
	    // DataFlavor[] dataFlavors = support.getDataFlavors();
	    // Transferable t = support.getTransferable();
	    // for (DataFlavor df : dataFlavors) {
	    // System.out.println("can import? " + df);
	    //
	    // }

	    return true;
	}

	public boolean importData(TransferSupport support) {
	    DataFlavor[] dataFlavors = support.getDataFlavors();
	    String componentId = null;
	    for (DataFlavor df : dataFlavors) {
		if (df.getRepresentationClass().equals(String.class) && df.isMimeTypeEqual(DataFlavor.getTextPlainUnicodeFlavor())) {
		    try {
			componentId = (String) support.getTransferable().getTransferData(df);
			return mainController.addComponent(/*componentId, */support.getDropLocation().getDropPoint());
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	    return false;
	}
    }
}
