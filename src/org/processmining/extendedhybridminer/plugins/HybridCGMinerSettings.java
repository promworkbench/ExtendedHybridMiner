package org.processmining.extendedhybridminer.plugins;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;


public class HybridCGMinerSettings {

    private XEventClassifier classifier = new XEventNameClassifier();
    private Map<String, Integer> activityFrequencyMap;
	private double activityThreshold;
	private double traceVariantsThreshold;
    private double sureThreshold;
    private double questionMarkThreshold;
    private double longDepThreshold;
    private double causalityWeight;
    private static double POSITIVEOBSERVATIONDEGREE = 0;
    private static double TRACEVARIANTSTHRESHOLD = 0;
    private static double SURETHRESHOLD = 0.5;
    private static double QUESTIONMARKTHRESHOLD = 0.3;
    private static double LONGDEPTHRESHOLD = 0.8;
    private static double CAUSALITYWEIGHT = 0.5;
    private static double DEPENDENCYPARAMETER = 1;
    
    
    public HybridCGMinerSettings(double activityThreshold, double traceThreshold, 
    		double sureThreshold, double questionMarkThreshold, double longDep, double causalityWeight) {
        this.activityThreshold = activityThreshold;
        this.traceVariantsThreshold = traceThreshold;
        this.sureThreshold = sureThreshold;
        this.questionMarkThreshold = questionMarkThreshold;
        this.longDepThreshold = longDep;
        this.activityFrequencyMap = new HashMap<>();
        this.causalityWeight = causalityWeight;
    }
    

    public HybridCGMinerSettings(){
    	this(POSITIVEOBSERVATIONDEGREE, TRACEVARIANTSTHRESHOLD, SURETHRESHOLD, QUESTIONMARKTHRESHOLD, LONGDEPTHRESHOLD, CAUSALITYWEIGHT);
    }
    
    public double getCausalityWeight() {
		return causalityWeight;
	}

	public void setCausalityWeight(double causalityWeight) {
		this.causalityWeight = causalityWeight;
	}

	public Map<String, Integer> getActivityFrequencyMap() {
    	return activityFrequencyMap;
    }
    
    public void setActivityFrequencyMap(Map<String, Integer> activityFrequencyMap) {
    	this.activityFrequencyMap = activityFrequencyMap;
    }
	
    public double getSureThreshold() {
        return sureThreshold;
    }

    public double getQuestionMarkThreshold() {
        return questionMarkThreshold;
    }

	public void setSureThreshold(double sureThreshold) {
		this.sureThreshold = sureThreshold;
	}

	public void setQuestionMarkThreshold(double questionMarkThreshold) {
		this.questionMarkThreshold = questionMarkThreshold;
	}
	
	public double getDependencyParamter() {
		return this.DEPENDENCYPARAMETER;
	}
	
	public double getLongDepThreshold() {
		return this.longDepThreshold;
	}

	public void setLongDepThreshold(double ld) {
		this.longDepThreshold = ld;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HybridCGMinerSettings that = (HybridCGMinerSettings) o;
        if (Double.compare(that.getFilterAcivityThreshold() , this.getFilterAcivityThreshold()) != 0) return false;
        if (Double.compare(that.getTraceVariantsThreshold() , this.getTraceVariantsThreshold()) != 0) return false;
        if (Double.compare(that.getSureThreshold(), getSureThreshold()) != 0) return false;
        if (Double.compare(that.getQuestionMarkThreshold(), getQuestionMarkThreshold()) != 0) return false;
        if (Double.compare(that.getLongDepThreshold(), getLongDepThreshold()) != 0) return false;   
        if (Double.compare(that.getCausalityWeight(), getCausalityWeight()) != 0) return false;
        return true;    
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = 1;
        temp = Double.doubleToLongBits(this.getFilterAcivityThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.getTraceVariantsThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getSureThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));    
        temp = Double.doubleToLongBits(getLongDepThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32)); 
        temp = Double.doubleToLongBits(getQuestionMarkThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));  
        temp = Double.doubleToLongBits(getCausalityWeight());
        result = 31 * result + (int) (temp ^ (temp >>> 32));          
        return result;
    }

	public XEventClassifier getClassifier() {
		return this.classifier;
	}

	public void setTraceVariantsThreshold(double trace_variants_threshold) {
		this.traceVariantsThreshold = trace_variants_threshold ;
	}
	
	public double getTraceVariantsThreshold() {
		return traceVariantsThreshold;
	}

	public void setFilterAcivityThreshold(double d) {
		this.activityThreshold = d;
	}
	
	public double getFilterAcivityThreshold() {
		return this.activityThreshold;
	}


	public void setClassifier(XEventClassifier classifier2) {
		this.classifier = classifier2;
	}

}
