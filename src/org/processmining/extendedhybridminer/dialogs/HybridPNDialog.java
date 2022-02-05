package org.processmining.extendedhybridminer.dialogs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.hybridpetrinet.FitnessType;
import org.processmining.extendedhybridminer.plugins.HybridPNMinerSettings;


public class HybridPNDialog extends JPanel {

	JPanel timeoutPanel,orderingStrategyPanel,fTypePanel,placeNumberPanel, stopPanel;
	JCheckBox timeoutBox,placeoutBox;
	JComboBox fType;
	JTextField timeoutField,placeNumberField,inputPlaceField,outputPlaceField,maxPlaceField, stopField;
	
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	private HybridPNMinerSettings settings;

	public HybridPNDialog(UIPluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings) {
		
		super(new GridLayout(3, 2,80,30));
		super.setSize(WIDTH, HEIGHT/2);
		this.settings = settings;
		
		stopPanel=new JPanel(new GridLayout(1,1,10,10));
		stopPanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		JLabel stopLabel=new JLabel();
		stopLabel.setText("<html><b><font size= '+1'>Threshold for early cancellation of place iterator:</font></b><br>after x consecutive rejected places, the place iterator is canceled.</html>");
		stopField=new JTextField(10);
		int stopNumber = settings.getThresholdEarlyCancelationIterator();
		stopField.setText(String.valueOf(stopNumber));
		stopField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e){ check(); }
			public void removeUpdate(DocumentEvent e) { check(); }
			public void insertUpdate(DocumentEvent e) { check(); }
			private void check() {
				String value=stopField.getText();
				settings.setThresholdEarlyCancelationIterator(Integer.parseInt(value));
			}
		});
		stopPanel.add(stopField);
		this.add(stopLabel);
		this.add(stopPanel);	
		
		fTypePanel=new JPanel(new GridLayout(1,1,10,10));	
		fTypePanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		JLabel conflictsLabel=new JLabel();		
		conflictsLabel.setText("<html><b><font size= '+1'>Place evaluation method</font></b></html>");
		fType = new JComboBox();
		int idx = 1;
		switch(settings.getFitnessType()){
			case LOCAL:
				idx =0;
				break;
			case GLOBAL:
				idx =1;
				break;
		}
		fType.addItem("local evaluation");
		fType.addItem("local evaluation with global fitness guarantee");
		fType.setSelectedIndex(idx);
		fType.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							switch(((JComboBox)e.getSource()).getSelectedIndex())
							{
								case 0:
									settings.setFitnessType(FitnessType.LOCAL);
									break;
								case 1:
									settings.setFitnessType(FitnessType.GLOBAL);
									break;
								default: 
									System.out.println("wrong fitness Type");
									break;								
							}
						}
					});
		fTypePanel.add(conflictsLabel);
		fTypePanel.add(fType);
		this.add(conflictsLabel);
		this.add(fTypePanel);
		
		final JLabel placeEvaluationThresholdLabel = new JLabel();
		placeEvaluationThresholdLabel.setText("<html><b><font size= '+1'>Fitness threshold for the place evaluation method</font></b></html>");
		final JSlider placeEvaluationThresholdSlider = new JSlider();
		placeEvaluationThresholdSlider.setBorder(new EmptyBorder(20, 20, 20, 20) );
		placeEvaluationThresholdSlider.setMinimum(0);
		placeEvaluationThresholdSlider.setMaximum(100);
		placeEvaluationThresholdSlider.setMajorTickSpacing(50);
		placeEvaluationThresholdSlider.setMinorTickSpacing(10);
		placeEvaluationThresholdSlider.setPaintTicks(true);
		placeEvaluationThresholdSlider.setValue(90);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0%"));
		labelTable.put(50, new JLabel("50%"));
		labelTable.put(100, new JLabel("100%"));
		placeEvaluationThresholdSlider.setLabelTable(labelTable);
		placeEvaluationThresholdSlider.setPaintLabels(true);
		placeEvaluationThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setPlaceEvalThreshold(placeEvaluationThresholdSlider.getValue()/100.0);
			}
		});
		this.add(placeEvaluationThresholdLabel);
		this.add(placeEvaluationThresholdSlider);
	}

}
