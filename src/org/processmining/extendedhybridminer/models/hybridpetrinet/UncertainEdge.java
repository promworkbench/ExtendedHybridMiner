package org.processmining.extendedhybridminer.models.hybridpetrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 23/08/16.
 */
public class UncertainEdge extends Edge {

    public UncertainEdge(Transition source, Transition target, int weight) {
        super(source, target, weight);
        getAttributeMap().put("edgeType", "uncertain");
		//getAttributeMap().put(AttributeMap.LABEL, " ");
		//getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		//getAttributeMap().put(AttributeMap.SHOWLABEL, true);
    }

    public UncertainEdge(Transition source, Transition target) {
        this(source, target, 1);
    }
}
