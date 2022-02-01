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

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Extended Causal Graph Miner", parameterLabels = {"log", "Hybrid Causal Graph Configuration" }, 
	    returnLabels = {"Causal Graph"}, returnTypes = {ExtendedCausalGraph.class})
public class HybridCGMinerPlugin {
	
	private ExtendedCausalGraph privateFCGMinerPlugin(PluginContext context, XLog log, HybridCGMinerSettings settings) {
        //double t1 = System.currentTimeMillis();
		//XLog preprocessedLog = LogPreprocessor.preprocessLog(log);
		//XLogInfo logInfo = XLogInfoFactory.createLogInfo(preprocessedLog, settings.getClassifier());
		//System.out.println("**** PREPROCESSING OVER ******");
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, settings.getClassifier());
		XLog filteredLog = LogFilterer.filterLogByActivityFrequency(log, logInfo, settings);
		System.out.println("**** FILTERING OVER ******");
		
		TraceVariantsLog variants = new TraceVariantsLog(filteredLog, settings.getActivityFrequencyMap(), settings.getTraceVariantsThreshold());
		//variants.filter(THRESHOLD_TRACE_VARIANTS_FREQUENCY);
		HybridCGMiner miner = new HybridCGMiner(filteredLog, filteredLog.getInfo(settings.getClassifier()), variants, settings);
		ExtendedCausalGraph fCG = miner.mineFCG();
		fCG.setUnfilteredLog(log);
		return fCG;
	}
	
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
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
		// Get the default configuration.
		/*XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);*/
					
		HybridCGMinerSettings settings = new HybridCGMinerSettings();
	    return privateFCGMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridCGMiner, dialog", requiredParameterLabels = { 0 })
	public ExtendedCausalGraph dialogFPNMinerPlugin(UIPluginContext context, XLog log) {
		// Get the default configuration.
	    
		/*XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);  */
		
	    HybridCGMinerSettings settings = new HybridCGMinerSettings();
		
	    // Get a dialog for this configuration.
	    CGDialog dialog = new CGDialog(context, log, settings);
	    // Show the dialog. User can now change the configuration.
	    InteractionResult result = context.showWizard("Extended Causal Graph Miner Settings", true, true, dialog);
	    // User has close the dialog.
	    if (result == InteractionResult.FINISHED) {
			// Do the heavy lifting.
	    	return privateFCGMinerPlugin(context, log, settings);
	    }
	    // Dialog got canceled.
	    return null;
	}	
}
