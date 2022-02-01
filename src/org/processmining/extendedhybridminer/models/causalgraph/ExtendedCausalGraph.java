package org.processmining.extendedhybridminer.models.causalgraph;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.extendedhybridminer.plugins.HybridCGMinerSettings;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Created by demas on 27/07/16.
 */
public class ExtendedCausalGraph extends CausalGraph<HybridDirectedGraphNode, HybridDirectedGraphEdge> {
	private Map<String, HybridDirectedGraphNode> labelNodeMap;
	private Map<String, Integer> activityFrequencyMap;

	private TraceVariantsLog traceVariants;
	
	private XLog log;
	private XLog unfilteredLog;
	private HashMap<Integer, String> activitiesMapping;
	private Map<String, Integer> mapActivityToIndex;
	private Map<HybridDirectedGraphNode, Integer> mapNodeToIndex;
	private HybridCGMinerSettings settings;
	private int eventsNumber;
	private DoubleMatrix2D directSuccessionCount;
	//private DoubleMatrix2D ABdependencyMetric;
	private DoubleMatrix2D rel1Metric;
	private DoubleMatrix2D rel2Metric;
	private DoubleMatrix2D outputDirectSuccessionMetric;
	private DoubleMatrix2D inputDirectSuccessionMetric;
	
	private DoubleMatrix2D EF;
	//private DoubleMatrix2D ABdependencyMetric;
	private DoubleMatrix2D rel1LD;
	private DoubleMatrix2D rel2LD;
	private DoubleMatrix2D oLD;
	private DoubleMatrix2D iLD;
	
	private Color sureColor;
	private Color unsureColor;
	private Color longDepColor;

	
	public ExtendedCausalGraph() {
		super();
		this.labelNodeMap = new HashMap<>();
		this.activityFrequencyMap = new HashMap<>();
		this.sureColor = Color.BLUE.darker();
		this.unsureColor = Color.RED.darker();
		this.longDepColor = Color.ORANGE;
		this.mapNodeToIndex = new HashMap<HybridDirectedGraphNode, Integer>();
	}
	
	public Map<String, Integer> getActivityFrequencyMap() {
		return activityFrequencyMap;
	}

	public void setActivityFrequencyMap(Map<String, Integer> activityFrequencyMap) {
		this.activityFrequencyMap = activityFrequencyMap;
	}
	
	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}
	
	public XLog getUnfilteredLog() {
		return this.unfilteredLog;
	}

	public void setUnfilteredLog(XLog ulog) {
		this.unfilteredLog = ulog;
	}
	
	
	public TraceVariantsLog getTraceVariants() {
		return this.traceVariants;
	}
	
	public void setTraceVariants(TraceVariantsLog variants) {
		this.traceVariants = variants;
	}

	public HybridCGMinerSettings getSettings() {
		return settings;
	}

	public void setSettings(HybridCGMinerSettings settings) {
		this.settings = settings;
	}
	
	public Map<String, Integer> getActivitiesMapping() {
		return this.mapActivityToIndex;
	}
	
	public Map<HybridDirectedGraphNode, Integer> getNodesMapping() {
		return this.mapNodeToIndex;
	}
	
	public HashMap<Integer, String> getActivitiesMappingStructures() {
		return this.activitiesMapping;
	}

	public void setActivitiesMapping(HashMap<Integer, String> activityMap, HashMap<String, Integer> reverseMap) {
		this.activitiesMapping = activityMap;
		this.mapActivityToIndex = reverseMap;	
	}

	
	public Double getOutputDirectSuccessionDependency(int i, int j) { 
		return this.outputDirectSuccessionMetric.get(i, j); 
    }


	public Double getInputDirectSuccessionDependency(int i, int j) { 
		return inputDirectSuccessionMetric.get(i, j); 
	}
	  
	 
	/**
	 * We build fuzzyCausalGraph with a strong limitation: we do not ever have two nodes with the same
	 * label.
	 * @param nodeLabel the node to be added
	 * @return a new node with the specified label if there is no other node in the graph with the same label,
	 * otherwise the already existing node.
	 */
	public HybridDirectedGraphNode addNode(String nodeLabel) {
		HybridDirectedGraphNode alreadyPresent = this.labelNodeMap.get(nodeLabel);
		if (alreadyPresent == null) {
			HybridDirectedGraphNode node = new HybridDirectedGraphNode(this, nodeLabel);
			super.addNode(node);
			labelNodeMap.put(nodeLabel, node);
			this.mapNodeToIndex.put(node, this.mapActivityToIndex.get(nodeLabel));
			return node;
		}
		else
			return alreadyPresent;
	}
	
	// Update by sabi (changed return type from void to HybridDirectedSureGraphEdge)
	public HybridDirectedSureGraphEdge addSureEdge(HybridDirectedGraphNode sourceNode, HybridDirectedGraphNode targetNode){
		HybridDirectedSureGraphEdge sureEdge = new HybridDirectedSureGraphEdge(sourceNode, targetNode);
//		HybridDirectedSureGraphEdge sureEdge=new HybridDirectedSureGraphEdge(sourceNode, targetNode);
//		sureEdge.setLabel(value1);
//		System.out.println("Sure edge: "+sureEdge);
		super.addEdge(sureEdge);
		try
		{
			this.wait();
		}
		catch(Exception e){}
		return sureEdge;
	}
	
	
	public HybridDirectedLongDepGraphEdge addLongDepEdge(HybridDirectedGraphNode sourceNode, HybridDirectedGraphNode targetNode){
		super.removeFromEdges(sourceNode, targetNode, super.getEdges());
		HybridDirectedLongDepGraphEdge edge = new HybridDirectedLongDepGraphEdge(sourceNode, targetNode);
		super.addEdge(edge);
		try
		{
			this.wait();
		}
		catch(Exception e){}
		return edge;
	}
	
	
	public HybridDirectedUncertainGraphEdge addUncertainEdge(HybridDirectedGraphNode sourceNode, HybridDirectedGraphNode targetNode){
		HybridDirectedUncertainGraphEdge unsureEdge = new HybridDirectedUncertainGraphEdge(sourceNode, targetNode);
//		HybridDirectedUncertainGraphEdge unsureEdge = new HybridDirectedUncertainGraphEdge(sourceNode, targetNode);
//		unsureEdge.setLabel(value1);
		super.addEdge(unsureEdge);
		try
		{
			this.wait();
		}
		catch(Exception e){}
		return unsureEdge;
	}


	public HybridDirectedGraphNode getNode(String label){
		return this.labelNodeMap.get(label);
	}

	public LinkedHashSet<HybridDirectedGraphEdge> getLongDepGraphEdges(){
		LinkedHashSet<HybridDirectedGraphEdge> longDepEdges = new LinkedHashSet<HybridDirectedGraphEdge>();
		for (HybridDirectedGraphEdge edge : getEdges()) {
	    	if (edge instanceof HybridDirectedLongDepGraphEdge)
	    		longDepEdges.add(edge);
		}
		return longDepEdges;
	}
	
	public LinkedHashSet<HybridDirectedGraphEdge> getSureGraphEdges(){
		LinkedHashSet<HybridDirectedGraphEdge> sureEdges = new LinkedHashSet<HybridDirectedGraphEdge>();
		for (HybridDirectedGraphEdge edge : getEdges()) {
	    	if (edge instanceof HybridDirectedSureGraphEdge)
	    		sureEdges.add(edge);
		}
		return sureEdges;
	}
	
	public LinkedHashSet<HybridDirectedUncertainGraphEdge> getUncertainGraphEdges(){
		LinkedHashSet<HybridDirectedUncertainGraphEdge> uncertainEdges = new LinkedHashSet<HybridDirectedUncertainGraphEdge>();
		for (HybridDirectedGraphEdge edge : getEdges()) {
	    	if (edge instanceof HybridDirectedUncertainGraphEdge)
	    		uncertainEdges.add((HybridDirectedUncertainGraphEdge)edge);
		}
		return uncertainEdges;
	}

	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		ExtendedCausalGraph newFCG = new ExtendedCausalGraph();
		for (HybridDirectedGraphNode node : this.getNodes()) {
			newFCG.addNode(node);
		}
	 	for (HybridDirectedGraphEdge edge : this.getEdges()) {
			newFCG.addEdge(edge);
		}
		return newFCG;
	}

	
	/**
	 * It empties the graph associated to this fCG
	 */
	public void emptyGraph() {
		ExtendedCausalGraph clonedGraph;
		try {
			clonedGraph = (ExtendedCausalGraph) this.clone();
			for (HybridDirectedGraphNode node : clonedGraph.getNodes()) {
				this.removeNode(node);
			}
			this.labelNodeMap = new HashMap<>();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public String toString() {
		String graphString = "*** GRAPH *** "+this.getLabel()+"\n";
        Set<HybridDirectedGraphNode> nodes  = this.getNodes();
        graphString+= "** NODES **\n";
        for (HybridDirectedGraphNode node : nodes) {
        	graphString+= node.getId()+" "+node.getLabel()+"\n";
		}
        graphString+= "** EDGES **\n";
        Set<HybridDirectedGraphEdge> edges = this.getEdges();
        for (HybridDirectedGraphEdge edge : edges) {
        	if (edge instanceof HybridDirectedSureGraphEdge)
        		graphString+= "SURE EDGE "+edge.getSource().getLabel()+" "+edge.getTarget().getLabel()+" "+edge.getLabel()+"\n";
        	else
        		graphString+= "UNSURE EDGE "+edge.getSource().getLabel()+" "+edge.getTarget().getLabel()+" "+edge.getLabel()+"\n";
			
		}
        return graphString;
	}

	public void setMetrics(int eventsNumber, DoubleMatrix2D directSucc, DoubleMatrix2D rel1, DoubleMatrix2D rel2, DoubleMatrix2D outputDirectSuccessionMetric, DoubleMatrix2D inputDirectSuccessionMetric, DoubleMatrix2D eF, DoubleMatrix2D oLD, DoubleMatrix2D iLD, DoubleMatrix2D rel1LD, DoubleMatrix2D rel2LD) {
		this.eventsNumber = eventsNumber;
		this.directSuccessionCount = directSucc;
		this.rel1Metric = rel1;	
		this.rel2Metric = rel2;
		this.outputDirectSuccessionMetric = outputDirectSuccessionMetric;
		this.inputDirectSuccessionMetric = inputDirectSuccessionMetric;
		this.EF = eF;
		this.rel1LD = rel1LD;
		this.rel2LD = rel2LD;
		this.oLD = oLD;
		this.iLD = iLD;
	
	}
	
	public int getEventsNumber() {
		return this.eventsNumber;
	}
	
	public double getDirectSuccessionCount(int i, int j) { 
		return this.directSuccessionCount.get(i, j); 
	}

	public double getRel1(int i, int j) {
		return this.rel1Metric.get(i,j);
	}

	public double getRel2(int i, int j) {
		return this.rel2Metric.get(i,j);
	}
	
	public DoubleMatrix2D getDirectSuccessionCount() { 
		return this.directSuccessionCount;
	}

	public DoubleMatrix2D getRel1() {
		return this.rel1Metric;
	}

	public DoubleMatrix2D getRel2() {
		return this.rel2Metric;
	}

	public Color getSureColor() {
		return sureColor;
	}

	public Color getUnsureColor() {
		return unsureColor;
	}

	public void updateSureColor(Color c) {
		this.sureColor = c;
	}

	public void updateUnsureColor(Color c) {
		this.unsureColor = c;
	}
	
	public DoubleMatrix2D getEF() {
		return this.EF;
	};
	public DoubleMatrix2D getILD() {
		return this.iLD;
	};
	public DoubleMatrix2D getOLD() {
		return this.oLD;
	};
	public DoubleMatrix2D getrel1LD() {
		return this.rel1LD;
	};
	public DoubleMatrix2D getrel2LD() {
		return this.rel2LD;
	}

	public Color getLongDepColor() {
		return this.longDepColor;
	}

	public void updateLongDepColor(Color c) {
		this.longDepColor = c;
	};

	

}
