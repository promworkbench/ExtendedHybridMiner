package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Class implementing the incremental generation of PlaceEvaluations from the set of edges of the Cluster.
 * The PEs are returned (via hasNext()) by **increasing** size. The size of a PE is the sum of its input and outputPlaces. This is clearly not a total order,
 * hence places with the same sum of input and outputs are returned in any order.
 * 
 * @author demas
 *
 * @param <N>
 * @param <E>
 */
public class EdgesOrderingIterator<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> extends CandidatePlaceIterator<N> {
	private ArrayList<E> edges;
	private int currentK;
	private Iterator<int[]> currentIterator;
	private PartialPlaceEvaluation<N> nextElement;



	public EdgesOrderingIterator(CandidatePlaceSelectionStrategy<N> selectionStrategy, Set<E> edges) {
		super(selectionStrategy);

		if (edges==null || edges.size()==0)
			throw new RuntimeException("The set of output and input nodes (transitions) should be nonempty!");

		this.edges = new ArrayList<>(edges);
		this.currentK = 1;
		this.currentIterator = CombinatoricsUtils.combinationsIterator(edges.size(), currentK);

		this.nextElement = null;
		//nextElement is assigned by hasNext.
		this.findNext();
	}


	private void findNext() {
		PartialPlaceEvaluation<N> possiblyNext;

		while (currentIterator.hasNext() || currentK<edges.size()) {
			// first check if the currentIterator has elements left
			if(!this.currentIterator.hasNext()) {
				currentK++;
				this.currentIterator = CombinatoricsUtils.combinationsIterator(edges.size(), currentK);
			}
			
			possiblyNext = buildPartialPEFromIndexes(this.edges, this.currentIterator.next());
			if ( this.getSelectionStrategy().isCandidate(possiblyNext) ) {
				nextElement = possiblyNext;
				return;
			}
		}
		this.nextElement=null;
	}


	public static <N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> PartialPlaceEvaluation<N> buildPartialPEFromIndexes(ArrayList<E> edges, int[] indexes) {
		Set<N> outputNodes = new HashSet<>();
		Set<N> inputNodes = new HashSet<>();
		for (int i=0; i<indexes.length; i++) {
			E e = edges.get(indexes[i]);
			outputNodes.add((N)e.getSource());
			inputNodes.add((N)e.getTarget());
		}
		return new PartialPlaceEvaluation<N>(outputNodes, inputNodes);
	}


	public boolean hasNext() {
		return this.nextElement!=null;
	}


	public PartialPlaceEvaluation<N> next() {
		PartialPlaceEvaluation<N> result = this.nextElement;
		this.findNext();
		return result;
	}


}
