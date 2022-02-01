package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;


public class PlacePWSetStructContainer<N extends AbstractDirectedGraphNode> {
	private Map<N, PlacePWSetStructNew<N>> inputMap;
	
	public PlacePWSetStructContainer() {
		this.inputMap = new HashMap<N, PlacePWSetStructNew<N>>();
	}
	
	public boolean isConflicting(PartialPlaceEvaluation<N> ppe){
		boolean conflicting = false;
		
		for (N ppeInputNode : ppe.getPlaceInputNodes()) {
			if(this.inputMap.containsKey(ppeInputNode)){
				PlacePWSetStructNew<N> structure = this.inputMap.get(ppeInputNode);
				//maybe here we can add a depth and we check only for limited checks
				if (structure.isConflicting(ppe))
					return true;
			}
				
		}
		
		return conflicting;
		
	}
	
	public void addPlace(PlaceEvaluation<N> p){
		PlacePWSetStructNew<N> structure;
		for (N pInputNode : p.getPlaceInputNodes()) {
			if(this.inputMap.containsKey(pInputNode)){
				structure = this.inputMap.get(pInputNode);
			}
			else {
				structure = new PlacePWSetStructNew<>();
			}
			//maybe here we can add a depth and we check only for limited checks
			/*System.out.print("\n");
			System.out.print("structure of inputNode :" + pInputNode.toString());
			System.out.print("\n");
			structure.print();
			System.out.print("\n");
			*/
			inputMap.put(pInputNode, structure);
			structure.addPlace(p);
			/*System.out.print("\n");
			System.out.print("structure of inputNode :" + pInputNode.toString());
			System.out.print("\n");
			structure.print();
			System.out.print("\n");
			*/
			inputMap.put(pInputNode, structure);
			//System.out.println("---- INPUT MAP "+inputMap);
		}
	}
	
	
	/*
	 * Return all the singleton in the first level of struct.
	 */
	public Set<PlaceEvaluation<N>> getPlacesToBeAddedRec() {
		Set<PlaceEvaluation<N>> firstLevelPlaces = new HashSet<PlaceEvaluation<N>>();
		for (N inputNode : inputMap.keySet()) {
			firstLevelPlaces.addAll(inputMap.get(inputNode).getPlacesToBeAddedRec());
		}
		return firstLevelPlaces;
	}
	
	
	
	
	
	
}