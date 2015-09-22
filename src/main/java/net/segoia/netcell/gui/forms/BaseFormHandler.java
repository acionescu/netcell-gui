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

import net.segoia.java.forms.Form;

public abstract class BaseFormHandler<O> implements FormHandler<O> {
    private FormManager formManager;
    
    public BaseFormHandler(){
	
    }
    
    public BaseFormHandler(FormManager fm){
	formManager = fm;
    }
    protected Form createFormForObject(O target) {
	Form f = formManager.createFormForId(target.getClass().getName());
	f.setDataObject(target);
	return f;
    }
    
    protected Form createFormForObject(O target, String formName) {
	Form f = formManager.createFormForId(formName);
	f.setDataObject(target);
	return f;
    }
    
    public Form createForm(O target){
	return createFormForObject(target);
    }
    
    public Form createForm(O target, String formName){
	return createFormForObject(target, formName);
    }

    public FormManager getFormManager() {
	return formManager;
    }

    public void setFormManager(FormManager formManager) {
	this.formManager = formManager;
    }

}
