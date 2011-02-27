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
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import ro.zg.netcell.vo.definitions.WorkFlowDefinition;
import ro.zg.util.data.reflection.ReflectionUtility;
import ro.zg.util.gui.annotations.UiProperty;


public class AnnotationsTest {
    public static void main(String[] args) throws Exception{
	
	Annotation[] a  = ReflectionUtility.getFieldForFieldName(WorkFlowDefinition.class, "id").getAnnotations();
	List<Annotation> al = Arrays.asList(a);
	System.out.println( ((UiProperty)a[0]).type());
    }
}
