package org.processmining.extendedhybridminer.models.pnml;

import java.awt.geom.Point2D;
import java.util.Map;

import org.jgraph.graph.GraphConstants;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.extendedhybridminer.models.hybridpetrinet.LongDepEdge;
import org.processmining.extendedhybridminer.models.hybridpetrinet.SureEdge;
import org.processmining.extendedhybridminer.models.hybridpetrinet.UncertainEdge;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.elements.PnmlArc;
import org.processmining.plugins.pnml.elements.PnmlBasicObject;
import org.processmining.plugins.pnml.elements.PnmlPage;
import org.processmining.plugins.pnml.elements.extensions.PnmlArcType;
import org.processmining.plugins.pnml.toolspecific.PnmlToolSpecific;
import org.xmlpull.v1.XmlPullParser;


public class PnmlHybridArc extends PnmlArc {

	protected PnmlHybridArcType arcType;
	protected String id;
	protected String source;
	protected String target;

	public PnmlHybridArc() {
		super();
		arcType = null;
		id = null;
		source = null;
		target = null;
	}
	
	protected void importAttributes(XmlPullParser xpp, Pnml pnml) {
		super.importAttributes(xpp, pnml);
		id = xpp.getAttributeValue(null, "id");
		source = xpp.getAttributeValue(null, "source");
		target = xpp.getAttributeValue(null, "target");
	}

	protected boolean importElements(XmlPullParser xpp, Pnml pnml) {
		String arcTypeTag = PnmlArcType.TAG;
		if (xpp.getName().equals(arcTypeTag)) {
			arcType = new PnmlHybridArcType(arcTypeTag);
			arcType.importElement(xpp, pnml);
			return true;
		}
		return super.importElements(xpp, pnml);
	}

	protected String exportElements(Pnml pnml) {
		String s = super.exportElements(pnml);
		if (arcType != null) {
			s += arcType.exportElement(pnml);
		}
		return s;
	}

	public void convertToNet(PetrinetGraph net, ExpandableSubNet subNet, Map<String, Place> placeMap,
			Map<String, Transition> transitionMap,
			Map<String, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edgeMap,
			Point2D.Double displacement, GraphLayoutConnection layout) {
		
		if (transitionMap.containsKey(source) && transitionMap.containsKey(target) && net instanceof ExtendedHybridPetrinet) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc = null;
			if (arcType.isHybridLongDep()) {
				arc = ((ExtendedHybridPetrinet) net).addLongDepArc(transitionMap.get(source), transitionMap.get(target));
			} else if (arcType.isHybridSure()) {
				arc = ((ExtendedHybridPetrinet) net).addSureArc(transitionMap.get(source), transitionMap.get(target));
			} else if (arcType.isHybridUncertain()) {
				arc = ((ExtendedHybridPetrinet) net).addUnsureArc(transitionMap.get(source), transitionMap.get(target));
			}
			if (arc != null) {
				edgeMap.put(id, arc);
				arc.getAttributeMap().put(AttributeMap.STYLE, GraphConstants.STYLE_SPLINE);
				((PnmlBasicObject) this).convertToNet(arc);
				for (PnmlToolSpecific toolSpecific : toolSpecificList) {
					toolSpecific.convertToNet(arc);
				}
			}
		} else {
			super.convertToNet(net, subNet, placeMap, transitionMap, edgeMap, displacement, layout);
		}		
	}

	public PnmlHybridArc convertFromNet(ExpandableSubNet parent,
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge, PnmlPage page,
			Map<Pair<AbstractGraphElement, ExpandableSubNet>, String> idMap, GraphLayoutConnection layout) {
		
		super.convertFromNet(parent, edge, page, idMap, layout);
		if (edge instanceof LongDepEdge) {
			arcType = new PnmlHybridArcType(PnmlArcType.TAG);
			arcType.setHybridLongDep();
		} else if (edge instanceof SureEdge) {
			arcType = new PnmlHybridArcType(PnmlArcType.TAG);
			arcType.setHybridSure();
		} else if (edge instanceof UncertainEdge) {
			arcType = new PnmlHybridArcType(PnmlArcType.TAG);
			arcType.setHybridUncertain();
		} else if (edge instanceof Arc) {
			arcType = new PnmlHybridArcType(PnmlArcType.TAG);
			arcType.setNormal();
		}
		id = "arc" + idMap.size();
		source = idMap.get(new Pair<DirectedGraphElement, ExpandableSubNet>(edge.getSource(), parent));
		target = idMap.get(new Pair<DirectedGraphElement, ExpandableSubNet>(edge.getTarget(), parent));
		return this;
	}

}


