package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgePWSetMatrixMinimalBackwards<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> {
	Map<Integer, LinkedHashSet<PartialPlaceEvaluation<N>>> matrix;
	LinkedHashSet<HybridDirectedSureGraphEdge> allEdges;
	//private boolean startWithOutputNodes;
	
	public EdgePWSetMatrixMinimalBackwards(LinkedHashSet<HybridDirectedSureGraphEdge> edges, LinkedHashSet<N> outputNodes, LinkedHashSet<N> inputNodes, int currentK) {
		this.matrix = new HashMap<>();
		this.allEdges = new LinkedHashSet<>();
		this.allEdges.addAll(edges);
		//this.startWithInputNodes = outputNodes.size() <= inputNodes.size();
		buildLevelMax(currentK);
	}
	
	public void updateEdges(Set<HybridDirectedSureGraphEdge> edges) {
		this.allEdges = new LinkedHashSet<HybridDirectedSureGraphEdge>();
		this.allEdges.addAll(edges);
	}
	
	private void buildLevelMax(int k) {
		LinkedHashSet<PartialPlaceEvaluation<N>> maxLevelSet = new LinkedHashSet<>();
		Set<N> outputPlaceSet = new HashSet<N>();
		Set<N> inputPlaceSet = new HashSet<N>();
		for (HybridDirectedSureGraphEdge edge: this.allEdges) {
			outputPlaceSet.add((N) edge.getSource());
			inputPlaceSet.add((N) edge.getTarget());
		}
		
		/*for (N  input : inputPlaceSet) {
			HybridDirectedGraphNode target = (HybridDirectedGraphNode) input;
			for (N output: outputPlaceSet) {
				HybridDirectedGraphNode source = (HybridDirectedGraphNode) output;
				HybridDirectedSureGraphEdge e = new HybridDirectedSureGraphEdge(source, target);
				if (!this.allEdges.contains(e)) {
					matrix.put(k, maxLevelSet);
					return;
				}
			}
		}*/
		
		PartialPlaceEvaluation<N> ppe = new PartialPlaceEvaluation<N>(outputPlaceSet,inputPlaceSet);
        maxLevelSet.add(ppe);
		matrix.put(k, maxLevelSet);
	}


	/* Humam
	 * After initialization, matrix[1] = {place(e1), place(e2), ..}
	 */

	/*
	 * RECALL: level 1 one edge, level 2 two edges
	 */

	public void addLevel(int level) {
		/*
		 * E.g. edges: a-c b-c c-d c-e
		 * level i: p({a, b}, {c}), p({c}, {d, e})
		 * level i+1: p({a}, {c}), p({b}, {c}), p({c}, {d}), p({c}, {e}),   
		 * 
		 */
		System.out.println("We're building LEVEL "+level);
		//long startTime = System.currentTimeMillis();

		LinkedHashSet<PartialPlaceEvaluation<N>> previousLevel = getElement(level+1);
		LinkedHashSet<PartialPlaceEvaluation<N>> newLevel = new LinkedHashSet<PartialPlaceEvaluation<N>>();
	
		for (PartialPlaceEvaluation<N> previousPpe : previousLevel) { // p({a}, {c})
			Set<N> outputNodes = previousPpe.getPlaceOutputNodes();
			Set<N> inputNodes = previousPpe.getPlaceInputNodes();

			if (inputNodes.size() > 1) {
				addLevelInput(newLevel, outputNodes, inputNodes);
			}
			if (outputNodes.size() > 1) {
				addLevelOutput(newLevel, outputNodes, inputNodes);
			}
			
		}



		this.matrix.put(level, newLevel);
	
		//long endTime = System.currentTimeMillis();
		//System.out.println("adding a new layer took "+(endTime-startTime)+" milliseconds ");

	}
	
	private void addLevelInput(LinkedHashSet<PartialPlaceEvaluation<N>> newLevel, Set<N> outputNodes, Set<N> inputNodes) {
		outerloop:
			for (N inputNode : inputNodes) { // d
				Set<N> newInputNodes = new HashSet<>(inputNodes);
				newInputNodes.remove(inputNode); //{e}
				/*for (N  input : newInputNodes) {
					HybridDirectedGraphNode target = (HybridDirectedGraphNode) input;
					for (N output: outputNodes) {
						HybridDirectedGraphNode source = (HybridDirectedGraphNode) output;
						HybridDirectedSureGraphEdge e = new HybridDirectedSureGraphEdge(source, target);
						if (!this.allEdges.contains(e)) {
							continue outerloop;
						}
					}
				}*/
				PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(outputNodes, newInputNodes);
				newLevel.add(currentPPe);
			}
	}
	
	
	
	private void addLevelOutput(LinkedHashSet<PartialPlaceEvaluation<N>> newLevel, Set<N> outputNodes, Set<N> inputNodes) {
		outerloop:
			for (N outputNode : outputNodes) { // d
				Set<N> newOutputNodes = new HashSet<>(outputNodes);
				newOutputNodes.remove(outputNode); //{e}
				/*for (N  input : inputNodes) {
					HybridDirectedGraphNode target = (HybridDirectedGraphNode) input;
					for (N output: newOutputNodes) {
						HybridDirectedGraphNode source = (HybridDirectedGraphNode) output;
						HybridDirectedSureGraphEdge e = new HybridDirectedSureGraphEdge(source, target);
						if (!this.allEdges.contains(e)) {
							continue outerloop;
						}
					}
				}*/
				PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(newOutputNodes, inputNodes);
				newLevel.add(currentPPe);
			}
	}
	
	public LinkedHashSet<PartialPlaceEvaluation<N>> getLevel(int level) {
		//Set<EdgeClique<N,E>> result = new HashSet<>();
		return matrix.get(level);
	}
	

	public static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> PartialPlaceEvaluation<N> buildPPEFromEdges(EdgeClique<N,E> edges) {
		Set<N> outputNodes = edges.getOutgoingNodes();
		Set<N> inputNodes = edges.getIngoingNodes();
		/*for (E e : edges) {
			outputNodes.add((N)e.getSource());
			inputNodes.add((N)e.getTarget());
		}*/
		return new PartialPlaceEvaluation<N>(outputNodes, inputNodes);
		
	}

	public LinkedHashSet<PartialPlaceEvaluation<N>> getElement(int i) {
		return matrix.get(i);
	}

}

