package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.Iterator;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public abstract class CandidatePlaceIterator<N extends AbstractDirectedGraphNode> implements Iterator<PartialPlaceEvaluation<N>> {
	private CandidatePlaceSelectionStrategy<N> selectionStrategy;

	public CandidatePlaceIterator(CandidatePlaceSelectionStrategy<N> selectionStrategy) {
		this.selectionStrategy = selectionStrategy;
	}

	public CandidatePlaceSelectionStrategy<N> getSelectionStrategy() {
		return selectionStrategy;
	}


	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported by CandidatePlaceIterator!");
	}
	
	public int getCurrentLevel() {
		return 1;
	}

}
