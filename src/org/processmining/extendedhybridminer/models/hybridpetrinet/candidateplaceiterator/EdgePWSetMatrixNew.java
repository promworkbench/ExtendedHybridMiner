package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.Cluster;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgePWSetMatrixNew<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> {
	Map<Integer, Set<PartialPlaceEvaluation<N>>> matrix;
	ArrayList<E> allEdges;

	public EdgePWSetMatrixNew(Set<E> edges) {
		this.matrix = new HashMap<>();
		this.allEdges = new ArrayList<>(edges);
		/*Set singleLevelSet = new HashSet<EdgeClique<N,E>>();
		for (int i=0; i<edges.size(); i++) {
			Set<N> outputPlaceSet = new HashSet<N>();
			outputPlaceSet.add((N) allEdges.get(i).getSource());
			Set<N> inputPlaceSet = new HashSet<N>();
			inputPlaceSet.add((N) allEdges.get(i).getTarget());
			PartialPlaceEvaluation<N> singleton = new PartialPlaceEvaluation<N>(outputPlaceSet,inputPlaceSet);
			singleLevelSet.add(singleton);
		}
		matrix.put(1, singleLevelSet);*/
		buildLevelMax();
	}
	
	private void buildLevelMax() {
		LinkedHashSet<PartialPlaceEvaluation<N>> maxLevelSet = new LinkedHashSet<>();
		Set<N> outputPlaceSet = new HashSet<N>();
		Set<N> inputPlaceSet = new HashSet<N>();
		for (E edge: this.allEdges) {
			outputPlaceSet.add((N) edge.getSource());
			inputPlaceSet.add((N) edge.getTarget());
		}
		PartialPlaceEvaluation<N> ppe = new PartialPlaceEvaluation<N>(outputPlaceSet,inputPlaceSet);
        maxLevelSet.add(ppe);
		matrix.put(0, maxLevelSet);
	}
	
	private void buildLevel1() {
		Set singleLevelSet = new HashSet<EdgeClique<N,E>>();
		for (int i=0; i<allEdges.size(); i++) {
			Set<N> outputPlaceSet = new HashSet<N>();
			outputPlaceSet.add((N) allEdges.get(i).getSource());
			Set<N> inputPlaceSet = new HashSet<N>();
			inputPlaceSet.add((N) allEdges.get(i).getTarget());
			PartialPlaceEvaluation<N> singleton = new PartialPlaceEvaluation<N>(outputPlaceSet,inputPlaceSet);
			singleLevelSet.add(singleton);
		}
		matrix.put(1, singleLevelSet);
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
		 * level 1: p({a}, {c}), p({b}, {c}), p({c}, {d}), p({c}, {e}),   
		 * level 2: p({a, b}, {c}), p({c}, {d, e})
		 */
		
		if (level<=0)
			throw new RuntimeException("Level 0 has already been built!");
		
		System.out.println("We're building LEVEL "+level);
		//long startTime = System.currentTimeMillis();

		if (level == 1) {
			buildLevel1();
			return;
		}
		
		Set<PartialPlaceEvaluation<N>> previousLevel = getElement(level-1);
		Set<PartialPlaceEvaluation<N>> newLevel = new HashSet<PartialPlaceEvaluation<N>>();
	
		for (PartialPlaceEvaluation<N> previousPpe : previousLevel) { // p({a}, {c})
			//output set
			Set<N> outputNodes = previousPpe.getPlaceOutputNodes();
			for (N outputNode : outputNodes) { // a
				ArrayList<E> outgoingEdges = (ArrayList<E>) outputNode.getGraph().getOutEdges(outputNode); // {a-c}
				for (E outgoingEdge : outgoingEdges) {
					if (! previousPpe.getPlaceInputNodes().contains(outgoingEdge.getTarget())){ // if (!{c} contains c)
						Set<N> newPlaceInputNodes = new HashSet<>(previousPpe.getPlaceInputNodes());
						newPlaceInputNodes.add((N) outgoingEdge.getTarget());
						PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(previousPpe.getPlaceOutputNodes(),newPlaceInputNodes);
						if (Cluster.checkPlaceConsistency(currentPPe, new HashSet<E>(allEdges))){
							newLevel.add(currentPPe);
						}
					}
				}
			}
			
			//input set
			Set<N> inputNodes = previousPpe.getPlaceInputNodes(); // {c}
			for (N inputNode : inputNodes) { // c
				ArrayList<E> incomingEdges = (ArrayList<E>) inputNode.getGraph().getInEdges(inputNode); // {a-c, b-c}
				for (E incomingEdge : incomingEdges) { // b-c
					if (! previousPpe.getPlaceOutputNodes().contains(incomingEdge.getSource())){ // if(!{a} contains b)
						Set<N> newPlaceOutputNodes = new HashSet(previousPpe.getPlaceOutputNodes());
						newPlaceOutputNodes.add((N) incomingEdge.getSource()); // {a, b}
						PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(newPlaceOutputNodes,previousPpe.getPlaceInputNodes()); // p({a,b}, {c})
						if (Cluster.checkPlaceConsistency(currentPPe, new HashSet<E>(allEdges))){
							newLevel.add(currentPPe);
						}
					}
				}
			}

		}
		
		
		
		this.matrix.put(level, newLevel);
	
		//long endTime = System.currentTimeMillis();
		//System.out.println("adding a new layer took "+(endTime-startTime)+" milliseconds ");

	}
	
	
	public Set<PartialPlaceEvaluation<N>> getLevel(int level) {
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

	public Set<PartialPlaceEvaluation<N>> getElement(int i) {
		return matrix.get(i);
	}

}
