package org.processmining.extendedhybridminer.models.hybridpetrinet;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.processmining.extendedhybridminer.algorithms.cg2hpn.Utils;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class PartialPlaceEvaluation<N extends AbstractDirectedGraphNode>  {
	private Set<HybridDirectedGraphEdge> edges;
	private Set<N> placeOutputNodes, placeInputNodes;
	private Set<N> loopNodes;
	private boolean conflicting;
	private boolean hasTransitiveCausalities;
	private Set<HybridDirectedGraphEdge> transitiveEdges;

	public PartialPlaceEvaluation(Set<N> placeOutputNodes, Set<N> placeInputNodes) {
		this.edges = new HashSet<HybridDirectedGraphEdge>();
		this.placeOutputNodes = placeOutputNodes; // *p
		this.placeInputNodes = placeInputNodes;// p*
		this.loopNodes = new HashSet<N>(placeOutputNodes);
		this.loopNodes.retainAll(placeInputNodes);
		this.conflicting = false;
		this.hasTransitiveCausalities = false;
		this.transitiveEdges = new LinkedHashSet<HybridDirectedGraphEdge>();
	}
	
	public PartialPlaceEvaluation(PartialPlaceEvaluation<N> p) {
		this.edges = p.getEdges();
		this.placeOutputNodes = p.placeOutputNodes;
		this.placeInputNodes = p.placeInputNodes;
		this.loopNodes = p.loopNodes;
		this.conflicting = p.conflicting;
		this.hasTransitiveCausalities = p.hasTransitiveCausalities;
		this.transitiveEdges = p.transitiveEdges;
	}
	
	
	
	/*public PartialPlaceEvaluation(Set<N> placeOutputNodes, Set<N> placeInputNodes, Set<HybridDirectedSureGraphEdge> edges) {
		this.placeOutputNodes = placeOutputNodes; // *p
		this.placeInputNodes = placeInputNodes; // p*
		this.edges = edges;
	}*/
	
	/*public void setEdges(Set<HybridDirectedSureGraphEdge> edges) {
		this.edges = edges;
	}*/
	
	/*public Set<HybridDirectedSureGraphEdge> getEdges() {
		return this.edges;
	}*/

	public int getMaxIO() {
		return Math.max(this.placeOutputNodes.size(), this.placeInputNodes.size());
	}


	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((placeInputNodes == null) ? 0 : placeInputNodes.hashCode());
		result = prime * result + ((placeOutputNodes == null) ? 0 : placeOutputNodes.hashCode());
		return result;
	}



	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartialPlaceEvaluation other = (PartialPlaceEvaluation) obj;
		if (placeInputNodes == null) {
			if (other.placeInputNodes != null)
				return false;
		} else if (!placeInputNodes.equals(other.placeInputNodes))
			return false;
		if (placeOutputNodes == null) {
			if (other.placeOutputNodes != null)
				return false;
		} else if (!placeOutputNodes.equals(other.placeOutputNodes))
			return false;
		return true;
	}

	
	/*
	 * Check if this is disjoint with places.
	 */
	public boolean isDisjoint(Collection<PlaceEvaluation<N>> places) {
		// for each outputPlace
		for (N n : this.placeOutputNodes) {
			for (PartialPlaceEvaluation<N> p : places) {
				if (p.getPlaceOutputNodes().contains(n))
					return false;
			}
		}

		// for each inputPlace
		for (N n : this.placeInputNodes) {
			for (PartialPlaceEvaluation<N> p : places) {
				if (p.getPlaceInputNodes().contains(n))
					return false;
			}
		}
		return true;
	}
	
	
    /*
	 * Given a set of (Partial)PlaceEvaluations, it checks if they are all pairwise disjoint.
	 */
	public static <N extends AbstractDirectedGraphNode> boolean areDisjoint(Collection<PlaceEvaluation<N>> places) {
		for (PartialPlaceEvaluation<N> ppei : places) {
			for (PartialPlaceEvaluation<N> ppej : places) {
				Set<N> ppeiOutPlaceClone = Utils.copySet(ppei.getPlaceOutputNodes());

				// l.retainAll(c) removes elements from a list l that are not contained in c.
				// it returns true if l changed as a result of the call
				if (ppeiOutPlaceClone.retainAll(ppej.getPlaceOutputNodes())) 
					return false;

				Set<N> ppeiInputPlaceClone = Utils.copySet(ppei.getPlaceInputNodes());

				if (ppeiInputPlaceClone.retainAll(ppej.getPlaceInputNodes()))
					return false;
			}
		}
		return true;
	}



	public Set<N> getPlaceOutputNodes() {
		return placeOutputNodes;
	}



	public Set<N> getPlaceInputNodes() {
		return placeInputNodes;
	}


	/*
	 * Given a place p, Checks if the inputTransitions *p are a subset (even not proper) of the outputTransitions p*.
	 */
	public boolean isSyphon() {
		if (this.placeInputNodes.containsAll(this.placeOutputNodes))
			return true;
		return false;
	}
	
	public String toString() {
		// TODO Auto-generated method stub
		return ("Place: "+ this.placeOutputNodes+" --> "+this.placeInputNodes);
	}

	/*public boolean containsSelfLoop(HybridDirectedGraphNode n) {
		return this.placeOutputNodes.contains(n) && this.placeInputNodes.contains(n);
	}*/

	public Set<N> getLoopNodes() {
		return this.loopNodes;
	}

	public void setConfliting(boolean c) {
		this.conflicting = c;
		
	}

	public boolean isConflicting() {
		return this.conflicting;
	}

	public void addEdge(HybridDirectedGraphEdge e) {
		this.edges.add(e);
	}

	public Set<HybridDirectedGraphEdge> getEdges() {
		return this.edges;
	}

	/*public int getNumberOfTransitiveCausalities() {
		// TODO Auto-generated method stub
		return this.transitiveCausalities;
	}*/
	
	public boolean hasTransitiveCausalities() {
		// TODO Auto-generated method stub
		return this.hasTransitiveCausalities;
	}

	public void addTransitiveCausality(HybridDirectedGraphEdge e) {
		this.transitiveEdges.add(e);
		this.hasTransitiveCausalities = true;
	}
	
	public Set<HybridDirectedGraphEdge> getTransitiveEdges() {
		return transitiveEdges;
	}

	/*public void increaseTransitiveCausalities() {
		this.transitiveCausalities++;
		this.hasTransitiveCausalities = true;
	}
	
	public void decreaseTransitiveCausalities() {
		this.transitiveCausalities--;
	}*/

}