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
package net.segoia.netcell.gui.forms;

import java.util.HashMap;
import java.util.Map;

import net.segoia.cfgengine.core.configuration.ConfigurationManager;
import net.segoia.cfgengine.core.exceptions.ConfigurationException;
import net.segoia.cfgengine.util.PackageCfgLoader;
import net.segoia.java.forms.DefaultFormFactory;
import net.segoia.java.forms.Form;
import net.segoia.java.forms.FormConfig;
import net.segoia.java.forms.FormFactory;
import net.segoia.java.forms.swing.SwingFormUiFactory;
import net.segoia.netcell.gui.NetcellGuiController;
import net.segoia.netcell.vo.configurations.WorkFlowComponentConfiguration;
import net.segoia.netcell.vo.definitions.WorkFlowDefinition;

public class FormManager {
    private Map<String, FormHandler<?>> handlers;
    private FormHandler<Object> defaultHandler;
    private ConfigurationManager formsConfigManager;
    private FormFactory formFactory = new DefaultFormFactory(new SwingFormUiFactory());
    private NetcellGuiController controller;
    
    public FormManager(){
	init();
    }
    
    public void init(){
	defaultHandler = new DefaultFormHandler(this);
	initFormsConfigManager();
	handlers = new HashMap<String, FormHandler<?>>();
	handlers.put(WorkFlowComponentConfiguration.class.getName(), new WorkflowComponentConfigFormHandler(this));
	handlers.put(WorkFlowDefinition.class.getName(), new WorkflowDefinitionFormHandler(this));
    }
    
    private void initFormsConfigManager() {
	try {
	    formsConfigManager = PackageCfgLoader.getInstance().load("xmls/cfg/java-forms-handlers.xml",
		    "xmls/ui-config-entrypoint.xml");
	} catch (ConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public FormConfig getFormConfig(String formId) {
	return (FormConfig) formsConfigManager.getObjectById(formId);
    }

    public Form createFormForId(String formId) {
	return createFormFromConfig(getFormConfig(formId));
    }

    public Form createFormFromConfig(FormConfig fc) {
	return formFactory.createForm(fc);
    }
    
    public Form createFormForObject(Object o){
	FormHandler<Object> handler = (FormHandler<Object>)handlers.get(o.getClass().getName());
	if(handler != null){
	    return handler.createForm(o);
	}
	return defaultHandler.createForm(o);
    }
    
    public Form createFormForObject(Object o, String formIdentifier){
	String formName = o.getClass().getName()+"."+formIdentifier;
	FormHandler<Object> handler = (FormHandler<Object>)handlers.get(formName);
	if(handler != null){
	    return handler.createForm(o);
	}
	return defaultHandler.createForm(o,formName);
    }

    public Map<String, FormHandler<?>> getHandlers() {
	return handlers;
    }

    public ConfigurationManager getFormsConfigManager() {
	return formsConfigManager;
    }

    public FormFactory getFormFactory() {
	return formFactory;
    }

    public void setHandlers(Map<String, FormHandler<?>> handlers) {
	this.handlers = handlers;
    }

    public void setFormsConfigManager(ConfigurationManager formsConfigManager) {
	this.formsConfigManager = formsConfigManager;
    }

    public void setFormFactory(FormFactory formFactory) {
	this.formFactory = formFactory;
    }

    public NetcellGuiController getController() {
	return controller;
    }

    public void setController(NetcellGuiController controller) {
	this.controller = controller;
    }

}
