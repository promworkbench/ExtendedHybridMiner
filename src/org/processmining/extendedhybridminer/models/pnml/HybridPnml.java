package org.processmining.extendedhybridminer.models.pnml;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.pnml.base.Pnml;

public class HybridPnml extends Pnml {

	protected List<PnmlHybridNet> netList;
	
	public void setNetList(ArrayList<PnmlHybridNet> hybridNetList) {
		this.netList = hybridNetList;
		
	}
	
	protected String exportElements(Pnml pnml) {
		String s = "";
		for (PnmlHybridNet net : netList) {
			s += net.exportElement(pnml);
		}
		s = s + super.exportElements(pnml);
		return s;
	}

}
