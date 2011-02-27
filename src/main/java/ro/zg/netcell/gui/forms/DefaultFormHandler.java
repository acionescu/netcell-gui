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
package ro.zg.netcell.gui.forms;

import ro.zg.java.forms.Form;

public class DefaultFormHandler extends BaseFormHandler<Object>{

    public DefaultFormHandler(){
	
    }
    
    public DefaultFormHandler(FormManager fm){
	super(fm);
    }
    
    public Form createForm(Object dataObject) {
	Form form = createFormForObject(dataObject);
//	try {
//	    form.initialize();
//	} catch (Exception e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
	return form;
    }
    
    public Form createForm(Object dataObject, String formName) {
	Form form = createFormForObject(dataObject,formName);
//	try {
//	    form.initialize();
//	} catch (Exception e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
	return form;
    }

}
