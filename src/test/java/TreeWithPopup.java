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
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class TreeWithPopup extends JPanel {
    
    DefaultMutableTreeNode root, node1, node2, node3;
    
    public TreeWithPopup() {
        MyJTree tree;
        root = new DefaultMutableTreeNode("root", true);
        node1 = new DefaultMutableTreeNode("node 1", true);
        node2 = new DefaultMutableTreeNode("node 2" , true);
        node3 = new DefaultMutableTreeNode("node 3", true);
        root.add(node1);
        node1.add(node2);
        root.add(node3);
        setLayout(new BorderLayout());
        tree = new MyJTree(root);
        add(new JScrollPane((JTree)tree),"Center");
    }
    
    public Dimension getPreferredSize(){
        return new Dimension(300, 300);
    }
    
    public static void main(String s[]){
        JFrame frame = new JFrame("Tree With Popup");
        TreeWithPopup panel = new TreeWithPopup();
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setForeground(Color.black);
        frame.setBackground(Color.lightGray);
        frame.getContentPane().add(panel,"Center");
        
        frame.setSize(panel.getPreferredSize());
        frame.setVisible(true);
        frame.addWindowListener(new WindowCloser());
    }
}

class WindowCloser extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
        Window win = e.getWindow();
        win.setVisible(false);
        System.exit(0);
    }
}

class MyJTree extends JTree implements ActionListener{
    JPopupMenu popup;
    JMenuItem mi;
    
    MyJTree(DefaultMutableTreeNode dmtn) {
        super(dmtn);
        // define the popup
        popup = new JPopupMenu();
        mi = new JMenuItem("Insert a children");
        mi.addActionListener(this);
        mi.setActionCommand("insert");
        popup.add(mi);
        mi = new JMenuItem("Remove this node");
        mi.addActionListener(this);
        mi.setActionCommand("remove");
        popup.add(mi);
        popup.setOpaque(true);
        popup.setLightWeightPopupEnabled(true);
        
        addMouseListener(
                new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
        	System.out.println(e);
                if ( e.isPopupTrigger()) {
                    popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                    System.out.println("should show");
                }
            }
            
            public void mousePressed( MouseEvent e ) {
        	System.out.println(e);
                if ( e.isPopupTrigger()) {
                    popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
                    System.out.println("should show");
                }
            }
        }
        );
        
    }
    public void actionPerformed(ActionEvent ae) {
        DefaultMutableTreeNode dmtn, node;
        
        TreePath path = this.getSelectionPath();
        dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (ae.getActionCommand().equals("insert")) {
            node = new DefaultMutableTreeNode("children");
            dmtn.add(node);
            // thanks to Yong Zhang for the tip for refreshing the tree structure.
            ((DefaultTreeModel )this.getModel()).nodeStructureChanged((TreeNode)dmtn);
        }
        if (ae.getActionCommand().equals("remove")) {
            node = (DefaultMutableTreeNode)dmtn.getParent();
            // Bug fix by essam
            int nodeIndex=node.getIndex(dmtn); // declare an integer to hold the selected nodes index
            dmtn.removeAllChildren();          // remove any children of selected node
            node.remove(nodeIndex);            // remove the selected node, retain its siblings
            ((DefaultTreeModel )this.getModel()).nodeStructureChanged((TreeNode)dmtn);       }
    }
}
