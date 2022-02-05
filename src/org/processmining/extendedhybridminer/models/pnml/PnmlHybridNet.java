package org.processmining.extendedhybridminer.models.pnml;

import java.util.ArrayList;

import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.elements.PnmlNet;
import org.processmining.plugins.pnml.elements.PnmlPage;

public class PnmlHybridNet extends PnmlNet {

	protected ArrayList<PnmlPage> pageList;

	public void setPageList(ArrayList<PnmlPage> pageList) {
		this.pageList = pageList;
		
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		for (PnmlPage page : pageList) {
			s += page.exportElement(pnml);
		}
		return s;
	}
	
}
