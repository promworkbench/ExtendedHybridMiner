package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 21/06/17.
 */
public class BoundedSplitJoinOnly<N extends AbstractDirectedGraphNode> implements CandidatePlaceSelectionStrategy<N> {
	private int inputOutputSum;

    public BoundedSplitJoinOnly(int inputOutputSum) {
        if (inputOutputSum <1)
            throw new RuntimeException("The inputOutputSum on the split and join only must be at least 1!");
        this.inputOutputSum = inputOutputSum;
    }

    @Override
    public boolean isCandidate(PartialPlaceEvaluation<N> ppe) {

        if (ppe.getPlaceOutputNodes().size()==1 && ppe.getPlaceInputNodes().size()<=(inputOutputSum-1))
            return true;

        if (ppe.getPlaceInputNodes().size()==1 && ppe.getPlaceOutputNodes().size()<= (inputOutputSum-1))
            return true;

        return false;
    }


}
