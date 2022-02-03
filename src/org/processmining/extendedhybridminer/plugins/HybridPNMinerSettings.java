package org.processmining.extendedhybridminer.plugins;

import org.processmining.extendedhybridminer.models.hybridpetrinet.FitnessType;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplaceiterator.CandidatePlaceIteratorEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.candidateplacestrategy.CandidatePlaceSelectionStrategyEnum;
import org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy.ConflictStrategyEnum;


public class HybridPNMinerSettings {


    private double prePlaceEvaluationThreshold;
    private double placeEvalThreshold;
    private static double PREPLACEEVALUATIONTHRESHOLD = 0.0;
    private static double PLACEEVALTHRESHOLD = 0.8;
    private static int THRESHOLDEARLYCANCELATIONITERATOR = 1000;
    private static int MAXTIME=Integer.MAX_VALUE;
    private static int MAXPLACENUMBER=Integer.MAX_VALUE;
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

    public void setMaxPlaceNumber(int m)
    {
    	this.maxPlaces=m;
    }

    public void setPlaceNumber(int pn)
    {
    	if(this.selectionStrategy==CandidatePlaceSelectionStrategyEnum.INPUT_PLUS_OUTPUT_BOUND || this.selectionStrategy==CandidatePlaceSelectionStrategyEnum.SPLIT_JOIN_ONLY_BOUND)
    	{
    		this.places=pn;
    	}
    }
    
    public void setPlaceNumber(int pi, int po)
    {
    	if(this.selectionStrategy==CandidatePlaceSelectionStrategyEnum.INPUT_AND_OUTPUT_BOUND)
    	{
    		this.inputPlaces=pi;
    		this.outputPlaces=po;
    	}
    }
    
    public void setPlaceStrategy(CandidatePlaceSelectionStrategyEnum ps)
    {
    	this.selectionStrategy=ps;
    }
    
    public void setOrderingStrategy(CandidatePlaceIteratorEnum os)
    {
    	this.orderingStrategy=os;
    }
    
    public void setConflictStrategy(ConflictStrategyEnum cs)
    {
    	this.conflictStrategy=cs;
    }
    
    public void setReplayThreshold(int rt)
    {
    	this.replayThreshold=rt;
    }
    
    public void setMaxTime(int mt)
    {
    	this.time=mt*60;
    }

    public int getMaxPlaceNumber()
    {
    		return this.maxPlaces;
    }

    public int getPlaceNumber()
    {
    		return this.places;
    }
    
    public int getIPlaceNumber()
    {
    	return this.inputPlaces;
    }
    
    public int getOPlaceNumber()
    {
    	return this.outputPlaces;
    }
    
    public CandidatePlaceSelectionStrategyEnum getPlaceStrategy()
    {
    	return this.selectionStrategy;
    }
    
    public CandidatePlaceIteratorEnum getOrderingStrategy()
    {
    	return this.orderingStrategy;
    }
    
    public ConflictStrategyEnum getConflictStrategy()
    {
    	return this.conflictStrategy;
    }

    public int getReplayThreshold()
    {
    	return this.replayThreshold;
    }
    
    public int getMaxTime()
    {
    	return this.time;
    }
    
    public void setPlaceEvalThreshold(double placeEvalThreshold) {
		this.placeEvalThreshold = placeEvalThreshold;
	}
	
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
        return result;
    }

	public int getThresholdEarlyCancelationIterator() {
		// TODO Auto-generated method stub
		return this.thresholdEarlyCancelationIterator; 
	}
	
	public void setThresholdEarlyCancelationIterator(int i) {
		// TODO Auto-generated method stub
		this.thresholdEarlyCancelationIterator = i; 
	}
    
}


