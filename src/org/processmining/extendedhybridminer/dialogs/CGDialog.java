package org.processmining.extendedhybridminer.dialogs;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.extendedhybridminer.plugins.HybridCGMinerSettings;


public class CGDialog extends JPanel {

	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	private HybridCGMinerSettings settings;

	public CGDialog(UIPluginContext context, XLog log, 
			final HybridCGMinerSettings settings) {
		
		super(new GridLayout(6,2,80,30));
		this.settings = settings;
		
		final JLabel positiveObservationsLabel = new JLabel();
		positiveObservationsLabel.setText("<html><b><font size= '+1'>Minimal activity frequency:</font></b><br>an activitiy will be included if it occurs in at least x% of cases; <br>set to 0 to include all activities.</html>");
		this.add(positiveObservationsLabel);
		
		final JSlider positiveObservationsSlider = new JSlider();
		positiveObservationsSlider.setMinimum(0);
		positiveObservationsSlider.setMaximum(100);
		int positiveObservationsValue = (int) (settings.getFilterAcivityThreshold() * 100);
		positiveObservationsSlider.setValue(positiveObservationsValue);
		positiveObservationsSlider.setMajorTickSpacing(50);
		positiveObservationsSlider.setMinorTickSpacing(10);
		positiveObservationsSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> activitylabelTable = new Hashtable<Integer, JLabel>();
		activitylabelTable.put(positiveObservationsValue, new JLabel(positiveObservationsValue +"%"));
		positiveObservationsSlider.setLabelTable(activitylabelTable);
		positiveObservationsSlider.setPaintLabels(true);
		positiveObservationsSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = positiveObservationsSlider.getValue();
			    Hashtable<Integer, JLabel> newTable = new Hashtable<Integer, JLabel>();
				newTable.put(value, new JLabel(value +"%"));
				positiveObservationsSlider.setLabelTable(newTable);
				settings.setFilterAcivityThreshold(value / 100.0);
			}
		});
		this.add(positiveObservationsSlider);
		
		Component space = Box.createVerticalStrut(20);
		this.add(space);

		space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JLabel traceFilterLabel = new JLabel();
		traceFilterLabel.setText("<html><b><font size= '+1'>Minimal trace variant frequency:</font></b><br>a trace variant will be included if it covers at least x% of cases; <br>set to 0 to include all trace variants.</html>");
		this.add(traceFilterLabel);
		
		final JSlider traceFilterSlider = new JSlider();
		traceFilterSlider.setMinimum(0);
		traceFilterSlider.setMaximum(100);
		int traceFilterValue = (int) (settings.getTraceVariantsThreshold() *100);
		traceFilterSlider.setValue(traceFilterValue);
		traceFilterSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = traceFilterSlider.getValue();
			    Hashtable<Integer, JLabel> newTable = new Hashtable<Integer, JLabel>();
				newTable.put(value, new JLabel(value +"%"));
				traceFilterSlider.setLabelTable(newTable);
				settings.setTraceVariantsThreshold(value/100.0);
			}
		});
		traceFilterSlider.setMajorTickSpacing(50);
		traceFilterSlider.setMinorTickSpacing(10);
		traceFilterSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> tracelabelTable = new Hashtable<Integer, JLabel>();
		tracelabelTable.put(traceFilterValue, new JLabel(traceFilterValue +"%"));
		traceFilterSlider.setLabelTable(tracelabelTable);
		traceFilterSlider.setPaintLabels(true);
		this.add(traceFilterSlider);
		
		space = Box.createVerticalStrut(20);
		this.add(space);

		space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JLabel sureThresholdLabel = new JLabel();
		sureThresholdLabel.setText("<html><b><font size= '+1'>Strong causality threshold:</font></b><br>lower bound for a strong causality relation between two activities.</html>");
		this.add(sureThresholdLabel);
		
		final JSlider sureThresholdSlider = new JSlider();
		sureThresholdSlider.setMinimum(0);
		sureThresholdSlider.setMaximum(100);
		int sureValue = (int) (settings.getSureThreshold()*100);
		sureThresholdSlider.setValue(sureValue);
		sureThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = sureThresholdSlider.getValue();
			    Hashtable<Integer, JLabel> newTable = new Hashtable<Integer, JLabel>();
				newTable.put(value, new JLabel(value +"%"));
				sureThresholdSlider.setLabelTable(newTable);
				settings.setSureThreshold(value/100.0);
			}
		});
		sureThresholdSlider.setMajorTickSpacing(50);
		sureThresholdSlider.setMinorTickSpacing(10);
		sureThresholdSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> surelabelTable = new Hashtable<Integer, JLabel>();
		surelabelTable.put(sureValue, new JLabel(sureValue +"%"));
		sureThresholdSlider.setLabelTable(surelabelTable);
		sureThresholdSlider.setPaintLabels(true);
		this.add(sureThresholdSlider);
		
		space = Box.createVerticalStrut(20);
		this.add(space);
		
		space = Box.createHorizontalStrut(20);
		this.add(space);

		final JLabel questionMarkThresholdLabel = new JLabel();
		questionMarkThresholdLabel.setText("<html><b><font size= '+1'>Weak causality threshold:</font></b><br>lower bound for a weak causality relation between two activities; <br>set to 100% to avoid uncertain edges.</html>");
		this.add(questionMarkThresholdLabel);

		final JSlider questionMarkThresholdSlider = new JSlider();
		questionMarkThresholdSlider.setMinimum(0);
		questionMarkThresholdSlider.setMaximum(100);
		questionMarkThresholdSlider.setMajorTickSpacing(50);
		questionMarkThresholdSlider.setMinorTickSpacing(10);
		questionMarkThresholdSlider.setPaintTicks(true);
		int unsureValue = (int) (settings.getQuestionMarkThreshold()*100);
		questionMarkThresholdSlider.setValue(unsureValue);
		Hashtable<Integer, JLabel> unsureLabelTable = new Hashtable<Integer, JLabel>();
		unsureLabelTable.put(unsureValue, new JLabel(unsureValue +"%"));
		questionMarkThresholdSlider.setLabelTable(unsureLabelTable);
		questionMarkThresholdSlider.setPaintLabels(true);
		this.add(questionMarkThresholdSlider);
		
		questionMarkThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = questionMarkThresholdSlider.getValue();
			    Hashtable<Integer, JLabel> newTable = new Hashtable<Integer, JLabel>();
				newTable.put(value, new JLabel(value +"%"));
				questionMarkThresholdSlider.setLabelTable(newTable);
				settings.setQuestionMarkThreshold(value/100.0);
			}
		});
		this.add(questionMarkThresholdSlider);
		
		space = Box.createVerticalStrut(20);
		this.add(space);
		
		space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JLabel longDepThresholdLabel = new JLabel();
		longDepThresholdLabel.setText("<html><b><font size= '+1'>Long-term dependency threshold:</font></b><br>lower bound for a strong long-term causality relation between two activities.</html>");
		this.add(longDepThresholdLabel);

		final JSlider longDepThresholdSlider = new JSlider();
		longDepThresholdSlider.setMinimum(0);
		longDepThresholdSlider.setMaximum(100);
		longDepThresholdSlider.setMajorTickSpacing(50);
		longDepThresholdSlider.setMinorTickSpacing(10);
		longDepThresholdSlider.setPaintTicks(true);
		int longDepValue = (int) (settings.getLongDepThreshold()*100);
		longDepThresholdSlider.setValue(longDepValue);
		Hashtable<Integer, JLabel> longDepLabelTable = new Hashtable<Integer, JLabel>();
		longDepLabelTable.put(longDepValue, new JLabel(longDepValue +"%"));
		longDepThresholdSlider.setLabelTable(longDepLabelTable);
		longDepThresholdSlider.setPaintLabels(true);
		this.add(longDepThresholdSlider);
		
		longDepThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = longDepThresholdSlider.getValue();
			    Hashtable<Integer, JLabel> newTable = new Hashtable<Integer, JLabel>();
				newTable.put(value, new JLabel(value +"%"));
				longDepThresholdSlider.setLabelTable(newTable);
				settings.setLongDepThreshold(value/100.0);
			}
		});
		this.add(longDepThresholdSlider);

		space = Box.createVerticalStrut(20);
		this.add(space);
		
		space = Box.createHorizontalStrut(10);
		this.add(space);

		final JLabel causalityWeightLabel = new JLabel();
		causalityWeightLabel.setText("<html><b><font size= '+1'>Causality weight threshold:</font></b><br>high values mean more emphasis on the split and join behavior of activities; <br>low values mean more emphasis on the detection of concurrency and loops.</html>");
		this.add(causalityWeightLabel);

		final JSlider causalityWeightSlider = new JSlider();
		causalityWeightSlider.setMinimum(0);
		causalityWeightSlider.setMaximum(100);
		causalityWeightSlider.setMajorTickSpacing(50);
		causalityWeightSlider.setMinorTickSpacing(10);
		causalityWeightSlider.setPaintTicks(true);
		int causalityValue = (int) (settings.getCausalityWeight()*100);
		Hashtable<Integer, JLabel> causalitylabelTable = new Hashtable<Integer, JLabel>();
		causalitylabelTable.put(causalityValue, new JLabel(causalityValue +"%"));
		causalityWeightSlider.setLabelTable(causalitylabelTable);
		causalityWeightSlider.setPaintLabels(true);
		this.add(causalityWeightSlider);
		
		causalityWeightSlider.setValue(causalityValue);
		causalityWeightSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = causalityWeightSlider.getValue();
			    Hashtable<Integer, JLabel> newTable = new Hashtable<Integer, JLabel>();
				newTable.put(value, new JLabel(value +"%"));
				causalityWeightSlider.setLabelTable(newTable);
				settings.setCausalityWeight(value/100.0);
			}
		});
		this.add(causalityWeightSlider);

		space = Box.createVerticalStrut(20);
		this.add(space);
	}
	
}
