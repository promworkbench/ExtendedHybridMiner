package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultConflictStrategyNonOpt<N extends AbstractDirectedGraphNode> extends DefaultConflictStrategy<N> {
	private Set<PlaceEvaluation<N>> places;
	

	public DefaultConflictStrategyNonOpt() {
		this.places = new HashSet<>();
	}
	
	
	
	public boolean checkConflict(PartialPlaceEvaluation<N> ppe) {
		if (this.places.size()==0)
			return false;
		
		ArrayList<PlaceEvaluation<N>> setArrayList = new ArrayList<>(places);
		
		/*
		 * Optimization: no need to check every combination, just those with K <= ppe.getMaxIO()
		 */
		System.out.print("getMaxIO: " + ppe.getMaxIO() + " ");
		for (int currentK=1; (currentK<=ppe.getMaxIO() && currentK<=places.size()); currentK++) {
			System.out.print("CurrentK: " + currentK + " ");
			Iterator<int[]> it = CombinatoricsUtils.combinationsIterator(places.size(), currentK);
			while (it.hasNext()) {
				int[] combinationIndexes = it.next();
				Set<PlaceEvaluation<N>> currentCombination = new HashSet<>();
				
				for (int i=0; i<combinationIndexes.length; i++)
					currentCombination.add(setArrayList.get(combinationIndexes[i]));
				
				if (PartialPlaceEvaluation.areDisjoint(currentCombination)) {
					if (conflicting(ppe, currentCombination))
						return true;
				}
			}	
		}
		return false;
	}

	public void addPlace(PlaceEvaluation<N> p) {
		this.places.add(p);
	}

	public Set<PlaceEvaluation<N>> getPlacesToBeAdded() {
		return this.places;
	}

}
