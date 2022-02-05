package org.processmining.extendedhybridminer.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariant;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedGraphNode;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedLongDepGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedUncertainGraphEdge;
import org.processmining.extendedhybridminer.plugins.HybridCGMinerSettings;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;


public class HybridCGMiner {

	private static final double LDDIFFERENCETHRESHOLD = 0.01;
	protected double longDepThreshold; 
	protected XLog log;
    ExtendedCausalGraph fCG;
    protected TraceVariantsLog traceVariants;
    protected HashMap<String, Integer> keys;
    protected HashMap<Integer, String> activityMap;
    protected HybridCGMinerSettings settings;
    protected int eventsNumber;
    protected DoubleMatrix2D directSuccessionCount;
    protected DoubleMatrix2D eventuallyFollows;
    protected DoubleMatrix2D splitJoinCausality;
    protected DoubleMatrix2D rel2Metric;
    protected DoubleMatrix2D causality;
    protected DoubleMatrix2D rel1LD;
    protected DoubleMatrix2D rel2LD;
    protected DoubleMatrix2D LD;
    protected DoubleMatrix2D outgoingLongtermDependency;
    protected DoubleMatrix2D incomingLongtermDependency;
    protected DoubleMatrix2D outgoingDirectSuccessionDependency;
    protected DoubleMatrix2D incomingDirectSuccessionDependency;
    protected List<Integer> activityTraceCount;
    protected List<Double> rowSumDirectDependency;
    protected List<Double> columnSumDirectDependency;
    
    public HybridCGMiner(XLog log, XLogInfo infoo, TraceVariantsLog variants, HybridCGMinerSettings settings) {
        this.log = log;
        this.traceVariants = variants;
        this.longDepThreshold = settings.getLongDepThreshold();
        this.settings = settings;
    }

	public ExtendedCausalGraph mineFCG(){
		fCG = new ExtendedCausalGraph();
		this.updateCG(fCG);
		return fCG;
	}
	
	private void setKeys() {
		this.activityMap = new HashMap<Integer, String>();
        int n = 0;
        for (String activity: settings.getActivityFrequencyMap().keySet()) {
        	this.activityMap.put(n, activity);
        	n++;
        }
        this.eventsNumber = n;
        this.activityTraceCount = new ArrayList<Integer>();
        this.directSuccessionCount = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.eventuallyFollows = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.splitJoinCausality = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.rel2Metric = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.causality = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.rel1LD = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.rel2LD = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.LD = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.outgoingLongtermDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.incomingLongtermDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.outgoingDirectSuccessionDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.incomingDirectSuccessionDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);  
		this.keys = new HashMap<String, Integer>();
		HashMap<Integer, String> map = this.activityMap;
		for(int i=0; i< map.size(); i++) {
			this.activityTraceCount.add(0);
			String label = map.get(i);
		    this.keys.put(label, i);
	    }	
	}

	private double calculateRel2(int i, int j) {
		double res = 0;
		if (i == j) {
			double count_i_j = this.directSuccessionCount.get(i, j);
			double c = this.settings.getDependencyParamter();
			res = count_i_j / (count_i_j + c);
		} else {
			double count_i_j = this.directSuccessionCount.get(i, j);
			double count_j_i = this.directSuccessionCount.get(j, i);
			double diff = count_i_j - count_j_i;
			if (diff > 0) {
				double c = this.settings.getDependencyParamter();
				res = diff / (count_i_j + count_j_i + c);
			}
		}
		return res;
	}
	
	
	private double calculateRel2EF(int i, int j) {
		double res = 0;
		if (i == j) {
			return 0;
		} else {
			double count_i_j = this.eventuallyFollows.get(i, j);
			double count_j_i = this.eventuallyFollows.get(j, i);
			double diff = count_i_j - count_j_i;
			if (diff > 0) {
				res = diff / (count_i_j + count_j_i);
			}
		}
		return res;
	}
	
	private void computeSumDirectDependencyMetrics(){
		this.rowSumDirectDependency = new ArrayList<Double>();
		this.columnSumDirectDependency = new ArrayList<Double>();	
		for (int i = 0; i < this.eventsNumber; i++) {
    		double rowSumDirectDependency = 0.0;
    		double columnSumDirectDependency = 0.0;

    		for (int j = 0; j < this.eventsNumber; j++) {
            	rowSumDirectDependency += this.directSuccessionCount.get(i, j);
            	columnSumDirectDependency += this.directSuccessionCount.get(j, i);
	        }
            this.rowSumDirectDependency.add(i, rowSumDirectDependency);
            this.columnSumDirectDependency.add(i, columnSumDirectDependency);
        }
	}

	private void calculateSuccessionMatrices() {
		ArrayList<TraceVariant> variants = this.traceVariants.getVariants();    
		for (int i = 0; i < this.traceVariants.getSize(); i++) {
			TraceVariant v = variants.get(i);
			HashSet<Integer> seenBefore = new HashSet<Integer>();
			Integer lastEventIndex = new Integer(-1);
            for (int index1 = 0; index1 < v.getActivities().size(); index1++) {  //String event : v.getActivities()
            	String a1 =  v.getActivities().get(index1);
            	int i1 = keys.get(a1);
            	if (lastEventIndex != -1) {
	                incrementDirectSuccessionCount(lastEventIndex, i1, v.getFrequency());
	            }
    			lastEventIndex = i1;
            	if (seenBefore.contains(i1)) {
    				continue;
    			} else {
    				seenBefore.add(i1);
    				this.activityTraceCount.set(i1, this.activityTraceCount.get(i1) + v.getFrequency());
    			}
            	Set<Integer> eventuallyFollowsI1 = new HashSet<Integer>();
            	for (int index2 = index1; index2 < v.getActivities().size(); index2++) {
            		String a2 =  v.getActivities().get(index2);
                	int i2 = keys.get(a2);
                	eventuallyFollowsI1.add(i2);
                }
            	for (int i2: eventuallyFollowsI1) {
            			this.eventuallyFollows.set(i1, i2, (this.eventuallyFollows.get(i1, i2) + v.getFrequency() ));
            	}	
            }	
        }	
    }
	
	private void incrementDirectSuccessionCount(int x, int y, double value){
		this.directSuccessionCount.set(x, y, (this.directSuccessionCount.get(x, y) + value));
	}

	public void updateCG(ExtendedCausalGraph fCG) {
		fCG.setLog(log);
		fCG.setTraceVariants(traceVariants);
		setKeys();
		fCG.setActivityFrequencyMap(settings.getActivityFrequencyMap());
		fCG.setActivitiesMapping(this.activityMap, this.keys);
		fCG.setSettings(settings);
		this.calculateSuccessionMatrices();
		this.computeSumDirectDependencyMetrics();
		for(int i=this.eventsNumber-1; i>=0; i--) {
			String nodeILabel = this.activityMap.get(i);
			HybridDirectedGraphNode nodeI = fCG.addNode(nodeILabel);
			for (int j=eventsNumber-1; j>=0; j--) {
				String nodeJLabel = this.activityMap.get(j);
				HybridDirectedGraphNode nodeJ = fCG.addNode(nodeJLabel);
				// #(i,j,L)
				double directSuccession = this.directSuccessionCount.get(i, j);	
				// #(i,j,L) / #(i,*,L) 
				double outputDirectSuccessionDependency = (this.rowSumDirectDependency.get(i)>0) ? (directSuccession/this.rowSumDirectDependency.get(i)) :0.0;
				// #(i,j,L) / #(*,j,L)
				double inputDirectSuccessionDependency = (this.columnSumDirectDependency.get(j)>0) ? (directSuccession/this.columnSumDirectDependency.get(j)) :0.0;
				// Rel1(i,j,L)
				double rel_1 = (2*(directSuccession))/(this.columnSumDirectDependency.get(j) + this.rowSumDirectDependency.get(i));	
				this.splitJoinCausality.set(i, j, rel_1);
				// Rel2_c(i,j,L)
				double rel_2 = calculateRel2(i, j);
				this.rel2Metric.set(i, j, rel_2);
				// Caus_c,w(i,j,L)
				double causalityMetric = (settings.getCausalityWeight()*rel_1) + ((1-settings.getCausalityWeight())*rel_2);
				this.causality.set(i, j, causalityMetric);
				double eF = this.eventuallyFollows.get(i, j);
				this.outgoingLongtermDependency.set(i, j, eF/this.activityTraceCount.get(i));
				this.incomingLongtermDependency.set(i, j, eF/this.activityTraceCount.get(j));
				this.outgoingDirectSuccessionDependency.set(i, j, outputDirectSuccessionDependency);
				this.incomingDirectSuccessionDependency.set(i, j, inputDirectSuccessionDependency);
				if (causalityMetric>=settings.getSureThreshold()){
					HybridDirectedSureGraphEdge sureEdge = fCG.addSureEdge(nodeI, nodeJ);
					sureEdge.setAbDependencyMetric(rel_2);
					sureEdge.setCausalityMetric(causalityMetric);
					sureEdge.setDirectSuccession(directSuccession);
					sureEdge.setDirectSuccessionDependency(rel_1);
					sureEdge.setIDSD_ODSD(outputDirectSuccessionDependency, inputDirectSuccessionDependency);
				} else if (causalityMetric>=settings.getQuestionMarkThreshold()){
					HybridDirectedUncertainGraphEdge unsureEdge = fCG.addUncertainEdge(nodeI, nodeJ);
					unsureEdge.setAbDependencyMetric(rel_2);
					unsureEdge.setCausalityMetric(causalityMetric);
					unsureEdge.setDirectSuccession(directSuccession);
					unsureEdge.setDirectSuccessionDependency(rel_1);
					unsureEdge.setIDSD_ODSD(outputDirectSuccessionDependency, inputDirectSuccessionDependency);
				}			
			}
		}
		for(int i=this.eventsNumber-1; i>=0; i--) {
			String nodeILabel = this.activityMap.get(i);
			for (int j=eventsNumber-1; j>=0; j--) {
				String nodeJLabel = this.activityMap.get(j);
				if (checkLongTermDependency(i, j)) {
					HybridDirectedLongDepGraphEdge edge = fCG.addLongDepEdge(fCG.getNode(nodeILabel), fCG.getNode(nodeJLabel));
					edge.setAbDependencyMetric(this.rel2LD.get(i, j));
					edge.setCausalityMetric(this.LD.get(i, j));
					edge.setDirectSuccession(this.eventuallyFollows.get(i, j));
					edge.setDirectSuccessionDependency(this.rel1LD.get(i, j));
					edge.setIDSD_ODSD(this.outgoingLongtermDependency.get(i, j), this.incomingLongtermDependency.get(i, j));
				}
			}
		}
		fCG.setMetrics(eventsNumber, directSuccessionCount, splitJoinCausality, rel2Metric, outgoingDirectSuccessionDependency, incomingDirectSuccessionDependency,
				this.eventuallyFollows, this.outgoingLongtermDependency, this.incomingLongtermDependency, this.rel1LD, this.rel2LD);
	}
	
	/*
	 * This function is used for filtering "interesting" long-term dependency relations.
	 * Returns true if a long-term dependency edge from i to j should be added to the graph.
	 */
	private boolean checkLongTermDependency(int i, int j) {
	   
		// no self-loops
		if (i == j) {
	        return false;
	    }
		
		// no LD if a sure edge can be added
	    if (this.causality.get(i, j) >= this.settings.getSureThreshold()) {
	    	return false;
	    }
	    
	    // Computing tOLD
		double res1 = 0;
		double sum1 = 0;
		for (int k = this.eventsNumber-1; k>=0; k--) {
			if (k == i) {
				continue;
			}
			double oDS = this.outgoingDirectSuccessionDependency.get(i, k);
			if (k == j) {
				res1 = res1 + oDS;
				sum1 = sum1 + oDS;
			} else {
				if (this.outgoingLongtermDependency.get(i, k) * this.outgoingLongtermDependency.get(k, j) >= this.outgoingLongtermDependency.get(i, j)) {
					return false;
				}
				res1 = res1 + (this.outgoingLongtermDependency.get(k, j)*oDS);
				sum1 = sum1 + oDS;
			}	
		}
		if (sum1 != 0) {
			res1 = res1 / sum1;
		}
		if (this.outgoingLongtermDependency.get(i, j) - res1 < this.LDDIFFERENCETHRESHOLD) {
			return false;
		}
		
		// Computing tILD
		double res2 = 0;
		double sum2 = 0;
		for (int k = this.eventsNumber-1; k>=0; k--) {
			if (k == j) {
				continue;
			}
			double iDS = this.incomingDirectSuccessionDependency.get(k, j);
			
			if (k == i) {
				res2 = res2  + iDS;
				sum2 = sum2  + iDS;
			} else {
				if (this.incomingLongtermDependency.get(i, k) * this.incomingLongtermDependency.get(k, j) >= this.incomingLongtermDependency.get(i, j)) {
					return false;
				}
				res2 = res2 +  (this.incomingLongtermDependency.get(i, k)*iDS);
				sum2 = sum2  + iDS;
			}	
		}
		if (sum2 != 0) {
			res2 = res2 / sum2;
		}
		if (this.incomingLongtermDependency.get(i, j) - res2 < this.LDDIFFERENCETHRESHOLD) {
			return false;
		}
	
	   if (computeLDMetrics(i, j)) {
		   System.out.println("Long-term dependency detected from " + this.activityMap.get(i) + "  to  " + this.activityMap.get(j) 
					+ ", LD: " + this.LD.get(i, j));
           return true;
	   } else {
		   return false;
	   }
	}

	/*
	 * This function computes the LD metrics for the relation (i -> j).
	 * Returns true if the LD score >= LD-threshold. 
	 */
	private boolean computeLDMetrics(int i, int j) {
		double rel1 = (this.outgoingLongtermDependency.get(i, j) 
				+ this.incomingLongtermDependency.get(i, j)) / 2;
	    double rel2 = this.calculateRel2EF(i, j);
	    double LD = (this.settings.getCausalityWeight()*rel1) + ((1 - this.settings.getCausalityWeight())*rel2);
	    this.LD.set(i, j, LD);
	    this.rel1LD.set(i, j, rel1);
	    this.rel2LD.set(i, j, rel2);
	    return LD >= this.longDepThreshold;
	}
	
}
