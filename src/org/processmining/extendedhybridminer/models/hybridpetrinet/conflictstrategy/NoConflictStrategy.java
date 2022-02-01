package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import java.util.HashSet;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class NoConflictStrategy<N extends AbstractDirectedGraphNode> implements ConflictStrategy<N> {
    private Set<PlaceEvaluation<N>> places;
	

	public NoConflictStrategy() {
		this.places = new HashSet<>();
	}
	
	
	
	public boolean checkConflict(PartialPlaceEvaluation<N> ppe) {
		return false;
	}

	public void addPlace(PlaceEvaluation<N> p) {
		this.places.add(p);
	}

	public Set<PlaceEvaluation<N>> getPlacesToBeAdded() {
		return this.places;
	}

}
