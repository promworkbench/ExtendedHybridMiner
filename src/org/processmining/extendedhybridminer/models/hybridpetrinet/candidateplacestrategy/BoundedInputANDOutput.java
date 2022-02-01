package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class BoundedInputANDOutput<N extends AbstractDirectedGraphNode> implements CandidatePlaceSelectionStrategy<N> {
	private int maxOutputNum, maxInputNum;
	
	
	public BoundedInputANDOutput(int maxOutputNum, int maxInputNum) {
		this.maxOutputNum = maxOutputNum;
		this.maxInputNum = maxInputNum;
	}



	public int getMaxOutputNum() {
		return maxOutputNum;
	}



	public int getMaxInputNum() {
		return maxInputNum;
	}



	public boolean isCandidate(PartialPlaceEvaluation<N> ppe) {
		if (ppe.getPlaceOutputNodes().size()<=maxOutputNum && ppe.getPlaceInputNodes().size()<=maxInputNum)
			return true;
		else
			return false;
	}
}
