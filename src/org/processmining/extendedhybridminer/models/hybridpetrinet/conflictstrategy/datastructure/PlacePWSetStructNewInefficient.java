package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.DefaultConflictStrategy;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/*
 * This structure is TRIANGULAR SUPERIOR! When increasingLevel, this is taken into account!
 */
public class PlacePWSetStructNewInefficient<N extends AbstractDirectedGraphNode> {
	private Set<PlaceEvaluation<N>> currentSet;	
	private PlacePWSetStructNewInefficient<N> nested;
	private PlacePWSetStructNewInefficient<N> sibling;
	private Map<N,Vector<PlacePWSetStructNewInefficient<N>>> map;
	private CurrentDepth currentDepth;
	private boolean isRoot;


	// To be used for the "root" of the recursive struct.
	public PlacePWSetStructNewInefficient() {
		this.currentSet = null;
		this.currentDepth = new CurrentDepth();
		this.map=new HashMap<N,Vector<PlacePWSetStructNewInefficient<N>>>();
		this.isRoot = true;
	}


	// To be used elsewhere.
	public PlacePWSetStructNewInefficient(CurrentDepth currentDepth, Set<PlaceEvaluation<N>> currentSet) {
		this.currentDepth = currentDepth;
		this.currentSet = currentSet;
		this.map = new HashMap<>();
		this.isRoot = false;
	}


	/*
	 * Increase the depth of the structure by one. currentDepth=i means that the structure has at most subset of i elements.
	 * Hence, this method takes each singleton element and tries to add it to level=currentDepth to generate the next level.
	 * This method must be called on the "root" of the structure!
	 */
	private void increaseDepth() {
		System.out.println("START increseDepth");
		long startTime= System.currentTimeMillis();
		if (!this.isRoot)
			throw new RuntimeException("Something is wrong. This should happen only in the root!");

		if (this.currentSet==null)
			throw new RuntimeException("This should not happen. The check should already been performed on isConflicting()!");

		this.currentDepth.increaseDepth();

		Iterator<PlaceEvaluation<N>> it = this.singletonInOrder(new LinkedList<PlaceEvaluation<N>>()).iterator();

		// Always call the function on root, i.e., on this!
		while (it.hasNext())
			this.increaseDepthRec(it.next());
		long endTime = System.currentTimeMillis();
		System.out.println("END increseDepth. It took "+(endTime-startTime)+" milliseconds");
	}


	/*
	 * To return all singletons (in the reverse order), this method must be called on the root.
	 * It simply add the current singleton on top of stack and call recursively on sibling if not empty.
	 */
	private List<PlaceEvaluation<N>> singletonInOrder(List<PlaceEvaluation<N>> result) {
		// Add current element to the stack
		result.add(this.currentSet.iterator().next());

		// Recursive call
		if (this.sibling!=null)
			return this.sibling.singletonInOrder(result);
		else
			return result;
	}


	/*
	 * Tries to add ppe from root on, row by row, until it reaches row which at the first level has (is) ppe.
	 * This is to maintain the superior triangularity of the struct.
	 */
	private void increaseDepthRec(PlaceEvaluation<N> ppe) {
		// base case: if I reached the row with ppe as first element, i.e., with ppe at level one, then return (for maintaing the ordering of the structure)
		if (this.level()==1 && this.currentSet.contains(ppe))
			return;

		/*
		 * If ppe is disjoint with current set, I have to add (p U this.currentList) AT THE END of nested IF NESTED DOES NOT GOES BEYOND CURRENTDEPTH!
		 * Notice that I just increased the depth by one in increaseDepth()
		 * then the recursive call is ALSO on nested!
		 */
		if (this.level()<this.currentDepth.depth && ppe.isDisjoint(this.currentSet)) {

			Set<PlaceEvaluation<N>> set = new HashSet<>();
			set.addAll(currentSet);
			set.add(ppe);

			// nested==null
			if (this.nested==null) {
				this.nested = new PlacePWSetStructNewInefficient<N>(this.currentDepth, set);
				for (PlaceEvaluation<N> placeEvaluation : set) {
					for (N iNode : placeEvaluation.getPlaceInputNodes()) {
						Vector<PlacePWSetStructNewInefficient<N>> nodeV = map.get(iNode);
						if (nodeV==null)
							nodeV=new Vector<>();
						nodeV.add(this.nested);
						map.put(iNode, nodeV);
					}
				}

			}
			// nested!=null navigate at the end of nested and add the new set at the last position.
			else {
				PlacePWSetStructNewInefficient<N> lastSiblingOfNextLevel = this.nested.getLastSibling();
				lastSiblingOfNextLevel.sibling = new PlacePWSetStructNewInefficient<N>(currentDepth, set);
				for (PlaceEvaluation<N> placeEvaluation : set) {
					for (N iNode : placeEvaluation.getPlaceInputNodes()) {
						Vector<PlacePWSetStructNewInefficient<N>> nodeV = map.get(iNode);
						if (nodeV==null)
							nodeV=new Vector<>();
						nodeV.add(lastSiblingOfNextLevel.sibling);
						map.put(iNode, nodeV);
					}
				}

			}

			//2) Recursive call in-depth. Actually the nullity check is not necessary: I added at least one element above.
			//if (this.nested != null)
			this.nested.increaseDepthRec(ppe);
		}

		// In any case, recursive call on siblings.
		if (this.sibling != null)
			this.sibling.increaseDepthRec(ppe);		
	}



	/*
	 * In-depth check of conflicting p with the other places. In depth means recursively first checking nested and then
	 * siblings. For optimizations, let O, I be the output and input places of p. Then, there is no need of checking
	 * set of elements with size greater than max(|I|, |O|).
	 */
	public boolean isConflicting(PartialPlaceEvaluation<N> ppe) {
		
		System.out.println("---- STRUCT = "+this);
		System.out.println("---- DEPTH "+this.currentDepth.depth);
		System.out.println("---- SIBLING "+this.sibling);
		System.out.println("---- NESTED "+this.sibling);
		
		
		/*
		 * BASE CASE
		 */
		// 0) If the structure is newly created, return false;
		if (this.currentSet==null)
			return false;

		/*
		 * First of all, we have to check that this structure has the adequate level for checking the conflict of ppe.
		 * Namely if ppe.getMaxIO==5 and this.currentDepth.depth=3, then we have to increase the depth of the structure
		 * up to 5 otherwise the result is incorrect. 
		 */
		while (this.currentDepth.depth < ppe.getMaxIO()) {
			if (!this.isRoot)
				throw new RuntimeException("Something is wrong. This should happen only in the root!");
			this.increaseDepth();
			System.out.println("** ---- NEW STRUCT "+this);
			System.out.println("---- DEPTH "+this.currentDepth.depth);
			System.out.println("---- Sibling "+this.sibling);
			System.out.println("---- Nested "+this.nested);
		}

		System.out.println("---- Checking conflict for "+ppe+" and "+this.currentSet);

		//0b) if ppe.getMAXIO()==1 i.e., if the two (input and output) sets are composed of a single item and currentDepth==1, just return false
		if (ppe.getMaxIO()==1 && this.level()==1)
			return false;

		// 1) first check if current level is greater than max(|I|, |0|). If it is return, as both nested and siblings will not
		//	pass this check anyway.
		if (this.level() > ppe.getMaxIO())
			return false;
		
		// 2) otherwise, check if the current set P of place is conflicting with p. If it is, we are done.
		if (DefaultConflictStrategy.conflicting(ppe, this.currentSet))
			return true;

		for (N iNode : ppe.getPlaceInputNodes()) {
			if(map!=null){
				Vector<PlacePWSetStructNewInefficient<N>> test = map.get(iNode);
				System.out.println("I will check only "+test);

				if (test!=null){
					for (PlacePWSetStructNewInefficient<N> placePWSetStructNew : test) {
						placePWSetStructNew.isConflicting(ppe);
					}
				}
			}
		}
		
		/*
		 * INDUCTIVE CASE
		 */
		// we go in-depth, then first check nested and then siblings.
		boolean nestedResult = false;
		boolean siblingResult = false;
/*		if (this.nested!=null)
			nestedResult = this.nested.isConflicting(ppe);

		if (this.sibling!=null)
			siblingResult = this.sibling.isConflicting(ppe);*/
		
		//Chiara ... da sistemare
		if (this.sibling!=null)
			siblingResult = this.sibling.isConflicting(ppe);
		
		if (siblingResult)
			return siblingResult;
		else {
			if (this.nested!=null)
				nestedResult = this.nested.isConflicting(ppe);
		return nestedResult || siblingResult;
		}
	}



	/*
	 * Update the dataStructure by adding p. IT DOES NOT GOES BEYOND CURRENTDEPTH! It checks for independency!
	 */
	public void addPlace(PlaceEvaluation<N> p) {

		// 0) If the structure is newly created, just add p to currentSet
		if (this.currentSet==null) {
			this.currentSet = new HashSet<>();
			this.currentSet.add(p);
			for (N iNode : p.getPlaceInputNodes()) {
				Vector<PlacePWSetStructNewInefficient<N>> nodeV = map.get(iNode);
				if (nodeV==null)
					nodeV=new Vector<>();
				nodeV.add(new PlacePWSetStructNewInefficient<N>(currentDepth, this.currentSet));
				map.put(iNode, nodeV);
			}

			if (this.currentDepth.depth != 0)
				throw new RuntimeException("Something is wrong. If PlacePWStruct is newley created, currentDepth must be zero!");
			this.currentDepth.increaseDepth();
			return;
		}

		/*
		 * If p is disjoint with current set, I have to add (p U this.currentList) AT THE END of nested IF NESTED DOES NOT GOES BEYOND CURRENTDEPTH!
		 * then the recursive call is ALSO on nested!
		 */
		if (this.level()<this.currentDepth.depth && p.isDisjoint(this.currentSet)) {

			Set<PlaceEvaluation<N>> set = new HashSet<>();
			set.addAll(currentSet);
			set.add(p);

			// nested==null
			if (this.nested==null) {
				this.nested = new PlacePWSetStructNewInefficient<N>(this.currentDepth, set);
				for (PlaceEvaluation<N> placeEvaluation : set) {
					for (N iNode : placeEvaluation.getPlaceInputNodes()) {
						Vector<PlacePWSetStructNewInefficient<N>> nodeV = map.get(iNode);
						if (nodeV==null)
							nodeV=new Vector<>();
						nodeV.add(this.nested);
						map.put(iNode, nodeV);
					}
				}


			}
			// nested!=null navigate at the end of nested and add the new set at the last position.
			else {
				PlacePWSetStructNewInefficient<N> lastSiblingOfNextLevel = this.nested.getLastSibling();
				lastSiblingOfNextLevel.sibling = new PlacePWSetStructNewInefficient<N>(currentDepth, set);
				for (PlaceEvaluation<N> placeEvaluation : set) {
					for (N iNode : placeEvaluation.getPlaceInputNodes()) {
						Vector<PlacePWSetStructNewInefficient<N>> nodeV = map.get(iNode);
						if (nodeV==null)
							nodeV=new Vector<>();
						nodeV.add(this.sibling);
						map.put(iNode, nodeV);
					}
				}

			}

			//2) Recursive call in-depth. Actually the nullity check is not necessary: I added at least one element above.
			//if (this.nested != null)
			this.nested.addPlace(p);
		}

		// In any case, recursive call on siblings
		if (this.sibling != null)
			this.sibling.addPlace(p);
		// if no more sibling and I am at the first level, I need to add the singleton {p} to the siblings.
		else {
			if (this.level()==1) {
				Set<PlaceEvaluation<N>> singletonSet = new HashSet<>();
				singletonSet.add(p);
				this.sibling = new PlacePWSetStructNewInefficient<N>(currentDepth, singletonSet);
				for (PlaceEvaluation<N> placeEvaluation : singletonSet) {
					if(placeEvaluation!=null){
						for (N iNode : placeEvaluation.getPlaceInputNodes()) {
							Vector<PlacePWSetStructNewInefficient<N>> nodeV = map.get(iNode);
							if (nodeV==null)
								nodeV=new Vector<>();
							nodeV.add(this.sibling);
							map.put(iNode, nodeV);
						}
					}
				}

				
			}
		}
	}


	private PlacePWSetStructNewInefficient<N> getLastSibling() {
		if (this.sibling==null)
			return this;

		PlacePWSetStructNewInefficient<N> result = this.sibling;
		while (result.sibling != null)
			result = result.sibling;

		return result;
	}


	/*
	 * Return the nesting level of this.
	 * If the structure has been newly created, then return zero. Otherwise the first level is one.
	 */
	public int level() {
		if (this.currentSet==null)
			return 0;
		else
			return currentSet.size();
	}


	public Set<PlaceEvaluation<N>> getPlacesToBeAddedRec() {
		Set<PlaceEvaluation<N>> result = new HashSet<>();

		// Base case for the newly created structure;
		if (this.currentSet==null)
			return result;

		//Doublecheck on the size of currentSet, should be one
		if (this.currentSet.size() != 1) 
			throw new RuntimeException("Something wrong!");

		result.addAll(this.currentSet);

		// Recursive call on siblings only (if present!)
		if (this.sibling != null)
			result.addAll(this.sibling.getPlacesToBeAddedRec());

		return result;
	}


	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentDepth == null) ? 0 : currentDepth.hashCode());
		result = prime * result + ((currentSet == null) ? 0 : currentSet.hashCode());
		result = prime * result + ((nested == null) ? 0 : nested.hashCode());
		result = prime * result + ((sibling == null) ? 0 : sibling.hashCode());
		return result;
	}


	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlacePWSetStructNewInefficient other = (PlacePWSetStructNewInefficient) obj;
		if (currentDepth == null) {
			if (other.currentDepth != null)
				return false;
		} else if (!currentDepth.equals(other.currentDepth))
			return false;
		if (currentSet == null) {
			if (other.currentSet != null)
				return false;
		} else if (!currentSet.equals(other.currentSet))
			return false;
		if (nested == null) {
			if (other.nested != null)
				return false;
		} else if (!nested.equals(other.nested))
			return false;
		if (sibling == null) {
			if (other.sibling != null)
				return false;
		} else if (!sibling.equals(other.sibling))
			return false;
		return true;
	}


	// Prints the data structure in depth first.
	public String toString() {
		if (this.currentSet==null)
			return new String();

		StringBuffer result = new StringBuffer(this.currentSet.toString());
		if (this.nested != null) {
			result.append(this.nested.toString());
			result.append("\n");
		}

		if (this.sibling!=null)
			result.append(this.sibling.toString());

		return new String(result);
	}


	public Set<PlaceEvaluation<N>> getCurrentSet() {
		return currentSet;
	}


	public PlacePWSetStructNewInefficient<N> getNested() {
		return nested;
	}


	public PlacePWSetStructNewInefficient<N> getSibling() {
		return sibling;
	}



	/*
	 * Data structure for side effect on the depth of PlacePWSetStruct
	 */
	class CurrentDepth {
		private int depth;

		public CurrentDepth() {
			this.depth=0;
		}

		public CurrentDepth(int depth) {
			this.depth=depth;
		}

		public int increaseDepth() {
			this.depth++;
			return this.depth;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + depth;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CurrentDepth other = (CurrentDepth) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (depth != other.depth)
				return false;
			return true;
		}

		private PlacePWSetStructNewInefficient<N> getOuterType() {
			return PlacePWSetStructNewInefficient.this;
		}


	}





}
