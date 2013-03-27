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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public abstract class GraphFigure extends JPanel implements MouseMotionListener, MouseListener{
    /**
     * 
     */
    private static final long serialVersionUID = 2927651624942635151L;
    private Map<String,AnchorEdge> anchorEdges = new LinkedHashMap<String, AnchorEdge>();
    private String id;
    /* dragging */
    private Point pressedOffset;
    
    private int boxWrapperSize = 15;
    
    public GraphFigure(){
	this.addMouseMotionListener(this);
	this.addMouseListener(this);
    }
    
    public List<Anchor> getAvailableAnchors(){
	List<Anchor> anchors = new ArrayList<Anchor>();
	for(AnchorEdge edge : anchorEdges.values()){
	    anchors.add(edge.getNextPreferedAnchor());
	}
	return anchors;
    }
    
    protected Rectangle enlargeBounds(Rectangle bounds) {
	bounds.x-= boxWrapperSize;
	bounds.y-= boxWrapperSize;
	bounds.width += 2*boxWrapperSize;
	bounds.height += 2*boxWrapperSize;
	return bounds;
    }
    
    public Anchor getNextAvailableAnchor(String edgeId) {
	return anchorEdges.get(edgeId).getNextPreferedAnchor();
    }
    
    public void setLocation(Point p){
	super.setLocation(p);
	updateAnchors();
    }
    
    public void setLocation(int x, int y){
	super.setLocation(x, y);
	updateAnchors();
    }
    
    public void setSize(Dimension d){
	super.setSize(d);
	updateAnchors();
    }
    
    public void setSize(int w, int h){
	super.setSize(w,h);
	updateAnchors();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public void addAnchorEdge(AnchorEdge e){
	anchorEdges.put(e.getId(), e);
    }
    
    
    /**
     * @return the anchorEdges
     */
    public Map<String, AnchorEdge> getAnchorEdges() {
        return anchorEdges;
    }

    /**
     * @param anchorEdges the anchorEdges to set
     */
    public void setAnchorEdges(Map<String, AnchorEdge> anchorEdges) {
        this.anchorEdges = anchorEdges;
    }

    protected abstract void updateAnchors();
    
    protected abstract Rectangle getEnlargedBounds();

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent e) {
	pressedOffset = e.getPoint();
    }

    public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    public void mouseDragged(MouseEvent e) {
	Point newPos = e.getPoint();
	int dx = newPos.x - pressedOffset.x;
	int dy = newPos.y - pressedOffset.y;
	Point newLocation = getLocation();
	newLocation.translate(dx, dy);
	setLocation(newLocation);
	
	updateAnchors();
    }

    public void mouseMoved(MouseEvent e) {
	// TODO Auto-generated method stub

    }

    /**
     * @return the boxWrapperSize
     */
    public int getBoxWrapperSize() {
        return boxWrapperSize;
    }

}
