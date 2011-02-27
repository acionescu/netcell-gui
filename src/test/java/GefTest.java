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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ro.zg.math.GraphUtil;
import ro.zg.netcell.gui.components.ExitPointMappingUI;
import ro.zg.netcell.gui.components.JArrow;
import ro.zg.netcell.gui.components.JBrokenArrow;
import ro.zg.netcell.toolkit.EventProcessor;
import ro.zg.netcell.toolkit.GenericEventListener;
import ro.zg.netcell.toolkit.listeners.SwingEventsListener;


public class GefTest {
    public static void main(String[] args){
		EventProcessor evp = new EventProcessor();
		SwingEventsListener evl = new SwingEventsListener("customframe",evp);
	      JFrame f = new JFrame("JScrollPane");
	      f.addComponentListener(evl);
	      f.setLayout(null);
	      JArrow ar = new JArrow(new Point(0,0),new Point(200,100));
	      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      f.setSize(500, 400);
	      f.setVisible(true);
	      f.add(ar);
	      ar.setSize(500,400);
	      ar.setLocation(0,0);
	      
	      
	      
	      ExitPointMappingUI eui = new ExitPointMappingUI("dezastrul economico-social a lovit in plina figura populatia, cand aceasta statea cu spatele.","gd");
	      
		eui.setLocation(300, 200);
		f.add(eui);
//		eui.setSize(200,200);
		
		List<Point2D> path = new ArrayList();
		System.out.println(eui.getAnchorEdges());
		GraphUtil.getShortestPath(new Point(335,300), eui, new ArrayList(), 20, path);
		JArrow a2 = new JBrokenArrow(path);
		
		a2.setLocation(0, 0);
		a2.setSize(800, 600);
		f.add(a2);
		f.repaint();      
	   
    }
}
