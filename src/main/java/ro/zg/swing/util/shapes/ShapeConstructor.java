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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class ShapeConstructor {

    public static Shape equilateralTriangle(Point2D.Float tipPoint, float edgeLength, double rotation){
	return isoscelusTriangle(tipPoint, edgeLength, (float)Math.PI/3, rotation);
    }
    
    public static Shape isoscelusTriangle(Point2D tipPoint, float edgeLength, double tipAngle, double rotation){
//	AffineTransform at1 = AffineTransform.getTranslateInstance(0, edgeLength);
	AffineTransform at1 = AffineTransform.getTranslateInstance(-edgeLength,0);
	AffineTransform at2 = (AffineTransform)at1.clone();
	double halfAngle = tipAngle/2;
//	double rotationAnchorY = tipPoint.getY()-edgeLength;
	double rotationAnchorX = tipPoint.getX()+edgeLength;
	
//	at1.rotate(-halfAngle+rotation,tipPoint.getX(),rotationAnchorY);
//	at2.rotate(halfAngle+rotation,tipPoint.getX(),rotationAnchorY);
	
	at1.rotate(-halfAngle+rotation,rotationAnchorX,tipPoint.getY());
	at2.rotate(halfAngle+rotation,rotationAnchorX,tipPoint.getY());
	
	Point2D p2 = (Point2D)at1.transform(tipPoint, null);
	Point2D p3 = (Point2D)at2.transform(tipPoint, null);
	
	GeneralPath path = new GeneralPath();
	path.moveTo(tipPoint.getX(), tipPoint.getY());
	path.lineTo(p2.getX(), p2.getY());
	path.lineTo(p3.getX(), p3.getY());
	path.closePath();
	
	return path;
    }
    
    public static Shape simpleArrow(Point2D.Float tailPoint, Point2D.Float headPoint){
	GeneralPath arrow = new GeneralPath();
	Shape tail = new Line2D.Float(tailPoint,headPoint);
	arrow.append(tail, false);
	
	double angle = Math.atan2(headPoint.y-tailPoint.y, headPoint.x-tailPoint.x) + Math.PI/2;
	Shape head = equilateralTriangle(headPoint, 10f, angle);
	arrow.append(head, false);
	
	return arrow;
    }
}
