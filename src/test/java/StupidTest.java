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
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

public class StupidTest {
    public static void main(String[] args) {
	JFrame f = new JFrame("JScrollPane");

	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f.setSize(500, 400);
	f.setVisible(true);
	f.setLayout(new BorderLayout());
	JToolBar tb = new JToolBar();
	tb.add(new JButton("bla bla bla"));
	f.add(tb,BorderLayout.NORTH);
	f.validate();
    }
}
