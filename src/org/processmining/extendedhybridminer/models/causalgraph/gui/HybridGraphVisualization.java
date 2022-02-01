package org.processmining.extendedhybridminer.models.causalgraph.gui;

import java.awt.Component;

import javax.swing.JViewport;

public interface HybridGraphVisualization  {

	public PIPInteractionPanel getpip();

	public double factorMultiplyGraphToPIP();
	
	public double getScale();
	
	public void setScale(double d);

	public JViewport getViewport();

	public Component getVerticalScrollBar();

	public Component getHorizontalScrollBar();

	public void showLegend(boolean b);

	public void showConfig(boolean b);

}
