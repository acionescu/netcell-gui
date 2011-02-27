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
package ro.zg.netcell.toolkit.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.event.MouseInputListener;

import ro.zg.netcell.toolkit.EventProcessingReport;
import ro.zg.netcell.toolkit.GenericEventListener;
import ro.zg.util.execution.ExecutionEntity;

public class SwingEventsListener extends GenericEventListener implements MouseInputListener, ComponentListener{

    public SwingEventsListener(String type, ExecutionEntity<EventObject, EventProcessingReport> processor) {
	super(type, processor);
	// TODO Auto-generated constructor stub
    }
    

    public void actionPerformed(ActionEvent e) {
	onEvent(e);
    }

    public void mouseClicked(MouseEvent e) {
	onEvent(e);
    }

    public void mouseEntered(MouseEvent e) {
	onEvent(e);
    }

    public void mouseExited(MouseEvent e) {
	onEvent(e);
    }

    public void mousePressed(MouseEvent e) {
	onEvent(e);
    }

    public void mouseReleased(MouseEvent e) {
	onEvent(e);
    }

    public void mouseDragged(MouseEvent e) {
	onEvent(e);
    }

    public void mouseMoved(MouseEvent e) {
	onEvent(e);
    }

    public void componentHidden(ComponentEvent e) {
	onEvent(e);
    }

    public void componentMoved(ComponentEvent e) {
	onEvent(e);
    }

    public void componentResized(ComponentEvent e) {
	onEvent(e);
    }

    public void componentShown(ComponentEvent e) {
	onEvent(e);
    }

}
