package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.Cluster;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgePWSetMatrix<N extends AbstractDirectedGraphNode, E extends AbstractDirectedGraphEdge> {
	Map<Pair, Set<Set<E>>> matrix;
	ArrayList<E> allEdges;

	public EdgePWSetMatrix(Set<E> edges) {
		this.matrix = new HashMap<>();
		this.allEdges = new ArrayList<>(edges);
		System.out.println("BUILDING LEVEL 0");
		for (int i=0; i<edges.size(); i++) {
			Pair pair = new Pair(i, 0);
			Set<E> singleton = new HashSet<>();
			singleton.add(allEdges.get(i));
			Set<Set<E>> singleLevelSet = new HashSet<Set<E>>();
			singleLevelSet.add(singleton);
			matrix.put(pair, singleLevelSet);
		}
	}

	/*
	 * RECALL: level 1 one edge, level 2 two edges
	 */
/*	public void addLevel(int level) {
		if (level<=0)
			throw new RuntimeException("Level 0 has already been built!");

		for (int i=0; i<allEdges.size(); i++) {
			E currentEdge = allEdges.get(i);
			
			System.out.println("CURRENT EDGE "+currentEdge);

			for (int k=level-1; k<=allEdges.size(); k++) {
				Set<Set<E>> previousSet = getElement(k, level-2);
				
				System.out.println(" PREVIOUS SET "+previousSet);
				System.out.println(" OF LEVEL "+k);
				if (previousSet!=null) {

					long startTime = System.currentTimeMillis();
					
					for (Set<E> edgeClique : previousSet) {
						System.out.println(" EDGE CLIQUE "+edgeClique);
						if(!edgeClique.contains(currentEdge)) {
							long cliqueStartTime = System.currentTimeMillis();
							Set<E> edgeUnion = new HashSet<>();
							edgeUnion.add(currentEdge);
							edgeUnion.addAll(edgeClique);
							PartialPlaceEvaluation<N> ppe = buildPPEFromEdges(edgeUnion);
							//if (Cluster.checkPlaceConsistency(ppe, edgeUnion)) {
							if (Cluster.checkPlaceConsistency(ppe, new HashSet<E>(allEdges))) {
								Set<Set<E>> newCell = getElement(i, level-1);
								if (newCell==null) {
									newCell = new HashSet<Set<E>>();
									Pair pair = new Pair(i, level-1);
									matrix.put(pair, newCell);
								}
								newCell.add(edgeUnion);
							}
							long cliqueEndTime = System.currentTimeMillis();
							System.out.println("checking a new clique took "+(cliqueEndTime-cliqueStartTime)+" milliseconds");
						}
					}
					long endTime = System.currentTimeMillis();
					System.out.println("adding a new layer took "+(endTime-startTime)+" milliseconds ");

				}
			}
		}
	}*/
	
	public void addLevel(int level) {
		if (level<=0)
			throw new RuntimeException("Level 0 has already been built!");
		
		System.out.println("WE ARE BUILDING LEVEL "+level);
		long startTime = System.currentTimeMillis();

		for (int i=0; i<allEdges.size(); i++) {
			E currentEdge = allEdges.get(i);
			
			//System.out.println("CURRENT EDGE "+currentEdge);

			//for (int k=level-1; k<=allEdges.size(); k++) {
			for (int k=1; k<=(allEdges.size()-(level-1)); k++) {
				Set<Set<E>> previousSet = getElement(k, level-2);

				//System.out.println(" PREVIOUS SET "+previousSet+" OF GROUP "+k+" OF LEVEL "+(level-2));
				if (previousSet!=null) {

					
					for (Set<E> edgeClique : previousSet) {
						//System.out.println(" EDGE CLIQUE "+edgeClique);
						if(!edgeClique.contains(currentEdge)) {
							long cliqueStartTime = System.currentTimeMillis();
							Set<E> edgeUnion = new HashSet<>();
							edgeUnion.add(currentEdge);
							edgeUnion.addAll(edgeClique);
							PartialPlaceEvaluation<N> ppe = buildPPEFromEdges(edgeUnion);
							//if (Cluster.checkPlaceConsistency(ppe, edgeUnion)) {
							if (Cluster.checkPlaceConsistency(ppe, new HashSet<E>(allEdges))) {
								Set<Set<E>> newCell = getElement(i, level-1);
								if (newCell==null) {
									newCell = new HashSet<Set<E>>();
									Pair pair = new Pair(i, level-1);
									matrix.put(pair, newCell);
								}
								newCell.add(edgeUnion);
								//System.out.println("ADDED "+newCell+" IN POSITION ("+i+","+(level-1)+")");
							}
							long cliqueEndTime = System.currentTimeMillis();
							//System.out.println("checking a new clique took "+(cliqueEndTime-cliqueStartTime)+" milliseconds");
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		//System.out.println("adding a new layer took "+(endTime-startTime)+" milliseconds ");
		System.out.println("LEVEL "+level+" took "+(endTime-startTime)+" milliseconds ");

	}
	
	
	public Set<Set<E>> getLevel(int level) {
		Set<Set<E>> result = new HashSet<>();
		
		//for (int k=(level-1); k<allEdges.size(); k++) {
		for (int k=0; k<allEdges.size()-(level-1); k++) {
			Set<Set<E>> toAdd = this.getElement(k, level-1);
			if (toAdd != null)
				result.addAll(toAdd);
		}

		return result;
	}
	

	public static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> PartialPlaceEvaluation<N> buildPPEFromEdges(Set<E> edges) {
		Set<N> outputNodes = new HashSet<>();
		Set<N> inputNodes = new HashSet<>();
		for (E e : edges) {
			outputNodes.add((N)e.getSource());
			inputNodes.add((N)e.getTarget());
		}
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

	public Set<Set<E>> getElement(int i, int j) {
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

		private EdgePWSetMatrix<N, E> getOuterType() {
			return EdgePWSetMatrix.this;
		}


	}



}
