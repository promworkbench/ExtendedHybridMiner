package org.processmining.extendedhybridminer.models.hybridpetrinet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.CandidatePlaceIterator;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.CandidatePlaceIteratorEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.EdgesFirst;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.EdgesOrderingIterator;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.EdgesOrderingIteratorOpt;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.EdgesOrderingIteratorOptNew_FV;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.MinimalEdgeOrdering;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.NodesOrderingIteratorOptNew;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.OutputThenInputOrderingIterator;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.EdgesOrderingCompatibleSelStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.ConflictStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.ConflictStrategyEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.DefaultConflictStrategyNonOpt;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.DefaultConflictStrategyOpt;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.DefaultConflictStrategyOptNew;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.DefaultConflictStrategyOptNewOutput;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.MinimalPlaces;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.NoConflictStrategy;
import org.processmining.models.graphbased.NodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;



/**
 * Created by demas on 19/08/16.
 */

public class Cluster<E extends HybridDirectedGraphEdge, N extends AbstractDirectedGraphNode> implements Runnable {
//public class Cluster<E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> {
	private LinkedHashSet<HybridDirectedGraphEdge> edges;
	LinkedHashSet<HybridDirectedGraphEdge> removedEdges;
	private LinkedHashSet<N> inputNodes, outputNodes;
	private static boolean DEBUG_MODE = false;
	//private double precision;

	//private Set<PlaceEvaluation<N>> places;

	// new variables for the thread implementation, to be initialized later.
	//private XLog log;
	private ExtendedCausalGraph hCG;
	private Map<String, Integer> activityFrequencyMap;
	private double placeEvaluationThreshold;
	private double prePlaceEvaluationThreshold;

	// new parameters for performance optimization
	//private Set<PlaceEvaluation<N>> placesToBeAdded;
	private CandidatePlaceIterator<N> placeIterator;
	private ConflictStrategy<N> conflictStrategy;
	private volatile boolean stopped;
	private AtomicInteger maxPlaceNumber;
	//private boolean checkPresicion;
	private FitnessType fitnessType;
	private TraceVariantsLog variants;
	private double logSize;
	//private Set<String> selfLoops;
	//private Set<String> optionalSelfLoops;
	private boolean longDep;
	/*private static HybridPetrinet petriNet;
	private static Marking iMarking;
	private static Marking[] fMarkings;*/
	private int thresholdStopIt;


	public Cluster(ExtendedCausalGraph hCG, LinkedHashSet<HybridDirectedGraphEdge> edges, CandidatePlaceSelectionStrategy<N> selectionStrategy, CandidatePlaceIteratorEnum placeIteratorType, ConflictStrategyEnum conflictStrategyType, AtomicInteger maxPlaceNumber, FitnessType fType, TraceVariantsLog variants2, boolean longDep, int thresholdStopIt) {
		this.hCG = hCG;
		this.edges = edges;
		removedEdges = new LinkedHashSet<HybridDirectedGraphEdge>();
		this.inputNodes = new LinkedHashSet<>();
		this.outputNodes = new LinkedHashSet<>();
		this.longDep = longDep;
		this.thresholdStopIt = thresholdStopIt;
		computeInputAndOutputNodes();
		this.conflictStrategy = selectConflictStrategy(conflictStrategyType);
		this.stopped = false;
		this.maxPlaceNumber = maxPlaceNumber;
		this.placeIterator = selectPlaceIterator(selectionStrategy, placeIteratorType, this.conflictStrategy);
	    //this.checkPresicion = false;
	    this.fitnessType = fType;
	    this.variants = variants2;
	    this.logSize = hCG.getLog().size();
	    
	}

	private ConflictStrategy<N> selectConflictStrategy(ConflictStrategyEnum conflictStrategyType) {
		switch(conflictStrategyType) {
			case DEFAULT_CONFLICT_STRATEGY:
				return new DefaultConflictStrategyNonOpt<N>();

			case DEFAULT_CONFLICT_STRATEGY_OPT:
				return new DefaultConflictStrategyOpt<N>();
				
			case DEFAULT_CONFLICT_STRATEGY_OPT_NEW:
				if (outputNodes.size()>inputNodes.size())
					return new DefaultConflictStrategyOptNewOutput<N>();	
				else 
					return new DefaultConflictStrategyOptNew<N>();

			case NO_CONFLICT_STRATEGY:
				return new NoConflictStrategy<N>();
			/*case CONFLICT_STRATEGY_LONG_DEP:
				return new ConflictStrategyLongDep<N>((LinkedHashSet<HybridDirectedSureGraphEdge>) this.edges, (Set<HybridDirectedGraphNode>) this.outputNodes, (Set<HybridDirectedGraphNode>) this.inputNodes, this.hCG);
			*/case MINIMAL_PLACES:
				return new MinimalPlaces(this.edges, this.removedEdges, this.outputNodes, this.inputNodes, this.hCG, this.longDep);
			/*case MINIMAL_PLACES_BACKWARDS:
				return new MinimalPlacesBackwards<N>((LinkedHashSet<HybridDirectedSureGraphEdge>) this.edges);*/
			
				
			default:
				throw new RuntimeException("Please select a valid conflict strategy!");
		}
	}


	public void printDotFile() {
		/*
        Printing to .gv (graphviz) file
		 */
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(new Integer(System.identityHashCode(this)).toString() + ".gv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintStream ps = new PrintStream(fos);
		ps.println(this.toDot());
		ps.flush();
		ps.close();
	}


	public String toDot() {
		Map<NodeID, Integer> nodeNameMap = new HashMap<>();

		StringBuilder b = new StringBuilder("digraph Cluster {\n");
		b.append("  rankdir = LR;\n");
		Set<N> IONodes = new HashSet<>();
		IONodes.addAll(this.outputNodes);
		IONodes.addAll(this.inputNodes);

		Set<N> intersectionIONodes = new HashSet<>();
		intersectionIONodes.addAll(this.outputNodes);
		intersectionIONodes.retainAll(this.inputNodes);
		System.out.println("Cluster: " + System.identityHashCode(this) + " has only " + (IONodes.size() - intersectionIONodes.size()) + " which are both input and output." );


		// print states;
		int counter = 0;
		for (N n : IONodes) {
			Integer newNodeName = nodeNameMap.get(n.getId());
			if (newNodeName==null) {
				newNodeName = new Integer(counter);
				nodeNameMap.put(n.getId(), newNodeName);
				counter++;
			}
			b.append("  ").append(newNodeName.toString());
			b.append(" [shape=circle,label=\"" + newNodeName.toString() + "\"];\n");
		}

		for (HybridDirectedGraphEdge e : this.edges) {
			NodeID idSource = ((N) e.getSource()).getId();
			NodeID idTarget = ((N) e.getTarget()).getId();
			b.append("  ").append(nodeNameMap.get(idSource).toString());
			b.append(" -> ").append(nodeNameMap.get(idTarget).toString()).append(" [label=\"");
			b.append("\"]\n");
		}
		return b.append("}\n").toString();
	}


	private CandidatePlaceIterator<N> selectPlaceIterator(CandidatePlaceSelectionStrategy<N> selectionStrategy, CandidatePlaceIteratorEnum placeIteratorType, ConflictStrategy<N> conflict) {
		switch(placeIteratorType) {
			case OUTPUT_THEN_INPUT_NUMBER :
				return new OutputThenInputOrderingIterator<N>(selectionStrategy, this.outputNodes, this.inputNodes);
				
			case OUTPUT_THEN_INPUT_NUMBER_OPT:
				return new NodesOrderingIteratorOptNew<N, HybridDirectedGraphEdge>((EdgesOrderingCompatibleSelStrategy<N>)selectionStrategy, this.edges);

			case EDGE_NUMBER :
				return new EdgesOrderingIterator<N, HybridDirectedGraphEdge>(selectionStrategy, this.edges);

			case EDGE_NUMBER_OPT :
				if (! (selectionStrategy instanceof EdgesOrderingCompatibleSelStrategy))
					throw new RuntimeException("In order to use the EDGE_NUMBER_OPT ordering strategy, the set of candidate place must be compatible, i.e., either BOUNDED_INPUT_PLUS_OUTPUT or SPLIT_JOIN_ONLY_BOUND!");
				return new EdgesOrderingIteratorOpt<N, HybridDirectedGraphEdge>((EdgesOrderingCompatibleSelStrategy<N>)selectionStrategy, this.edges);
				
			case EDGE_NUMBER_OPT_NEW :
				if (! (selectionStrategy instanceof EdgesOrderingCompatibleSelStrategy))
					throw new RuntimeException("In order to use the EDGE_NUMBER_OPT ordering strategy, the set of candidate place must be compatible, i.e., either BOUNDED_INPUT_PLUS_OUTPUT or SPLIT_JOIN_ONLY_BOUND!");
				return new EdgesOrderingIteratorOptNew_FV<N, HybridDirectedGraphEdge>((EdgesOrderingCompatibleSelStrategy<N>)selectionStrategy, this.edges);

			case EDGES_FIRST:
				return new EdgesFirst<N, HybridDirectedGraphEdge>((EdgesOrderingCompatibleSelStrategy<N>)selectionStrategy, (MinimalPlaces) this.conflictStrategy, this.edges, this.outputNodes, this.inputNodes);
			
			case MINIMAL:
				return new MinimalEdgeOrdering((EdgesOrderingCompatibleSelStrategy<N>)selectionStrategy, (MinimalPlaces) this.conflictStrategy, this.edges, removedEdges,this.outputNodes, this.inputNodes, (MinimalPlaces) conflict, this.thresholdStopIt);
			
				
			default:
				throw new RuntimeException("Please select or implement the right CandidatePlaceIterator class!");
		}
	}


	private void computeInputAndOutputNodes() {
		for(HybridDirectedGraphEdge edge : this.edges) {
			this.inputNodes.add((N) edge.getTarget());
			this.outputNodes.add((N) edge.getSource());
		}
	}

	
	public void setPrePlaceEvaluationThreshold(double prePlaceEvaluationThreshold) {
		this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
	}

	public void setPlaceEvaluationThreshold(double placeEvaluationThreshold) {
		this.placeEvaluationThreshold = placeEvaluationThreshold;
	}

	public Set<HybridDirectedGraphEdge> getEdges() {
		return new HashSet<>(edges);
	}


	public Set<N> getInputNodes() {
		return new HashSet<>(inputNodes);
	}


	public Set<N> getOutputNodes() {
		return new HashSet<>(outputNodes);
	}


	/*public void setLog(XLog log) {
		this.log = log;
	}*/

	public void setActivityFrequencyMap(Map<String, Integer> activityFrequencyMap) {
		this.activityFrequencyMap = activityFrequencyMap;
	}


	public Set<PlaceEvaluation<N>> getPlacesToBeAdded() {
		return this.conflictStrategy.getPlacesToBeAdded();
	}


	public void run() {
		//int originalNumPlacesBound = this.maxPlaceNumber.get();
		long startTime, endTime, elapsedTimeSec;
		System.out.println("---- CLUSTER "+this.edges);
		System.out.println("---- EdgeNumber "+this.edges.size());
		startTime = System.currentTimeMillis();
		
		FileWriter fW =null;
		if (DEBUG_MODE){
			try {
				fW = new FileWriter(new File("./logFiles/"+System.identityHashCode(this)+".txt"));
				fW.write(this.edges.toString());
				fW.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int conflictChecks = 0;
		int conflictFound = 0;

		//n.compareAndSet(0, 0) returns true if n==0 and sets n to 0 (only if it's already 0).

		whileLoop:
		while(!this.stopped && !maxPlaceNumber.compareAndSet(0, 0) && this.placeIterator.hasNext()) {
			//endTime = System.currentTimeMillis();
			//elapsedTimeSec = (endTime - startTime)/1000;
			//System.out.println(" END iterator.hasNext() in " + elapsedTimeSec + "sec.");


			//System.out.print("START iterator.next()... ");
			//startTime = System.currentTimeMillis();
			PartialPlaceEvaluation<N> candidatePPE = this.placeIterator.next();
			
			//endTime = System.currentTimeMillis();
			//elapsedTimeSec = (endTime - startTime)/1000;
			//System.out.println(" END iterator.next() in " + elapsedTimeSec + "sec.");
			
			//System.out.println("---- CANDIDATE PLACE "+candidatePPE);

			/* 
			 * isSyphon() Checks whether the inputTransitions *p (placeOutputNodes) are a subset
			 * (even not proper) of the outputTransitions p* (placeInputNodes).
			 * We do not want to add such places.
			 */
			if (!candidatePPE.isSyphon()){// && checkPlaceConsistency(candidatePPE, this.edges)) {
				boolean conflicting = checkConflict(candidatePPE);			
				conflictChecks++;
				if (!conflicting) {
					PlaceEvaluation<N> candidatePlaceEval;
					switch (fitnessType) {
						case GLOBAL:
							candidatePlaceEval = new PlaceEvaluation<N>(candidatePPE, variants, logSize, activityFrequencyMap, prePlaceEvaluationThreshold, placeEvaluationThreshold);
							
							if (candidatePlaceEval.replayPlaceGlobally()) {
								addPlace(candidatePlaceEval);
								maxPlaceNumber.getAndDecrement();
								System.out.println("Place added: " + candidatePlaceEval);
								if(this.placeIterator.getCurrentLevel() == 0) {
									break whileLoop;
								}
							}
							break;
						case LOCAL:
							candidatePlaceEval = new PlaceEvaluation<N>(candidatePPE, variants, logSize, activityFrequencyMap, prePlaceEvaluationThreshold, placeEvaluationThreshold);
							boolean preevaluation = candidatePlaceEval.preEvaluate();
							if (!preevaluation) {
								candidatePlaceEval.replayPlace();
								if (candidatePlaceEval.evaluateReplayScore() >= placeEvaluationThreshold) {
									/*if (this.checkPresicion) {
										if(!this.placeImprovesPrecision(candidatePlaceEval)) {
											this.petriNet.removePlace(this.petriNet.getPlace(candidatePlaceEval.toString()));
											continue;	
										}
									}*/
									
									if(this.placeIterator.getCurrentLevel() == 0) {
										if (edges.containsAll(getEdges(candidatePlaceEval))) {
											addPlace(candidatePlaceEval);
											maxPlaceNumber.getAndDecrement();
											System.out.println("Place added: " + candidatePlaceEval);
											break whileLoop;
										}
									} else {
										addPlace(candidatePlaceEval);
										maxPlaceNumber.getAndDecrement();
										System.out.println("Place added: " + candidatePlaceEval);
									}
								}
							}
							break;
					}				
				}
				else {
					conflictFound++;
				}
			}
		}
		
		if (DEBUG_MODE){
			try {
				fW.write(this.printStatistics());
				fW.write("Number of conflicts checked "+conflictChecks+" \n");
				fW.write("Number of conflicts found "+conflictFound+" \n");
				fW.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*private boolean placeImprovesPrecision(PlaceEvaluation<N> pe) {
		
	    //HybridPetrinet pn = new HybridPetrinet(pe.toString());
		
		//Set<N> nodes = new HashSet<N>();
		//nodes.addAll(this.inputNodes);
	    //nodes.addAll(this.outputNodes);
		
		//for (N node: nodes) {
		//	pn.addTransition(node.getLabel());
		//}
		//for (PlaceEvaluation pe2: this.conflictStrategy.getPlacesToBeAdded()) {
			//pn.addPlaceFromPlaceEvaluation(pe2);
		//}
		petriNet.addPlaceFromPartialPlaceEvaluation(pe);
		
		MetricsComputator computator;
		computator = new MetricsComputator(this.petriNet, this.hCG.getLog());
		//System.out.println("PE: " + pe.toString());
		double new_precision = computator.getPrecision();
		System.out.println("New Precision = " + new_precision);
		if (new_precision >= this.precision*1.05) {
			this.precision = new_precision;
			if (new_precision*1.05 > 1) {
		    	this.stopped = true;
		    } 
			this.petriNet.addPlaceFromPartialPlaceEvaluation(pe);
			//System.out.print("\n");
			//System.out.print("Precision PN changes: ");
			//System.out.print(this.petriNet.getPlaces().toString());
			//System.out.print("\n");
			return true;
		}
		
		return false;
	}*/

	public Set<HybridDirectedGraphEdge> getEdges(PlaceEvaluation<N> ppe) {
		Set<HybridDirectedGraphEdge> res = new HashSet<HybridDirectedGraphEdge>();
		for (N  input : ppe.getPlaceInputNodes()) {
			HybridDirectedGraphNode target = (HybridDirectedGraphNode) input;
			for (N output: ppe.getPlaceOutputNodes()) {
				
				HybridDirectedGraphNode source = (HybridDirectedGraphNode) output;
				HybridDirectedGraphEdge e = new HybridDirectedGraphEdge(source, target);
				res.add(e);
			}
		}
		return res;
	}
	
	private void addPlace(PlaceEvaluation<N> p) {
		this.conflictStrategy.addPlace(p);
	}


	public void stop() {
		this.stopped=true;
	}


	/*
	 * Checks if ppe is in conflict with set places according to the selected strategy.
	 */
	private <T extends PartialPlaceEvaluation<N>> boolean checkConflict(PartialPlaceEvaluation<N> ppe) {
				//long startTime = System.currentTimeMillis();
				boolean result = this.conflictStrategy.checkConflict(ppe);
				//long stopTime = System.currentTimeMillis();
				//System.out.println("CheckConflict required: " + (stopTime-startTime) + "ms");
				//System.out.println("---- Conflict found for "+ppe +": **** " + result);
		return result;
	}




	/*
	 * Given a place p, check if for each output node and each input node there exists an edge in the graph.
	 */
	public static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> boolean checkPlaceConsistency(PartialPlaceEvaluation<N> ppe, Set<E> edges) {
		Set<N> outputNodes = ppe.getPlaceOutputNodes();
		Set<N> inputNodes = ppe.getPlaceInputNodes();
		boolean result = true;
		for (N outNode : outputNodes) {
			for (N inNode : inputNodes) {
				if (!containsEdge(outNode, inNode, edges)) {
					//System.out.println("PlaceEvaluation not consistent! OutputNodes: " + outNode + " InputNodes: " + inNode);
					return false; 
				}
			}
		}
		return result;
	
	}


	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> boolean containsEdge(N outputNode, N inputNode, Set<E> edges) {
		for (E edge : edges) {
			if (edge.getSource().equals(outputNode) && edge.getTarget().equals(inputNode))
				return true;
		}
		return false;
	}




	/*public Set<PlaceEvaluation<N>> getNonRedundantPlacesAboveThreshold(double threshold) {
        Set<PlaceEvaluation<N>> aboveThreshold = this.getPlacesAboveThreshold(threshold);
        //aboveThreshold.removeAll(getRedundantPlaces(aboveThreshold));
        return aboveThreshold;
    }*/


	@Override
	public String toString() {
		return "Cluster{" +
				"edges=" + edges +
				", inputNodes=" + inputNodes +
				", outputNodes=" + outputNodes +
				'}';
	}


	public String printStatistics() {
		StringBuffer result = new StringBuffer();
		result.append("*** Info for cluster " + System.identityHashCode(this) + " ***\n");
		result.append("No. of output nodes: " + this.getOutputNodes().size() + "\n");
		result.append("No. of input nodes: " + this.getInputNodes().size() + "\n");
		result.append("No. of edges: " + this.getEdges().size() + "\n");

		int maxInput = 0;
		int maxOutput = 0;
		for (N n : this.getOutputNodes()) {
			int maxNodeOutput = 0;
			int maxNodeInput = 0;
			for (HybridDirectedGraphEdge e : this.getEdges()) {
				if (e.getSource().equals(n))
					maxNodeOutput++;
				if (e.getTarget().equals(n))
					maxNodeInput++;
			}
			if (maxNodeOutput > maxOutput)
				maxOutput = maxNodeOutput;
			if (maxNodeInput > maxInput)
				maxInput = maxNodeInput;
		}

		for (N n : this.getInputNodes()) {
			int maxNodeOutput = 0;
			int maxNodeInput = 0;
			for (HybridDirectedGraphEdge e : this.getEdges()) {
				if (e.getSource().equals(n))
					maxNodeOutput++;
				if (e.getTarget().equals(n))
					maxNodeInput++;
			}
			if (maxNodeOutput > maxOutput)
				maxOutput = maxNodeOutput;
			if (maxNodeInput > maxInput)
				maxInput = maxNodeInput;
		}

		result.append("Max outgoing edges: " + maxOutput + ". Max ingoing edges " + maxInput + "\n");

		int maxIO = Math.max(maxOutput, maxInput);

		double candidateNum = 0;
		for (int i=1; i<=maxIO; i++)
			candidateNum = candidateNum + CombinatoricsUtils.binomialCoefficientDouble(this.getEdges().size(), i);

		result.append("No. of possible candidates (not consistent are included): " + candidateNum + "\n");

		return new String(result);
	}

	/*public void enableCheckPresicion(HybridPetrinet pn) {
		this.checkPresicion = true;
		this.petriNet = pn.cloneToPN();
		Place startPlace = this.petriNet.getPlace("start");
	    iMarking = new Marking();
		iMarking.add(startPlace);
		Place endPlace = this.petriNet.addPlace("end");
	    fMarkings = new Marking[1];
	    Marking fm = new Marking();
	    fm.add(endPlace);        
	    fMarkings[0] = fm;
	    
		MetricsComputator computator;
		computator = new MetricsComputator(this.petriNet, this.hCG.getLog(), this.iMarking, this.fMarkings);
		this.precision = computator.getPrecision();	
		if (this.precision*1.05 > 1 || true) {
			this.stopped = true;
		} 
		System.out.print("\n");
		System.out.print("INIT PRECISION: " + precision);
		System.out.print("\n");
		
		/*this.petriNet = new HybridPetrinet("Cluster Petrinet");
		for (PartialPlaceEvaluation<?> p: ConflictStrategyLongDep.getCheckedPlaces() ) {
			this.petriNet.addPlaceFromPartialPlaceEvaluation(p);
		}
		Place startPlace = this.petriNet.addPlace("start");
        Marking im = new Marking();
        im.add(startPlace);
        Transition startTransition = this.petriNet.getTransition("start");
        this.petriNet.addArc(startPlace, startTransition);
        
        Place endPlace = this.petriNet.addPlace("end");
        Marking fm = new Marking();
        fm.add(endPlace);        
        Transition endTransition = this.petriNet.getTransition("end");
        this.petriNet.addArc(endTransition, endPlace);
	    
	}*/

	/*public void setSelfLoops(Set<String> selfLoops) {
		this.selfLoops = selfLoops;
	}*/
	
	/*public void setOptionalSelfLoops(Set<String> selfLoops) {
		this.optionalSelfLoops = selfLoops;
	}*/

	/*    private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> getRedundantPlaces(Set<PlaceEvaluation<N>> placesAboveThreshold) {
        System.out.println("Redundant place computation started!");
    	Set<PlaceEvaluation<N>> toBeDiscarded = new HashSet<>();

        for (PlaceEvaluation<N> p : placesAboveThreshold) {
            if(isRedundant(placesAboveThreshold, p))
                toBeDiscarded.add(p);
        }
        System.out.println("Redundant place computation ended!");
        return toBeDiscarded;
    }*/


	/* To be called AFTER evaluateBestPlaces!
       The method scans this.places and sees if pe is redundant, i.e., if there is a set of placeEvaluation such that:
       the union of input sets is equal to pe input set
       the intersection of input sets is empty
       the union of output sets is equal to pe output set
       the intersection of output sets is empty
	 */
	/*private static <N extends AbstractDirectedGraphNode> boolean isRedundant(Set<PlaceEvaluation<N>> aboveThreshold, PlaceEvaluation<N> pe) {
        // First of all, remove pe from above threshold.
        Set<PlaceEvaluation<N>> otherPlaces = new HashSet<>();
        otherPlaces.addAll(aboveThreshold);
        if (!otherPlaces.remove(pe))
            throw new RuntimeException("The input place evaluation should be contained in the set of place evaluations above the threshold");

        // We need the array to access elements by index (the apache math methods returns an iterator on indices)
        PlaceEvaluation<N>[] otherPlacesArray = (PlaceEvaluation<N>[]) otherPlaces.toArray(new PlaceEvaluation<?>[otherPlaces.size()]);


        // We need to try every possible combination of i elements from the set otherPlacesArray.
        for (int i=2; i < otherPlacesArray.length; i++) {

            Iterator<int[]> it = org.apache.commons.math3.util.CombinatoricsUtils.combinationsIterator(otherPlacesArray.length, i);

            // Each next() is an array of indices representing the current choice of elements.
            while (it.hasNext()) {
                int[] indexesArray = it.next();
                //System.out.println("OtherPlaces number: " + otherPlacesArray.length + ". Current combination: " + Arrays.toString(indexesArray));
                Set<PlaceEvaluation<N>> currentCombination = new HashSet<>();

                // Build the set of PlacesEvalutions corresponding to the current choice of elements
                for (int j=0; j<indexesArray.length; j++)
                    currentCombination.add(otherPlacesArray[indexesArray[j]]);

                // Call the method that actually checks the redundancy
                if (checkRedundancy(currentCombination, pe))
                    return true;
            }
        }
        return false;
    }


    private static <N extends AbstractDirectedGraphNode> boolean checkRedundancy(Set<PlaceEvaluation<N>> currentCombination, PlaceEvaluation<N> placeEval) {
        // Check nullity of inputs!
        if (currentCombination==null || currentCombination.size()<2 || placeEval==null)
            throw new RuntimeException("Check the inputs of checkRedundancy!");

        // Build the union and the intersection of inputPlaces and outputPlaces of the currentCombination
        Set<N> intersectionInputPlaces = new HashSet<>();
        Set<N> intersectionOutputPlaces = new HashSet<>();
        Set<N> unionInputPlaces = new HashSet<>();
        Set<N> unionOutputPlaces = new HashSet<>();

        PlaceEvaluation<N>[] currentCombinationArray = (PlaceEvaluation<N>[]) currentCombination.toArray(new PlaceEvaluation<?>[currentCombination.size()]);
        intersectionInputPlaces.addAll(currentCombinationArray[0].getPlaceInputNodes());
        intersectionOutputPlaces.addAll(currentCombinationArray[0].getPlaceOutputNodes());

        for (PlaceEvaluation<N> p : currentCombination) {
            intersectionInputPlaces.retainAll(p.getPlaceInputNodes());
            intersectionOutputPlaces.retainAll(p.getPlaceOutputNodes());
            unionInputPlaces.addAll(p.getPlaceInputNodes());
            unionOutputPlaces.addAll(p.getPlaceOutputNodes());
        }

        if (intersectionInputPlaces.size() != 0 || intersectionOutputPlaces.size() != 0)
            return false;

        if (unionInputPlaces.equals(placeEval.getPlaceInputNodes()) && unionOutputPlaces.equals(placeEval.getPlaceOutputNodes())){
        	System.out.println("Removed "+placeEval+" because of:");
        	for (PlaceEvaluation<N> placeEvaluation : currentCombinationArray) {
				System.out.println(placeEvaluation);
			}
        }

        return (unionInputPlaces.equals(placeEval.getPlaceInputNodes()) && unionOutputPlaces.equals(placeEval.getPlaceOutputNodes()));
    }*/


}
