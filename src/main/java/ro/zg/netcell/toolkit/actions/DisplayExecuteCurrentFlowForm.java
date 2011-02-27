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
package ro.zg.netcell.toolkit.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import ro.zg.java.forms.Form;
import ro.zg.netcell.control.CommandResponse;
import ro.zg.netcell.gui.NetcellGuiController;
import ro.zg.netcell.gui.vo.ExecuteFlowRequest;
import ro.zg.netcell.vo.definitions.WorkFlowDefinition;
import ro.zg.util.data.NameValue;
import ro.zg.util.data.ObjectsUtil;

public class DisplayExecuteCurrentFlowForm extends ThreadedNetcellAbstractAction {

    public DisplayExecuteCurrentFlowForm(NetcellGuiController controller) {
	super(controller);
	// TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = -3333246179670168388L;

    public void actionPerformedDelegate(ActionEvent e) throws Exception {
	ExecuteFlowRequest request = new ExecuteFlowRequest();
	WorkFlowDefinition wfDef = controller.getCurrentDefinition();
	request.setFlowId(wfDef.getId());
	request.setInputParameters((List) ObjectsUtil.copy(wfDef.getInputParameters()));

	JTextArea output = new JTextArea();
	output.setEditable(false);
	output.setLineWrap(true);
	output.setColumns(5);

	JScrollPane outputScrollPane = new JScrollPane(output);

	Form form = controller.getFormForObject(request);
	form.getModel().setActionListener(new ExecuteAction(output, request));
	form.initialize();

	JComponent content = (JComponent) form.getUi().getHolder();
	JFrame jf = new JFrame("Execute flow " + wfDef.getId());
	JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, content, outputScrollPane);

	jf.add(sp);

	jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	jf.setSize(new Dimension(content.getPreferredSize().width + 20, 600));
	jf.setVisible(true);
    }

    class ExecuteAction implements ActionListener {
	private JTextArea output;
	ExecuteFlowRequest request;

	public ExecuteAction(JTextArea output, ExecuteFlowRequest request) {
	    this.output = output;
	    this.request = request;
	}

	public void actionPerformed(final ActionEvent e) {
	    output.setText("running..");
	    Thread t = new Thread() {
		public void run() {
		    ((JButton)e.getSource()).setEnabled(false);
		    CommandResponse cr = controller.executeFlow(request);
		    String text = cr.getResponseCode();
		    for (NameValue<Object> nv : cr.getParametersAsList()) {
			text += "\n";
			text += nv.getName() + "=" + nv.getValue();
		    }
		    output.setText(text);
		    System.out.println("Finished executing");
		    ((JButton)e.getSource()).setEnabled(true);
		}
	    };
	    t.start();
	}

    }

}
