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

/**
 * Created by demas on 25/07/16.
 * Modified by Humam on 01/05/21
 */

public class HybridCGMiner {

	// this value is used when comparing LD scores during the filtering step (instead of using 0).
	private static final double LDDIFFERENCETHRESHOLD = 0.01;
	
	protected double longDepThreshold; 
	
	protected XLog log;
    //protected XLogInfo logInfo;
    ExtendedCausalGraph fCG;
    protected TraceVariantsLog traceVariants;
    protected HashMap<String, Integer> keys;
    //protected ActivitiesMappingStructures activitiesMappingStructures;
    protected HashMap<Integer, String> activityMap;
    protected HybridCGMinerSettings settings;
    protected int eventsNumber;
    protected DoubleMatrix2D directSuccessionCount;
    protected DoubleMatrix2D eventuallyFollows;
    //protected DoubleMatrix2D eventuallyPreceeds;
    protected DoubleMatrix2D splitJoinCausality;
    protected DoubleMatrix2D rel2Metric;
    //protected DoubleMatrix2D rel3Metric;
    protected DoubleMatrix2D causality;
    protected DoubleMatrix2D rel1LD;
    protected DoubleMatrix2D rel2LD;
    protected DoubleMatrix2D LD;
    //protected DoubleMatrix2D causalityODS;
    //protected DoubleMatrix2D causalityIDS;
    protected DoubleMatrix2D outgoingLongtermDependency;
    protected DoubleMatrix2D incomingLongtermDependency;
    //protected DoubleMatrix2D comRel1; 
    //protected DoubleMatrix2D comODS; 
    //protected DoubleMatrix2D comIDS; 
    protected DoubleMatrix2D outgoingDirectSuccessionDependency;
    protected DoubleMatrix2D incomingDirectSuccessionDependency;
    protected List<Integer> activityTraceCount;
    protected List<Double> rowSumDirectDependency;
    protected List<Double> columnSumDirectDependency;
    //protected List<Double> rowSumDirectDependencyEF;
    //protected List<Double> columnSumDirectDependencyEF;
	//private Map<Integer, Set<Integer>> incoming;
    //private Map<Integer, Set<Integer>> outgoing;
	//private BooleanMatrix2D activeComputationODS;
	//private BooleanMatrix2D activeComputationIDS;
	
    
    /*public HybridCGMiner(XLog log) {
        this(log, XLogInfoFactory.createLogInfo(log));
    }

    public HybridCGMiner(XLog log, XLogInfo info) {
        this(log, info, new HybridCGMinerSettings(log));
    }

	public HybridCGMiner(XLog log, HybridCGMinerSettings settings) {
        this(log, XLogInfoFactory.createLogInfo(log, settings.getClassifier()), settings);
    }*/
    
    public HybridCGMiner(XLog log, XLogInfo infoo, TraceVariantsLog variants, HybridCGMinerSettings settings) {
        this.log = log;
        //this. = info;
        //this.logInfo.getEventClasses(settings.getClassifier()).register("start");
        //this.logInfo.getEventClasses(settings.getClassifier()).register("end");
        this.traceVariants = variants;
        this.longDepThreshold = settings.getLongDepThreshold();
        this.settings = settings;

        		//logInfo.getEventClasses().size();
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
        	//if (settings.getActivityFrequencyMap().get(activity) > 0) {
            this.activityMap.put(n, activity);
        	n++;
        	//}	
        }
        this.eventsNumber = n;
        
        this.activityTraceCount = new ArrayList<Integer>();
        this.directSuccessionCount = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.eventuallyFollows = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        //this.eventuallyPreceeds = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.splitJoinCausality = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.rel2Metric = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        //this.rel3Metric = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.causality = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.rel1LD = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.rel2LD = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.LD = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        //this.causalityODS = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        //this.causalityIDS = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.outgoingLongtermDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.incomingLongtermDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        //this.comRel1 = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, -1);
        //this.comODS = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, -1);
       // this.comIDS = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, -1);
        this.outgoingDirectSuccessionDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        this.incomingDirectSuccessionDependency = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);  
		//this.incoming = new HashMap<Integer, Set<Integer>>();
		//this.outgoing = new HashMap<Integer, Set<Integer>>();
		//this.activeComputationODS = BooleanMatrix2D.factory.zeros(eventsNumber, eventsNumber);
		//this.activeComputationIDS = BooleanMatrix2D.factory.zeros(eventsNumber, eventsNumber);

        
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
			/*double res2 = this.splitJoinCausality.get(i, i);
			if (res2 > this.LOOPTHRESHOLD) {
				String label = activitiesMappingStructures.getActivitiesMapping()[i].getId();
				System.out.println("Self-loop detected: " + label + ", score: " + res2);
				
				//if (this.fCG.getActivityFrequencyMap().get(label) == this.log.size()) {
					//fCG.addSelfLoop(label);
				//} else {
					fCG.addOptionalSelfLoop(label);
				//}
			}*/
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
				//double c = this.settings.getDependencyParamter();
				res = diff / (count_i_j + count_j_i);
			}
		}
		return res;
	}

	
	/*private void computeSumDirectDependencyEF(){
		this.rowSumDirectDependencyEF = new ArrayList<Double>();
		this.columnSumDirectDependencyEF = new ArrayList<Double>();
		for (int i = 0; i < this.eventsNumber; i++) {
    		this.rowSumDirectDependencyEF.add(i, 0.0);
    		this.columnSumDirectDependencyEF.add(i, 0.0);
        }
	}*/
	
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

	/*private void computeColumnSumDirectDependency(){
		this.columnSumDirectDependency = new ArrayList<Double>();
		//this.columnSumDirectDependencyEF = new ArrayList<Double>();
        for (int j = 0; j < this.eventsNumber; j++) {
    		double sumDirectDependency = 0.0;
    		//double sumDirectDependencyEF = 0.0;

            for (int i = 0; i < this.eventsNumber; i++) {
            	/*if (i != j) {
            		sumDirectDependencyEF += this.directSuccessionCount.get(i, j);
            	}
            	sumDirectDependency += this.directSuccessionCount.get(i, j);
	        }
            this.columnSumDirectDependency.add(j, sumDirectDependency);
            //this.columnSumDirectDependencyEF.add(j, sumDirectDependencyEF);
        }
	}*/
	
	
	/*private void computeRowSumDirectDependencyEF(){
		this.rowSumDirectDependencyEF = new ArrayList<Double>();
        for (int i = 0; i < this.eventsNumber; i++) {
    		double sumDirectDependency = 0.0;

            for (int j = 0; j < this.eventsNumber; j++) {
            	sumDirectDependency += this.eventuallyFollows.get(i, j);
	        }
            
            this.rowSumDirectDependencyEF.add(i, sumDirectDependency);
        }
	}*/

	/*private void computeColumnSumDirectDependencyEF(){
		this.columnSumDirectDependencyEF = new ArrayList<Double>();
        for (int j = 0; j < this.eventsNumber; j++) {
    		double sumDirectDependency = 0.0;
            for (int i = 0; i < this.eventsNumber; i++) {
            	sumDirectDependency += this.eventuallyFollows.get(i, j);
	        }
            this.columnSumDirectDependencyEF.add(j, sumDirectDependency);
        }
	}*/
	
	
	/*private void calculateDirectSuccessionCountMatrix() {
		ArrayList<TraceVariant> variants = this.traceVariants.getVariants();
		for (int i = 0; i < this.traceVariants.getSize(); i++) {
			TraceVariant v = variants.get(i);
            Integer lastEventIndex = new Integer(-1);
            
            for (String event : v.getActivities()) {
                Integer eventIndex = keys.get(event);
    			
    			if (lastEventIndex != -1) {
	                incrementDirectSuccessionCount(lastEventIndex, eventIndex, v.getFrequency());
	            }
    			
	            lastEventIndex = eventIndex;
            }
        }
		calculateEventuallyFollowsMatrix();
    }*/
	
	
	/*private void calculateEventuallyFollowsMatrix_DD() {
		ArrayList<TraceVariant> variants = this.traceVariants.getVariants();
		//double counter = 0;
		for (int i = 0; i < this.traceVariants.getSize(); i++) {
			TraceVariant v = variants.get(i);
			for (int index1 = 0; index1 < v.getActivities().size(); index1++) {  //String event : v.getActivities()
            	String a1 =  v.getActivities().get(index1);
            	int i1 = keys.get(a1);
            	for (int index2 = index1 + 1; index2 < v.getActivities().size(); index2++) {
            		String a2 =  v.getActivities().get(index2);
                	int i2 = keys.get(a2);
                	this.eventuallyFollows.set(i1, i2, (this.eventuallyFollows.get(i1, i2) + v.getFrequency() ));
            	}
            }	
        }
    }*/
	
	
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
	
	
	/*private void calculateEventuallyFollowsMetrics() {
		ArrayList<TraceVariant> variants = this.traceVariants.getVariants();
		for (int i = 0; i < this.traceVariants.getSize(); i++) {
			TraceVariant v = variants.get(i);
			for (int index1 = 0; index1 < v.getActivities().size(); index1++) {  //String event : v.getActivities()
            	String a1 =  v.getActivities().get(index1);
            	int i1 = keys.get(a1);
            	Set<Integer> eventuallyFollowsI1 = new HashSet<Integer>();
            	for (int index2 = index1 + 1; index2 < v.getActivities().size(); index2++) {
            		String a2 =  v.getActivities().get(index2);
                	int i2 = keys.get(a2);
                	eventuallyFollowsI1.add(i2);
                }
            	for (int i2: eventuallyFollowsI1) {
            			this.eventuallyFollows.set(i1, i2, (this.eventuallyFollows.get(i1, i2) + v.getFrequency() ));
            	}	
            }	
        }
		
		for (int i = 0; i < this.traceVariants.getSize(); i++) {
			TraceVariant v = variants.get(i);
			for (int index1 = v.getActivities().size() -1; index1 >= 0; index1--) {  //String event : v.getActivities()
            	String a1 =  v.getActivities().get(index1);
            	int y = keys.get(a1);
            	Set<Integer> eventuallyPreceedsY = new HashSet<Integer>();
            	for (int index2 = index1 + -1; index2 >=0; index2--) {
            		String a2 =  v.getActivities().get(index2);
                	int x = keys.get(a2);
                	eventuallyPreceedsY.add(x);
                }
            	for (int x: eventuallyPreceedsY) {
            			this.eventuallyPreceeds.set(x, y, (this.eventuallyPreceeds.get(x, y) + v.getFrequency() ));
            	}	
            }	
        }
		
    }*/
	
	/*private void calculateEventuallyFollowsMatrixDD() {
		ArrayList<TraceVariant> variants = this.traceVariants.getVariants();
		//HashSet<Integer> seenBefore = new HashSet<Integer>();
		for (int i = 0; i < this.traceVariants.getSize(); i++) {
			TraceVariant v = variants.get(i);
			//boolean addEF = true;
			for (int index1 = 0; index1 < v.getActivities().size(); index1++) {  //String event : v.getActivities()
            	String a1 =  v.getActivities().get(index1);
            	int i1 = keys.get(a1);
            	/*if(seenBefore.contains(i1)) {
            		addEF = true;
            	}
            	//seenBefore.add(i1);
            	Set<Integer> eventuallyFollowsI1 = new HashSet<Integer>();
            	for (int index2 = index1 + 1; index2 < v.getActivities().size(); index2++) {
            		String a2 =  v.getActivities().get(index2);
                	int i2 = keys.get(a2);
                	if (i1 == i2) {
                		break;
                	}
                	eventuallyFollowsI1.add(i2);
                	
                	//this.eventuallyFollows.set(i1, i2, (this.eventuallyFollows.get(i1, i2) + v.getFrequency() ));
            	}
            	//this.rowSumDirectDependencyEF.set(i1, this.rowSumDirectDependencyEF.get(i1) + v.getFrequency());	
            	/*if (addEF) {
            		for (int i2: eventuallyFollowsI1) {
            			this.eventuallyFollows.set(i1, i2, (this.eventuallyFollows.get(i1, i2) + v.getFrequency() ));
            			this.columnSumDirectDependencyEF.set(i2, this.columnSumDirectDependencyEF.get(i2) + v.getFrequency());
            		}
            		addEF = false;
            	} else {
            		for (int i2: eventuallyFollowsI1) {
            			this.eventuallyFollows.set(i1, i2, (this.eventuallyFollows.get(i1, i2) + v.getFrequency() ));
            		}
            	//}
            		
            }	
        }
    }*/
	
	
	private void incrementDirectSuccessionCount(int x, int y, double value){
		this.directSuccessionCount.set(x, y, (this.directSuccessionCount.get(x, y) + value));
	}

	public void updateCG(ExtendedCausalGraph fCG) {
		fCG.setLog(log);
		fCG.setTraceVariants(traceVariants);
		
		//activitiesMappingStructures = new ActivitiesMappingStructures(logInfo.getEventClasses(settings.getClassifier()));
		//activitiesMappingStructures = new ActivitiesMappingStructures();
		//activitiesMappingStructures
		setKeys();
		fCG.setActivityFrequencyMap(settings.getActivityFrequencyMap());
		fCG.setActivitiesMapping(this.activityMap, this.keys);
		fCG.setSettings(settings);
		this.calculateSuccessionMatrices();
		this.computeSumDirectDependencyMetrics();
		//this.computeColumnSumDirectDependency();
		
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

				//double sumODS = this.activityTraceCount.get(i);
						//this.rowSumDirectDependency.get(i);// - this.directSuccessionCount.get(i, i);
						//
						//this.rowSumDirectDependency.get(i);
						//this.rowSumDirectDependencyEF.get(i);
				/*if (sumODS == 0) {
					this.outgoingLongtermDependency.set(i, j, 0);
				} else {
					this.outgoingLongtermDependency.set(i, j, eF/sumODS);
				}*/
				//System.out.println("OLD from " + nodeILabel + " to " + nodeJLabel + ": " + outgoingLongtermDependency.get(i, j));
				
				//eF = this.eventuallyPreceeds.get(i, j);
				//double sumIDS = this.activityTraceCount.get(j);//this.columnSumDirectDependency.get(j);// - this.directSuccessionCount.get(j, j);
						//this.activityCount.get(j);
						//this.columnSumDirectDependency.get(j);
						//this.columnSumDirectDependencyEF.get(j);
				/*if (sumIDS == 0) {
					this.incomingLongtermDependency.set(i, j, 0);
				} else {
					this.incomingLongtermDependency.set(i, j, eF/sumIDS);
				}*/
				//System.out.println("ILD from " + nodeILabel + " to " + nodeJLabel + ": " + incomingLongtermDependency.get(i, j));
				
				//double rel_3_2 = this.calculateRel3_2(i, j);
				//double sum = sumIDS + sumODS;
				/*if (sum == 0) {
					this.rel3Metric.set(i, j, rel_3_2/2);
				} else {
					this.rel3Metric.set(i, j, (((2*eF)/sum)+rel_3_2)/2);
				}*/
				/*if (sum == 0) {
					this.rel3Metric.set(i, j, 0);
				} else {
					this.rel3Metric.set(i, j, (((2*eF)/sum)));
				}*/

				//double causalityMetric = (0.33*rel_3) + (0.33*rel_2) + (0.33*rel_3);
				//if (causalityMetric < settings.getSureThreshold()) {
				//if (true) {
					
					/*
					rel_2 = 0;
					if (i == j) {
						double count_i_j = this.directSuccessionCount.get(i, j);
						double c = this.settings.getDependencyParamter();
						rel_2 = count_i_j / (count_i_j + c);
					} else {
						double count_i_j = this.directSuccessionCount.get(i, j);
						double count_j_i = this.directSuccessionCount.get(j, i);
						double diff = count_i_j - count_j_i;
						if (diff > 0) {
							double c = this.settings.getDependencyParamter();
							rel_2 = diff / (count_i_j + count_j_i + c);
						}
					}
					
					
					//double d  = Math.min(settings.getActivityFrequencyMap().get(nodeILabel), settings.getActivityFrequencyMap().get(nodeJLabel));
					
					//double rel_3_1 = this.calculateEventuallyFollowsMatrix(i, j) / d;	
					//rel_3 = rel_3 * rel_3;
				    double rel_3_2 = 1 - ((d - 2*eF)/d);	
				    double rel_3 = rel_3_1 * rel_3_2 * (1 - (Math.max(settings.getActivityFrequencyMap().get(nodeILabel), settings.getActivityFrequencyMap().get(nodeJLabel)) / log.size()));
					//double rel_3 = rel_3_1*(1 - d/(log.size()));
					causalityMetric = Math.max(0, rel_3); */
				//}
				
				this.outgoingDirectSuccessionDependency.set(i, j, outputDirectSuccessionDependency);
				this.incomingDirectSuccessionDependency.set(i, j, inputDirectSuccessionDependency);
				// R_S = { edge (i,j) | Caus_c,w(i,j,L) >= t_R_S } 
				if (causalityMetric>=settings.getSureThreshold()){
					// Update by sabi (capture returned edge and set the values)
					HybridDirectedSureGraphEdge sureEdge = fCG.addSureEdge(nodeI, nodeJ);
					sureEdge.setAbDependencyMetric(rel_2);
					sureEdge.setCausalityMetric(causalityMetric);
					sureEdge.setDirectSuccession(directSuccession);
					sureEdge.setDirectSuccessionDependency(rel_1);
					sureEdge.setIDSD_ODSD(outputDirectSuccessionDependency, inputDirectSuccessionDependency);

//					fCG.addSureEdge(nodeI, nodeJ, new Double(roundedODSD), new Double(roundedIDSD));
					//System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+rel_1+" "+rel_2);
				// R_S = { edge (i,j) | t_R_S > Caus_c,w(i,j,L) >= t_R_W }
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
	
	
	/*private double computeTransitivDirectSuccOLD(int i, int j) {
		double v1 = computeTransitivDirectSuccODS(i, j);
		double v2 = computeTransitivDirectSuccIDS(i, j);
		//System.out.println("v1: " + v1 + ",   v2: " + v2);
		return ((v1 + v2) / 2);
	}*/

	
	/*private double computeTransitivDirectSuccODS(int i, int j) {
		if (this.activeComputationODS.getBoolean(i, j) == true) {
			// loop detected
			//this.comODS.set(i, j, 1);
	    	this.activeComputationODS.setBoolean(false, i, j);
	    	return -5;
		} 
		
		this.activeComputationODS.setBoolean(true, i, j);
		
		if (this.comODS.get(i, j) != -1) {
			this.activeComputationODS.setBoolean(false, i, j);
			return this.comODS.get(i, j);
		}
		
		/*if (this.eventuallyFollows.get(i, j) > 0 && this.eventuallyFollows.get(j, i) > 0) {
	    	this.comODS.set(i, j, 1);
	    	this.comODS.set(j, i, 1);
	    	this.activeComputationODS.setBoolean(false, i, j);
	    	return 1;
		}
		
		if (i == j) {
			this.comODS.set(i, j, 1);
			this.activeComputationODS.setBoolean(false, i, j);
			return 1;
		} 
		
		double res = 0;
		double sum = 0;
		//String nodeJLabel = activitiesMappingStructures.getActivitiesMapping()[j].getId();
		//for (HybridDirectedGraphEdge edge: fCG.getInEdges(fCG.getNode(nodeJLabel))) {
		//String nodeKLabel = edge.getSource().getLabel();
		//int k = this.keys.get(nodeKLabel);
		//System.out.println(nodeKLabel + ",   " + nodeJLabel );
		if (!this.outgoing.containsKey(i)) { //e.g., j = start
			this.comODS.set(i, j, 0);
			this.activeComputationODS.setBoolean(false, i, j);
			return 0;
		}
		for (int k: this.outgoing.get(i)) {
			if (k == i) {
				continue;
			}
			double oDS = this.outputDirectSuccessionMetric.get(i, k);
			if (oDS == 0) {
				continue;
			}
			if (computeTransitivDirectSuccODS(k, j) == -5) {
				continue;
			}
			double maxValue = Math.max(this.rel3MetricODS.get(k, j), computeTransitivDirectSuccODS(k, j));
			res = res + (maxValue * oDS);
			sum = sum + oDS;
		}
		if (sum != 0) {
			res = res/sum;
		}
		this.comODS.set(i, j, res);
		this.activeComputationODS.setBoolean(false, i, j);
		return res;
	}*/
	
	
	/*private double computeTransitivDirectSuccIDS(int i, int j) {
		if (this.activeComputationIDS.getBoolean(i, j) == true) {
			// loop detected
			//this.comIDS.set(i, j, 1);
	    	this.activeComputationIDS.setBoolean(false, i, j);
	    	return -5;
		} 
		
		this.activeComputationIDS.setBoolean(true, i, j);
		
		if (this.comIDS.get(i, j) != -1) {
			this.activeComputationIDS.setBoolean(false, i, j);
			return this.comIDS.get(i, j);
		}
		
		/*if (this.eventuallyFollows.get(i, j) > 0 && this.eventuallyFollows.get(j, i) > 0) {
	    	this.comIDS.set(i, j, 1);
	    	this.comIDS.set(j, i, 1);
	    	this.activeComputationIDS.setBoolean(false, i, j);
	    	return 1;
		}
		
		//System.out.println(activitiesMappingStructures.getActivitiesMapping()[i].getId() + ",  " + activitiesMappingStructures.getActivitiesMapping()[j].getId());
		if (i == j) {
			this.comIDS.set(i, j, 1);
			this.activeComputationIDS.setBoolean(false, i, j);
			return 1;
		} 
		
		double res = 0;
		double sum = 0;
		//String nodeJLabel = activitiesMappingStructures.getActivitiesMapping()[j].getId();
		//for (HybridDirectedGraphEdge edge: fCG.getInEdges(fCG.getNode(nodeJLabel))) {
		//String nodeKLabel = edge.getSource().getLabel();
		//int k = this.keys.get(nodeKLabel);
		//System.out.println(nodeKLabel + ",   " + nodeJLabel );
		if (!this.incoming.containsKey(j)) { //e.g., j = start
			this.comIDS.set(i, j, 0);
			this.activeComputationIDS.setBoolean(false, i, j);
			return 0;
		}
		for (int k: this.incoming.get(j)) {
			if (k == j) {
				continue;
			}
			double iDS = this.inputDirectSuccessionMetric.get(k,j);
			if (iDS == 0) {
				continue;
			}
			if (computeTransitivDirectSuccIDS(i, k) == -5) {
				continue;
			}
			double maxValue = Math.max(this.rel3MetricIDS.get(i, k), computeTransitivDirectSuccIDS(i, k));
			res = res + (maxValue * iDS);
			sum = sum + iDS;
		}
		if (sum != 0) {
			res = res/sum;
		}
		
		this.comIDS.set(i, j, res);
		this.activeComputationIDS.setBoolean(false, i, j);
		return res;
	}*/
	
}
