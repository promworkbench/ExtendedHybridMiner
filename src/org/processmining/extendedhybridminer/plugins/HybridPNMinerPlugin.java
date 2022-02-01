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
import org.processmining.extendedhybridminer.models.hybridpetrinet.HybridPetrinet;
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

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Extended Hybrid Petri Net Miner", parameterLabels = {"Causal Graph", "Hybrid Petri Net Configuration" }, 
	    returnLabels = { "Hybrid Petri Net"}, returnTypes = {HybridPetrinet.class})
public class HybridPNMinerPlugin {

	private HybridPetrinet privateFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings) {	
		//double t1 = System.currentTimeMillis();
        HybridPetrinet fPN = CGToHybridPN.fuzzyCGToFuzzyPN(fCG, settings);
        fPN = addMarkings(context, fPN);
        System.out.println("Number of transitions: "+fPN.getTransitions().size());
        System.out.println("Number of places: "+fPN.getPlaces().size());
        System.out.println("Number of edges: "+fPN.getEdges().size());
        saveHybridPetrinet(context, fPN, "./output/output_models/HMOEC2_model.pnml");
/*        double t2 = System.currentTimeMillis();
		System.out.println("*****************************************");
		System.out.println("TIME REQUIRED FOR FPN "+(t2 - t1));
		System.out.println("*****************************************");	*/	
		return fPN;

	}
	
	/**
	 * Add initial and end place, as well as the initial and final marking
	 * @param context
	 * @param fPN
	 * @return fPN
	 */
	private HybridPetrinet addMarkings(PluginContext context, HybridPetrinet fPN){
        Place startPlace = fPN.getPlace("start");
        Marking im = new Marking();
        im.add(startPlace);
        //Transition startTransition = fPN.getTransition("start");
        //fPN.addArc(startPlace, startTransition);
        
        Place endPlace = fPN.getPlace("end");
        Marking fm = new Marking();
        fm.add(endPlace);        
        //Transition endTransition = fPN.getTransition("end");
        //fPN.addArc(endTransition, endPlace);
               
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
	public HybridPetrinet configuredFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG, HybridPNMinerSettings settings) {
		return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, parameters", requiredParameterLabels = { 0 })
	public HybridPetrinet defaultFPNMinerPlugin(PluginContext context, ExtendedCausalGraph fCG) {
		// Get the default configuration.
			
		HybridPNMinerSettings settings = new HybridPNMinerSettings();
		// Do the heavy lifting.
	    return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "HybridPNMiner, dialog", requiredParameterLabels = { 0})
	public HybridPetrinet dialogFPNMinerPlugin(UIPluginContext context, ExtendedCausalGraph fCG) {
		// Get the default configuration.
	    HybridPNMinerSettings settings = new HybridPNMinerSettings();
	     
	    // Get a dialog for this configuration.
	    HybridPNDialog dialog = new HybridPNDialog(context, fCG, settings);
	    // Show the dialog. User can now change the configuration.
	    InteractionResult result = context.showWizard("Extended Hybrid Petri Net Miner Settings", true, true, dialog);
	    // User has close the dialog.
	    if (result == InteractionResult.FINISHED) {
			// Do the heavy lifting.
	    	return privateFPNMinerPlugin(context, fCG, settings);
	    }
	    // Dialog got canceled.
	    return null;
	}	
	
	private void saveHybridPetrinet(PluginContext context, HybridPetrinet fPN, String filePath){
	       PnmlExportNet pnmlEN = new PnmlExportNet();
	       String pnmlString = pnmlEN.exportPetriNetToPNMLOrEPNMLString(context, fPN, PnmlType.PNML, true);
	       try {
			FileWriter fW  = new FileWriter(new File(filePath));
			fW.write(pnmlString);
			fW.close();
			//org.freehep.graphicsbase.util.export.ExportFileType type = new  E
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
