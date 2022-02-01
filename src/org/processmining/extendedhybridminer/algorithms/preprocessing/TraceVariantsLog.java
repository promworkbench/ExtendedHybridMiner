package org.processmining.extendedhybridminer.algorithms.preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class TraceVariantsLog {
	//protected int size;
	protected int originalLogSize;
	ArrayList<TraceVariant> nonFilteredVariants;
	protected ArrayList<TraceVariant> variants;
	protected int minimalFrequency;
	protected int numberOfCoveredTraces;
	
	
	
	public TraceVariantsLog(XLog log, Map<String, Integer> activityFrequencyMap, double freq) {
		this.originalLogSize = log.size();
		this.numberOfCoveredTraces = 0;
		this.variants = new ArrayList<TraceVariant>();
		this.minimalFrequency = (int) Math.ceil(this.originalLogSize * freq);
		//this.logName = XConceptExtension.instance().extractName(log);
		//this.logInfo = XLogInfoFactory.createLogInfo(log, XLogInfoImpl.NAME_CLASSIFIER);
			
		nonFilteredVariants = new ArrayList<TraceVariant>();
		outerloop:
		for (XTrace trace : log) {
			ArrayList<String> activities = new ArrayList<String>();
			for (XEvent event : trace) {
				StandardModel eventTransition = XLifecycleExtension.instance().extractStandardTransition(event);
				if (eventTransition !=null && !eventTransition.equals(XLifecycleExtension.StandardModel.COMPLETE)) {
					continue;
				}
				String activity = XConceptExtension.instance().extractName(event);
				activities.add(activity);
			}
			if (!activities.get(0).equals("start")) {
				activities.add(0, "start");
				activityFrequencyMap.compute("start", (k, v) -> (v == null) ? 1 : v+1);
			}
			if (!activities.get(activities.size() - 1).equals("end")) {
				activities.add(activities.size(), "end");
				activityFrequencyMap.compute("end", (k, v) -> (v == null) ? 1 : v+1);
				
			}
            TraceVariant variant = new TraceVariant(activities);
           
			for (int i = 0; i < nonFilteredVariants.size(); i++) {
				if (nonFilteredVariants.get(i).sameActivitySequence(variant)) {
					nonFilteredVariants.get(i).increaseFrequency();
					continue outerloop;
				}
			}
			
			nonFilteredVariants.add(variant);
		}
		
		Collections.sort(nonFilteredVariants);
		
		for (TraceVariant t: nonFilteredVariants) {
			int f = t.getFrequency();
			if (f < this.minimalFrequency) {
				for (String activity: new HashSet<String>(t.getActivities())) {
					int actFreq = activityFrequencyMap.get(activity) - f;
					activityFrequencyMap.put(activity, actFreq);
					
				}
			} else {
				this.variants.add(t);
				this.numberOfCoveredTraces = this.numberOfCoveredTraces + f;
			}
		}
	}
	

	public int getSize() {
		return variants.size();
	}
	
	public int getOriginalLogSize() {
		return this.originalLogSize;
	}
	
	public ArrayList<TraceVariant> getVariants() {
		return this.variants;
	}

	
	public ArrayList<String> getActivitySequence(int i) {
		return this.variants.get(i).getActivities();
	}
	
	public int getFrequency(int i) {
		return this.variants.get(i).getFrequency();
	}
	
	
	public void print() {
		System.out.println("#Variants: " );
		System.out.println("Trace Variants");
		for (int i = 0; i < this.variants.size(); i++) {
			TraceVariant v = this.variants.get(i); 
			System.out.println("Index: " + i + ", Variant: " + v.getActivities() + ", Frequency: " + v.getFrequency());
		}	
	}


	/*public String getLogName() {
		return this.logName;
	}*/


	/*public XLogInfo getLogInfo() {
		return this.logInfo;
	}*/
	

	public ArrayList<TraceVariant> filterMostFrequent(double placeEvalThreshold) {
		int nTracesToCover = (int) Math.ceil(this.numberOfCoveredTraces * placeEvalThreshold);
		if (nTracesToCover ==  this.numberOfCoveredTraces) {
			return this.variants;
		}
		ArrayList<TraceVariant> res = new ArrayList<TraceVariant>();
		for (int i = 0; i < this.getSize(); i++) {
    		TraceVariant trace = this.getVariants().get(i);
    		res.add(trace);
    		nTracesToCover = nTracesToCover - trace.getFrequency();
    		if (nTracesToCover < 1) {
    			return res;
    		}
    	}
		return res;
	}


	public int getNumberOfCoveredTraces() {
		return this.numberOfCoveredTraces;
	}

}
