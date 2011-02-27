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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import ro.zg.swing.util.shapes.Arrow;

public class JBrokenArrow extends JArrow {

    /**
     * 
     */
    private static final long serialVersionUID = 3356557612086486908L;

    private List<Point2D> points;
    private Color color = Color.black;

    public JBrokenArrow(List<Point2D> points) {
	this.points = points;
	init();
    }
    
    public JBrokenArrow(List<Point2D> points, Color c) {
	this.points = points;
	this.color = c;
	init();
    }

    private void init() {
	Point2D headPoint = points.remove(points.size() - 1);
	Point2D arrowTailPoint = points.get(points.size() - 1);
	arrow = new Arrow(arrowTailPoint, headPoint);
    }

    protected void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	Point2D prevPoint = points.get(0);
	g2.setColor(color);
	for (int i = 1; i < points.size(); i++) {
	    Point2D p = points.get(i);
	    g2.draw(new Line2D.Double(prevPoint, p));
	    prevPoint = p;
	}
	paintArrow(g2);
    }

}
