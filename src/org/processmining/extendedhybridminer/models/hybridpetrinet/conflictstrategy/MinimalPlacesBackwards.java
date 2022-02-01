package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class MinimalPlacesBackwards<N extends AbstractDirectedGraphNode> extends DefaultConflictStrategy<N> {
	protected LinkedHashSet<HybridDirectedSureGraphEdge> edges;
	protected LinkedHashSet<PlaceEvaluation<N>> places;
	protected Set<HybridDirectedSureGraphEdge> toBeRemovedEdges;
	
	public MinimalPlacesBackwards(Set<HybridDirectedSureGraphEdge> edges) {
		this.edges = new LinkedHashSet<>(edges);
		this.places = new LinkedHashSet<PlaceEvaluation<N>>();
	}
	
	public LinkedHashSet<HybridDirectedSureGraphEdge> getEdges() {
		return edges;
	}

	public boolean checkConflict(PartialPlaceEvaluation<N> ppe) {
		boolean res = false;
		this.toBeRemovedEdges = new HashSet<>();
		for (N  input : ppe.getPlaceInputNodes()) {
			HybridDirectedGraphNode target = (HybridDirectedGraphNode) input;
			for (N output: ppe.getPlaceOutputNodes()) {
				
				// avoid self-loops (self-loop places seem to reduce fitness!)
				//if (input.equals(output)) {
				//	return true;
				//}
				
				HybridDirectedGraphNode source = (HybridDirectedGraphNode) output;
				HybridDirectedSureGraphEdge e = new HybridDirectedSureGraphEdge(source, target);
				if (!edges.contains(e)) {
			        return true;
				} else {
					toBeRemovedEdges.add(e);
				}
			}
		}
		
		return res;
	}


	public void addPlace(PlaceEvaluation<N> p) {
		this.edges.removeAll(this.toBeRemovedEdges);
		this.places.add(p);
	}


	/*
	 * Return all the singleton in the first level of struct.
	 */
	public Set<PlaceEvaluation<N>> getPlacesToBeAdded() {
		return this.places;
	}

}
