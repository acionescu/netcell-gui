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
package ro.zg.netcell.gui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ro.zg.data.util.graphs.DefaultGraphModel;
import ro.zg.data.util.graphs.DefaultGraphModelFactory;
import ro.zg.data.util.graphs.GraphModel;
import ro.zg.data.util.graphs.GraphModelFactory;
import ro.zg.java.forms.Form;
import ro.zg.netcell.constants.SpecialExitPoints;
import ro.zg.netcell.control.CommandResponse;
import ro.zg.netcell.gui.components.EntitiesTree;
import ro.zg.netcell.gui.components.WorkFlowComponentUI;
import ro.zg.netcell.gui.components.WorkflowController;
import ro.zg.netcell.gui.forms.FormManager;
import ro.zg.netcell.gui.resources.PopupLabelsKeys;
import ro.zg.netcell.gui.util.DefinitionsStatusMonitor;
import ro.zg.netcell.gui.vo.ExecuteFlowRequest;
import ro.zg.netcell.toolkit.Tool;
import ro.zg.netcell.toolkit.Toolkit;
import ro.zg.netcell.toolkit.actions.RemoveComponentAction;
import ro.zg.netcell.toolkit.listeners.NetcellActionsListener;
import ro.zg.netcell.vo.DefinitionsRepository;
import ro.zg.netcell.vo.InputParameter;
import ro.zg.netcell.vo.OutputParameter;
import ro.zg.netcell.vo.configurations.ComponentConfiguration;
import ro.zg.netcell.vo.configurations.ComponentExitPoint;
import ro.zg.netcell.vo.configurations.ComponentExitPointsMapping;
import ro.zg.netcell.vo.configurations.WorkFlowComponentConfiguration;
import ro.zg.netcell.vo.configurations.WorkFlowConfiguration;
import ro.zg.netcell.vo.definitions.ComponentDefinition;
import ro.zg.netcell.vo.definitions.ConfigurableComponentDefinition;
import ro.zg.netcell.vo.definitions.DataAccessComponentDefinition;
import ro.zg.netcell.vo.definitions.DataSourceDefinition;
import ro.zg.netcell.vo.definitions.EntitiesTypes;
import ro.zg.netcell.vo.definitions.EntityDefinition;
import ro.zg.netcell.vo.definitions.EntityType;
import ro.zg.netcell.vo.definitions.ExecutableEntityDefinition;
import ro.zg.netcell.vo.definitions.ScheduledJobDefinition;
import ro.zg.netcell.vo.definitions.WorkFlowDefinition;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.ListMap;
import ro.zg.util.data.ObjectsUtil;
import ro.zg.util.data.ValueType;
import ro.zg.util.data.type.ParameterType;

public class NetcellGuiController implements TreeSelectionListener {
    private NetcellGui mainFrame;
    private DefinitionsRepository definitionsRepository;
    private WorkFlowDefinition currentDefinition;
    private GraphModel currentWorkflowGraph;
    private String selectedPackageName;
    private String selectedEntityType;
    private WorkFlowComponentConfiguration currentSelectedComponent;
    private Map<String, GraphModel> workflowGraphs = new HashMap<String, GraphModel>();
    private GraphModelFactory graphFactory = new DefaultGraphModelFactory();
    private WorkflowController workflowController;
    private ComponentsDragAndDropController dragAndDropController;
    private boolean isWorkflowSelected;
    private JPopupMenu workflowDefPopup;
    private DefinitionsStatusMonitor definitionsStatusMonitor;
    private EntityDefinition selectedDefinitionInTree;
    private NetcellDao netcellDao = new NetcellDao();
    private NetcellActionsListener actionsListener = new NetcellActionsListener(this);
    /**
     * Current component definitions selected from the tree
     */
    private ComponentDefinition currentComponentDefinition;
    private FormManager formsManager = new FormManager();

    public NetcellGuiController() {

    }

    public void start() throws Exception {
	initUi();
//	mainFrame.showSplasScreen();
	initFormsConfigManager();
	loadDefinitions();
	populateDefinitionsTree();
	definitionsStatusMonitor = new DefinitionsStatusMonitor(definitionsRepository.getAllObjectsList());
	initDragAndDropController();

	mainFrame.hideSplashScreen();
	mainFrame.displayWorkingArea(true);
    }

    private void initUi() {
	mainFrame = new NetcellGui(this);
	populateToolBar();
	populateEntitiesToolBar();
	/* add listener for workflowui */

	/* add listener to the definitions tree */
	EntitiesTree entitiesTree = mainFrame.getEntitiesTree();
	entitiesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	entitiesTree.addTreeSelectionListener(this);
	entitiesTree.addMouseListener(new TreeMouseEvent());
	workflowController = new WorkflowController(mainFrame.getWorkflowUi(), this);

    }

    private void initFormsConfigManager() {

	formsManager.setController(this);
    }

    private void createWorkflowDefPopup() {
	workflowDefPopup = new JPopupMenu();
	ResourceBundle wfdefres = ResourceBundle.getBundle("PopupLabels");
	/* add workflow */
	JMenuItem item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.ADD_NEW_WORKFLOW));
	item.setActionCommand("add.workflow");
	item.addActionListener(actionsListener);
	workflowDefPopup.add(item);
	/* add datasource */
	item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.ADD_NEW_DATASOURCE));
	item.setActionCommand("add.datasource");
	item.addActionListener(actionsListener);
	workflowDefPopup.add(item);
	/* add data access component */
	item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.ADD_NEW_DATA_ACCESS_COMPONENT));
	item.setActionCommand("add.datacomponent");
	item.addActionListener(actionsListener);
	workflowDefPopup.add(item);
	/* add custom component */
	item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.ADD_NEW_CONFIGURABLE_COMPONENT));
	item.setActionCommand("add.customcomponent");
	item.addActionListener(actionsListener);
	workflowDefPopup.add(item);
	/* reload engine */
	item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.RELOAD_ENGINE));
	item.setActionCommand("engine.reload");
	item.addActionListener(actionsListener);
	workflowDefPopup.add(item);
	
	if (selectedDefinitionInTree != null) {
	    item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.REMOVE));
	    item.setActionCommand("remove.entity");
	    item.addActionListener(actionsListener);
	    workflowDefPopup.add(item);

	    if (definitionsStatusMonitor.isChanged(selectedDefinitionInTree.getId())) {
		item = new JMenuItem(wfdefres.getString(PopupLabelsKeys.COMMIT));
		item.setActionCommand("commit");
		item.addActionListener(actionsListener);
		workflowDefPopup.add(item);
	    }
	}

    }

    private void populateToolBar() {
	JToolBar toolBar = mainFrame.getToolBar();
	toolBar.removeAll();
	JButton button = new JButton();
	button.setActionCommand("display.execute.flow.form");
	button.addActionListener(actionsListener);
	button.setText("Execute");
	toolBar.add(button);
	mainFrame.validate();
    }

    private void populateEntitiesToolBar() {
	JToolBar toolBar = mainFrame.getEntitiesToolBar();
	toolBar.removeAll();
	addActionButton(toolBar, "add.datasource", "images/datasource.png",true);
	addActionButton(toolBar, "add.datacomponent", "images/data_access.png",true);
	addActionButton(toolBar, "add.workflow", "images/workflow.png",true);
	addActionButton(toolBar, "add.customcomponent", "images/custom_component.png",true);
    }

    private void addActionButton(JComponent owner, String actionName, String text, boolean isIcon) {
	JButton button = new JButton();
	button.setActionCommand(actionName);
	button.addActionListener(actionsListener);
	if (!isIcon) {
	    button.setText(text);
	} else {
	   ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(text));
	   button.setIcon(imageIcon);
	}
	owner.add(button);
    }

    private void loadDefinitions() throws Exception {
	definitionsRepository = netcellDao.getDefinitions();
	// dataSourceDefinitions = netcellDao.getDatasourcesDefinitions();
    }

    private void initDragAndDropController() {
	dragAndDropController = new ComponentsDragAndDropController(mainFrame.getEntitiesTree(), mainFrame
		.getWorkflowUi(), this);
    }

    public boolean checkEntityExists(String name) {
	// return definitionsMap.containsKey(name);
	return definitionsRepository.containsDefinitionWithId(name);
    }

    public boolean commitSelectedEntity() throws Exception {
	if (selectedDefinitionInTree == null) {
	    return false;
	}
	String id = selectedDefinitionInTree.getId();
	boolean result = false;
	if (definitionsStatusMonitor.isChanged(id)) {
	    if (definitionsStatusMonitor.isNew(id)) {
		netcellDao.createEntity(selectedDefinitionInTree);
	    } else {
		netcellDao.updateEntity(selectedDefinitionInTree);
	    }
	    definitionsStatusMonitor.createCheckPoint(selectedDefinitionInTree);
	    result = true;
	}
	return result;
    }
    
    public void reloadEngine() {
	netcellDao.reloadEngine();
    }

    public void addNewWorkflow(String name) {
	WorkFlowDefinition newDef = new WorkFlowDefinition();

	newDef.setId(name);
	newDef.setWorkFlowConfig(new WorkFlowConfiguration());
	definitionsRepository.addObject(newDef);

	// addNodeToTree(name, "workflow-definition");
	mainFrame.getEntitiesTree().addNode(EntitiesTypes.WORKFLOW, name);
	setCurrentDefinition(newDef);
	definitionsStatusMonitor.onDefinitionChanged(currentDefinition);
    }

    public void addNewDataAccessComponent(DataAccessComponentDefinition dacd) {
	// definitionsMap.put(dacd.getId(), dacd);
	definitionsRepository.addObject(dacd);
	addNodeToTree(dacd.getId(), "data-access-component-definition");
	definitionsStatusMonitor.onDefinitionChanged(dacd);
    }

    public void addNewDatasource(DataSourceDefinition dsd) {
	EntityType et = EntityType.getEntityType(EntitiesTypes.DATASOURCE, dsd.getDatasourceType().toString());
	try {
	    DataSourceDefinition newDsd = (DataSourceDefinition) netcellDao.createEntityDirectoryStructure(dsd.getId(), et);
	    // definitionsMap.put(newDsd.getId(), newDsd);
	    definitionsRepository.addObject(newDsd);
	    addNodeToTree(newDsd.getId(), EntitiesTypes.DATASOURCE);
	    definitionsStatusMonitor.onDefinitionChanged(newDsd);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void fireDatasourceUpdate(String dsName) {
	ActionEvent actionEvent = new ActionEvent(definitionsRepository.getDefinitionById(dsName), 0,
		"update.datasource");
	actionsListener.actionPerformed(actionEvent);
    }
    
    public void fireScheduledJobUpdate(String jobName){
	ActionEvent actionEvent = new ActionEvent(definitionsRepository.getDefinitionById(jobName), 0,
		"update.scheduledjob");
	actionsListener.actionPerformed(actionEvent);
    }

    public void addNewCustomComponent(ConfigurableComponentDefinition ccd) {
	EntityType et = EntityType.getEntityType(EntitiesTypes.CONFIGURABLE_COMPONENT, ccd.getTemplateId());
	try {
	    ConfigurableComponentDefinition newCcd = (ConfigurableComponentDefinition) netcellDao.createEntityDirectoryStructure(ccd
		    .getId(), et);
	    definitionsRepository.addObject(newCcd);
	    addNodeToTree(newCcd.getId(), EntitiesTypes.CONFIGURABLE_COMPONENT);
	    definitionsStatusMonitor.onDefinitionChanged(newCcd);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void fireCustomComponentUpdate(String ccName) {
	ActionEvent actionEvent = new ActionEvent(definitionsRepository.getDefinitionById(ccName), 0,
		"update.customcomponent");
	actionsListener.actionPerformed(actionEvent);
    }

    public void fireDataAccessComponentUpdate(String name) {
	ActionEvent actionEvent = new ActionEvent(definitionsRepository.getDefinitionById(name), 0,
		"update.datacomponent");
	actionsListener.actionPerformed(actionEvent);
    }

    private void addNodeToTree(String name, String parentNode) {
	// EntitiesTree t = mainFrame.getEntitiesTree();
	// DefaultMutableTreeNode root = (DefaultMutableTreeNode) t.getModel().getRoot();
	// TreePath tp = t.getNextMatch(parentNode, 0, Position.Bias.Backward);
	// DefaultMutableTreeNode wfDefRoot = (DefaultMutableTreeNode) tp.getPathComponent(1);
	// DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
	// wfDefRoot.add(newNode);
	// ((DefaultTreeModel) t.getModel()).nodeStructureChanged(wfDefRoot);
	// t.setSelectionPath(new TreePath(newNode.getPath()));
	mainFrame.getEntitiesTree().addNode(parentNode, name);
    }

    private void populateDefinitionsTree() {
	// Map<String, DefaultMutableTreeNode> packageNodes = new HashMap<String, DefaultMutableTreeNode>();
	//
	// JTree entitiesTree = mainFrame.getEntitiesTree();
	// DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Entities");
	// DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
	// entitiesTree.setModel(treeModel);
	// entitiesTree.revalidate();
	// /* add datasources */
	// // DefaultMutableTreeNode datasourcesNode = new DefaultMutableTreeNode("datasources");
	// // rootNode.add(datasourcesNode);
	// // for(String datasourceName : dataSourceDefinitions.keySet()){
	// // DefaultMutableTreeNode dsNode = new DefaultMutableTreeNode(datasourceName);
	// // datasourcesNode.add(dsNode);
	// // }
	// for (Map.Entry<String, List<EntityDefinition>> entry :
	// definitionsRepository.getDefinitionsByType().entrySet()) {
	// DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(entry.getKey());
	// treeModel.insertNodeInto(currentNode, rootNode, rootNode.getChildCount());
	//
	// String entityType = entry.getKey();
	// boolean isFlow = entityType.equals(EntitiesTypes.WORKFLOW);
	// for (EntityDefinition entityDef : entry.getValue()) {
	// String entityId = entityDef.getId();
	// int nameStartIndex = entityId.lastIndexOf(".");
	// if (nameStartIndex < 0) {
	// DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(entityId);
	// treeModel.insertNodeInto(entityNode, currentNode, currentNode.getChildCount());
	// } else {
	//
	// String packageName = entityId.substring(0, nameStartIndex);
	// DefaultMutableTreeNode pkgNode = packageNodes.get(packageName);
	// if (pkgNode == null) {
	// pkgNode = new DefaultMutableTreeNode(packageName);
	// treeModel.insertNodeInto(pkgNode, currentNode, currentNode.getChildCount());
	// packageNodes.put(packageName, pkgNode);
	// }
	// DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(entityId
	// .substring(nameStartIndex + 1));
	// treeModel.insertNodeInto(entityNode, pkgNode, pkgNode.getChildCount());
	// }
	//
	// // /* map this entity to the id so we can recognize it later when it is selected from the tree */
	// // definitionsMap.put(entityDef.getId(), entityDef);
	// // if (isFlow) {
	// // allFlowsDefinitions.put(entityDef.getId(), (WorkFlowDefinition) entityDef);
	// // }
	// }
	// }
	// treeModel.reload();
	mainFrame.getEntitiesTree().populateFromDefinitionsRepository(definitionsRepository);
    }

    public void setCurrentSelectedComponent(WorkFlowComponentConfiguration wfCompConfig) {
	if (currentSelectedComponent != null) {
	    WorkFlowComponentUI cui = workflowController.getComponentUiForId(currentSelectedComponent.getLocalId());
	    if (cui != null) {
		cui.getInputMap().clear();
		cui.getActionMap().clear();
	    }
	}
	if (wfCompConfig != null) {
	    currentSelectedComponent = wfCompConfig;
	    WorkFlowComponentUI cui = workflowController.getComponentUiForId(currentSelectedComponent.getLocalId());
	    cui.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
		    "removeComponent");
	    cui.getActionMap().put("removeComponent", new RemoveComponentAction(this));
	}
    }

    /**
     * It removes the selected component in the workflow editor
     */
    public void removeSelectedComponent() {
	if (currentSelectedComponent == null) {
	    return;
	}
	currentDefinition.getWorkFlowConfig().removeComponentWithId(currentSelectedComponent.getLocalId());
	updateWorkflowView(false);

	workflowController.removeComponent(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
	setCurrentSelectedComponent(null);
	showWorkflowControlForm();
    }

    public void removeSelectedEntity() {
	try {
	    boolean removed = netcellDao.removeEntity(selectedDefinitionInTree);
	} catch (Exception e) {
	    // TODO display an error message
	    e.printStackTrace();
	}
    }

    public void populateControls(WorkFlowComponentConfiguration wfCompConfig) {
	setCurrentSelectedComponent(wfCompConfig);

	// Form form = getFormForObject(wfCompConfig);
	// form.setAuxiliaryData(getAuxiliaryDataForWfComp(wfCompConfig));
	// form.addPropertyChangeListener(new WorkflowComponentConfigFormListener(this, form));
	// try {
	// form.initialize();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	Form form = formsManager.createFormForObject(wfCompConfig);
	mainFrame.setControlsComponent((JComponent) form.getUi().getHolder());
	mainFrame.validate();
    }

    public void showWorkflowControlForm() {
	if (currentDefinition == null) {
	    return;
	}
	// Form form = getFormForObject(currentDefinition);
	// Map<String, Object> auxiliaryData = new HashMap<String, Object>();
	// auxiliaryData.put("types", InputParameterType.valuesAsStringArray());
	// auxiliaryData.put("componentIds", getAllComponentsIds());
	// auxiliaryData.put("contextParameters", getContextParamsNames());
	// form.setAuxiliaryData(auxiliaryData);
	// // form.addPropertyChangeListener(new WorkflowComponentConfigFormListener(this,form));
	// try {
	// form.initialize();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	Form form = formsManager.createFormForObject(currentDefinition);
	mainFrame.setControlsComponent((JComponent) form.getUi().getHolder());
	mainFrame.validate();
    }

    private Map<String, Object> getAuxiliaryDataForWfComp(WorkFlowComponentConfiguration wfCompConfig) {
	Map<String, Object> auxiliaryData = new HashMap<String, Object>();
	/* get the configuration for the component */
	ComponentConfiguration compConfig = wfCompConfig.getComponentConfig();
	/* get the actual component definition */
	ComponentDefinition compDefinition = (ComponentDefinition) definitionsRepository.getDefinitionById(compConfig
		.getComponent());
	List<GenericNameValue> availableContextParameters = getContextParamsAvailableToComponent(wfCompConfig);
	auxiliaryData.put("inputParameters", compDefinition.getInputParameters());
	auxiliaryData.put("availableParameters", availableContextParameters);
	auxiliaryData.put("outputParameters", getCompOutputParams(compDefinition));
	auxiliaryData.put("componentIds", getAllComponentsIds());
	List<String> mapableExitParameters = getMapableExitParams(compDefinition);
	ComponentExitPointsMapping cmap = wfCompConfig.getComponentMapping();
	if (mapableExitParameters.size() > 0) {
	    auxiliaryData.put("exitParameters", mapableExitParameters);
	    cmap.setComplexMappingTypes();
	} else {
	    cmap.setSimpleMappingTypes();
	}
	auxiliaryData.put("valuesForParameter", getPossibleValuesForExitParam(compDefinition));
	return auxiliaryData;
    }

    public Map<String, List<String>> getContextParameterNameByType() {
	ListMap<String, String> map = new ListMap<String, String>();
	for (GenericNameValue p : getContextParamsMap().values()) {
	    map.add(p.getType(), p.getName());
	    /* create also an entry for the generic types and put all the context params */
	    map.add(ParameterType.GENERIC_TYPE, p.getName());
//	    if (p.getComplexType().getParameterTypeType().equals(ParameterType.LIST_TYPE)) {
//		map.add(ParameterType.LIST_TYPE + "[]", p.getName());
//	    }
	    map.add(p.getComplexType().fullGenericTypes(),p.getName());
	}
	return map.getAll();
    }

    public List<String> getAllFlowsIds() {
	// return new ArrayList<String>(allFlowsDefinitions.keySet());
	return definitionsRepository.getAllIdsForType(EntitiesTypes.WORKFLOW);
    }

    private List<String> getCompOutputParams(ComponentDefinition compDef) {
	List<String> op = new ArrayList<String>();
	for (OutputParameter p : compDef.getOutputParameters()) {
	    op.add(p.getName());
	}
	return op;
    }

    private List<String> getMapableExitParams(ComponentDefinition compDef) {
	Map<String, String> simpleRespMappings = compDef.getSimpleResponseMappings();
	List<String> validParamNames = new ArrayList<String>();
	if (simpleRespMappings != null && simpleRespMappings.size() > 0) {
	    String mappedParamName = compDef.getOutputParameters().get(0).getName();
	    validParamNames.add(mappedParamName);

	} else {
	    // TODO: check form complex response mappings
	}
	return validParamNames;
    }

    private Map<String, List<String>> getPossibleValuesForExitParam(ComponentDefinition compDef) {
	Map<String, List<String>> values = new LinkedHashMap<String, List<String>>();
	Map<String, String> simpleRespMappings = compDef.getSimpleResponseMappings();
	if (simpleRespMappings != null) {
	    String paramName = compDef.getOutputParameters().get(0).getName();
	    values.put(paramName, new ArrayList(simpleRespMappings.values()));
	} else {
	    // TODO: do it for complex params
	}
	return values;
    }

    public List<String> getAllComponentsIds() {
	return new ArrayList(currentDefinition.getWorkFlowConfig().getComponents().keySet());
    }

    public Form getFormForObject(Object target) {
	return formsManager.createFormForObject(target);
    }

    public Form getFormForObject(Object target, String formIdentifier) {
	return formsManager.createFormForObject(target, formIdentifier);
    }

    public EntityDefinition getEntityDefinitionById(String entityId) {
	return definitionsRepository.getDefinitionById(entityId);
    }

    public List<GenericNameValue> getContextParamsAvailableToComponent(WorkFlowComponentConfiguration wfCompConfig) {
	return new ArrayList<GenericNameValue>(getContextParamsAvailableToComponentMap(wfCompConfig).values());
    }

    /**
     * 
     * @param wfCompConfig
     * @return a list with all the context parameters that are available to a certain component. That is all the flow
     *         input parameters and all the parameters added on the context by the previous components executed before
     *         the current component.
     */
    public Map<String, GenericNameValue> getContextParamsAvailableToComponentMap(
	    WorkFlowComponentConfiguration wfCompConfig) {
	String currentCompId = wfCompConfig.getLocalId();
	if (currentCompId.equals(currentDefinition.getWorkFlowConfig().getEntryPointId())) {
	    return (Map<String, GenericNameValue>) ObjectsUtil.createMapFromCollection(
		    currentDefinition.getInputParameters(), "name");
	}
	Map<String, GenericNameValue> params = new LinkedHashMap<String, GenericNameValue>();
	List<Object> inNodes = currentWorkflowGraph.getInNodes(currentCompId);
	if (inNodes != null) {
	    for (Object o : inNodes) {
		WorkFlowComponentConfiguration prevComp = currentDefinition.getWorkFlowConfig().getComponents().get(o);
		if (!currentCompId.equals(o)) {
		    Map<String, GenericNameValue> paramsFromPrevComp = getContextParamsAvailableToComponentMap(prevComp);
		    params.putAll(paramsFromPrevComp);
		}

		ExecutableEntityDefinition compDef = (ExecutableEntityDefinition) definitionsRepository
			.getDefinitionById(prevComp.getComponentConfig().getComponent());
		/* add also the output params that are mapped on context parameters */
		Map<String, String> outputParamsMappings = prevComp.getOutputParamsMappings();
		Map<String, ValueType> staticInputParamMappings = prevComp.getComponentConfig().getStaticInputParams();
		Map<String, String> dynamicInputParamMappings = prevComp.getComponentConfig().getDynamicInputParams();
		/*
		 * iterate over the output params of this component and see if any of them is mapped on the global
		 * context. if it is then add this parameter to the list of available parameters for our target
		 * component
		 */
		for (OutputParameter outParam : compDef.getOutputParameters()) {
		    if (outputParamsMappings.containsKey(outParam.getName())) {
			String contextName = outputParamsMappings.get(outParam.getName());
			GenericNameValue cp = new GenericNameValue();
			cp.setName(contextName);
			if (outParam.getMatchingTypeParamName() != null) {
			    String refIpName = outParam.getMatchingTypeParamName();
			    /* check to see if the input param is mapped as static or dynamic */
			    if (staticInputParamMappings.containsKey(refIpName)) {
				cp.setType(staticInputParamMappings.get(refIpName).getType());
			    } else if (dynamicInputParamMappings.containsKey(refIpName)) {
				String mappedParamName = dynamicInputParamMappings.get(refIpName);
				if (params.containsKey(mappedParamName)) {
				    cp.setType(params.get(mappedParamName).getType());
				}

			    }
			} else {
			    cp.setType(outParam.getType());
			}
			params.put(cp.getName(), cp);
		    }
		}
	    }
	}
	return params;
    }

    /**
     * Returns all the available context parameters
     * 
     * @return
     */
    public List<String> getContextParamsNames() {
	Set<String> params = new HashSet<String>();
	/* add input parameters */
	for (InputParameter ip : currentDefinition.getInputParameters()) {
	    params.add(ip.getName());
	}
	/* add the output params for each component mapped as context params */
	for (WorkFlowComponentConfiguration wfc : currentDefinition.getWorkFlowConfig().getComponents().values()) {
	    params.addAll(wfc.getOutputParamsMappings().values());
	}
	return new ArrayList<String>(params);
    }

    public Map<String, GenericNameValue> getContextParamsMap() {
	// Map<String, GenericNameValue> cpm = new LinkedHashMap<String, GenericNameValue>();
	// cpm.putAll((Map<String, GenericNameValue>) ObjectsUtil.createMapFromList(
	// currentDefinition.getInputParameters(), "name"));
	// cpm.putAll((Map<String, GenericNameValue>) ObjectsUtil.createMapFromList(
	// currentDefinition.getLocalParameters(), "name"));
	// return cpm;
	return currentDefinition.getContextParamsMap();
    }

    public List<GenericNameValue> getContextParamsList() {
	// List<GenericNameValue> allContextParamsList = new ArrayList<GenericNameValue>();
	// allContextParamsList.addAll(currentDefinition.getInputParameters());
	// allContextParamsList.addAll(currentDefinition.getLocalParameters());
	// return allContextParamsList;
	return currentDefinition.getContextParamsList();
    }

    /**
     * Returns the fully defined input params for the specified workflow component Fully defined meaning they have the
     * right types set
     * 
     * @param wfc
     * @return
     */
    public Map<String, InputParameter> getInputParameters(WorkFlowComponentConfiguration wfc) {
	ComponentConfiguration cc = wfc.getComponentConfig();
	ExecutableEntityDefinition cDef = (ExecutableEntityDefinition) definitionsRepository.getDefinitionById(cc
		.getComponent());
	Map<String, String> dynamicParams = cc.getDynamicInputParams();
	Map<String, ValueType> staticParams = cc.getStaticInputParams();
	Map<String, GenericNameValue> contextParams = getContextParamsMap();
	/* do a deep copy for this */
	Map<String, InputParameter> response = (Map<String, InputParameter>) ObjectsUtil.copy(cDef
		.getInputParametersMap());
	/*
	 * we're mainly interested in the dynamic input params because their type cannot be obtained directly, we don't
	 * have this kind of problem with static params because their type is strongly defined
	 */
	for (InputParameter ip : response.values()) {
	    String name = ip.getName();
	    if (dynamicParams.containsKey(name)) {
		String mappedName = dynamicParams.get(name);
		GenericNameValue contextParam = contextParams.get(mappedName);
		if (contextParam != null) {
		    /* now set the type for the input param to be the same like the mapped context param */
		    ip.setType(contextParam.getType());
		}
	    }
	}
	/* for static params set the values */
	for (Map.Entry<String, ValueType> entry : staticParams.entrySet()) {
	    ValueType vt = entry.getValue();
	    String value = (vt != null)?vt.getValue():null;
	    if(response.containsKey(entry.getKey())) {
		response.get(entry.getKey()).setValue(value);
	    }
	    
	}
	return response;
    }

    public List<OutputParameter> getOutputParameters(WorkFlowComponentConfiguration wfc) {
	ExecutableEntityDefinition cDef = (ExecutableEntityDefinition) definitionsRepository.getDefinitionById(wfc
		.getComponentConfig().getComponent());
	// List<OutputParameter> outList = new ArrayList<OutputParameter>(cDef.getOutputParameters());
	// Map<String, ValueType> staticParams = wfc.getComponentConfig().getStaticInputParams();
	// Map<String, String> dynamicParams = wfc.getComponentConfig().getDynamicInputParams();
	// Map<String, GenericNameValue> contextParams = getContextParamsAvailableToComponentMap(wfc);
	// /* now set the type of the dynamic output parameters */
	// for (OutputParameter op : outList) {
	// if (op.getType() == null && op.getMatchingTypeParamName() != null) {
	// String typePrmName = op.getMatchingTypeParamName();
	// if (staticParams.containsKey(typePrmName)) {
	// op.setType(staticParams.get(typePrmName).getType());
	// } else if (dynamicParams.containsKey(typePrmName)) {
	// String mappedPrmName = dynamicParams.get(typePrmName);
	// op.setType(contextParams.get(mappedPrmName).getType());
	// }
	// }
	// }
	// return outList;
	return cDef.getOutputParamsForInputParams(getInputParameters(wfc));
    }

    public void onPropertyChanged() {
	definitionsStatusMonitor.onDefinitionChanged(currentDefinition);
    }

    public void onEntityChanged(EntityDefinition entity) {
	definitionsStatusMonitor.onDefinitionChanged(entity);
    }

    public void onNextComponentIdChanged() {
	updateWorkflowView(false);
	workflowController.updateComponent(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
    }

    public void onComponentMappingChanged() {
	updateWorkflowView(false);
	workflowController.updateExitPoints(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
	workflowController.updateComponent(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
    }

    public void onExitPointMappingChanged(String exitPointName, String value) {
	String epUiId = currentSelectedComponent.getLocalId() + "." + exitPointName;
	updateWorkflowView(false);
	workflowController.updateExitPoints(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
	workflowController.updateComponent(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
	workflowController.updateExitPointUi(epUiId, value, currentWorkflowGraph);
    }

    public void onExitPointMappingTypeChanged(String exitPointName, String value, boolean isExitPoint) {
	updateWorkflowView(false);
	String epUiId = currentSelectedComponent.getLocalId() + "." + exitPointName;
	if (isExitPoint) {
	    workflowController.addExitPointUi(epUiId, value, currentWorkflowGraph);
	    workflowController.updateExitPoints(currentSelectedComponent.getLocalId(), currentWorkflowGraph);
	} else {
	    workflowController.removeExitPointUi(epUiId, currentWorkflowGraph);
	}
    }

    private void updateWorkflowView(boolean reconstructTree) {
	System.out.println("update workflow view");
	WorkFlowConfiguration workFlowConfig = currentDefinition.getWorkFlowConfig();
	String startCompId = workFlowConfig.getEntryPointId();
	Map<String, WorkFlowComponentConfiguration> componentsConfig = workFlowConfig.getComponents();
	currentWorkflowGraph = createGraphFromWorkflowDefinition(componentsConfig, startCompId);
	workflowGraphs.put(currentDefinition.getId(), currentWorkflowGraph);
	// workflowController.drawGraph(currentWorkflowGraph, currentDefinition, reconstructTree);
	// mainFrame.validate();
	// mainFrame.getWorkflowUi().repaint();
    }

    /* definitions tree listener */
    public void valueChanged(TreeSelectionEvent e) {
	// Object selection = mainFrame.getEntitiesTree().getLastSelectedPathComponent();
	//
	// System.out.println("Selected : " + selection);
	// EntityDefinition currentDef = definitionsMap.get(selection.toString());
	// isWorkflowSelected = false;
	// if (currentDef instanceof WorkFlowDefinition) {
	// setCurrentDefinition((WorkFlowDefinition) currentDef);
	// currentComponentDefinition = null;
	// isWorkflowSelected = true;
	// } else if (currentDef instanceof ComponentDefinition) {
	// currentComponentDefinition = (ComponentDefinition) currentDef;
	// }
	// selectedDefinitionInTree = currentDef;
	// mainFrame.validate();
    }

    private void setCurrentDefinition(WorkFlowDefinition currentDefinition) {
	if (this.currentDefinition == null || !this.currentDefinition.equals(currentDefinition)) {
	    this.currentDefinition = currentDefinition;

	    GraphModel newGraphModel = workflowGraphs.get(currentDefinition.getId());

	    /* see if the graph for this workflow was already generated */
	    if (newGraphModel == null) {
		/* if not generate it */
		WorkFlowConfiguration workFlowConfig = currentDefinition.getWorkFlowConfig();
		if (workFlowConfig != null && workFlowConfig.getEntryPointId() != null) {
		    String startCompId = workFlowConfig.getEntryPointId();
		    Map<String, WorkFlowComponentConfiguration> componentsConfig = workFlowConfig.getComponents();
		    if (componentsConfig != null) {
			WorkFlowComponentConfiguration startCompConfig = componentsConfig.get(startCompId);
			startCompConfig.setLocalId(startCompId);
			newGraphModel = createGraphFromWorkflowDefinition(componentsConfig, startCompId);
		    }
		} else {
		    newGraphModel = new DefaultGraphModel();
		}
		workflowGraphs.put(currentDefinition.getId(), newGraphModel);
	    }
	    currentWorkflowGraph = newGraphModel;
	    workflowController.drawGraph(newGraphModel, currentDefinition, true);
	    showWorkflowControlForm();
	}

    }

    private GraphModel createGraphFromWorkflowDefinition(Map<String, WorkFlowComponentConfiguration> componentsConfig,
	    String startCompId) {
	WorkFlowComponentConfiguration startCompConfig = componentsConfig.get(startCompId);
	GraphModel graphModel = graphFactory.createGraphModel();
	if (startCompConfig != null) {
	    addToGraph(startCompConfig, componentsConfig, graphModel);
	}
	return graphModel;
    }

    private void addToGraph(WorkFlowComponentConfiguration currentComp,
	    Map<String, WorkFlowComponentConfiguration> components, GraphModel graph) {
	String currentCompId = currentComp.getLocalId();
	if (currentComp == null || graph.containsNode(currentCompId)) {
	    return;
	}
	graph.addNode(currentCompId);
	ComponentExitPointsMapping compMapping = currentComp.getComponentMapping();
	String nextCompId = compMapping.getNextComponentId();
	String exitPointPrmName = null;
	WorkFlowComponentConfiguration nextComp = null;
	Map<Object, ComponentExitPoint> mappedValues = null;

	/* if we have a direct connection, add this edge */
	if (nextCompId != null) {
	    nextComp = components.get(nextCompId);
	    nextComp.setLocalId(nextCompId);

	    /* go down on the flow, to add the rest of connections */
	    addToGraph(nextComp, components, graph);
	    /*
	     * add this edge to the graph, without any associated data. It's a direct connection, so there is no rule
	     * for this/
	     */
	    graph.addEdge(currentCompId, nextCompId, null);
	}
	/* we have a mapped runtime context parameter */
	else if ((exitPointPrmName = compMapping.getExitPointPrmName()) != null) {
	    mappedValues = compMapping.getExitValuesMappings();
	} else if (compMapping.getFixedMappings().size() > 0) {
	    mappedValues = compMapping.getFixedMappings();
	}

	if (mappedValues != null) {
	    /* iterate through the mapped connections */
	    for (Map.Entry<Object, ComponentExitPoint> entry : mappedValues.entrySet()) {
		ComponentExitPoint exitPointMapping = entry.getValue();
		String exitPointName = (String) entry.getKey();
		nextCompId = exitPointMapping.getNextComponentId();
		/*
		 * if this exit point is mapped to another component, add this edge to the graph, with the associated
		 * data containing the name of the parameter and the value
		 */
		if (nextCompId != null) {
		    nextComp = components.get(nextCompId);
		    nextComp.setLocalId(nextCompId);

		    /* go down on the flow, to add the rest of connections */
		    addToGraph(nextComp, components, graph);
		    graph.addEdge(currentCompId, nextCompId, new GenericNameValue(exitPointName, null));
		}
		/* else - this is an exit point */
		else {
		    graph.addEdge(currentCompId, currentCompId + "." + exitPointName, new GenericNameValue(
			    exitPointName, exitPointMapping));
		}
	    }
	}

	/* else this is a terminal node */
	// TODO : find a way to make the error mapping visual
	// /* add also the next component in case of error */
	ComponentExitPoint errorMapping = currentComp.getErrorMapping();
	if (errorMapping != null) {
	    nextCompId = errorMapping.getNextComponentId();

	    if (nextCompId != null) {
		nextComp = components.get(nextCompId);
		nextComp.setLocalId(nextCompId);
		/* go down on the flow, to add the rest of connections */
		addToGraph(nextComp, components, graph);
		graph
			.addEdge(currentCompId, nextCompId, new GenericNameValue(SpecialExitPoints.CAUGHT_EXCEPTION,
				null));
	    } else if (errorMapping.getExitPointMapping() != null) {
		graph.addEdge(currentCompId, currentCompId + "." + SpecialExitPoints.CAUGHT_EXCEPTION,
			new GenericNameValue("ERROR", errorMapping));
		System.out.println("wtf: " + currentCompId);

	    }
	}
    }

    public boolean addComponent(Point location) {
	return addComponent(selectedDefinitionInTree.getId(), location);
    }

    public boolean addComponent(String selectedCompId, Point location) {

	ExecutableEntityDefinition selectedDefinition = (ExecutableEntityDefinition) definitionsRepository
		.getDefinitionById(selectedCompId);
	if (selectedDefinition == null) {
	    return false;
	}
	/* create a new workflow component configuration */
	WorkFlowComponentConfiguration newWfCompConfig = new WorkFlowComponentConfiguration();
	ComponentConfiguration compConfig = new ComponentConfiguration();
	compConfig.setComponent(selectedCompId);
	newWfCompConfig.setComponentConfig(compConfig);
	ComponentExitPointsMapping compMappings = new ComponentExitPointsMapping();

	newWfCompConfig.setComponentMapping(compMappings);
	Map<Object, ComponentExitPoint> exitValues = compMappings.getExitValuesMappings();
	Map<String, List<String>> exitParamsValues = selectedDefinition.getPossibleValuesForExitParam();
	if (exitParamsValues != null && exitParamsValues.size() > 0) {
	    for (List<String> vals : exitParamsValues.values()) {
		for (String exitPointName : vals) {
		    ComponentExitPoint ep = new ComponentExitPoint();
		    ep.setExitPointMapping(exitPointName);
		    exitValues.put(exitPointName, ep);
		}
	    }
	}
	/* set the fixed exit points */
	if (selectedDefinition.getFixedExitPoints() != null) {
	    compMappings.setFixedMappings(selectedDefinition.getFixedExitPoints().asMap());
	}

	Map<String, WorkFlowComponentConfiguration> components = currentDefinition.getWorkFlowConfig().getComponents();
	int compIndex = components.size();
	/* make sure the chosen default local id is unique */
	String localId = selectedCompId + "_" + compIndex;
	while (components.containsKey(localId)) {
	    ++compIndex;
	    localId = selectedCompId + "_" + compIndex;
	}
	/* add the new component to the current workflow */
	newWfCompConfig.setLocalId(localId);
	/* if this is the first component in the flow set it as entry point */
	if (components.size() == 0) {
	    currentDefinition.getWorkFlowConfig().setEntryPointId(localId);
	}
	components.put(localId, newWfCompConfig);
	workflowController.addNewComponent(location, newWfCompConfig, currentDefinition);
	setCurrentSelectedComponent(newWfCompConfig);
	return true;
    }

    public CommandResponse executeFlow(ExecuteFlowRequest req) {
	return netcellDao.execute(req);
    }

    public List<String> getDatasourcesTypes() {
	try {
	    return netcellDao.getDefinitionTypes(EntitiesTypes.DATASOURCE);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    public List<String> getTemplates(String type) {
	try {
	    return netcellDao.getTemplates(type);
	} catch (Exception e) {
	    // TODO deal with this
	    e.printStackTrace();
	}
	return null;
    }

    public Map<String, DataSourceDefinition> getDatasources() {
	return (Map) definitionsRepository.getDefinitionsForTypeById(EntitiesTypes.DATASOURCE);
    }

    public String getPackageNameForEntityType(String entityType) {
	if (entityType.equals(selectedEntityType)) {
	    return selectedPackageName;
	}
	return "";
    }

    public Frame getMainFrame() {
	return mainFrame;
    }

    /**
     * 
     * @return {@link ExecutableEntityDefinition}
     */
    public EntityDefinition getSelectedDefinitionInTree() {
	return selectedDefinitionInTree;
    }

    public WorkFlowDefinition getCurrentDefinition() {
	return currentDefinition;
    }

    public void renameWorkflowComponent(String oldId, String newId) {
	currentDefinition.getWorkFlowConfig().renameWorkflowComponent(oldId, newId);
	WorkFlowComponentConfiguration wfc = currentDefinition.getWorkFlowConfig().getComponents().get(newId);
	updateWorkflowView(true);
	workflowController.renameComponent(oldId, wfc, currentWorkflowGraph);
    }

    public static void main(String[] args) throws Exception {

	// JPanel workPane = new JPanel();
	// workPane.setSize(800, 600);
	// workPane.setBackground(Color.white);

	// mainFrame.add(workPane);

	// JArrow a1 = new JArrow(new Point(400,400),new Point(450,300));
	// mainFrame.add(a1);

	// WorkFlowComponentUI wfc1ui = new WorkFlowComponentUI();
	//	
	// workPane.add(wfc1ui);

	// NetcellRmiClient client = new NetcellRmiClient("localhost", 2000, "NetcellRmiReceiver");
	// Command c = new Command();
	// c.setName("get_definitions");
	//
	// CommandResponse cr = client.execute(c);
	//
	// Map definitions = (Map) cr.getValue("definitions");
	//
	// // WorkFlowUI wfui = new WorkFlowUI(definitions);
	// // wfui.setPreferredSize(new Dimension(800, 600));
	// // mainFrame.add(wfui);
	//
	// mainFrame.validate();

	Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	Enumeration keys = UIManager.getDefaults().keys();
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    Object value = UIManager.get(key);
	    if (value instanceof javax.swing.plaf.FontUIResource)
		UIManager.put(key, f);
	}

	NetcellGuiController nc = new NetcellGuiController();
	Toolkit tk = Toolkit.getInstance();
	Tool selectMoveTool = new Tool("Select-Move");
	tk.registerTool(selectMoveTool);
	// selectMoveTool.registerAction("workflowui.WorkFlowUI.MouseEvent." + MouseEvent.MOUSE_PRESSED,
	// new AddComponentAction(nc));

	/* start the controller */
	nc.start();

    }

    private void traceCodes(Collection c) {
	System.out.println("hash codes for " + c);
	for (Object o : c) {
	    System.out.println(o.hashCode());
	}
    }

    class TreeMouseEvent extends MouseAdapter {

	private boolean showMenu(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		createWorkflowDefPopup();
		workflowDefPopup.show((JComponent) e.getSource(), e.getX(), e.getY());
		return true;
	    }
	    return false;
	}

	public void mouseReleased(MouseEvent e) {
	    boolean menuShown = showMenu(e);
	}

	public void mousePressed(MouseEvent e) {
	    // Object path = mainFrame.getEntitiesTree().getLastSelectedPathComponent();
	    TreePath tp = mainFrame.getEntitiesTree().getSelectionPath();
	    if (tp == null) {
		return;
	    }
	    Object[] path = tp.getPath();
	    if (path == null || path.length < 1) {
		return;
	    }
	    int index = path.length - 1;
	    EntityDefinition currentDef = null;
	    String selection = path[index--].toString();
	    currentDef = definitionsRepository.getDefinitionById(selection);
	    while (currentDef == null && index >= 0) {
		String currentNodeName = path[index--].toString();
		/*
		 * if the node specifies a definition type break. We're only interested in the packages names
		 */
		if (definitionsRepository.containsDefinitionsOfType(currentNodeName)) {
		    selectedEntityType = currentNodeName;
		    break;
		}
		selection = currentNodeName + "." + selection;
		if (definitionsRepository.containsDefinitionWithId(selection)) {
		    currentDef = definitionsRepository.getDefinitionById(selection);
		}
	    }
	    System.out.println("Selected : " + selection);
	    selectedDefinitionInTree = currentDef;
	    selectedPackageName = selection;

	    if (e.getClickCount() == 2) {
		/* TODO: refactor this to use some handlers */
		isWorkflowSelected = false;
		if (currentDef instanceof WorkFlowDefinition) {
		    setCurrentDefinition((WorkFlowDefinition) currentDef);
		    currentComponentDefinition = null;
		    isWorkflowSelected = true;
		} else if (currentDef instanceof DataAccessComponentDefinition) {
		    fireDataAccessComponentUpdate(selection);
		} else if (currentDef instanceof ComponentDefinition) {
		    currentComponentDefinition = (ComponentDefinition) currentDef;
		    if (currentDef instanceof ConfigurableComponentDefinition) {
			fireCustomComponentUpdate(selection);
		    }
		} else if (currentDef instanceof DataSourceDefinition) {
		    fireDatasourceUpdate(selection);
		}
		else if(currentDef instanceof ScheduledJobDefinition){
		    fireScheduledJobUpdate(selection);
		}
		mainFrame.validate();
	    } else {
		boolean menuShown = showMenu(e);
	    }
	}
    }
}
