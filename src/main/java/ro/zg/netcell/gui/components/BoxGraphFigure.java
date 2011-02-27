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

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class BoxGraphFigure extends GraphFigure implements ComponentListener{
    public static final String TOP="TOP";
    public static final String BOTTOM="BOTTOM";
    public static final String LEFT="LEFT";
    public static final String RIGHT="RIGHT";
    

    /**
     * 
     */
    private static final long serialVersionUID = -7073937153065176805L;
    protected BoxGraphFigureConstraints constraints = new BoxGraphFigureConstraints();
    private int boxWrapperSize = 1;
    private Rectangle enlargedBounds;
    public BoxGraphFigure(){
//	updateAnchors();
	this.addComponentListener(this);
    }
    
    
    public void componentHidden(ComponentEvent e) {
	// TODO Auto-generated method stub
	
    }
    public void componentMoved(ComponentEvent e) {
	updateAnchors();
    }
    public void componentResized(ComponentEvent e) {
	updateAnchors();
    }
    
    
    private Rectangle enlargeBounds(Rectangle bounds) {
	bounds.x-= boxWrapperSize;
	bounds.y-= boxWrapperSize;
	bounds.width += 2*boxWrapperSize;
	bounds.height += 2*boxWrapperSize;
	return bounds;
    }
    protected void updateAnchors(){
	Rectangle bounds = enlargeBounds(getBounds());
	enlargedBounds = bounds;
	Point2D topleft = new Point2D.Double(bounds.getMinX(),bounds.getMinY());
	Point2D topright = new Point2D.Double(bounds.getMaxX(),bounds.getMinY());
	Point2D bottomleft = new Point2D.Double(bounds.getMinX(),bounds.getMaxY());
	Point2D bottomright = new Point2D.Double(bounds.getMaxX(),bounds.getMaxY());
	getAnchorEdges().clear();
	if(constraints.topEdgeAnchorable){
	    addAnchorEdge(new AnchorEdge(this,new Line2D.Double(topleft,topright),TOP));
	}
	if(constraints.bottomEdgeAnchorable){
	    addAnchorEdge(new AnchorEdge(this,new Line2D.Double(bottomleft,bottomright),BOTTOM));
	}
	if(constraints.leftEdgeAnchorable){
	    addAnchorEdge(new AnchorEdge(this,new Line2D.Double(topleft,bottomleft),LEFT));
	}
	if(constraints.rightEdgeAnchorable){
	    addAnchorEdge(new AnchorEdge(this,new Line2D.Double(topright,bottomright),RIGHT));
	}
    }
    public void componentShown(ComponentEvent e) {
	updateAnchors();
    }


    public BoxGraphFigureConstraints getConstraints() {
        return constraints;
    }


    public void setConstraints(BoxGraphFigureConstraints constraints) {
        this.constraints = constraints;
    }


    public Rectangle getEnlargedBounds() {
        return enlargedBounds;
    }
    
}
