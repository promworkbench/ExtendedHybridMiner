package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.LinkedHashSet;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.MinimalPlaces;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;


public class EdgesFirst<N extends AbstractDirectedGraphNode, E extends HybridDirectedGraphEdge> extends CandidatePlaceIterator<N>{
	//Iterator<EdgeClique<N,E>> currentIterator;
	int maxInputPlusOutput;
	MinimalEdgeOrdering edgeIt;
	OutputThenInputOrderingIterator<HybridDirectedGraphNode> secondIt;
	boolean edgeItFinished;
	

	public EdgesFirst(EdgesOrderingCompatibleSelStrategy<N> selectionStrategy, MinimalPlaces conflictStrategy, LinkedHashSet<HybridDirectedGraphEdge> edges, LinkedHashSet<N> outputNodes, LinkedHashSet<N> inputNodes) {
		super(selectionStrategy);
		this.maxInputPlusOutput = selectionStrategy.getInputOutputSum();
		//edgeIt = new MinimalEdgeOrdering(selectionStrategy, conflictStrategy, edges, outputNodes, inputNodes);
		//secondIt = new OutputThenInputOrderingIterator<HybridDirectedGraphNode>(selectionStrategy, outputNodes, inputNodes);
		//currentIterator = edgeIt.currentIterator;
		edgeItFinished = false;
	}

	
	public boolean hasNext() {
		if (edgeItFinished) {
			if (secondIt.hasNext()) {
				return true;
			} else {
				return false;
			}
			
		} else {
			if (edgeIt.hasNext()) {
				return true;
			} else {
				//currentIterator = secondIt.currentIterator;
				edgeItFinished = true;
				return true;
			}
		}
	}
	
	
	public PartialPlaceEvaluation<N> next() {
		if (!this.hasNext())
			throw new RuntimeException("hasNext() must be called before next()!");
		
		if (this.edgeItFinished) {
			return (PartialPlaceEvaluation<N>) secondIt.next();
		} else {
			return (PartialPlaceEvaluation<N>) edgeIt.next();
		}
	}
	
}
