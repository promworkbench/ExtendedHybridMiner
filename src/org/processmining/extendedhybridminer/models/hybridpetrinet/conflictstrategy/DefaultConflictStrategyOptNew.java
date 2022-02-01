package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.datastructure.PlacePWSetStructContainer;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public class DefaultConflictStrategyOptNew<N extends AbstractDirectedGraphNode> extends DefaultConflictStrategy<N> {
	private PlacePWSetStructContainer<N> struct;
	

	public DefaultConflictStrategyOptNew() {
		this.struct = new PlacePWSetStructContainer<N>();
	}


	public boolean checkConflict(PartialPlaceEvaluation<N> ppe) {
		return this.struct.isConflicting(ppe);
	}


	public void addPlace(PlaceEvaluation<N> p) {
		this.struct.addPlace(p);
	}


	/*
	 * Return all the singleton in the first level of struct.
	 */
	public Set<PlaceEvaluation<N>> getPlacesToBeAdded() {
		return this.struct.getPlacesToBeAddedRec();
	}

}
