package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgePWSetMatrixNew_FV<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> {
	Map<Pair, Set<EdgeClique<N, E>>> matrix;
	ArrayList<E> allEdges;

	public EdgePWSetMatrixNew_FV(Set<E> edges) {
		this.matrix = new HashMap<>();
		this.allEdges = new ArrayList<>(edges);
		for (int i=0; i<edges.size(); i++) {
			Pair pair = new Pair(i, 0);
			EdgeClique<N,E> singleton = new EdgeClique();
			singleton.addEdge(allEdges.get(i));
			Set<EdgeClique<N,E>> singleLevelSet = new HashSet<EdgeClique<N,E>>();
			singleLevelSet.add(singleton);
			matrix.put(pair, singleLevelSet);
		}
	}

	/*
	 * RECALL: level 1 one edge, level 2 two edges
	 */
	
	public void addLevel(int level) {
		if (level<=0)
			throw new RuntimeException("Level 0 has already been built!");

		int currMax= allEdges.size()-(level-2);
		//for (int i=0; i<allEdges.size()-1; i++) {
		for (int i=0; i<currMax; i++) {
			E currentEdge = allEdges.get(i);
			
			System.out.println("---- CURRENT EDGE "+currentEdge);

			//for (int k=level-1; k<allEdges.size(); k++) {
			for (int k=i+1; k<currMax; k++) {
				Set<EdgeClique<N,E>> previousSet = getElement(k, level-2);
				
				System.out.println(" ---- PREVIOUS SET "+previousSet);
				System.out.println(" ---- OF GROUP "+k+" AND LEVEL "+(level-2));
				if (previousSet!=null) {

					long startTime = System.currentTimeMillis();
					
					for (EdgeClique<N,E> edgeClique : previousSet) {
						long cliqueStartTime = System.currentTimeMillis();
						System.out.println(" ---- EDGE CLIQUE "+edgeClique);
						if(!edgeClique.contains(currentEdge) && 
						   (!edgeClique.getIngoingNodes().contains(currentEdge.getTarget()) && edgeClique.getOutgoingNodes().contains(currentEdge.getSource())) ||
						   (edgeClique.getIngoingNodes().contains(currentEdge.getTarget()) && !edgeClique.getOutgoingNodes().contains(currentEdge.getSource()))) {
							// does it mean that the place built starting from these edges is connected?
							Set<EdgeClique<N,E>> newCell = getElement(i, level-1);
							EdgeClique<N, E> edgeCliqueClone = null;
							try {
								edgeCliqueClone = edgeClique.clone();
							} catch (CloneNotSupportedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							edgeCliqueClone.addEdge(currentEdge);
							if (newCell==null) {
								newCell = new HashSet<EdgeClique<N,E>>();
								Pair pair = new Pair(i, level-1);
								matrix.put(pair, newCell);
							}
							newCell.add(edgeCliqueClone);
						}
							long cliqueEndTime = System.currentTimeMillis();
							System.out.println("checking a new clique took "+(cliqueEndTime-cliqueStartTime)+" milliseconds");
					}
					long endTime = System.currentTimeMillis();
					System.out.println("adding a new layer took "+(endTime-startTime)+" milliseconds ");

				}
			}
		}
		
	}
	
	
	public Set<EdgeClique<N,E>> getLevel(int level) {
		Set<EdgeClique<N,E>> result = new HashSet<>();
		
		//for (int k=(level-1); k<allEdges.size(); k++) {
		for (int k=0; k<allEdges.size()-(level-1); k++) {
			Set<EdgeClique<N,E>> toAdd = this.getElement(k, level-1);
			if (toAdd != null)
				result.addAll(toAdd);
		}

		return result;
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
	
	/*public static <N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> PartialPlaceEvaluation<N> buildPartialPEFromIndexes(ArrayList<E> edges, int[] indexes) {
		Set<N> outputNodes = new HashSet<>();
		Set<N> inputNodes = new HashSet<>();
		for (int i=0; i<indexes.length; i++) {
			E e = edges.get(indexes[i]);
			outputNodes.add((N)e.getSource());
			inputNodes.add((N)e.getTarget());
		}
		return new PartialPlaceEvaluation<N>(outputNodes, inputNodes);
	}*/

	public Set<EdgeClique<N,E>> getElement(int i, int j) {
		Pair pair = new Pair(i, j);
		return matrix.get(pair);
	}


	class Pair {
		int row, column;

		public Pair(int row, int column) {
			this.row = row;
			this.column = column;
		}


		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + column;
			result = prime * result + row;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (column != other.column)
				return false;
			if (row != other.row)
				return false;
			return true;
		}

		private EdgePWSetMatrixNew_FV<N, E> getOuterType() {
			return EdgePWSetMatrixNew_FV.this;
		}


	}



}
