package org.processmining.extendedhybridminer.algorithms.preprocessing;

import java.util.ArrayList;

public class TraceVariant implements Comparable<TraceVariant> {

    private ArrayList<String> activities;
    private int frequency;

    public TraceVariant(ArrayList<String> activities){
        this.activities = activities;
        this.frequency = 1;
    }
    
    public ArrayList<String> getActivities() {
    	return this.activities;
    }
    
    public int getFrequency() {
    	return this.frequency;
    }
    
    void increaseFrequency() {
    	this.frequency++;
    }

    public int compareTo(TraceVariant t) {
        return  t.frequency - this.frequency;
    }
    
    public boolean sameActivitySequence(TraceVariant v) {
    	return this.activities.equals(v.activities);
    }

}

