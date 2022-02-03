package org.processmining.extendedhybridminer.models.hybridpetrinet;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 28/07/16.
 */
public abstract class Edge extends Arc {

    public Edge(Transition source, Transition target, int weight) {
        super(source, target, weight);
    }

    public Edge(Transition source, Transition target) {
        this(source, target, 1);
    }

	public void setEdgeColor(Color color) {
		getAttributeMap().put(AttributeMap.EDGECOLOR, color);	
	}
}
