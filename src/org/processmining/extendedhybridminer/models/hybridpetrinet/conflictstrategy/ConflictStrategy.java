package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;


/*
 * In class Cluster, all addition of places should be made from the ConflictStrategy
 */
public interface ConflictStrategy<N extends AbstractDirectedGraphNode> {
	
	public boolean checkConflict(PartialPlaceEvaluation<N> ppe);
	
	public void addPlace(PlaceEvaluation<N> p);
	
	public Set<PlaceEvaluation<N>> getPlacesToBeAdded();
	
	
}
