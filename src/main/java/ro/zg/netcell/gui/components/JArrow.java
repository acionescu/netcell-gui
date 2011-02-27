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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import ro.zg.swing.util.shapes.Arrow;
import ro.zg.swing.util.shapes.ShapeConstructor;

public class JArrow extends JComponent{

    /**
     * 
     */
    private static final long serialVersionUID = -3525439215589850709L;

    protected Arrow arrow;
    
    public JArrow(){
	
    }
    
    public JArrow(Point2D tail,Point2D head){
	arrow = new Arrow(tail,head);
    }
    
    protected void paintComponent(Graphics g){
	paintArrow(g);
    }
    
    protected void paintArrow(Graphics g){
	Graphics2D g2 = (Graphics2D)g;
	g2.draw(new Line2D.Float(arrow.getTailPoint(),arrow.getHeadPoint()));
	g2.fill(ShapeConstructor.isoscelusTriangle(arrow.getHeadPoint(), arrow.getHeadEdgeLength(), arrow.getHeadAngle(), arrow.getRotation()));
    }
    
}
