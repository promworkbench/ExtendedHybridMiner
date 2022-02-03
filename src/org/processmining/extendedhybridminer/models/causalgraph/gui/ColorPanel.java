package org.processmining.extendedhybridminer.models.causalgraph.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.ui.action.ActionButton;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.framework.util.ui.scalableview.ScalableComponent;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.framework.util.ui.scalableview.interaction.ViewInteractionPanel;

import info.clearthought.layout.TableLayout;

public class ColorPanel extends JPanel implements ViewInteractionPanel {

	private static final long serialVersionUID = 1036741994786060955L;
	//protected final HybridCausalGraphVisualization panel;
	//protected HybridCausalGraph cg;
	private JButton placeColorButton;
	private JButton sureColorButton;
	private JButton unsureColorButton;
	private JButton longDepColorButton;
	private int height;
	private int width;

	public ColorPanel(HybridCausalGraphVisualization panel, ExtendedCausalGraph cg) {
		//this.panel = panel;
		this.width = 200;
		this.height = 100;
		double size[][] = { { this.width }, { 25, 25, 25, 25 } };
		
		setLayout(new TableLayout(size));
		this.setBackground(Color.LIGHT_GRAY);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setOpaque(true);
		
		final JLabel label = new JLabel();
		label.setText("<html><font size= '+1'>Set color of: </font></html>");
		label.setLocation(0, 0);
		this.add(label, "0, 0");

		
		sureColorButton = new JButton("sure edges");
		sureColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		sureColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for sure edges", cg.getSureColor());
				if (c != null) {
					cg.updateSureColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(sureColorButton, "0, 1");
		
		unsureColorButton = new JButton("unsure edges");
		unsureColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		unsureColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for unsure edges", cg.getUnsureColor());
				if (c != null) {
					cg.updateUnsureColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(unsureColorButton, "0, 2");
		
		longDepColorButton = new ActionButton("long-term dependency edges");
		longDepColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		longDepColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for long-term dependency edges", cg.getLongDepColor());
				if (c != null) {
					cg.updateLongDepColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(longDepColorButton, "0, 3");
	}
	
	
	public ColorPanel(HybridPetrinetVisualization panel, ExtendedHybridPetrinet pn) {
		//this.panel = panel;
		this.width = 200;
		this.height = 125;
		double size[][] = { { this.width }, { 25, 25, 25, 25, 25 } };
		
		setLayout(new TableLayout(size));
		this.setBackground(Color.LIGHT_GRAY);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setOpaque(true);
		
		final JLabel label = new JLabel();
		label.setText("<html><font size= '+1'>Set color of: </font></html>");
		label.setLocation(0, 0);
		this.add(label, "0, 0");

		placeColorButton = new JButton("places");
		placeColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		placeColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for places", pn.getSurePlaceColor());
				if (c != null) {
					pn.updateSurePlaceColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(placeColorButton, "0, 1");
		
		sureColorButton = new JButton("sure edges");
		sureColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		sureColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for sure edges", pn.getSureColor());
				if (c != null) {
					pn.updateSureColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(sureColorButton, "0, 2");
		
		unsureColorButton = new JButton("unsure edges");
		unsureColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		unsureColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for unsure edges", pn.getUnsureColor());
				if (c != null) {
					pn.updateUnsureColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(unsureColorButton, "0, 3");
		
		longDepColorButton = new ActionButton("long-term dependency edges");
		longDepColorButton.setHorizontalAlignment(SwingConstants.LEFT);
		longDepColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(new Frame(),"Pick a color for long-term dependency edges", pn.getLDColor());
				if (c != null) {
					pn.updateLDColor(c);
				    panel.updateColor();
				}
			}
		});
		this.add(longDepColorButton, "0, 4");
	}


	
	public void updated() {
	}

	public String getPanelName() {
		return "Edge Colors";
	}

	public JComponent getComponent() {
		return this;
	}

	public void setParent(ScalableViewPanel viewPanel) {
	}

	public double getHeightInView() {
		return this.height;
	}

	public double getWidthInView() {
		return this.width;
	}

	public void willChangeVisibility(boolean to) {
	}

	public void setScalableComponent(ScalableComponent scalable) {
		
	}
}
