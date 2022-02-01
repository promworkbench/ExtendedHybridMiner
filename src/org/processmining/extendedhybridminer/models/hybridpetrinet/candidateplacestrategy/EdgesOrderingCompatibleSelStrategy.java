package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy;

import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 21/06/17.
 */
public abstract class EdgesOrderingCompatibleSelStrategy<N extends AbstractDirectedGraphNode> implements CandidatePlaceSelectionStrategy<N> {
    int inputOutputSum;

    public int getInputOutputSum() {
        return inputOutputSum;
    }

}
