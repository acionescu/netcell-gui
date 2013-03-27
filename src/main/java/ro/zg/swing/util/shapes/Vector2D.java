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
package ro.zg.swing.util.shapes;

import java.awt.geom.Point2D;

public class Vector2D {
    private Point2D tail;
    private Point2D head;
    private double rotation;
    private double magnitude;
    private Vector2D normal;
    private double dx;
    private double dy;
    
    public Vector2D(Point2D tail, Point2D head){
	this.tail = tail;
	this.head = head;
	computeDirectionAndMagnitude();
    }
    
    public Vector2D(double dx, double dy) {
	this.dx=dx;
	this.dy=dy;
	head = new Point2D.Double(dx,dy);
	tail = new Point2D.Double(0,0);
	
	rotation = Math.atan2(dy, dx);
//	magnitude = Math.abs(Math.sin(rotation)/dx);
	magnitude = Math.sqrt(dx*dx+dy*dy);
    }
    
    private void computeDirectionAndMagnitude(){
	dy = head.getY() - tail.getY();
	dx = head.getX() - tail.getX();
	rotation = Math.atan2(dy, dx);
//	magnitude = Math.abs(Math.sin(rotation)/dx);
	magnitude = Math.sqrt(dx*dx+dy*dy);
    }

    public Point2D getTail() {
        return tail;
    }

    public Point2D getHead() {
        return head;
    }
    

    /**
     * @return the dx
     */
    public double getDx() {
        return dx;
    }

    /**
     * @return the dy
     */
    public double getDy() {
        return dy;
    }

    public double getRotation() {
        return rotation;
    }

    public double getMagnitude() {
        return magnitude;
    }
    
    public Vector2D getNormal() {
	if(normal==null) {
	    normal=new Vector2D(-dy/magnitude,dx/magnitude);
	}
	
	return normal;
    }
    
    public Vector2D multiply(double scalar) {
	return new Vector2D(dx*scalar,dy*scalar);
    }
}
