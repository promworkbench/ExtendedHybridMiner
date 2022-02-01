package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Class implementing the incremental generation of PlaceEvaluations from the set of edges of the Cluster.
 * The PEs are returned (via hasNext()) by **increasing** size in the following way:
 * output 1 -> input 1;
 * output 1 -> input 2;
 * ...
 * output 2 -> input 1;
 * output 2 -> input 2;
 * 
 * 
 * The ordering is: for each output -> for each input. NOTICE THAT the naming is (as usual) reversed: outputNodes are actually
 * the transitions IN INPUT to the the place, and the inputNodes are the OUTPUT transitions to the place.
 * 
 * @author demas
 *
 * @param <N>
 * @param <E>
 */
public class OutputThenInputOrderingIterator<N extends AbstractDirectedGraphNode> extends CandidatePlaceIterator<N> {
	private ArrayList<N> outputNodes;
	private ArrayList<N> inputNodes;
	private int currentOutputK, currentInputK;
	private Iterator<int[]> currentOutputIterator;
	private Iterator<int[]> currentInputIterator;
	private Set<N> currentOutputSet;
	private PartialPlaceEvaluation<N> nextElement;
	
	
	
	public OutputThenInputOrderingIterator(CandidatePlaceSelectionStrategy<N> selectionStrategy, Set<N> outputNodes, Set<N> inputNodes) {
		super(selectionStrategy);
		
		if (outputNodes==null || outputNodes.size()==0 || inputNodes==null || inputNodes.size()==0)
			throw new RuntimeException("The set of output and input nodes (transitions) should be nonempty!");
		
		this.outputNodes = new ArrayList<>(outputNodes);
		this.inputNodes = new ArrayList<>(inputNodes);
		this.currentOutputK = 1;
		this.currentInputK = 1;
		this.currentOutputIterator = CombinatoricsUtils.combinationsIterator(this.outputNodes.size(), currentOutputK);
		this.currentInputIterator = CombinatoricsUtils.combinationsIterator(this.inputNodes.size(), currentInputK);
		this.currentOutputSet = buildNodesFromIndexes(this.outputNodes, currentOutputIterator.next()); //There should be at least one element!
	
		this.nextElement=null;
		this.findNext();
	}
	
	
	public void findNext() {
		PartialPlaceEvaluation<N> possiblyNext;
		
		while (currentInputIterator.hasNext() || currentOutputIterator.hasNext() || currentInputK<inputNodes.size() || currentOutputK<outputNodes.size()) {
			Set<N> newInputs;
			Set<N> newOutputs;
			
			// Check if currentInputIterator has elements left to process.
			if (currentInputIterator.hasNext()) {
				newInputs = buildNodesFromIndexes(inputNodes, currentInputIterator.next());
				newOutputs = currentOutputSet;
			}
			
			else {
				// check if the currentOutputIterator has any element left to process
				if (currentOutputIterator.hasNext()) {
					// if so, generate such an output set
					currentOutputSet = buildNodesFromIndexes(outputNodes, currentOutputIterator.next());
					newOutputs = currentOutputSet;
					// and then re-generate the same combinations for the InputIterator without incrementing the Ks.
					this.currentInputIterator = CombinatoricsUtils.combinationsIterator(this.inputNodes.size(), currentInputK);
					newInputs = buildNodesFromIndexes(this.inputNodes, currentInputIterator.next()); // there is at least one element; 
				}
				
				// if currentOutputIterator has **not** any element left to process then, of the current Ks, we have analyzed every element.
				// Thus, we first increase the size of inputK.
				else {
					// we do not know the strategy. Try to increase the currentInput
					if (currentInputK<inputNodes.size()) {
						currentInputK++;
						this.currentInputIterator = CombinatoricsUtils.combinationsIterator(this.inputNodes.size(), currentInputK);
						this.currentOutputIterator = CombinatoricsUtils.combinationsIterator(this.outputNodes.size(), currentOutputK);
						newOutputs = buildNodesFromIndexes(this.outputNodes, this.currentOutputIterator.next()); //there is at least one element;
						this.currentOutputSet = newOutputs;
						newInputs = buildNodesFromIndexes(this.inputNodes, this.currentInputIterator.next()); //there is at least one element;
					}
					
					// increase currentOutputK and reset to one currentInputK
					else {
						if (currentOutputK<outputNodes.size()) {
							currentOutputK++;
							currentInputK=1;
							this.currentInputIterator = CombinatoricsUtils.combinationsIterator(this.inputNodes.size(), currentInputK);
							this.currentOutputIterator = CombinatoricsUtils.combinationsIterator(this.outputNodes.size(), currentOutputK);
							newOutputs = buildNodesFromIndexes(this.outputNodes, this.currentOutputIterator.next()); //there is at least one element;
							this.currentOutputSet = newOutputs;
							newInputs = buildNodesFromIndexes(this.inputNodes, this.currentInputIterator.next()); //there is at least one element;
						}
						else
							throw new RuntimeException("Error in the findNext() iterator.");
					}
				}
			}
			possiblyNext = new PartialPlaceEvaluation<N>(newOutputs, newInputs);
			if (this.getSelectionStrategy().isCandidate(possiblyNext)) {
				this.nextElement = possiblyNext;
				return;
			}
		}
		
		this.nextElement=null;
	}
	
	
	
	private static <N extends AbstractDirectedGraphNode> Set<N> buildNodesFromIndexes(ArrayList<N> nodes, int[] indexes) {
		Set<N> result = new HashSet<N>();
		for (int i=0; i<indexes.length; i++) {
			result.add(nodes.get(indexes[i]));
		}
		return result;
	}


	public boolean hasNext() {
		if (this.nextElement!=null)
			return true;
		else
			return false;
	}


	public PartialPlaceEvaluation<N> next() {
		PartialPlaceEvaluation<N> result = this.nextElement;
		this.findNext();
		return result;
	}
	
	
}
