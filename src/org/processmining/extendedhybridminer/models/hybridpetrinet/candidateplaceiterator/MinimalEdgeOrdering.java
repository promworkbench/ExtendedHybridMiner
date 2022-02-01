package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.MinimalPlaces;

public class MinimalEdgeOrdering<N> extends CandidatePlaceIterator<HybridDirectedGraphNode>{
	MinimalPlaceIteratorMatrix matrix;
	private int currentK;
	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> currentIterator;
	private int maxInputPlusOutput;
	private int currentNumberOfEdges;
	private MinimalPlaces conflictStrategy;
	int innerCounter = 0;
	private int thresholdStopIterator;
	
	
	public MinimalEdgeOrdering(EdgesOrderingCompatibleSelStrategy<HybridDirectedGraphNode> selectionStrategy, MinimalPlaces conflictStrategy, LinkedHashSet<HybridDirectedGraphEdge> edges, LinkedHashSet<HybridDirectedGraphEdge> removedEdges, LinkedHashSet<HybridDirectedGraphNode> outputNodes, LinkedHashSet<HybridDirectedGraphNode> inputNodes, MinimalPlaces conflcitStrategy, int thresholdStopIt) {
		super(selectionStrategy);
		this.conflictStrategy = conflictStrategy;
		this.matrix = new MinimalPlaceIteratorMatrix(edges, removedEdges, outputNodes, inputNodes, conflictStrategy);
		this.currentNumberOfEdges = edges.size();
		this.maxInputPlusOutput = selectionStrategy.getInputOutputSum();
		this.conflictStrategy = conflictStrategy;
        currentK=0;
		currentIterator = this.matrix.addLevel(currentK);	
		this.thresholdStopIterator = thresholdStopIt;
	}

	public boolean hasNext() {
		if (this.currentIterator.hasNext()) {
			if(innerCounter == thresholdStopIterator) {
				int n = conflictStrategy.getEdges().size();
				if (this.currentNumberOfEdges == n) {
	            	System.out.println("Iterator stopped");
					return false;
			    } else {
			    	innerCounter = 0;
			    	this.currentNumberOfEdges = n;
			    }
				
			}
			innerCounter++;
			return true;
		}
		
		if (currentK<maxInputPlusOutput) {
			currentK++;
		    int n = conflictStrategy.getEdges().size();
		    if (n == 0) {
		    	return false;
		    }
			System.out.println("---- Remaining Edges: " + n);
		    System.out.println("---- START CREATING LEVEL "+currentK);
			//this.matrix.updateEdges(edges);
		    
		    //System.out.println(edges.toString());
		    //matrix.addLevel(currentK);
			//Set<PartialPlaceEvaluation<HybridDirectedGraphNode>> level = matrix.getLevel(currentK);
		    // currentIterator=level.iterator();
			
			
			
			currentIterator = matrix.addLevel(currentK);
            //this.currentNumberOfEdges = n;
            
			//System.out.println("---- LEVEL "+currentK+" NUMBER OF NEW ITEMS "+level.size());
			innerCounter++;
			return currentIterator.hasNext();
		}
		
		return false;
	}
	
	public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
		/*if (!this.hasNext())
			throw new RuntimeException("hasNext() must be called before next()!");
		*/
		PartialPlaceEvaluation<HybridDirectedGraphNode> currentEdgeSet = currentIterator.next();

		//System.out.print("\n");
		//System.out.print("PLACE FROM ITERATOR: " + currentEdgeSet.toString());
		//System.out.print("\n");
		return currentEdgeSet;

}
	
	
}

