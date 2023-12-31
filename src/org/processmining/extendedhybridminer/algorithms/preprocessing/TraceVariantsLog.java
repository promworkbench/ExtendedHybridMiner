package org.processmining.extendedhybridminer.algorithms.preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.extendedhybridminer.plugins.HybridCGMinerSettings;

public class TraceVariantsLog {

	protected int originalLogSize;
	ArrayList<TraceVariant> nonFilteredVariants;
	protected ArrayList<TraceVariant> variants;
	protected int minimalFrequency;
	protected int numberOfCoveredTraces;
	
	
	public TraceVariantsLog(XLog log, HybridCGMinerSettings settings, double freq) {
		
		this.originalLogSize = log.size();
		this.numberOfCoveredTraces = 0;
		this.variants = new ArrayList<TraceVariant>();
		this.minimalFrequency = (int) Math.ceil(this.originalLogSize * freq);
		Map<String, Integer> activityFrequencyMap = new HashMap<String, Integer>();
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
			if (f >= this.minimalFrequency) {
				this.variants.add(t);
				this.numberOfCoveredTraces = this.numberOfCoveredTraces + f;
				for (String eventKey : t.getActivities()) {				
		            Integer value = activityFrequencyMap.get(eventKey);
		            if (value==null)
		            	value = new Integer(f);
		            else 
		            	value = value+f;
		            activityFrequencyMap.put(eventKey, value);

				}
			}
		}
		
		settings.setActivityFrequencyMap(activityFrequencyMap);
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
