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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import net.segoia.netcell.vo.DefinitionsRepository;
import net.segoia.netcell.vo.definitions.EntityDefinition;
import net.segoia.netcell.vo.definitions.ExecutableEntityDefinition;

public class EntitiesTree extends JTree {

    /**
     * 
     */
    private static final long serialVersionUID = 2833461639228099487L;
    
    private static final String PACKAGE_SEPARATOR = "\\.";

    private Map<String, DefaultMutableTreeNode> rootNodesForTypes = new HashMap<String, DefaultMutableTreeNode>();

    private DefaultMutableTreeNode rootNode;
    
    public EntitiesTree(MutableTreeNode node) {
	super(node);
    }

    public EntitiesTree() {
	super();
    }

    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {
	if (value != null) {
	    if (value instanceof ExecutableEntityDefinition) {
		ExecutableEntityDefinition ed = (ExecutableEntityDefinition) value;
		return ed.getId();
	    } else {
		return value.toString();
	    }
	}
	return "";
    }

    public void populateFromDefinitionsRepository(DefinitionsRepository definitionsRepository) {
	Map<String, DefaultMutableTreeNode> packageNodes = new HashMap<String, DefaultMutableTreeNode>();
	rootNode = new DefaultMutableTreeNode("Entities");
	DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
	setModel(treeModel);

	for (Map.Entry<String, Collection<EntityDefinition>> entry : definitionsRepository.getDefinitionsByType().entrySet()) {
	    String entityType = entry.getKey();
	    System.out.println("add to tree entitytype "+entityType);
	    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(entityType);
	    treeModel.insertNodeInto(currentNode, rootNode, rootNode.getChildCount());
	    rootNodesForTypes.put(entityType, currentNode);
	    for (EntityDefinition entityDef : entry.getValue()) {
		String entityId = entityDef.getId();
		int nameStartIndex = entityId.lastIndexOf(".");
		if (nameStartIndex < 0) {
		    DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(entityId);
		    treeModel.insertNodeInto(entityNode, currentNode, currentNode.getChildCount());
		} else {
		    String localPackageName = entityId.substring(0, nameStartIndex);
		    String packageName = entityType+"."+localPackageName;
		    DefaultMutableTreeNode pkgNode = packageNodes.get(packageName);
		    if (pkgNode == null) {
			pkgNode = new DefaultMutableTreeNode(localPackageName);
			treeModel.insertNodeInto(pkgNode, currentNode, currentNode.getChildCount());
			packageNodes.put(packageName, pkgNode);
		    }
		    DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(entityId
			    .substring(nameStartIndex + 1));
		    treeModel.insertNodeInto(entityNode, pkgNode, pkgNode.getChildCount());
		}
	    }
	}
	treeModel.reload();
    }

    private DefaultMutableTreeNode getRootNodeForType(String type) {
	DefaultMutableTreeNode rootNodeForType = rootNodesForTypes.get(type);
	if(rootNodeForType == null) {
	    rootNodeForType =  getNodeForPackageName(rootNode, type);
	    rootNodesForTypes.put(type, rootNodeForType);
	}
	return rootNodeForType;
    }
    
    public void addNode(String nodeType,String name){
	DefaultMutableTreeNode rootNodeForType = getRootNodeForType(nodeType);
	/* get package name */
	int nameStartIndex = name.lastIndexOf(".");
	DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
	DefaultMutableTreeNode entityNode = null;
	if(nameStartIndex < 0){
	    entityNode = new DefaultMutableTreeNode(name);
	    treeModel.insertNodeInto(entityNode, rootNodeForType, rootNodeForType.getChildCount());
	}
	else{
	    String packageName = name.substring(0,nameStartIndex);
	    String nodeName = name.substring(nameStartIndex+1);
	    DefaultMutableTreeNode pkgNode = getNodeForPackageName(rootNodeForType, packageName);
	    entityNode = new DefaultMutableTreeNode(nodeName);
	    treeModel.insertNodeInto(entityNode, pkgNode, pkgNode.getChildCount());
	}
	treeModel.reload();
	setSelectionPath(new TreePath(entityNode.getPath()));
    }
    /* Not used anymore since the whole package name is used as a node */
//    private DefaultMutableTreeNode getNodeForPackageName(DefaultMutableTreeNode rootNode, String packageName){
//	String[] path = packageName.split(PACKAGE_SEPARATOR);
//	List<String> l = new ArrayList(Arrays.asList(path));
//	return getNodeForPackageName(rootNode, l);
//    }
    
    private DefaultMutableTreeNode getNodeForPackageName(DefaultMutableTreeNode rootNode, List<String> path){
	if(path.size() == 0){
	    return rootNode;
	}
	String name = path.remove(0);
	for(int i=0; i<rootNode.getChildCount();i++){
	    DefaultMutableTreeNode cn = (DefaultMutableTreeNode)rootNode.getChildAt(i);
	    if(name.equals(cn.toString())){
		return getNodeForPackageName(cn, path);
	    }
	}
	path.add(0, name);
	return createHierarchy(rootNode, path);
    }
    
    private DefaultMutableTreeNode getNodeForPackageName(DefaultMutableTreeNode rootNode, String packageName){
	for(int i=0; i<rootNode.getChildCount();i++){
	    DefaultMutableTreeNode cn = (DefaultMutableTreeNode)rootNode.getChildAt(i);
	    if(packageName.equals(cn.toString())){
		return cn;
	    }
	}
	List<String> l = new ArrayList<String>();
	l.add(packageName);
	return createHierarchy(rootNode, l);
    }
    
    private DefaultMutableTreeNode createHierarchy(DefaultMutableTreeNode parent,List<String> path){
	if(path.size() == 0){
	    return parent;
	}
	String name = path.remove(0);
	DefaultTreeModel model = (DefaultTreeModel)getModel();
	DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
	model.insertNodeInto(node, parent, parent.getChildCount());
	return createHierarchy(node, path);
    }
    
    public boolean removeNode(String name) {
	DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
	return false;
    }
}
