package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class BoundedInputPLUSOutput<N extends AbstractDirectedGraphNode> extends EdgesOrderingCompatibleSelStrategy<N> {

	public BoundedInputPLUSOutput(int inputOutputSum) {
		if (inputOutputSum < 2)
			throw new RuntimeException("The minimum number of input and output transitions is two!");
		
		this.inputOutputSum = inputOutputSum;
	}


	public boolean isCandidate(PartialPlaceEvaluation<N> ppe) {
		if (ppe.getPlaceOutputNodes().size() + ppe.getPlaceInputNodes().size() <= inputOutputSum)
			return true;
		else
			return false;
	}
}
