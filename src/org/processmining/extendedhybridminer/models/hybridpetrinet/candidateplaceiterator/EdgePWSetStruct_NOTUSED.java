package org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;



public class EdgePWSetStruct_NOTUSED<E extends AbstractDirectedGraphEdge> {
//	Set<E> currentSet;
//	private EdgePWSetStruct<E> nested;
//	private EdgePWSetStruct<E> sibling;
//
//	public EdgePWSetStruct() {}
//
//	public EdgePWSetStruct(Set<E> currentSet) {
//		this.currentSet = currentSet;
//	}
//
//	/*
//	 * Generate the first level of the structure, i.e., all singleton siblings
//	 */
//	public void initialize(Set<E> allEdges) {
//		if (this.currentSet != null)
//			throw new RuntimeException("This method must be called right after the constructor!");
//
//		Iterator<E> it = allEdges.iterator();
//		Set<E> curr;
//		
//		//First element
//		if (it.hasNext()) {
//			E e = (E) it.next();
//			curr = new HashSet<>();
//			curr.add(e);
//			this.currentSet = curr;
//		}
//			
//		EdgePWSetStruct<E> currentSibling;
//		
//		while (it.hasNext()) {
//			currentSibling = this.sibling;
//			E e = (E) it.next();
//			curr = new HashSet<>();
//			curr.add(e);
//			this.currentSet = curr;
//		}
//	}
//
//
//
//	/*
//	 * The building of the structure is different from that of PlacePWSetStruct.
//	 * Here, all singleton edges must be added. Given the EdgesOrderingStrategy,
//	 * maybe the best choice is to generate it level by level.
//	 * Given that the structure is ordered, best way to add a level is to start from the top singleton and try to
//	 * add the last element to the elements below in depth. This gives a reverse ordering
//	 * compared to that of PlacePWSetStruct.
//	 * 
//	 * How to do that? Suppose want to generate level n.
//	 * 1) Select a singleton element e at level 0;
//	 * 2) move to sibling. Check consistency between them if so, move to second level and so on
//	 * until we reach level n-1. If we do, try to each subset of level n-1 to add e if they are consistent.
//	 * 3) go to next sibling.
//	 */
//	
//	
//	public void generateLevel(int level) {
//		
//		
	//}
}
