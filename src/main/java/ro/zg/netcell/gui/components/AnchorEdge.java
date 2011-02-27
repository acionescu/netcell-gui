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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class AnchorEdge {
    private String id;
    private Line2D edge;
    private GraphFigure parent;
    private List<Anchor> anchors = new ArrayList<Anchor>();
    
    public AnchorEdge(GraphFigure parent, Line2D edge,String id){
	this.edge = edge;
	this.parent = parent;
	this.id =id;
    }
    
    public String toString(){
	return "("+edge.getP1()+","+edge.getP2()+")";
    }
    
    public Anchor getNextPreferedAnchor(){
	float r = 1f/(anchors.size()+2);
	Point2D p1 = edge.getP1();
	Point2D p2 = edge.getP2();
	Point2D pos = new Point2D.Double((p1.getX()+p2.getX())*r,(p1.getY()+p2.getY())*r);
	return new Anchor(this,pos);
    }
    
    public Point2D getNormalPoint(Point2D source, Point2D dest){
	//TODO: this is bullshit, do it well, with vectors and stuff
	Point2D p1 = edge.getP1();
	Point2D p2 = edge.getP2();
	Rectangle shapeBounds = parent.getEnlargedBounds();
	if(p1.getX() == p2.getX()){
	    Point2D np = new Point2D.Double(source.getX(),dest.getY());
//	    if(!shapeBounds.contains(np)){
		return np;
//	    }
//	    else{
//		
//		/* left edge */
//		if(p1.getX() == shapeBounds.getMinX()){
//		    return new Point2D.Double(2*shapeBounds.getMinX()-np.getX(),np.getY());
//		}
//		/* right edge */
//		if(p1.getX() == shapeBounds.getMaxX()){
//		    return new Point2D.Double(2*shapeBounds.getMaxX()-np.getX(),np.getY());
//		}
//		throw new UnsupportedOperationException("this only works for boxes");
//	    }
	}
	else if(p2.getY() == p1.getY()){
	    Point2D np = new Point2D.Double(dest.getX(),source.getY());
//	    if(!shapeBounds.contains(np)){
		return np;
//	    }
//	    else{
		/* top */
//		if(p1.getY() == shapeBounds.getMinY()){
//		    return new Point2D.Double(np.getX(), 2*shapeBounds.getMinY() - np.getY());
//		}
//		/* bottom */
//		if(p1.getY() == shapeBounds.getMaxY()){
//		    return new Point2D.Double(np.getX(), 2*shapeBounds.getMaxY() - np.getY());
//		}
//		throw new UnsupportedOperationException("this only works for boxes");
//	    }
	}
	else{
	    throw new UnsupportedOperationException("this only works for boxes");
	}
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    
}
