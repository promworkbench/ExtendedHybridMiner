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
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class EdgePWSetMatrixMinimalOLD {
	Map<Integer, LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>> matrix;
	LinkedHashSet<HybridDirectedGraphEdge> allEdges;
	LinkedHashSet<HybridDirectedGraphEdge> removedEdges;
	//LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> allPlaces;
	private HashSet<HybridDirectedGraphNode> outputNodes;
	private HashSet<HybridDirectedGraphNode> inputNodes;
	//private HashSet<HybridDirectedGraphNode> loopNodes;
	
	public EdgePWSetMatrixMinimalOLD(LinkedHashSet<HybridDirectedGraphEdge> edges, LinkedHashSet<HybridDirectedGraphEdge> removedEdges, LinkedHashSet<HybridDirectedGraphNode> outputNodes, LinkedHashSet<HybridDirectedGraphNode> inputNodes) {
		this.matrix = new HashMap<>();
		//this.allEdges = new LinkedHashSet<>();
	    //this.allEdges.addAll(edges);
		this.allEdges = edges;
		//this.removedEdges = removedEdges;
		//this.allPlaces = new LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>();
		//this.startWithInputNodes = outputNodes.size() <= inputNodes.size();
		//buildLevelMax();
	}
	
	/*public void updateEdges(Set<HybridDirectedGraphEdge> edges) {
		//this.allEdges = new LinkedHashSet<HybridDirectedGraphEdge>();
		//this.allEdges.addAll(edges);
	}*/
	
	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> buildLevelMax() {
		LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> maxLevelSet = new LinkedHashSet<>();
		this.outputNodes = new HashSet<HybridDirectedGraphNode>();
		this.inputNodes = new HashSet<HybridDirectedGraphNode>();
		for (HybridDirectedGraphEdge edge: this.allEdges) {
			HybridDirectedGraphNode source = edge.getSource();
			outputNodes.add(source);
			HybridDirectedGraphNode target = edge.getTarget();
			inputNodes.add(target);
			/*if (source.getLabel() == target.getLabel()) {
				loopNodes.add(source);
			}*/
		}
		
		for (HybridDirectedGraphNode  input : inputNodes) {
			for (HybridDirectedGraphNode output: outputNodes) {
				HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(output, input);
				if (!this.allEdges.contains(e)) {
					matrix.put(0, maxLevelSet);
					return maxLevelSet.iterator();
				} 
			}
		}
		
		PartialPlaceEvaluation<HybridDirectedGraphNode> ppe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(outputNodes,inputNodes);
		maxLevelSet.add(ppe);
		matrix.put(0, maxLevelSet);
		return maxLevelSet.iterator();
		
	}

	private Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> buildLevel1() {
		LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> singleLevelSet = new LinkedHashSet<>();
		/*Iterator<HybridDirectedGraphEdge> edgeIt = this.allEdges.iterator();
		
		return new Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> () {

			HybridDirectedGraphEdge next;
			
			public boolean hasNext() {
				while(edgeIt.hasNext()) {
					next = edgeIt.next();
					if (!removedEdges.contains(next)) {
						return true;
					}
				}
				matrix.put(1, singleLevelSet);
				return false;
			}

			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				Set<HybridDirectedGraphNode> outputPlaceSet = new HashSet<HybridDirectedGraphNode>();
				outputPlaceSet.add(next.getSource());
				Set<HybridDirectedGraphNode> inputPlaceSet = new HashSet<HybridDirectedGraphNode>();
				inputPlaceSet.add(next.getTarget());
				PartialPlaceEvaluation<HybridDirectedGraphNode> singleton = new PartialPlaceEvaluation<HybridDirectedGraphNode>(outputPlaceSet,inputPlaceSet);
				singleLevelSet.add(singleton);
				return singleton;
			}
			
		};*/
		
		for (HybridDirectedGraphEdge edge: this.allEdges) {
			Set<HybridDirectedGraphNode> outputPlaceSet = new HashSet<HybridDirectedGraphNode>();
			outputPlaceSet.add(edge.getSource());
			Set<HybridDirectedGraphNode> inputPlaceSet = new HashSet<HybridDirectedGraphNode>();
			inputPlaceSet.add(edge.getTarget());
			PartialPlaceEvaluation<HybridDirectedGraphNode> singleton = new PartialPlaceEvaluation<HybridDirectedGraphNode>(outputPlaceSet,inputPlaceSet);
			singleLevelSet.add(singleton);
		}
		matrix.put(1, singleLevelSet);
		return singleLevelSet.iterator();

	}

	
	
	

	/*public void addLevel2(int level) {
		/*
		 * E.g. edges: a-c b-c c-d c-e
		 * level 1: p({a}, {c}), p({b}, {c}), p({c}, {d}), p({c}, {e}),   
		 * level 2: p({a, b}, {c}), p({c}, {d, e})
		 
		//System.out.println("We're building LEVEL "+level);
		//long startTime = System.currentTimeMillis();

		if (level == 1) {
			buildLevel1();
			return;
		}
		LinkedHashSet<PartialPlaceEvaluation<N>> previousLevel = getElement(level-1);
		LinkedHashSet<PartialPlaceEvaluation<N>> newLevel = new LinkedHashSet<PartialPlaceEvaluation<N>>();
	
		for (PartialPlaceEvaluation<N> previousPpe : previousLevel) { // p({a}, {c})
			Set<N> oNodes = previousPpe.getPlaceOutputNodes();
			Set<N> iNodes = previousPpe.getPlaceInputNodes();

			addLevelInput2(newLevel, oNodes, iNodes);
			addLevelOutput2(newLevel, oNodes, iNodes);
			
		}



		this.matrix.put(level, newLevel);
	
		//long endTime = System.currentTimeMillis();
		//System.out.println("adding a new layer took "+(endTime-startTime)+" milliseconds ");

	}*/
	
	public Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> addLevel(int level) {
		if (level == 0) {
			return buildLevelMax();
		}
		if (level == 1) {
			return buildLevel1();
		}
		
		LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> newLevel = new LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>();
		Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> placeIt = this.matrix.get(level-1).iterator();
		
		return new Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> () {

			Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> newIt = (new HashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>>()).iterator();
			PartialPlaceEvaluation<HybridDirectedGraphNode> nextPlace;
			//PartialPlaceEvaluation<HybridDirectedGraphNode> next;
			boolean inputPlacesAddedOutputPlacesNotAdded = false;
			
			public boolean hasNext() {
				while (!newIt.hasNext()) {
					if (inputPlacesAddedOutputPlacesNotAdded) {
						newIt = addLevelOutput(newLevel, nextPlace.getPlaceOutputNodes(), nextPlace.getPlaceInputNodes());
						inputPlacesAddedOutputPlacesNotAdded = false;
					} else if (placeIt.hasNext()) {
						nextPlace = placeIt.next();
						newIt = addLevelInput(newLevel, nextPlace.getPlaceOutputNodes(), nextPlace.getPlaceInputNodes());
						inputPlacesAddedOutputPlacesNotAdded = true;
					} else {
						matrix.put(level, newLevel);
						return false;
					}
				}
				return true;
			}

			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				return newIt.next();
			}
			
		};
		
		
		
		/*LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> previousLevel = getElement(level-1);
		
		outerLoop:
		for (PartialPlaceEvaluation<HybridDirectedGraphNode> previousPpe : previousLevel) { // p({a}, {c})
			Set<HybridDirectedGraphNode> oNodes = previousPpe.getPlaceOutputNodes();
			Set<HybridDirectedGraphNode> iNodes = previousPpe.getPlaceInputNodes();

			for (HybridDirectedGraphEdge edge : this.removedEdges) { // a
				if (oNodes.contains(edge.getSource()) && iNodes.contains(edge.getTarget())) {
					continue outerLoop;
				}
			}
			
			addLevelInput(newLevel, oNodes, iNodes);
			addLevelOutput(newLevel, oNodes, iNodes);	
		}

		this.matrix.put(level, newLevel);*/
	
	}
	
	
	private boolean edgeContained(HybridDirectedGraphNode source, Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes, LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> level) {
		
		boolean res = false;
		/*if (this.allEdges.contains(e)) {
			return true;
		}*/
		
		//if (source.getLabel().equals(target.getLabel())) {
		//	return true;
		//}/*else if (this.removedEdges.contains(e)) {
		/*	return false;
		} else {*/
		//Set<HybridDirectedGraphNode> loopNodes = new HashSet<HybridDirectedGraphNode> (loopN);
		//loopNodes.remove(source);
		//loopNodes.remove(target);

		//System.out.println(source.toString() + " ---> " + target.toString());
		for (HybridDirectedGraphNode  mid : oNodes) {
			//System.out.println("mid: " + mid.toString());

			HybridDirectedGraphEdge e1 = new HybridDirectedGraphEdge(source, mid);
			//HybridDirectedGraphEdge e2 = new HybridDirectedGraphEdge(mid, target);
			if (allEdges.contains(e1)) {
				res = true;
				Set<HybridDirectedGraphNode> newPlaceOutputNodes = new HashSet<>(oNodes);
				newPlaceOutputNodes.remove(mid);
				newPlaceOutputNodes.add(source);
				PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(newPlaceOutputNodes, iNodes);
				//if (allPlaces.add(currentPPe)) {
					level.add(currentPPe);
				//}
				
			}
			//Set<HybridDirectedGraphNode> loopNodesNew = new HashSet<HybridDirectedGraphNode>(loopNodes);
			//loopNodesNew.remove(mid);
			
			//if (edgeContained(source, mid, e1, loopNodesNew) && edgeContained(mid, target, e2, loopNodesNew)) {
			//	return true;
			//}
		}

		//}
		return res;
	}
	
	
private boolean edgeContainedTarget(HybridDirectedGraphNode target, Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes, LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> level) {
		
		boolean res = false;
		/*if (this.allEdges.contains(e)) {
			return true;
		}*/
		
		//if (source.getLabel().equals(target.getLabel())) {
		//	return true;
		//}/*else if (this.removedEdges.contains(e)) {
		/*	return false;
		} else {*/
		//Set<HybridDirectedGraphNode> loopNodes = new HashSet<HybridDirectedGraphNode> (loopN);
		//loopNodes.remove(source);
		//loopNodes.remove(target);

		//System.out.println(source.toString() + " ---> " + target.toString());
		for (HybridDirectedGraphNode  mid : iNodes) {
			//System.out.println("mid: " + mid.toString());

			HybridDirectedGraphEdge e1 = new HybridDirectedGraphEdge(mid, target);
			//HybridDirectedGraphEdge e2 = new HybridDirectedGraphEdge(mid, target);
			if (allEdges.contains(e1)) {
				res = true;
				Set<HybridDirectedGraphNode> newPlaceInputNodes = new HashSet<>(iNodes);
				newPlaceInputNodes.remove(mid);
				newPlaceInputNodes.add(target);
				PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(oNodes, newPlaceInputNodes);
				//if (allPlaces.add(currentPPe)) {
					level.add(currentPPe);
				//}
				
			}
			//Set<HybridDirectedGraphNode> loopNodesNew = new HashSet<HybridDirectedGraphNode>(loopNodes);
			//loopNodesNew.remove(mid);
			
			//if (edgeContained(source, mid, e1, loopNodesNew) && edgeContained(mid, target, e2, loopNodesNew)) {
			//	return true;
			//}
		}

		//}
		return res;
	}


   private PartialPlaceEvaluation<HybridDirectedGraphNode> addPlaceSource(Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes, HybridDirectedGraphNode source) {
	   Set<HybridDirectedGraphNode> newPlaceOutputNodes = new HashSet<>(oNodes);
		newPlaceOutputNodes.add(source);
		PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(newPlaceOutputNodes, iNodes);
		return currentPPe;
		//newLevel.add(currentPPe);
   }
   
   
   private PartialPlaceEvaluation<HybridDirectedGraphNode> addPlaceTarget(Set<HybridDirectedGraphNode> oNodes, Set<HybridDirectedGraphNode> iNodes, HybridDirectedGraphNode target) {
	   Set<HybridDirectedGraphNode> newPlaceInputNodes = new HashSet<>(iNodes);
	   newPlaceInputNodes.add(target);
	   PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(oNodes, newPlaceInputNodes);
	   //newLevel.add(currentPPe);
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
					
					boolean add = true;
					for (HybridDirectedGraphNode  target : iNodes) {
						HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(next, target);
						if (allEdges.contains(e)) {
							if (oNodes.contains(target)) {
								nextPlace = addPlaceSource(oNodes, iNodes, next);
								if (newLevel.add(nextPlace)) {
									return true;
								} else {
									continue whileLoop;
								}
							}
						} else {
							add = false;
						}
					}
					if (add) {
						nextPlace = addPlaceSource(oNodes, iNodes, next);
						if (newLevel.add(nextPlace)) {
							return true;
						}
					}
				}
				
				return false;
			}

			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				return nextPlace;
			}
			
		};
		
		
		/*outerLoop: 
			for (HybridDirectedGraphNode source : outputNodes) {
				if (oNodes.contains(source)) {
					continue outerLoop;
				}
				boolean add = true;
				for (HybridDirectedGraphNode  target : iNodes) {

					HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);


					if (this.allEdges.contains(e)) {
						if (oNodes.contains(target)) {
							addPlaceSource(newLevel, oNodes, iNodes, source);
							continue outerLoop; 
						}
					} else {
						add = false;
					}
				}
				if (add) {
					addPlaceSource(newLevel, oNodes, iNodes, source);
				}
			}*/

		
		
		/*outerLoop:
			for (HybridDirectedGraphNode source : outputNodes) { // a
				//boolean directCausalities = true;
				for (HybridDirectedGraphNode oNode: oNodes) {
					if (oNode.equals(source)) {
						continue outerLoop;
					}
					HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
					
				}
				if (oNodes.contains(source)) {
					continue;
				}
				for (HybridDirectedGraphNode  target : iNodes) {
					HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
					if (! this.allEdges.contains(e)) {
						if (edgeContained(source, oNodes, iNodes, newLevel)) {
							//directCausalities = false;
						} else {
							continue outerLoop;
						}
					}
				}

				Set<HybridDirectedGraphNode> newPlaceOutputNodes = new HashSet<>(oNodes);
				newPlaceOutputNodes.add(source);
				PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(newPlaceOutputNodes, iNodes);
				if (allPlaces.add(currentPPe)) {
					newLevel.add(currentPPe);
				}
				
			}*/
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
					
					boolean add = true;
					for (HybridDirectedGraphNode  source : oNodes) {
						HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, next);
						if (allEdges.contains(e)) {
							if (iNodes.contains(source)) {
								nextPlace = addPlaceTarget(oNodes, iNodes, next);
								if (newLevel.add(nextPlace)) {
									return true;
								} else {
									continue whileLoop;
								}
							}
						} else {
							add = false;
						}
					}
					if (add) {
						nextPlace = addPlaceTarget(oNodes, iNodes, next);
						if (newLevel.add(nextPlace)) {
							return true;
						}
					}
				}
				
				return false;
			}

			public PartialPlaceEvaluation<HybridDirectedGraphNode> next() {
				return nextPlace;
			}
			
		};
		
		
		
		/*outerLoop: 
			for (HybridDirectedGraphNode target : inputNodes) {
				if (iNodes.contains(target)) {
					continue outerLoop;
				}
				boolean add = true;
				for (HybridDirectedGraphNode  source : oNodes) {

					HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
					if (this.allEdges.contains(e)) {
						if (iNodes.contains(source)) {
							addPlaceTarget(newLevel, oNodes, iNodes, target);
							continue outerLoop; 
						}
					} else {
						add = false;
					}
				}
				if (add) {
					addPlaceSource(newLevel, oNodes, iNodes, target);
				}
			}*/
		
		
		
		/*outerLoop:
			for (HybridDirectedGraphNode target : inputNodes) { // a
				//boolean direct = true;
				if (iNodes.contains(target)) {
					continue;
				}
				for (HybridDirectedGraphNode  source : oNodes) {
					HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
					if (! this.allEdges.contains(e)) {
						if (edgeContainedTarget(target, oNodes, iNodes, newLevel)) {
							//directCausalities = false;
						} else {
							continue outerLoop;
						}
					}
				}

				Set<HybridDirectedGraphNode> newPlaceInputNodes = new HashSet<>(iNodes);
				newPlaceInputNodes.add(target);
				PartialPlaceEvaluation<HybridDirectedGraphNode> currentPPe = new PartialPlaceEvaluation<HybridDirectedGraphNode>(oNodes, newPlaceInputNodes);
				//if (allPlaces.add(currentPPe)) {
					newLevel.add(currentPPe);
				//}
			}*/
	}
	
	/*private void addLevelInput2(LinkedHashSet<PartialPlaceEvaluation<N>> newLevel, Set<N> outputNodes, Set<N> inputNodes) {
		
			for (N outputNode : outputNodes) { // a
				for (HybridDirectedGraphEdge edge : this.allEdges) {
					if (edge.getSource().equals(outputNode) && ! inputNodes.contains(edge.getTarget())){ // if (!{c} contains c)
						Set<N> newPlaceInputNodes = new HashSet<>(inputNodes);
						newPlaceInputNodes.add((N) edge.getTarget());
						PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(outputNodes, newPlaceInputNodes);
						newLevel.add(currentPPe);
					}
					
					// x --> a
					if (edge.getTarget().equals(outputNode) && ! outputNodes.contains(edge.getSource()) ){ // if (!{a} contains x)
						Set<N> newPlaceOutputNodes = new HashSet<>(outputNodes);
						newPlaceOutputNodes.remove(outputNode);
						Set<N> newPlaceInputNodes = new HashSet<>(inputNodes);
						newPlaceInputNodes.remove(outputNode);
						newPlaceOutputNodes.add((N) edge.getSource());
						PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(newPlaceOutputNodes, inputNodes);
						newLevel.add(currentPPe);
					}
				}
			}
		
	}*/
	
	/*private void addLevelOutput2(LinkedHashSet<PartialPlaceEvaluation<N>> newLevel, Set<N> outputNodes, Set<N> inputNodes) {
			for (N inputNode : inputNodes) { // c
				//ArrayList<E> incomingEdges = (ArrayList<E>) inputNode.getGraph().getInEdges(inputNode); // {a-c, b-c}
				for (HybridDirectedGraphEdge edge : this.allEdges) { // b-c
					if (edge.getTarget().equals(inputNode) && ! outputNodes.contains(edge.getSource())){ // if(!{a} contains b)
						Set<N> newPlaceOutputNodes = new HashSet(outputNodes);
						newPlaceOutputNodes.add((N) edge.getSource()); // {a, b}
						PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(newPlaceOutputNodes, inputNodes); // p({a,b}, {c})
						newLevel.add(currentPPe);
					}
					
					// c -> x
					if (edge.getSource().equals(inputNode) && ! inputNodes.contains(edge.getTarget())){ // if(!{c} contains x)
						Set<N> newPlaceInputNodes = new HashSet(inputNodes);
						newPlaceInputNodes.remove(inputNode); // {c, x}
						Set<N> newPlaceOutputNodes = new HashSet(inputNodes);
						newPlaceOutputNodes.remove(inputNode);	
						newPlaceInputNodes.add((N) edge.getTarget()); // {c, x}
						PartialPlaceEvaluation<N> currentPPe = new PartialPlaceEvaluation<N>(outputNodes, newPlaceInputNodes); // p({a}, {c, x})
						newLevel.add(currentPPe);
					}
				}

			}
		
	}*/
	
	public Iterator<PartialPlaceEvaluation<HybridDirectedGraphNode>> getLevel(int level) {
		//Set<EdgeClique<N,E>> result = new HashSet<>();
		return addLevel(level);
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

	public LinkedHashSet<PartialPlaceEvaluation<HybridDirectedGraphNode>> getElement(int i) {
		return matrix.get(i);
	}

}

