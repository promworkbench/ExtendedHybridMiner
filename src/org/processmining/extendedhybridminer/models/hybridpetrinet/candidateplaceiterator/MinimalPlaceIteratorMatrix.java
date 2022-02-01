package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.MinimalPlaces;

public class MinimalPlaceIteratorMatrix {
	Map<Integer, LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>> matrix;
	LinkedHashSet<HybridDirectedGraphEdge> allEdges;
	private HashSet<HybridDirectedGraphNode> outputNodes;
	private HashSet<HybridDirectedGraphNode> inputNodes;
	private MinimalPlaces conflictStrategy;
	private Set<PartialPlaceEvaluation<HybridDirectedGraphNode>> currentLevelCheckedPlaces;
	
	
	public MinimalPlaceIteratorMatrix(LinkedHashSet<HybridDirectedGraphEdge> edges, LinkedHashSet<HybridDirectedGraphEdge> removedEdges, LinkedHashSet<HybridDirectedGraphNode> outputNodes, LinkedHashSet<HybridDirectedGraphNode> inputNodes, MinimalPlaces conflictStrategy) {
		this.matrix = new HashMap<>();
		this.allEdges = edges;
		this.inputNodes = inputNodes;
		this.outputNodes = outputNodes;
		this.conflictStrategy = conflictStrategy;
	}
	
	
	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> buildLevelMax() {		
		LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> maxLevelSet = new LinkedHashSet<>();		
		for (HybridDirectedGraphNode  input : inputNodes) {
			for (HybridDirectedGraphNode output: outputNodes) {
				HybridDirectedGraphEdge e = conflictStrategy.getEdge(output, input);	
				if (e == null) { // if no direct edge exists
					matrix.put(0, maxLevelSet);
					return maxLevelSet.iterator(); // return empty level
				}
			}
		}
		PartialPlaceEvaluation<HybridDirectedGraphNode> ppe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(outputNodes,inputNodes);
		maxLevelSet.add(ppe);
		mapPlaceToEdges(ppe);
		matrix.put(0, maxLevelSet);
		return maxLevelSet.iterator();
	}

	
	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> buildLevel1() {
		LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> singleLevelSet = new LinkedHashSet<>();
		for (HybridDirectedGraphEdge edge: allEdges) {
			Set<HybridDirectedGraphNode> outputPlaceSet = new HashSet<HybridDirectedGraphNode>();
			outputPlaceSet.add(edge.getSource());
			Set<HybridDirectedGraphNode> inputPlaceSet = new HashSet<HybridDirectedGraphNode>();
			inputPlaceSet.add(edge.getTarget());
			PartialPlaceEvaluation<HybridDirectedGraphNode> singleton = new PartialPlaceEvaluation<HybridDirectedGraphNode>(outputPlaceSet,inputPlaceSet);
			singleLevelSet.add(singleton);
			edge.addPlace(singleton);
			singleton.addEdge(edge);
		}
		matrix.put(1, singleLevelSet);
		return singleLevelSet.iterator();
	}

	
	public Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> addLevel(int level) {
		if (level == 0) {
			return buildLevelMax();
		}
		if (level == 1) {
			return buildLevel1();
		}
		
		currentLevelCheckedPlaces = new HashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>();
		LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> newLevel = new LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>();
		Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> placeIt = this.matrix.get(level-1).iterator();
		
		return new Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> () {

			Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> newIt = (new HashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>()).iterator();
			PartialPlaceEvaluation<HybridDirectedGraphNode> place;
			boolean inputPlacesAddedOutputPlacesNotAdded = false;
			
			public boolean hasNext() {
				if (place != null && !checkPlace(false)) {
					return false;
				}
				while (!newIt.hasNext()) {
					if (inputPlacesAddedOutputPlacesNotAdded) {
						newIt = addLevelOutput(newLevel, place.getPlaceOutputNodes(), place.getPlaceInputNodes());
						inputPlacesAddedOutputPlacesNotAdded = false;
					} else {
						if (!findNextPlace()) {
							return false;
						}
					}
				}
				return true;
			}

			private boolean findNextPlace() {
				if (placeIt.hasNext()) {
					place = placeIt.next();
					return checkPlace(true);
				} else {
					matrix.put(level, newLevel);
					return false;
				}
			}

			private boolean checkPlace(boolean changed) {
			    if (place.isConflicting()) {
					changed = true;
					do {
						if (placeIt.hasNext()) {
							place = placeIt.next();
						} else {
							matrix.put(level, newLevel);
							return false;
						}
					} while (place.isConflicting());
				}
				
				if (changed) {
					newIt = addLevelInput(newLevel, place.getPlaceOutputNodes(), place.getPlaceInputNodes());
					inputPlacesAddedOutputPlacesNotAdded = true;
				}
				return true;
			}

			
			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				return newIt.next();
			}	
		};
	}
	

	private PartialPlaceEvaluation<HybridDirectedGraphNode> addPlaceSource(Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes, HybridDirectedGraphNode source) {
		Set<HybridDirectedGraphNode> newPlaceOutputNodes = new HashSet<>(oNodes);
		newPlaceOutputNodes.add(source);
		PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(newPlaceOutputNodes, iNodes);
		return currentPPe;
	}

   
   private PartialPlaceEvaluation<HybridDirectedGraphNode> addPlaceTarget(Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes, HybridDirectedGraphNode target) {
	   Set<HybridDirectedGraphNode> newPlaceInputNodes = new HashSet<>(iNodes);
	   newPlaceInputNodes.add(target);
	   PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(oNodes, newPlaceInputNodes);
	   return currentPPe;
   }
   	
	
	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> addLevelOutput(LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> newLevel, Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes) {

        Iterator<HybridDirectedGraphNode> oNodesIt = outputNodes.iterator();
		
        return new Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> () {

			HybridDirectedGraphNode next;
			PartialPlaceEvaluation<HybridDirectedGraphNode> nextPlace;
			
			public boolean hasNext() {	
				whileLoop:
				while(oNodesIt.hasNext()) {
					next = oNodesIt.next();
					if (oNodes.contains(next)) {
						continue whileLoop;
					}					
					nextPlace = addPlaceSource(oNodes, iNodes, next);
					if (!currentLevelCheckedPlaces.add(nextPlace)) {
						continue whileLoop;
					}				
					boolean add = true;
					for (HybridDirectedGraphNode  target : iNodes) {
						HybridDirectedGraphEdge e = conflictStrategy.getEdge(next, target);
						if (e == null) {
							add = false;
						} else {
							if (oNodes.contains(target)) {
								if (mapPlaceToEdges(nextPlace)) {
									newLevel.add(nextPlace);
									return true;	
								}
							}
						}
					}
					if (add) {
						newLevel.add(nextPlace);// {
						mapPlaceToEdges(nextPlace);
						return true;
					}
				}			
				return false;
			}

			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				return nextPlace;
			}
			
		};
	}

	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> addLevelInput(LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> newLevel, Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes) {

        Iterator<HybridDirectedGraphNode> iNodesIt = inputNodes.iterator();
		
		return new Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> () {

			HybridDirectedGraphNode next;
			PartialPlaceEvaluation<HybridDirectedGraphNode> nextPlace;
			
			public boolean hasNext() {
				whileLoop:
				while(iNodesIt.hasNext()) {
					next = iNodesIt.next();				
					if (iNodes.contains(next)) {
						continue whileLoop;
					}
					nextPlace = addPlaceTarget(oNodes, iNodes, next);
					
					if (!currentLevelCheckedPlaces.add(nextPlace)) {
						continue whileLoop;
					}
					boolean add = true;
					for (HybridDirectedGraphNode  source : oNodes) {
						HybridDirectedGraphEdge e = conflictStrategy.getEdge(source, next);
						if (e == null) {
							add = false;
						} else {
							if (iNodes.contains(source)) {
								if (mapPlaceToEdges(nextPlace)) { // if no covered edges are removed
									newLevel.add(nextPlace);
									return true;	
								}
							}
						}
					}
					if (add) {
						newLevel.add(nextPlace);
						mapPlaceToEdges(nextPlace);
						return true;
					}
				}			
				return false;
			}

			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				return nextPlace;
			}
			
		};
	}
	
	
	// returns true if no covered edges are removed
	public boolean mapPlaceToEdges(PartialPlaceEvaluation<HybridDirectedGraphNode> ppe) {
		for (HybridDirectedGraphNode  target : ppe.getPlaceInputNodes()) {
			for (HybridDirectedGraphNode source: ppe.getPlaceOutputNodes()) {
				HybridDirectedGraphEdge e = this.conflictStrategy.getEdge(source, target);
				if (e == null) {
					e = this.conflictStrategy.addTransitiveEdge(source, target); //get or create if not removed
					if (e == null) { // if e removed (as transitive or direct Edge)
						ppe.setConfliting(true);
						return false;
					} else {
						ppe.addTransitiveCausality(e);
						e.addPlace(ppe);
					}
				} else {
					e.addPlace(ppe);
					ppe.addEdge(e);
				}
			}
		}
		return true;
	}


	/*public int getSize(int i) {
		//System.out.print("Size Level " + i + ": ");
		//System.out.println(matrix.get(i).size());
		return matrix.get(i).size();
	}*/

}

