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
package ro.zg.netcell.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.gui.resources.ComponentControlsKeys;
import ro.zg.netcell.vo.configurations.ComponentExitPoint;
import ro.zg.netcell.vo.configurations.ComponentExitPointsMapping;
import ro.zg.netcell.vo.configurations.WorkFlowComponentConfiguration;
import ro.zg.netcell.vo.definitions.ComponentDefinition;

public class ComponentMappingsControls extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -9139070907253959122L;

    private NetcellGuiController controller;
    private GridBagConstraints constraints = new GridBagConstraints();
    private ComponentDefinition compDef;
    private WorkFlowComponentConfiguration compConfig;
    private Map<String, WorkFlowComponentConfiguration> allComponents;
    private String[] allComponentsIds;
    private ResourceBundle controlsResources = ResourceBundle.getBundle("ComponentControls");
    private JComboBox directMappingCombo;
    private JComboBox mappedParamCombo;
    private JLabel mappedParamLabel;

    private String currentMappingType;

    public ComponentMappingsControls(NetcellGuiController controller) {
	this.controller = controller;
    }

    public void populate(WorkFlowComponentConfiguration compConfig, ComponentDefinition compDef,
	    Map<String, WorkFlowComponentConfiguration> allComponents) {
	this.compConfig = compConfig;
	this.compDef = compDef;
	this.allComponents = allComponents;
	allComponentsIds = allComponents.keySet().toArray(new String[allComponents.size()]);
	init();
    }

    private void init() {
	removeAll();
	setLayout(new GridBagLayout());

	constraints.anchor = GridBagConstraints.NORTHWEST;
	List<String> mappingTypes = new ArrayList<String>();
	/* initialize mapping type */
	if (currentMappingType == null) {
	    currentMappingType = controlsResources.getString(ComponentControlsKeys.EP_MAPPING_NONE);
	    ComponentExitPointsMapping compMapping = compConfig.getComponentMapping();
	    if (compMapping != null && compMapping.getExitPointPrmName() != null) {
		currentMappingType = controlsResources.getString(ComponentControlsKeys.EP_MAPPING_PARAMETER);
	    }
	    else if(compMapping.getNextComponentId() != null){
		currentMappingType = controlsResources.getString(ComponentControlsKeys.EP_MAPPING_DIRECT);
	    }
	}
	mappingTypes.add(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_NONE));
	mappingTypes.add(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_DIRECT));
	/* check if this component has output parameters with discrete values */
	Map<String, String> simpleRespMappings = compDef.getSimpleResponseMappings();
	if (simpleRespMappings != null && simpleRespMappings.size() > 0) {
	    mappingTypes.add(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_PARAMETER));
	} else if(compConfig.getComponentMapping().getNextComponentId() != null){
	    // TODO check complex mappings
	    currentMappingType = controlsResources.getString(ComponentControlsKeys.EP_MAPPING_DIRECT);
	}
	JComboBox epMappingsCombo = UiElementsHelper.createStandardCombo(mappingTypes.toArray(new String[0]));
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	JLabel epMappingsLabel = new JLabel("Mapping type:");
	add(epMappingsLabel, constraints);
	constraints.fill = GridBagConstraints.NONE;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.insets = new Insets(0, 2, 0, 0);
	add(epMappingsCombo, constraints);

	if (!currentMappingType.equals(epMappingsCombo.getSelectedItem())) {
	    epMappingsCombo.setSelectedItem(currentMappingType);
	}
	epMappingsCombo.addActionListener(new ComponentMappingTypeChangedListener());
	createMappingControls();
    }

    private void createMappingControls() {
	if (currentMappingType.equals(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_DIRECT))) {
	    onDirectMapping();
	} else if (currentMappingType.equals(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_PARAMETER))) {
	    onParameterMapping();
	}
    }

    private void clean() {
	if (directMappingCombo != null) {
	    remove(directMappingCombo);
	    directMappingCombo = null;
	}
	if (mappedParamLabel != null) {

	}
    }

    private void onDirectMapping() {
	JLabel nextCompIdLablel = new JLabel(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_NEXT_COMP_ID));
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	add(nextCompIdLablel, constraints);
	ComponentExitPointsMapping compMappings = compConfig.getComponentMapping();
	String nextCompId = null;
	if (compMappings != null) {
	    nextCompId = compMappings.getNextComponentId();
	}
	directMappingCombo = UiElementsHelper.createStandardCombo(allComponentsIds);
	if (nextCompId != null) {
	    directMappingCombo.setSelectedItem(nextCompId);
	} else {
	    compConfig.getComponentMapping().setNextComponentId((String) directMappingCombo.getSelectedItem());
//	    controller.onNextComponentIdChanged();
	}
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	directMappingCombo.addActionListener(new DirectMappingChangedListener());
	add(directMappingCombo, constraints);
    }

    private void onParameterMapping() {
	compConfig.getComponentMapping().setNextComponentId(null);
	constraints.anchor = GridBagConstraints.WEST;
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	mappedParamLabel = new JLabel(controlsResources.getString(ComponentControlsKeys.EP_MAPPING_PARAMETER_NAME));
	add(mappedParamLabel, constraints);

	Map<String, String> simpleRespMappings = compDef.getSimpleResponseMappings();
	String[] validParamNames = null;
	if (simpleRespMappings != null && simpleRespMappings.size() > 0) {
	    String mappedParamName = compDef.getOutputParameters().get(0).getName();
	    validParamNames = new String[] { mappedParamName };
	    
	} else {
	    // TODO: check form complex response mappings
	}
	mappedParamCombo = UiElementsHelper.createStandardCombo(validParamNames);
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.insets = new Insets(0, 0, 5, 0);
	add(mappedParamCombo, constraints);

	ComponentExitPointsMapping compMappings = compConfig.getComponentMapping();
	String mappedParamName = compMappings.getExitPointPrmName();
	if (mappedParamName != null) {
	    mappedParamCombo.setSelectedItem(mappedParamName);
	}
	else{
	    compMappings.setExitPointPrmName((String)mappedParamCombo.getSelectedItem());
	}
	constraints.insets = new Insets(0, 0, 0, 0);
	constraints.weightx = 1.0;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	Map<Object, ComponentExitPoint> exitValuesMappings = compMappings.getExitValuesMappings();
	for (Map.Entry<Object, ComponentExitPoint> entry : exitValuesMappings.entrySet()) {
	    ComponentExitPoint ep = entry.getValue();
	    boolean isExitPoint = true;
	    String currentValue = ep.getExitPointMapping();
	    if (currentValue == null) {
		currentValue = ep.getNextComponentId();
		isExitPoint = false;
	    }
	    String exitPointName = entry.getKey().toString();
	    ExitPointMappingControls epControls = new ExitPointMappingControls(exitPointName, currentValue,
		    isExitPoint, allComponentsIds);
	    constraints.gridwidth = GridBagConstraints.REMAINDER;
	    add(epControls, constraints);
	    epControls.setExitPointMappingChangeListener(new ExitPointMappingChangeListener(exitPointName));
	    epControls.setComponentMappingChangeListener(new ComponentMappingChangeListener(exitPointName));
	    epControls.setMappingTypeChangeListener(new ExitPointMappingTypeChangeListener(epControls));
	}

    }

    /**
     * Listens for the component mapping type change
     * 
     * @author adi
     * 
     */
    class ComponentMappingTypeChangedListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    JComboBox combo = (JComboBox) e.getSource();
	    currentMappingType = (String) combo.getSelectedItem();
	    init();
	    revalidate();
	    controller.onComponentMappingChanged();
	}
    }

    /**
     * Listens for the change of the direct mapping component id
     * 
     * @author adi
     * 
     */
    class DirectMappingChangedListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    String nextCompId = (String) directMappingCombo.getSelectedItem();
	    compConfig.getComponentMapping().setNextComponentId(nextCompId);
	    controller.onNextComponentIdChanged();
	}

    }

    /**
     * Listenes for the change of an exit point mapping
     * 
     * @author adi
     * 
     */
    class ExitPointMappingChangeListener implements DocumentListener {
	private String exitPointName;

	public ExitPointMappingChangeListener(String epName) {
	    exitPointName = epName;
	}

	private void update(DocumentEvent e) {
	    Document d = e.getDocument();
	    try {
		String value = d.getText(0, d.getLength());
		ComponentExitPoint cep = compConfig.getComponentMapping().getExitValuesMappings().get(exitPointName);
		cep.setExitPointMapping(value);
		cep.setNextComponentId(null);
		controller.onExitPointMappingChanged(exitPointName, value);
	    } catch (BadLocationException e1) {
		System.out.println(e1);
	    }
	}

	public void changedUpdate(DocumentEvent e) {
	    update(e);
	}

	public void insertUpdate(DocumentEvent e) {
	    update(e);
	}

	public void removeUpdate(DocumentEvent e) {
	    update(e);
	}
    }

    /**
     * Listens for the change of a component mapping
     * 
     * @author adi
     * 
     */
    class ComponentMappingChangeListener implements ActionListener {
	private String exitPointName;

	public ComponentMappingChangeListener(String epName) {
	    exitPointName = epName;
	}

	public void actionPerformed(ActionEvent e) {
	    JComboBox combo = (JComboBox) e.getSource();
	    String nextCompId = (String) combo.getSelectedItem();
	    ComponentExitPoint cep = compConfig.getComponentMapping().getExitValuesMappings().get(exitPointName);
	    cep.setExitPointMapping(null);
	    cep.setNextComponentId(nextCompId);
	    controller.onNextComponentIdChanged();
	}
    }

    /**
     * Listenes for the chage of the mapping type for an exit point
     * 
     * @author adi
     * 
     */
    class ExitPointMappingTypeChangeListener implements ActionListener {
	private ExitPointMappingControls epControl;

	public ExitPointMappingTypeChangeListener(ExitPointMappingControls epControl) {
	    this.epControl = epControl;
	}

	public void actionPerformed(ActionEvent e) {
	    ComponentExitPoint cep = compConfig.getComponentMapping().getExitValuesMappings().get(
		    epControl.getExitValue());
	    if (epControl.isExitPoint()) {
		cep.setExitPointMapping(epControl.getCurrentMapping());
		cep.setNextComponentId(null);
	    } else {
		cep.setExitPointMapping(null);
		cep.setNextComponentId(epControl.getCurrentMapping());
	    }
	    // controller.onNextComponentIdChanged();
	    controller.onExitPointMappingTypeChanged(epControl.getExitValue(), epControl.getCurrentMapping(), epControl
		    .isExitPoint());
	}
    }
}
