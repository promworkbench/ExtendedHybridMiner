package org.processmining.extendedhybridminer.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JComponent;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.freehep.graphicsbase.util.export.ExportFileType;
import org.freehep.graphicsio.exportchooser.ImageExportFileType;
import org.jgraph.JGraph;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.extendedhybridminer.algorithms.cg2hpn.CGToHybridPN;
import org.processmining.extendedhybridminer.dialogs.HybridPNDialog;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.gui.HybridPetrinetVisualizer;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.impl.ProgressBarImpl;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml.PnmlType;
import org.processmining.plugins.pnml.exporting.PnmlExportNet;

@Plugin(name = "Extended Hybrid Petri Net Miner", parameterLabels = {"Causal Graph", "Hybrid Petri Net Configuration" }, 
	    returnLabels = { "Hybrid Petri Net"}, returnTypes = {ExtendedHybridPetrinet.class})
public class HybridPNMinerPlugin {

	private ExtendedHybridPetrinet privateFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings) {	
		ExtendedHybridPetrinet fPN = CGToHybridPN.fuzzyCGToFuzzyPN(fCG, settings);
        fPN = addMarkings(context, fPN);
        saveHybridPetrinet(context, fPN, "./output/output_models/HMOEC2_model.pnml");
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
        context.addConnection(new InitialMarkingConnection(fPN, 
                im));

        context.getProvidedObjectManager().createProvidedObject(
                    "Final marking for " + fPN.getLabel(),
                    fm, Marking.class, context);
        context.addConnection(new FinalMarkingConnection(fPN, fm));
        
        return fPN;
	}
	
		
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, parameters", requiredParameterLabels = { 0, 1})
	public ExtendedHybridPetrinet configuredFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings) {
		return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, parameters", requiredParameterLabels = { 0 })
	public ExtendedHybridPetrinet defaultFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG) {
		HybridPNMinerSettings settings = new HybridPNMinerSettings();
		return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, dialog", requiredParameterLabels = { 0})
	public ExtendedHybridPetrinet dialogFPNMinerPlugin(UIPluginContext context, ExtendedCausalGraph fCG) {
		HybridPNMinerSettings settings = new HybridPNMinerSettings();
	    HybridPNDialog dialog = new HybridPNDialog(context, fCG, settings);
	    InteractionResult result = context.showWizard("Extended Hybrid Petri Net Miner Settings", true, true, dialog);
	    if (result == InteractionResult.FINISHED) {
			return privateFPNMinerPlugin(context, fCG, settings);
	    }
	    return null;
	}	
	
	private void saveHybridPetrinet(PluginContext context, ExtendedHybridPetrinet fPN, String filePath){
	       PnmlExportNet pnmlEN = new PnmlExportNet();
	       String pnmlString = pnmlEN.exportPetriNetToPNMLOrEPNMLString(context, fPN, PnmlType.PNML, true);
	       try {
			FileWriter fW  = new FileWriter(new File(filePath));
			fW.write(pnmlString);
			fW.close();
			JGraph graph;
			try {
				JComponent comp = HybridPetrinetVisualizer.getResultsPanel(fPN, new ViewSpecificAttributeMap(), new ProgressBarImpl(context));
				graph = HybridPetrinetVisualizer.createJGraph(fPN, new ViewSpecificAttributeMap(), new ProgressBarImpl(context));
				graph.setSize(2000, 2000);
				List<ExportFileType> list = ImageExportFileType.getExportFileTypes();
				for (ExportFileType exportFileType : list) {
					for (String type : exportFileType.getExtensions()) {
						if(type.equalsIgnoreCase("png")){
							Properties properties = new Properties();
							exportFileType.exportToFile(new File("./output/output_models/HMOEC2_model_image.png"), graph, null, properties, null);
						}
					}					
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
