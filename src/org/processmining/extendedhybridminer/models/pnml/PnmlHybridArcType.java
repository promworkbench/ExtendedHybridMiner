package org.processmining.extendedhybridminer.models.pnml;

import org.processmining.plugins.pnml.elements.extensions.PnmlArcType;


public class PnmlHybridArcType extends PnmlArcType {

	protected PnmlHybridArcType(String tag) {
		super(tag);
	}

	protected boolean isHybridSure() {
		return (text != null) && (text.getText() != null) && text.getText().equals("sure");
	}

	protected boolean isHybridUncertain() {
		return (text != null) && (text.getText() != null) && text.getText().equals("unsure");
	}
	
	protected boolean isHybridLongDep() {
		return (text != null) && (text.getText() != null) && text.getText().equals("longDep");
	}
	
	protected void setHybridSure() {
		text = factory.createPnmlText("sure");
	}
	
	protected void setHybridUncertain() {
		text = factory.createPnmlText("unsure");
	}
	
	protected void setHybridLongDep() {
		text = factory.createPnmlText("longDep");
	}

}
