package org.processmining.extendedhybridminer.plugins;

import org.processmining.extendedhybridminer.models.hybridpetrinet.FitnessType;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.CandidatePlaceIteratorEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategyEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.ConflictStrategyEnum;

/**
 * Created by demas on 25/07/16.
 */
public class HybridPNMinerSettings {


    private double prePlaceEvaluationThreshold;

	private double placeEvalThreshold;
    
	/*private int maxeEdgeClusterSize; 
    private int maxClusterSize; 
	private boolean maxClusterSizeEnabled;*/

    
    private static double PREPLACEEVALUATIONTHRESHOLD = 0.0;
    private static double PLACEEVALTHRESHOLD = 0.8;
    private static int THRESHOLDEARLYCANCELATIONITERATOR = 1000;
    /*private static int MAXEDGECLUSTERSIZE = 6;
    private static int MAXCLUSTERSIZE = 10000;
    private static boolean MAXCLUSTERSIZEENABLED = false;*/
    
    // Updates by sabi
    private static int MAXTIME=Integer.MAX_VALUE;
    //private static int TIMEOUTTHRESHOLD=Integer.MAX_VALUE;
    private static int MAXPLACENUMBER=Integer.MAX_VALUE;
    // Previously it was 
    //private static int PLACENUMBER=5;
    //private static int PLACENUMBER=Integer.MAX_VALUE;
    //private static int PLACENUMBER=10;
    private static int PLACENUMBER=20;
    
    private static CandidatePlaceSelectionStrategyEnum PLACESTRATEGY=CandidatePlaceSelectionStrategyEnum.INPUT_PLUS_OUTPUT_BOUND;
    private static CandidatePlaceIteratorEnum ORDERINGSTRATEGY=CandidatePlaceIteratorEnum.MINIMAL;
    private static ConflictStrategyEnum CONFLICTSTRATEGY=ConflictStrategyEnum.MINIMAL_PLACES;
    private static FitnessType FITNESSTYPE = FitnessType.GLOBAL;
    private static int REPLAYTHRESHOLD=Integer.MAX_VALUE;
    private static int PLACENUMBERINPUT=Integer.MAX_VALUE;
    private static int PLACENUMBEROUTPUT=Integer.MAX_VALUE;
    
	private int time; // t_max
	private int replayThreshold; // t_replay
	private int maxPlaces; // n_max
	// next 3 variables: needed for the cluster strategy
	private int places;
	private int inputPlaces;
	private int outputPlaces;
    private CandidatePlaceSelectionStrategyEnum selectionStrategy;
    private CandidatePlaceIteratorEnum orderingStrategy;
    private ConflictStrategyEnum conflictStrategy;
    private FitnessType fitnessType;

	private int thresholdEarlyCancelationIterator = THRESHOLDEARLYCANCELATIONITERATOR;
    
    public double getPrePlaceEvaluationThreshold() {
		return prePlaceEvaluationThreshold;
	}

	public void setPrePlaceEvaluationThreshold(double prePlaceEvaluationThreshold) {
		this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
	}

    
    public double getPlaceEvalThreshold() {
        return placeEvalThreshold;
    }
    
    // Updates by sabi
    // Constructor for setting the default values
    public HybridPNMinerSettings()
    {
        this(MAXTIME,REPLAYTHRESHOLD,PLACESTRATEGY,MAXPLACENUMBER,PLACENUMBER,PLACENUMBERINPUT,PLACENUMBEROUTPUT,ORDERINGSTRATEGY,CONFLICTSTRATEGY, PREPLACEEVALUATIONTHRESHOLD,PLACEEVALTHRESHOLD, FITNESSTYPE);
    }
    
    public HybridPNMinerSettings(double placeEvalThresold)
    {
        this(MAXTIME,REPLAYTHRESHOLD,PLACESTRATEGY,MAXPLACENUMBER,PLACENUMBER,PLACENUMBERINPUT,PLACENUMBEROUTPUT,ORDERINGSTRATEGY,CONFLICTSTRATEGY, PREPLACEEVALUATIONTHRESHOLD,placeEvalThresold, FITNESSTYPE);
    }
    
    public HybridPNMinerSettings(double placeEvalThresold, FitnessType fitness)
    {
        this(MAXTIME,REPLAYTHRESHOLD,PLACESTRATEGY,MAXPLACENUMBER,PLACENUMBER,PLACENUMBERINPUT,PLACENUMBEROUTPUT, ORDERINGSTRATEGY, CONFLICTSTRATEGY, PREPLACEEVALUATIONTHRESHOLD, placeEvalThresold, fitness);
    }
    
    // Constructor for setting the initial values (i.e. default one)
    public HybridPNMinerSettings(int t,int r,CandidatePlaceSelectionStrategyEnum ps,int pm,int pn,int pi,int po,CandidatePlaceIteratorEnum os,
    		ConflictStrategyEnum cs,Double prePlaceEvalThreshold, Double placeEvalThreshold, FitnessType fitness)
    {
        this.time=t;
        this.replayThreshold=r;
        this.selectionStrategy=ps;
        this.maxPlaces=pm;
        this.places=pn;
        this.inputPlaces=pi;
        this.outputPlaces=po;
        this.orderingStrategy=os;
        this.conflictStrategy=cs;
        this.prePlaceEvaluationThreshold = prePlaceEvalThreshold;
        this.placeEvalThreshold = placeEvalThreshold;
        this.fitnessType = fitness;
        

    }
    // Sets the maximum number of places to consider
    public void setMaxPlaceNumber(int m)
    {
    	this.maxPlaces=m;
    }
    // Sets the number of places if the selection strategy requires one
    public void setPlaceNumber(int pn)
    {
    	if(this.selectionStrategy==CandidatePlaceSelectionStrategyEnum.INPUT_PLUS_OUTPUT_BOUND || this.selectionStrategy==CandidatePlaceSelectionStrategyEnum.SPLIT_JOIN_ONLY_BOUND)
    	{
    		this.places=pn;
    	}
    }
    // Sets the number of places if the selection strategy requires two
    public void setPlaceNumber(int pi, int po)
    {
    	if(this.selectionStrategy==CandidatePlaceSelectionStrategyEnum.INPUT_AND_OUTPUT_BOUND)
    	{
    		this.inputPlaces=pi;
    		this.outputPlaces=po;
    	}
    }
    // Sets the selection strategy
    public void setPlaceStrategy(CandidatePlaceSelectionStrategyEnum ps)
    {
    	this.selectionStrategy=ps;
    }
    // Sets the ordering strategy
    public void setOrderingStrategy(CandidatePlaceIteratorEnum os)
    {
    	this.orderingStrategy=os;
    }
    // Sets the strategy for handling conflicts
    public void setConflictStrategy(ConflictStrategyEnum cs)
    {
    	this.conflictStrategy=cs;
    }
    // Sets the threshold of the replay
    public void setReplayThreshold(int rt)
    {
    	this.replayThreshold=rt;
    }
    // Sets the maximum amount of time the algorithm can take
    public void setMaxTime(int mt)
    {
    	this.time=mt*60;
    }
    
    
    // Gets the number of maximum places to consider
    public int getMaxPlaceNumber()
    {
    		return this.maxPlaces;
    }
    // Gets the number of places (input + output)
    public int getPlaceNumber()
    {
    		return this.places;
    }
    // Gets the number of input places (to be used only when the selection strategy requires it)
    public int getIPlaceNumber()
    {
    	return this.inputPlaces;
    }
    // Gets the number of output places (to be used only when the selection strategy requires it)
    public int getOPlaceNumber()
    {
    	return this.outputPlaces;
    }
    // Gets the selection strategy
    public CandidatePlaceSelectionStrategyEnum getPlaceStrategy()
    {
    	return this.selectionStrategy;
    }
    // Gets the ordering strategy
    public CandidatePlaceIteratorEnum getOrderingStrategy()
    {
    	return this.orderingStrategy;
    }
    // Gets the strategy for handling conflicts
    public ConflictStrategyEnum getConflictStrategy()
    {
    	return this.conflictStrategy;
    }
    // Gets the threshold of the replay
    public int getReplayThreshold()
    {
    	return this.replayThreshold;
    }
    // Gets the maximum amount of time the algorithm can take
    public int getMaxTime()
    {
    	return this.time;
    }
    
    
    /*public boolean isMaxClusterSizeEnabled() {
		return maxClusterSizeEnabled;
	}*/

	public void setPlaceEvalThreshold(double placeEvalThreshold) {
		this.placeEvalThreshold = placeEvalThreshold;
	}
	
	/*public void setMaxClusterSizeEnabled(boolean maxClusterSizeEnabled) {
		this.maxClusterSizeEnabled = maxClusterSizeEnabled;
	}
	
    public int getMaxeEdgeClusterSize() {
		return maxeEdgeClusterSize;
	}

	public int getMaxClusterSize() {
		return maxClusterSize;
	}*/
	
	public void setFitnessType (FitnessType t) {
	   this.fitnessType = t;
	}
	
	public FitnessType getFitnessType() {
		return this.fitnessType;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HybridPNMinerSettings that = (HybridPNMinerSettings) o;

        if (Double.compare(that.getPrePlaceEvaluationThreshold(), prePlaceEvaluationThreshold) != 0) return false;       
        if (Double.compare(that.getPlaceEvalThreshold(), placeEvalThreshold) != 0) return false;
        //return (Boolean.compare(that.isMaxClusterSizeEnabled(), maxClusterSizeEnabled) == 0);
        return true;

        
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = 1;
        temp = Double.doubleToLongBits(getPrePlaceEvaluationThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(getPlaceEvalThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        /*temp = isMaxClusterSizeEnabled()? 1231:1237;
        result = 31 * result + (int) (temp ^ (temp >>> 32)); */       
        return result;
    }

	public int getThresholdEarlyCancelationIterator() {
		// TODO Auto-generated method stub
		return this.thresholdEarlyCancelationIterator; 
	}
	
	public void setThresholdEarlyCancelationIterator(int i) {
		// TODO Auto-generated method stub
		//System.out.println("new ItStop: " + thresholdEarlyCancelationIterator);
		this.thresholdEarlyCancelationIterator = i; 
	}
    
}


