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

public class WorkFlowNodeUiInfo {
    /* the component associated with this node */
    private GraphFigure wfCompUi;
    /* the dimension of the subflow under this component */
    private Dimension subFlowDimension;
    /* dimension of the current node */
    private Dimension nodeDimension;
    /* the relative x ratio of this node from it's left subtree */
    private double relativeXRatio;
    
    public GraphFigure getWfCompUi() {
        return wfCompUi;
    }
    public Dimension getSubFlowDimension() {
        return subFlowDimension;
    }
    public void setWfCompUi(GraphFigure wfCompUi) {
        this.wfCompUi = wfCompUi;
    }
    public void setSubFlowDimension(Dimension subFlowDimension) {
        this.subFlowDimension = subFlowDimension;
    }
    public Dimension getNodeDimension() {
        return nodeDimension;
    }
    public void setNodeDimension(Dimension nodeDimension) {
        this.nodeDimension = nodeDimension;
    }
    /**
     * @return the relativeXRatio
     */
    public double getRelativeXRatio() {
        return relativeXRatio;
    }
    /**
     * @param relativeXRatio the relativeXRatio to set
     */
    public void setRelativeXRatio(double relativeXRatio) {
        this.relativeXRatio = relativeXRatio;
    }
    
}
