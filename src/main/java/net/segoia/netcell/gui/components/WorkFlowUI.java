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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class WorkFlowUI extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 3233734039752483598L;

    private Map<String, WorkFlowComponentUI> componentUis = new HashMap<String, WorkFlowComponentUI>();

    private int hGap = 10;
    private int vGap = 30;
    private int leftBorder = 20;
    private int rightBorder = 20;
    private int topBorder = 20;
    private int bottomBorder = 20;

    public WorkFlowUI() {
	System.out.println("ui enabled : " + isEnabled());
	init();
    }

    private void init() {
	setLayout(new FlowLayout());

    }

    public void setTitle(String title) {
	setBorder(new TitledBorder(title));
    }

    public Dimension getPreferredSize() {
	int top = 0;
	int left = 0;
	int bottom = 0;
	int right = 0;
	for (Component c : getComponents()) {
	    Point pos = c.getLocation();
	    Dimension size = c.getPreferredSize();
	    if (pos.x < left) {
		left = pos.x;
	    }
	    int r = (pos.x + size.width);
	    if (r > right) {
		right = r;
	    }
	    if (pos.y < top) {
		top = pos.y;
	    }
	    int b = (pos.y + size.height);
	    if (b > bottom) {
		bottom = b;
	    }
	}
	Dimension d = new Dimension(right - left + leftBorder + rightBorder, bottom - top + topBorder + bottomBorder);
	return d;
    }

    public void shiftComponentsToZero() {
	int top = 0;
	int left = 0;
	int bottom = bottomBorder;
	int right = rightBorder;
	int diff = 0;
	for (Component c : getComponents()) {
	    if(!(c instanceof GraphFigure)) {
		continue;
	    }
	    Point pos = c.getLocation();
	    Dimension size = c.getPreferredSize();
	    diff = pos.x - leftBorder;
	    if (diff < 0 && diff < left) {
		left = diff;
	    }
	    int r = (pos.x + size.width);
	    if (r > right) {
		right = r;
	    }
	    diff = pos.y - topBorder;
	    if (diff < 0 && diff < top) {
		top = diff;
	    }
	    int b = (pos.y + size.height);
	    if (b > bottom) {
		bottom = b;
	    }
	}
	for (Component c : getComponents()) {
	    Point pos = c.getLocation();
	    if (left < 0) {
		pos.x -= left;
	    }
	    if (top < 0) {
		pos.y -= top;
	    }
	    c.setLocation(pos);
	}

    }

}
