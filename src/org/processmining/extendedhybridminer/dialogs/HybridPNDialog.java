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


public class HybridPNDialog extends JPanel 
{

	JPanel timeoutPanel,orderingStrategyPanel,fTypePanel,placeNumberPanel, stopPanel;
	JCheckBox timeoutBox,placeoutBox;
	JComboBox fType;
	JTextField timeoutField,placeNumberField,inputPlaceField,outputPlaceField,maxPlaceField, stopField;
	
	
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	//private FuzzyCGConfiguration configuration;
	private HybridPNMinerSettings settings;

	public HybridPNDialog(UIPluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings)
	{
		//super(new GridLayout(6,2,0,10));
		super(new GridLayout(3, 2,80,30));
		super.setSize(WIDTH, HEIGHT/2);
		this.settings = settings;
		
		
		/**
		 * Additional code by sabi
		 */
		/*timeoutPanel=new JPanel();
		timeoutPanel=new JPanel(new GridLayout(1,1,10,10));
		timeoutPanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		JLabel timeoutLabel=new JLabel();
		timeoutLabel.setText("<html>Time bound (minutes):</html>");
		timeoutField=new JTextField(10);
		int timeout = settings.getMaxTime();
		timeoutField.setText(String.valueOf(timeout/60));
		timeoutField.getDocument().addDocumentListener(new DocumentListener()
				{
					public void changedUpdate(DocumentEvent e){ check(); }
					public void removeUpdate(DocumentEvent e) { check(); }
					public void insertUpdate(DocumentEvent e) { check(); }
					private void check()
					{
						String value=timeoutField.getText().trim();
						settings.setMaxTime(Integer.parseInt(value.trim())*60);
					}
				});
		timeoutPanel.add(timeoutField);
		
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.gridx = 0;
		c1.gridy = 0;
		c1.ipady = 20;
		this.add(timeoutLabel, c1);
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.gridx = 1;
		c2.gridy = 0;
		c2.ipady = 20;
		c2.gridwidth = 2;
		this.add(timeoutPanel, c2);
		
//		this.add(timeoutLabel);
//		this.add(timeoutPanel);

		placeNumberPanel=new JPanel(new GridLayout(1,1,10,10));
		placeNumberPanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		JLabel placeNumberLabel=new JLabel();
		placeNumberLabel.setText("<html>Place size bound:</html>");
		placeNumberField=new JTextField(10);
		int placeNumber = settings.getPlaceNumber();
		placeNumberField.setText(String.valueOf(placeNumber));

		placeNumberField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e){ check(); }
			public void removeUpdate(DocumentEvent e) { check(); }
			public void insertUpdate(DocumentEvent e) { check(); }
			private void check()
			{
				String value=placeNumberField.getText();
				if(value.trim().matches("^[0-9]*$"))
				{
					settings.setPlaceNumber(Integer.parseInt(value));
				}
			}
		});
		//placeNumberPanel.add(new JLabel());		
		//placeNumberPanel.add(new JLabel());
		//placeNumberPanel.add(new JLabel());
		placeNumberPanel.add(placeNumberField);
		//placeNumberPanel.add(new JLabel());
		//placeNumberPanel.add(new JLabel());
		//placeNumberPanel.add(new JLabel());
		
		c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.HORIZONTAL;
		//c1.weightx = 2;
		c1.gridx = 0;
		c1.gridy = 1;
		c1.ipady = 20;
		this.add(placeNumberLabel, c1);
		
		c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.HORIZONTAL;
		//c2.weightx = 2;
		c2.gridx = 1;
		c2.gridy = 1;
		c2.ipady = 20;
		c2.gridwidth = 2;
		this.add(placeNumberPanel, c2);		
		*/
		
		stopPanel=new JPanel(new GridLayout(1,1,10,10));
		stopPanel.setBorder(new EmptyBorder(20, 20, 20, 20) );
		JLabel stopLabel=new JLabel();
		stopLabel.setText("<html><b><font size= '+1'>Threshold for early cancellation of place iterator:</font></b><br>after x consecutive rejected places, the place iterator is canceled.</html>");
		stopField=new JTextField(10);
		int stopNumber = settings.getThresholdEarlyCancelationIterator();
		stopField.setText(String.valueOf(stopNumber));

		stopField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e){ check(); }
			public void removeUpdate(DocumentEvent e) { check(); }
			public void insertUpdate(DocumentEvent e) { check(); }
			private void check()
			{
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
//		conflictsStrategy.addItem("Default conflict strategy");
//		conflictsStrategy.addItem("Default conflict strategy (optimised)");
		//conflictsStrategy.setSelectedIndex(1);
		//conflictsStrategy.addItem("No conflict strategy");
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
		//this.add(conflictsLabel);
		//this.add(conflictsPanel);
		this.add(conflictsLabel);
		
		this.add(fTypePanel);
		
		//Component space = Box.createHorizontalStrut(10);
		
		final JLabel placeEvaluationThresholdLabel = new JLabel();
		placeEvaluationThresholdLabel.setText("<html><b><font size= '+1'>Fitness threshold for the place evaluation method</font></b></html>");
		//this.add(placeEvaluationThresholdLabel);

		
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
		
//		int intValue = (int) (settings.getPlaceEvalThreshold()*100);
//		placeEvaluationThresholdSlider.setValue(intValue);
		placeEvaluationThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setPlaceEvalThreshold(placeEvaluationThresholdSlider.getValue()/100.0);
			}
		});
		//this.add(placeEvaluationThresholdSlider);		
		
		this.add(placeEvaluationThresholdLabel);
		
		this.add(placeEvaluationThresholdSlider);
		

				

	}
	

}
