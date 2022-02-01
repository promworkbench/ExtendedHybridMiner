package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.MinimalPlacesBackwards;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class MinimalEdgeOrderingBackwards<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> extends CandidatePlaceIterator<N>{
	EdgePWSetMatrixMinimalBackwards<N, E> matrix;
	private int currentK;
	private Iterator<PartialPlaceEvaluation<N>> currentIterator;
	//private int maxInputPlusOutput;
	private int currentNumberOfEdges;
	private MinimalPlacesBackwards<N> conflictStrategy;
	
	/*public void updateEdges(Set<HybridDirectedSureGraphEdge> edges) {
		this.matrix.updateEdges(edges);
	}*/
	
	public MinimalEdgeOrderingBackwards(EdgesOrderingCompatibleSelStrategy<N> selectionStrategy, MinimalPlacesBackwards<N> conflictStrategy, LinkedHashSet<E> edges, LinkedHashSet<N> outputNodes, LinkedHashSet<N> inputNodes) {
		super(selectionStrategy);
		this.currentNumberOfEdges = edges.size();
		this.currentK = outputNodes.size() + inputNodes.size();
		//this.maxInputPlusOutput = selectionStrategy.getInputOutputSum();
		this.matrix = new EdgePWSetMatrixMinimalBackwards<>((LinkedHashSet<HybridDirectedSureGraphEdge>) edges, outputNodes, inputNodes, currentK);
		this.conflictStrategy = conflictStrategy;
        currentIterator = this.matrix.getLevel(currentK).iterator();
	}

	public boolean hasNext() {
		if (this.currentIterator.hasNext())
			return true;
		
		if (currentK>2) {
			currentK--;
			System.out.println("---- START CREATING LEVEL "+currentK);
			LinkedHashSet<HybridDirectedSureGraphEdge> edges = conflictStrategy.getEdges();
			this.matrix.updateEdges(edges);
		    System.out.println("---- Edges Updated. Remaining: " + conflictStrategy.getEdges().size());
		    matrix.addLevel(currentK);
			int n = edges.size();
			Set<PartialPlaceEvaluation<N>> level = matrix.getLevel(currentK);
		    currentIterator=level.iterator();
		    
            if (n > this.currentNumberOfEdges*0.9) {
            	if (level.size() > 1000) {
		    	    return false;
            	}
		    }
            this.currentNumberOfEdges = n;
		    
			System.out.println("---- LEVEL "+currentK+" NUMBER OF NEW ITEMS "+level.size());
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
	
	
}


