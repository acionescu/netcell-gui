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

import java.awt.geom.Point2D;

public class Anchor {
    private Point2D location;
    private AnchorEdge parent;
    
    public Anchor(AnchorEdge parent, Point2D l){
	location = l;
	this.parent = parent;
    }
    
    public String toString(){
	return location.toString();
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }
    
    public Point2D getNormalPoint(Point2D source){
	return parent.getNormalPoint(source, location);
    }
}
