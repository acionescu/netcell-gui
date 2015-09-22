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
package net.segoia.swing.util.shapes;

import java.awt.geom.Point2D;

public class Arrow {
    private Vector2D arrow;
    private double headAngle = Math.PI/3;
    private float headEdgeLength = 10f;
    
    public Arrow(Point2D tail, Point2D head){
	arrow = new Vector2D(tail,head);
    }

    public double getHeadAngle() {
        return headAngle;
    }

    public float getHeadEdgeLength() {
        return headEdgeLength;
    }

    public void setHeadAngle(double headAngle) {
        this.headAngle = headAngle;
    }

    public void setHeadEdgeLength(float headEdgeLength) {
        this.headEdgeLength = headEdgeLength;
    }
    
    public Point2D getTailPoint(){
	return arrow.getTail();
    }
    
    public Point2D getHeadPoint(){
	return arrow.getHead();
    }
    
    public double getRotation(){
	return arrow.getRotation();
    }
}
