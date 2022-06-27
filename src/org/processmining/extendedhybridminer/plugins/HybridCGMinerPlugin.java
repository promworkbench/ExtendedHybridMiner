package org.processmining.extendedhybridminer.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.extendedhybridminer.algorithms.HybridCGMiner;
import org.processmining.extendedhybridminer.algorithms.preprocessing.LogFilterer;
import org.processmining.extendedhybridminer.algorithms.preprocessing.TraceVariantsLog;
import org.processmining.extendedhybridminer.dialogs.CGDialog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = "Extended Causal Graph Miner", parameterLabels = {"log", "Hybrid Causal Graph Configuration" }, 
	    returnLabels = {"Causal Graph"}, returnTypes = {ExtendedCausalGraph.class}, categories = PluginCategory.Discovery)
public class HybridCGMinerPlugin {
	
	public static ExtendedCausalGraph CGMinerPlugin(PluginContext context, XLog log, HybridCGMinerSettings settings) {
        XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, settings.getClassifier());
		XLog filteredLog = LogFilterer.filterLogByActivityFrequency(log, logInfo, settings);
		TraceVariantsLog variants = new TraceVariantsLog(filteredLog, settings, settings.getTraceVariantsThreshold());
		HybridCGMiner miner = new HybridCGMiner(filteredLog, filteredLog.getInfo(settings.getClassifier()), variants, settings);
		ExtendedCausalGraph cg = miner.mineFCG();
		cg.setUnfilteredLog(log);
		return cg;
	}
	
	
	@UITopiaVariant(affiliation = "RWTH, FBK", author = "H. Kourani et al.", email = "humam.kourani@rwth-aachen.de, r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridCGMiner, parameters", requiredParameterLabels = { 0 })
	public ExtendedCausalGraph defaultFCGMinerPlugin(PluginContext context, XLog log) {		
		HybridCGMinerSettings settings = new HybridCGMinerSettings();
	    return CGMinerPlugin(context, log, settings);
	}
	
	@UITopiaVariant(affiliation = "RWTH, FBK", author = "H. Kourani et al.", email = "humam.kourani@rwth-aachen.de, r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridCGMiner, dialog", requiredParameterLabels = { 0 })
	public ExtendedCausalGraph dialogFPNMinerPlugin(UIPluginContext context, XLog log) {		
	    HybridCGMinerSettings settings = new HybridCGMinerSettings();
	    CGDialog dialog = new CGDialog(context, log, settings);
	    InteractionResult result = context.showWizard("Extended Causal Graph Miner Settings", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
			return CGMinerPlugin(context, log, settings);
	    }
	    return null;
	}	
	
}
