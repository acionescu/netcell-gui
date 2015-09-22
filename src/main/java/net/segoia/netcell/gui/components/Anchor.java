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

import java.awt.geom.Point2D;

public class Anchor {
    private Point2D location;
    private AnchorEdge parent;
    private Point2D root;
    
    public Anchor(AnchorEdge parent, Point2D l,Point2D root){
	location = l;
	this.parent = parent;
	this.root=root;
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
    
    public Point2D getNormalPoint(double dist) {
	return parent.getNormalPoint(dist);
    }

    /**
     * @return the root
     */
    public Point2D getRoot() {
        return root;
    }
    
    
}
