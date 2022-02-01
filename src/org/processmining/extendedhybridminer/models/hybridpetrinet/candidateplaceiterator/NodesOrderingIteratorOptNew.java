package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;
// OUTPUT_THEN_INPUT_NUMBER_OPT
import java.util.Iterator;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class NodesOrderingIteratorOptNew<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> extends CandidatePlaceIterator<N>{
	EdgePWSetMatrixNew<N, E> matrix;
	private int currentK;
	Iterator<PartialPlaceEvaluation<N>> currentIterator;
	int maxInputPlusOutput;
	
	
	public NodesOrderingIteratorOptNew(EdgesOrderingCompatibleSelStrategy<N> selectionStrategy, Set<E> edges) {
		super(selectionStrategy);
		this.matrix = new EdgePWSetMatrixNew<>(edges);
		this.maxInputPlusOutput = selectionStrategy.getInputOutputSum();
		currentK=0;
		currentIterator = this.matrix.getLevel(currentK).iterator();
	}

	public boolean hasNext() {
		if (this.currentIterator.hasNext())
			return true;
		
		if (currentK<maxInputPlusOutput) {
			currentK++;
			System.out.println("---- START CREATING LEVEL "+currentK);
			
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
		
		PartialPlaceEvaluation<N> currentEdgeSet = currentIterator.next();

		//System.out.print("\n");
		//System.out.print("PLACE FROM ITERATOR: " + currentEdgeSet.toString());
		//System.out.print("\n");
		return currentEdgeSet;
    }
	
	@Override
	public int getCurrentLevel() {
		return currentK;
	}
	
}
