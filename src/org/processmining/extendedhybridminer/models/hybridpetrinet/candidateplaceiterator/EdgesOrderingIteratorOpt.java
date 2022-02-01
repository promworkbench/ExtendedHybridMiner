package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.Iterator;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgesOrderingIteratorOpt<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> extends CandidatePlaceIterator<N>{
	EdgePWSetMatrix<N, E> matrix;
	private int currentK;
	Iterator<Set<E>> currentIterator;
	int maxInputPlusOutput;
	
	
	public EdgesOrderingIteratorOpt(EdgesOrderingCompatibleSelStrategy<N> selectionStrategy, Set<E> edges) {
		super(selectionStrategy);
		this.matrix = new EdgePWSetMatrix<>(edges);
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
			System.out.println("LEVEL "+currentK+" NUMBER OF NEW ITEMS "+matrix.getLevel(currentK).size());
			return currentIterator.hasNext();
		}
		
		return false;
	}
	
	public PartialPlaceEvaluation<N> next() {
		if (!this.hasNext())
			throw new RuntimeException("hasNext() must be called before next()!");
		
		Set<E> currentEdgeSet = currentIterator.next();

		return EdgePWSetMatrix.buildPPEFromEdges(currentEdgeSet);

}
	
	
}
