package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PartialPlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;

public class MinimalPlacesOLD<N> extends DefaultConflictStrategy<HybridDirectedGraphNode> {
	protected LinkedHashSet<HybridDirectedGraphEdge> edges;
	protected Map<Integer, HybridDirectedGraphEdge> edgesHashMap;
	protected LinkedHashSet<Integer> removedEdges;
	protected Map<Integer, HybridDirectedGraphEdge> transitiveEdgesHashMap;
	protected LinkedHashSet<PlaceEvaluation<HybridDirectedGraphNode>> places;
	protected Set<HybridDirectedGraphEdge> currentEdges;
	protected Set<HybridDirectedGraphNode> currentSources;
	protected Set<HybridDirectedGraphNode> currentTargets;
	protected Set<HybridDirectedGraphNode> sourcesWithRODSBelowThreshold;
	protected Set<HybridDirectedGraphNode> targetsWithRIDSBelowThreshold;
	protected Map<HybridDirectedGraphNode, Set<HybridDirectedGraphEdge>> mapSourceToEdges;
	protected Map<HybridDirectedGraphNode, Set<HybridDirectedGraphEdge>> mapTargetToEdges;
	protected Map<HybridDirectedGraphNode, Double> reversedOutputDirectSuccessionMetric; // reversed: we start by max value and decrease until reaching 0
	protected Map<HybridDirectedGraphNode, Double> reversedInputDirectSuccessionMetric;
	protected ExtendedCausalGraph cg;
	protected Map<HybridDirectedGraphNode, Integer> mapActivityToIndex;
	protected boolean longDep;
	double THRESHOLD_FOR_REMOVING_EDGES = 0.05;
	
	public MinimalPlacesOLD(LinkedHashSet<HybridDirectedGraphEdge> edges2,LinkedHashSet<HybridDirectedGraphEdge> removedEdges, LinkedHashSet<HybridDirectedGraphNode> outputNodes, LinkedHashSet<HybridDirectedGraphNode> inputNodes, ExtendedCausalGraph cg, boolean longDep) {
		
		this.edges = edges2;
		this.edgesHashMap = new HashMap<Integer, HybridDirectedGraphEdge>();
		this.removedEdges = new LinkedHashSet<Integer>();
		this.transitiveEdgesHashMap = new HashMap<Integer, HybridDirectedGraphEdge>();
		this.places = new LinkedHashSet<PlaceEvaluation<HybridDirectedGraphNode>>();
		this.cg = cg;
		this.mapActivityToIndex = cg.getNodesMapping();
		this.sourcesWithRODSBelowThreshold = new HashSet<HybridDirectedGraphNode>();
		this.targetsWithRIDSBelowThreshold = new HashSet<HybridDirectedGraphNode>();
		this.mapSourceToEdges = new HashMap<HybridDirectedGraphNode, Set<HybridDirectedGraphEdge>>();
		for (HybridDirectedGraphNode s: outputNodes) {
			this.mapSourceToEdges.put(s, new HashSet<HybridDirectedGraphEdge>());
		}
		this.mapTargetToEdges = new HashMap<HybridDirectedGraphNode, Set<HybridDirectedGraphEdge>>();
		for (HybridDirectedGraphNode t: inputNodes) {
			this.mapTargetToEdges.put(t, new HashSet<HybridDirectedGraphEdge>());
		}
		this.longDep = longDep;
		calculateInitialMetrics();	
	}
	
	protected void calculateInitialMetrics() {
		this.reversedInputDirectSuccessionMetric = new HashMap<HybridDirectedGraphNode, Double>();
		this.reversedOutputDirectSuccessionMetric = new HashMap<HybridDirectedGraphNode, Double>();
		for (HybridDirectedGraphEdge e: this.edges) {
			this.edgesHashMap.put(e.hashCode(), e);
			HybridDirectedGraphNode s = e.getSource();
			HybridDirectedGraphNode t = e.getTarget();
			int i = this.mapActivityToIndex.get(s);
			int j = this.mapActivityToIndex.get(t);
			
			double oldIDS;
			try {
				oldIDS = this.reversedInputDirectSuccessionMetric.get(t);
			} catch (Exception ex) {
				oldIDS = 0;
			}
			this.reversedInputDirectSuccessionMetric.put(t,  oldIDS +  this.getID(i, j) ); // IDS
			
			double oldODS;
			try {
				oldODS = this.reversedOutputDirectSuccessionMetric.get(s);
			} catch (Exception ex) {
				oldODS = 0;
			}
			this.reversedOutputDirectSuccessionMetric.put(s,  oldODS +  this.getOD(i, j) ); // IDS
			
		}
		
	}

	
	public LinkedHashSet<HybridDirectedGraphEdge> getEdges() {
		return this.edges;
	}

	
	public boolean checkConflict(PartialPlaceEvaluation<HybridDirectedGraphNode> ppe) {
		this.currentEdges = ppe.getEdges();
		this.currentSources = ppe.getPlaceOutputNodes();
		this.currentTargets = ppe.getPlaceInputNodes();
		return false;
	}
	

	public boolean checkConflictOLD(PartialPlaceEvaluation<HybridDirectedGraphNode> ppe) {
		
		////System.out.println("Check Conflict: " + ppe.toString());
		/*boolean c = (ppe.getPlaceInputNodes().iterator().next().getLabel() == "end");
		if (c) {
			//System.out.println("Check Conflict: " + ppe.toString());
		}*/
		this.currentEdges = new HashSet<>();
		this.currentSources = new HashSet<>();
		this.currentTargets = new HashSet<>();
		////System.out.println("Edges: " + this.edges.toString());
		////System.out.println("New evaluation: " + ppe.toString());
		
		for (HybridDirectedGraphNode  target : ppe.getPlaceInputNodes()) {
			for (HybridDirectedGraphNode source: ppe.getPlaceOutputNodes()) {
				
				//HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
				////System.out.println("Edge: " + e);
				
				/*if (this.removedEdges.contains(e)) {
					return true;
				} */
				
				//if (!edgeContained(source, target, e, ppe.getLoopNodes())) {
				// if (this.removedEdges.contains(e)) {

					////System.out.println("Conflict");
			   //     return true;
				//}
				/* else {
					currentEdges.add(e);
					currentSources.add(source.getLabel());
					currentTargets.add(target.getLabel());

				}*/
			}
		}
		////System.out.println("No conflict");
		return false;
	}


	private boolean edgeContained(HybridDirectedGraphNode source, HybridDirectedGraphNode target, HybridDirectedGraphEdge e, Set<HybridDirectedGraphNode> loopN) {
		//HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
		/*if (edges.contains(e)) {
			return true;
		} else {
			for (String label: this.optionalSelfLoops) {
				HybridDirectedGraphNode mid = this.cg.getNode(label);
				HybridDirectedGraphEdge e1 = new HybridDirectedGraphEdge(source, mid);
				HybridDirectedGraphEdge e2 = new HybridDirectedGraphEdge(mid, target);
				if (edges.contains(e1) && edges.contains(e2) && ppe.containsSelfLoop(mid)) {
					return true;
				}
			}
		}
		return false;*/
		/*if (this.removedEdges.contains(e)) {
			return false;
		} */
		
		//if (this.edges.contains(e)) {
		if (true) {
			////System.out.println("E: " + e.toString());
			currentEdges.add(e);
			currentSources.add(source);
			currentTargets.add(target);
			return true;
		}
		//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX");
		if (source.getLabel().equals(target.getLabel())) {
			return true;
		}/*else if (this.removedEdges.contains(e)) {
			return false;
		} else*/ {
			Set<HybridDirectedGraphNode> loopNodes = new HashSet<HybridDirectedGraphNode> (loopN);
			loopNodes.remove(source);
			loopNodes.remove(target);
			
			////System.out.println(source.toString() + " ---> " + target.toString());
			for (HybridDirectedGraphNode  mid : loopNodes) {
				////System.out.println("mid: " + mid.toString());
				
				//HybridDirectedGraphEdge e1 = new HybridDirectedGraphEdge(source, mid);
				//HybridDirectedGraphEdge e2 = new HybridDirectedGraphEdge(mid, target);
				Set<HybridDirectedGraphNode> loopNodesNew = new HashSet<HybridDirectedGraphNode>(loopNodes);
				loopNodesNew.remove(mid);
				/*if (edgeContained(source, mid, e1, loopNodesNew) && edgeContained(mid, target, e2, loopNodesNew)) {
					return true;
				}*/
			}

		}
		return false;
	}

	public void addPlace(PlaceEvaluation<HybridDirectedGraphNode> p) {
		this.places.add(p);
		
		if (p.hasTransitiveCausalities()) {
			//System.out.println("TTTTTTTTTTTTTTTTTTt");
			addPlaceTransitiveCausalities(p);
			return;
		}
		// Case 1: place covers a single edge
		// We remove the edge
		if (this.currentEdges.size() == 1) {
			//System.out.println("Case 1");
			HybridDirectedGraphEdge e = this.currentEdges.iterator().next();
			HybridDirectedGraphNode s = e.getSource();
			HybridDirectedGraphNode t = e.getTarget();
			this.removeEdge(e);
			
			//System.out.println("Case 1 Edge removed: " + e.toString());
			int i = this.mapActivityToIndex.get(s);
			int j = this.mapActivityToIndex.get(t);
			//System.out.println("Old RODS for s: " + this.reversedOutputDirectSuccessionMetric.get(s) );
			//System.out.println("Old RIDS for t: " + this.reversedInputDirectSuccessionMetric.get(t));
			//System.out.println("ODS: " +  this.cg.getOutputDirectSuccessionDependency(i, j) );
			//System.out.println("IDS: " + this.cg.getInputDirectSuccessionDependency(i, j));
			this.reversedInputDirectSuccessionMetric.put(t, this.reversedInputDirectSuccessionMetric.get(t) 
					- this.getID(i, j) ); // IDS
			this.reversedOutputDirectSuccessionMetric.put(s, this.reversedOutputDirectSuccessionMetric.get(s) 
					- this.getOD(i, j) ); // ODS
			//System.out.println("New RODS for s: " + this.reversedOutputDirectSuccessionMetric.get(s) );
			if (this.reversedOutputDirectSuccessionMetric.get(s)  <= this.THRESHOLD_FOR_REMOVING_EDGES) {
				this.sourcesWithRODSBelowThreshold.add(s);
			}

			//System.out.println("New RIDS for t: " + this.reversedInputDirectSuccessionMetric.get(t));
			if (this.reversedInputDirectSuccessionMetric.get(t)  <= this.THRESHOLD_FOR_REMOVING_EDGES) {
				this.targetsWithRIDSBelowThreshold.add(t);
			}
			return;
		}
		
		// Case 2: XOR-Split    ({a}  ->  {b, c})
		// ODS = #(i,j,L) / #(i,*,L) 
		// IDS = #(i,j,L) / #(*,j,L)
		if (this.currentSources.size() == 1) {
			//System.out.println("Case 2: " + this.currentEdges.toString());
			HybridDirectedGraphNode s = this.currentSources.iterator().next();
			//System.out.println("s: " + s );
			double sumODS = this.reversedOutputDirectSuccessionMetric.get(s); 
			//System.out.println("RODS for s: " + reversedOutputDirectSuccessionMetric.get(s));
			int i = this.mapActivityToIndex.get(s);
			HybridDirectedGraphEdge edgeToBeRemoved = null;
			double lowestIDS = 1.1;  // higher than any valid value in [0, 1]

			for (HybridDirectedGraphEdge e: this.currentEdges) {
				HybridDirectedGraphNode t = e.getTarget();
				//System.out.println("t: " + t);
				int j = this.mapActivityToIndex.get(t);
				double IDS = getID(i, j);
				//System.out.println("local IDS: " + getID(i, j) );
				

				if (IDS < lowestIDS) {
					lowestIDS = IDS;
					edgeToBeRemoved = e;
					//System.out.println("edgeToBeRemoved updated: " + e);
				}
				
				if (!this.mapSourceToEdges.get(s).contains(e)) {
					//System.out.println("edge NOT in mapSourceToEdges.get(s)");
					sumODS = sumODS - this.getOD(i, j);
					this.mapSourceToEdges.get(s).add(e);
					//System.out.println("RODS updated: " + sumODS);	
				}
				
				if (!this.mapTargetToEdges.get(t).contains(e)) {
					//System.out.println("edge NOT in mapTargetToEdges.get(t)");
					//System.out.println("Old RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));
					double sumIDS = this.reversedInputDirectSuccessionMetric.get(t)
							- this.getID(i, j);
					this.reversedInputDirectSuccessionMetric.put(t, sumIDS);
					this.mapTargetToEdges.get(t).add(e);
					//System.out.println("New RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));
					if (sumIDS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
						this.targetsWithRIDSBelowThreshold.add(t);
					}
				}
			}
			
			this.reversedOutputDirectSuccessionMetric.put(s, sumODS);
			//System.out.println("Final RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));		
	           
			if (sumODS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
				this.sourcesWithRODSBelowThreshold.add(s);
				//System.out.println("RODS < 0.05 ---> all edges connected to source removed: " + this.mapSourceToEdges.get(s).toString());	
				for (HybridDirectedGraphEdge eToRem: this.mapSourceToEdges.get(s)) {
					this.removeEdge(eToRem);
				}
				
				//this.edges.removeAll();
				//this.removedEdges.addAll(this.mapSourceToEdges.get(s));
			} else {
				//System.out.println("RODS still high ---> only worst edge removed: " + edgeToBeRemoved.toString());
				this.removeEdge(edgeToBeRemoved);

				//this.edges.remove(edgeToBeRemoved);
				//this.removedEdges.add(edgeToBeRemoved);
			}
			return;
		}
		
		
		// Case 3: XOR-Join   ({a,b}  ->  {c})
		// ODS: #(i,j,L) / #(i,*,L) 
		// IDS: #(i,j,L) / #(*,j,L)
		if (this.currentTargets.size() == 1) {
			//System.out.println("Case 3: " + this.currentEdges.toString());
			HybridDirectedGraphNode t = this.currentTargets.iterator().next();
			//System.out.println("t: " + t );
			double SumIDS = this.reversedInputDirectSuccessionMetric.get(t); 
			//System.out.println("RIDS for t: " + this.reversedInputDirectSuccessionMetric.get(t));
			int j = this.mapActivityToIndex.get(t);
			HybridDirectedGraphEdge edgeToBeRemoved = null;
			double lowestODS = 1.1;  // higher than any valid value in [0, 1]

			for (HybridDirectedGraphEdge e: this.currentEdges) {
				HybridDirectedGraphNode s = e.getSource();
				//System.out.println("s: " + s );
				int i = this.mapActivityToIndex.get(s);
				double ODS = getOD(i, j);
				//System.out.println("local ODS: " + cg.getOutputDirectSuccessionDependency(i, j) );
				if (ODS < lowestODS) {
					lowestODS = ODS;
					edgeToBeRemoved = e;
					//System.out.println("edgeToBeRemoved updated: " + e);
				}
				
				if (!this.mapTargetToEdges.get(t).contains(e)) {
					//System.out.println("edge NOT in mapTargetToEdges.get(t)");
					SumIDS = SumIDS - getID(i, j);
					this.mapTargetToEdges.get(t).add(e);
					//System.out.println("RIDS updated: " + SumIDS);					
				}
				
				if (!this.mapSourceToEdges.get(s).contains(e)) {
					//System.out.println("edge NOT in mapSourceToEdges.get(s)");
					//System.out.println("Old RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));
					double sumODS = this.reversedOutputDirectSuccessionMetric.get(s)
							- this.getOD(i, j);
					this.reversedOutputDirectSuccessionMetric.put(s, sumODS);
					this.mapSourceToEdges.get(s).add(e);
					//System.out.println("New RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));
					if (sumODS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
						this.sourcesWithRODSBelowThreshold.add(s);
					}
				}
			}
			
            this.reversedInputDirectSuccessionMetric.put(t, SumIDS);
            //System.out.println("Final RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));		
            
			if (SumIDS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
				this.targetsWithRIDSBelowThreshold.add(t);
				//System.out.println("RIDS < 0.05 ---> edges connected to target removed: " + this.mapTargetToEdges.get(t).toString());
				for (HybridDirectedGraphEdge eToRem: this.mapTargetToEdges.get(t)) {
					this.removeEdge(eToRem);
				}
				
				//this.edges.removeAll(this.mapTargetToEdges.get(t));
				//this.removedEdges.addAll(this.mapTargetToEdges.get(t));
			} else {
				//System.out.println("RIDS still high ---> only worst edge removed: " + edgeToBeRemoved.toString());
				this.removeEdge(edgeToBeRemoved);
				//this.edges.remove(edgeToBeRemoved);
				//this.removedEdges.add(edgeToBeRemoved);
			}
			return;
		}
		
		// Case 4: non-clear structure
		//System.out.println("Case 4: " + this.currentEdges.toString());
		HybridDirectedGraphEdge edgeToBeRemoved = null;
		double lowest_IDS_ODS = 2.1;  // higher than any valid value in [0, 1] + [0, 1]

		for (HybridDirectedGraphEdge e: this.currentEdges) {
			HybridDirectedGraphNode s = e.getSource();
			HybridDirectedGraphNode t = e.getTarget();
			////System.out.println(" ");
			//System.out.println("Edge: " + s + " -> " + t);
			int i = this.mapActivityToIndex.get(s);
			int j = this.mapActivityToIndex.get(t);
			double IDS_ODS = getID(i, j) + getOD(i, j);
			//System.out.println("ODS: " + cg.getOutputDirectSuccessionDependency(i, j));
			//System.out.println("IDS: " + cg.getInputDirectSuccessionDependency(i, j));
			//System.out.println("Sum: " + IDS_ODS);
			
			if (IDS_ODS  < lowest_IDS_ODS) {
				lowest_IDS_ODS  = IDS_ODS;
				edgeToBeRemoved = e;
				//System.out.println("edgeToBeRemoved updated: " + edgeToBeRemoved);
			}
			
			//System.out.println("Old RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));
			double sumODS = this.reversedOutputDirectSuccessionMetric.get(s)
					- getOD(i, j);
			if (!this.mapSourceToEdges.get(s).contains(e)) {
				//System.out.println("edge NOT in mapSourceToEdges.get(s)");
				this.reversedOutputDirectSuccessionMetric.put(s, sumODS);
				this.mapSourceToEdges.get(s).add(e);
				//System.out.println("New RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));
				if (sumODS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
					this.sourcesWithRODSBelowThreshold.add(s);
					//System.out.println("RODS below theshold");
					for (HybridDirectedGraphEdge edge: this.mapSourceToEdges.get(s)) {
						if (this.targetsWithRIDSBelowThreshold.contains(edge.getTarget())) {
							this.removeEdge(edge);
							//edges.remove(edge);
							//this.removedEdges.add(edge);
							//System.out.println("Edge removed: " + edge.toString());
						}
					}
				}
			}
			
			//System.out.println("Old RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));
			double sumIDS = this.reversedInputDirectSuccessionMetric.get(t)
					- getID(i, j);
			if (!this.mapTargetToEdges.get(t).contains(e)) {
				//System.out.println("edge NOT in mapTargetToEdges.get(t)");
				this.reversedInputDirectSuccessionMetric.put(t, sumIDS);
				this.mapTargetToEdges.get(t).add(e);
				//System.out.println("New RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));
				if (sumIDS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
					this.targetsWithRIDSBelowThreshold.add(t);
					//System.out.println("RIDS below theshold");
					for (HybridDirectedGraphEdge edge: this.mapTargetToEdges.get(t)) {
						if (this.sourcesWithRODSBelowThreshold.contains(edge.getSource())) {
							this.removeEdge(edge);
							//edges.remove(edge);
							//this.removedEdges.add(edge);
							////System.out.println("Edge removed: " + edge.toString());
						}
					}
				}
			}
		}
		this.removeEdge(edgeToBeRemoved);
		//this.edges.remove(edgeToBeRemoved);
	    //this.removedEdges.add(edgeToBeRemoved);
	    ////System.out.println("Worst edge removed: " + edgeToBeRemoved);
	}


	
	private void addPlaceTransitiveCausalities(PlaceEvaluation<HybridDirectedGraphNode> p) {
		
		HybridDirectedGraphEdge edgeToBeRemoved = null;
		double lowest_IDS_ODS = 2.1;  // higher than any valid value in [0, 1] + [0, 1]

		boolean removeTransitiveEdge = false;
		
		for (HybridDirectedGraphEdge e: p.getTransitiveEdges()) {
			HybridDirectedGraphNode s = e.getSource();
			HybridDirectedGraphNode t = e.getTarget();
			////System.out.println(" ");
			//System.out.println("Edge: " + s + " -> " + t);
			int i = this.mapActivityToIndex.get(s);
			int j = this.mapActivityToIndex.get(t);
			double IDS_ODS = getID(i, j, true) + getOD(i, j, true);
			//System.out.println("ODS: " + getOD(i, j, true));
			//System.out.println("IDS: " + getID(i, j, true));
			//System.out.println("Sum: " + IDS_ODS);
			
			if (IDS_ODS  < lowest_IDS_ODS) {
				lowest_IDS_ODS  = IDS_ODS;
				edgeToBeRemoved = e;
				removeTransitiveEdge = true;
				//System.out.println("edgeToBeRemoved updated: " + edgeToBeRemoved);
			}
		}
		
		for (HybridDirectedGraphEdge e: this.currentEdges) {
			HybridDirectedGraphNode s = e.getSource();
			HybridDirectedGraphNode t = e.getTarget();
			////System.out.println(" ");
			//System.out.println("Edge: " + s + " -> " + t);
			int i = this.mapActivityToIndex.get(s);
			int j = this.mapActivityToIndex.get(t);
			double IDS_ODS = getID(i, j, true) + getOD(i, j, true);
		    //System.out.println("ODS: " + getOD(i, j, true));
			//System.out.println("IDS: " + getID(i, j, true));
			//System.out.println("Sum: " + IDS_ODS);
			
			if (IDS_ODS  < lowest_IDS_ODS && !s.equals(t)) {
				lowest_IDS_ODS  = IDS_ODS;
				edgeToBeRemoved = e;
				//System.out.println("edgeToBeRemoved updated: " + edgeToBeRemoved);
			}
			
			//System.out.println("Old RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));
			
			if (!this.mapSourceToEdges.get(s).contains(e)) {
				double sumODS = this.reversedOutputDirectSuccessionMetric.get(s)
						- getOD(i, j);
				//System.out.println("edge NOT in mapSourceToEdges.get(s)");
				this.reversedOutputDirectSuccessionMetric.put(s, sumODS);
				this.mapSourceToEdges.get(s).add(e);
				//System.out.println("New RODS: " + this.reversedOutputDirectSuccessionMetric.get(s));
				if (sumODS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
					this.sourcesWithRODSBelowThreshold.add(s);
					//System.out.println("RODS below theshold");
				}
			}
			
			//System.out.println("Old RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));
			if (!this.mapTargetToEdges.get(t).contains(e)) {
				//System.out.println("edge NOT in mapTargetToEdges.get(t)");
				double sumIDS = this.reversedInputDirectSuccessionMetric.get(t) - getID(i, j);
				this.reversedInputDirectSuccessionMetric.put(t, sumIDS);
				this.mapTargetToEdges.get(t).add(e);
				//System.out.println("New RIDS: " + this.reversedInputDirectSuccessionMetric.get(t));
				if (sumIDS <= this.THRESHOLD_FOR_REMOVING_EDGES) {
					this.targetsWithRIDSBelowThreshold.add(t);
					//System.out.println("RIDS below theshold");
				}
			}
		}
		
		
		
		
		
		
		if (removeTransitiveEdge) {
			this.removeTransitiveEdge(edgeToBeRemoved);
		} else {
			this.removeEdge(edgeToBeRemoved);
		}
		
	}

	private void removeTransitiveEdge(HybridDirectedGraphEdge e) {
		int hashCode = e.hashCode();
		this.removedEdges.add(hashCode);
		this.transitiveEdgesHashMap.remove(hashCode);
		//System.out.println("TransitiveEdge removed: " + e.toString());
		e.setPlacesToConflicting();	
	}

	private void removeEdge(HybridDirectedGraphEdge e) {
		
		/*if (this.edges.remove(e)) {
			this.removedEdges.add(e);
		} else {
			this.removedEdges.add(e);
		}*/
		
		if (this.edges.remove(e)) {
			int hashCode = e.hashCode();
			this.removedEdges.add(hashCode);
			this.edgesHashMap.remove(hashCode);
			//System.out.println("Edge removed: " + e.toString());
			e.setPlacesToConflicting();
			
		}

	}

	/* 
	 * Return all the singleton in the first level of struct.
	 */
	public Set<PlaceEvaluation<HybridDirectedGraphNode>> getPlacesToBeAdded() {
		return this.places;
	}
	
	private double getID(int i, int j) {
		if (longDep) {
			return this.cg.getILD().get(i, j);
		} else {
			return this.cg.getInputDirectSuccessionDependency(i, j);
		}
	}
	
    private double getOD(int i, int j) {
    	if (longDep) {
			return this.cg.getOLD().get(i, j);
		} else {
			return this.cg.getOutputDirectSuccessionDependency(i, j);
		}
	}
    
    private double getID(int i, int j, boolean longDep) {
		return this.cg.getILD().get(i, j);
	}
	
    private double getOD(int i, int j, boolean longDep) {
    	return this.cg.getOLD().get(i, j);
	}

	public HybridDirectedGraphEdge getEdge(HybridDirectedGraphNode source, HybridDirectedGraphNode target) {
		int hashScore = source.hashCode() + 37 * target.hashCode();
		return this.edgesHashMap.getOrDefault(hashScore, null);
	}

	public HybridDirectedGraphEdge addTransitiveEdge(HybridDirectedGraphNode source, HybridDirectedGraphNode target) {
		int hashScore = source.hashCode() + 37 * target.hashCode();
		if (this.removedEdges.contains(hashScore)) {
			return null;
		} 
		HybridDirectedGraphEdge e = this.transitiveEdgesHashMap.getOrDefault(hashScore, null); 
		if (e == null) {
			e = new HybridDirectedGraphEdge(source, target);
			this.transitiveEdgesHashMap.put(hashScore, e);
		}
		return e;
	}


}

