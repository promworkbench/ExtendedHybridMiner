package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

public abstract class DefaultConflictStrategy<N extends AbstractDirectedGraphNode> implements ConflictStrategy<N> {
	
	/*
	 * Check if a ppe is conflicting with PartialPlaces in places. Notice that places are disjoint!
	 */
	public static <N extends AbstractDirectedGraphNode> boolean conflicting(PartialPlaceEvaluation<N> ppe, Collection<PlaceEvaluation<N>> places) {
		Set<N> unionOutputNodes = new HashSet<>();
		Set<N> unionInputNodes = new HashSet<>();

		for (PartialPlaceEvaluation<N> p : places) {
			unionOutputNodes.addAll(p.getPlaceOutputNodes());
			unionInputNodes.addAll(p.getPlaceInputNodes());
		}

		if (ppe.getPlaceOutputNodes().containsAll(unionOutputNodes) && unionInputNodes.containsAll(ppe.getPlaceInputNodes()))
			return true;

		if (unionOutputNodes.containsAll(ppe.getPlaceOutputNodes()) && ppe.getPlaceInputNodes().containsAll(unionInputNodes))
			return true;

		return false;
	}


}
