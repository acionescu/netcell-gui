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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitPane;

import ro.zg.netcell.gui.components.EntitiesTree;
import ro.zg.netcell.gui.components.WorkFlowUI;
import ro.zg.netcell.gui.components.WorkflowComponentControlForm;
import ro.zg.netcell.toolkit.Toolkit;
import ro.zg.netcell.toolkit.listeners.SwingEventsListener;

public class NetcellGui extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -2787778066438840802L;
    private MultiSplitPane mainPane;
    private EntitiesTree entitiesTree;
    private WorkFlowUI workflowUi;
    private WorkflowComponentControlForm wfCompControlForm;
    private JScrollPane workflowSrollPane;
    private NetcellGuiController controller;
    private JScrollPane controlsHolder;
    private JToolBar toolBar;
    private JToolBar entitiesToolBar;
    private JScrollPane entitiesScrollPane;
    
    private SplashScreen splashScreen = new SplashScreen();

    public NetcellGui(NetcellGuiController controller) {
	this.controller = controller;
	this.setVisible(false);
	createLayout();
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void displayWorkingArea(boolean b) {
	setVisible(b);
    }
    
    private void createLayout() {
	this.setSize(1224, 600);
	createMainPane();
	getContentPane().add(mainPane);
//	createEntitiesTree();
	createEntitiesView();
	createWorkflowUi();
	createToolBar();

	mainPane.add(entitiesScrollPane, "left");
	/* create work area : middle */

	workflowSrollPane = new JScrollPane(workflowUi);
//	workflowSrollPane.setViewportView(workflowUi);
//	workflowSrollPane.setAutoscrolls(true);
	

	JPanel middlePanel = new JPanel(new BorderLayout());
	middlePanel.add(toolBar, BorderLayout.NORTH);
	middlePanel.add(workflowSrollPane, BorderLayout.CENTER);

	mainPane.add(middlePanel, "middle");

	/* controls */
	wfCompControlForm = new WorkflowComponentControlForm(controller);
	controlsHolder = new JScrollPane();
	JPanel controlsPane = new JPanel(new GridBagLayout());
	controlsPane.setMaximumSize(new Dimension(180, 2000));
	controlsPane.setPreferredSize(new Dimension(180, 600));
	GridBagConstraints paneConstraints = new GridBagConstraints();
	paneConstraints.anchor = GridBagConstraints.NORTHWEST;
	paneConstraints.fill = GridBagConstraints.BOTH;
	paneConstraints.weightx = 1.0;
	paneConstraints.weighty = 1.0;
	controlsPane.add(controlsHolder, paneConstraints);
	// controllsScrollPane.setViewportView(wfCompControlForm);
	// controllsScrollPane.setMaximumSize(new Dimension(180, 2000));
	// controllsScrollPane.setPreferredSize(new Dimension(180, 600));
	mainPane.add(controlsPane, "right");

    }

    public void setControlsComponent(JComponent c) {
	controlsHolder.setViewportView(c);
	controlsHolder.setAutoscrolls(true);
	controlsHolder.revalidate();
	validate();

    }

    private void createMainPane() {
	String layoutDef = "(ROW (LEAF name=left) (LEAF name=middle weight=0.7) (LEAF name=right))";
	MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
	mainPane = new MultiSplitPane();
	mainPane.setDividerSize(5);
	mainPane.getMultiSplitLayout().setModel(modelRoot);
    }

    private void createEntitiesTree() {
	// DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Entities");
	entitiesTree = new EntitiesTree();
    }

    private void createWorkflowUi() {
	workflowUi = new WorkFlowUI();
	workflowUi.addComponentListener(new WorkflowUiListener());
	SwingEventsListener sel = new SwingEventsListener("workflowui", Toolkit.getInstance());
	workflowUi.addMouseListener(sel);
    }

    private void createToolBar() {
	toolBar = new JToolBar();
    }
    
    private void createEntitiesView(){
	entitiesTree = new EntitiesTree();
	entitiesToolBar = new JToolBar();
	JPanel entitiesPanel = new JPanel(new BorderLayout());
	entitiesPanel.add(entitiesToolBar, BorderLayout.NORTH);
	entitiesPanel.add(entitiesTree, BorderLayout.CENTER);
	entitiesScrollPane = new JScrollPane(entitiesPanel);
    }
    
    public void showSplasScreen() {
	splashScreen.showSplash();
    }
    
    public void hideSplashScreen() {
	splashScreen.hideSplash();
    }

    public EntitiesTree getEntitiesTree() {
	return entitiesTree;
    }

    public WorkFlowUI getWorkflowUi() {
	return workflowUi;
    }

    public WorkflowComponentControlForm getWfCompControlForm() {
	return wfCompControlForm;
    }

    public JToolBar getToolBar() {
	return toolBar;
    }

    /**
     * @return the entitiesToolBar
     */
    public JToolBar getEntitiesToolBar() {
        return entitiesToolBar;
    }



    class WorkflowUiListener implements ComponentListener {

	public void componentHidden(ComponentEvent e) {
	    // TODO Auto-generated method stub

	}

	public void componentMoved(ComponentEvent e) {
//	    workflowSrollPane.scrollRectToVisible(workflowUi.getBounds());
//	    workflowSrollPane.revalidate();
//	    workflowUi.revalidate();
	}

	public void componentResized(ComponentEvent e) {
//	    Dimension ss = workflowSrollPane.getSize();
//	    Dimension ws = workflowUi.getPreferredSize();
//	    Rectangle r = new Rectangle(ss.width-ws.width,ss.height-ws.height,ss.width,ss.height);
//	    System.out.println(r);
//	    workflowSrollPane.getViewport().scrollRectToVisible(r);
//	    System.out.println("hau: "+workflowUi.getPreferredSize());
	}

	public void componentShown(ComponentEvent e) {
	    // TODO Auto-generated method stub

	}
    }
}
