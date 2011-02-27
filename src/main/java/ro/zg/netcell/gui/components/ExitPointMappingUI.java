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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

public class ExitPointMappingUI extends OvalGraphFigure {

    /**
     * 
     */
    private static final long serialVersionUID = 1757980263869441197L;

    private String mappingName;
    private Rectangle2D bounds;
    
    public ExitPointMappingUI(String name, String id) {
	mappingName = name;
	setId(id);
	init();
    }
    
    private void init(){
	FontMetrics fm = getFontMetrics(getFont());
	bounds = fm.getStringBounds(mappingName, getGraphics());
	setSize(getPreferredSize());
    }
    
    public Dimension getPreferredSize(){
	return new Dimension((int)bounds.getWidth()+21,(int)bounds.getHeight()+21);
    }

    public void setMappingName(String mappingName) {
        this.mappingName = mappingName;
        init();
    }

    protected void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;

	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	g.setColor(new Color(200, 100, 100));
	int x = (int) bounds.getMinX();
	int y = (int) bounds.getMinY();
	int width = (int) bounds.getWidth();
	int height = (int) bounds.getHeight();

	int ox = 0;
	int oy = 0;
	int owidth = width + 20;
	int oheight = height + 20;

	g2.fillOval(ox, oy, owidth, oheight);
	g2.setColor(Color.black);
	g2.drawOval(ox, oy, owidth, oheight);

	g2.drawString(mappingName, 10, 20);
    }
}
