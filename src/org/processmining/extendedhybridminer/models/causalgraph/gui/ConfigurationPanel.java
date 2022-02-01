package org.processmining.extendedhybridminer.models.causalgraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.processmining.framework.util.ui.scalableview.VerticalLabelUI;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Operator;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Split;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Stats;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

class ConfigurationPanel extends JPanel {

	private static final long serialVersionUID = 8415559591750873767L;
	private final JSlider sureThresholdSlider, unsureThresholdSlider, longDepThresholdSlider, causalityWeightSlider;
	private JLabel sureSliderMinValue, sureSliderMaxValue, sureSliderFitValue, sureSliderValue, sureSliderLabelValue;
	private JLabel unsureSliderMinValue, unsureSliderMaxValue, unsureSliderFitValue, unsureSliderValue, unsureSliderLabelValue;
	private JLabel longDepSliderMinValue, longDepSliderMaxValue, longDepSliderFitValue, longDepSliderValue, longDepSliderLabelValue;
	private JLabel causalityWeightSliderMinValue, causalityWeightSliderMaxValue, causalityWeightSliderFitValue, causalityWeightSliderValue, causalityWeightSliderLabelValue;


	private int sureCurrValue;
	private int unsureCurrValue;
	private int longDepCurrValue;
	private int causalityWeightCurrValue;

	public ConfigurationPanel(SlickerFactory factory, SlickerDecorator decorator,
			int sureCurrValue, int unsureCurrValue, int longDepCurrValue, int causalityWeightCurrValue) {

		super(null);

		int maximumValue = 100;
		
		this.sureCurrValue = sureCurrValue;
		this.unsureCurrValue = unsureCurrValue;
		this.causalityWeightCurrValue = causalityWeightCurrValue;
		this.longDepCurrValue = longDepCurrValue;

		this.sureThresholdSlider = factory.createSlider(1);
		this.unsureThresholdSlider = factory.createSlider(1);
		this.longDepThresholdSlider = factory.createSlider(1);
		this.causalityWeightSlider = factory.createSlider(1);
		
		sureThresholdSlider.setName("SureSlider");
		unsureThresholdSlider.setName("UnsureSlider");
		longDepThresholdSlider.setName("longDepSlider");
		causalityWeightSlider.setName("CausalityWeightSlider");
		
		this.sureThresholdSlider.setMinimum(0);
		this.sureThresholdSlider.setMaximum(maximumValue);
		this.sureThresholdSlider.setValue(sureCurrValue);

		this.unsureThresholdSlider.setMinimum(0);
		this.unsureThresholdSlider.setMaximum(maximumValue);
		this.unsureThresholdSlider.setValue(unsureCurrValue);
		
		this.longDepThresholdSlider.setMinimum(0);
		this.longDepThresholdSlider.setMaximum(maximumValue);
		this.longDepThresholdSlider.setValue(longDepCurrValue);
		
		this.causalityWeightSlider.setMinimum(0);
		this.causalityWeightSlider.setMaximum(maximumValue);
		this.causalityWeightSlider.setValue(causalityWeightCurrValue);

		
		this.sureThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateSureSlider();
			}
		});
		
		this.unsureThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateUnsureSlider();
			}
		});
		
		this.longDepThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateLongDepSlider();
			}
		});
		
		this.causalityWeightSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateCausalityWeightSlider();
			}
		});

		this.sureSliderMinValue = factory.createLabel("0%");
		this.sureSliderMaxValue = factory.createLabel(maximumValue + "%");
		this.sureSliderFitValue = factory.createLabel("Def >");
		this.sureSliderValue = factory.createLabel(sureCurrValue + "%");
		this.sureSliderLabelValue = factory.createLabel("Strong");

		this.sureSliderMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.sureSliderMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.sureSliderFitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		this.sureSliderValue.setHorizontalAlignment(SwingConstants.LEFT);
		this.sureSliderLabelValue.setHorizontalAlignment(SwingConstants.CENTER);


		this.sureSliderMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sureSliderMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sureSliderFitValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sureSliderValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sureSliderLabelValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.sureSliderMinValue.setForeground(Color.GRAY);
		this.sureSliderMaxValue.setForeground(Color.GRAY);
		this.sureSliderFitValue.setForeground(Color.GRAY);
		this.sureSliderValue.setForeground(Color.DARK_GRAY);
		this.sureSliderLabelValue.setForeground(Color.GRAY);

		this.add(this.sureThresholdSlider);
		this.add(this.sureSliderMinValue);
		this.add(this.sureSliderMaxValue);
		this.add(this.sureSliderFitValue);
		this.add(this.sureSliderValue);
		this.add(this.sureSliderLabelValue);
		
		this.unsureSliderMinValue = factory.createLabel("0%");
		this.unsureSliderMaxValue = factory.createLabel(maximumValue + "%");
		this.unsureSliderFitValue = factory.createLabel("Def >");
		this.unsureSliderValue = factory.createLabel(unsureCurrValue + "%");
		this.unsureSliderLabelValue = factory.createLabel("Weak");

		

		this.unsureSliderMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.unsureSliderMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.unsureSliderFitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		this.unsureSliderValue.setHorizontalAlignment(SwingConstants.LEFT);
		this.unsureSliderLabelValue.setHorizontalAlignment(SwingConstants.CENTER);


		this.unsureSliderMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.unsureSliderMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.unsureSliderFitValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.unsureSliderValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.unsureSliderLabelValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.unsureSliderMinValue.setForeground(Color.GRAY);
		this.unsureSliderMaxValue.setForeground(Color.GRAY);
		this.unsureSliderFitValue.setForeground(Color.GRAY);
		this.unsureSliderValue.setForeground(Color.DARK_GRAY);
		this.unsureSliderLabelValue.setForeground(Color.GRAY);

		this.add(this.unsureThresholdSlider);
		this.add(this.unsureSliderMinValue);
		this.add(this.unsureSliderMaxValue);
		this.add(this.unsureSliderFitValue);
		this.add(this.unsureSliderValue);
		this.add(this.unsureSliderLabelValue);
		
		this.longDepSliderMinValue = factory.createLabel("0%");
		this.longDepSliderMaxValue = factory.createLabel(maximumValue + "%");
		this.longDepSliderFitValue = factory.createLabel("Def >");
		this.longDepSliderValue = factory.createLabel(longDepCurrValue + "%");
		this.longDepSliderLabelValue = factory.createLabel("Long-term");

		

		this.longDepSliderMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.longDepSliderMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.longDepSliderFitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		this.longDepSliderValue.setHorizontalAlignment(SwingConstants.LEFT);
		this.longDepSliderLabelValue.setHorizontalAlignment(SwingConstants.CENTER);


		this.longDepSliderMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.longDepSliderMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.longDepSliderFitValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.longDepSliderValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.longDepSliderLabelValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.longDepSliderMinValue.setForeground(Color.GRAY);
		this.longDepSliderMaxValue.setForeground(Color.GRAY);
		this.longDepSliderFitValue.setForeground(Color.GRAY);
		this.longDepSliderValue.setForeground(Color.DARK_GRAY);
		this.longDepSliderLabelValue.setForeground(Color.GRAY);

		this.add(this.longDepThresholdSlider);
		this.add(this.longDepSliderMinValue);
		this.add(this.longDepSliderMaxValue);
		this.add(this.longDepSliderFitValue);
		this.add(this.longDepSliderValue);
		this.add(this.longDepSliderLabelValue);
		
		this.causalityWeightSliderMinValue = factory.createLabel("0%");
		this.causalityWeightSliderMaxValue = factory.createLabel(maximumValue + "%");
		this.causalityWeightSliderFitValue = factory.createLabel("Def >");
		this.causalityWeightSliderValue = factory.createLabel(causalityWeightCurrValue + "%");
		this.causalityWeightSliderLabelValue = factory.createLabel("Causality");

		this.causalityWeightSliderMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.causalityWeightSliderMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.causalityWeightSliderFitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		this.causalityWeightSliderValue.setHorizontalAlignment(SwingConstants.LEFT);
		this.causalityWeightSliderLabelValue.setHorizontalAlignment(SwingConstants.CENTER);


		this.causalityWeightSliderMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.causalityWeightSliderMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.causalityWeightSliderFitValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.causalityWeightSliderValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.causalityWeightSliderLabelValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.causalityWeightSliderMinValue.setForeground(Color.GRAY);
		this.causalityWeightSliderMaxValue.setForeground(Color.GRAY);
		this.causalityWeightSliderFitValue.setForeground(Color.GRAY);
		this.causalityWeightSliderValue.setForeground(Color.DARK_GRAY);
		this.causalityWeightSliderLabelValue.setForeground(Color.GRAY);

		this.add(this.causalityWeightSlider);
		this.add(this.causalityWeightSliderMinValue);
		this.add(this.causalityWeightSliderMaxValue);
		this.add(this.causalityWeightSliderFitValue);
		this.add(this.causalityWeightSliderValue);
		this.add(this.causalityWeightSliderLabelValue);

		this.setBackground(Color.LIGHT_GRAY);
	}
	
	
	
	

	public void setHeight(int height) {

		//this.setSize(115, height);
		//this.setSize(190, height);
		this.setSize(320, height);

		int sliderHeight = height - 60;

		// this.title.setBounds(0, (int) (height * 0.5) - 25, 30, 50);

		this.sureThresholdSlider.setBounds(45, 30, 30, sliderHeight);
		this.sureSliderMaxValue.setBounds(10, 10, 100, 20);
		this.sureSliderMinValue.setBounds(10, height - 35, 100, 20);
		this.sureSliderLabelValue.setBounds(10, height - 20, 100, 20);
		int value = this.sureThresholdSlider.getValue();
		int span = this.sureThresholdSlider.getMaximum() - this.sureThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.sureThresholdSlider.getMaximum() - this.sureCurrValue)
				/ (float) span * (sliderHeight - 28));
		this.sureSliderFitValue.setBounds(-50, position, 100, 20);
		if (value == this.sureCurrValue)
			this.sureSliderValue.setBounds(80, position, 60, 20);
		else {
			position = 33 + (int) ((float) (this.sureThresholdSlider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.sureSliderValue.setBounds(80, position, 60, 20);
		}

        this.unsureThresholdSlider.setBounds(115, 30, 30, sliderHeight);
		this.unsureSliderMaxValue.setBounds(80, 10, 100, 20);
		this.unsureSliderMinValue.setBounds(80, height - 35, 100, 20);
		this.unsureSliderLabelValue.setBounds(80, height - 20, 100, 20);
		value = this.unsureThresholdSlider.getValue();
		span = this.unsureThresholdSlider.getMaximum() - this.unsureThresholdSlider.getMinimum();
		position = 33 + (int) ((float) (this.unsureThresholdSlider.getMaximum() - this.unsureCurrValue)
				/ (float) span * (sliderHeight - 28));
		this.unsureSliderFitValue.setBounds(20, position, 100, 20);
		if (value == this.unsureCurrValue)
			this.unsureSliderValue.setBounds(145, position, 60, 20);
		else {
			position = 33 + (int) ((float) (this.unsureThresholdSlider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.unsureSliderValue.setBounds(145, position, 60, 20);
		}
		
		this.longDepThresholdSlider.setBounds(185, 30, 30, sliderHeight);
		this.longDepSliderMaxValue.setBounds(150, 10, 100, 20);
		this.longDepSliderMinValue.setBounds(150, height - 35, 100, 20);
		this.longDepSliderLabelValue.setBounds(150, height - 20, 100, 20);
		value = this.longDepThresholdSlider.getValue();
		span = this.longDepThresholdSlider.getMaximum() - this.longDepThresholdSlider.getMinimum();
		position = 33 + (int) ((float) (this.longDepThresholdSlider.getMaximum() - this.longDepCurrValue)
				/ (float) span * (sliderHeight - 28));
		this.longDepSliderFitValue.setBounds(130, position, 60, 20);
		if (value == this.longDepCurrValue)
			this.longDepSliderValue.setBounds(210, position, 60, 20);
		else {

			position = 33 + (int) ((float) (this.longDepThresholdSlider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.longDepSliderValue.setBounds(210, position, 60, 20);
		}
	
		//CAUSALITY THRESHOLD SLIDER
		
		this.causalityWeightSlider.setBounds(255, 30, 30, sliderHeight);
		this.causalityWeightSliderMaxValue.setBounds(220, 10, 100, 20);
		this.causalityWeightSliderMinValue.setBounds(220, height - 35, 100, 20);
		this.causalityWeightSliderLabelValue.setBounds(220, height - 20, 100, 20);

		value = this.causalityWeightSlider.getValue();
		span = this.causalityWeightSlider.getMaximum() - this.causalityWeightSlider.getMinimum();
		position = 33 + (int) ((float) (this.causalityWeightSlider.getMaximum() - this.causalityWeightCurrValue)
				/ (float) span * (sliderHeight - 28));
		this.causalityWeightSliderFitValue.setBounds(200, position, 60, 20);

		if (value == this.causalityWeightCurrValue)
			this.causalityWeightSliderValue.setBounds(280, position, 60, 20);
		else {

			position = 33 + (int) ((float) (this.causalityWeightSlider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.causalityWeightSliderValue.setBounds(210, position, 60, 20);
		}
		
	}

	private void updateSureSlider() {

		int value = this.sureThresholdSlider.getValue();

		int span = this.sureThresholdSlider.getMaximum() - this.sureThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.sureThresholdSlider.getMaximum() - value)
				/ (float) span * (this.sureThresholdSlider.getBounds().height - 28));

		this.sureSliderValue.setText(value + "%");
		this.sureSliderValue.setBounds(85, position, 60, 20);
		
	}

	private void updateUnsureSlider() {
		//UNSURE THRESHOLD SLIDER
		int value = this.unsureThresholdSlider.getValue();

		int span = this.unsureThresholdSlider.getMaximum() - this.unsureThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.unsureThresholdSlider.getMaximum() - value)
				/ (float) span * (this.unsureThresholdSlider.getBounds().height - 28));

		this.unsureSliderValue.setText(value + "%");
		this.unsureSliderValue.setBounds(155, position, 60, 20);
	}
	
	
	private void updateLongDepSlider() {
		int value = this.longDepThresholdSlider.getValue();

		int span = this.longDepThresholdSlider.getMaximum() - this.longDepThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.longDepThresholdSlider.getMaximum() - value)
				/ (float) span * (this.longDepThresholdSlider.getBounds().height - 28));

		this.longDepSliderValue.setText(value + "%");
		this.longDepSliderValue.setBounds(215, position, 60, 20);
	}

	

	private void updateCausalityWeightSlider() {
		//CAUSALITY THRESHOLD SLIDER
		int value = this.causalityWeightSlider.getValue();

		int span = this.causalityWeightSlider.getMaximum() - this.causalityWeightSlider.getMinimum();
		int position = 33 + (int) ((float) (this.causalityWeightSlider.getMaximum() - value)
				/ (float) span * (this.causalityWeightSlider.getBounds().height - 28));

		this.causalityWeightSliderValue.setText(value + "%");
		this.causalityWeightSliderValue.setBounds(275, position, 60, 20);
	}
	
	public void setSureCurrValue(int value) {

		this.sureCurrValue = value;

		int span = this.sureThresholdSlider.getMaximum() - this.sureThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.sureThresholdSlider.getMaximum() - value)
				/ (float) span * (this.sureThresholdSlider.getBounds().height - 28));
		this.sureSliderFitValue.setBounds(-60, position, 40, 20);
	}
	
	public void setUnSureCurrValue(int value) {

		this.unsureCurrValue = value;

		int span = this.unsureThresholdSlider.getMaximum() - this.unsureThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.unsureThresholdSlider.getMaximum() - value)
				/ (float) span * (this.unsureThresholdSlider.getBounds().height - 28));
		this.unsureSliderFitValue.setBounds(60, position, 40, 20);
	}
	
	public void setLongDepCurrValue(int value) {

		this.longDepCurrValue = value;

		int span = this.longDepThresholdSlider.getMaximum() - this.longDepThresholdSlider.getMinimum();
		int position = 33 + (int) ((float) (this.longDepThresholdSlider.getMaximum() - value)
				/ (float) span * (this.longDepThresholdSlider.getBounds().height - 28));
		this.longDepSliderFitValue.setBounds(180, position, 40, 20);
	}

	public void setCausalityWeightCurrValue(int value) {

		this.causalityWeightCurrValue = value;

		int span = this.causalityWeightSlider.getMaximum() - this.causalityWeightSlider.getMinimum();
		int position = 33 + (int) ((float) (this.causalityWeightSlider.getMaximum() - value)
				/ (float) span * (this.causalityWeightSlider.getBounds().height - 28));
		this.causalityWeightSliderFitValue.setBounds(300, position, 40, 20);
	}
	
	
	public void addSliderChangeListener(ChangeListener listener) {
		this.sureThresholdSlider.addChangeListener(listener);
		this.unsureThresholdSlider.addChangeListener(listener);
		this.longDepThresholdSlider.addChangeListener(listener);
		this.causalityWeightSlider.addChangeListener(listener);
	}
}


class FilterPanel extends JPanel {

	private static final long serialVersionUID = 8455559591750873767L;
	private final JSlider filterActivitySlider, filterTraceSlider;
	private JLabel filterActivityMinValue, filterActivityMaxValue, filterActivityValue, filterActivityLabelValue;
	private JLabel filterTraceMinValue, filterTraceMaxValue, filterTraceValue, filterTraceLabelValue;
	
	private int filterActivityCurrValue;
	private int filterTraceCurrValue;

	public FilterPanel(SlickerFactory factory, SlickerDecorator decorator,
			int filterActivityCurrValue, int filterTraceCurrValue) {

		super(null);
		int maximumValue = 100;
	
		this.filterActivityCurrValue = filterActivityCurrValue;
		this.filterTraceCurrValue = filterTraceCurrValue;
		
		this.filterActivitySlider = factory.createSlider(1);
		this.filterTraceSlider = factory.createSlider(1);
		
		filterActivitySlider.setName("filterActivitySlider");
		filterTraceSlider.setName("filterTraceSlider");
		
		this.filterActivitySlider.setMinimum(0);
		this.filterActivitySlider.setMaximum(maximumValue);
		this.filterActivitySlider.setValue(filterActivityCurrValue);

		this.filterTraceSlider.setMinimum(0);
		this.filterTraceSlider.setMaximum(maximumValue);
		this.filterTraceSlider.setValue(filterTraceCurrValue);
		
		

		
		this.filterActivitySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateFilterActivitySlider();
			}
		});
		
		this.filterTraceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateFilterTraceSlider();
			}
		});
		

		this.filterActivityMinValue = factory.createLabel("0%");
		this.filterActivityMaxValue = factory.createLabel(maximumValue + "%");
		this.filterActivityValue = factory.createLabel(filterActivityCurrValue + "%");
		this.filterActivityLabelValue = factory.createLabel("Activity");

		this.filterActivityMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.filterActivityMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.filterActivityValue.setHorizontalAlignment(SwingConstants.LEFT);
		this.filterActivityLabelValue.setHorizontalAlignment(SwingConstants.CENTER);


		this.filterActivityMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.filterActivityMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.filterActivityValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.filterActivityLabelValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.filterActivityMinValue.setForeground(Color.GRAY);
		this.filterActivityMaxValue.setForeground(Color.GRAY);
		this.filterActivityValue.setForeground(Color.DARK_GRAY);
		this.filterActivityLabelValue.setForeground(Color.GRAY);

		this.add(this.filterActivitySlider);
		this.add(this.filterActivityMinValue);
		this.add(this.filterActivityMaxValue);
		this.add(this.filterActivityValue);
		this.add(this.filterActivityLabelValue);
		
		this.filterTraceMinValue = factory.createLabel("0%");
		this.filterTraceMaxValue = factory.createLabel(maximumValue + "%");
		this.filterTraceValue = factory.createLabel(filterTraceCurrValue + "%");
		this.filterTraceLabelValue = factory.createLabel("Trace");

		

		this.filterTraceMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.filterTraceMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.filterTraceValue.setHorizontalAlignment(SwingConstants.LEFT);
		this.filterTraceLabelValue.setHorizontalAlignment(SwingConstants.CENTER);


		this.filterTraceMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.filterTraceMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.filterTraceValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.filterTraceLabelValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.filterTraceMinValue.setForeground(Color.GRAY);
		this.filterTraceMaxValue.setForeground(Color.GRAY);
		this.filterTraceValue.setForeground(Color.DARK_GRAY);
		this.filterTraceLabelValue.setForeground(Color.GRAY);

		this.add(this.filterTraceSlider);
		this.add(this.filterTraceMinValue);
		this.add(this.filterTraceMaxValue);
		this.add(this.filterTraceValue);
		this.add(this.filterTraceLabelValue);
		
		this.setBackground(Color.LIGHT_GRAY);
	}
	
	public void setHeight(int height) {
		this.setSize(200, height);

		int sliderHeight = height - 60;

		// this.title.setBounds(0, (int) (height * 0.5) - 25, 30, 50);

		this.filterActivitySlider.setBounds(45, 30, 30, sliderHeight);
		this.filterActivityMaxValue.setBounds(10, 10, 100, 20);
		this.filterActivityMinValue.setBounds(10, height - 35, 100, 20);
		this.filterActivityLabelValue.setBounds(10, height - 20, 100, 20);

		int value = this.filterActivitySlider.getValue();
		int span = this.filterActivitySlider.getMaximum() - this.filterActivitySlider.getMinimum();
		int position = 33 + (int) ((float) (this.filterActivitySlider.getMaximum() - this.filterActivityCurrValue)
				/ (float) span * (sliderHeight - 28));
	
		if (value == this.filterActivityCurrValue)
			this.filterActivityValue.setBounds(80, position, 60, 20);
		else {

			position = 33 + (int) ((float) (this.filterActivitySlider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.filterActivityValue.setBounds(80, position, 60, 20);
		}


		this.filterTraceSlider.setBounds(115, 30, 30, sliderHeight);
		this.filterTraceMaxValue.setBounds(80, 10, 100, 20);
		this.filterTraceMinValue.setBounds(80, height - 35, 100, 20);
		this.filterTraceLabelValue.setBounds(80, height - 20, 100, 20);

		value = this.filterTraceSlider.getValue();
		span = this.filterTraceSlider.getMaximum() - this.filterTraceSlider.getMinimum();
		position = 33 + (int) ((float) (this.filterTraceSlider.getMaximum() - this.filterTraceCurrValue)
				/ (float) span * (sliderHeight - 28));
		
		if (value == this.filterTraceCurrValue)
			this.filterTraceValue.setBounds(145, position, 60, 20);
		else {

			position = 33 + (int) ((float) (this.filterTraceSlider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.filterTraceValue.setBounds(145, position, 60, 20);
		}
		
		
	}

	private void updateFilterActivitySlider() {

		int value = this.filterActivitySlider.getValue();

		int span = this.filterActivitySlider.getMaximum() - this.filterActivitySlider.getMinimum();
		int position = 33 + (int) ((float) (this.filterActivitySlider.getMaximum() - value)
				/ (float) span * (this.filterActivitySlider.getBounds().height - 28));

		this.filterActivityValue.setText(value + "%");
		this.filterActivityValue.setBounds(85, position, 60, 20);
		
	}

	private void updateFilterTraceSlider() {
		int value = this.filterTraceSlider.getValue();

		int span = this.filterTraceSlider.getMaximum() - this.filterTraceSlider.getMinimum();
		int position = 33 + (int) ((float) (this.filterTraceSlider.getMaximum() - value)
				/ (float) span * (this.filterTraceSlider.getBounds().height - 28));

		this.filterTraceValue.setText(value + "%");
		this.filterTraceValue.setBounds(155, position, 60, 20);
	}
	
	public void setSureCurrValue(int value) {

		this.filterActivityCurrValue = value;

		int span = this.filterActivitySlider.getMaximum() - this.filterActivitySlider.getMinimum();
		int position = 33 + (int) ((float) (this.filterActivitySlider.getMaximum() - value)
				/ (float) span * (this.filterActivitySlider.getBounds().height - 28));
	}
	
	public void setUnSureCurrValue(int value) {

		this.filterTraceCurrValue = value;

		int span = this.filterTraceSlider.getMaximum() - this.filterTraceSlider.getMinimum();
		int position = 33 + (int) ((float) (this.filterTraceSlider.getMaximum() - value)
				/ (float) span * (this.filterTraceSlider.getBounds().height - 28));

	}

	
	public void addSliderChangeListener(ChangeListener listener) {
		this.filterActivitySlider.addChangeListener(listener);
		this.filterTraceSlider.addChangeListener(listener);
	}
}


class AnnotationsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6615860311124501461L;

	private String opID;

	private JLabel title, none;
	private JComboBox perspective;
	private JComboBox metric;
	private JPanel annotationsPanel;
	private JScrollPane annotationsScroll;
	private JTable patterns, connections;

	// -------------------------------------

	public AnnotationsPanel(SlickerFactory factory, SlickerDecorator decorator,
			Operator op, String opID) {

		this.opID = opID;

		this.setLayout(null);

		this.title = factory.createLabel("");
		this.title.setForeground(Color.darkGray);
		this.title
				.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 18));

		this.none = factory.createLabel("None");
		this.none.setForeground(Color.darkGray);
		this.none.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		this.none.setVisible(false);

		this.perspective = factory.createComboBox(new String[] { "Connections",
				"Patterns" });
		this.perspective.setSelectedItem("Patterns");
		this.perspective.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (perspective.getSelectedIndex() == 1)
					annotationsScroll.setViewportView(patterns);
				else
					annotationsScroll.setViewportView(connections);
			}

		});

		this.metric = factory.createComboBox(new String[] { "Frequency" });
		this.metric.setSelectedItem("Frequency");

		this.annotationsScroll = new JScrollPane();
		this.annotationsScroll.setBorder(javax.swing.BorderFactory
				.createEmptyBorder());
		decorator.decorate(this.annotationsScroll, Color.WHITE, Color.GRAY,
				Color.DARK_GRAY);
		this.annotationsScroll.getViewport().setBackground(Color.WHITE);

		this.initTables(op, null);

		this.annotationsPanel = factory.createRoundedPanel(15, Color.WHITE);
		this.annotationsPanel.setLayout(null);
		this.annotationsPanel.add(this.none);
		this.annotationsPanel.add(this.metric);
		this.annotationsPanel.add(this.annotationsScroll);

		this.add(this.title);
		this.add(this.perspective);
		this.add(this.annotationsPanel);

		this.setBackground(Color.LIGHT_GRAY);
	}

	public void setSize(int width, int height) {

		super.setSize(width, height);

		this.title.setBounds(0, 0, width, 20);
		this.none.setBounds(20, 40, width - 60, 20);
		this.perspective.setBounds(0, 30, width, 20);
		this.annotationsPanel.setBounds(0, 60, width, height - 60);

		this.annotationsScroll.setBounds(5, 40, width - 10, height - 105);
		this.metric.setBounds(width - 105, 10, 100, 20);
	}

	private void initTables(Operator op, HashMap<String, String> keys) {

		int elements = 0;
		int patterns = 0;

		if (op != null) {

			elements = op.getElements().size();
			patterns = op.getLearnedPatterns().size();
		}

		// ------------------

		this.patterns = new JTable();

		this.patterns.setGridColor(Color.GRAY);
		this.patterns.setBackground(Color.WHITE);
		this.patterns.setSelectionBackground(Color.LIGHT_GRAY);
		this.patterns.setSelectionForeground(Color.DARK_GRAY);
		this.patterns.setShowVerticalLines(false);

		final Class<?>[] classesTypes = new Class<?>[elements + 2];
		final boolean[] classesEdit = new boolean[elements + 2];
		for (int i = 0; i < elements; i++) {

			classesEdit[i] = false;
			classesTypes[i] = Boolean.class;
		}
		classesEdit[elements] = false;
		classesTypes[elements] = String.class;
		classesEdit[elements + 1] = false;
		classesTypes[elements + 1] = String.class;

		javax.swing.table.DefaultTableModel newTableModelP = new javax.swing.table.DefaultTableModel(
				new Object[][] {}, new String[] {}) {
			private static final long serialVersionUID = -3760751300047576804L;
			Class<?>[] types = classesTypes;
			boolean[] canEdit = classesEdit;

			public Class<?> getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};

		int sum = 0;
		ArrayList<Integer> stackC = new ArrayList<Integer>(elements);
		for (int i = 0; i < elements; i++)
			stackC.add(new Integer(0));

		if (op != null) {

			ArrayList<String> stackP = new ArrayList<String>(patterns);
			ArrayList<Integer> stackV = new ArrayList<Integer>(patterns);
			for (java.util.Map.Entry<String, Stats> entry : op
					.getLearnedPatterns().entrySet()) {

				int occurrences = entry.getValue().getOccurrences();

				boolean isInserted = false;
				for (int i = 0; i < stackP.size(); i++) {

					if (occurrences > stackV.get(i)) {

						stackP.add(i, entry.getKey());
						stackV.add(i, occurrences);
						isInserted = true;
						break;
					}
				}
				if (!isInserted) {

					stackP.add(entry.getKey());
					stackV.add(occurrences);
				}

				sum += occurrences;
			}

			Boolean[][] p = new Boolean[elements][patterns];
			String[][] m = new String[2][patterns];

			for (int i = 0; i < stackP.size(); i++) {

				String code = stackP.get(i);
				int occurrences = stackV.get(i);

				float percentage = Math.round((float) occurrences / (float) sum
						* 10000) / 100f;

				for (int j = 0; j < elements; j++) {

					if (code.charAt(j) == '1') {

						p[j][i] = true;

						Integer temp = stackC.remove(j);
						temp += occurrences;
						stackC.add(j, temp);
					} else
						p[j][i] = false;
				}
				m[0][i] = " " + String.valueOf(occurrences);
				m[1][i] = percentage + "%";
			}

			for (int i = 0; i < elements; i++)
				newTableModelP.addColumn("", p[i]);

			newTableModelP.addColumn("", m[0]);
			newTableModelP.addColumn("", m[1]);
		}

		final TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();

		this.patterns.setModel(newTableModelP);

		if (elements > 0) {

			for (int i = 0; i < elements + 2; i++) {

				TableColumn column = this.patterns.getColumnModel()
						.getColumn(i);

				if (i < elements) {

					String headerValue = " "
							+ this.convertID(keys.get(String.valueOf(op
									.getElements().get(i))));

					if (headerValue.length() > 20)
						headerValue = headerValue.substring(0, 17) + "...";

					column.setMinWidth(20);
					column.setMaxWidth(20);
					column.setHeaderValue(headerValue);
				}
				column.setHeaderRenderer(headerRenderer);
			}
			TableColumn column1 = this.patterns.getColumnModel().getColumn(
					elements);
			TableColumn column2 = this.patterns.getColumnModel().getColumn(
					elements + 1);

			column1.setMinWidth(60);
			column1.setMaxWidth(60);
			column2.setMinWidth(50);
			column2.setMaxWidth(50);

			this.patterns.getTableHeader().setBackground(Color.WHITE);
		}

		// ------------------

		this.connections = new JTable();

		this.connections.setGridColor(Color.GRAY);
		this.connections.setBackground(Color.WHITE);
		this.connections.setSelectionBackground(Color.LIGHT_GRAY);
		this.connections.setSelectionForeground(Color.DARK_GRAY);
		this.connections.setShowVerticalLines(false);

		javax.swing.table.DefaultTableModel newTableModelC = new javax.swing.table.DefaultTableModel(
				new Object[][] {}, new String[] {}) {
			private static final long serialVersionUID = -9201456440598163559L;
			Class<?>[] types = new Class<?>[] { String.class, String.class,
					String.class };
			boolean[] canEdit = new boolean[] { false, false, false };

			public Class<?> getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};

		if (op != null) {

			ArrayList<Integer> stackI = new ArrayList<Integer>(elements);
			for (int i = 0; i < stackC.size(); i++) {

				int value = stackC.get(i);

				boolean isInserted = false;
				for (int j = 0; j < stackI.size(); j++) {

					int temp = stackC.get(stackI.get(j));

					if (value > temp) {

						stackI.add(j, new Integer(i));
						isInserted = true;
						break;
					}
				}
				if (!isInserted) {

					stackI.add(new Integer(i));
				}
			}

			String[][] m = new String[3][elements];

			for (int i = 0; i < stackC.size(); i++) {

				int element = op.getElements().get(stackI.get(i));
				String elementID = this.convertID(keys.get(String
						.valueOf(element)));
				int occurrences = stackC.get(stackI.get(i));

				float percentage = Math.round((float) occurrences / (float) sum
						* 10000) / 100f;

				m[0][i] = elementID;
				m[1][i] = String.valueOf(occurrences);
				m[2][i] = percentage + "%";
			}

			for (int i = 0; i < 3; i++)
				newTableModelC.addColumn("", m[i]);
		}

		this.connections.setModel(newTableModelC);

		if (op != null) {

			TableColumn column1 = this.connections.getColumnModel()
					.getColumn(1);
			TableColumn column2 = this.connections.getColumnModel()
					.getColumn(2);

			column1.setMinWidth(60);
			column1.setMaxWidth(60);
			column2.setMinWidth(50);
			column2.setMaxWidth(50);
		}

		// ------------------

		if (this.perspective.getSelectedIndex() == 1)
			this.annotationsScroll.setViewportView(this.patterns);
		else
			this.annotationsScroll.setViewportView(this.connections);
	}

	public void update(Operator op, String opID, HashMap<String, String> keys) {

		if (!this.opID.equals(opID)) {

			this.opID = opID;

			String text = "";
			if (op instanceof Split)
				text = "Outputs of " + this.convertID(opID);
			else
				text = "Inputs of " + this.convertID(opID);

			this.title.setText(text);
			this.title.setToolTipText(text);

			if (op.getLearnedPatterns().isEmpty()) {

				this.none.setVisible(true);
				// this.perspective.setVisible(false);
				this.perspective.setEnabled(false);
				// this.metric.setVisible(false);
				this.metric.setEnabled(false);
				this.annotationsScroll.setVisible(false);
				this.annotationsScroll.setEnabled(false);
			} else {

				this.none.setVisible(false);
				// this.perspective.setVisible(true);
				this.perspective.setEnabled(true);
				// this.metric.setVisible(true);
				this.metric.setEnabled(true);
				this.annotationsScroll.setVisible(true);
				this.annotationsScroll.setEnabled(true);

				this.initTables(op, keys);
			}
		}
	}

	private String convertID(String id) {

		int index = id.indexOf("+");

		if (index == -1) {
			return id; 
		}
		else {
			return id.substring(0, index) + " (" + id.substring(index + 1) + ")";
		}
	}
}

// class OptionsPanel extends JPanel {
//	
// private final JLabel title;
//	
// public OptionsPanel(SlickerFactory factory, SlickerDecorator decorator){
//		
// this.setLayout(null);
//		
// this.title = factory.createLabel("Options");
// this.title.setForeground(Color.DARK_GRAY);
// this.title.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
// }
// }

class SetupPanel extends JPanel {


	private static final long serialVersionUID = 8408305033071764421L;
	private final AnnotatedVisualizationSettings settings;
	private boolean hasChanged;

	// ----------------------------------

	private JPanel nodesPanel, edgesPanel;
	private JLabel nodesTitle, edgesTitle;

	private JLabel n1, n2, n3;
	private JLabel e1, e2, e3;

	private JCheckBox nShowUT, nColor;
	private JComboBox nMeasure;

	private JCheckBox eColor;
	private JComboBox eMeasure, eStyle;

	public SetupPanel(SlickerFactory factory, SlickerDecorator decorator,
			final AnnotatedVisualizationSettings settings) {

		this.hasChanged = false;
		this.settings = settings;

		this.setLayout(null);

		this.nodesTitle = factory.createLabel("Events");
		this.nodesTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		this.nodesTitle.setForeground(new Color(40, 40, 40));

		this.nodesPanel = factory.createRoundedPanel(15, Color.gray);
		this.nodesPanel.setLayout(null);

		this.n1 = factory.createLabel("Show unconnected tasks:");
		this.n1.setHorizontalAlignment(SwingConstants.RIGHT);
		this.n1.setForeground(new Color(40, 40, 40));
		this.n2 = factory.createLabel("Color scaling:");
		this.n2.setHorizontalAlignment(SwingConstants.RIGHT);
		this.n2.setForeground(new Color(40, 40, 40));
		this.n3 = factory.createLabel("Measure:");
		this.n3.setHorizontalAlignment(SwingConstants.RIGHT);
		this.n3.setForeground(new Color(40, 40, 40));

		this.nShowUT = new JCheckBox();
		this.nShowUT.setSelected(settings.isShowingUnconnectedTasks());
		this.nShowUT.setBackground(Color.GRAY);
		this.nShowUT.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setShowingUnconnectedTasks(nShowUT.isSelected());
			}
		});

		this.nColor = new JCheckBox();
		this.nColor.setSelected(settings.isColorScalingEvents());
		this.nColor.setBackground(Color.GRAY);
		this.nColor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setColorScalingEvents(nColor.isSelected());
			}
		});
		this.nMeasure = factory.createComboBox(new String[] { "None",
				"End Counter", "Frequency", "Start Counter" });
		this.nMeasure.setSelectedItem(settings.getMeasureEvents());
		this.nMeasure.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setMeasureEvents((String) nMeasure.getSelectedItem());
			}
		});

		this.nodesPanel.add(this.nodesTitle);
		this.nodesPanel.add(this.n1);
		this.nodesPanel.add(this.n2);
		this.nodesPanel.add(this.n3);
		this.nodesPanel.add(this.nShowUT);
		this.nodesPanel.add(this.nColor);
		this.nodesPanel.add(this.nMeasure);

		this.nodesPanel.setBounds(0, 0, 315, 150);
		this.nodesTitle.setBounds(10, 10, 100, 30);
		this.n1.setBounds(20, 50, 150, 20);
		this.n2.setBounds(20, 80, 150, 20);
		this.n3.setBounds(20, 110, 150, 20);
		this.nShowUT.setBounds(175, 50, 25, 20);
		this.nColor.setBounds(175, 80, 25, 20);
		this.nMeasure.setBounds(175, 110, 120, 20);

		// -------------------------------------------

		this.edgesTitle = factory.createLabel("Transitions");
		this.edgesTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		this.edgesTitle.setForeground(new Color(40, 40, 40));

		this.e1 = factory.createLabel("Color scaling:");
		this.e1.setHorizontalAlignment(SwingConstants.RIGHT);
		this.e1.setForeground(new Color(40, 40, 40));
		this.e2 = factory.createLabel("Measure:");
		this.e2.setHorizontalAlignment(SwingConstants.RIGHT);
		this.e2.setForeground(new Color(40, 40, 40));
		this.e3 = factory.createLabel("Line style:");
		this.e3.setHorizontalAlignment(SwingConstants.RIGHT);
		this.e3.setForeground(new Color(40, 40, 40));

		this.eColor = new JCheckBox();
		this.eColor.setSelected(settings.isColorScalingTransitions());
		this.eColor.setBackground(Color.GRAY);
		this.eColor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setColorScalingTransitions(eColor.isSelected());
			}
		});
		this.eMeasure = factory.createComboBox(new String[] { "None",
				"Dependency", "Frequency" });
		this.eMeasure.setSelectedItem(settings.getMeasureTransitions());
		this.eMeasure.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setMeasureTransitions((String) eMeasure
						.getSelectedItem());
			}
		});
		this.eStyle = factory.createComboBox(new String[] { "Beizer",
				"Orthogonal", "Spline" });
		this.eStyle.setSelectedItem(settings.getLineStyle());
		this.eStyle.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setLineStyle((String) eStyle.getSelectedItem());
			}
		});

		this.edgesPanel = factory.createRoundedPanel(15, Color.gray);
		this.edgesPanel.setLayout(null);

		this.edgesPanel.add(this.edgesTitle);
		this.edgesPanel.add(this.e1);
		this.edgesPanel.add(this.e2);
		this.edgesPanel.add(this.e3);
		this.edgesPanel.add(this.eColor);
		this.edgesPanel.add(this.eMeasure);
		this.edgesPanel.add(this.eStyle);

		this.edgesPanel.setBounds(0, 160, 315, 150);
		this.edgesTitle.setBounds(10, 10, 100, 30);
		this.e1.setBounds(20, 50, 150, 20);
		this.e2.setBounds(20, 80, 150, 20);
		this.e3.setBounds(20, 110, 150, 20);
		this.eColor.setBounds(175, 50, 25, 20);
		this.eMeasure.setBounds(175, 80, 120, 20);
		this.eStyle.setBounds(175, 110, 120, 20);

		this.add(this.nodesPanel);
		this.add(this.edgesPanel);

		this.setBackground(Color.LIGHT_GRAY);
	}

	public AnnotatedVisualizationSettings getSettings() {

		this.hasChanged = false;

		return this.settings;
	}

	public boolean hasChanged() {
		return this.hasChanged;
	}
}

class VerticalTableHeaderCellRenderer extends
		javax.swing.table.DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5792846837172592507L;

	public VerticalTableHeaderCellRenderer() {

		setOpaque(false);

		setHorizontalAlignment(LEFT);
		setHorizontalTextPosition(CENTER);
		setVerticalAlignment(CENTER);
		setVerticalTextPosition(TOP);
		setUI(new VerticalLabelUI(false));
	}

	protected javax.swing.Icon getIcon(JTable table, int column) {

		javax.swing.RowSorter.SortKey sortKey = getSortKey(table, column);
		if (sortKey != null && sortKey.getColumn() == column) {
			javax.swing.SortOrder sortOrder = sortKey.getSortOrder();
			switch (sortOrder) {
			case ASCENDING:
				return VerticalSortIcon.ASCENDING;
			case DESCENDING:
				return VerticalSortIcon.DESCENDING;
			}
		}
		return null;
	}

	private enum VerticalSortIcon implements javax.swing.Icon {

		ASCENDING, DESCENDING;
		private javax.swing.Icon icon = javax.swing.UIManager
				.getIcon("Table.ascendingSortIcon");

		/**
		 * Paints an icon suitable for the header of a sorted table column,
		 * rotated by 90 degrees clockwise. This rotation is applied to compensate the
		 * rotation already applied to the passed in Graphics reference by the
		 * VerticalLabelUI.
		 * <P>
		 * The icon is retrieved from the UIManager to obtain an icon
		 * appropriate to the L&F.
		 * 
		 * @param c
		 *            the component to which the icon is to be rendered
		 * @param g
		 *            the graphics context
		 * @param x
		 *            the X coordinate of the icon's top-left corner
		 * @param y
		 *            the Y coordinate of the icon's top-left corner
		 */
		public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
			switch (this) {
			case ASCENDING:
				icon = javax.swing.UIManager.getIcon("Table.ascendingSortIcon");
				break;
			case DESCENDING:
				icon = javax.swing.UIManager
						.getIcon("Table.descendingSortIcon");
				break;
			}
			int maxSide = Math.max(getIconWidth(), getIconHeight());
			Graphics2D g2 = (Graphics2D) g.create(x, y, maxSide, maxSide);
			g2.rotate((Math.PI / 2));
			g2.translate(0, -maxSide);
			icon.paintIcon(c, g2, 0, 0);
			g2.dispose();
		}

		/**
		 * Returns the width of the rotated icon.
		 * 
		 * @return the <B>height</B> of the contained icon
		 */
		public int getIconWidth() {
			return icon.getIconHeight();
		}

		/**
		 * Returns the height of the rotated icon.
		 * 
		 * @return the <B>width</B> of the contained icon
		 */
		public int getIconHeight() {
			return icon.getIconWidth();
		}
	}

	protected javax.swing.RowSorter.SortKey getSortKey(JTable table, int column) {
		javax.swing.RowSorter<?> rowSorter = table.getRowSorter();
		if (rowSorter == null) {
			return null;
		}

		List<?> sortedColumns = rowSorter.getSortKeys();
		if (sortedColumns.size() > 0) {
			return (javax.swing.RowSorter.SortKey) sortedColumns.get(0);
		}
		return null;
	}

	@Override
	public java.awt.Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		setIcon(getIcon(table, column));
		setBorder(null);
		return this;
	}
}

class DashedBorder extends javax.swing.border.LineBorder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1357931293759243135L;

	public DashedBorder(Color color) {
		super(color);
	}

	public void paintBorder(java.awt.Component comp, Graphics g, int x1,
			int x2, int y1, int y2) {

		Stroke old = ((Graphics2D) g).getStroke();
		BasicStroke bs = new BasicStroke(5.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, new float[] { 15.0f, 30.0f },
				2.0f);
		((Graphics2D) g).setStroke(bs);
		super.paintBorder(comp, g, x1, x2, y1, y2);
		((Graphics2D) g).setStroke(old);
	}
}

// Update by sabi
class legendPanel extends JPanel
{
	
	private static final long serialVersionUID = 8415559591750873767L;
	private String[] DEFAULTVALUES={"Split and join fractions","Succession count","Strength (split & join)","Strength (concurrency & loops)","Causality (average strength)"};
	private JRadioButton[] options;
	//private ActionListener action;
	private ButtonGroup bg=new ButtonGroup();
	
	public legendPanel(SlickerFactory factory, SlickerDecorator decorator)
	{
		super(null);
		this.options=new JRadioButton[DEFAULTVALUES.length];

		for(int i=0;i<DEFAULTVALUES.length;i++)
		{
			this.options[i]=factory.createRadioButton(DEFAULTVALUES[i]);
			this.options[i].setForeground(Color.WHITE);

			this.options[i].setFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,12));
			this.setForeground(Color.WHITE);
			this.add(this.options[i]);
			this.bg.add(this.options[i]);
		}
		this.setBackground(Color.LIGHT_GRAY);
		this.setVisible(true);
		this.setEnabled(true);
	}

	public void setSize(int width,int height)
	{
//		this.setSize(new Dimension(width,height));
		int h=height;
		if(this.DEFAULTVALUES.length>1)
			h=(height-2)/this.DEFAULTVALUES.length;
		for(int i=0;i<DEFAULTVALUES.length;i++)
			this.options[i].setBounds(1,i*h+1,width-2,h);
	}

	public void setHeight(int height)
	{
		if(height>300)
		{
			super.setSize(241,300);
			this.setSize(239,300);
		}
		else
		{
			super.setSize(241,height);
			this.setSize(239,height);
		}
	}
	
	public void setTotalHeight(int height)
	{
		int temp=(int)(height*0.66);
		while(temp+280>height)
			temp=(int)(temp*0.66);
		this.setHeight(temp);
	}

    public void setListener(ActionListener action)
	{
		//this.action=action;
		for(JRadioButton b:options)
		{
			if(b.getText().equals("Causality (average strength)")) {
				b.setSelected(true);
			}
			b.addActionListener(action);
		}
	}
}