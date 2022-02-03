package org.processmining.extendedhybridminer.models.hybridpetrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 23/08/16.
 */
public class SureEdge extends Edge {

    public SureEdge(Transition source, Transition target, int weight) {
        super(source, target, weight);
    }

    public SureEdge(Transition source, Transition target) {
        this(source, target, 1);
    }


}
