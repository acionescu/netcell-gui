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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;

import net.segoia.data.util.graphs.DefaultGraphEdgeFactory;
import net.segoia.data.util.graphs.DefaultGraphModelFactory;
import net.segoia.data.util.graphs.GraphEdge;
import net.segoia.data.util.graphs.GraphEdgeFactory;
import net.segoia.data.util.graphs.GraphModel;
import net.segoia.data.util.graphs.GraphModelFactory;
import net.segoia.math.GraphUtil;
import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.vo.configurations.ComponentExitPoint;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;
import net.segoia.netcell.vo.definitions.WorkFlowDefinition;
import net.segoia.util.data.GenericNameValue;

public class WorkflowController implements ComponentListener {
    private WorkFlowDefinition currentDefinition;
    private Map<String, WorkFlowComponentConfiguration> componentsConfigurations;
    private GraphEdgeFactory edgeFactory = new DefaultGraphEdgeFactory();
    private GraphModelFactory graphFactory = new DefaultGraphModelFactory();
    /* the actual presentation for the workflow */
    private WorkFlowUI workflowUi;
    /* the master controller instance */
    private NetcellGuiController mainController;
    private WorkflowModel workflowModel = new WorkflowModel();
    /* the graph with the components uis as nodes and the connecting arrows as edges */
    // private GraphModel viewGraph;
    private Map<GraphEdge, JArrow> arrows;
    private GraphModel dataGraph;

    private Map<String, Rectangle2D> componentsBounds = new HashMap<String, Rectangle2D>();
    private Map<String, GraphFigure> componentsUis = new HashMap<String, GraphFigure>();
    private WorkflowComponentUiMouseListener wfCompUiListener = new WorkflowComponentUiMouseListener();

    public WorkflowController(WorkFlowUI ui, NetcellGuiController controller) {
	workflowUi = ui;
	mainController = controller;
	workflowUi.addMouseListener(new WorkflowMouseListener());
    }

    public WorkFlowDefinition getCurrentDefinition() {
	return currentDefinition;
    }

    private void initialize() {
	System.out.println("init wfui");
	workflowUi.removeAll();
	// viewGraph = graphFactory.createGraphModel();
	arrows = new HashMap<GraphEdge, JArrow>();
	componentsBounds = new HashMap<String, Rectangle2D>();
	componentsUis = new HashMap<String, GraphFigure>();
    }

    public void drawGraph(GraphModel graph, WorkFlowDefinition definition, boolean constructTree) {
	currentDefinition = definition;
	dataGraph = graph;
	componentsConfigurations = currentDefinition.getWorkFlowConfig().getComponents();
	workflowUi.setLayout(null);
	if (constructTree) {
	    initialize();
	    if (componentsConfigurations.size() > 0) {
		drawWorkflow(graph, currentDefinition.getWorkFlowConfig().getEntryPointId());
	    }
	    workflowUi.repaint();
	}

	if (componentsConfigurations.size() > 0) {
	    updateArrows();
	}
	workflowUi.revalidate();
    }

    public void addNewComponent(Point location, WorkFlowComponentConfiguration compConfig, WorkFlowDefinition definition) {
	currentDefinition = definition;
	componentsConfigurations = currentDefinition.getWorkFlowConfig().getComponents();
	WorkFlowComponentUI compUi = addNewWfComp(compConfig, location);
	workflowUi.repaint();
    }

    public void removeComponent(String componentId, GraphModel graph) {
	List inputNodes = dataGraph.getInNodes(componentId);
	updateExitPoints(componentId, graph);
	WorkFlowComponentUI ui = (WorkFlowComponentUI) componentsUis.remove(componentId);
	componentsBounds.remove(componentId);
	workflowUi.remove(ui);
	if (inputNodes != null) {
	    for (int i = 0; i < inputNodes.size(); i++) {
		String iCompId = (String) inputNodes.get(i);
		WorkFlowComponentUI iui = (WorkFlowComponentUI) componentsUis.get(iCompId);
		updateExitPoints(iCompId, graph);
		iui.refresh();
	    }
	}
	dataGraph = graph;
	workflowUi.validate();
	updateArrows();
	workflowUi.repaint();
    }

    public void updateComponent(String componentId, GraphModel graph) {
	dataGraph = graph;
	((WorkFlowComponentUI) componentsUis.get(componentId)).refresh();
	workflowUi.validate();
	updateArrows();
	workflowUi.repaint();
    }

    public void renameComponent(String oldId, WorkFlowComponentConfiguration wfc, GraphModel graph) {
	WorkFlowComponentUI cui = (WorkFlowComponentUI) componentsUis.remove(oldId);
	cui.setCompConfig(wfc);
	componentsUis.put(wfc.getLocalId(), cui);
	updateComponent(wfc.getLocalId(), graph);

    }

    public void updateExitPointUi(String id, String value, GraphModel newModel) {
	System.out.println("update exit point for id " + id + " value " + value);
	dataGraph = newModel;
	ExitPointMappingUI epui = (ExitPointMappingUI) componentsUis.get(id);
	epui.setMappingName(value);
	workflowUi.validate();
	updateArrows(epui);
	workflowUi.repaint();
    }

    public void removeExitPointUi(String id, GraphModel newModel) {
	dataGraph = newModel;
	workflowUi.remove(componentsUis.get(id));
	componentsBounds.remove(id);
	workflowUi.validate();
	updateArrows();
	workflowUi.repaint();
    }

    public void addExitPointUi(String id, String value, GraphModel newModel) {
	dataGraph = newModel;
	ExitPointMappingUI epui = new ExitPointMappingUI(value, id);
	addNewUiObject(epui, id);
	updateArrows();
	workflowUi.repaint();
    }

    public void updateExitPoints(String compId, GraphModel newGraph) {
	GraphModel oldGraph = dataGraph;
	List<GraphEdge> prevOutEdges = oldGraph.getOutEdges(compId);
	List<GraphEdge> newOutEdges = newGraph.getOutEdges(compId);
	/* determine removed exit points */
	if (prevOutEdges != null) {
	    for (GraphEdge edge : prevOutEdges) {
		if (edge != null) {
		    if (newOutEdges == null || !newOutEdges.contains(edge)) {
			GenericNameValue edgeValue = (GenericNameValue) edge.getValue();
			if (edgeValue != null) {
			    ComponentExitPoint epm = (ComponentExitPoint) edgeValue.getValue();
			    if (epm != null && epm.getExitPointMapping() != null) {
				String id = compId + "." + edgeValue.getName();
				GraphFigure exitPointUi = componentsUis.remove(id);
				if (exitPointUi != null) {
				    workflowUi.remove(exitPointUi);
				    componentsBounds.remove(id);
				}

			    }
			}
		    }
		}
	    }
	}
	/* add new exit points */
	if (newOutEdges != null) {
	    for (GraphEdge edge : newOutEdges) {
		if (edge != null) {
		    if (prevOutEdges == null || !prevOutEdges.contains(edge)) {
			GenericNameValue edgeValue = (GenericNameValue) edge.getValue();
			if (edgeValue != null) {
			    ComponentExitPoint epm = (ComponentExitPoint) edgeValue.getValue();
			    // System.out.println("new ep: "+edge.getSourceNode().toString()+"->"+edge.getDestNode()+" "+epm);
			    if (epm != null && epm.getExitPointMapping() != null) {
				String id = compId + "." + edgeValue.getName();
				ExitPointMappingUI epui = new ExitPointMappingUI(epm.getExitPointMapping(), id);
				addNewUiObject(epui, id);
			    }
			}
		    }
		}
	    }
	}
    }

    public WorkFlowComponentUI getComponentUiForId(String id) {
	return (WorkFlowComponentUI) componentsUis.get(id);
    }

    private void drawWorkflow(GraphModel graph, String currentCompId) {
	Map<String, WorkFlowNodeUiInfo> subtrees = new HashMap<String, WorkFlowNodeUiInfo>();
	// workflowUi.setLayout(new FlowLayout());
	try {
	    computeDimensions(graph, currentCompId, subtrees, null);
	} catch (Exception e) {
	    System.out.println("Failed to compute dim for " + currentCompId);
	    e.printStackTrace();
	}
	workflowUi.setLayout(null);
	Point rootLocation = new Point(subtrees.get(currentCompId).getSubFlowDimension().width / 2, 0);
	drawSubtree(graph, currentCompId, subtrees, rootLocation, new HashSet<String>());
	workflowUi.shiftComponentsToZero();
    }

    private void drawArrow(GraphEdge edge) {
	String sourceCompId = (String) edge.getSourceNode();
	String destCompId = (String) edge.getDestNode();
	List<Point2D> sourcePoints = new ArrayList<Point2D>();
	List<Point2D> destPoints = new ArrayList<Point2D>();
	List<Point2D> path = new ArrayList<Point2D>();
	WorkFlowComponentUI sourceComp = (WorkFlowComponentUI) componentsUis.get(sourceCompId);
	GraphFigure destComp = componentsUis.get(destCompId);
	// GraphEdge edge = edgeFactory.createEdge(sourceCompId, destCompId);
	/* see if any value associated with this edge */
	GenericNameValue edgeValue = (GenericNameValue) edge.getValue();
	
	Point2D anchorPoint = null;
	boolean isError = false;
	if (edgeValue != null) {
	    /*
	     * this edge is an exit point mapping. We need to get the exact location of the label describing this exit
	     * point in order to draw the arrow from there
	     */
	    Object exitPointId = edgeValue.getName();
	    // if (!exitPointId.equals(SpecialExitPoints.CAUGHT_EXCEPTION)) {
	    JLabel exitPointLabel = sourceComp.getExitPointLabel(exitPointId);

	    /* the arrow can only leave form the center of the bottom edge of the label's bounding box */
	    if (exitPointLabel == null) {
		// System.out.println("no exitpointlabel " + exitPointId + " " + sourceCompId + " " + destCompId);
		Anchor errAnchor = sourceComp.getNextAvailableAnchor(BoxGraphFigure.RIGHT);
		anchorPoint = errAnchor.getLocation();
		sourcePoints.add(new Point2D.Double(anchorPoint.getX(), anchorPoint.getY()));
		anchorPoint = errAnchor.getRoot();
		/* this happens only when the exit point is actually an error mapping */
		isError = true;
	    } else {
		Rectangle bounds = exitPointLabel.getBounds();
		Point compUiLocation = sourceComp.getLocation();
		anchorPoint = new Point2D.Double(compUiLocation.x + bounds.getCenterX(), sourceComp.getBounds()
			.getMaxY());
		sourcePoints.add(new Point2D.Double(anchorPoint.getX(), anchorPoint.getY() + 15));
	    }

	    path.add(anchorPoint);
	    // } else {
	    // System.out.println("****does this ever arrives here?");
	    // }
	} else {
	    /*
	     * we have a direct transition, simply draw an arrow from the middle of the bottom edge of the source
	     * component to the destination component
	     */
	    Rectangle bounds = sourceComp.getBounds();
	    anchorPoint = new Point2D.Double(bounds.getCenterX(), bounds.getMaxY());
	    sourcePoints.add(new Point2D.Double(anchorPoint.getX(), anchorPoint.getY() + 15));
	    path.add(anchorPoint);
	    // sourcePoints = MathUtil.getMiddleEdges(enlargeBounds(sourceComp.getBounds()));
	}
	// Line2D minSegment = MathUtil.minDist(sourcePoints, MathUtil
	// .getMiddleEdges(destComp.getBounds()));
	// JArrow ar = new JArrow(minSegment.getP1(), minSegment.getP2());
	//	
	//	

	// path = MathUtil.getShortestPath(sourcePoints, getInAnchors(enlargeBounds(destComp.getBounds())),
	// componentsBounds.values(), (double) 20);
//	System.out.println("Compute arrow from "+sourceCompId+ " to "+destCompId);
	double pathDim = GraphUtil.getShortestPath(sourcePoints, destComp, componentsBounds.values(), (double) 50, path);
	JArrow ar = null;
	if (pathDim >= 0) {
	    //System.out.println(path);
	    // if (anchorPoint != null && !path.contains(anchorPoint)) {
	    // path.add(0, anchorPoint);
	    // }
	    if (isError) {
		ar = new JBrokenArrow(path, Color.red);
	    } else {
		ar = new JBrokenArrow(path);
	    }
	    ar.setSize(workflowUi.getSize());
	    ar.setLocation(0, 0);
	    workflowUi.add(ar);

	}
	// viewGraph.addEdge(edgeFactory.createEdge(sourceCompId, destCompId), ar);
	// viewGraph.addEdge(edge.getSourceNode(),edge.getDestNode(),ar);
	arrows.put(edge, ar);
    }

    private void drawSubtree(GraphModel graphData, String currentNodeId, Map<String, WorkFlowNodeUiInfo> subtrees,
	    Point nodeLocation, Set<String> processed) {
	if (processed.contains(currentNodeId)) {
	    return;
	}
	int hgap = workflowModel.getHGap();
	int vgap = workflowModel.getVGap();
	processed.add(currentNodeId);
	WorkFlowNodeUiInfo nodeInfo = subtrees.get(currentNodeId);
	Dimension currentSubflowDimension = nodeInfo.getSubFlowDimension();
	Dimension currentNodeDimension = nodeInfo.getNodeDimension();
	/* set location for current node */
	GraphFigure currentComponentUi = nodeInfo.getWfCompUi();
	currentComponentUi.setLocation(nodeLocation);
	currentComponentUi.setSize(currentNodeDimension);
	updateBounds(currentComponentUi);
	int nextFlowPos = nodeLocation.y + currentSubflowDimension.height + workflowModel.getVGap();
	int medianX = nodeLocation.x + currentNodeDimension.width / 2;
	String middleChildId = (String) graphData.getMiddleChild(currentNodeId);

	/*
	 * define default values for the max x the left child tree may reach and the min x the right child tree may
	 * reach
	 */
	int maxLeftEdge = medianX - hgap / 2;
	int minRightEdge = medianX + hgap / 2;

	/* draw middle child */
	if (middleChildId != null && !processed.contains(middleChildId)) {
	    WorkFlowNodeUiInfo middleNodeInfo = subtrees.get(middleChildId);
	    Dimension middleNodeDimension = middleNodeInfo.getNodeDimension();
	    Dimension middleSubtreeDimension = middleNodeInfo.getSubFlowDimension();
	    int halfWidth = (int) middleSubtreeDimension.getWidth() / 2;
	    maxLeftEdge -= (halfWidth + hgap / 2);
	    minRightEdge += halfWidth + hgap / 2;
	    int refSpanPos = medianX - middleNodeDimension.width / 2;
	    drawSubtree(graphData, middleChildId, subtrees, new Point(refSpanPos, nextFlowPos), processed);
	}
	/* draw children from the left */
	String leftChildId = (String) graphData.getMiddleChildLeft(currentNodeId);
	int nextSpanPos = maxLeftEdge;
	while (leftChildId != null) {
	    WorkFlowNodeUiInfo leftNodeInfo = subtrees.get(leftChildId);
	    Dimension leftSubtreeDimension = leftNodeInfo.getSubFlowDimension();
	    Dimension leftNodeDimension = leftNodeInfo.getNodeDimension();
	    // nextSpanPos -= (leftNodeDimension.width + (leftSubtreeDimension.width - leftNodeDimension.width)/2);
	    nextSpanPos -= (leftSubtreeDimension.width * (1 - leftNodeInfo.getRelativeXRatio()) + leftNodeDimension.width / 2);
	    drawSubtree(graphData, leftChildId, subtrees, new Point(nextSpanPos, nextFlowPos), processed);
	    // drawArrow(currentNodeId, leftChildId);
	    nextSpanPos -= workflowModel.getHGap();
	    leftChildId = (String) graphData.getPrevSibbling(leftChildId, currentNodeId);
	}
	/* draw children from the right */
	String rightChildId = (String) graphData.getMiddleChildRight(currentNodeId);
	nextSpanPos = minRightEdge;
	while (rightChildId != null) {
	    WorkFlowNodeUiInfo rightNodeInfo = subtrees.get(rightChildId);
	    Dimension rightSubtreeDimension = rightNodeInfo.getSubFlowDimension();
	    Dimension rightNodeDimension = rightNodeInfo.getNodeDimension();
	    // nextSpanPos += (rightSubtreeDimension.width - rightNodeDimension.width)/2;
	    nextSpanPos += rightSubtreeDimension.width * rightNodeInfo.getRelativeXRatio() - rightNodeDimension.width
		    / 2;
	    drawSubtree(graphData, rightChildId, subtrees, new Point(nextSpanPos, nextFlowPos), processed);
	    // drawArrow(currentNodeId, rightChildId);
	    nextSpanPos += rightSubtreeDimension.width + workflowModel.getHGap();
	    rightChildId = (String) graphData.getNextSibbling(rightChildId, currentNodeId);
	}
    }

    private Dimension computeDimensions(GraphModel graphData, String currentCompId,
	    Map<String, WorkFlowNodeUiInfo> subtrees, GraphEdge inEdge) {

	int hgap = workflowModel.getHGap();
	subtrees.put(currentCompId, null);
	/* add this component */
	boolean isExitPoint = false;
	GenericNameValue edgeValue = null;
	ComponentExitPoint exitPointMapping = null;
	if (inEdge != null) {
	    edgeValue = (GenericNameValue) inEdge.getValue();
	    if (edgeValue != null) {
		exitPointMapping = (ComponentExitPoint) edgeValue.getValue();
		if (exitPointMapping != null) {
		    isExitPoint = exitPointMapping.isExitPoint();
		}
	    }
	}
	GraphFigure currentCompUi = null;
	if (isExitPoint) {
	    currentCompUi = new ExitPointMappingUI(exitPointMapping.getExitPointMapping(), currentCompId);
	} else {
	    WorkFlowComponentConfiguration currentCompConfig = componentsConfigurations.get(currentCompId);
	    currentCompUi = new WorkFlowComponentUI(currentCompConfig);
	    currentCompUi.addMouseListener(wfCompUiListener);
	}
	workflowUi.add(currentCompUi);
	componentsUis.put(currentCompId, currentCompUi);
	currentCompUi.addComponentListener(this);
	workflowUi.validate();

	Dimension currentCompDimension = currentCompUi.getSize();
	Dimension curentSubtreeDimension = currentCompDimension;
	int subtreeWidth = 0;
	int subtreeHeight = 0;
	int maxSubtreeHeight = 0;
	int leftSubtreeWidth = 0;
	List<GraphEdge> outEdges = graphData.getOutEdges(currentCompId);
	int lastLeftIndex = -1;
	List<String> processedEdges = new ArrayList<String>();
	if (outEdges != null) {
	    int edgeIndex = -1;
	    int edgesCount = outEdges.size();
	    if (edgesCount > 1) {
		if (edgesCount % 2 == 0) {
		    lastLeftIndex = edgesCount / 2;
		} else {
		    lastLeftIndex = (edgesCount - 1) / 2;
		}
	    }
	    /* iterate over the descendants and calculate their widths and heights */
	    for (GraphEdge edge : outEdges) {
		edgeIndex++;
		String nodeName = (String) edge.getDestNode();
		if (subtrees.containsKey(nodeName)) {
		    continue;
		}
		processedEdges.add(nodeName);
		Dimension currentDimensions = computeDimensions(graphData, nodeName, subtrees, edge);
		subtreeWidth += currentDimensions.width;
		if (currentDimensions.height > maxSubtreeHeight) {
		    maxSubtreeHeight = currentDimensions.height;
		}
		if (lastLeftIndex >= 0 && edgeIndex < lastLeftIndex) {
		    leftSubtreeWidth += currentDimensions.width;
		}
	    }
	    /* add dist */
	    subtreeWidth += (processedEdges.size() - 1) * workflowModel.getHGap();
	    if (currentCompUi.getSize().width > subtreeWidth) {
		subtreeWidth = currentCompUi.getSize().width;
	    }
	    subtreeHeight += currentCompUi.getSize().height + workflowModel.getVGap();
	    curentSubtreeDimension = new Dimension(subtreeWidth, subtreeHeight);
	}
	WorkFlowNodeUiInfo nodeInfo = new WorkFlowNodeUiInfo();
	nodeInfo.setSubFlowDimension(curentSubtreeDimension);
	nodeInfo.setWfCompUi(currentCompUi);
	nodeInfo.setNodeDimension(currentCompDimension);
	if (lastLeftIndex >= 0) {
	    if (leftSubtreeWidth < currentCompDimension.width / 2 - hgap) {
		leftSubtreeWidth = currentCompDimension.width / 2 - hgap;
	    }
	    nodeInfo.setRelativeXRatio((double) leftSubtreeWidth / curentSubtreeDimension.width);
	    System.out.println(currentCompId + " : " + nodeInfo.getRelativeXRatio() + " (computed)");
	} else if (outEdges != null && outEdges.size() == 1) {
	    String middleEdgeId = (String) outEdges.get(0).getDestNode();
	    WorkFlowNodeUiInfo middleNodeInfo = subtrees.get(middleEdgeId);
	    if (middleNodeInfo != null && processedEdges.contains(middleEdgeId)) {
		nodeInfo.setRelativeXRatio(middleNodeInfo.getRelativeXRatio());
		System.out.println(currentCompId + " : " + nodeInfo.getRelativeXRatio() + " (inherited)");
	    } else {
		nodeInfo.setRelativeXRatio(0.5d);
		System.out.println(currentCompId + " : " + nodeInfo.getRelativeXRatio() + " (default)");
	    }
	} else {
	    nodeInfo.setRelativeXRatio(0.5d);
	    // System.out.println(currentCompId+" : "+nodeInfo.getRelativeXRatio()+" (default)");
	}

	subtrees.put(currentCompId, nodeInfo);
	return curentSubtreeDimension;
    }

    private void addNewUiObject(GraphFigure ui, String id) {
	workflowUi.add(ui);
	componentsUis.put(id, ui);
	ui.addComponentListener(this);
	workflowUi.validate();
    }

    private WorkFlowComponentUI addNewWfComp(WorkFlowComponentConfiguration compConfig, Point pos) {
	WorkFlowComponentUI currentCompUi = new WorkFlowComponentUI(compConfig);
	currentCompUi.addMouseListener(wfCompUiListener);
	if (pos != null) {
	    currentCompUi.setLocation(pos);
	}
	addNewUiObject(currentCompUi, compConfig.getLocalId());
	return currentCompUi;
    }

    private void updateArrows() {
	for (JArrow arrow : arrows.values()) {
	    if (arrow != null) {
		workflowUi.remove(arrow);
	    }
	}
	arrows.clear();
	Set<Object> nodes = dataGraph.getNodes();
	for (Object n : nodes) {
	    updateArrows((String) n);
	}
    }

    private void updateArrows(String compId) {
	List<GraphEdge> outEdges = dataGraph.getOutEdges(compId);
	if (outEdges == null) {
	    return;
	}
	for (GraphEdge e : outEdges) {
	    drawArrow(e);
	}
    }

    private void updateArrows(GraphFigure target) {
	String targetId = target.getId();
	List<GraphEdge> inEdges = dataGraph.getInEdges(targetId);
	List<GraphEdge> outEdges = dataGraph.getOutEdges(targetId);

	if (inEdges != null) {
	    for (GraphEdge edge : inEdges) {
		/* remove in arrows */
		Component arrow = arrows.get(edge);
		if (arrow != null) {
		    workflowUi.remove(arrow);
		}
		/* create new arrow */
		drawArrow(edge);
	    }
	}
	if (outEdges != null) {
	    for (GraphEdge edge : outEdges) {
		/* remove out arrows */
		Component arrow = (Component) arrows.get(edge);
		if (arrow != null) {
		    workflowUi.remove(arrow);
		}
		/* create new arrow */
		drawArrow(edge);
	    }
	}

	workflowUi.repaint();
    }

    public void componentHidden(ComponentEvent e) {
	// TODO Auto-generated method stub

    }

    private void updateBounds(GraphFigure wfCompUi) {
	Rectangle bounds = wfCompUi.getBounds();
//	double minDist = (double) 40;
//	bounds = new Rectangle(bounds.getLocation(), new Dimension( (int)(bounds.getWidth()+ minDist), (int)(bounds.getHeight()+ minDist)));
	
	componentsBounds.put(wfCompUi.getId(), bounds);
    }

    private List<Point2D> getInAnchors(Rectangle2D rectangle) {
	List<Point2D> middles = new ArrayList<Point2D>(4);

	middles.add(new Point2D.Double(rectangle.getMinX(), rectangle.getCenterY()));
	// middles.add(new Point2D.Double(rectangle.getCenterX(), rectangle.getMaxY()));
	middles.add(new Point2D.Double(rectangle.getMaxX(), rectangle.getCenterY()));
	middles.add(new Point2D.Double(rectangle.getCenterX(), rectangle.getMinY()));
	return middles;
    }

    private Rectangle enlargeBounds(Rectangle bounds) {
	bounds.x--;
	bounds.y--;
	bounds.width += 2;
	bounds.height += 2;
	return bounds;
    }

    public void componentMoved(ComponentEvent e) {
	Component c = e.getComponent();
	Dimension wfd = workflowUi.getSize();
	Point wfl = workflowUi.getLocation();
	int cx = c.getLocation().x;
	int newRight = cx + c.getSize().width;

	// if(newRight > wfd.width){
	// workflowUi.setSize(newRight,wfd.height);
	// }
	// // if(cx < 0){
	// // workflowUi.setLocation(cx-wfl.x, wfl.y);
	// // }
	// // if(c.getLocation().x < 0){
	// // workflowUi.setLocation(wfl.x - c.getLocation().x , wfl.y);
	// // }

	GraphFigure wfCompUi = (GraphFigure) c;
	updateBounds(wfCompUi);
	updateArrows(wfCompUi);

    }

    public void componentResized(ComponentEvent e) {

    }

    public void componentShown(ComponentEvent e) {
	// TODO Auto-generated method stub

    }

    /**
     * Mouse listener for workflow components
     * 
     * @author adi
     * 
     */
    class WorkflowComponentUiMouseListener implements MouseListener {

	public void mouseClicked(MouseEvent e) {
	    // TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
	    // TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
	    // TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
	    WorkFlowComponentUI compUi = (WorkFlowComponentUI) e.getComponent();
	    mainController.populateControls(compUi.getCompConfig());
	}

	public void mouseReleased(MouseEvent e) {
	    workflowUi.revalidate();
	    workflowUi.shiftComponentsToZero();
	}
    }

    /**
     * Mouse listener for workflow
     * 
     * @author adi
     * 
     */
    class WorkflowMouseListener implements MouseListener {

	public void mouseClicked(MouseEvent e) {
	    mainController.setCurrentSelectedComponent(null);
	    mainController.showWorkflowControlForm();
	}

	public void mouseEntered(MouseEvent e) {
	    // TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
	    // TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
	    mainController.showWorkflowControlForm();

	}

	public void mouseReleased(MouseEvent e) {
	    // TODO Auto-generated method stub

	}

    }
}
