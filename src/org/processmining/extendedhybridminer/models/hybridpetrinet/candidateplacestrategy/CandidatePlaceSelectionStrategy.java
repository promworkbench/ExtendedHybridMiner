package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public interface CandidatePlaceSelectionStrategy<N extends AbstractDirectedGraphNode> {
	
	 public boolean isCandidate(PartialPlaceEvaluation<N> ppe);

	}
