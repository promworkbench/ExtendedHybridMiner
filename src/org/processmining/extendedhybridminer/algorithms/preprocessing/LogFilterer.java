package org.processmining.extendedhybridminer.algorithms.preprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.extendedhybridminer.plugins.HybridCGMinerSettings;

public class LogFilterer {
	
    public static XLog filterLogByActivityFrequency(XLog log, XLogInfo logInfo, HybridCGMinerSettings settings){
		
		Map<String, Integer> localActivityFrequencyMap = new HashMap<String, Integer>();
		
		for (XTrace trace : log) {
			Set<String> traceEvents = new HashSet<String>();
	        for (XEvent event : trace) {
	            String eventKey = logInfo.getEventClasses(settings.getClassifier()).getClassOf(event).getId();
	            traceEvents.add(eventKey);
	        }
	        for (String eventKey : traceEvents) {				
	            Integer value = localActivityFrequencyMap.get(eventKey);
	            if (value==null)
	            	value = new Integer(1);
	            else 
	            	value = value+1;
	            localActivityFrequencyMap.put(eventKey, value);

			}
		}

		int minFreq = (int) Math.ceil(settings.getFilterAcivityThreshold() * log.size());
		if(minFreq == 0) {
			return log;
		}
		XLog filteredLog = XFactoryRegistry.instance().currentDefault().createLog();
		filteredLog.setAttributes(log.getAttributes());
		for (XTrace trace : log) {
			XTrace filteredTrace = XFactoryRegistry.instance().currentDefault().createTrace();
			filteredTrace.setAttributes(trace.getAttributes());
	        for (XEvent event : trace) {
	        	String eventKey = logInfo.getEventClasses(settings.getClassifier()).getClassOf(event).getId(); 
	        	if((localActivityFrequencyMap.get(eventKey)) >= minFreq){
	        		filteredTrace.add(event);
	        	}
	        }
	        if (filteredTrace.size()>0) {
	        	filteredLog.add(filteredTrace);
	        }
		}
		XLogInfo filteredLogInfo = XLogInfoFactory.createLogInfo(filteredLog, settings.getClassifier());
		filteredLog.setInfo(settings.getClassifier(), filteredLogInfo);
		return filteredLog;
	}

}
