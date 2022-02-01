package org.processmining.extendedhybridminer.models.hybridpetrinet;

import java.util.ArrayList;
import java.util.Map;

import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariant;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 22/08/16.
 */
public class PlaceEvaluation<N extends AbstractDirectedGraphNode> extends PartialPlaceEvaluation<N> {
    private int acceptedTracesNumber;
    private int currentTokenNumber;
    //private XLog log;
    private TraceVariantsLog variants;
    private Map<String, Integer> activityFrequencyMap;
    double prePlaceEvaluationThreshold;
    double placeEvaluationThreshold;
    // A trace in the log is "active" if, during the replay it has activated at least one outputTransition or inputTransition.
    int activeTracesNumber;
	private double logSize;
	//private Set<String> optionalSelfLoops;
    
    
    public PlaceEvaluation(PartialPlaceEvaluation<N> ppe, TraceVariantsLog variants2, double logSize, Map<String, Integer> activityFrequencyMap, double prePlaceEvaluationThreshold, double placeEvaluationThreshold) {
        super(ppe);
        this.variants = variants2;
        this.logSize = logSize;
        this.acceptedTracesNumber = 0;
        this.currentTokenNumber = 0;
        this.activityFrequencyMap = activityFrequencyMap;
        this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
        this.placeEvaluationThreshold = placeEvaluationThreshold;
        this.activeTracesNumber = 0;
        //this.optionalSelfLoops = optionalSelfLoops;
    }
    

	/*@Override
    public String toString() {
        return "PlaceEvaluation{" +
                "placeInputNodes=" + this.getPlaceInputNodes() +
                ", placeOutputNodes=" + this.getPlaceOutputNodes() +
                '}';
    }*/

  

	/*
     * This methods is for optimizing large cluster. It pre-evaluates a specific inputSet and outputSet set and analyzes if
     * it is a good candidate if:
     * 				AbsoluteValue[Sum_i(InputSet(i)) - Sum_j(OutputSet(j))] / Sum_i(InputSet(i)) + Sum_j(OutputSet(j))  <  prePlaceEvaluationThreshold
     */
    public boolean preEvaluate() {
    	double inputSum = 0;
    	double outputSum = 0;
    	for (N node : this.getPlaceInputNodes())
    		inputSum += this.activityFrequencyMap.get(node.getLabel());
    	for (N node : this.getPlaceOutputNodes())
    		outputSum += this.activityFrequencyMap.get(node.getLabel());
    	double divisor = Math.abs(inputSum - outputSum);
    	double dividend = inputSum + outputSum;
    	//System.out.println("Divisor/dividend:" + divisor/dividend);
    	return ((divisor/dividend) < this.prePlaceEvaluationThreshold);
    }
    

    public boolean replayPlaceGlobally() {
    	double numAnalyzedTraces = 0.0;
    	int nTracesToCover = (int) Math.ceil(variants.getNumberOfCoveredTraces() * this.placeEvaluationThreshold);
		for (TraceVariant trace: variants.getVariants()) {
			int frequency =  trace.getFrequency();
			if (this.replayPlaceOnTrace(trace.getActivities())) {
    			this.activeTracesNumber = this.activeTracesNumber + frequency;
    			if (isCurrentTokenNumberZero()) {
        			increaseAcceptedTracesNumber(frequency);
    		    } else if (numAnalyzedTraces < nTracesToCover) {
    		    	resetCurrentTokenNumber();
    		    	return false;
    		    }
    		}
			resetCurrentTokenNumber();
			numAnalyzedTraces = numAnalyzedTraces + frequency;
			if (runtimePlaceEvaluation(numAnalyzedTraces) < this.placeEvaluationThreshold) {
    			return false;
    		}
    	}
		return true;
    }
    
    
    // replay all traces on the place to update activeTracesNumber and acceptedTracesNumber
    public void replayPlace() {
    	double numAnalyzedTraces = new Double(0);
    	// for each trace in the log
    	for (TraceVariant trace: variants.getVariants()) {
    		int frequency =  trace.getFrequency();
    		boolean peActivatedByTrace = this.replayPlaceOnTrace(trace.getActivities());
    		if (peActivatedByTrace) {
    			this.activeTracesNumber = this.activeTracesNumber + frequency;
    			if (isCurrentTokenNumberZero()) {
        			increaseAcceptedTracesNumber(frequency);
    		    }
    		}
    	    resetCurrentTokenNumber();
    		
    		// OPTIMIZATION in appendix of paper v4.
    		numAnalyzedTraces = numAnalyzedTraces + frequency;
    		// "Prematurely end place evaluation" in improve performance section.
    		if (runtimePlaceEvaluation(numAnalyzedTraces) < this.placeEvaluationThreshold) {
    			//System.out.println("Evaluation of a log interrupted for optimization.");
    	    	//System.out.println("accepted trace number: "+getAcceptedTracesNumber()+" active trace number:"+activeTracesNumber);
    			return;
    		}
    		
    		/* Notice that when returns, the evaluateReplayScore() still is less than placeEvaluationThreshold, for the theorem
    		 (a + x) / (b + x) < c ===> a/b < c when a>b, which is always the case.
    		 */
    	}
    	//System.out.println("accepted trace number: "+getAcceptedTracesNumber()+" active trace number:"+activeTracesNumber);
    }
    
    
    
//    void printReplayInfo(XTrace trace, boolean replayInfo){
//    	for (XEvent event : trace) {
//			System.out.print(XConceptExtension.instance().extractName(event)+"-");
//		}
//    	System.out.print("   activated: "+replayInfo+"    tokens: "+currentTokenNumber+"    activeTraceNumber: "+activeTracesNumber);
//    	System.out.println();
//    }
    
    // As in appendix of the paper in v4
    double runtimePlaceEvaluation(double numAnalyzedTraces) {
    	double logLength = logSize;
    	double numRemainingTraces = logLength - numAnalyzedTraces;
    	
    	double numerator = getAcceptedTracesNumber() + numRemainingTraces;
    	double denominator = activeTracesNumber + numRemainingTraces;
    	
    	
    	return numerator/denominator;
    }
    
    
    /*
     * The return value is true if trace "activated" the place evaluation, i.e., the trace had at least one
     * event corresponding to an output transition (so input to the place) or an input transition; otherwise it returns false.
     */
    		
    public boolean replayPlaceOnTrace(ArrayList<String> trace){
    	boolean result = false;
    	//String lastActivity = "";
    	for (String activity: trace) {
            /*if (this.optionalSelfLoops.contains(activity)) {// || activity == lastActivity) {
            	//lastActivity = activity;
            	continue;
            }*/
           // lastActivity = activity;
    		boolean isInput=false, isOutput = false;
    		for (N placeOutputNode : getPlaceOutputNodes()) {
    			if (placeOutputNode.getLabel().equalsIgnoreCase(activity)) {
    				isOutput = true;
    				result = true;
    			}
    		}
    		for (N placeInputNode : getPlaceInputNodes()) {
    			if (placeInputNode.getLabel().equalsIgnoreCase(activity)) {
    				isInput=true;
    				result = true;
    			}
    		}
    		//if it is both input and output: be conservative, i.e., 
    		//if we have at least a token, we decrease the token number and return it
    		// otherwise, we increase the token number
    		//				if (isInput && isOutput){
    		//					if (this.currentTokenNumber>0)
    		//						decreaseTokenNumber();
    		//					else 
    		//						increaseTokenNumber();
    		//				} else{

    		//if it is output increase
    		if (isInput){
    			decreaseTokenNumber();
    			// CHECK: if negative return;
    			if (isCurrentTokenNumberNegative()) {
    				/* Humam
    				 * in this case we break the loop without analyzing the remaining 
    				 * events of the trace
    				 * -> #actived_traces is already increased 
    				 *    #accepted_traces will not be increased
    				 *    since currentTokenNumber != 0
    				 */
    				return result;
    			}
    		}
    		if (isOutput) {
    			increaseTokenNumber();
    			if (isCurrentTokenNumberHigherThanOne()) {
    				return result;
    			}
    		}


		}
		return result;
    }
    
    
    // return true if fitting; false if non-fitting;
   /*public boolean replayPlaceGloballyOnTrace_L(ArrayList<String> trace) {
    	for (String activity: trace) {
    		boolean isInput=false, isOutput = false;
    		for (N placeOutputNode : getPlaceOutputNodes()) {
    			if (placeOutputNode.getLabel().equalsIgnoreCase(activity)) {
    				isOutput = true;
    				activated = true;
    			}
    		}
    		for (N placeInputNode : getPlaceInputNodes()) {
    			if (placeInputNode.getLabel().equalsIgnoreCase(activity)) {
    				isInput=true;
    				activated = true;
    			}
    		}
    		
    		if (isInput) {
    			decreaseTokenNumber();
    			if (isCurrentTokenNumberNegative()) {
    				return false;
    			}
    		}
    		if (isOutput) {
    			increaseTokenNumber();
    			if (isCurrentTokenNumberHigherThanOne()) {
    				return false;
    			}
    		}
		}
    	//System.out.println(toString());
		return this.isCurrentTokenNumberZero();
    }*/
    

    public void increaseAcceptedTracesNumber(int frequency) {
        acceptedTracesNumber = this.acceptedTracesNumber + frequency;
    }

    public int getAcceptedTracesNumber() {
        return acceptedTracesNumber;
    }

    /*
     * Now changed: the score is not anymore: #acceptedTraces/#allTracesInTheLog but
     * it is #acceptedTraces/#activeTraces
     */
    // score_rel
    public double evaluateReplayScore() {
    	if (this.activeTracesNumber==0)
    		return 0.0;
    	
        double acceptedTraces = new Double(this.getAcceptedTracesNumber());
        double activeTraces = new Double(this.activeTracesNumber);
    
        return acceptedTraces/activeTraces;
    }

    public void increaseTokenNumber() {
    	//this.outputOccurred = true;
        this.currentTokenNumber++;
    }

    public void decreaseTokenNumber() {
        this.currentTokenNumber--;
    }
    
    public boolean isCurrentTokenNumberNegative(){
    	return currentTokenNumber<0;
    }
    
    public boolean isCurrentTokenNumberZero(){
    	return currentTokenNumber==0;
    }
    
    public void resetCurrentTokenNumber(){
    	currentTokenNumber = 0;
    }
    
    public int getActiveTracesNumber() {
    	return this.activeTracesNumber;
    }

    public boolean isCurrentTokenNumberHigherThanOne(){
    	return currentTokenNumber>1;
    }

	
}
