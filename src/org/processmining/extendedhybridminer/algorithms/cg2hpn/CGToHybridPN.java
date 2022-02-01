package org.processmining.extendedhybridminer.algorithms.cg2hpn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.hybridpetrinet.Cluster;
import org.processmining.extendedhybridminer.models.hybridpetrinet.FitnessType;
import org.processmining.extendedhybridminer.models.hybridpetrinet.HybridPetrinet;
import org.processmining.extendedhybridminer.models.hybridpetrinet.PlaceEvaluation;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.CandidatePlaceIteratorEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.BoundedInputANDOutput;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.BoundedInputPLUSOutput;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.BoundedSplitJoinOnly;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategy;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategyEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.ConflictStrategyEnum;
import org.processmining.extendedhybridminer.plugins.HybridPNMinerSettings;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 19/08/16.
 */
public class CGToHybridPN {
	
    private static boolean clustering_disabled = false;
    private static boolean additionalConsistensyChecks = true;
	private static boolean addLongDependencies = true;
	private static FitnessType fitnessType;
	private static TraceVariantsLog variants;
	private static int thresholdStopIt;
	
    public static <N extends AbstractDirectedGraphNode> HybridPetrinet fuzzyCGToFuzzyPN(ExtendedCausalGraph graph, HybridPNMinerSettings pNSettings) {
		long totalStartTime = System.currentTimeMillis();
		HybridPetrinet result = new HybridPetrinet("minedHybridPetrinet");
		result.setColors(graph.getSureColor(), graph.getUnsureColor(), graph.getLongDepColor());
        // We consider only sure edges!
		LinkedHashSet<HybridDirectedGraphEdge> edges = graph.getSureGraphEdges();
		LinkedHashSet<HybridDirectedGraphEdge> edgesLD = graph.getLongDepGraphEdges();
		thresholdStopIt = pNSettings.getThresholdEarlyCancelationIterator();
		fitnessType = pNSettings.getFitnessType();
		/*switch (fitnessType) {
			case GLOBAL:
				variants = graph.getTraceVariants().filterMostFrequent(pNSettings.getPlaceEvalThreshold());
				break;
			case LOCAL:
				variants = graph.getTraceVariants().getVariants();
				break;
		}*/
		variants = graph.getTraceVariants();
		
		// New parameters to be added to the graphical interface!
//		long timerSec = Integer.MAX_VALUE;
//		AtomicInteger maxPlaceNumber = new AtomicInteger(Integer.MAX_VALUE);
//		int inputPlusOutputBound = Integer.MAX_VALUE;
		//int inputPlusOutputBound = 5;
//		int inputBound = Integer.MAX_VALUE;
//		int outputBound = Integer.MAX_VALUE;
//		CandidatePlaceSelectionStrategyEnum selectionStrategyType = CandidatePlaceSelectionStrategyEnum.INPUT_PLUS_OUTPUT_BOUND;
//		CandidatePlaceIteratorEnum placeIteratorType = CandidatePlaceIteratorEnum.EDGE_NUMBER;
//		ConflictStrategyEnum conflictStrategyType = ConflictStrategyEnum.DEFAULT_CONFLICT_STRATEGY;

		// Update by sabi
		//long timerSec = pNSettings.getMaxTime();
		AtomicInteger maxPlaceNumber = new AtomicInteger(pNSettings.getMaxPlaceNumber());
		int inputPlusOutputBound = pNSettings.getPlaceNumber();
		//int inputPlusOutputBound = 5;
		int inputBound = pNSettings.getIPlaceNumber();
		int outputBound = pNSettings.getOPlaceNumber();
		CandidatePlaceSelectionStrategyEnum selectionStrategyType = pNSettings.getPlaceStrategy();
		CandidatePlaceIteratorEnum placeIteratorType = pNSettings.getOrderingStrategy();
		ConflictStrategyEnum conflictStrategyType = pNSettings.getConflictStrategy();
		
		switch(conflictStrategyType) {
			case MINIMAL_PLACES:
				if (placeIteratorType.equals(CandidatePlaceIteratorEnum.MINIMAL)) {
					additionalConsistensyChecks = false;
				} else {
					throw new RuntimeException("Place Iterator and Conflict Strategy are not compatible!");
				}
				break;
			case MINIMAL_PLACES_BACKWARDS:
				if (placeIteratorType.equals(CandidatePlaceIteratorEnum.MINIMAL_BACKWARDS)) {
					additionalConsistensyChecks = false;
				} else {
					throw new RuntimeException("Place Iterator and Conflict Strategy are not compatible!");
				}
				break;
			/*case CONFLICT_STRATEGY_LONG_DEP:
				if (placeIteratorType.equals(CandidatePlaceIteratorEnum.EDGES_FIRST)) {
					additionalConsistensyChecks = false;
					addLongDependencies = true;
					placeIteratorType = CandidatePlaceIteratorEnum.MINIMAL;
				} else {
					throw new RuntimeException("Place Iterator and Conflict Strategy are not compatible!");
				}
				break;*/
			default :
				break;
		}
			
		
		//ConflictStrategyEnum conflictStrategyType = ConflictStrategyEnum.NO_CONFLICT_STRATEGY;
		CandidatePlaceSelectionStrategy<HybridDirectedGraphNode> selectionStrategy = selectSelectionStrategy(selectionStrategyType, inputPlusOutputBound, inputBound, outputBound);

		//long t_start=System.currentTimeMillis();
		// Build the clusters
		Set<Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode>> clusters = identifyClusters(edges, selectionStrategy, placeIteratorType, conflictStrategyType, maxPlaceNumber, graph, false);
		
		
		//long t_end=System.currentTimeMillis();
		//System.out.println("NUMBER OF CLUSTERS "+clusters.size());
		//System.out.println("Time required for computing cluster macro-structures "+(t_end-t_start)+" milliseconds");


		/*
		 * MULTITHREAD
		 */
/*				ExecutorService exec = Executors.newCachedThreadPool();

				//		System.out.println("*********** Start multithread place evaluations on clusters ***********");
				//		long startTime = System.currentTimeMillis();

				for (Cluster<HybridDirectedSureGraphEdge, HybridDirectedGraphNode> c : clusters) {
					c.setLog(log);
					c.setActivityFrequencyMap(graph.getActivityFrequencyMap());
					c.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
					c.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
					exec.execute(c);
				}

				exec.shutdown();
				try {
					boolean terminated = exec.awaitTermination(timerSec, TimeUnit.SECONDS);
					if (!terminated) {
						for (Cluster<HybridDirectedSureGraphEdge, HybridDirectedGraphNode> c : clusters) {
							c.stop();
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
*/		/*
		 * END MULTITHREAD
		 */

		/*
		 * MONOTHREAD
		 */
		//ExecutorService executor = Executors.newSingleThreadExecutor();
		for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clusters) {
			c.setActivityFrequencyMap(graph.getActivityFrequencyMap());
			c.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
			c.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
			//c.setSelfLoops(graph.getSelfLoops());
			//c.setOptionalSelfLoops(graph.getOptionalSelfLoops());
			
//			System.out.println("*********** CLUSTER "+c+" of size "+c.getEdges().size());
//			System.out.print("EDGES: ");
//			for (HybridDirectedSureGraphEdge edge : c.getEdges()) {
//				System.out.print(edge);
//			}
//			System.out.println("*********** END CLUSTER ");
			
			
			// Update by sabi
			// executor.execute(c);
			c.run();
			
			//c.printDotFile();
			//System.out.println(c.printStatistics()); 
		} 
		
		
 		//executor.shutdown();
 		/*try
 		{
			boolean terminated=executor.awaitTermination(timerSec,TimeUnit.SECONDS);
			if(!terminated)
			{
				for(Cluster<HybridDirectedSureGraphEdge,HybridDirectedGraphNode> c:clusters)
				{
					c.stop();
				}
			}
		}
 		catch(InterruptedException e)
 		{ho 
			e.printStackTrace();
		}*/
		/*
		 * END MONOTHREAD
		 */	
		
		/*Set<PlaceEvaluation<HybridDirectedGraphNode>> tempPlaceEvaluation = new HashSet<PlaceEvaluation<HybridDirectedGraphNode>>();

		for (Cluster<HybridDirectedSureGraphEdge, HybridDirectedGraphNode> c : clusters) {
			tempPlaceEvaluation.addAll(c.getPlacesToBeAdded());
		}
		
		Set<PlaceEvaluation<HybridDirectedGraphNode>> redundantPlaces = computeRedundantPlaces(tempPlaceEvaluation);*/
		 		
 		
		/*
		 * I have to remove places that have the same input and output sets automatically removed as I adding them to the same set
		 * and the equals has been defined as having same input and output sets.
		 */
		//Set<PlaceEvaluation<HybridDirectedGraphNode>> allPlaceEvaluations = new HashSet<>();
		//int maxIO = 0;

		for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clusters) {
			
			if (!additionalConsistensyChecks) {
				for (PlaceEvaluation<HybridDirectedGraphNode> pe : c.getPlacesToBeAdded()) {
					result.addPlaceFromPartialPlaceEvaluation(pe);	
				}
			} else {
				/*Set<PlaceEvaluation<HybridDirectedGraphNode>> nonRedundantPlaces = computeNonRedundantPlaces(c.getPlacesToBeAdded());
				System.out.println(nonRedundantPlaces);
				*/
				//Set<PlaceEvaluation<HybridDirectedGraphNode>> selfloopPlaces = identifySelfLoops(c.getPlacesToBeAdded());
				outerLoop:
				for (PlaceEvaluation<HybridDirectedGraphNode> pe : c.getPlacesToBeAdded()) {

					/*
					 * We add places with self-loops, but we do not add their PROPER sub-sets!!!
					 */
					///if (!isSelfLoopPlaceSubset(pe,selfloopPlaces)){
					for (PlaceEvaluation<HybridDirectedGraphNode> pe2 : c.getPlacesToBeAdded()) {
						if (isProperSubset(pe, pe2)) {
							Set<HybridDirectedGraphEdge> currEdges = graph.getEdges();
							for (HybridDirectedGraphEdge edge : currEdges) {
								if (pe.getPlaceOutputNodes().contains(edge.getSource()) && pe.getPlaceOutputNodes().contains(edge.getTarget())){
									//System.out.println(pe.toString());
									//System.out.println("edge removed: " + edge.toString());
									graph.removeEdge(edge);
								}
							}
							continue outerLoop;
						}
					}
					result.addPlaceFromPartialPlaceEvaluation(pe);
					//if (!isProperSubset(pe,selfloopPlaces)){
//						allPlaceEvaluations.add(pe);
						//result.addPlaceFromPlaceEvaluation(pe);
//					
//						//DEBUG:
//						//int currentMaxIO = pe.getMaxIO();
//						//if (currentMaxIO > maxIO)
//						//	maxIO = currentMaxIO;
					/*} else {
						Set<HybridDirectedGraphEdge> currEdges = graph.getEdges();
						for (HybridDirectedGraphEdge edge : currEdges) {
							if (pe.getPlaceOutputNodes().contains(edge.getSource()) && pe.getPlaceOutputNodes().contains(edge.getTarget())){
								System.out.print("\n");
								System.out.print("remove edge: " + edge.toString());
								System.out.print("\n");
								graph.removeEdge(edge);
							}
						}
					}*/
				}
			}	
		}
		
		if (addLongDependencies) {
			Set<Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode>> clustersLd = identifyClusters(edgesLD, selectionStrategy, placeIteratorType, conflictStrategyType, maxPlaceNumber, graph, true);
			
			for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clustersLd) {
				c.setActivityFrequencyMap(graph.getActivityFrequencyMap());
				c.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
				c.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
				//c.setOptionalSelfLoops(new HashSet());
				c.run();
			} 
			
			for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clustersLd) {
				for (PlaceEvaluation<HybridDirectedGraphNode> pe : c.getPlacesToBeAdded()) {
					result.addPlaceFromPartialPlaceEvaluation(pe);	
				}	
			}
		}
		
		
		result = addStartAndEndPlaces(result);
		/*if (addLongDependencies) {
			placeIteratorType = CandidatePlaceIteratorEnum.OUTPUT_THEN_INPUT_NUMBER;
			//ConflictStrategyLongDep.firstIteratorFinished();		
			LinkedHashSet<HybridDirectedSureGraphEdge> edgesSet = new LinkedHashSet<>();
			for (HybridDirectedSureGraphEdge edge : edges) {
				edgesSet.add(edge);
			}
			Cluster<HybridDirectedSureGraphEdge, HybridDirectedGraphNode> bigCluster 
			      = new Cluster<HybridDirectedSureGraphEdge, HybridDirectedGraphNode>(
			    		  graph, edgesSet, selectionStrategy, placeIteratorType,
			    		  conflictStrategyType, maxPlaceNumber, fitnessType, variants);
				
			bigCluster.setActivityFrequencyMap(graph.getActivityFrequencyMap());
			bigCluster.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
			bigCluster.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
			//bigCluster.enableCheckPresicion(result);
			
			bigCluster.run();
			
			for (PlaceEvaluation<HybridDirectedGraphNode> pe : bigCluster.getPlacesToBeAdded()) {
                //allPlaceEvaluations.add(pe);
				result.addPlaceFromPartialPlaceEvaluation(pe);
			}
		}*/
//		System.out.println();
//		System.out.println("PLACES ABOVE THRESHOLD:");
//		System.out.println(allPlaceEvaluations);
//		System.out.println();
		//System.out.println();
		//System.out.println("maxIO : " + maxIO);


/*		
		 * Now in allPlaceEvaluation I have all the places that have to be added. Start the computation for nonRedundancy!
		 
		Set<PlaceEvaluation<HybridDirectedGraphNode>> nonRedundantPlaces = computeNonRedundantPlaces(allPlaceEvaluations);
		System.out.println(nonRedundantPlaces);*/

		/*
		 * Remove places that are subset of other places. A place p is subset of q if pre(p) subseteq pre(q) AND post(p) subseteq post(q).
		 */
		//Set<PlaceEvaluation<FuzzyDirectedGraphNode>> finalPlaces = computeSubsetPlaces(nonRedundantPlaces);
		//Set<PlaceEvaluation<FuzzyDirectedGraphNode>> finalPlaces = nonRedundantPlaces;

		/*
		 * Now that I eliminated redundant places I can add the others to the net.
		 */
//		for (PlaceEvaluation<HybridDirectedGraphNode> pe : allPlaceEvaluations) {
//			result.addPlaceFromPlaceEvaluation(pe);
//			//System.out.println(pe);
//			//System.out.println(pe.evaluateReplayScore());
//		}

		/*
        Add **ALL** transitions of the causal graph to the set of transition of the petrinet.
        (Transition already present will not be added.)
		 */


		/* Then add the sure and uncertain arcs between transitions in the net coming from the causal graph
            I do not know which sure transitions have met the threshold thus have been replaced by a place transition,
             but such a check is directly in the method
		 */
		for (HybridDirectedGraphEdge edge : graph.getEdges())
			result.addTransitionsArcFromFCGEdge(edge);

		/*
		 * Finally, we need to add all the transitions of the Fuzzy Causal Graph
		 */
		for (DirectedGraphNode activity : graph.getGraph().getNodes()) {
			Transition transition = result.getTransition(activity.getLabel());
			if (transition==null)
				result.addTransition(activity.getLabel());
		}

		/*		System.out.println("********************************");
		System.out.println("Transitions : "+result.getTransitions().size());
		System.out.println("Places : "+(result.getPlaces().size()+2));
		int sureEdges = 0;
		int unsureEdges = 0;
		int f1 = 0;
		int f1_hat = 0;
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : result.getEdges()) {
			if (edge instanceof SureTransitionsArc)
				sureEdges++;
			else if (edge instanceof UncertainTransitionsArc)
				unsureEdges++;
			else
				f1++;
		}
		for (Place place : result.getPlaces()) {
			f1_hat = f1_hat+ (result.getGraph().getInEdges(place).size()*result.getGraph().getOutEdges(place).size());
		}
		System.out.println("SureEdges : "+sureEdges);
		System.out.println("Uncertain Edges : "+unsureEdges);
		System.out.println("f1 : "+(f1+2));
		System.out.println("f1 hat : "+f1_hat);
		System.out.println("********************************");*/
		long totalEndTime = System.currentTimeMillis();
		long totalElapsedTimeSec = (totalEndTime - totalStartTime)/1000;
		try {
			FileWriter fW = new FileWriter(new File("./logFiles/statistics.txt"));
			fW.write("Transitions "+result.getTransitions().size()+"\n");
			fW.write("Places "+(result.getPlaces().size()+2)+"\n");
			fW.write("Total hybrid PN computation time: " + totalElapsedTimeSec + " sec.");
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Total hybrid PN computation time: " + totalElapsedTimeSec + " sec.");

		
		return result;
	}




	public static HybridPetrinet addStartAndEndPlaces(HybridPetrinet fPN){
		Place startPlace = fPN.addPlace("start");
	    //iMarking = new Marking();
		//iMarking.add(startPlace);
	    Transition startTransition = fPN.addTransition("start");
	    fPN.addArc(startPlace, startTransition);
	        
	    Place endPlace = fPN.addPlace("end");
	    //fMarkings = new Marking[1];
	    //Marking fm = new Marking();
	    //fm.add(endPlace);        
	    //fMarkings[0] = fm;
	    Transition endTransition = fPN.addTransition("end");
	    fPN.addArc(endTransition, endPlace);
	    
	    return fPN;
	}




	/*private static Set<PlaceEvaluation<HybridDirectedGraphNode>> identifySelfLoops(Set<PlaceEvaluation<HybridDirectedGraphNode>> places){
		 Set<PlaceEvaluation<HybridDirectedGraphNode>> selfLoopPlaces = new HashSet<PlaceEvaluation<HybridDirectedGraphNode>>();
		 for (PlaceEvaluation<HybridDirectedGraphNode> place : places) {
			 if (isSelfLoop(place))
				 selfLoopPlaces.add(place);
		}
		 return selfLoopPlaces;
	}*/
	
	/*
	 * Returns true if the sets of input and output nodes are not disjoint
	 */
	/*private static boolean isSelfLoop(PlaceEvaluation<HybridDirectedGraphNode> place){
		for (HybridDirectedGraphNode input : place.getPlaceInputNodes()) {
			if (place.getPlaceOutputNodes().contains(input))
				return true;
		}
		return false;
	}*/
	
	private static <N extends AbstractDirectedGraphNode>  boolean isProperSubset(PlaceEvaluation<N> subsetPlace, PlaceEvaluation<N> supersetPlace){
		if (supersetPlace.getPlaceOutputNodes().containsAll(subsetPlace.getPlaceOutputNodes()) && 
				supersetPlace.getPlaceInputNodes().containsAll(subsetPlace.getPlaceInputNodes()) && 
				(supersetPlace.getPlaceOutputNodes().size()>subsetPlace.getPlaceOutputNodes().size() ||
				supersetPlace.getPlaceInputNodes().size()>subsetPlace.getPlaceInputNodes().size()))
			return true;
		else 
			return false;
	}

	
	/*private static <N extends AbstractDirectedGraphNode>  boolean isSelfLoopPlaceSubset(PlaceEvaluation<N> pe,
			Set<PlaceEvaluation<N>> selfloopPlaces) {
		for (PlaceEvaluation<N> selfLoopPlace : selfloopPlaces) {
			if(isProperSubset(pe, selfLoopPlace))
				return true;
		}
		return false;
	}*/


	private static <N extends AbstractDirectedGraphNode> CandidatePlaceSelectionStrategy<N> selectSelectionStrategy(CandidatePlaceSelectionStrategyEnum selectionStrategyType, int inputPlusOutputBound, int inputBound, int outputBound) {
		switch(selectionStrategyType) {
			case INPUT_PLUS_OUTPUT_BOUND :
				return new BoundedInputPLUSOutput<N>(inputPlusOutputBound);

			case INPUT_AND_OUTPUT_BOUND :
				return new BoundedInputANDOutput<N>(outputBound, inputBound);

			case SPLIT_JOIN_ONLY_BOUND:
				return (CandidatePlaceSelectionStrategy<N>) new BoundedSplitJoinOnly<>(inputPlusOutputBound-1);

			default:
				throw new RuntimeException("Please select a valid candidate place selection strategy!");
		}

	}

	// not used
//	private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> computeSubsetPlaces(Set<PlaceEvaluation<N>> allPlaces) {
//		Set<PlaceEvaluation<N>> result = new HashSet<>();
//		// Check the subset place by place
//		for(PlaceEvaluation<N> pe : allPlaces) {
//			if (!(isSubset(pe, allPlaces)))
//				result.add(pe);
//		}
//
//		return result;
//	}

	// not used
//	private static <N extends AbstractDirectedGraphNode> boolean isSubset(PlaceEvaluation<N> pe, Set<PlaceEvaluation<N>> allPlaces) {
//		for (PlaceEvaluation<N> other : allPlaces) {
//			if (!other.equals(pe) && other.getPlaceOutputNodes().containsAll(pe.getPlaceOutputNodes()) && other.getPlaceInputNodes().containsAll(pe.getPlaceInputNodes())) {
//				System.out.println("Place Evaluation: " + pe);
//				System.out.println("is subset of: " + other);
//				return true;
//			}
//		}
//		return false;
//	}

	/*private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> computeRedundantPlaces(Set<PlaceEvaluation<N>> allPlaces) {
		Set<PlaceEvaluation<N>> redundantPlaces = new HashSet<>();

		// Check the redundancy place by place
		for(PlaceEvaluation<N> pe : allPlaces) {*/
			/*if (!(isRedundant(pe, allPlaces)))
				result.add(pe);*/
	/*		 redundantPlaces.addAll(getRedundantPlaces(pe, allPlaces));

		}
		System.out.println(redundantPlaces);
		return redundantPlaces;
	}*/

	// not used
//	private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> computeNonRedundantPlaces(Set<PlaceEvaluation<N>> allPlaces) {
//		Set<PlaceEvaluation<N>> result = new HashSet<>();
//		Set<PlaceEvaluation<N>> redundantPlaces = new HashSet<>();
//
//		// Check the redundancy place by place
//		for(PlaceEvaluation<N> pe : allPlaces) {
//			if (!(isRedundant(pe, allPlaces)))
//				result.add(pe);
//			 //redundantPlaces.addAll(getRedundantPlaces(pe, allPlaces));
//
//		}
//		/*System.out.println(redundantPlaces);
//		result.addAll(allPlaces);
//		result.removeAll(redundantPlaces);*/
//		return result;
//	}
	
	/*private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> getRedundantPlaces(PlaceEvaluation<N> pe, Set<PlaceEvaluation<N>> allPlaces){
		Set<PlaceEvaluation<N>> redundantPlaces = new HashSet<>();
		Set<PlaceEvaluation<N>> properSubPlaces = getProperSubPlaces(pe, allPlaces);
    	List<Object[]> combinations = new ArrayList<Object[]>();
    	for (int i = 2; i < properSubPlaces.toArray().length; i++) {
        	combinations.addAll(Utils.printCombination(properSubPlaces.toArray(),properSubPlaces.toArray().length,i));
		}
    	List<Set<PlaceEvaluation<N>>> placeEvaluationCombinations = new ArrayList<Set<PlaceEvaluation<N>>>();
    	for (Object[] combination : combinations) {
    		Set<PlaceEvaluation<N>> placeEvaluationCombination =new HashSet<PlaceEvaluation<N>>();
			for (int i = 0; i < combination.length; i++) {
				placeEvaluationCombination.add((PlaceEvaluation) combination[i]);
			}
			placeEvaluationCombinations.add(placeEvaluationCombination);
		}
		for (Set<PlaceEvaluation<N>> placeEvaluationCombination : placeEvaluationCombinations) {
			System.out.println(placeEvaluationCombination);
			if (checkRedundancy(placeEvaluationCombination, pe)){
				redundantPlaces.addAll(placeEvaluationCombination);
			}
		}
		return redundantPlaces;
		
	}
		
	private static <N extends AbstractDirectedGraphNode> boolean checkRedundancy(Set<PlaceEvaluation<N>> placeEvaluationCombination, PlaceEvaluation<N> pe){
		boolean redundant = false;
		Set<N> inputNodes = new HashSet<N>();
		Set<N> outputNodes = new HashSet<N>();

		for (PlaceEvaluation<N> placeEvaluation : placeEvaluationCombination) {
			inputNodes.addAll(placeEvaluation.getPlaceInputNodes());
			outputNodes.addAll(placeEvaluation.getPlaceOutputNodes());
		}
		
		if (pe.getPlaceInputNodes().equals(inputNodes) && pe.getPlaceOutputNodes().equals(outputNodes))
			redundant = true;
		return redundant;
	}
	
	private static  <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> getProperSubPlaces(PlaceEvaluation<N> pe, Set<PlaceEvaluation<N>> allPlaces){
		Set<PlaceEvaluation<N>> properSubPlaces = new HashSet<>();
		for (PlaceEvaluation<N> placeEvaluation : allPlaces) {
			if (isProperSubset(placeEvaluation, pe))
				properSubPlaces.add(placeEvaluation);
		}
		return properSubPlaces;
	}*/

	// never used
//	private static <N extends AbstractDirectedGraphNode> boolean isRedundant(PlaceEvaluation<N> pe, Set<PlaceEvaluation<N>> allPlaces) {
//		// If pe has a single inputNode or a single OutputNode cannot be redundant!
//		if (pe.getPlaceInputNodes().size()==1 || pe.getPlaceOutputNodes().size()==1)
//			return false;
//
//		// Otherwise, let us compute the possible partitions of the input and those of the output:
//		List<List<List<List<N>>>> outputNodePartitions = Utils.getAllPartitions(pe.getPlaceOutputNodes());
//		List<List<List<List<N>>>> inputNodePartitions = Utils.getAllPartitions(pe.getPlaceInputNodes());
//
//		// I only need to check the bijections from output to inputs which partition sets WITH THE SAME CARDINALITY!
//		// Hence, I need to compute the smallest of the two List of List of ...
//		int partitionsToCheck = Math.min(outputNodePartitions.size(), inputNodePartitions.size());
//
//		//this number cannot be one, given the check on the input and output nodes...
//		if (partitionsToCheck == 1)
//			throw new RuntimeException("Something wrong with the isRedundant algorithm");
//
//		// no need to check the partitions with a single element...
//		for (int i=1; i<partitionsToCheck; i++) {
//			/* Compute all possible bijections from outputPartition of size i+1 and inputPartition of size i+1!
//			 * The bijections are obtained as follows: I leave the outputNode current partition list as it is and I compute all possible permutations
//			 * of the input node partition list. The bijection always associate the first element of the first list with the first of the second. 
//			 */
//			List<List<List<N>>> allOutputPartitionsOfiElements = outputNodePartitions.get(i);
//			List<List<List<N>>> allInputPartitionsOfiElements = inputNodePartitions.get(i);
//
//			for (int ii=0; ii<allOutputPartitionsOfiElements.size(); ii++) {
//				for (int jj=0; jj<allInputPartitionsOfiElements.size(); jj++) {
//					/*
//					 * WARNING! generatePerm does **SIDE EFFECT** on the argument! Perform a copy first! 
//					 */
//					for (List<List<N>> currentInputPerm : Utils.generatePerm(new ArrayList<List<N>>(allInputPartitionsOfiElements.get(jj)))) {
//						// Now that I have a permutation, I have to check that all placeEval of the current bijection are in allPlaceEvaluation
//						boolean allPresent = true;
//
//						// I already know how many elements will be in the partition! They are i+1!
//						for (int k=0; k<i+1; k++) {
//							Set<N> currentOutputSet = new HashSet<N>(allOutputPartitionsOfiElements.get(ii).get(k));
//							Set<N> currentInputSet = new HashSet<N>(currentInputPerm.get(k));
//
//							// Now check if the placeEval is present!
//							PlaceEvaluation<N> currentPE = new PlaceEvaluation<N>(currentOutputSet, currentInputSet, null, null, 0, 0);
//							if (!allPlaces.contains(currentPE)) {
//								allPresent=false;
//								break;
//							}
//						}
//						if (allPresent) {
//							System.out.println("One redundant place found. OutputSet: " + pe.getPlaceOutputNodes());
//							System.out.println("InputSet: " + pe.getPlaceInputNodes());
//							System.out.println("It is implicated by OutputSet: " + allOutputPartitionsOfiElements.get(ii));
//							System.out.println("And InputSet: " + currentInputPerm);
//							return true;
//						}
//					}
//				}
//			}
//		}
//		return false;
//	}



	// Build the clusters by least fixpoint computations, according to the definition on the paper.
	private static <E extends HybridDirectedGraphEdge, N extends AbstractDirectedGraphNode> LinkedHashSet<Cluster<E, N>> identifyClusters(LinkedHashSet<E> edges, CandidatePlaceSelectionStrategy<N> selectionStrategy, CandidatePlaceIteratorEnum placeIteratorType, ConflictStrategyEnum conflictStrategyType, AtomicInteger maxPlaceNumber, ExtendedCausalGraph graph, boolean longDep) {
		
		
		LinkedHashSet<Cluster<E, N>> clusterSet = new LinkedHashSet<>();
		if (clustering_disabled) { 
			LinkedHashSet<HybridDirectedGraphEdge> newCluster2 = new LinkedHashSet<>();
			for (HybridDirectedGraphEdge edge : edges) {
				newCluster2.add(edge);
			}
			clusterSet.add(new Cluster<E, N>(graph, newCluster2, selectionStrategy, placeIteratorType, conflictStrategyType,
					maxPlaceNumber, fitnessType, variants, longDep, thresholdStopIt));
			return clusterSet;
		}
		
		
//		File directory =new File("./logFiles");
//		for (File logFile : directory.listFiles()) {
//			logFile.delete();
//		}
		

		// Efficiency: keep a set of edges already analyzed
		Set<E> alreadyAnalyzed = new HashSet<>();

		// for each edge not yet analyzed, create a cluster, as every edge must be contained in a cluster.
		for (E edge : edges) {
			if (alreadyAnalyzed.contains(edge))
				continue;

			// create the new set of edges constituting the new cluster
			LinkedHashSet<HybridDirectedGraphEdge> newCluster = new LinkedHashSet<>();

			// add the current edge to it
			newCluster.add(edge);
			alreadyAnalyzed.add(edge);
			Set<HybridDirectedGraphEdge> oldCluster = new HashSet<>();
			// add all the other edges to the current cluster.
			while (! oldCluster.equals(newCluster)) {
				// 1) oldCluster = new Cluster
				oldCluster.addAll(newCluster);

				// 2) Analyze all the edges in OldCluster and add them to newCluster...
				for (HybridDirectedGraphEdge e : oldCluster) {
					N source = (N) e.getSource();
					N target = (N) e.getTarget();

					Set<E> edgesForSource = getEdgesHavingSourceNode(source, edges);
					Set<E> edgesForTarget = getEdgesHavingTargetNode(target, edges);
					newCluster.addAll(edgesForSource);
					newCluster.addAll(edgesForTarget);

					//Add them to already analyzed, as they are already part of a cluster.
					alreadyAnalyzed.addAll(edgesForSource);
					alreadyAnalyzed.addAll(edgesForTarget);
				}
				// ...until there is no other edge to add.
			}
			// add it to the set of cluster
			clusterSet.add(new Cluster<E, N>(graph, newCluster, selectionStrategy, placeIteratorType, conflictStrategyType, maxPlaceNumber, fitnessType, variants, longDep, thresholdStopIt));
		}
		return clusterSet;
	}


	/*
    Given a source node s and a set of edges, the method returns all edges having s as source.
	 */
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesHavingSourceNode(N source, Set<E> edges) {
		Set<E> result = new HashSet<>();
		for (E edge : edges) {
			if (edge.getSource().equals(source))
				result.add(edge);
		}
		return result;
	}

	/*
    Given a target node t and a set of edges, the method returns all edges having t as target.
	 */
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesHavingTargetNode(N target, Set<E> edges) {
		Set<E> result = new HashSet<>();
		for (E edge : edges) {
			if (edge.getTarget().equals(target))
				result.add(edge);
		}
		return result;
	}

    // never used
//	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> double computeIOPartitionMaxPowerSet(Set<Set<E>> sets){
//		double maxPowerSet = 0;
//		for (Set<E> set : sets) {
//			Set<N> iNodes = new HashSet<N>();
//			Set<N> oNodes = new HashSet<N>();
//			for (E edge : set) {
//				iNodes.add((N) edge.getTarget());
//				oNodes.add((N) edge.getSource());
//			}
//			double numberOfReplay = Math.pow(2, iNodes.size()) * Math.pow(2,oNodes.size());
//			if (numberOfReplay>maxPowerSet)
//				maxPowerSet = numberOfReplay;
//		}
//		return maxPowerSet;
//	}

	/*
	 * Prints the partition(s) set
	 */
	// never used
//	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> void printPartition(Set<Set<E>> edgePartition){
//		Set<Set<N>> vInputNodes = new HashSet<Set<N>>();
//		Set<Set<N>> vOutputNodes = new HashSet<Set<N>>();
//		for (int i = 0; i < edgePartition.size(); i++) {
//			Set<E> set = (Set<E>) edgePartition.toArray()[i]; 
//			System.out.print("{");
//			for (E e : set) {
//				System.out.print(e.getSource().toString()+" "+e.getTarget().toString()+", ");
//			}
//			System.out.println("}");
//			System.out.println("*******");
//		}
//	}

    // never used
//	private static <E extends AbstractDirectedGraphEdge> void printPartitions(Set<Set<Set<E>>> edgePartitions){
//		for (Set<Set<E>> edgePartition : edgePartitions) {
//			printPartition(edgePartition);
//		}
//	} 

}
