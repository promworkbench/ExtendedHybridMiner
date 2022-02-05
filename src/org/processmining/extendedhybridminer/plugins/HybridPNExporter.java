package org.processmining.extendedhybridminer.plugins;

import java.io.File;
import java.io.IOException;

import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.extendedhybridminer.models.pnml.utils;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.exporting.PnmlExportNet;

@Plugin(name = "Export Hybrid Petri Net into PNML File", level = PluginLevel.PeerReviewed, returnLabels = {}, returnTypes = {}, parameterLabels = { "Hybrid Petri Net", "File" }, userAccessible = true)
@UIExportPlugin(description = "PNML files (Hybrid Petri Net) ", extension = "pnml")
public class HybridPNExporter extends PnmlExportNet {
	
	@PluginVariant(variantLabel = "Export Hybrid Petri Net into PNML File", requiredParameterLabels = { 0, 1 })
    public void exportPetriNetToPNMLFile(PluginContext context, ExtendedHybridPetrinet net, File file) throws IOException {
            export(context, net, file, Pnml.PnmlType.PNML);
    }
    
    protected void export(PluginContext context, ExtendedHybridPetrinet net, File file, Pnml.PnmlType type)  throws IOException {
    	utils.exportHybridPetrinetToFile(context, file, net);
    }
    
}