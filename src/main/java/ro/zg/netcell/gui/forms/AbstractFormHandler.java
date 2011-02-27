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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFormHandler<O> extends BaseFormHandler<O>{
    protected Map<String,FormHandler<O>> subHandlers = new HashMap<String, FormHandler<O>>();
    protected FormHandler<O> defaultHandler;
    
    public AbstractFormHandler(){
	
    }
    
    public AbstractFormHandler(FormManager fm){
	super(fm);
    }
    
    public Map<String, FormHandler<O>> getSubHandlers() {
        return subHandlers;
    }

    public void setSubHandlers(Map<String, FormHandler<O>> subHandlers) {
        this.subHandlers = subHandlers;
    }

    public FormHandler<O> getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(FormHandler<O> defaultHandler) {
        this.defaultHandler = defaultHandler;
    }
    
    
    
}
