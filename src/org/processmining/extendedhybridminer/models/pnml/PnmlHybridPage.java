package org.processmining.extendedhybridminer.models.pnml;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.elements.PnmlNode;
import org.processmining.plugins.pnml.elements.PnmlPage;

public class PnmlHybridPage extends PnmlPage {
	
	protected List<PnmlHybridArc> arcList;
	
	public void setLists(ArrayList<PnmlNode> nodes, ArrayList<PnmlHybridArc> arcList) {
		this.arcList = arcList;
		this.nodeList = nodes;
	}
	
	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		String lineSeparator = "";
		for (PnmlHybridArc arc : arcList) {
			s += arc.exportElement(pnml);
			s += lineSeparator;
		}
		return s;
	}

}
