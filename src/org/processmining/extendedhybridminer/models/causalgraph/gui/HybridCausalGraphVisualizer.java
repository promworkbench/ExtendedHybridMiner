package org.processmining.extendedhybridminer.models.causalgraph.gui;


import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.jgraph.graph.AttributeMap.SerializablePoint2D;
import org.jgraph.graph.GraphConstants;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.extendedhybridminer.models.causalgraph.ExtendedCausalGraph;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedLongDepGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedSureGraphEdge;
import org.processmining.extendedhybridminer.models.causalgraph.HybridDirectedUncertainGraphEdge;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.impl.ProgressBarImpl;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphPort;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayoutProgress;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

@Plugin(name = "Visualize Extended Causal Graph", parameterLabels = { "Extended Causal Graph" }, returnLabels = { "Extended Causal Graph" }, returnTypes = { JComponent.class })
@Visualizer
public class HybridCausalGraphVisualizer {
	
	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent visualize(PluginContext context, final ExtendedCausalGraph fCG) {
		return HybridCausalGraphVisualizer.getVisualizationPanel(fCG, new ProgressBarImpl(context));
	}

	protected HybridCausalGraphVisualizer() {
	};

	public static HybridCausalGraphVisualization getVisualizationPanel(
			ExtendedCausalGraph graph,
			Progress progress) {
		return getResultsPanel(graph, new ViewSpecificAttributeMap(), progress);
	}

	public static HybridCausalGraphVisualization getResultsPanel(ExtendedCausalGraph graph,  
			ViewSpecificAttributeMap map, Progress progress) {

		ProMJGraph jgraph=createJGraph(graph,map,progress,"default");

		return new HybridCausalGraphVisualization(jgraph, graph);
	}

	public static ProMJGraph createJGraph(ExtendedCausalGraph fuzzyCausalNet,ViewSpecificAttributeMap map,Progress progress,String label)
	{

		Set<?> edges=fuzzyCausalNet.getEdges();
		Color sureColor = fuzzyCausalNet.getSureColor();
		Color unsureColor = fuzzyCausalNet.getUnsureColor();
		Color LDColor = fuzzyCausalNet.getLongDepColor();
		switch(label)
		{
			case "Split and join fractions":
			//default:
				System.out.println("CASE 1");
				for(Object e:edges){
					if(e instanceof HybridDirectedSureGraphEdge) {
						((HybridDirectedSureGraphEdge)e).setLabel(((HybridDirectedSureGraphEdge)e).getODSD(),((HybridDirectedSureGraphEdge)e).getIDSD());
						//System.out.println("Label: " + ((HybridDirectedSureGraphEdge)e));
						((HybridDirectedSureGraphEdge) e).setEdgeColor(sureColor);
					} else if(e instanceof HybridDirectedUncertainGraphEdge) {
							((HybridDirectedUncertainGraphEdge)e).setLabel(((HybridDirectedUncertainGraphEdge)e).getODSD(),((HybridDirectedUncertainGraphEdge)e).getIDSD());
							((HybridDirectedUncertainGraphEdge) e).setEdgeColor(unsureColor);
					}	
					else
						if(e instanceof HybridDirectedLongDepGraphEdge)
						{
							((HybridDirectedLongDepGraphEdge)e).setLabel(((HybridDirectedLongDepGraphEdge)e).getODSD(), ((HybridDirectedLongDepGraphEdge)e).getIDSD());
							((HybridDirectedLongDepGraphEdge) e).setEdgeColor(LDColor);
						}
					
				}
				break;
			case "Succession count":
				System.out.println("CASE 2");
				for(Object e:edges)
				{
					if(e instanceof HybridDirectedSureGraphEdge)
					{
						((HybridDirectedSureGraphEdge)e).setLabel(((HybridDirectedSureGraphEdge)e).getDirectSuccession());
						((HybridDirectedSureGraphEdge) e).setEdgeColor(sureColor);
					}
					else
						if(e instanceof HybridDirectedUncertainGraphEdge)
						{
							((HybridDirectedUncertainGraphEdge)e).setLabel(((HybridDirectedUncertainGraphEdge)e).getDirectSuccession());
							((HybridDirectedUncertainGraphEdge) e).setEdgeColor(unsureColor);
						}
						else
								if(e instanceof HybridDirectedLongDepGraphEdge)
								{
									((HybridDirectedLongDepGraphEdge)e).setLabel(((HybridDirectedLongDepGraphEdge)e).getDirectSuccession());
									((HybridDirectedLongDepGraphEdge) e).setEdgeColor(LDColor);
								}
				}
				break;			
			case "Strength (split & join)":
				System.out.println("CASE 3");
				for(Object e:edges)
				{
					if(e instanceof HybridDirectedSureGraphEdge)
					{
						((HybridDirectedSureGraphEdge)e).setLabel(((HybridDirectedSureGraphEdge)e).getDirectSuccessionDependency());
						((HybridDirectedSureGraphEdge) e).setEdgeColor(sureColor);
					}
					else
						if(e instanceof HybridDirectedUncertainGraphEdge)
						{
							((HybridDirectedUncertainGraphEdge)e).setLabel(((HybridDirectedUncertainGraphEdge)e).getDirectSuccessionDependency());
							((HybridDirectedUncertainGraphEdge) e).setEdgeColor(unsureColor);
						}
						else
							if(e instanceof HybridDirectedLongDepGraphEdge)
							{
								((HybridDirectedLongDepGraphEdge)e).setLabel(((HybridDirectedLongDepGraphEdge)e).getDirectSuccessionDependency());
								((HybridDirectedLongDepGraphEdge) e).setEdgeColor(LDColor);
							}
				}
				break;
			case "Strength (concurrency & loops)":
				System.out.println("CASE 4");
				for(Object e:edges)
				{
					if(e instanceof HybridDirectedSureGraphEdge)
					{
						((HybridDirectedSureGraphEdge)e).setLabel(((HybridDirectedSureGraphEdge)e).getAbDependencyMetric());
						((HybridDirectedSureGraphEdge) e).setEdgeColor(sureColor);
					}
					else
						if(e instanceof HybridDirectedUncertainGraphEdge)
						{
							((HybridDirectedUncertainGraphEdge)e).setLabel(((HybridDirectedUncertainGraphEdge)e).getAbDependencyMetric());
							((HybridDirectedUncertainGraphEdge) e).setEdgeColor(unsureColor);
						}
					else
							if(e instanceof HybridDirectedLongDepGraphEdge)
							{
								((HybridDirectedLongDepGraphEdge)e).setLabel(((HybridDirectedLongDepGraphEdge)e).getAbDependencyMetric());
								((HybridDirectedLongDepGraphEdge) e).setEdgeColor(LDColor);
							}
				}
				break;
			//case "Causality (average strength)":
			default:
				System.out.println("CASE 5");
				for(Object e:edges)
				{
					if(e instanceof HybridDirectedSureGraphEdge)
					{
						((HybridDirectedSureGraphEdge)e).setLabel(((HybridDirectedSureGraphEdge)e).getCausalityMetric());
						((HybridDirectedSureGraphEdge) e).setEdgeColor(sureColor);
					}
					else if(e instanceof HybridDirectedUncertainGraphEdge)
						{
							((HybridDirectedUncertainGraphEdge)e).setLabel(((HybridDirectedUncertainGraphEdge)e).getCausalityMetric());
							((HybridDirectedUncertainGraphEdge) e).setEdgeColor(unsureColor);
						}
					else if(e instanceof HybridDirectedLongDepGraphEdge)
							{
								((HybridDirectedLongDepGraphEdge)e).setLabel(((HybridDirectedLongDepGraphEdge)e).getCausalityMetric());
								((HybridDirectedLongDepGraphEdge) e).setEdgeColor(LDColor);
							}
				}
				break;
				
			/*default:
				System.out.println("CASE D");
				for(Object e:edges){
					if(e instanceof HybridDirectedSureGraphEdge) {
						((HybridDirectedSureGraphEdge)e).setLabel(((HybridDirectedSureGraphEdge)e).getODSD(),((HybridDirectedSureGraphEdge)e).getIDSD());
						//System.out.println("Label: " + ((HybridDirectedSureGraphEdge)e));
						((HybridDirectedSureGraphEdge) e).setEdgeColor(sureColor);
					} else if(e instanceof HybridDirectedUncertainGraphEdge) {
							((HybridDirectedUncertainGraphEdge)e).setLabel(((HybridDirectedUncertainGraphEdge)e).getODSD(),((HybridDirectedUncertainGraphEdge)e).getIDSD());
							((HybridDirectedUncertainGraphEdge) e).setEdgeColor(unsureColor);
					}				
				}
				break;*/
			
		}


		GraphLayoutConnection layoutConnection=new GraphLayoutConnection(fuzzyCausalNet);

		ProMGraphModel model=new ProMGraphModel(fuzzyCausalNet);
		ProMJGraph jGraph = new ProMJGraph(model,map,layoutConnection);

		//Set nodes=model.getGraph().getNodes();

		/*Map attributes = jGraph.getGraphLayoutCache().getRoots()[12].getAllAttributes();
		attributes.put("labelAlongEdge", new Boolean(true));
		jGraph.getGraphLayoutCache().edit(attributes);*/
		
		// A nested map with the edge as key
		// and its attributes as the value
		// is required for the edit.
		/*Map allEdgeAttributes = new HashMap();
		Object[] cells = jGraph.getGraphLayoutCache().getCells(false, false, false, true);
		for (Object cell : cells) {
			ProMGraphEdge cellEdge = (ProMGraphEdge) cell;
			
			Map attrs = cellEdge.getAttributes();
			attrs.put("labelAlongEdge", new Boolean(true));
			allEdgeAttributes.put(cellEdge, attrs);
		}
		org.jgraph.graph.AttributeMap attrsss = (jGraph.getGraphLayoutCache().getAllViews()[0]).getAllAttributes();
		
		jGraph.getGraphLayoutCache().edit(allEdgeAttributes);*/

		JGraphHierarchicalLayout layout=new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);

		layout.setOrientation(map.get(fuzzyCausalNet,AttributeMap.PREF_ORIENTATION,SwingConstants.NORTH));

		if(!layoutConnection.isLayedOut())
		{

			JGraphFacade facade=new JGraphFacade(jGraph);

			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();
			if(layout instanceof JGraphHierarchicalLayout)
			{
				facade.run((JGraphHierarchicalLayout) layout, true);
			}
			else
			{
				facade.run(layout, true);
			}
	
			java.util.Map<?,?> nested=facade.createNestedMap(true,true);

			jGraph.getGraphLayoutCache().edit(nested);
			layoutConnection.setLayedOut(true);
		}

		jGraph.setUpdateLayout(layout);

		return jGraph;
	}
	
	
//	public static ProMJGraph createJGraph(DirectedGraph<?, ?> graph,
//			ViewSpecificAttributeMap map, Progress progress){
//		
//		GraphLayoutConnection con = new GraphLayoutConnection(graph);
//		
//		ProMJGraph jgraph = new ProMJGraph(new ProMGraphModel(graph), map, con);
//
//		JGraphHierarchicalLayout layout = getHierarchicalLayout(progress);
//		layout.setOrientation(graph.getAttributeMap().get(AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));
//		
//
//		if (!graph.isLayedOut()) {
//				
//				JGraphFacade facade = new JGraphFacade(jgraph);
//
//				facade.setOrdered(true);
//				facade.setEdgePromotion(true);
//				facade.setIgnoresCellsInGroups(false);
//				facade.setIgnoresHiddenCells(false);
//				facade.setIgnoresUnconnectedCells(false);
//				facade.setDirected(false);
//				facade.resetControlPoints();
//							
//				facade.run(layout, false);
//
//				fixParallelEdges(facade, 15);
//
//				Map<?, ?> nested = facade.createNestedMap(true, false);
//
//				jgraph.getGraphLayoutCache().edit(nested);
//
////			}
//
//
//			graph.setLayedOut(true);
//
//		}
//		
//		jgraph.repositionToOrigin();
//		jgraph.setUpdateLayout(layout);
//		
//		return jgraph;
//	}

	protected static JGraphHierarchicalLayout getHierarchicalLayout(final Progress progress) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();

		if (progress != null) {
			layout.getProgress().addPropertyChangeListener(new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(JGraphLayoutProgress.MAXIMUM_PROPERTY)) {
						progress.setIndeterminate(false);
						progress.setMaximum(((Integer) evt.getNewValue()).intValue());
						Thread.yield();
					} else if (evt.getPropertyName().equals(JGraphLayoutProgress.PROGRESS_PROPERTY)) {
						progress.setValue(((Integer) evt.getNewValue()).intValue());
						Thread.yield();
					}
				}
			});
		}
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(20);

		return layout;
	}

	@SuppressWarnings("unchecked")
	protected static void fixParallelEdges(JGraphFacade facade, double spacing) {
		ArrayList edges = new ArrayList(facade.getEdges());
		for (Object edge : edges) {
			List points = facade.getPoints(edge);
			if (points.size() != 2) {
				continue;
			}
			Object sourceCell = facade.getSource(edge);
			Object targetCell = facade.getTarget(edge);
			Object sourcePort = facade.getSourcePort(edge);
			Object targetPort = facade.getTargetPort(edge);
			Object[] between = facade.getEdgesBetween(sourcePort, targetPort, false);
			if ((between.length == 1) && !(sourcePort == targetPort)) {
				continue;
			}
			Rectangle2D sCP = facade.getBounds(sourceCell);
			Rectangle2D tCP = facade.getBounds(targetCell);
			Point2D sPP = GraphConstants.getOffset(((ProMGraphPort) sourcePort).getAttributes());
			// facade. getBounds (sourcePort ) ;

			if (sPP == null) {
				sPP = new Point2D.Double(sCP.getCenterX(), sCP.getCenterY());
			}
			Point2D tPP = GraphConstants.getOffset(((ProMGraphPort) targetPort).getAttributes());
			// facade.getBounds(sourcePort);

			if (tPP == null) {
				tPP = new Point2D.Double(tCP.getCenterX(), tCP.getCenterY());
			}

			if (sourcePort == targetPort) {
				assert (sPP.equals(tPP));
				double x = sPP.getX();
				double y = sPP.getY();
				for (int i = 2; i < between.length + 2; i++) {
					List newPoints = new ArrayList(5);
					newPoints.add(new Point2D.Double(x - (spacing + i * spacing), y));
					newPoints.add(new Point2D.Double(x - (spacing + i * spacing), y - (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x, y - (2 * spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x + (spacing + i * spacing), y - (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x + (spacing), y - (spacing / 2 + i * spacing)));
					facade.setPoints(between[i - 2], newPoints);
				}

				continue;
			}

			double dx = (sPP.getX()) - (tPP.getX());
			double dy = (sPP.getY()) - (tPP.getY());
			double mx = (tPP.getX()) + dx / 2.0;
			double my = (tPP.getY()) + dy / 2.0;
			double slope = Math.sqrt(dx * dx + dy * dy);
			for (int i = 0; i < between.length; i++) {
				List newPoints = new ArrayList(3);
				double pos = 2 * i - (between.length - 1);
				if (facade.getSourcePort(between[i]) == sourcePort) {
					newPoints.add(sPP);
					newPoints.add(tPP);
				} else {
					newPoints.add(tPP);
					newPoints.add(sPP);
				}
				if (pos != 0) {
					pos = pos / 2;
					double x = mx + pos * spacing * dy / slope;
					double y = my - pos * spacing * dx / slope;
					newPoints.add(1, new SerializablePoint2D.Double(x, y));
				}
				facade.setPoints(between[i], newPoints);
			}
		}
	}

	public static JComponent visualizeGraph(
			ExtendedCausalGraph graph, 
			HeuristicsNet net, 
			AnnotatedVisualizationSettings settings, 
			Progress progress) {
		
		return getResultsPanel(graph, new ViewSpecificAttributeMap(), progress);
	}
}

