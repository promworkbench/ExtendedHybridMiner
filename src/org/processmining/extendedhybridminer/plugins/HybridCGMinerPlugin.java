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
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = "Extended Causal Graph Miner", parameterLabels = {"log", "Hybrid Causal Graph Configuration" }, 
	    returnLabels = {"Causal Graph"}, returnTypes = {ExtendedCausalGraph.class})
public class HybridCGMinerPlugin {
	
	private ExtendedCausalGraph privateFCGMinerPlugin(PluginContext context, XLog log, HybridCGMinerSettings settings) {
        XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, settings.getClassifier());
		XLog filteredLog = LogFilterer.filterLogByActivityFrequency(log, logInfo, settings);
		TraceVariantsLog variants = new TraceVariantsLog(filteredLog, settings, settings.getTraceVariantsThreshold());
		HybridCGMiner miner = new HybridCGMiner(filteredLog, filteredLog.getInfo(settings.getClassifier()), variants, settings);
		ExtendedCausalGraph fCG = miner.mineFCG();
		fCG.setUnfilteredLog(log);
		return fCG;
	}
	
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "H. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridCGMiner, parameters", requiredParameterLabels = { 0, 1})
	public ExtendedCausalGraph configuredFPNMinerPlugin(PluginContext context, XLog log, HybridCGMinerSettings settings) {		
		return privateFCGMinerPlugin(context, log, settings);		
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridCGMiner, parameters", requiredParameterLabels = { 0 })
	public ExtendedCausalGraph defaultFCGMinerPlugin(PluginContext context, XLog log) {		
		HybridCGMinerSettings settings = new HybridCGMinerSettings();
	    return privateFCGMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridCGMiner, dialog", requiredParameterLabels = { 0 })
	public ExtendedCausalGraph dialogFPNMinerPlugin(UIPluginContext context, XLog log) {		
	    HybridCGMinerSettings settings = new HybridCGMinerSettings();
	    CGDialog dialog = new CGDialog(context, log, settings);
	    InteractionResult result = context.showWizard("Extended Causal Graph Miner Settings", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
			return privateFCGMinerPlugin(context, log, settings);
	    }
	    return null;
	}	
}
