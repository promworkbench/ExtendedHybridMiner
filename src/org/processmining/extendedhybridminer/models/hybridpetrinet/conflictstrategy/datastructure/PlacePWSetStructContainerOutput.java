package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;


public class PlacePWSetStructContainerOutput<N extends AbstractDirectedGraphNode> {
	private Map<N, PlacePWSetStructNew<N>> outputMap;
	
	public PlacePWSetStructContainerOutput() {
		this.outputMap = new HashMap<N, PlacePWSetStructNew<N>>();
	}
	
	public boolean isConflicting(PartialPlaceEvaluation<N> ppe){
		boolean conflicting = false;
		
		for (N ppeOutputNode : ppe.getPlaceOutputNodes()) {
			if(this.outputMap.containsKey(ppeOutputNode)){
				PlacePWSetStructNew<N> structure = this.outputMap.get(ppeOutputNode);
				//maybe here we can add a depth and we check only for limited checks
				if (structure.isConflicting(ppe))
					return true;
			}
				
		}
		
		return conflicting;
		
	}
	
	public void addPlace(PlaceEvaluation<N> p){
		PlacePWSetStructNew<N> structure;
		for (N pOutputNode : p.getPlaceOutputNodes()) {
			if(this.outputMap.containsKey(pOutputNode)){
				structure = this.outputMap.get(pOutputNode);
			}
			else {
				structure = new PlacePWSetStructNew<>();
			}
			//maybe here we can add a depth and we check only for limited checks
			structure.addPlace(p);
			outputMap.put(pOutputNode, structure);
			//System.out.println("---- INPUT MAP "+inputMap);
		}
	}
	
	
	/*
	 * Return all the singleton in the first level of struct.
	 */
	public Set<PlaceEvaluation<N>> getPlacesToBeAddedRec() {
		Set<PlaceEvaluation<N>> firstLevelPlaces = new HashSet<PlaceEvaluation<N>>();
		for (N outputNode : outputMap.keySet()) {
			firstLevelPlaces.addAll(outputMap.get(outputNode).getPlacesToBeAddedRec());
		}
		return firstLevelPlaces;
	}
	
	
	
	
	
	
}