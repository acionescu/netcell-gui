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
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import ro.zg.netcell.gui.resources.ComponentControlsKeys;

public class ExitPointMappingControls extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 4496571059494777045L;

    private String exitValue;
    private String currentMapping;
    private boolean isExitPoint;
    private String[] allComponentsIds;

    private String exitPointText;
    private String componentMappingText;

    private ResourceBundle controlsBundle;
    private GridBagConstraints constraints;

    private ActionListener componentMappingChangeListener;
    private DocumentListener exitPointMappingChangeListener;
    private ActionListener mappingTypeChangeListener;

    private JTextField exitPointMappingTf;
    private JComboBox componentsCombo;
    private JComboBox mappingTypesCombo;

    public ExitPointMappingControls(String exitValue, String currentMapping, boolean isExitPoint,
	    String[] allComponentsIds) {
	this.exitValue = exitValue;
	this.currentMapping = currentMapping;
	this.isExitPoint = isExitPoint;
	this.allComponentsIds = allComponentsIds;
	controlsBundle = ResourceBundle.getBundle("ComponentControls");
	exitPointText = controlsBundle.getString(ComponentControlsKeys.EP_MAPPING_EXIT_POINT);
	componentMappingText = controlsBundle.getString(ComponentControlsKeys.EP_MAPPING_COMPONENT);
	init();
    }

    private void init() {
	removeAll();
	exitPointMappingTf = null;
	componentsCombo = null;
	mappingTypesCombo = null;
	setLayout(new GridBagLayout());
	constraints = new GridBagConstraints();
	constraints.insets = new Insets(0, 5, 0, 0);
	constraints.anchor = GridBagConstraints.WEST;
	// constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.gridwidth = 3;
	constraints.gridheight = 1;
	constraints.weightx = 0.2;
	JLabel exitValueLabel = new JLabel(exitValue + ":");
	add(exitValueLabel, constraints);
	// constraints.anchor = GridBagConstraints.EAST;
	constraints.insets = new Insets(0, 2, 0, 0);
	constraints.weightx = 0.0;
	if (isExitPoint) {
	    exitPointMappingTf = UiElementsHelper.createStandardTextField();
	    if (currentMapping == null) {
		currentMapping = exitValue;
	    }
	    exitPointMappingTf.setText(currentMapping);
	    add(exitPointMappingTf, constraints);
	} else {
	    componentsCombo = UiElementsHelper.createStandardCombo(allComponentsIds);
	    if (currentMapping != null) {
		componentsCombo.setSelectedItem(currentMapping);
	    }
	    else{
		currentMapping = (String)componentsCombo.getSelectedItem();
	    }
	    add(componentsCombo, constraints);
	}
	
	String[] mappingTypes = { exitPointText, componentMappingText };
	mappingTypesCombo = UiElementsHelper.createStandardCombo(mappingTypes);
	if (isExitPoint) {
	    mappingTypesCombo.setSelectedItem(exitPointText);
	} else {
	    mappingTypesCombo.setSelectedItem(componentMappingText);
	}
	add(mappingTypesCombo, constraints);
	mappingTypesCombo.addActionListener(new MappingTypeChangedListener());
	addListeners();
    }

    private void addListeners() {
	if (isExitPoint) {
	    if (exitPointMappingChangeListener != null) {
		exitPointMappingTf.getDocument().removeDocumentListener(exitPointMappingChangeListener);
		exitPointMappingTf.getDocument().addDocumentListener(exitPointMappingChangeListener);
	    }
	} else {
	    if (componentMappingChangeListener != null) {
		componentsCombo.removeActionListener(componentMappingChangeListener);
		componentsCombo.addActionListener(componentMappingChangeListener);
	    }
	}
	
//	if(mappingTypeChangeListener != null){
//	    mappingTypesCombo.removeActionListener(mappingTypeChangeListener);
//	    mappingTypesCombo.addActionListener(mappingTypeChangeListener);
//	}
    }

    public void setComponentMappingChangeListener(ActionListener componentMappingChangeListener) {
	this.componentMappingChangeListener = componentMappingChangeListener;
	addListeners();
    }

    public void setExitPointMappingChangeListener(DocumentListener exitPointMappingChangeListener) {
	this.exitPointMappingChangeListener = exitPointMappingChangeListener;
	addListeners();
    }

    public void setMappingTypeChangeListener(ActionListener mappingTypeChangeListener) {
        this.mappingTypeChangeListener = mappingTypeChangeListener;
//        mappingTypesCombo.addActionListener(mappingTypeChangeListener);
    }

    public String getCurrentMapping() {
        return currentMapping;
    }

    public boolean isExitPoint() {
        return isExitPoint;
    }



    public String getExitValue() {
        return exitValue;
    }



    /**
     * Listens for the mapping type change
     * 
     * @author adi
     * 
     */
    class MappingTypeChangedListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    JComboBox combo = (JComboBox) e.getSource();
	    String value = (String) combo.getSelectedItem();
	    currentMapping = null;
	    if (value.equals(exitPointText)) {
		isExitPoint = true;
	    } else if (value.equals(componentMappingText)) {
		isExitPoint = false;
	    }
	    init();
	    revalidate();
	    mappingTypeChangeListener.actionPerformed(e);
	}

    }
}
