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
package net.segoia.netcell.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class SplashScreen extends JWindow {
    /**
     * 
     */
    private static final long serialVersionUID = 8994468000724543826L;

    public SplashScreen() {
    }

    // A simple little method to show a title screen in the center
    // of the screen for the amount of time given in the constructor
    public void showSplash() {
	JPanel content = (JPanel) getContentPane();
	content.setBackground(Color.white);

	// Set the window's bounds, centering the window
	int width = 450;
	int height = 115;
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int x = (screen.width - width) / 2;
	int y = (screen.height - height) / 2;
	setBounds(x, y, width, height);

	// Build the splash screen
	JLabel label = new JLabel("Loading...", JLabel.CENTER);
	content.add(label, BorderLayout.CENTER);
	Color oraRed = new Color(0, 0, 150, 255);
	content.setBorder(BorderFactory.createLineBorder(oraRed, 10));

	// Display it
	setVisible(true);
    }
    
    public void hideSplash() {
	setVisible(false);
    }
}
