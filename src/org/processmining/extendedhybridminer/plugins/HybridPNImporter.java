package org.processmining.extendedhybridminer.plugins;

import java.io.InputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.extendedhybridminer.models.hybridpetrinet.ExtendedHybridPetrinet;
import org.processmining.extendedhybridminer.models.pnml.utils;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;

@Plugin(name = "Import Hybrid Petri Net from PNML File", level = PluginLevel.Regular, parameterLabels = { "Filename" }, returnLabels = { "Hybrid Petri Net" }, returnTypes = { ExtendedHybridPetrinet.class})
@UIImportPlugin(description = "PNML Hybrid Petri Net files", extensions = { "pnml" })
public class HybridPNImporter extends AbstractImportPlugin {
	
	protected FileFilter getFileFilter() {
        return new FileNameExtensionFilter("PNML Hybrid Petri Net files", "pnml");
    }

    protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
                throws Exception {
    	ExtendedHybridPetrinet net = new ExtendedHybridPetrinet("Hybrid Petrinet");
    	utils.importHybridPetrinetFromStream(context, input, net);
    	return net;
	} 
    
}