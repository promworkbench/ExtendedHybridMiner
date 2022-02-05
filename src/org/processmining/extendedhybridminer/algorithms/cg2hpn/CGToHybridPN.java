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
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.extendedhybridminer.models.hybridpetrinet.FitnessType;
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


public class CGToHybridPN {
	
    private static FitnessType fitnessType;
	private static TraceVariantsLog variants;
	private static int thresholdStopIt;
	
    public static <N extends AbstractDirectedGraphNode> ExtendedHybridPetrinet fuzzyCGToFuzzyPN(ExtendedCausalGraph graph, HybridPNMinerSettings pNSettings) {
		long totalStartTime = System.currentTimeMillis();
		ExtendedHybridPetrinet result = new ExtendedHybridPetrinet("minedHybridPetrinet");
		result.setColors(graph.getSureColor(), graph.getUnsureColor(), graph.getLongDepColor());
        LinkedHashSet<HybridDirectedGraphEdge> edges = graph.getSureGraphEdges();
		LinkedHashSet<HybridDirectedGraphEdge> edgesLD = graph.getLongDepGraphEdges();
		thresholdStopIt = pNSettings.getThresholdEarlyCancelationIterator();
		fitnessType = pNSettings.getFitnessType();
		variants = graph.getTraceVariants();
		AtomicInteger maxPlaceNumber = new AtomicInteger(pNSettings.getMaxPlaceNumber());
		int inputPlusOutputBound = pNSettings.getPlaceNumber();
		int inputBound = pNSettings.getIPlaceNumber();
		int outputBound = pNSettings.getOPlaceNumber();
		CandidatePlaceSelectionStrategyEnum selectionStrategyType = pNSettings.getPlaceStrategy();
		CandidatePlaceIteratorEnum placeIteratorType = pNSettings.getOrderingStrategy();
		ConflictStrategyEnum conflictStrategyType = pNSettings.getConflictStrategy();
		CandidatePlaceSelectionStrategy<HybridDirectedGraphNode> selectionStrategy = selectSelectionStrategy(selectionStrategyType, inputPlusOutputBound, inputBound, outputBound);
		Set<Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode>> clusters = identifyClusters(edges, selectionStrategy, placeIteratorType, conflictStrategyType, maxPlaceNumber, graph, false);
		for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clusters) {
			c.setActivityFrequencyMap(graph.getActivityFrequencyMap());
			c.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
			c.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
			c.run();
		} 
		for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clusters) {
			for (PlaceEvaluation<HybridDirectedGraphNode> pe : c.getPlacesToBeAdded()) {
				result.addPlaceFromPartialPlaceEvaluation(pe);	
			}
		}
		Set<Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode>> clustersLd = identifyClusters(edgesLD, selectionStrategy, placeIteratorType, conflictStrategyType, maxPlaceNumber, graph, true);
		for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clustersLd) {
			c.setActivityFrequencyMap(graph.getActivityFrequencyMap());
			c.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
			c.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
			c.run();
		} 
		for (Cluster<HybridDirectedGraphEdge, HybridDirectedGraphNode> c : clustersLd) {
			for (PlaceEvaluation<HybridDirectedGraphNode> pe : c.getPlacesToBeAdded()) {
				result.addPlaceFromPartialPlaceEvaluation(pe);	
			}	
		}
		result = addStartAndEndPlaces(result);
		for (HybridDirectedGraphEdge edge : graph.getEdges())
			result.addTransitionsArcFromFCGEdge(edge);
		for (DirectedGraphNode activity : graph.getGraph().getNodes()) {
			Transition transition = result.getTransition(activity.getLabel());
			if (transition==null)
				result.addTransition(activity.getLabel());
		}
		long totalEndTime = System.currentTimeMillis();
		long totalElapsedTimeSec = (totalEndTime - totalStartTime)/1000;
		try {
			FileWriter fW = new FileWriter(new File("./logFiles/statistics.txt"));
			fW.write("Transitions "+result.getTransitions().size()+"\n");
			fW.write("Places "+(result.getPlaces().size()+2)+"\n");
			fW.write("Total hybrid PN computation time: " + totalElapsedTimeSec + " sec.");
			fW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Total hybrid PN computation time: " + totalElapsedTimeSec + " sec.");
		return result;
	}

	public static ExtendedHybridPetrinet addStartAndEndPlaces(ExtendedHybridPetrinet fPN){
		Place startPlace = fPN.addPlace("start");
	    Transition startTransition = fPN.addTransition("start");
	    fPN.addArc(startPlace, startTransition);
	    Place endPlace = fPN.addPlace("end");
	    Transition endTransition = fPN.addTransition("end");
	    fPN.addArc(endTransition, endPlace); 
	    return fPN;
	}
	
	private static <N extends AbstractDirectedGraphNode> CandidatePlaceSelectionStrategy<N> selectSelectionStrategy(CandidatePlaceSelectionStrategyEnum selectionStrategyType, int inputPlusOutputBound, int inputBound, int outputBound) {
		switch(selectionStrategyType) {
			case INPUT_PLUS_OUTPUT_BOUND :
				return new BoundedInputPLUSOutput<N>(inputPlusOutputBound);

			case INPUT_AND_OUTPUT_BOUND :
				return new BoundedInputANDOutput<N>(outputBound, inputBound);

			case SPLIT_JOIN_ONLY_BOUND:
				return new BoundedSplitJoinOnly<>(inputPlusOutputBound-1);

			default:
				throw new RuntimeException("Please select a valid candidate place selection strategy!");
		}
	}

	private static <E extends HybridDirectedGraphEdge, N extends AbstractDirectedGraphNode> LinkedHashSet<Cluster<E, N>> identifyClusters(LinkedHashSet<E> edges, CandidatePlaceSelectionStrategy<N> selectionStrategy, CandidatePlaceIteratorEnum placeIteratorType, ConflictStrategyEnum conflictStrategyType, AtomicInteger maxPlaceNumber, ExtendedCausalGraph graph, boolean longDep) {
		
		LinkedHashSet<Cluster<E, N>> clusterSet = new LinkedHashSet<>();
		Set<E> alreadyAnalyzed = new HashSet<>();
		for (E edge : edges) {
			if (alreadyAnalyzed.contains(edge))
				continue;
			LinkedHashSet<HybridDirectedGraphEdge> newCluster = new LinkedHashSet<>();
			newCluster.add(edge);
			alreadyAnalyzed.add(edge);
			Set<HybridDirectedGraphEdge> oldCluster = new HashSet<>();
			while (! oldCluster.equals(newCluster)) {
				oldCluster.addAll(newCluster);
				for (HybridDirectedGraphEdge e : oldCluster) {
					N source = (N) e.getSource();
					N target = (N) e.getTarget();
					Set<E> edgesForSource = getEdgesHavingSourceNode(source, edges);
					Set<E> edgesForTarget = getEdgesHavingTargetNode(target, edges);
					newCluster.addAll(edgesForSource);
					newCluster.addAll(edgesForTarget);
					alreadyAnalyzed.addAll(edgesForSource);
					alreadyAnalyzed.addAll(edgesForTarget);
				}
			}
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

}
