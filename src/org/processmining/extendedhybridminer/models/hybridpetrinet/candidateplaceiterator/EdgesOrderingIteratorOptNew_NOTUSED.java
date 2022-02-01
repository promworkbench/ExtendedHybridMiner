package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;
// CLASS NOT USED
import java.util.Iterator;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgesOrderingIteratorOptNew_NOTUSED<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> extends CandidatePlaceIterator<N>{
	EdgePWSetMatrixNew_FV<N, E> matrix;
	private int currentK;
	Iterator<EdgeClique<N,E>> currentIterator;
	int maxInputPlusOutput;
	
	
	public EdgesOrderingIteratorOptNew_NOTUSED(EdgesOrderingCompatibleSelStrategy<N> selectionStrategy, Set<E> edges) {
		super(selectionStrategy);
		this.matrix = new EdgePWSetMatrixNew_FV<>(edges);
		this.maxInputPlusOutput = selectionStrategy.getInputOutputSum();
		currentK=1;
		currentIterator = this.matrix.getLevel(currentK).iterator();
	}

	public boolean hasNext() {
		if (this.currentIterator.hasNext())
			return true;
		
		if (currentK<maxInputPlusOutput) {
			currentK++;
			matrix.addLevel(currentK);
			currentIterator=matrix.getLevel(currentK).iterator();
			System.out.println("---- LEVEL "+currentK+" NUMBER OF NEW ITEMS "+matrix.getLevel(currentK).size());
			return currentIterator.hasNext();
		}
		
		return false;
	}
	
	public PartialPlaceEvaluation<N> next() {
		if (!this.hasNext())
			throw new RuntimeException("hasNext() must be called before next()!");
		
		EdgeClique<N,E> currentEdgeSet = currentIterator.next();

		return EdgePWSetMatrixNew_FV.buildPPEFromEdges(currentEdgeSet);

}
	
	
}
