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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.segoia.netcell.vo.configurations.ComponentExitPoint;
import net.segoia.netcell.vo.configurations.ComponentExitPointsMapping;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;

public class WorkFlowComponentUI extends BoxGraphFigure {

    /**
     * 
     */
    private static final long serialVersionUID = -3666283904301075547L;

    private WorkFlowComponentConfiguration compConfig;

    private JLabel localIdLabel = new JLabel();
    private JLabel compIdLabel = new JLabel();
    private LinkedHashMap<Object, JLabel> exitPointLabels = new LinkedHashMap<Object, JLabel>();

    public WorkFlowComponentUI(WorkFlowComponentConfiguration config) {
	super();
	compConfig = config;
	syncWithData();
	setConstraints();
	
    }

    private void setConstraints() {
	BoxGraphFigureConstraints c = new BoxGraphFigureConstraints();
	c.bottomEdgeAnchorable = false;
	// c.leftEdgeAnchorable = false;
	// c.rightEdgeAnchorable = false;
	setConstraints(c);
    }

    public void refresh() {
	init();
    }

    private void syncWithData() {
	setId(compConfig.getLocalId());
	localIdLabel.setText(compConfig.getLocalId());
	compIdLabel.setText(compConfig.getComponentConfig().getComponent());
	init();
    }

    private void init() {
	removeAll();
	GridBagConstraints layoutContraints = new GridBagConstraints();
	setLayout(new GridBagLayout());
	setBorder(BorderFactory.createLineBorder(Color.black));
	setBackground(new Color(150, 200, 150));

	layoutContraints.anchor = GridBagConstraints.CENTER;
	layoutContraints.fill = GridBagConstraints.VERTICAL;
	layoutContraints.gridwidth = GridBagConstraints.REMAINDER;
	layoutContraints.insets = new Insets(0, 10, 0, 10);
	add(localIdLabel, layoutContraints);

	JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
	layoutContraints.anchor = GridBagConstraints.WEST;
	layoutContraints.weightx = 1.0;
	layoutContraints.fill = GridBagConstraints.HORIZONTAL;
	layoutContraints.insets = new Insets(0, 0, 0, 0);
	sep1.setBackground(Color.black);
	add(sep1, layoutContraints);

	layoutContraints.insets = new Insets(0, 10, 0, 10);
	layoutContraints.anchor = GridBagConstraints.CENTER;
	layoutContraints.fill = GridBagConstraints.VERTICAL;
	add(compIdLabel, layoutContraints);
	
	Dimension localIdSize = localIdLabel.getPreferredSize();
	Dimension compIdSize = compIdLabel.getPreferredSize();
	
	int maxWidth = Math.max(localIdSize.width,compIdSize.width);
	int maxHeight = localIdSize.height+compIdSize.height+sep1.getPreferredSize().height;

	setSize(getPreferredSize());
	ComponentExitPointsMapping exitPoints = compConfig.getComponentMapping();
//	if(exitPoints == null){
//	    return;
//	}
//	if (exitPoints.getNextComponentId() == null && exitPoints.getExitPointPrmName() != null) {
	String mappingType = exitPoints.getMappingType();
	Map<Object, ComponentExitPoint> exitPointsMappings = null;
	if(mappingType.equals(ComponentExitPointsMapping.PARAMETER_MAPPING)) {
	    exitPointsMappings = exitPoints.getExitValuesMappings();
	}
	else if(mappingType.equals(ComponentExitPointsMapping.FIXED_MAPPINGS)) {
	    exitPointsMappings = exitPoints.getFixedMappings();
	}
	
	if( exitPointsMappings != null ) {
	    JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
	    layoutContraints.anchor = GridBagConstraints.WEST;
	    layoutContraints.fill = GridBagConstraints.HORIZONTAL;
	    layoutContraints.insets = new Insets(0, 0, 0, 0);
	    sep2.setBackground(Color.black);
	    add(sep2, layoutContraints);

	    layoutContraints.gridwidth = GridBagConstraints.RELATIVE;
	    // layoutContraints.gridheight = GridBagConstraints.RELATIVE;
	    layoutContraints.anchor = GridBagConstraints.WEST;
	    layoutContraints.fill = GridBagConstraints.HORIZONTAL;
	    layoutContraints = new GridBagConstraints();
	    // layoutContraints.insets = new Insets(0, 0, 0, 0);
	    
	    
	    int epMaxWidth =0;
	    
	    int current = 1;
	    int size = exitPointsMappings.size();
	    for (Map.Entry<Object, ComponentExitPoint> entry : exitPointsMappings.entrySet()) {
		JLabel exitPointLabel = null;
		layoutContraints.fill = GridBagConstraints.HORIZONTAL;
		layoutContraints.insets = new Insets(0, 5, 0, 5);
		String exitPointId = (String) entry.getKey();
		exitPointLabel = new JLabel(exitPointId);
		add(exitPointLabel, layoutContraints);
		exitPointLabels.put(exitPointId, exitPointLabel);
		/* add separator */
		if (current++ < size) {
		    layoutContraints.fill = GridBagConstraints.VERTICAL;
		    layoutContraints.insets = new Insets(0, 0, 0, 0);
		    JSeparator vSep = new JSeparator(SwingConstants.VERTICAL);
		    vSep.setBackground(Color.black);
		    add(vSep, layoutContraints);
		}
	    }
	}
	setSize(getPreferredSize());
    }

    public WorkFlowComponentConfiguration getCompConfig() {
	return compConfig;
    }

    public JLabel getExitPointLabel(Object exitPoint) {
	return exitPointLabels.get(exitPoint);
    }

    public void setCompConfig(WorkFlowComponentConfiguration compConfig) {
	this.compConfig = compConfig;
	syncWithData();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((compConfig == null) ? 0 : compConfig.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	WorkFlowComponentUI other = (WorkFlowComponentUI) obj;
	if (compConfig == null) {
	    if (other.compConfig != null)
		return false;
	} else if (!compConfig.equals(other.compConfig))
	    return false;
	return true;
    }

}
