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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import ro.zg.netcell.vo.InputParameter;

public class InputParameterControls extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6253982266550086510L;

    public static String EMPTY = "Empty";
    public static String STATIC = "Static";
    public static String DYNAMIC = "Dynamic";
    public static String DEFAULT = "Default";

    private InputParameter inputParam;
    private String mappingType;
    private Object value;
    private List<String> availableContextParams;

    private JTextField staticValueTf;
    private JComboBox dynamicValuesCombo;
    private JComboBox mappingTypesCombo;

    private DocumentListener staticValueChangedListener;
    private ActionListener dynamicValueChangedListener;

    private Font controlsFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

    public InputParameterControls(InputParameter inputParam, String mappingType, Object value,
	    List<String> availableContextParams) {
	this.inputParam = inputParam;
	this.mappingType = mappingType;
	this.value = value;
	this.availableContextParams = availableContextParams;

	init();
    }

    private void init() {
	removeAll();
	staticValueTf = null;
	dynamicValuesCombo = null;
	GridBagLayout layout = new GridBagLayout();
	setLayout(layout);
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(0, 2, 0, 0);
	constraints.anchor = GridBagConstraints.WEST;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.gridwidth = 3;
	constraints.gridheight = 1;
	constraints.weightx = 1.0;
	String paramName = inputParam.getName();
	String paramType = inputParam.getType();

	JLabel paramNameLabel = createLabel();
	paramNameLabel.setText(paramName + ":");
	add(paramNameLabel, constraints);
	constraints.weightx = 0.0;
	if (mappingType.equals(STATIC)) {
	    staticValueTf = createStaticValueTextField();
	    /* add change listeners */
	    addListeners();
	    if (value != null) {
		staticValueTf.setText(value.toString());
	    }
	    else{
		staticValueTf.setText("");
	    }
	    add(staticValueTf, constraints);
	} else if (mappingType.equals(DYNAMIC)) {
	    dynamicValuesCombo = createDynamicParamsCombo(availableContextParams.toArray(new String[0]));
	    /* add change listeners */
	    addListeners();
	    if (value != null) {
		dynamicValuesCombo.setSelectedItem(value);
	    } else {
		dynamicValuesCombo.setSelectedIndex(0);
	    }
	    add(dynamicValuesCombo, constraints);
	} else if (mappingType.equals(DEFAULT)) {
	    JLabel defaultValueLabel = new JLabel(inputParam.getValue().toString());
	    add(defaultValueLabel, constraints);
	} else if (mappingType.equals(EMPTY)) {
	    JLabel defaultValueLabel = new JLabel("undefined");
	    add(defaultValueLabel, constraints);
	}
	
	mappingTypesCombo = createTypesCombo(inputParam.isMandatory(), availableContextParams.size() > 0, inputParam
		.getValue() != null);
	add(mappingTypesCombo, constraints);

    }

    private JComboBox createDynamicParamsCombo(String[] values) {
	JComboBox combo = new JComboBox(values);
	FontMetrics fm = combo.getFontMetrics(combo.getFont());
	combo.setPreferredSize(new Dimension(150, fm.getHeight()));
	combo.setMinimumSize(new Dimension(75, fm.getHeight()));
	return combo;
    }

    private JComboBox createTypesCombo(boolean isMandatory, boolean hasContextParams, boolean hasDefaultValue) {
	List<String> types = new ArrayList<String>();
//	if (!isMandatory) {
	    types.add(EMPTY);
//	}
	types.add(STATIC);
	if (hasContextParams) {
	    types.add(DYNAMIC);
	}
	if (hasDefaultValue) {
	    types.add(DEFAULT);
	}

	JComboBox combo = new JComboBox(types.toArray(new String[0]));
	combo.setSelectedItem(mappingType);
	FontMetrics fm = combo.getFontMetrics(combo.getFont());
	combo.setPreferredSize(new Dimension(100, fm.getHeight()));

	combo.addActionListener(new TypeComboBoxListener());
	return combo;
    }

    private JTextField createStaticValueTextField() {
	JTextField tf = new JTextField();
	FontMetrics fm = tf.getFontMetrics(tf.getFont());
	tf.setPreferredSize(new Dimension(150, fm.getHeight()));
	return tf;
    }

    private JLabel createLabel() {
	JLabel l = new JLabel();
	FontMetrics fm = l.getFontMetrics(l.getFont());
	l.setMaximumSize(new Dimension(150, fm.getHeight()));
	return l;
    }

    private void addListeners() {
	if (staticValueTf != null && staticValueChangedListener != null) {
	    staticValueTf.getDocument().removeDocumentListener(staticValueChangedListener);
	    staticValueTf.getDocument().addDocumentListener(staticValueChangedListener);
	} 
	if (dynamicValuesCombo != null && dynamicValueChangedListener != null) {
	    dynamicValuesCombo.removeActionListener(dynamicValueChangedListener);
	    dynamicValuesCombo.addActionListener(dynamicValueChangedListener);
	}
    }

    public DocumentListener getStaticValueChangedListener() {
	return staticValueChangedListener;
    }

    public ActionListener getDynamicValueChangedListener() {
	return dynamicValueChangedListener;
    }

    public void setStaticValueChangedListener(DocumentListener staticValueChangedListener) {
	this.staticValueChangedListener = staticValueChangedListener;
	addListeners();
    }

    public void setDynamicValueChangedListener(ActionListener dynamicValueChangedListener) {
	this.dynamicValueChangedListener = dynamicValueChangedListener;
	addListeners();
    }

    /**
     * Listener for the types combo box
     * 
     * @author adi
     * 
     */
    class TypeComboBoxListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    mappingType = (String) mappingTypesCombo.getSelectedItem();
	    value = null;
	    init();
	    revalidate();
	}

    }
}
