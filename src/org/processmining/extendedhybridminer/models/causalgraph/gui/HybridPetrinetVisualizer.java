package org.processmining.extendedhybridminer.models.causalgraph.gui;


import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.jgraph.graph.AttributeMap.SerializablePoint2D;
import org.jgraph.graph.GraphConstants;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.extendedhybridminer.models.hybridpetrinet.HybridPetrinet;
import org.processmining.extendedhybridminer.models.hybridpetrinet.LongDepTransitionsArc;
import org.processmining.extendedhybridminer.models.hybridpetrinet.SureTransitionsArc;
import org.processmining.extendedhybridminer.models.hybridpetrinet.TransitionsArc;
import org.processmining.extendedhybridminer.models.hybridpetrinet.UncertainTransitionsArc;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.plugin.impl.ProgressBarImpl;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphPort;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayoutProgress;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

@Plugin(name = "Visualize Hybrid Petrinet", parameterLabels = { "Hybrid Petri Net" }, returnLabels = { "Hybrid Petri Net" }, returnTypes = { JComponent.class })
@Visualizer
public class HybridPetrinetVisualizer {
	
	@PluginVariant(requiredParameterLabels = { 0 })
    public static JComponent visualize(PluginContext context, HybridPetrinet fPN) throws Exception {
	       
        JComponent result = HybridPetrinetVisualizer.getVisualizationPanel(fPN, new ProgressBarImpl(context));
        context.addConnection(new GraphLayoutConnection(fPN.getGraph()));
        final ConnectionManager cm = context.getConnectionManager();
        try {
            if (cm.getConnections(GraphLayoutConnection.class, context) != null)
                return result;
            return null;
        } catch (final ConnectionCannotBeObtained e) {
            // No connections available
            context.log("Connection does not exist", MessageLevel.DEBUG);
            return null;
        }


    }

	protected HybridPetrinetVisualizer() {
	};

	public static HybridPetrinetVisualization getVisualizationPanel(
			HybridPetrinet graph, 
			Progress progress) throws Exception {
		return getResultsPanel(graph, new ViewSpecificAttributeMap(), progress);
	}

	public static HybridPetrinetVisualization getResultsPanel(HybridPetrinet graph,
			ViewSpecificAttributeMap map, Progress progress) throws Exception {
		
		ProMJGraph jgraph = createJGraph(graph, map, progress);

		return new HybridPetrinetVisualization(jgraph, graph);
	}
	
	public static ProMJGraph createJGraph(HybridPetrinet fuzzyPetrinet,
			ViewSpecificAttributeMap map, Progress progress) throws Exception{
		
		Color surePlaceColor = fuzzyPetrinet.getSurePlaceColor();
		Color sureColor = fuzzyPetrinet.getSureColor();
		Color unsureColor = fuzzyPetrinet.getUnsureColor();
		Color ldColor = fuzzyPetrinet.getLDColor();
		Set<?> edges=fuzzyPetrinet.getEdges();
		for(Object e:edges) {
			if(e instanceof SureTransitionsArc) {
				((TransitionsArc) e).setEdgeColor(sureColor);
			}
			else if(e instanceof UncertainTransitionsArc) {
				((TransitionsArc) e).setEdgeColor(unsureColor);
			} else if(e instanceof LongDepTransitionsArc) {
				((TransitionsArc) e).setEdgeColor(ldColor);
			} else if(e instanceof Arc) {
				((Arc) e).getAttributeMap().put(AttributeMap.EDGECOLOR, surePlaceColor);
			}			
		}
		Collection<Place> places = fuzzyPetrinet.getPlaces();
		for(Place p: places) {
			p.getAttributeMap().put(AttributeMap.STROKECOLOR, surePlaceColor);		
		}
		
		
		GraphLayoutConnection layoutConnection = new GraphLayoutConnection(fuzzyPetrinet);
		
		ProMGraphModel model = new ProMGraphModel(fuzzyPetrinet);
		ProMJGraph jGraph = new ProMJGraph(model, map, layoutConnection);

	
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);

		layout.setOrientation(map.get(fuzzyPetrinet, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

		if(!layoutConnection.isLayedOut()){
		
			JGraphFacade facade = new JGraphFacade(jGraph);
	
			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();
			if (layout instanceof JGraphHierarchicalLayout) {
				try{
				facade.run((JGraphHierarchicalLayout) layout, true);
				}catch(Exception ex){
					if (ex instanceof IllegalArgumentException){
						Exception nEx = new Exception("Impossible to visualize the Petri Net: too many places");
						nEx.setStackTrace(ex.getStackTrace());
						throw nEx;
					}
				}
			} else {
				facade.run(layout, true);
			}
	
			java.util.Map<?, ?> nested = facade.createNestedMap(true, true);
	
			jGraph.getGraphLayoutCache().edit(nested);
			layoutConnection.setLayedOut(true);
		}
		
		jGraph.setUpdateLayout(layout);
		
		//return jGraph;
		
		
		//ProMGraphModel model = new ProMGraphModel(jGraph);
		//ProMJGraph jgraph;
		/*
		 * Make sure that only a single ProMJGraph is created at every time.
		 * The underlying JGrpah code cannot handle creating multiple creations at the same time.
		 */
		

		/*ProMJGraphPanel panel = new ProMJGraphPanel(jGraph);

		panel.addViewInteractionPanel(new org.processmining.framework.util.ui.scalableview.interaction.PIPInteractionPanel(panel), SwingConstants.NORTH);
		panel.addViewInteractionPanel(new ZoomInteractionPanel(panel, ScalableViewPanel.MAX_ZOOM), SwingConstants.WEST);
		panel.addViewInteractionPanel(new ExportInteractionPanel(panel), SwingConstants.SOUTH);

		layoutConnection.updated();*/
		
		/*glc = layoutConnection;

		if (newConnection) {
			context.getConnectionManager().addConnection(layoutConnection);
		}*/

		return jGraph;
	}
	
	/*synchronized void addViewInteractionPanel(ViewInteractionPanel panel, int location) {
		panel.setScalableComponent(scalable);
		panel.setParent(this);

		JPanel panelOn = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		JPanel panelOff = factory.createRoundedPanel(15, Color.DARK_GRAY);
		panelOn.setLayout(null);
		panelOff.setLayout(null);

		panelOn.add(panel.getComponent());
		panelOn.setVisible(false);
		panelOn.setEnabled(false);
		panelOff.setVisible(true);
		panelOff.setEnabled(true);
		JLabel panelTitle = factory.createLabel(panel.getPanelName());
		panelTitle.setHorizontalTextPosition(SwingConstants.CENTER);
		panelTitle.setVerticalTextPosition(SwingConstants.CENTER);
		panelTitle.setForeground(Color.WHITE);
		panelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		panelOff.add(panelTitle);

		panels.put(panel, new Pair<JPanel, JPanel>(panelOn, panelOff));
		locations.put(panel, location);

		switch (location) {
			case SwingConstants.NORTH : {
				panelTitle.setBounds(5, 10, TAB_WIDTH - 15, TAB_HEIGHT);
				panelOn.setLocation(TAB_HEIGHT + 10 + north * TAB_WIDTH, -10);
				panel.getComponent().setLocation(10, 20);
				panelOff.setBounds(TAB_HEIGHT + 10 + north * TAB_WIDTH, -10, TAB_WIDTH - 5, TAB_HEIGHT + 10);
				north++;
				break;
			}
			case SwingConstants.EAST : {
				panelTitle.setBounds(0, 5, TAB_HEIGHT, TAB_WIDTH - 15);
				panelTitle.setUI(new VerticalLabelUI(true));
				panelOn.setLocation(getWidth() - TAB_HEIGHT, TAB_HEIGHT + 10 + east * TAB_WIDTH);
				panelOff.setBounds(getWidth() - TAB_HEIGHT, TAB_HEIGHT + 10 + east * TAB_WIDTH, TAB_HEIGHT + 10,
						TAB_WIDTH - 5);
				panel.getComponent().setLocation(10, 10);
				east++;
				break;
			}
			case SwingConstants.WEST : {
				panelTitle.setBounds(10, 5, TAB_HEIGHT, TAB_WIDTH - 15);
				panelTitle.setUI(new VerticalLabelUI(true));
				panelOn.setLocation(-10, TAB_HEIGHT + 10 + west * TAB_WIDTH);
				panelOff.setBounds(-10, TAB_HEIGHT + 10 + west * TAB_WIDTH, TAB_HEIGHT + 10, TAB_WIDTH - 5);
				panel.getComponent().setLocation(20, 10);
				west++;
				break;
			}
			default : {
				//SOUTH
				panelTitle.setBounds(5, 0, TAB_WIDTH - 15, TAB_HEIGHT);
				panelOn.setLocation(TAB_HEIGHT + 10 + south * TAB_WIDTH, getHeight() - TAB_HEIGHT);
				panelOff.setBounds(TAB_HEIGHT + 10 + south * TAB_WIDTH, getHeight() - TAB_HEIGHT, TAB_WIDTH - 5,
						TAB_HEIGHT + 10);
				panel.getComponent().setLocation(10, 10);
				south++;

			}
		}
		setSize(panel, panelOff, panelOn);
		setLocation(panel, panelOff, panelOn);

		add(panelOn, JLayeredPane.PALETTE_LAYER);
		add(panelOff, JLayeredPane.PALETTE_LAYER);
		panel.updated();
	}*/
	
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
			HybridPetrinet graph, 
			HeuristicsNet net, 
			AnnotatedVisualizationSettings settings, 
			Progress progress) throws Exception {
		
		return getResultsPanel(graph, new ViewSpecificAttributeMap(), progress);
	}
}

