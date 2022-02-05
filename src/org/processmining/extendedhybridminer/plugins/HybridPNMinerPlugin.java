package org.processmining.extendedhybridminer.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.extendedhybridminer.algorithms.cg2hpn.CGToHybridPN;
import org.processmining.extendedhybridminer.dialogs.HybridPNDialog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;

@Plugin(name = "Extended Hybrid Petri Net Miner", parameterLabels = {"Causal Graph", "Hybrid Petri Net Configuration" }, 
	    returnLabels = { "Hybrid Petri Net"}, returnTypes = {ExtendedHybridPetrinet.class}, categories = PluginCategory.Discovery)
public class HybridPNMinerPlugin {

	private ExtendedHybridPetrinet privateFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings) {	
		ExtendedHybridPetrinet fPN = CGToHybridPN.fuzzyCGToFuzzyPN(fCG, settings);
        fPN = addMarkings(context, fPN);
        return fPN;
	}	
	
	private ExtendedHybridPetrinet addMarkings(PluginContext context, ExtendedHybridPetrinet fPN){
        Place startPlace = fPN.getPlace("start");
        Marking im = new Marking();
        im.add(startPlace);
        Place endPlace = fPN.getPlace("end");
        Marking fm = new Marking();
        fm.add(endPlace);        
        context.getProvidedObjectManager().createProvidedObject(
                "Initial marking for " + fPN.getLabel(),
                im, Marking.class, context);
        context.addConnection(new InitialMarkingConnection(fPN, im));
        context.getProvidedObjectManager().createProvidedObject(
                    "Final marking for " + fPN.getLabel(),
                    fm, Marking.class, context);
        context.addConnection(new FinalMarkingConnection(fPN, fm));
        return fPN;
	}	
		
	@UITopiaVariant(affiliation = "RWTH, FBK", author = "H. Kourani et al.", email = "humam.kourani@rwth-aachen.de, r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, parameters", requiredParameterLabels = {0})
	public ExtendedHybridPetrinet defaultFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG) {
		HybridPNMinerSettings settings = new HybridPNMinerSettings();
		return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	@UITopiaVariant(affiliation = "RWTH, FBK", author = "H. Kourani et al.", email = "humam.kourani@rwth-aachen.de, r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, dialog", requiredParameterLabels = {0})
	public ExtendedHybridPetrinet dialogFPNMinerPlugin(UIPluginContext context, ExtendedCausalGraph fCG) {
		HybridPNMinerSettings settings = new HybridPNMinerSettings();
	    HybridPNDialog dialog = new HybridPNDialog(context, fCG, settings);
	    InteractionResult result = context.showWizard("Extended Hybrid Petri Net Miner Settings", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
			return privateFPNMinerPlugin(context, fCG, settings);
	    }
	    return null;
	}	
	
}
