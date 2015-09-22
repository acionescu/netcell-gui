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
package net.segoia.netcell.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.vo.InputParameter;
import net.segoia.netcell.vo.OutputParameter;
import net.segoia.netcell.vo.configurations.ComponentConfiguration;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;
import net.segoia.netcell.vo.definitions.ComponentDefinition;
import net.segoia.util.data.GenericNameValue;
import net.segoia.util.data.ValueType;

public class WorkflowComponentControlForm extends JPanel implements Scrollable {

    /**
     * 
     */
    private static final long serialVersionUID = -2365424331794814122L;
    private GridBagConstraints constraints;
    private JLabel localIdLabel;
    private JTextField localId;

    private ResourceBundle controlsResources;
    private NetcellGuiController controller;
    private WorkFlowComponentConfiguration currentCompConfig;

    public WorkflowComponentControlForm(NetcellGuiController controller) {
	this.controller = controller;
	controlsResources = ResourceBundle.getBundle("ComponentControls");
	createForm();
    }

    protected void createForm() {
	removeAll();
	GridBagLayout layout = new GridBagLayout();
	setLayout(layout);
	constraints = new GridBagConstraints();
	constraints.insets = new Insets(5, 5, 0, 5);
	constraints.anchor = GridBagConstraints.NORTHWEST;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	// constraints.gridwidth = GridBagConstraints.RELATIVE;
	localIdLabel = new JLabel("Local id:");
	add(localIdLabel, constraints);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	localId = new JTextField();
	localId.setPreferredSize(new Dimension(150, 20));
	add(localId, constraints);
    }

    public void populate(WorkFlowComponentConfiguration wfCompConfig, ComponentDefinition compDef,
	    List<GenericNameValue> availableParams, Map<String, WorkFlowComponentConfiguration> allComponents) {
	createForm();
	currentCompConfig = wfCompConfig;
	localId.setText(wfCompConfig.getLocalId());

	/* input parameters */
	JSeparator ipsep = new JSeparator(SwingConstants.HORIZONTAL);
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.insets = new Insets(5, 0, 5, 0);
	ipsep.setBackground(Color.black);
	add(ipsep, constraints);
	constraints.gridwidth = 0;
	constraints.insets = new Insets(0, 5, 0, 5);
	JLabel inputParamsLabel = new JLabel("Input parameters:");
	add(inputParamsLabel, constraints);
	constraints = new GridBagConstraints();
	// constraints.fill=GridBagConstraints.HORIZONTAL;
	constraints.anchor = GridBagConstraints.WEST;
	// constraints.weightx = 1.0;
	// constraints.gridx = 0;
	// constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.gridwidth = 0;
	ComponentConfiguration compConfig = wfCompConfig.getComponentConfig();
	Map<String, ValueType> staticParams = compConfig.getStaticInputParams();
	Map<String, String> dynamicParams = compConfig.getDynamicInputParams();
	
	/* populate input parameters */
	for (InputParameter p : compDef.getInputParameters()) {
	    String paramName = p.getName();
	    List<String> availableContextParamsForType = getContextParamNamesForType(availableParams, p.getType());
	    InputParameterControls paramControls = null;
	    /* check if defined as static parameter */
	    if (staticParams!= null && staticParams.containsKey(paramName)) {
		paramControls = new InputParameterControls(p, InputParameterControls.STATIC, staticParams
			.get(paramName).getValue(), availableContextParamsForType);
	    }
	    /* check if defined as dynamic parameter */
	    else if (dynamicParams != null && dynamicParams.containsKey(paramName)) {
		paramControls = new InputParameterControls(p, InputParameterControls.DYNAMIC, dynamicParams
			.get(paramName), availableContextParamsForType);
	    }
	    /* the parameter has default value */
	    else if (p.getValue() != null) {
		paramControls = new InputParameterControls(p, InputParameterControls.DEFAULT, p.getValue(),
			availableContextParamsForType);
	    }
	    /* empty */
	    else {
		paramControls = new InputParameterControls(p, InputParameterControls.EMPTY, null,
			availableContextParamsForType);
	    }

	    /* add listeners */
	    paramControls.setStaticValueChangedListener(new StaticValueChangedListener(paramName));
	    paramControls.setDynamicValueChangedListener(new DynamicValueChangedListener(paramName));

	    add(paramControls, constraints);
	}

	/* output parameters */
	JSeparator opsep = new JSeparator(SwingConstants.HORIZONTAL);
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.insets = new Insets(5, 0, 5, 0);
	opsep.setBackground(Color.black);
	add(opsep, constraints);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.insets = new Insets(0, 5, 0, 5);
	JLabel outputParamsLabel = new JLabel("Output parameters:");
	add(outputParamsLabel, constraints);

	Map<String, String> outputParamsMappings = wfCompConfig.getOutputParamsMappings();
	List<OutputParameter> componentOutputParams = compDef.getOutputParameters();
	constraints = new GridBagConstraints();
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.gridwidth = 2;
	// constraints.gridheight = componentOutputParams.size();
	constraints.anchor = GridBagConstraints.WEST;
	for (OutputParameter outputParam : componentOutputParams) {
	    String paramName = outputParam.getName();
	    JLabel paramNameLabel = new JLabel(paramName + ":");
	    // constraints = new GridBagConstraints();
	    // constraints.insets = new Insets(0, 5, 0, 0);
	    constraints.weightx = 1.0;
	    // constraints.gridwidth = GridBagConstraints.RELATIVE;
	    add(paramNameLabel, constraints);
	    JTextField mappedNameTf = UiElementsHelper.createStandardTextField();
	    constraints.insets = new Insets(0, 0, 0, 0);
	    // constraints.fill = GridBagConstraints.NONE;
	    constraints.gridwidth = GridBagConstraints.REMAINDER;
	    constraints.weightx = 0.0;
	    String mappedName = null;
	    if (outputParamsMappings != null) {
		mappedName = outputParamsMappings.get(paramName);
	    }
	    if (mappedName != null) {
		mappedNameTf.setText(mappedName);
	    }
	    add(mappedNameTf, constraints);
	    mappedNameTf.getDocument().addDocumentListener(new OutputParamMappingChangedListener(paramName));
	}

	/* exit points mappings */
	constraints = new GridBagConstraints();
	constraints.gridx = 0;
	constraints.gridwidth = 0;
	JSeparator epsep = new JSeparator(SwingConstants.HORIZONTAL);
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.insets = new Insets(5, 0, 5, 0);
	epsep.setBackground(Color.black);
	add(epsep, constraints);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.insets = new Insets(0, 5, 0, 5);
	JLabel exitPointsLabel = new JLabel("Exit points mappings:");
	add(exitPointsLabel, constraints);

	// constraints = new GridBagConstraints();
	constraints.fill = GridBagConstraints.NONE;
	constraints.gridx = 0;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.insets = new Insets(0, 5, 0, 0);
	constraints.anchor = GridBagConstraints.WEST;
	ComponentMappingsControls compMappingsControls = new ComponentMappingsControls(controller);
	compMappingsControls.populate(wfCompConfig, compDef, allComponents);
	add(compMappingsControls, constraints);

    }

    private List<String> getContextParamNamesForType(List<GenericNameValue> contextParams, String type) {
	List<String> params = new ArrayList<String>();
	for (GenericNameValue contextParam : contextParams) {
	    if (type.equals(contextParam.getType())) {
		params.add(contextParam.getName());
	    }
	}
	return params;
    }

    /**
     * Listens for a static parameter value change
     * 
     * @author adi
     * 
     */
    class StaticValueChangedListener implements DocumentListener {
	private String paramName;

	public StaticValueChangedListener(String name) {
	    paramName = name;
	}

	private void updateChange(DocumentEvent e) {
	    Document d = e.getDocument();
	    try {
		currentCompConfig.getComponentConfig().getDynamicInputParams().remove(paramName);
		String value = d.getText(0, d.getLength());
		ValueType vt = new ValueType();
		vt.setValue(value);
		currentCompConfig.getComponentConfig().getStaticInputParams().put(paramName, vt);
	    } catch (BadLocationException e1) {
		System.out.println(e1);
	    }
	}

	public void changedUpdate(DocumentEvent e) {
	    updateChange(e);
	}

	public void insertUpdate(DocumentEvent e) {
	    updateChange(e);
	}

	public void removeUpdate(DocumentEvent e) {
	    updateChange(e);
	}
    }

    /**
     * Listens for an output parameter mapping change
     * 
     * @author adi
     * 
     */
    class OutputParamMappingChangedListener implements DocumentListener {
	private String paramName;

	private void updateChange(DocumentEvent e) {
	    Document d = e.getDocument();
	    try {
		String value = d.getText(0, d.getLength());
		ValueType vt = new ValueType();
		vt.setValue(value);
		currentCompConfig.getOutputParamsMappings().put(paramName, value);
	    } catch (BadLocationException e1) {
		System.out.println(e1);
	    }
	}

	public OutputParamMappingChangedListener(String name) {
	    paramName = name;
	}

	public void changedUpdate(DocumentEvent e) {
	    updateChange(e);
	}

	public void insertUpdate(DocumentEvent e) {
	    updateChange(e);
	}

	public void removeUpdate(DocumentEvent e) {
	    updateChange(e);
	}

    }

    /**
     * Listens for a dynamic parameter value change
     * 
     * @author adi
     * 
     */
    class DynamicValueChangedListener implements ActionListener {
	private String paramName;

	public DynamicValueChangedListener(String name) {
	    paramName = name;
	}

	public void actionPerformed(ActionEvent e) {
	    System.out.println(e);
	    JComboBox combo = (JComboBox) e.getSource();
	    currentCompConfig.getComponentConfig().getStaticInputParams().remove(paramName);
	    currentCompConfig.getComponentConfig().getDynamicInputParams().put(paramName,
		    combo.getSelectedItem().toString());
	}

    }

    public Dimension getPreferredScrollableViewportSize() {
	// return new Dimension(200,600);
	return null;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	// TODO Auto-generated method stub
	return 0;
    }

    public boolean getScrollableTracksViewportHeight() {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean getScrollableTracksViewportWidth() {
	// TODO Auto-generated method stub
	return false;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
	// TODO Auto-generated method stub
	return 0;
    }

}
